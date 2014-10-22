package org.ctyc.mgt.summercamp.costfunction;

import org.ctyc.mgt.summercamp.DineAssignmentPlan;

public abstract class AbstractCostFunction {
	
	protected String name = "";
	protected int priority;	// Value from 1 to 10. 1 is the highest priority
	protected float weight;	// Value from 0 to 1
	
	public AbstractCostFunction(int priority, float weight) {
		super();
		this.priority = priority;
		this.weight = weight;
	}
	
	public String getName() {
		return name;
	}
	
	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public float getWeight() {
		return weight;
	}

	public void setWeight(float weight) {
		this.weight = weight;
	}
	
	abstract public double doCompute(DineAssignmentPlan dineAssignmentPlan);
}
