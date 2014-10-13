package org.ctyc.mgt.summercamp;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.ctyc.mgt.model.summercamp.DineTableGroup;
import org.ctyc.mgt.model.summercamp.DineTimeSlot;

public class DineAssignmentPlan {
	
	private Map<DineTimeSlot, Collection<DineTableGroup>> plan;
	
	public DineAssignmentPlan(){
		
	}
	
	public Map<DineTimeSlot, Collection<DineTableGroup>> getPlan(){
		if (this.plan == null){
			this.plan = new HashMap<DineTimeSlot, Collection<DineTableGroup>>();
		}
		return this.plan;
	}

}
