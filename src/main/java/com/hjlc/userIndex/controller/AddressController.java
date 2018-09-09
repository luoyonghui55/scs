package com.hjlc.userIndex.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hjlc.base.controller.BaseController;
import com.hjlc.userIndex.service.AddressService;
import com.hjlc.util.utils.AppUtil;
import com.hjlc.util.utils.Constants;
import com.hjlc.util.utils.PageData;

/**
 * 地址处理
 */
@Controller
@RequestMapping(value="/address")
public class AddressController extends BaseController{
	@Resource(name="addressService")
	private AddressService addressService;
	
	//通过城市名获取城市id
	@RequestMapping(value="queryCityIdByName",produces="application/json;charset=UTF-8")
	@ResponseBody
	public Object queryCityIdByName() throws Exception {
		Map<String, Object> cityMap = new HashMap<String, Object>(1);
		PageData pd = this.getPageData();
		
		//取得城市ID
		String cityId = pd.getString("cityId");
		if (!StringUtils.hasText(cityId)) {
			cityId = Constants.hotCityMap.get(pd.getString("cityName"));
			if (!StringUtils.hasText(cityId)) {
				cityId = addressService.findCityIdByName(pd.getString("cityName"));
			}
		}
		cityMap.put("cityid", cityId);
		return AppUtil.returnObject(new PageData(), cityMap);
	}
	//通过条件获取城市集合
	@RequestMapping(value="queryCityList",produces="application/json;charset=UTF-8")
	@ResponseBody
	public Object queryCityList() throws Exception {
		Map<String, Object> cityMap = new HashMap<String, Object>(1);
		PageData pd = this.getPageData();
		List<PageData> cityList = addressService.listAllCities(pd);
		cityMap.put("cityList", cityList);
		return AppUtil.returnObject(new PageData(), cityMap);
	}
	//通过条件获取城市集合
	@RequestMapping(value="queryAreaList",produces="application/json;charset=UTF-8")
	@ResponseBody
	public Object queryAreaList() throws Exception {
		Map<String, Object> areaMap = new HashMap<String, Object>(1);
		PageData pd = this.getPageData();
		List<PageData> areaList = addressService.listAllArea(pd);
		areaMap.put("areaList", areaList);
		return AppUtil.returnObject(new PageData(), areaMap);
	}
}