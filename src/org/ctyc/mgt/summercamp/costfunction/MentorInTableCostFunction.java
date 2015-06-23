package org.ctyc.mgt.summercamp.costfunction;

import org.apache.commons.lang3.StringUtils;
import org.ctyc.mgt.model.summercamp.DineTableGroup;
import org.ctyc.mgt.model.summercamp.Participant;
import org.springframework.util.CollectionUtils;

public class MentorInTableCostFunction extends AbstractCostFunction {

	public MentorInTableCostFunction(int priority, double weight) {
		super(priority, weight);
		this.name = "";
		this.code = "MENTOR_IN_TABLE";
	}

	@Override
	public double evaluateTableCost(DineTableGroup dineTableGroup) {
		
		if (dineTableGroup == null || CollectionUtils.isEmpty(dineTableGroup.getParticipants())){
			return 0;
		}
		
		for (Participant participant : dineTableGroup.getParticipants()){
			if (participant.isMentor() || participant.isGroupMentor() || StringUtils.contains(participant.getSundaySchoolClass(), "導師")){
				return 0;
			}
		}

		return PENALTY_COST * weight;
	}

}