package org.ctyc.mgt.summercamp;

import java.util.ArrayList;
import java.util.Collection;

import org.ctyc.mgt.model.summercamp.DineTableGroup;
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

	public void evaluatePlan(DineAssignmentPlan plan){
		
		if (CollectionUtils.isEmpty(plan.getDineTableGroups())){
			return;
		}
		
		double totalCost = 0;
		for (DineTableGroup dineTableGroup : plan.getDineTableGroups()){
			evaluateTable(dineTableGroup);
			totalCost += dineTableGroup.getCost();
		}
		
		plan.setCost(totalCost);
	}
	
	public void evaluateTable(DineTableGroup dineTableGroup){
		
		double cost = 0;
		
		for (AbstractCostFunction costFunction : this.costFunctions){
			double evaluatedCost = costFunction.evaluateTableCost(dineTableGroup);
			dineTableGroup.getEvaluationResultMap().put(costFunction.getCode(), evaluatedCost);
			cost += evaluatedCost;
		}
		
		for (AbstractCostFunction constraintFunction : this.constraintFunctions){
			double evaluatedCost = constraintFunction.evaluateTableCost(dineTableGroup);
			dineTableGroup.getEvaluationResultMap().put(constraintFunction.getCode(), evaluatedCost);
			cost += evaluatedCost;
		}
		
		dineTableGroup.setCost(cost);
		
	}
}
