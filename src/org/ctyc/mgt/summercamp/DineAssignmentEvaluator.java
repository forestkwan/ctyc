package org.ctyc.mgt.summercamp;

import java.util.ArrayList;
import java.util.Collection;

import org.ctyc.mgt.summercamp.costfunction.AbstractCostFunction;
import org.springframework.util.CollectionUtils;

public class DineAssignmentEvaluator {
	
	private Collection<AbstractCostFunction> costFunctions = new ArrayList<AbstractCostFunction>();
	private Collection<AbstractCostFunction> constraintFunctions = new ArrayList<AbstractCostFunction>();
	
	public DineAssignmentEvaluator(
			Collection<AbstractCostFunction> costFunctions,
			Collection<AbstractCostFunction> constraintFunctions){
		
		if (!CollectionUtils.isEmpty(costFunctions)){
			this.costFunctions.addAll(costFunctions);
		}
		
		if (!CollectionUtils.isEmpty(constraintFunctions)){
			this.constraintFunctions.addAll(constraintFunctions);
		}
	}

	public void evaluate(DineAssignmentPlan plan){

		double cost = 0;
		
		for (AbstractCostFunction costFunction : this.costFunctions){
			cost = costFunction.doCompute(plan);
			plan.setCost(plan.getCost() + cost);
		}
		
		for (AbstractCostFunction constraintFunction : this.constraintFunctions){
			cost = constraintFunction.doCompute(plan);
			plan.setCost(plan.getCost() + cost);
		}
	}
}
