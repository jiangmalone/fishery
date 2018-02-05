package com.geariot.platform.fishery.entities;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

@Entity
public class DataAlarm {
	private int id;
	private String deviceSn;
	private int way;
	private String relation;// 绑定的用户，WX微信用户，CO企业用户
	//private boolean isWatch;// 是否已读
	private List<AlarmMessage> message;
	private int pondId;// 绑定的塘口ID
	private Date createDate;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDeviceSn() {
		return deviceSn;
	}

	public void setDeviceSn(String deviceSn) {
		this.deviceSn = deviceSn;
	}

	public int getWay() {
		return way;
	}

	public void setWay(int way) {
		this.way = way;
	}

	public String getRelation() {
		return relation;
	}

	public void setRelation(String relation) {
		this.relation = relation;
	}

	/*public boolean isWatch() {
		return isWatch;
	}

	public void setWatch(boolean isWatch) {
		this.isWatch = isWatch;
	}*/

	@OneToMany(cascade = { CascadeType.ALL }, orphanRemoval = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "DataalarmId", foreignKey = @ForeignKey(name = "none"))
	public List<AlarmMessage> getMessage() {
		return message;
	}

	public void setMessage(List<AlarmMessage> message) {
		this.message = message;
	}

	public int getPondId() {
		return pondId;
	}

	public void setPondId(int pondId) {
		this.pondId = pondId;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

}
