package org.ctyc.mgt.summercamp.costfunction;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.ctyc.mgt.model.Believer;
import org.ctyc.mgt.model.FamilyGroup;
import org.ctyc.mgt.model.summercamp.DineTableGroup;
import org.ctyc.mgt.model.summercamp.Participant;
import org.springframework.util.CollectionUtils;

public class FamilyGroupCostFunction extends AbstractCostFunction {

	public FamilyGroupCostFunction(int priority, double weight) {
		super(priority, weight);
		this.name = "";
		this.code = "FAMILY_SAME_TABLE";
	}

	@Override
	public double evaluateTableCost(DineTableGroup dineTableGroup) {
		
		if (dineTableGroup == null || CollectionUtils.isEmpty(dineTableGroup.getParticipants())){
			return 0;
		}
		
		Collection<FamilyGroup> familyGroups = new ArrayList<FamilyGroup>();
		
		for (Participant participant : dineTableGroup.getParticipants()){
			if (participant.getFamilyGroup() == null){
				continue;
			}
			
			familyGroups.add(participant.getFamilyGroup());
		}
		
		/* All family group member must be exist in the table */
		for (FamilyGroup familyGroup : familyGroups){
			
			for (String believerId : familyGroup.getBelieverIds()){
				if (!isExistInTable(dineTableGroup, believerId)){
					return PENALTY_COST * weight;
				}
			}
			
		}
		
		return 0;
	}
	
	private boolean isExistInTable(DineTableGroup dineTableGroup, String believerId){
		
		if (dineTableGroup == null || CollectionUtils.isEmpty(dineTableGroup.getParticipants())){
			return false;
		}
		
		for (Believer believer :  dineTableGroup.getParticipants()){
			if (StringUtils.equalsIgnoreCase(believer.getId(), believerId)){
				return true;
			}
		}
		
		return false;
		
	}

}
