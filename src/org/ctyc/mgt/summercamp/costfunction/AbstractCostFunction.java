package org.ctyc.mgt.summercamp.costfunction;

import org.ctyc.mgt.model.summercamp.DineTableGroup;
import org.ctyc.mgt.summercamp.DineAssignmentPlan;
import org.springframework.util.CollectionUtils;

public abstract class AbstractCostFunction {
	
	public static int PENALTY_COST = 10000;
	public static short MAX_COST = 100;
	protected String name = "";
	protected String code = "";

	protected int priority;	// Value from 1 to 10. 1 is the highest priority
	protected double weight;	// Value from 0 to 1
	
	public AbstractCostFunction(int priority, double weight) {
		super();
		this.priority = priority;
		this.weight = weight;
	}
	
	public String getName() {
		return name;
	}
	
	public String getCode() {
		return code;
	}
	
	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}
	
	public abstract double evaluateTableCost(DineTableGroup dineTableGroup);
}
