package org.ctyc.mgt.summercamp.costfunction;

import org.ctyc.mgt.model.summercamp.DineTableGroup;
import org.ctyc.mgt.model.summercamp.Participant;

public class MentorInTableCostFunction extends AbstractCostFunction {

	public MentorInTableCostFunction(int priority, double weight) {
		super(priority, weight);
		this.name = "�C�i���@�ɮv";
	}

	@Override
	public double evaluateTableCost(DineTableGroup dineTableGroup) {
		
		for (Participant participant : dineTableGroup.getParticipants()){
			if (participant.isMentor()){
				return 0;
			}
		}

		return PENALTY_COST;
	}

}