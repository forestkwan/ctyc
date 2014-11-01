package org.ctyc.mgt.summercamp.costfunction;

import java.util.ArrayList;
import java.util.Collection;

import org.ctyc.mgt.model.Believer;
import org.ctyc.mgt.model.FamilyGroup;
import org.ctyc.mgt.model.summercamp.DineTableGroup;
import org.ctyc.mgt.model.summercamp.Participant;
import org.springframework.util.CollectionUtils;

public class FamilyGroupCostFunction extends AbstractCostFunction {

	public FamilyGroupCostFunction(int priority, double weight) {
		super(priority, weight);
		this.name = "®a®x¦PÂi";
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
			
			for (Believer believer : familyGroup.getBelievers()){
				if (!isExistInTable(dineTableGroup, believer)){
					return PENALTY_COST;
				}
			}
			
		}
		
		return 0;
	}
	
	private boolean isExistInTable(DineTableGroup dineTableGroup, Believer targetBeliever){
		
		if (dineTableGroup == null || CollectionUtils.isEmpty(dineTableGroup.getParticipants())){
			return false;
		}
		
		for (Believer believer :  dineTableGroup.getParticipants()){
			if (believer.equals(targetBeliever)){
				return true;
			}
		}
		
		return false;
		
	}

}
