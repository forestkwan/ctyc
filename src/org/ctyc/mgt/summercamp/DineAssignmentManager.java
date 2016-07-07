package org.ctyc.mgt.summercamp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.ctyc.mgt.model.FamilyGroup;
import org.ctyc.mgt.model.Gender;
import org.ctyc.mgt.model.summercamp.CampSite;
import org.ctyc.mgt.model.summercamp.DineAvailability;
import org.ctyc.mgt.model.summercamp.DineTableGroup;
import org.ctyc.mgt.model.summercamp.DineTimeSlot;
import org.ctyc.mgt.model.summercamp.Participant;
import org.ctyc.mgt.summercamp.costfunction.AbstractCostFunction;
import org.ctyc.mgt.utils.RandomnessUtils;
import org.springframework.util.CollectionUtils;

public class DineAssignmentManager {
	
	// Static constant
	private static Collection<DineTimeSlot> ALL_DINE_TIME_SLOT = createAllDineTimeSlot();
	
	// Dine Assignment Object
	private DineAssignmentPlan plan;
	private DineAssignmentEvaluator evaluator;
	
	// Input Object
	private CampSite campSite;
	private Collection<Participant> participants;
	private Map<String, Participant> participantMap;
	private int tableCapacity;
	private static Map<String, Map<Integer, String>> campTableMentorMap;
	private static String SAVE_HOME;
	
	// Private calculation object
	private Random randomObj;
	
	public DineAssignmentManager(
			String campSiteName,
			int day,
			CampSite campSite,
			int tableCapacity,
			Collection<AbstractCostFunction> costFunctions,
			Collection<AbstractCostFunction> constraintFunctions){
		
		this.campSite = campSite;
		this.participants = campSite.getParticipants();
		this.tableCapacity = tableCapacity;
		this.plan = new DineAssignmentPlan(campSiteName, day);
		this.randomObj = new Random();
		
		this.participantMap = new HashMap<String, Participant>();
		for(Participant participant : this.participants){
			this.participantMap.put(participant.getId(), participant);
		}
		
		this.evaluator = new DineAssignmentEvaluator(costFunctions, constraintFunctions);
	}
	
	static {
		SAVE_HOME = System.getenv("SAVE_HOME");
		
		if (SystemUtils.IS_OS_WINDOWS){
			
			if (SAVE_HOME == null){
				SAVE_HOME = "c:\\CTYCSave";
			}
			
		}else if (SystemUtils.IS_OS_MAC){
			
			if (SAVE_HOME == null){
				SAVE_HOME = "CTYCSave";
			}
		}
	}
	
	public DineAssignmentManager(
			String campSiteName,
			int day,
			CampSite campSite,
			int tableCapacity,
			Collection<AbstractCostFunction> costFunctions,
			Collection<AbstractCostFunction> constraintFunctions,
			int seed){
		
		this(campSiteName, day, campSite, tableCapacity, costFunctions, constraintFunctions);
		this.randomObj = new Random(seed);
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
	
	public DineAssignmentPlan getAssignmentPlan(){
		return this.plan;
	}
	
	private void initAssignment(){
		
		System.out.println("Total Number of Participants: " + this.participants.size());
		printCurrentAssignmentInfo();
		
		Collection<Participant> unassignedParticipants = filterLeftParticipants(this.participants);
		Collection<Participant> assignedParticipants = new HashSet<Participant>();
		
		Collection<DineTableGroup> specialDineTableGroups = this.createDineTableGroupList(unassignedParticipants, 1, true);
		Collection<DineTableGroup> dineTableGroups = this.createDineTableGroupList(unassignedParticipants, specialDineTableGroups.size() + 1, false);
		
		listOutParticipants(unassignedParticipants);
		
		printOutAssignmentStatus("Before preassigning special group", unassignedParticipants, assignedParticipants);
		assignPreassignedAssignmentAndFamily(unassignedParticipants, assignedParticipants, dineTableGroups);
		printOutAssignmentStatus("After preassigning special group", unassignedParticipants, assignedParticipants);
		
		printOutAssignmentStatus("Before preassigning normal group", unassignedParticipants, assignedParticipants);
		assignPreassignedAssignmentAndFamily(unassignedParticipants, assignedParticipants, specialDineTableGroups);
		printOutAssignmentStatus("After preassigning normal group", unassignedParticipants, assignedParticipants);
		
		printOutAssignmentStatus("Before assigning special group family", unassignedParticipants, assignedParticipants);
		assignFamilyGroupToSpecialGroupTable(unassignedParticipants, assignedParticipants, specialDineTableGroups);
		printOutAssignmentStatus("After assigning special group family", unassignedParticipants, assignedParticipants);
		
		printOutAssignmentStatus("Before assigning normal group family", unassignedParticipants, assignedParticipants);
		assignFamilyGroupToTable(unassignedParticipants, assignedParticipants, dineTableGroups);
		printOutAssignmentStatus("After assigning normal group family", unassignedParticipants, assignedParticipants);
		
		printOutAssignmentStatus("Before assigning special group participants", unassignedParticipants, assignedParticipants);
		assignSpecialGroupToTable2(unassignedParticipants, assignedParticipants, specialDineTableGroups);
		printOutAssignmentStatus("After assigning special group participants", unassignedParticipants, assignedParticipants);
		
		printOutAssignmentStatus("Before assigning special group participants", unassignedParticipants, assignedParticipants);
		assignParticipantToTable2(unassignedParticipants, assignedParticipants, dineTableGroups);
		printOutAssignmentStatus("After assigning special group participants", unassignedParticipants, assignedParticipants);
		
		listOutParticipants(assignedParticipants);
		
//		assignTableMentor(filteredParticipants, assignedParticipants, dineTableGroups);
//		assignTableMentor(filteredParticipants, assignedParticipants, specialDineTableGroups);
		
//		assignFamilyGroupToSpecialGroupTable(unassignedParticipants, assignedParticipants, specialDineTableGroups);
//		assignMentorToSpecialGroupTable(unassignedParticipants, assignedParticipants, specialDineTableGroups);

		
//		assignGroupMentorToTable(unassignedParticipants, assignedParticipants, dineTableGroups);
		
//		assignGroupMemberWithMentor(unassignedParticipants, assignedParticipants, dineTableGroups);
		
//		assignThreeSameGroupParticipantsToTables(unassignedParticipants, assignedParticipants, dineTableGroups);
//		assignParticipantToTable(unassignedParticipants, assignedParticipants, dineTableGroups);
//		assignParticipantToTable(unassignedParticipants, assignedParticipants, specialDineTableGroups);
		
		this.plan.getDineTableGroups().addAll(specialDineTableGroups);
		this.plan.getDineTableGroups().addAll(dineTableGroups);
	}

	private Collection<Participant> filterLeftParticipants(Collection<Participant> participants) {
		
		int day = this.getAssignmentPlan().getDay();
		
		Collection<Participant> filteredParticipants = new HashSet<Participant>();
		
		for (Participant participant : participants){
			
			boolean isMorningJoin = false;
			boolean isNoonJoin = false;
			boolean isNightJoin = false;
			
			Collection<DineAvailability> dineAvailabilitys = participant.getDineAvailabilitys();
			
			for (DineAvailability dineAvailability : dineAvailabilitys){
				
				if (dineAvailability.getNumberOfDay() != day){
					continue;
				}
				
				if (StringUtils.equalsIgnoreCase(DineTimeSlot.TimeOfDay.MORNING.toString(), dineAvailability.getTimeOfDay())){
					isMorningJoin = dineAvailability.isJoin();
				}else if (StringUtils.equalsIgnoreCase(DineTimeSlot.TimeOfDay.NOON.toString(), dineAvailability.getTimeOfDay())){
					isNoonJoin = dineAvailability.isJoin();
				}else if (StringUtils.equalsIgnoreCase(DineTimeSlot.TimeOfDay.NIGHT.toString(), dineAvailability.getTimeOfDay())){
					isNightJoin = dineAvailability.isJoin();
				}
			}
			
			if (!isMorningJoin && !isNoonJoin && !isNightJoin){
				continue;
			}
			
			filteredParticipants.add(participant);
		}
		
		return filteredParticipants;
		
	}

	private void assignPreassignedAssignmentAndFamily(
			Collection<Participant> unassignedParticipants,
			Collection<Participant> assignedParticipants,
			Collection<DineTableGroup> dineTableGroups) {
		
		Collection<Participant> operatedParticipant = new HashSet<Participant>();
		
		// Assign the mentor to pre-assigned table
		for (Participant participant : unassignedParticipants){
			
			if (this.getPreassignedTable(participant.getId()) != null){
				
				int preAssignedTable = this.getPreassignedTable(participant.getId()).intValue();
				
				for (DineTableGroup dineTableGroup : dineTableGroups){
					
					if (dineTableGroup.getTableNumber() == preAssignedTable){
						
						dineTableGroup.getParticipants().add(participant);
						operatedParticipant.add(participant);
						
						Collection<Participant> familyMembers = findFamilyMembers(participant);
						if (!CollectionUtils.isEmpty(familyMembers)){
							
							/* Prevent the family member that is not in the unassigned list */
							familyMembers.retainAll(unassignedParticipants);
							
							dineTableGroup.getParticipants().addAll(familyMembers);
							operatedParticipant.addAll(familyMembers);
						}
						
						break;
					}
				}
				
			}
		}
		
		unassignedParticipants.removeAll(operatedParticipant);
		assignedParticipants.addAll(operatedParticipant);
	}
	
	private void assignTableMentor(
			Collection<Participant> participants,
			Collection<Participant> assignedParticipants,
			Collection<DineTableGroup> dineTableGroups) {
		
		if (campTableMentorMap != null && campTableMentorMap.get(this.plan.getCampName()) != null){
			
			Map<Integer, String> tableMentorMap = campTableMentorMap.get(this.plan.getCampName());
			
			for (DineTableGroup dineTableGroup : dineTableGroups){
				
				String tableMentorId = tableMentorMap.get(dineTableGroup.getTableNumber());
				if (tableMentorId == null){
					continue;
				}
				
				Participant tableMentor = this.participantMap.get(tableMentorId);
				if (tableMentor == null || assignedParticipants.contains(tableMentor)){
					continue;
				}
				
				dineTableGroup.getParticipants().add(tableMentor);
				assignedParticipants.add(tableMentor);
				
				Collection<Participant> familyMembers = findFamilyMembers(tableMentor);
				if (!CollectionUtils.isEmpty(familyMembers)){
					dineTableGroup.getParticipants().addAll(familyMembers);
					assignedParticipants.addAll(familyMembers);
				}
				
			}
		}
	}
	
	private void assignFamilyGroupToSpecialGroupTable(
			Collection<Participant> unassignedParticipants,
			Collection<Participant> assignedParticipants,
			Collection<DineTableGroup> dineTableGroups) {
		
		if (CollectionUtils.isEmpty(unassignedParticipants) || CollectionUtils.isEmpty(dineTableGroups)){
			return;
		}
		
		Collection<Participant> operatedParticipants = new HashSet<Participant>();
		
		Map<String, FamilyGroup> familyGroupMap = new HashMap<String, FamilyGroup>();
		for (Participant participant : unassignedParticipants){
			
			if (participant.getFamilyGroup() == null){
				continue;
			}
			
			if (participant.getSpecialGroup() == null || participant.getSpecialGroup() <= 0){
				continue;
			}
			
			if (assignedParticipants.contains(participant)){
				continue;
			}
			
			familyGroupMap.put(participant.getFamilyGroup().getFamilyId(), participant.getFamilyGroup());
		}
		
		for (Entry<String, FamilyGroup> entry : familyGroupMap.entrySet()){
			FamilyGroup familyGroup = entry.getValue();
			
			DineTableGroup tempTableGroup = null;
			
			Collection<DineTableGroup> availableTables = new ArrayList<DineTableGroup>();
			for (DineTableGroup dineTableGroup : dineTableGroups){
				
				if (dineTableGroup.getParticipants().size() > 0){
					continue;
				}
				
				int emptySeat = this.tableCapacity - dineTableGroup.getParticipants().size();
				
				if (familyGroup.getBelieverIds().size() + 2 > emptySeat){
					continue;
				}
				
				availableTables.add(dineTableGroup);
			}
			
			if (availableTables.size() == 0){
				for (DineTableGroup dineTableGroup : dineTableGroups){
					
					int emptySeat = this.tableCapacity - dineTableGroup.getParticipants().size();
					
					if (familyGroup.getBelieverIds().size()  + 2 > emptySeat){
						continue;
					}
					
					availableTables.add(dineTableGroup);
				}
			}
			
			tempTableGroup = RandomnessUtils.pickRandomDineTableGroup(availableTables, this.randomObj);
			if (tempTableGroup == null){
				tempTableGroup = RandomnessUtils.pickRandomDineTableGroup(dineTableGroups, this.randomObj);
			}
			
			for (String believerId : familyGroup.getBelieverIds()){
				Participant tempParticipant = this.participantMap.get(believerId);
				
				if (assignedParticipants.contains(tempParticipant)){
					continue;
				}
				
				tempTableGroup.getParticipants().add(tempParticipant);
				operatedParticipants.add(tempParticipant);
			}
		}
		
		unassignedParticipants.removeAll(operatedParticipants);
		assignedParticipants.addAll(operatedParticipants);
	}
	
	private void assignMentorToSpecialGroupTable(
			Collection<Participant> unassignedParticipants,
			Collection<Participant> assignedParticipants,
			Collection<DineTableGroup> dineTableGroups) {
		
		if (CollectionUtils.isEmpty(unassignedParticipants) || CollectionUtils.isEmpty(dineTableGroups)){
			return;
		}
		
		Collection<Participant> operatedParticipants = new HashSet<Participant>();
		
		Collection<Participant> specialGroupMentors = new ArrayList<Participant>();
		for (Participant participant : unassignedParticipants){
			if (participant.getSpecialGroup() == null || participant.getSpecialGroup() <= 0){
				continue;
			}
			
			if (assignedParticipants.contains(participant)){
				continue;
			}
			
			if (participant.isGroupMentor() || participant.isMentor() || StringUtils.contains(participant.getSundaySchoolClass(), "導師")){
				specialGroupMentors.add(participant);
			}
			
		}
		
		// Assign special group mentors to table
		for (Participant mentor : specialGroupMentors){
			DineTableGroup dineTable = randomlyPickMinimumGroupMentorTable(dineTableGroups);
			
			if (dineTable == null){
				continue;
			}
			
			dineTable.getParticipants().add(mentor);
			operatedParticipants.add(mentor);
		}
		
		unassignedParticipants.removeAll(operatedParticipants);
		assignedParticipants.addAll(operatedParticipants);
		
		// Since there may not have enough special group mentor, then assign non-special group mentor to special group
		Collection<DineTableGroup> noMentorDineTables = new ArrayList<DineTableGroup>();
		for (DineTableGroup dineTableGroup : dineTableGroups){
			if (dineTableGroup.getNoOfGroupMentor() <= 0){
				noMentorDineTables.add(dineTableGroup);
			}
		}
		
		if (CollectionUtils.isEmpty(noMentorDineTables)){
			return;
		}
		
		Collection<Participant> normalMentors = new ArrayList<Participant>();
		for (Participant participant : unassignedParticipants){
			
			if (!participant.isGroupMentor() || !participant.isMentor() || !StringUtils.contains(participant.getSundaySchoolClass(), "導師")){
				continue;
			}
			
			if (assignedParticipants.contains(participant)){
				continue;
			}
			
			if (participant.getSpecialGroup() != null && participant.getSpecialGroup() > 0){
				continue;
			}
			
			normalMentors.add(participant);
		}
		
		for (DineTableGroup dineTableGroup : noMentorDineTables){
			Participant mentor = RandomnessUtils.pickRandomParticipant(normalMentors, this.randomObj);
			if (mentor != null){
				
				dineTableGroup.getParticipants().add(mentor);
				operatedParticipants.add(mentor);
				normalMentors.remove(mentor);
			}
		}
		
		unassignedParticipants.removeAll(operatedParticipants);
		assignedParticipants.addAll(operatedParticipants);
		
	}

	private void assignSpecialGroupToTable(
			Collection<Participant> unassignedParticipants,
			Collection<Participant> assignedParticipants,
			Collection<DineTableGroup> dineTableGroups) {
		
		if (CollectionUtils.isEmpty(unassignedParticipants) || CollectionUtils.isEmpty(dineTableGroups)){
			return;
		}
		
		Collection<Participant> operatedParticipants = new HashSet<Participant>();
		
		Collection<Participant> specialParticipants = new ArrayList<Participant>();
		for (Participant participant : unassignedParticipants){
			
			if (assignedParticipants.contains(participant)){
				continue;
			}
			
			if (participant.getSpecialGroup() != null && participant.getSpecialGroup() > 0){
				specialParticipants.add(participant);
			}
		}
		
		if (CollectionUtils.isEmpty(specialParticipants) || CollectionUtils.isEmpty(dineTableGroups)){
			return;
		}
		
		/* Create a map according to participants' Group Number*/
		Map<Integer, Collection<Participant>> groupNumberParticipantMap = new HashMap<Integer, Collection<Participant>>();
		for (Participant unassignedParticipant : specialParticipants){
			
			Collection<Participant> groupNumberParticipants = groupNumberParticipantMap.get(unassignedParticipant.getGroupNumber());
			if (groupNumberParticipants == null){
				groupNumberParticipants = new ArrayList<Participant>();
				groupNumberParticipants.add(unassignedParticipant);
				groupNumberParticipantMap.put(unassignedParticipant.getGroupNumber(), groupNumberParticipants);
			}else {
				groupNumberParticipants.add(unassignedParticipant);
			}
		}
		
		/*
		 * For each group of participant, randomly pick a table with enough vacancy
		 * Randomly pick 2 or 3 participants from the group of participants
		 * assign the participants to the table
		 * Add the participants to assigned participant list
		 * */
		Collection<Participant> remainingParticipants = new ArrayList<Participant>();
		
		for (Entry<Integer, Collection<Participant>> entry : groupNumberParticipantMap.entrySet()){
			int groupNumber = entry.getKey();
			Collection<Participant> groupedParticipants = entry.getValue();
			
			while (!CollectionUtils.isEmpty(groupedParticipants)){
				
				Collection<Participant> selectedParticipants = RandomnessUtils.pickRandomMultiParticipant(groupedParticipants, this.randomObj);
				if (selectedParticipants == null){
					break;
				}
				
				for (Participant p : selectedParticipants){
					System.out.print(p.getId());
				}
				
				DineTableGroup selectedDineTable = randomPickTableForGroupParticipantAssignment(dineTableGroups, groupNumber, selectedParticipants, true);
				
				if (selectedDineTable == null){
					remainingParticipants.addAll(selectedParticipants);
					groupedParticipants.removeAll(selectedParticipants);
					continue;
				}
				
				selectedDineTable.getParticipants().addAll(selectedParticipants);
				operatedParticipants.addAll(selectedParticipants);
				groupedParticipants.removeAll(selectedParticipants);
			}
		}
		
		unassignedParticipants.removeAll(operatedParticipants);
		assignedParticipants.addAll(operatedParticipants);
		
		for (Participant remainingParticipant : remainingParticipants){

			Collection<DineTableGroup> availableTableGroups = new ArrayList<DineTableGroup>();
			for (DineTableGroup dineTableGroup : dineTableGroups){
				if (!this.isTableFull(dineTableGroup)){
					availableTableGroups.add(dineTableGroup);
				}
			}
			
			DineTableGroup dineTable = RandomnessUtils.pickRandomDineTableGroup(availableTableGroups, this.randomObj);
			
			if (dineTable != null){
				dineTable.getParticipants().add(remainingParticipant);
				operatedParticipants.add(remainingParticipant);
			}
		}
		
		unassignedParticipants.removeAll(operatedParticipants);
		assignedParticipants.addAll(operatedParticipants);
	}
	
	private void assignSpecialGroupToTable2(
			Collection<Participant> unassignedParticipants,
			Collection<Participant> assignedParticipants,
			Collection<DineTableGroup> dineTableGroups) {
		
		if (CollectionUtils.isEmpty(unassignedParticipants) || CollectionUtils.isEmpty(dineTableGroups)){
			return;
		}
		
		Collection<Participant> unassignedSpecialParticipants = new HashSet<Participant>();
		for (Participant unassignedParticipant : unassignedParticipants){
			if (unassignedParticipant.isSpecialParticipant()){
				unassignedSpecialParticipants.add(unassignedParticipant);
			}
		}
		
		/* Create a map according to participants' Group Number*/
		Map<Integer, Collection<Participant>> groupNumberParticipantMap = new HashMap<Integer, Collection<Participant>>();
		for (Participant unassignedSpecialParticipant : unassignedSpecialParticipants){
			
			Collection<Participant> groupNumberParticipants = groupNumberParticipantMap.get(unassignedSpecialParticipant.getGroupNumber());
			if (groupNumberParticipants == null){
				groupNumberParticipants = new ArrayList<Participant>();
				groupNumberParticipants.add(unassignedSpecialParticipant);
				groupNumberParticipantMap.put(unassignedSpecialParticipant.getGroupNumber(), groupNumberParticipants);
			}else {
				groupNumberParticipants.add(unassignedSpecialParticipant);
			}
		}
		
		for (DineTableGroup dineTableGroup : dineTableGroups){
			
			int MAX_TRY = 1000;
			int count = 0;
			int remainingSpace = 0;
			do {
				remainingSpace = this.tableCapacity - dineTableGroup.getParticipants().size();
				if (remainingSpace <= 0){
					break;
				}
				
				int genderBalance = dineTableGroup.getNetGenderBalance();
				int pickNumber = determineNumberOfPick(remainingSpace, this.randomObj);
				
				Collection<Participant> selectedParticipantGroup =
						RandomnessUtils.pickRandomParticipantGroupWithGender(groupNumberParticipantMap, (genderBalance * -1), pickNumber, this.randomObj);
				
				if (CollectionUtils.isEmpty(selectedParticipantGroup)){
					selectedParticipantGroup = RandomnessUtils.pickRandomParticipantGroupWithoutGender(groupNumberParticipantMap, pickNumber, this.randomObj);
				}
				
				if (CollectionUtils.isEmpty(selectedParticipantGroup)){
					break;
				}
				
				Collection<Participant> selectedParticipants = RandomnessUtils.pickRandomMultiParticipant(selectedParticipantGroup, pickNumber, randomObj);
				dineTableGroup.getParticipants().addAll(selectedParticipants);
				unassignedParticipants.removeAll(selectedParticipants);
				unassignedSpecialParticipants.removeAll(selectedParticipants);
				assignedParticipants.addAll(selectedParticipants);
				selectedParticipantGroup.removeAll(selectedParticipants);
				
			}while (!CollectionUtils.isEmpty(unassignedSpecialParticipants) && count < MAX_TRY);
			
		}
		
		/* Assign piecemeal participant to remaining table space */
		for (DineTableGroup dineTableGroup : dineTableGroups){
			
			int MAX_TRY = 1000;
			int count = 0;
			int remainingSpace = 0;
			do {
				remainingSpace = this.tableCapacity - dineTableGroup.getParticipants().size();
				if (remainingSpace <= 0){
					break;
				}
				
				if (CollectionUtils.isEmpty(unassignedSpecialParticipants)){
					break;
				}
				
//				int genderBalance = dineTableGroup.getNetGenderBalance();
				int pickNumber = determineNumberOfPick(remainingSpace, this.randomObj);
				
				Collection<Participant> selectedParticipants =
						RandomnessUtils.pickRandomMultiParticipant(unassignedSpecialParticipants, pickNumber, randomObj);
				
				dineTableGroup.getParticipants().addAll(selectedParticipants);
				unassignedParticipants.removeAll(selectedParticipants);
				unassignedSpecialParticipants.removeAll(selectedParticipants);
				assignedParticipants.addAll(selectedParticipants);
				
			}while (!CollectionUtils.isEmpty(unassignedSpecialParticipants) && count < MAX_TRY);
			
		}
	}

	private void assignFamilyGroupToTable(
			Collection<Participant> unassignedParticipants,
			Collection<Participant> assignedParticipants,
			Collection<DineTableGroup> dineTableGroups) {
		
		if (CollectionUtils.isEmpty(unassignedParticipants) || CollectionUtils.isEmpty(dineTableGroups)){
			return;
		}
		
		Collection<Participant> operatedParticipants = new HashSet<Participant>();
		
		Map<String, FamilyGroup> familyGroupMap = new HashMap<String, FamilyGroup>();
		for (Participant participant : unassignedParticipants){
			
			if (participant.getFamilyGroup() == null){
				continue;
			}
			
			if (assignedParticipants.contains(participant)){
				continue;
			}
			
			familyGroupMap.put(participant.getFamilyGroup().getFamilyId(), participant.getFamilyGroup());
		}
		
		for (Entry<String, FamilyGroup> entry : familyGroupMap.entrySet()){
			FamilyGroup familyGroup = entry.getValue();
			
			DineTableGroup tempTableGroup = null;
			
			Collection<DineTableGroup> availableTables = new ArrayList<DineTableGroup>();
			for (DineTableGroup dineTableGroup : dineTableGroups){
				
				if (dineTableGroup.getParticipants().size() > 0){
					continue;
				}
				
				int emptySeat = this.tableCapacity - dineTableGroup.getParticipants().size();
				
				if (familyGroup.getBelieverIds().size() + 2 > emptySeat){
					continue;
				}
				
				availableTables.add(dineTableGroup);
			}
			
			if (availableTables.size() == 0){
				for (DineTableGroup dineTableGroup : dineTableGroups){
					
					int emptySeat = this.tableCapacity - dineTableGroup.getParticipants().size();
					
					if (familyGroup.getBelieverIds().size() + 2 > emptySeat){
						continue;
					}
					
					availableTables.add(dineTableGroup);
				}
			}
			
			tempTableGroup = RandomnessUtils.pickRandomDineTableGroup(availableTables, this.randomObj);
			if (tempTableGroup == null){
				tempTableGroup = RandomnessUtils.pickRandomDineTableGroup(dineTableGroups, this.randomObj);
			}
			
			for (String believerId : familyGroup.getBelieverIds()){
				Participant tempParticipant = this.participantMap.get(believerId);
				
				if (assignedParticipants.contains(tempParticipant)){
					continue;
				}
				
				tempTableGroup.getParticipants().add(tempParticipant);
				operatedParticipants.add(tempParticipant);
			}
		}
		
		unassignedParticipants.removeAll(operatedParticipants);
		assignedParticipants.addAll(operatedParticipants);
	}
	
	private void assignGroupMentorToTable(
			Collection<Participant> unassignedParticipants,
			Collection<Participant> assignedParticipants,
			Collection<DineTableGroup> dineTableGroups) {
		
		if (CollectionUtils.isEmpty(unassignedParticipants) || CollectionUtils.isEmpty(dineTableGroups)){
			return;
		}
		
		Collection<Participant> operatedParticipants = new HashSet<Participant>();
		
		List<Participant> groupMentors = new ArrayList<Participant>();
		for (Participant participant : unassignedParticipants){
			
			if (assignedParticipants.contains(participant)){
				continue;
			}
			
			if (participant.isGroupMentor() || participant.isMentor() || StringUtils.contains(participant.getSundaySchoolClass(), "導師")){
				groupMentors.add(participant);
			}
		}
		
		GroupMentorComparator groupMentorComparator = new GroupMentorComparator(this.plan.getDay());
		
		Collections.sort(groupMentors, groupMentorComparator);
		
		for (Participant groupMentor : groupMentors){
			DineTableGroup minimumMentorDineTable = this.randomlyPickMinimumGroupMentorTable(dineTableGroups);
			
			minimumMentorDineTable.getParticipants().add(groupMentor);
			operatedParticipants.add(groupMentor);
		}
		
		unassignedParticipants.removeAll(operatedParticipants);
		assignedParticipants.addAll(operatedParticipants);
	}
	
	private void assignGroupMemberWithMentor(
			Collection<Participant> unassignedParticipants,
			Collection<Participant> assignedParticipants,
			Collection<DineTableGroup> dineTableGroups) {
		
//		Map<String, Integer> campPreassignedMap = new HashMap<String, Integer>();
//		if (StringUtils.equalsIgnoreCase(this.plan.getCampName(), "A")){
//			campPreassignedMap = campAPreassignedMap;
//		} else if (StringUtils.equalsIgnoreCase(this.plan.getCampName(), "B")){
//			campPreassignedMap = campBPreassignedMap;
//		}
		
		Collection<Participant> operatedParticipants = new HashSet<Participant>();
		
		for (DineTableGroup dineTableGroup : dineTableGroups){
			
			Collection<Participant> participantsToBeAdded = new ArrayList<Participant>();
			
			for (Participant participant : dineTableGroup.getParticipants()){
//				if (campPreassignedMap.get(participant.getName()) == null){
//					continue;
//				}
				
				Participant groupMentor = participant;
				
				/* Create a map according to participants' Group Number*/
				Collection<Participant> groupMembers = new ArrayList<Participant>();
				for (Participant unassignedParticipant : unassignedParticipants){
					
					if (!assignedParticipants.contains(unassignedParticipant) &&
							groupMentor.getGroupNumber() == unassignedParticipant.getGroupNumber()){
						groupMembers.add(unassignedParticipant);
					}
				}
				
				Collection<Participant> selectedParticipants = RandomnessUtils.pickRandomMultiParticipant(groupMembers, this.randomObj);
				if (!CollectionUtils.isEmpty(selectedParticipants) &&
						dineTableGroup.getParticipants().size() + participantsToBeAdded.size() + selectedParticipants.size() <= this.tableCapacity){
					participantsToBeAdded.addAll(selectedParticipants);
				}
			}
			
			dineTableGroup.getParticipants().addAll(participantsToBeAdded);
			operatedParticipants.addAll(participantsToBeAdded);
		}
		
		unassignedParticipants.removeAll(operatedParticipants);
		assignedParticipants.addAll(operatedParticipants);
		
	}
	
	private void assignThreeSameGroupParticipantsToTables(
			Collection<Participant> participants,
			Collection<Participant> assignedParticipants,
			Collection<DineTableGroup> dineTableGroups) {
		
		if (CollectionUtils.isEmpty(participants) || CollectionUtils.isEmpty(dineTableGroups)){
			return;
		}
		
		Collection<Participant> operatedParticipants = new HashSet<Participant>();
		
		Collection<Participant> unassignedParticipants = new ArrayList<Participant>();
		for (Participant participant : participants){
			if (!assignedParticipants.contains(participant)){
				unassignedParticipants.add(participant);
			}
		}
		
		/* Create a map according to participants' Group Number*/
		Map<Integer, Collection<Participant>> groupNumberParticipantMap = new HashMap<Integer, Collection<Participant>>();
		for (Participant unassignedParticipant : unassignedParticipants){
			
			Collection<Participant> groupNumberParticipants = groupNumberParticipantMap.get(unassignedParticipant.getGroupNumber());
			if (groupNumberParticipants == null){
				groupNumberParticipants = new ArrayList<Participant>();
				groupNumberParticipants.add(unassignedParticipant);
				groupNumberParticipantMap.put(unassignedParticipant.getGroupNumber(), groupNumberParticipants);
			}else {
				groupNumberParticipants.add(unassignedParticipant);
			}
		}
		
		/*
		 * For each group of participant, randomly pick a table with enough vacancy
		 * Randomly pick 2 or 3 participants from the group of participants
		 * assign the participants to the table
		 * Add the participants to assigned participant list
		 * */
		for (Entry<Integer, Collection<Participant>> entry : groupNumberParticipantMap.entrySet()){
			int groupNumber = entry.getKey();
			Collection<Participant> groupedParticipants = entry.getValue();
			
			while (!CollectionUtils.isEmpty(groupedParticipants)){
				
				Collection<Participant> selectedParticipants = RandomnessUtils.pickRandomMultiParticipant(groupedParticipants, this.randomObj);
				if (selectedParticipants == null){
					break;
				}
				
				DineTableGroup selectedDineTable = randomPickTableForGroupParticipantAssignment(dineTableGroups, groupNumber, selectedParticipants, false);
				
				if (selectedDineTable == null){
					break;
				}
				
				selectedDineTable.getParticipants().addAll(selectedParticipants);
				operatedParticipants.addAll(selectedParticipants);
				groupedParticipants.removeAll(selectedParticipants);
			}
		}
		
		participants.removeAll(operatedParticipants);
		assignedParticipants.addAll(operatedParticipants);
	}
	
	private DineTableGroup randomPickTableForGroupParticipantAssignment(
			Collection<DineTableGroup> dineTableGroups,
			int groupNumber,
			Collection<Participant> selectedParticipants,
			boolean allowSameGroup){
		
		if (CollectionUtils.isEmpty(dineTableGroups)){
			return null;
		}
		
		int vacancyRequired = selectedParticipants.size();
		Collection<DineTableGroup> candidateTables = new ArrayList<DineTableGroup>();
		Collection<DineTableGroup> genderCompensatedTables = new ArrayList<DineTableGroup>();
		
		int genderNetBalance = 0;
		for (Participant participant : selectedParticipants){
			if (participant.getGender() == Gender.MALE){
				genderNetBalance++;
			}else {
				genderNetBalance--;
			}
		}
		
		for (DineTableGroup dineTableGroup : dineTableGroups){
			if (dineTableGroup.getParticipants().size() + vacancyRequired > this.tableCapacity){
				continue;
			}
			
			boolean hasSameGroupParticipant = false;
			for (Participant participant : dineTableGroup.getParticipants()){
				if (participant.getGroupNumber() == groupNumber){
					hasSameGroupParticipant = true;
					break;
				}
			}
			
			if (hasSameGroupParticipant && !allowSameGroup){
				continue;
			}
			
			/* Construct a table candidate list with gender balance */
			int tableGenderBalance = dineTableGroup.getNetGenderBalance();
			if ((genderNetBalance == 0 && tableGenderBalance == 0) ||
					(genderNetBalance < 0 && tableGenderBalance > 0) ||
					(genderNetBalance > 0 && tableGenderBalance < 0)){
				genderCompensatedTables.add(dineTableGroup);
			}
			
			candidateTables.add(dineTableGroup);
		}
		
		DineTableGroup selectedTable = RandomnessUtils.pickRandomDineTableGroup(genderCompensatedTables, this.randomObj);
		if (selectedTable != null){
			return selectedTable;
		}else {
			return RandomnessUtils.pickRandomDineTableGroup(candidateTables, this.randomObj);
		}
	}

	private void assignParticipantToTable(
			Collection<Participant> participants,
			Collection<Participant> assignedParticipants,
			Collection<DineTableGroup> dineTableGroups) {
		
		if (CollectionUtils.isEmpty(participants) || CollectionUtils.isEmpty(dineTableGroups)){
			return;
		}
		
		Collection<Participant> operatedParticipants = new HashSet<Participant>();
		
		Collection<Participant> unassignedParticipants = new ArrayList<Participant>();
		for (Participant participant : participants){
			if (!assignedParticipants.contains(participant)){
				unassignedParticipants.add(participant);
			}
		}
		
		for (Participant unassignedParticipant : unassignedParticipants){
			
			DineTableGroup dineTable = this.randomlyPickTableWithGenderBalance(dineTableGroups, unassignedParticipant.getGender());
			
			if (dineTable == null){
				
				Collection<DineTableGroup> availableTables = new ArrayList<DineTableGroup>();
				for (DineTableGroup dineTableGroup : dineTableGroups){
					if (!this.isTableFull(dineTableGroup)){
						availableTables.add(dineTableGroup);
					}
				}
				
				dineTable = RandomnessUtils.pickRandomDineTableGroup(availableTables, this.randomObj);
			}
			
			if (dineTable == null){
				continue;
			}
			
			dineTable.getParticipants().add(unassignedParticipant);
			operatedParticipants.add(unassignedParticipant);
		}
		
		participants.removeAll(operatedParticipants);
		assignedParticipants.addAll(operatedParticipants);
	}
	
	private void assignParticipantToTable2(
			Collection<Participant> unassignedParticipants,
			Collection<Participant> assignedParticipants,
			Collection<DineTableGroup> dineTableGroups) {
				
		if (CollectionUtils.isEmpty(unassignedParticipants) || CollectionUtils.isEmpty(dineTableGroups)){
			return;
		}
		
		Collection<Participant> unassignedNormalParticipants = new HashSet<Participant>();
		for (Participant unassignedParticipant : unassignedParticipants){
			if (unassignedParticipant.isNormalParticipant()){
				unassignedNormalParticipants.add(unassignedParticipant);
			}
		}
		
		/* Create a map according to participants' Group Number*/
		Map<Integer, Collection<Participant>> groupNumberParticipantMap = new HashMap<Integer, Collection<Participant>>();
		for (Participant unassignedNormalParticipant : unassignedNormalParticipants){
			
			Collection<Participant> groupNumberParticipants = groupNumberParticipantMap.get(unassignedNormalParticipant.getGroupNumber());
			if (groupNumberParticipants == null){
				groupNumberParticipants = new ArrayList<Participant>();
				groupNumberParticipants.add(unassignedNormalParticipant);
				groupNumberParticipantMap.put(unassignedNormalParticipant.getGroupNumber(), groupNumberParticipants);
			}else {
				groupNumberParticipants.add(unassignedNormalParticipant);
			}
		}
		
		for (DineTableGroup dineTableGroup : dineTableGroups){
			
			int MAX_TRY = 1000;
			int count = 0;
			int remainingSpace = 0;
			do {
				remainingSpace = this.tableCapacity - dineTableGroup.getParticipants().size();
				if (remainingSpace <= 0){
					break;
				}
				
				if (CollectionUtils.isEmpty(unassignedParticipants)){
					break;
				}
				
				int genderBalance = dineTableGroup.getNetGenderBalance();
				int pickNumber = determineNumberOfPick(remainingSpace, this.randomObj);
				
				Collection<Participant> selectedParticipantGroup =
						RandomnessUtils.pickRandomParticipantGroupWithGender(groupNumberParticipantMap, (genderBalance * -1), pickNumber, this.randomObj);
				
				if (CollectionUtils.isEmpty(selectedParticipantGroup)){
					selectedParticipantGroup = RandomnessUtils.pickRandomParticipantGroupWithoutGender(groupNumberParticipantMap, pickNumber, this.randomObj);
				}
				
				if (CollectionUtils.isEmpty(selectedParticipantGroup)){
					break;
				}
				
				Collection<Participant> selectedParticipants = RandomnessUtils.pickRandomMultiParticipant(selectedParticipantGroup, pickNumber, randomObj);
				selectedParticipantGroup.retainAll(unassignedParticipants);
				dineTableGroup.getParticipants().addAll(selectedParticipants);
				unassignedParticipants.removeAll(selectedParticipants);
				unassignedNormalParticipants.removeAll(selectedParticipants);
				assignedParticipants.addAll(selectedParticipants);
				selectedParticipantGroup.removeAll(selectedParticipants);
				
			}while (!CollectionUtils.isEmpty(unassignedNormalParticipants) && count < MAX_TRY);
			
		}
		
		/* Assign piecemeal participant to remaining table space */
		for (DineTableGroup dineTableGroup : dineTableGroups){
			
			int MAX_TRY = 1000;
			int count = 0;
			int remainingSpace = 0;
			do {
				remainingSpace = this.tableCapacity - dineTableGroup.getParticipants().size();
				if (remainingSpace <= 0){
					break;
				}
				
				if (CollectionUtils.isEmpty(unassignedNormalParticipants)){
					break;
				}
				
//				int genderBalance = dineTableGroup.getNetGenderBalance();
				int pickNumber = determineNumberOfPick(remainingSpace, this.randomObj);
				
				Collection<Participant> selectedParticipants =
						RandomnessUtils.pickRandomMultiParticipant(unassignedNormalParticipants, pickNumber, randomObj);
				
				dineTableGroup.getParticipants().addAll(selectedParticipants);
				unassignedParticipants.removeAll(selectedParticipants);
				unassignedNormalParticipants.removeAll(selectedParticipants);
				assignedParticipants.addAll(selectedParticipants);
				
			}while (!CollectionUtils.isEmpty(unassignedNormalParticipants) && count < MAX_TRY);
			
		}
	}

	public void doAssignment(){
		
		System.out.printf("-->Start initial assignment\n");
		long startTime = new Date().getTime();
		this.initAssignment();
		long timeStamp1 = new Date().getTime();
		System.out.printf("-->End of initial assignment [Time span: %dms]\n", (timeStamp1 - startTime));
		
		System.out.println("-->Start re-assignment");
		this.doPlanEvaluation();
		
//		for (int i=0; i<10; i++){
//			this.reAssignment();
//			System.out.printf("-->Total cost after %d Iteration: %f\n", i, this.plan.getCost());
//			if (i>10){
//				break;
//			}
//		}
		
		long endTime = new Date().getTime();
		System.out.printf("-->End of re-assignment [Time span: %dms]\n", (endTime - timeStamp1));
		System.out.printf("-->Total time span: %dms]\n", (endTime - startTime));
	}
	
	private void reAssignment(){
		if (this.plan == null || this.plan.getDineTableGroups() == null || this.plan.getDineTableGroups().size() < 2){
			return;
		}

		DineTableGroup dineTableGroup1 = RandomnessUtils.pickRandomDineTableGroup(this.plan.getDineTableGroups(), this.randomObj);
		DineTableGroup dineTableGroup2 = RandomnessUtils.pickRandomDineTableGroup(this.plan.getDineTableGroups(), this.randomObj);
		
		Participant participant1 = null;
		Participant participant2 = null;
		while (dineTableGroup1 == dineTableGroup2 || participant1 == null || participant2 == null){
			dineTableGroup1 = RandomnessUtils.pickRandomDineTableGroup(this.plan.getDineTableGroups(), this.randomObj);
			dineTableGroup2 = RandomnessUtils.pickRandomDineTableGroup(this.plan.getDineTableGroups(), this.randomObj);
			
			participant1 = RandomnessUtils.pickRandomParticipant(dineTableGroup1.getParticipants(), randomObj);
			participant2 = RandomnessUtils.pickRandomParticipant(dineTableGroup2.getParticipants(), randomObj);
		}
		
		double originCost1 = dineTableGroup1.getCost();
		double originCost2 = dineTableGroup2.getCost();
		
		this.swapTable(dineTableGroup1, dineTableGroup2, participant1, participant2);
		
		this.evaluator.evaluateTable(dineTableGroup1);
		double newCost1 = dineTableGroup1.getCost();
		this.evaluator.evaluateTable(dineTableGroup2);
		double newCost2 = dineTableGroup2.getCost();
		
		double delta = (newCost1 + newCost2) - (originCost1 + originCost2);

		if (delta < 0){
			/* The change make positive contribution to the assignment,
			 * keep the change */
			this.plan.setCost(this.plan.getCost() + delta);
		}else {
			/* The change make negative contribution to the assignment,
			 * roll back the change */
			this.swapTable(dineTableGroup1, dineTableGroup2, participant2, participant1);
			this.evaluator.evaluateTable(dineTableGroup1);
			this.evaluator.evaluateTable(dineTableGroup2);
		}
	}
	
	private void swapTable(
			DineTableGroup dineTableGroup1,
			DineTableGroup dineTableGroup2,
			Participant participant1,
			Participant participant2){
		
		dineTableGroup1.getParticipants().remove(participant1);
		dineTableGroup2.getParticipants().remove(participant2);
		
		dineTableGroup1.getParticipants().add(participant2);
		dineTableGroup2.getParticipants().add(participant1);
		
	}
	
	public void doPlanEvaluation(){
		this.evaluator.evaluatePlan(this.plan);
	}
	
	private Collection<DineTableGroup> createDineTableGroupList(Collection<Participant> unassignedParticipants, int startIndex, boolean isForSpecial){
		if (CollectionUtils.isEmpty(unassignedParticipants)){
			return new ArrayList<DineTableGroup>();
		}
		
		if (this.tableCapacity <= 0){
			System.out.println("No table capacity");
			return new ArrayList<DineTableGroup>();
		}
		
		Collection<Participant> countedParticipants = new HashSet<Participant>();
		
		for (Participant participant : unassignedParticipants){
			
			if (participant.isSpecialParticipant()){
				if (!isForSpecial){
					continue;
				}
			}else {
				if (isForSpecial){
					continue;
				}
			}
			
			if (participant.countAvailableDine(this.plan.getDay()) <= 0){
				continue;
			}
			
			countedParticipants.add(participant);
			
			/* Check if there are family members with the participant */
			FamilyGroup familyGroup = participant.getFamilyGroup();
			
			if (familyGroup != null
					&& StringUtils.isNotBlank(familyGroup.getFamilyId())
					&& !CollectionUtils.isEmpty(familyGroup.getBelieverIds())){
				
				for (String id : familyGroup.getBelieverIds()){
					Participant familyMember = this.findParticipantById(id, this.participants);
					
					if (familyMember.countAvailableDine(this.plan.getDay()) <= 0){
						continue;
					}
					
					countedParticipants.add(familyMember);
				}
			}
		}
		
		countedParticipants.retainAll(unassignedParticipants);
		this.listOutParticipants(countedParticipants);
		int totalTableNeeded = 0;
		
		if (isForSpecial){
			System.out.printf("Special Participant Count: %d\n", countedParticipants.size());
		}else {
			System.out.printf("Normal Participant Count: %d\n", countedParticipants.size());
		}
		
		
		if (isForSpecial){
			totalTableNeeded = countedParticipants.size() / this.tableCapacity;
			if ((countedParticipants.size() % this.tableCapacity) > 0){
				totalTableNeeded++;
			}
		}else {
			totalTableNeeded = unassignedParticipants.size() / this.tableCapacity;
			if ((unassignedParticipants.size() % this.tableCapacity) > 0){
				totalTableNeeded++;
			}
			totalTableNeeded -= (startIndex - 1);
		}
		
		
		Collection<DineTableGroup> emptyTableGroups = new ArrayList<DineTableGroup>();
		for (int i=0; i< totalTableNeeded; i++){
			DineTableGroup dineTableGroup = new DineTableGroup();
			dineTableGroup.setTableNumber(i + startIndex);
			emptyTableGroups.add(dineTableGroup);
		}
		
		return emptyTableGroups;
		
	}
	
	private Collection<DineTableGroup> createEmptyTableGroupList(Collection<Participant> participants){
		if (CollectionUtils.isEmpty(participants)){
			return new ArrayList<DineTableGroup>();
		}
		
		if (this.tableCapacity <= 0){
			System.out.println("No table capacity");
			return new ArrayList<DineTableGroup>();
		}
		
		int normalParticipantCount = 0;
		int specialParticipantCount = 0;
		for (Participant participant : participants){
			if (participant.getSpecialGroup() == null || participant.getSpecialGroup() == 0){
				normalParticipantCount++;
			}else {
				specialParticipantCount++;
			}
		}
		
		int totalTableNeeded = (normalParticipantCount + specialParticipantCount) / this.tableCapacity;
		if (((normalParticipantCount + specialParticipantCount) % this.tableCapacity) > 0){
			totalTableNeeded++;
		}
		
		int specialTableNeeded = specialParticipantCount / this.tableCapacity;
		if ((specialParticipantCount % this.tableCapacity) > 0){
			specialTableNeeded++;
		}
		
		int normalTableNeeded = totalTableNeeded - specialTableNeeded;
		
		Collection<DineTableGroup> emptyTableGroupList = new ArrayList<DineTableGroup>();
		for (int i=0; i< normalTableNeeded; i++){
			DineTableGroup dineTableGroup = new DineTableGroup();
			dineTableGroup.setTableNumber(i + 1);
			emptyTableGroupList.add(dineTableGroup);
		}
		
		return emptyTableGroupList;
	}
	
	private Collection<DineTableGroup> createSpecialEmptyTableGroupList(int specialTableStartingIndex){
		if (CollectionUtils.isEmpty(this.participants)){
			return new ArrayList<DineTableGroup>();
		}
		
		if (this.tableCapacity <= 0){
			System.out.println("No table capacity");
			return new ArrayList<DineTableGroup>();
		}
		
		int participantCount = 0;
		for (Participant participant : this.participants){
			if (participant.getSpecialGroup() != null && participant.getSpecialGroup() > 0){
				participantCount++;
			}
		}
		
		int numberOfTable = participantCount / this.tableCapacity;
		if ((participantCount % this.tableCapacity) > 0){
			numberOfTable++;
		}
		
		Collection<DineTableGroup> emptyTableGroupList = new ArrayList<DineTableGroup>();
		for (int i=0; i< numberOfTable; i++){
			DineTableGroup dineTableGroup = new DineTableGroup();
			dineTableGroup.setTableNumber(specialTableStartingIndex + i + 1);
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
	
	private DineTableGroup randomlyPickMinimumGroupMentorTable(Collection<DineTableGroup> dineTableGroups){
		if (CollectionUtils.isEmpty(dineTableGroups)){
			return null;
		}
		
		Integer minimum = Integer.MAX_VALUE;
		Map<Integer, Collection<DineTableGroup>> mentorNoAndDineTableGroupMap = new HashMap<Integer, Collection<DineTableGroup>>();
		
		for (DineTableGroup dineTableGroup : dineTableGroups){
			
			if (dineTableGroup.getNoOfGroupMentor() < minimum){
				minimum = dineTableGroup.getNoOfGroupMentor();
			}
			
			if (mentorNoAndDineTableGroupMap.get(dineTableGroup.getNoOfGroupMentor()) == null){
				Collection<DineTableGroup> tempDineTableGroups = new ArrayList<DineTableGroup>();
				tempDineTableGroups.add(dineTableGroup);
				mentorNoAndDineTableGroupMap.put(dineTableGroup.getNoOfGroupMentor(), tempDineTableGroups);
			}else {
				Collection<DineTableGroup> tempDineTableGroups = mentorNoAndDineTableGroupMap.get(dineTableGroup.getNoOfGroupMentor());
				tempDineTableGroups.add(dineTableGroup);
			}
		}
		
		Collection<DineTableGroup> minimumMentorDineTableGroups = mentorNoAndDineTableGroupMap.get(minimum);
		return RandomnessUtils.pickRandomDineTableGroup(minimumMentorDineTableGroups, this.randomObj);
	}
	
	private DineTableGroup randomlyPickTableWithGenderBalance(Collection<DineTableGroup> dineTableGroups, Gender gender) {
		
		/*
		 * If gender is male, try to pick a table with more female to balance the gender 
		 * If gender is female, try to pick a table with more male to balance the gender */
		
		Collection<DineTableGroup> genderBalancedTables = new ArrayList<DineTableGroup>();
		Collection<DineTableGroup> maleDominatedTables = new ArrayList<DineTableGroup>();
		Collection<DineTableGroup> femaleDominatedTables = new ArrayList<DineTableGroup>();
		
		for (DineTableGroup dineTableGroup : dineTableGroups){
			
			if (this.isTableFull(dineTableGroup)){
				continue;
			}
			
			int tableCurrentGenderBalance = dineTableGroup.getNetGenderBalance();
			
			if (tableCurrentGenderBalance == 0){
				genderBalancedTables.add(dineTableGroup);
			}else if (tableCurrentGenderBalance > 0){
				maleDominatedTables.add(dineTableGroup);
			}else if (tableCurrentGenderBalance < 0){
				femaleDominatedTables.add(dineTableGroup);
			}
			
		}
		
		if (gender == Gender.MALE){
			if (!CollectionUtils.isEmpty(femaleDominatedTables)){
				return RandomnessUtils.pickRandomDineTableGroup(femaleDominatedTables, this.randomObj);
			}else if (!CollectionUtils.isEmpty(genderBalancedTables)){
				return RandomnessUtils.pickRandomDineTableGroup(genderBalancedTables, this.randomObj);
			}else if (!CollectionUtils.isEmpty(maleDominatedTables)){
				return RandomnessUtils.pickRandomDineTableGroup(maleDominatedTables, this.randomObj);
			}
		}else {
			if (!CollectionUtils.isEmpty(maleDominatedTables)){
				return RandomnessUtils.pickRandomDineTableGroup(maleDominatedTables, this.randomObj);
			}else if (!CollectionUtils.isEmpty(genderBalancedTables)){
				return RandomnessUtils.pickRandomDineTableGroup(genderBalancedTables, this.randomObj);
			}else if (!CollectionUtils.isEmpty(femaleDominatedTables)){
				return RandomnessUtils.pickRandomDineTableGroup(femaleDominatedTables, this.randomObj);
			}
		}
		
		return null;
	}
	
	private void printCurrentAssignmentInfo(){
		System.out.printf("Current Assignment: [Camp=%s][Day=%d]\n", this.plan.getCampName(), this.plan.getDay());
	}
	
	private void checkTableOverCapacity(Collection<DineTableGroup> dineTableGroups){
		for (DineTableGroup dineTable : dineTableGroups){
			if (dineTable.getParticipants().size() > this.tableCapacity){
				System.out.printf("Table %d is over capacity.\n", dineTable.getTableNumber());
			}
		}
	}
	
	private Map<Integer, String> constructTableMentorMap() {
		
		if (CollectionUtils.isEmpty(this.plan.getDineTableGroups())){
			return null;
		}
		
		Map<Integer, String> tableMentorMap = new HashMap<Integer, String>();
		
		for (DineTableGroup dineTableGroup : this.plan.getDineTableGroups()){
			
			Participant tableMentor = null;
			
			Collection<Participant> groupMentors = new ArrayList<Participant>();
			Collection<Participant> classMentors = new ArrayList<Participant>();
			
			for (Participant participant : dineTableGroup.getParticipants()){
				if (participant.isGroupMentor()){
					groupMentors.add(participant);
					continue;
				}
				
				if (participant.isMentor() || StringUtils.contains(participant.getSundaySchoolClass(), "導師")){
					classMentors.add(participant);
					continue;
				}
			}
			
			tableMentor = getHighestAvailability(groupMentors);
			if (tableMentor == null){
				tableMentor = getHighestAvailability(classMentors);
			}
			
			if (tableMentor != null){
				tableMentorMap.put(dineTableGroup.getTableNumber(), tableMentor.getId());
			}
		}
		
		return tableMentorMap;
		
	}
	
	private Participant getHighestAvailability(Collection<Participant> participants){
		
		if (CollectionUtils.isEmpty(participants)){
			return null;
		}
		
		int highestAvailableCount = 0;
		Participant highestAvailableParticipant = null;
		
		for (Participant participant : participants){
			
			if (participant.countTotalAvailableDine() > highestAvailableCount){
				highestAvailableCount = participant.countTotalAvailableDine();
				highestAvailableParticipant = participant;
			}
		}
		
		return highestAvailableParticipant;
	}
	
	private Collection<Participant> findFamilyMembers(Participant participant){
		
		if (participant == null || participant.getFamilyGroup() == null){
			return new ArrayList<Participant>();
		}
		
		Collection<Participant> familyMembers = new ArrayList<Participant>();
		
		for (String id : participant.getFamilyGroup().getBelieverIds()){
			
			if (StringUtils.equalsIgnoreCase(participant.getId(), id)){
				continue;
			}
			
			Participant familyMember = this.participantMap.get(id);
			
			if (familyMember != null){
				familyMembers.add(familyMember);
			}
		}
		
		return familyMembers;
	}
	
	private Participant findParticipantById(String id, Collection<Participant> participants){
		if (CollectionUtils.isEmpty(participants)){
			return null;
		}
		
		for (Participant participant : participants){
			if (StringUtils.equalsIgnoreCase(id, participant.getId())){
				return participant;
			}
		}
		
		return null;
	}
	
	private Integer getPreassignedTable(String id){
		return this.campSite.getCampPreassignedMap().get(id);
	}
	
	private int determineNumberOfPick(int remainingSpace, Random randomObj) {
		if (remainingSpace == 1){
			return 1;
		}
		if (remainingSpace == 2){
			return 2;
		}
		if (remainingSpace == 3){
			return 3;
		}
		if (remainingSpace == 4){
			return 2;
		}
		if (remainingSpace >= 5){
			return RandomnessUtils.randInt(2, 3, randomObj);
		}
		return 0;
	}
	
	private Collection<Participant> getAllSpecialGroupParticipants(Collection<Participant> unassignedParticipants) {
		
		if (CollectionUtils.isEmpty(participants)){
			return new HashSet<Participant>();
		}
		
		Collection<Participant> results = new HashSet<Participant>();
		
		for (Participant participant : participants){
			if (participant.getSpecialGroup() != null && participant.getSpecialGroup().intValue() > 0){
				results.add(participant);
			}
		}
		return results;
	}
	
	private int countSpecialParticipant(Collection<Participant> participants) {
		if (CollectionUtils.isEmpty(participants)){
			return 0;
		}
		
		int count = 0;
		
		for (Participant participant : participants){
			if (participant.getSpecialGroup() != null && participant.getSpecialGroup().intValue() > 0){
				count++;
			}
		}
		
		return count;
	}
	
	private int countNonSpecialParticipant(Collection<Participant> participants) {
		if (CollectionUtils.isEmpty(participants)){
			return 0;
		}
		
		int count = 0;
		
		for (Participant participant : participants){
			if (participant.getSpecialGroup() != null && participant.getSpecialGroup().intValue() == 0){
				count++;
			}
		}
		
		return count;
	}
	
	private void printOutAssignmentStatus(
			String stage,
			Collection<Participant> unassignedParticipants,
			Collection<Participant> assignedParticipants){
		
		System.out.printf("%s : [Unassigned:%d][Unassigned Special:%d][Unassigned Normal:%d]  [Assigned:%d][assigned special:%d][assigned Normal:%d]\n",
				stage,
				unassignedParticipants.size(),
				this.countSpecialParticipant(unassignedParticipants),
				this.countNonSpecialParticipant(unassignedParticipants),
				assignedParticipants.size(),
				this.countSpecialParticipant(assignedParticipants),
				this.countNonSpecialParticipant(assignedParticipants));
	}
	
	private void listOutParticipants(Collection<Participant> participants){
		if (CollectionUtils.isEmpty(participants)){
			return;
		}
		
		List<Participant> sortedParticipants = new ArrayList<Participant>();
		sortedParticipants.addAll(participants);
		Collections.sort(sortedParticipants, new ParticipantIdComparator());
		
		for (Participant participant : sortedParticipants){
			System.out.printf("[ID:%s][Name:%s][GroupNumber:%d]\n", participant.getId(), participant.getName(), participant.getGroupNumber());
		}
	}
}
