package org.ctyc.mgt.summercamp.costfunction;

import org.apache.commons.lang3.StringUtils;
import org.ctyc.mgt.model.summercamp.DineTableGroup;
import org.ctyc.mgt.model.summercamp.Participant;
import org.springframework.util.CollectionUtils;

public class SameGroupCostFunction extends AbstractCostFunction {

	public SameGroupCostFunction(int priority, double weight) {
		super(priority, weight);
		this.name = "";
		this.code = "SAME_GROUP";
	}

	@Override
	public double evaluateTableCost(DineTableGroup dineTableGroup) {
		
		if (dineTableGroup == null || CollectionUtils.isEmpty(dineTableGroup.getParticipants())){
			return 0;
		}
		
		int noSundayClassmateCount = 0;
		for (Participant participant : dineTableGroup.getParticipants()){
			
			if (participant.getFamilyGroup() != null && !CollectionUtils.isEmpty(participant.getFamilyGroup().getBelieverIds())){
				continue;
			}
			
			if (participant.isGroupMentor() || participant.isMentor() || StringUtils.contains(participant.getSundaySchoolClass(), "導師")){
				continue;
			}
			
			boolean hasSundayClassmate = false;
			for (Participant tempParticipant : dineTableGroup.getParticipants()){
				
				if (participant.equals(tempParticipant)){
					continue;
				}
				
				if (participant.getGroupNumber() == tempParticipant.getGroupNumber()){
					hasSundayClassmate = true;
				}
			}
			
			if (hasSundayClassmate == false){
				noSundayClassmateCount++;
			}
		}
		
		double penaltyCost = ((double)noSundayClassmateCount / (double)dineTableGroup.getParticipants().size()) * MAX_COST;

		return penaltyCost * weight;
	}

}