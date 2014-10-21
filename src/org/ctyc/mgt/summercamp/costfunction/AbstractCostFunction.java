package org.ctyc.mgt.summercamp.costfunction;

import org.ctyc.mgt.summercamp.DineAssignmentPlan;

public abstract class AbstractCostFunction {
	
	private String name;
	private int priority;	// Value from 1 to 10. 1 is the highest priority
	private float weight;	// Value from 0 to 1
	
	public AbstractCostFunction(String name, int priority, float weight) {
		super();
		this.name = name;
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
