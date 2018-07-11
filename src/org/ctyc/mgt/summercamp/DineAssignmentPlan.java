package org.ctyc.mgt.summercamp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import org.ctyc.mgt.model.summercamp.DineTableGroup;

public class DineAssignmentPlan implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double cost;
	private String campName;
	private int day;
	private Collection<DineTableGroup> dineTableGroups;
	
	public DineAssignmentPlan(String campName, int day){
		this.campName = campName;
		this.day = day;
		cost = 0;
	}
	
	public String getCampName() {
		return campName;
	}

	public int getDay() {
		return day;
	}
	
	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public Collection<DineTableGroup> getDineTableGroups(){
		if (this.dineTableGroups == null){
			this.dineTableGroups = new ArrayList<DineTableGroup>();
		}
		return this.dineTableGroups;
	}

}
