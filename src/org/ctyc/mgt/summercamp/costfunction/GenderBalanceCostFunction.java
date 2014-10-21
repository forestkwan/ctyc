package org.ctyc.mgt.summercamp.costfunction;

import org.ctyc.mgt.model.Sex;
import org.ctyc.mgt.model.summercamp.DineTableGroup;
import org.ctyc.mgt.model.summercamp.Participant;
import org.ctyc.mgt.summercamp.DineAssignmentPlan;
import org.springframework.util.CollectionUtils;


public class GenderBalanceCostFunction extends AbstractCostFunction {

	public GenderBalanceCostFunction(String name, int priority, float weight) {
		super(name, priority, weight);
	}

	@Override
	public double doCompute(DineAssignmentPlan dineAssignmentPlan) {
		
		if (CollectionUtils.isEmpty(dineAssignmentPlan.getPlan())){
			return 0;
		}
		
		double cost = 0;
		int diff = 0;
		for (DineTableGroup dineTableGroup : dineAssignmentPlan.getPlan()){
			
			int numberOfMale = 0;
			int numberOfFemale = 0;
			
			for (Participant participant : dineTableGroup.getParticipants()){
				if (participant.getSex() == Sex.MALE){
					numberOfMale ++;
				}else {
					numberOfFemale++;
				}
			}
			diff = Math.abs(numberOfFemale - numberOfMale);
			cost += diff;
		}
		return cost;
	}

}
