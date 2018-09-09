package com.hjlc.userIndex.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hjlc.base.dao.DaoSupport;
import com.hjlc.userIndex.model.AppUser;
import com.hjlc.util.utils.PageData;

@Service("appUserService")
public class AppUserService {
	@Resource(name = "daoSupport")
	private DaoSupport dao;
	/**
	 * 通过用户名和密码查找用户
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	public AppUser findByNameAndPassword(PageData pd) throws Exception{
		return  (AppUser) dao.findForObject("AppUserMapper.findByNameAndPassword", pd);
	}
	
	public void saveAppUser(PageData pd) throws Exception{
		dao.save("AppUserMapper.saveAppUser", pd);
	}
	
	public AppUser findByMobile(PageData pd) throws Exception{
		return (AppUser) dao.findForObject("AppUserMapper.findByMobile", pd);
	}

}