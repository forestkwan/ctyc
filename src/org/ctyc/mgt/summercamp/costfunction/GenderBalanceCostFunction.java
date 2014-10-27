package org.ctyc.mgt.summercamp.costfunction;

import org.ctyc.mgt.model.Sex;
import org.ctyc.mgt.model.summercamp.DineTableGroup;
import org.ctyc.mgt.model.summercamp.Participant;


public class GenderBalanceCostFunction extends AbstractCostFunction {

	public GenderBalanceCostFunction(int priority, double weight) {
		super(priority, weight);
		this.name = "男女比例平衡";
	}

	@Override
	public double evaluateTableCost(DineTableGroup dineTableGroup) {
		
		int numberOfMale = 0;
		int numberOfFemale = 0;
		
		for (Participant participant : dineTableGroup.getParticipants()){
			if (participant.getSex() == Sex.MALE){
				numberOfMale ++;
			}else {
				numberOfFemale++;
			}
		}
		double diff = Math.abs(numberOfFemale - numberOfMale);
		double size = dineTableGroup.getParticipants().size();
		double factor = diff / size;
		
		
		double cost = MAX_COST * factor * weight;
		return cost;
	}

}
