package org.ctyc.mgt.summercamp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

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
			
			Stack<Participant> unassignedParticipants = new Stack<Participant>();
			unassignedParticipants.addAll(participants);
			
			Collection<DineTableGroup> dineTableGroupList = this.createEmptyTableGroupList();
			
			for (DineTableGroup dineTableGroup : dineTableGroupList){
					
				if (!isTableFull(dineTableGroup) && !CollectionUtils.isEmpty(unassignedParticipants)){
					Participant unassignedParticipant = unassignedParticipants.pop();
					dineTableGroup.getParticipants().add(unassignedParticipant);
				}else {
					continue;
				}
			}
				
			
			dineAssignmentPlan.put(dineTimeSlot, dineTableGroupList);
		}
	}

	public void doAssignment(){
		
		this.initAssignment();
	}
	
	private Collection<DineTableGroup> createEmptyTableGroupList(){
		if (CollectionUtils.isEmpty(this.participants)){
			return new ArrayList<DineTableGroup>();
		}
		
		if (this.tableCapacity <= 0){
			System.out.println("No table capacity");
			return new ArrayList<DineTableGroup>();
		}
		
		int numberOfTable = this.participants.size() / this.tableCapacity;
		if ((this.participants.size() % this.tableCapacity) > 0){
			numberOfTable++;
		}
		
		Collection<DineTableGroup> emptyTableGroupList = new ArrayList<DineTableGroup>();
		for (int i=0; i< numberOfTable; i++){
			DineTableGroup dineTableGroup = new DineTableGroup();
			dineTableGroup.setTableNumber(i + 1);
			emptyTableGroupList.add(dineTableGroup);
		}
		
		return emptyTableGroupList;
	}
	
	private boolean isTableFull(DineTableGroup dineTableGroup) {
		
		if (dineTableGroup.getParticipants().size() >= this.tableCapacity){
			return true;
		}
		return false;
	}
}
