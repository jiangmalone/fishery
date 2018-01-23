package com.geariot.platform.fishery.entities;

/**
 * @author mxy940127
 *
 */

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class WXUser {
	
	private int id;								//普通用户Id
	private String name;						//普通用户姓名
	private String sex;							//普通用户性别
	private String phone;						//普通用户联系方式
	private String life;						//普通用户养殖年限
	private String headimgurl;					//普通用户头像地址
	private String openId;						//普通用户微信的openId
	private String address;						//普通用户的联系地址
	private Date createDate;					//普通用户的创建时间
	private String relationId;					//普通用户的relationId(格式  = "WX" + id)
	private boolean isLogin;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getLife() {
		return life;
	}
	public void setLife(String life) {
		this.life = life;
	}
	public String getHeadimgurl() {
		return headimgurl;
	}
	public void setHeadimgurl(String headimgurl) {
		this.headimgurl = headimgurl;
	}
	public String getOpenId() {
		return openId;
	}
	public void setOpenId(String openId) {
		this.openId = openId;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public String getRelationId() {
		return relationId;
	}
	public void setRelationId(String relationId) {
		this.relationId = relationId;
	}
	public boolean isLogin() {
		return isLogin;
	}
	public void setLogin(boolean isLogin) {
		this.isLogin = isLogin;
	}
	
}
