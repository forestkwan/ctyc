package org.ctyc.mgt.summercamp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.ctyc.mgt.model.summercamp.DineTableGroup;
import org.ctyc.mgt.model.summercamp.DineTimeSlot;
import org.ctyc.mgt.model.summercamp.Participant;
import org.springframework.util.CollectionUtils;

public class DineAssignmentManager {
	
	private static Collection<DineTimeSlot> ALL_DINE_TIME_SLOT = createAllDineTimeSlot();
	private Collection<Participant> participants;
	private int tableCapacity;
	private Map<DineTimeSlot, Collection<DineTableGroup>> dineAssignmentPlan = new HashMap<DineTimeSlot, Collection<DineTableGroup>>();
	
	public DineAssignmentManager(Collection<Participant> participants, int tableCapacity){
		this.participants = participants;
		this.tableCapacity = tableCapacity;
	}
	
	private static Collection<DineTimeSlot> createAllDineTimeSlot(){
		
		if (!CollectionUtils.isEmpty(ALL_DINE_TIME_SLOT)){
			return ALL_DINE_TIME_SLOT;
		}
		Collection<DineTimeSlot> allDineTimeSlot = new ArrayList<DineTimeSlot>();

		allDineTimeSlot.add(new DineTimeSlot(1, DineTimeSlot.TimeOfDay.NIGHT));
		allDineTimeSlot.add(new DineTimeSlot(2, DineTimeSlot.TimeOfDay.MORNING));
		allDineTimeSlot.add(new DineTimeSlot(2, DineTimeSlot.TimeOfDay.NOON));
		allDineTimeSlot.add(new DineTimeSlot(2, DineTimeSlot.TimeOfDay.NIGHT));
		allDineTimeSlot.add(new DineTimeSlot(3, DineTimeSlot.TimeOfDay.MORNING));
		allDineTimeSlot.add(new DineTimeSlot(3, DineTimeSlot.TimeOfDay.NOON));
		allDineTimeSlot.add(new DineTimeSlot(3, DineTimeSlot.TimeOfDay.NIGHT));
		allDineTimeSlot.add(new DineTimeSlot(4, DineTimeSlot.TimeOfDay.MORNING));
		allDineTimeSlot.add(new DineTimeSlot(4, DineTimeSlot.TimeOfDay.NOON));
		allDineTimeSlot.add(new DineTimeSlot(4, DineTimeSlot.TimeOfDay.NIGHT));
		allDineTimeSlot.add(new DineTimeSlot(5, DineTimeSlot.TimeOfDay.MORNING));
		allDineTimeSlot.add(new DineTimeSlot(5, DineTimeSlot.TimeOfDay.NOON));
		
		return allDineTimeSlot;
	}
	
	public Map<DineTimeSlot, Collection<DineTableGroup>> getAssignmentPlan(){
		return this.dineAssignmentPlan;
	}
	
	private void initAssignment(){
		
		for (DineTimeSlot dineTimeSlot : ALL_DINE_TIME_SLOT){
			
			Collection<DineTableGroup> dineTableGroupList = new ArrayList<DineTableGroup>();
			
			dineAssignmentPlan.put(dineTimeSlot, dineTableGroupList);
		}
	}
	
	public void doAssignment(){
		
		this.initAssignment();
	}
}
