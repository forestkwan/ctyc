package org.ctyc.mgt.summercamp;

import java.util.ArrayList;
import java.util.Collection;

import org.ctyc.mgt.model.summercamp.DineTableGroup;

public class DineAssignmentPlan {
	
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

	public Collection<DineTableGroup> getPlan(){
		if (this.plan == null){
			this.plan = new ArrayList<DineTableGroup>();
		}
		return this.plan;
	}

}
