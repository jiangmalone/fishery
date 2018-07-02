package com.geariot.platform.fishery.entities;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="javaClassName")
public class Diagnosing {

    private int id;
    //0.DO;1.WT;2.pH
    private int type;
    //0.正常;1.预警;2.危险
    private int status;
    
	private String broken_name;		//
    private String solution;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getBroken_name() {
        return broken_name;
    }
    public void setBroken_name(String fish_name) {
        this.broken_name = broken_name;
    }
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }

    public String getSolution() {
        return solution;
    }
    public void setSolution(String solution) {
        this.solution = solution;
    }
    public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
}
