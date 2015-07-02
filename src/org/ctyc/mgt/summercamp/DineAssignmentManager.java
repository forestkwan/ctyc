package org.ctyc.mgt.summercamp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.ctyc.mgt.model.FamilyGroup;
import org.ctyc.mgt.model.Gender;
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
	private Collection<Participant> participants;
	private Map<String, Participant> participantMap;
	private int tableCapacity;
	private static Map<String, Integer> mentorTableMap;
	
	// Private calculation object
	private Random randomObj;
	
	public DineAssignmentManager(
			String campSiteName,
			int day,
			Collection<Participant> participants,
			int tableCapacity,
			Collection<AbstractCostFunction> costFunctions,
			Collection<AbstractCostFunction> constraintFunctions){
		
		this.participants = participants;
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
		mentorTableMap = new HashMap<String, Integer>();
	}
	
	public DineAssignmentManager(
			String campSiteName,
			int day,
			Collection<Participant> participants,
			int tableCapacity,
			Collection<AbstractCostFunction> costFunctions,
			Collection<AbstractCostFunction> constraintFunctions,
			int seed){
		
		this(campSiteName, day, participants, tableCapacity, costFunctions, constraintFunctions);
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
		
		Collection<Participant> filteredParticipants = filterLeftParticipants(this.participants);
		
		Collection<Participant> assignedParticipants = new HashSet<Participant>();		
		Collection<DineTableGroup> dineTableGroups = this.createEmptyTableGroupList();
		
		int specialTableStartingIndex = dineTableGroups.size();
		Collection<DineTableGroup> specialDineTableGroups = this.createSpecialEmptyTableGroupList(specialTableStartingIndex);
		
		assignPreassignedAssignment(filteredParticipants, assignedParticipants, dineTableGroups);
		checkTableOverCapacity(dineTableGroups);
		assignMentorToSpecialGroupTable(filteredParticipants, assignedParticipants, specialDineTableGroups);
		checkTableOverCapacity(dineTableGroups);
		assignSpecialGroupToTable(filteredParticipants, assignedParticipants, specialDineTableGroups);
		checkTableOverCapacity(dineTableGroups);
		assignFamilyGroupToTable(filteredParticipants, assignedParticipants, dineTableGroups);
		checkTableOverCapacity(dineTableGroups);
		assignGroupMentorToTable(filteredParticipants, assignedParticipants, dineTableGroups);
		checkTableOverCapacity(dineTableGroups);
		assignThreeSameGroupParticipantsToTables(filteredParticipants, assignedParticipants, dineTableGroups);
		checkTableOverCapacity(dineTableGroups);
		assignParticipantToTable(filteredParticipants, assignedParticipants, dineTableGroups);
		checkTableOverCapacity(dineTableGroups);
		
		this.plan.getDineTableGroups().addAll(dineTableGroups);
		this.plan.getDineTableGroups().addAll(specialDineTableGroups);
	}
	
	private Collection<Participant> filterLeftParticipants(Collection<Participant> participants) {
		
		int day = this.getAssignmentPlan().getDay();
		
		Collection<Participant> filteredParticipants = new ArrayList<Participant>();
		
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

	private void assignPreassignedAssignment(
			Collection<Participant> participants,
			Collection<Participant> assignedParticipants,
			Collection<DineTableGroup> dineTableGroups) {
		
		// Assign the mentor to pre-assigned table
		for (Participant participant : participants){
			
			//Assign Dr.Wong to table 1
			if (StringUtils.equalsIgnoreCase(participant.getName(), "黃耀銓")){
				
				for (DineTableGroup dineTableGroup : dineTableGroups){
					if (dineTableGroup.getTableNumber() == 1){
						dineTableGroup.getParticipants().add(participant);
						assignedParticipants.add(participant);
					}
				}
				
				continue;
			}
			
			//Assign Cheung Wan Sang to table 2
			if (StringUtils.equalsIgnoreCase(participant.getName(), "張運生")){
				
				for (DineTableGroup dineTableGroup : dineTableGroups){
					if (dineTableGroup.getTableNumber() == 2){
						dineTableGroup.getParticipants().add(participant);
						assignedParticipants.add(participant);
					}
				}
				
				continue;
			}
			
			Integer tableNumber = mentorTableMap.get(participant.getId());
			if (tableNumber == null){
				continue;
			}
			
			for (DineTableGroup dineTableGroup : dineTableGroups){
				if (dineTableGroup.getTableNumber() == tableNumber.intValue()){
					dineTableGroup.getParticipants().add(participant);
					assignedParticipants.add(participant);
				}
			}
		}
	}
	
	private void assignMentorToSpecialGroupTable(
			Collection<Participant> participants,
			Collection<Participant> assignedParticipants,
			Collection<DineTableGroup> dineTableGroups) {
		
		if (CollectionUtils.isEmpty(participants) || CollectionUtils.isEmpty(dineTableGroups)){
			return;
		}
		
		Collection<Participant> specialGroupMentors = new ArrayList<Participant>();
		for (Participant participant : participants){
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
			
			if (dineTable.getNoOfGroupMentor() < 1){
				mentorTableMap.put(mentor.getId(), dineTable.getTableNumber());
			}
			
			dineTable.getParticipants().add(mentor);
			assignedParticipants.add(mentor);
		}
		
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
		for (Participant participant : participants){
			
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
				
				if (dineTableGroup.getNoOfGroupMentor() < 1){
					mentorTableMap.put(mentor.getId(), dineTableGroup.getTableNumber());
				}
				
				dineTableGroup.getParticipants().add(mentor);
				assignedParticipants.add(mentor);
				normalMentors.remove(mentor);
			}
		}
		
	}

	private void assignSpecialGroupToTable(
			Collection<Participant> participants,
			Collection<Participant> assignedParticipants,
			Collection<DineTableGroup> dineTableGroups) {
		
		if (CollectionUtils.isEmpty(participants) || CollectionUtils.isEmpty(dineTableGroups)){
			return;
		}
		
		Collection<Participant> specialParticipants = new ArrayList<Participant>();
		for (Participant participant : participants){
			
			if (assignedParticipants.contains(participant)){
				continue;
			}
			
			if (participant.getSpecialGroup() != null && participant.getSpecialGroup() > 0 && !assignedParticipants.contains(participant)){
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
		for (Entry<Integer, Collection<Participant>> entry : groupNumberParticipantMap.entrySet()){
			int groupNumber = entry.getKey();
			Collection<Participant> groupedParticipants = entry.getValue();
			
			while (!CollectionUtils.isEmpty(groupedParticipants)){
				
				Collection<Participant> selectedParticipants = RandomnessUtils.pickRandomMultiParticipant(groupedParticipants, this.randomObj);
				if (selectedParticipants == null){
					break;
				}
				
				DineTableGroup selectedDineTable = randomPickTableForGroupPanticipantAssignment(dineTableGroups, groupNumber, selectedParticipants);
				
				if (selectedDineTable == null){
					break;
				}
				
				selectedDineTable.getParticipants().addAll(selectedParticipants);
				assignedParticipants.addAll(selectedParticipants);
				groupedParticipants.removeAll(selectedParticipants);
			}
		}
	}

	private void assignFamilyGroupToTable(
			Collection<Participant> participants,
			Collection<Participant> assignedParticipants,
			Collection<DineTableGroup> dineTableGroups) {
		
		if (CollectionUtils.isEmpty(participants) || CollectionUtils.isEmpty(dineTableGroups)){
			return;
		}
		
		Map<String, FamilyGroup> familyGroupMap = new HashMap<String, FamilyGroup>();
		for (Participant participant : participants){
			
			if (participant.getFamilyGroup() == null){
				continue;
			}
			
			familyGroupMap.put(participant.getFamilyGroup().getFamilyId(), participant.getFamilyGroup());
		}
		
		for (Entry<String, FamilyGroup> entry : familyGroupMap.entrySet()){
			FamilyGroup familyGroup = entry.getValue();
			
			boolean hasEnoughCapacity = false;
			DineTableGroup tempTableGroup = null;
			
			int noOfIteration = 0;
			while (hasEnoughCapacity == false){
				
				noOfIteration++;
				
				tempTableGroup = RandomnessUtils.pickRandomDineTableGroup(dineTableGroups, this.randomObj);
				
				/* Try not to assign the family group to a table with another family group 
				 * Unless there is a significant try */
				if (tempTableGroup.getParticipants().size() > 0 && noOfIteration < 100){
					continue;
				}
				
				int emptySeat = this.tableCapacity - tempTableGroup.getParticipants().size();
				
				if (familyGroup.getBelieverIds().size() + 2 > emptySeat){
					continue;
				}
				
				hasEnoughCapacity = true;
			}
			
			for (String believerId : familyGroup.getBelieverIds()){
				Participant tempParticipant = this.participantMap.get(believerId);
				tempTableGroup.getParticipants().add(tempParticipant);
				assignedParticipants.add(tempParticipant);
			}
		}		
	}
	
	private void assignGroupMentorToTable(
			Collection<Participant> participants,
			Collection<Participant> assignedParticipants,
			Collection<DineTableGroup> dineTableGroups) {
		
		if (CollectionUtils.isEmpty(participants) || CollectionUtils.isEmpty(dineTableGroups)){
			return;
		}
		
		List<Participant> groupMentors = new ArrayList<Participant>();
		for (Participant participant : participants){
			
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
			
			if (minimumMentorDineTable != null && minimumMentorDineTable.getNoOfGroupMentor() < 1){
				mentorTableMap.put(groupMentor.getId(), minimumMentorDineTable.getTableNumber());
			}
			
			minimumMentorDineTable.getParticipants().add(groupMentor);
			assignedParticipants.add(groupMentor);
		}
		
	}
	
	private void assignThreeSameGroupParticipantsToTables(
			Collection<Participant> participants,
			Collection<Participant> assignedParticipants,
			Collection<DineTableGroup> dineTableGroups) {
		
		if (CollectionUtils.isEmpty(participants) || CollectionUtils.isEmpty(dineTableGroups)){
			return;
		}
		
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
				
				DineTableGroup selectedDineTable = randomPickTableForGroupPanticipantAssignment(dineTableGroups, groupNumber, selectedParticipants);
				
				if (selectedDineTable == null){
					break;
				}
				
				selectedDineTable.getParticipants().addAll(selectedParticipants);
				assignedParticipants.addAll(selectedParticipants);
				groupedParticipants.removeAll(selectedParticipants);
			}
		}
	}
	
	private DineTableGroup randomPickTableForGroupPanticipantAssignment(
			Collection<DineTableGroup> dineTableGroups,
			int groupNumber,
			Collection<Participant> selectedParticipants){
		
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
			
			if (hasSameGroupParticipant){
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
		
		Collection<Participant> unassignedParticipants = new ArrayList<Participant>();
		for (Participant participant : participants){
			if (!assignedParticipants.contains(participant)){
				unassignedParticipants.add(participant);
			}
		}
		
		for (Participant unassignedParticipant : unassignedParticipants){
			DineTableGroup dineTable = this.randomlyPickTableWithGenderBalance(dineTableGroups, unassignedParticipant.getGender());
			if (dineTable == null){
				dineTable = RandomnessUtils.pickRandomDineTableGroup(dineTableGroups, this.randomObj);
			}
			dineTable.getParticipants().add(unassignedParticipant);
			assignedParticipants.add(unassignedParticipant);
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
		
		for (int i=0; i<0; i++){
			this.reAssignment();
			System.out.printf("-->Total cost after %d Iteration: %f\n", i, this.plan.getCost());
		}
		
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
	
	private Collection<DineTableGroup> createEmptyTableGroupList(){
		if (CollectionUtils.isEmpty(this.participants)){
			return new ArrayList<DineTableGroup>();
		}
		
		if (this.tableCapacity <= 0){
			System.out.println("No table capacity");
			return new ArrayList<DineTableGroup>();
		}
		
		int participantCount = 0;
		for (Participant participant : this.participants){
			if (participant.getSpecialGroup() == null || participant.getSpecialGroup() == 0){
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
		
		boolean isAllTableFull = true;
		for (DineTableGroup dineTableGroup : dineTableGroups){
			
			if (this.isTableFull(dineTableGroup)){
				continue;
			}
			
			isAllTableFull = false;
			
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
	
	private boolean isOddNumber(int number){
		
		if( (number%2) == 0){
			return false;
		}
		
		return true;
	}
	
	private boolean isEvenNumber(int number){
		
		return !this.isOddNumber(number);
	}
	
	private void searchParticipantById(Collection<Participant> participants, String id, String type){
		for (Participant participant : participants){
			if (StringUtils.equalsIgnoreCase(participant.getId(), id)){
				System.out.printf("%s Participant ID %s-%s is Found.\n", type, id, participant.getName());
				return;
			}
		}
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
}
