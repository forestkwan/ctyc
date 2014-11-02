package org.ctyc.mgt.summercamp.costfunction;

import org.ctyc.mgt.model.summercamp.DineTableGroup;


public class GenderBalanceCostFunction extends AbstractCostFunction {

	public GenderBalanceCostFunction(int priority, double weight) {
		super(priority, weight);
		this.name = "男女比例平衡";
	}

	@Override
	public double evaluateTableCost(DineTableGroup dineTableGroup) {
		
		if (dineTableGroup == null){
			return 0;
		}

		double netGenderBalance = Math.abs(dineTableGroup.getNetGenderBalance());
		double size = dineTableGroup.getParticipants().size();
		double factor = netGenderBalance / size;
		
		double cost = MAX_COST * factor * weight;
		return cost;
	}

}
