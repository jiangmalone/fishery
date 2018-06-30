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
    private int type;
    private String broken_name;
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

}
