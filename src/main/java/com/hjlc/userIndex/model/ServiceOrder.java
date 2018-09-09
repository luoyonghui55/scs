package com.hjlc.userIndex.model;

import com.hjlc.system.model.Page;

/**
 * 服务订单model类
 */
public class ServiceOrder {
	private String soId;
	private String soBillNumber;
	private String soName;
	private String soOrderDate;
	private String soServiceDate;
	private String soServiceType;
	private String soCityid;
	private String soAreaid;
	private String soAddress;
	private String soPhone;
	private double soAmount;
	private String soDesc;
	private String soLng;
	private String soLat;
	private String soUserId;
	private byte soPaymentWay;
	private byte soStatus;
	private String soHtmlPath;

	private Page page;
	
	/**------------------getter  setter-----------------------**/
	public String getSoId() {
		return soId;
	}
	public void setSoId(String soId) {
		this.soId = soId;
	}
	public String getSoName() {
		return soName;
	}
	public void setSoName(String soName) {
		this.soName = soName;
	}
	public String getSoOrderDate() {
		return soOrderDate;
	}
	public void setSoOrderDate(String soOrderDate) {
		this.soOrderDate = soOrderDate;
	}
	public String getSoServiceDate() {
		return soServiceDate;
	}
	public void setSoServiceDate(String soServiceDate) {
		this.soServiceDate = soServiceDate;
	}
	public String getSoServiceType() {
		return soServiceType;
	}
	public void setSoServiceType(String soServiceType) {
		this.soServiceType = soServiceType;
	}
	public String getSoCityid() {
		return soCityid;
	}
	public void setSoCityid(String soCityid) {
		this.soCityid = soCityid;
	}
	public String getSoAreaid() {
		return soAreaid;
	}
	public void setSoAreaid(String soAreaid) {
		this.soAreaid = soAreaid;
	}
	public String getSoAddress() {
		return soAddress;
	}
	public void setSoAddress(String soAddress) {
		this.soAddress = soAddress;
	}
	public String getSoPhone() {
		return soPhone;
	}
	public void setSoPhone(String soPhone) {
		this.soPhone = soPhone;
	}
	public double getSoAmount() {
		return soAmount;
	}
	public void setSoAmount(double soAmount) {
		this.soAmount = soAmount;
	}
	public String getSoDesc() {
		return soDesc;
	}
	public void setSoDesc(String soDesc) {
		this.soDesc = soDesc;
	}
	public String getSoLng() {
		return soLng;
	}
	public void setSoLng(String soLng) {
		this.soLng = soLng;
	}
	public String getSoLat() {
		return soLat;
	}
	public void setSoLat(String soLat) {
		this.soLat = soLat;
	}
	public String getSoUserId() {
		return soUserId;
	}
	public void setSoUserId(String soUserId) {
		this.soUserId = soUserId;
	}
	public Page getPage() {
		return page;
	}
	public void setPage(Page page) {
		this.page = page;
	}
	public byte getSoPaymentWay() {
		return soPaymentWay;
	}
	public void setSoPaymentWay(byte soPaymentWay) {
		this.soPaymentWay = soPaymentWay;
	}
	public byte getSoStatus() {
		return soStatus;
	}
	public void setSoStatus(byte soStatus) {
		this.soStatus = soStatus;
	}
	public String getSoHtmlPath() {
		return soHtmlPath;
	}
	public void setSoHtmlPath(String soHtmlPath) {
		this.soHtmlPath = soHtmlPath;
	}
	public String getSoBillNumber() {
		return soBillNumber;
	}
	public void setSoBillNumber(String soBillNumber) {
		this.soBillNumber = soBillNumber;
	}
	
}