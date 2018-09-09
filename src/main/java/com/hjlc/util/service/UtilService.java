package com.hjlc.util.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hjlc.base.dao.DaoSupport;
import com.hjlc.system.model.Page;
import com.hjlc.util.utils.PageData;

/**
 * 工具service
 */

@Service("utilService")
public class UtilService {
	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/**
	 * 用户交易记录日志信息列表
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	public List<PageData> userDealslistPage(Page page)throws Exception{
		return (List<PageData>) dao.findForList("UtilMapper.userDealslistPage", page);
	}
	
}