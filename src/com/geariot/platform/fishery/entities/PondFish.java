package com.geariot.platform.fishery.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class PondFish {

	private int id;
	private String fishName;
	private int type;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY )
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getFishName() {
		return fishName;
	}
	public void setFishName(String fishName) {
		this.fishName = fishName;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
	
}
