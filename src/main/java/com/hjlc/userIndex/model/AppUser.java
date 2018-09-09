package com.hjlc.userIndex.model;

public class AppUser {
	
	private String userId;
	private String userName;
	private String passWord;
	private String name;
	private String rights;
	private String poleId;
	private String lastLogin;
	private String ip;
	private String status;
	private String bz;
	private String phone;
	private String sfid;
	private String startTime;
	private String endTime;
	private Integer years;
	private String number;
	private String email;
	
	public AppUser() {
	}

	public AppUser(String userId, String userName, String passWord, String name, String rights, String poleId,
			String lastLogin, String ip, String status, String bz, String phone, String sfid, String startTime,
			String endTime, Integer years, String number, String email) {
		super();
		this.userId = userId;
		this.userName = userName;
		this.passWord = passWord;
		this.name = name;
		this.rights = rights;
		this.poleId = poleId;
		this.lastLogin = lastLogin;
		this.ip = ip;
		this.status = status;
		this.bz = bz;
		this.phone = phone;
		this.sfid = sfid;
		this.startTime = startTime;
		this.endTime = endTime;
		this.years = years;
		this.number = number;
		this.email = email;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassWord() {
		return passWord;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRights() {
		return rights;
	}

	public void setRights(String rights) {
		this.rights = rights;
	}

	public String getPoleId() {
		return poleId;
	}

	public void setPoleId(String poleId) {
		this.poleId = poleId;
	}

	public String getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(String lastLogin) {
		this.lastLogin = lastLogin;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getBz() {
		return bz;
	}

	public void setBz(String bz) {
		this.bz = bz;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getSfid() {
		return sfid;
	}

	public void setSfid(String sfid) {
		this.sfid = sfid;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public Integer getYears() {
		return years;
	}

	public void setYears(Integer years) {
		this.years = years;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "AppUser [userId=" + userId + ", userName=" + userName + ", passWord=" + passWord + ", name=" + name
				+ ", rights=" + rights + ", poleId=" + poleId + ", lastLogin=" + lastLogin + ", ip=" + ip + ", status="
				+ status + ", bz=" + bz + ", phone=" + phone + ", sfid=" + sfid + ", startTime=" + startTime
				+ ", endTime=" + endTime + ", years=" + years + ", number=" + number + ", email=" + email + "]";
	}
	
	

}
