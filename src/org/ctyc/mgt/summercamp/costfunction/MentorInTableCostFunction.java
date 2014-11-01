package org.ctyc.mgt.summercamp.costfunction;

import org.ctyc.mgt.model.summercamp.DineTableGroup;
import org.ctyc.mgt.model.summercamp.Participant;
import org.springframework.util.CollectionUtils;

public class MentorInTableCostFunction extends AbstractCostFunction {

	public MentorInTableCostFunction(int priority, double weight) {
		super(priority, weight);
		this.name = "每檯有一導師";
	}

	@Override
	public double evaluateTableCost(DineTableGroup dineTableGroup) {
		
		if (dineTableGroup == null || CollectionUtils.isEmpty(dineTableGroup.getParticipants())){
			return 0;
		}
		
		for (Participant participant : dineTableGroup.getParticipants()){
			if (participant.isMentor()){
				return 0;
			}
		}

		return PENALTY_COST;
	}

}