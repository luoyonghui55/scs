package com.hjlc.userIndex.controller;

import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.hjlc.base.controller.BaseController;
import com.hjlc.system.model.Page;
import com.hjlc.userIndex.model.AppUser;
import com.hjlc.userIndex.model.ServiceOrder;
import com.hjlc.userIndex.service.AddressService;
import com.hjlc.userIndex.service.ServiceOrderService;
import com.hjlc.util.utils.AppUtil;
import com.hjlc.util.utils.Constants;
import com.hjlc.util.utils.DateUtil;
import com.hjlc.util.utils.EhcacheUtil;
import com.hjlc.util.utils.FileUtil;
import com.hjlc.util.utils.Freemarker;
import com.hjlc.util.utils.MapUtil;
import com.hjlc.util.utils.PageData;
import com.hjlc.util.utils.PathUtil;
import com.hjlc.util.utils.Tools;

/**
 * 订单controller
 */
@Controller
@RequestMapping(value="/serviceOrder")
public class ServiceOrderController extends BaseController{
	@Resource(name="serviceOrderService")
	private ServiceOrderService serviceOrderService;
	@Resource(name="addressService")
	private AddressService addressService;
	
	//保存服务订单
	@RequestMapping(value="/saveServiceOrder", produces="application/json;charset=UTF-8")
	@ResponseBody
	public Object saveServiceOrder(ServiceOrder serviceOrder) throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		AppUser appUser = this.getSessionAppUser();
		boolean isSuccess = false;
		if (appUser == null) {
			resultMap.put("success", isSuccess);
			resultMap.put("errorFlag", "1");
			return AppUtil.returnObject(new PageData(), resultMap);
		}
		if (serviceOrder != null && !StringUtils.isEmpty(serviceOrder.getSoName()) &&
			!StringUtils.isEmpty(serviceOrder.getSoPhone()) && !StringUtils.isEmpty(serviceOrder.getSoCityid())) {
			PageData pd = this.getPageData();
			String serviceDate = pd.getString("serviceDate");//获取服务时间
			String cityArea = pd.getString("cityArea");//城市下的区县名
			//从缓存取城市下的区县信息
			String cityId = serviceOrder.getSoCityid();
			List<PageData> areaList = EhcacheUtil.getInstance().forCacheGetAreaByCityId(cityId);//从缓存中取
			if (areaList == null || areaList.size() < 1) {
				pd.clear();
				pd.put("cityid", cityId);
				//缓存中不存在，则从数据库中取
				areaList = addressService.listAllArea(pd);
			}
			//获取区县编码
			for (int i = 0; i < areaList.size(); i ++){
				PageData pageData = areaList.get(i);
				if (cityArea.equals(pageData.getString("area"))) {
					serviceOrder.setSoAreaid(pageData.getString("areaid"));
					break;
				}
			}
			String soId = Tools.getUUID();
			serviceOrder.setSoServiceDate(serviceDate);
			serviceOrder.setSoId(soId);
			serviceOrder.setSoOrderDate(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
			serviceOrder.setSoUserId(appUser.getUserId());
			//serviceOrder.setSoUserId(appUser.getUserName());
			serviceOrder.setSoHtmlPath(PathUtil.HTML_PATH.split("html/")[1]);
			//订单号从前台生成，目前的规则是：1位客户端来源(0，1：PC，2，3，4：微信，5，6，7：安卓，8，9：IOS)+月日+4位随机码
			//serviceOrder.setSoBillNumber(new BillNumberTool(2).nextBillNumber());
			if (DateUtil.isValidDate(serviceDate)){
				for (int g = 0; g < 3; g++) {
					try {
						isSuccess = serviceOrderService.saveServiceOrder(serviceOrder);
						break;
					} catch (Exception e) {
						//如果订单号存在，则重新生成订单号
						String oldBillNumber = serviceOrder.getSoBillNumber();
						String newBillNumber = oldBillNumber.substring(0, oldBillNumber.length() - 4) + RandomStringUtils.randomNumeric(4);
						//System.out.println("\n\n=======newBillNumber=" + newBillNumber);
						serviceOrder.setSoBillNumber(newBillNumber);
					}
				}
				resultMap.clear();
				resultMap.put("so", serviceOrder);
				//Freemarker.printFileByTemplate("orderDetail.ftl", resultMap, soId + ".html", PathUtil.HTML_PATH, PathUtil.FTL_PATH);
			}
		}
		resultMap.put("success", isSuccess);
		resultMap.put("errorFlag", "0");//下单成功
		return AppUtil.returnObject(new PageData(), resultMap);
	}

	// 列表
	@RequestMapping(value="/allList")
	public ModelAndView listServiceOrders(HttpSession session, Page page) throws Exception {
		logBefore(logger, "服务订单列表");
		ModelAndView mv = new ModelAndView();
		PageData pd = this.getPageData();
		//MapUtil.removeNullValue(pd);
		if (StringUtils.isEmpty(pd.getString("so_area"))) {
			if (!StringUtils.isEmpty(pd.getString("so_city"))) 
				pd.remove("so_province");
		}else {
			MapUtil.removeKey(pd, "so_city","so_province");
		}
		page.setPd(pd);
		try {
			List<PageData> varList = serviceOrderService.serviceOrderAlllistPage(page);
			List<PageData> provincesList = addressService.listAllProvinces(pd);
			mv.setViewName(PAGE_PATH_PREFIX + "information/serviceOrder/serviceOrderList.jsp");
			mv.addObject("varList", varList);
			mv.addObject("provincesList", provincesList);//此省份信息应该从缓存中取
			mv.addObject("pd", pd);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mv;
	}
	
	// 生成html
	@RequestMapping(value="/createHtml")
	public void createHtml(PrintWriter out)throws Exception{
		PageData pd = this.getPageData();
		try{
			FileUtil.delFolder(PathUtil.getClasspath()+"html/news"); //生成代码前,先清空之前生成的代码
			Map<String,Object> root = new HashMap<String,Object>();		//创建数据模型
			String soId = pd.getString("soId");
			PageData paramPD = new PageData();
			paramPD.put("soId", soId);
			ServiceOrder serviceOrder = serviceOrderService.getServiceOrderById(paramPD);
			if (serviceOrder != null) {
				root.put("so", serviceOrder);
				/*生成*/
				Freemarker.printFileByTemplate("orderDetail.ftl", root, soId + ".html", PathUtil.HTML_PATH, PathUtil.FTL_PATH);
				out.write("success");
			}else {
				out.write("false");
			}
			out.close();
		} catch(Exception e){
			logger.error(e.toString(), e);
			e.printStackTrace();
		}
	}
	//抢服务订单
	@RequestMapping(value="/robServiceOrder", produces="application/json;charset=UTF-8")
	@ResponseBody
	public Object robServiceOrder() throws Exception {
		PageData resultMap = this.getPageData();
		AppUser appUser = this.getSessionAppUser();
		boolean isSuccess = false;
		if (appUser == null) {//如果没有登录
			resultMap.clear();
			resultMap.put("success", isSuccess);
			resultMap.put("errorFlag", "2");
			return AppUtil.returnObject(new PageData(), resultMap);
		}
		resultMap.put("status", "0");
		String soBillNumber = resultMap.getString("soBillNumber");
		ServiceOrder serviceOrder = serviceOrderService.getServiceOrderByBillNumber(resultMap);
		//如果订单存在，并且还没被抢
		if (serviceOrder != null && 0 == serviceOrder.getSoStatus()) {
			resultMap.put("so_status", "1");
			//resultMap.put("so_get_user", appUser.getUserName());
			resultMap.put("so_get_user", appUser.getUserId());
			resultMap.put("so_rob_date", DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
			resultMap.put("so_bill_number", soBillNumber);
			int row = serviceOrderService.editServiceOrderState(resultMap);
			resultMap.clear();
			if (row > 0) 
				isSuccess = true;
			else 
				isSuccess = false;
		}else 
			isSuccess = false;
		
		resultMap.put("errorFlag", isSuccess ? "0" : "1");//0:抢单成功,1:抢单失败
		resultMap.put("success", isSuccess);
		return AppUtil.returnObject(new PageData(), resultMap);
	}
	//已发布订单或已抢订单
	@RequestMapping(value="/issueOrRobServiceOrder", produces="application/json;charset=UTF-8")
	@ResponseBody
	public Object issueOrRobServiceOrder() throws Exception {
		PageData resultMap = this.getPageData();
		AppUser appUser = this.getSessionAppUser();
		boolean isSuccess = false;
		if (appUser == null) {//如果没有登录
			resultMap.clear();
			resultMap.put("success", isSuccess);
			resultMap.put("errorFlag", "2");
			return AppUtil.returnObject(new PageData(), resultMap);
		}
		resultMap.put("status", "0");
		String soBillNumber = resultMap.getString("soBillNumber");
		ServiceOrder serviceOrder = serviceOrderService.getServiceOrderByBillNumber(resultMap);
		//如果订单存在，并且还没被抢
		if (serviceOrder != null && 0 == serviceOrder.getSoStatus()) {
			resultMap.put("so_status", "1");
			resultMap.put("so_get_user", appUser.getUserName());
			resultMap.put("so_bill_number", soBillNumber);
			int row = serviceOrderService.editServiceOrderState(resultMap);
			resultMap.clear();
			if (row > 0) 
				isSuccess = true;
			else 
				isSuccess = false;
		}else 
			isSuccess = false;
		
		resultMap.put("errorFlag", isSuccess ? "0" : "1");//0:抢单成功,1:抢单失败
		resultMap.put("success", isSuccess);
		return AppUtil.returnObject(new PageData(), resultMap);
	}
	
	//我的服务订单信息
	@RequestMapping(value="/queryMyServiceOrder",produces="application/json;charset=UTF-8")
	@ResponseBody
	public Object queryMyServiceOrder(Page page) throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>(7);
		PageData pd = this.getPageData();
		
		//取得用户名
		AppUser appUser = this.getSessionAppUser();
		if (appUser == null) {
			resultMap.put("status", "1");//未登录
			return AppUtil.returnObject(new PageData(), resultMap);
		}
		String soOrderDate = pd.getString("soOrderDate") == null ? "" : pd.remove("soOrderDate").toString();
		String soServiceDate = pd.getString("soServiceDate") == null ? "" : pd.remove("soServiceDate").toString();
		String soRobDate = pd.getString("soRobDate") == null ? "" : pd.remove("soRobDate").toString();
		if (StringUtils.hasText(soOrderDate) && soOrderDate.length() < 9) {
			pd.put("soStartOrderDate", soOrderDate + "-01 00:00:00");
			pd.put("soEndOrderDate", soOrderDate + "-31 23:59:59");
		}
		if (StringUtils.hasText(soServiceDate) && soServiceDate.length() < 9) {
			pd.put("soStartServiceDate", soServiceDate + "-01 00:00:00");
			pd.put("soEndServiceDate", soServiceDate + "-31 23:59:59");
		}
		if (StringUtils.hasText(soRobDate) && soRobDate.length() < 9) {
			pd.put("soStartRobDate", soRobDate + "-01 00:00:00");
			pd.put("soEndRobDate", soRobDate + "-31 23:59:59");
		}
		pd.put("so_status", pd.getString("soStatus"));
		//soType,0:发布订单，1:已抢订单
		String soType = pd.getString("soType");
		if ("1".equals(soType)) {//如果是查询已抢订单
			pd.put("so_get_user", appUser.getUserId());
			pd.put("soRobDate", pd.remove("soServiceDate"));
		}else {
			pd.put("so_user_id", appUser.getUserId());
		}
		
		page.setPd(pd);
		List<PageData> serviceOrderList = serviceOrderService.myServiceOrderlistPage(page);
		resultMap.put("status", "0");
		resultMap.put("serviceOrderList", serviceOrderList);
		resultMap.put("serviceOrderTotalResult", page.getTotalResult());
		resultMap.put("serviceOrderTotalPage", page.getTotalPage());
		return AppUtil.returnObject(new PageData(), resultMap);
	}
	//我的服务订单信息
	@RequestMapping(value="/myMakeMoney",produces="application/json;charset=UTF-8")
	@ResponseBody
	public Object myMakeMoney(Page page) throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>(4);
		PageData pd = this.getPageData();
		//取得用户名
		AppUser appUser = this.getSessionAppUser();
		if (appUser == null) {
			resultMap.put("status", "1");//未登录
			return AppUtil.returnObject(new PageData(), resultMap);
		}
		pd.put("userName", appUser.getUserId());
		long makeMoney = serviceOrderService.myMakeMoney(pd);
		resultMap.put("status", "0");
		resultMap.put("totalMakeMoney", makeMoney);
		return AppUtil.returnObject(new PageData(), resultMap);
	}
	//编辑服务订单信息
	@RequestMapping(value="/editServiceOrder",produces="application/json;charset=UTF-8")
	@ResponseBody
	public Object editServiceOrder() throws Exception {
		PageData pd = this.getPageData();
		String robOrIssue = (String) (pd.get("robOrIssue") == null ? "" : pd.remove("robOrIssue")),
		isFinish = (String) (pd.get("isFinish") == null ? "" : pd.remove("isFinish")),
			billNumber = pd.getString("billNumber");
		if (StringUtils.isEmpty(robOrIssue) || (!"1".equals(robOrIssue) && !"0".equals(robOrIssue))) {
			pd.put("status", "1");
			return pd;
		}
		AppUser appUser = this.getSessionAppUser();
		String status = "0";
		if ("Y".equals(isFinish)) {//完成订单操作
			putFinishPdValue(pd, robOrIssue, appUser.getUserId());
			int rows = serviceOrderService.editServiceOrder(pd);
			if (rows < 1) {
				pd.clear();
				pd.put("status", "1");
				return pd;
			}
		}else {
			putCancelPdValue(pd, robOrIssue, appUser.getUserId());
			status = serviceOrderService.updateCancelOrder(pd,robOrIssue, isFinish,billNumber, appUser);
		}
		pd.put("status", status);
		return pd;
	}
	/**
	 * 给editServiceOrder方法中完成按钮请求的PD赋值
	 * @param pd
	 * @param robOrIssue
	 * @param userId
	 */
	private void putFinishPdValue(PageData pd,String robOrIssue,String userId){
		if ("0".equals(robOrIssue)) {//如果是已发布订单
			pd.put("so_user_id", userId);
			pd.put("so_status", "4");
			pd.put("no_so_get_user", userId);
			pd.put("soStatus", "2");
		}else {
			pd.put("no_so_user_id", userId);
			pd.put("so_status", "1");
			pd.put("soStatus", "4");
			pd.put("so_get_user", userId);
		}
	}
	/**
	 * 给editServiceOrder方法中完成按钮请求的PD赋值
	 * @param pd
	 * @param robOrIssue
	 * @param userId
	 */
	private void putCancelPdValue(PageData pd,String robOrIssue,String userId){
		if ("0".equals(robOrIssue)) {//如果是已发布订单
			pd.put("so_user_id", userId);
			pd.put("so_status", "0");
			pd.put("soStatus", "3");
		}else {
			pd.put("no_so_user_id", userId);
			pd.put("so_status", "1");
			pd.put("soStatus", "4");
			pd.put("so_get_user", userId);
		}
	}
	/* ===============================权限================================== */
	public Map<String, String> getHC(){
		Subject currentUser = SecurityUtils.getSubject();  //shiro管理的session
		Session session = currentUser.getSession();
		return (Map<String, String>)session.getAttribute(Constants.SESSION_QX);
	}
	/* ===============================权限================================== */
}