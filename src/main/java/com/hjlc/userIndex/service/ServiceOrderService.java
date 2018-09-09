package com.hjlc.userIndex.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hjlc.base.dao.DaoSupport;
import com.hjlc.system.model.Page;
import com.hjlc.userIndex.model.AppUser;
import com.hjlc.userIndex.model.ServiceOrder;
import com.hjlc.util.utils.PageData;

/**
 * 主页服务订单处理service
 */

@Service("serviceOrderService")
public class ServiceOrderService {
	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/**
	 * 获取服务订单信息列表(前台页面使用)
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public List<PageData> serviceOrderlistPage(Page page) throws Exception {
		return (List<PageData>) dao.findForList("ServiceOrderMapper.serviceOrderlistPage", page);
	}
	/**
	 * 获取我的服务订单信息列表(前台页面使用)
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public List<PageData> myServiceOrderlistPage(Page page) throws Exception {
		return (List<PageData>) dao.findForList("ServiceOrderMapper.myServiceOrderlistPage", page);
	}
	/**
	 * 获取服务订单信息列表(系统后台页面使用)
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public List<PageData> serviceOrderAlllistPage(Page page) throws Exception {
		return (List<PageData>) dao.findForList("ServiceOrderMapper.serviceOrderAlllistPage", page);
	}
	/**serviceOrderAlllistPage
	 * 保存服务订单信息
	 * @param serviceOrder 服务订单对象
	 * @return
	 * @throws Exception
	 */
	public boolean saveServiceOrder(ServiceOrder serviceOrder) throws Exception {
		int row = dao.save("ServiceOrderMapper.saveServiceOrder", serviceOrder);
		return row > 0 ? true : false;
	}
	/**
	 * 修改服务订单信息
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	public int editServiceOrder(PageData pd)throws Exception{
		return dao.update("ServiceOrderMapper.editServiceOrder", pd);
	}
	/**
	 * 修改服务订单状态
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	@Transactional
	public int editServiceOrderState(PageData pd)throws Exception{
		return dao.update("ServiceOrderMapper.editServiceOrderState", pd);
	}
	/**
	*通过id获取数据  
	*@Param("soId")String soId, 
	*@Param("status")String status
	*/
	public ServiceOrder getServiceOrderById(PageData pd) throws Exception {
		return (ServiceOrder) dao.findForObject("ServiceOrderMapper.getServiceOrderById", pd);
	}
	/**
	*通过订单号获取数据 
	* @Param("soId")String soId, 
	* @Param("status")String status
	*/
	public ServiceOrder getServiceOrderByBillNumber(PageData pd) throws Exception {
		return (ServiceOrder) dao.findForObject("ServiceOrderMapper.getServiceOrderByBillNumber", pd);
	}
	/**
	 * 获取我赚到的总钱数
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	public long myMakeMoney(PageData pd)throws Exception{
		Object object = dao.findForObject("ServiceOrderMapper.myMakeMoney", pd);
		if (object == null) 
			return 0;
		return Long.valueOf(object.toString());
	}
	/**
	 * 取消服务订单信息
	 * @param pd
	 * @param robOrIssue 是我发布的还是我抢单的，0：我发布，1：我抢单
	 * @param isFinish 是执行订单完成事件还是取消事件，Y是完成事件，N是取消事件
	 * @param billNumber 订单号
	 * @param user 当前登录用户
	 * @return
	 * @throws Exception
	 */
	public String updateCancelOrder(PageData pd, String robOrIssue, String isFinish,String billNumber, AppUser user)throws Exception{
		int rows = dao.update("ServiceOrderMapper.editServiceOrder", pd);
		if (rows < 1) {
			return "1";
		}
		if (!"Y".equals(isFinish)) {
			String sql = "insert into cor_cancel_order(bill_number,user_id,cancel_date,issue_rob) values('" + billNumber + "','" +
				user.getUserId() + "',now(),'" + robOrIssue + "')";
			rows = dao.save("UtilMapper.insertObject", sql);
		}
		return rows > 0 ? "0" : "1";
	}
}