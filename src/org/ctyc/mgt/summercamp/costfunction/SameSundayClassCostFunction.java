package org.ctyc.mgt.summercamp.costfunction;

import org.apache.commons.lang3.StringUtils;
import org.ctyc.mgt.model.summercamp.DineTableGroup;
import org.ctyc.mgt.model.summercamp.Participant;
import org.springframework.util.CollectionUtils;

public class SameSundayClassCostFunction  extends AbstractCostFunction {

	private String[] excludedSundayClassArray = {"小學班", "搖籃班 / 幼兒班", "外傭"};
	
	public SameSundayClassCostFunction(int priority, double weight) {
		super(priority, weight);
		this.name = "";
		this.code = "SAME_SUNDAY_CLASS";
	}

	@Override
	public double evaluateTableCost(DineTableGroup dineTableGroup) {
		
		if (dineTableGroup == null || CollectionUtils.isEmpty(dineTableGroup.getParticipants())){
			return 0;
		}
		
		int lonelyParticipantCount = 0;
		for (Participant participant : dineTableGroup.getParticipants()){
			
			boolean hasSundayClassmate = false;
			
			for (Participant anotherParticipant : dineTableGroup.getParticipants()){
				if (participant.equals(anotherParticipant)){
					continue;
				}
				
				if (this.isExcluded(participant.getSundaySchoolClass())){
					continue;
				}
				
				if (StringUtils.equalsIgnoreCase(participant.getSundaySchoolClass(), anotherParticipant.getSundaySchoolClass())){
					hasSundayClassmate = true;
					break;
				}
			}
			
			if (!hasSundayClassmate){
				lonelyParticipantCount++;
			}
			
		}
		
		if (lonelyParticipantCount == 0){
			return 0;
		}
		
		double size = dineTableGroup.getParticipants().size();
		double factor = lonelyParticipantCount / size;
		
		double cost = MAX_COST * factor * weight;
		return cost;
	}
	
	private boolean isExcluded(String sundayClass){
		if (StringUtils.isBlank(sundayClass)){
			return true;
		}
		
		for (String excludedSundayClass : excludedSundayClassArray){
			if (StringUtils.equalsIgnoreCase(excludedSundayClass, sundayClass)){
				return true;
			}
		}
		
		return false;
	}
}
