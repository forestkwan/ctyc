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
	private Collection<DineTableGroup> plan;
	
	public DineAssignmentPlan(){
		cost = 0;
	}
	
	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public Collection<DineTableGroup> getDineTableGroups(){
		if (this.plan == null){
			this.plan = new ArrayList<DineTableGroup>();
		}
		return this.plan;
	}

}
