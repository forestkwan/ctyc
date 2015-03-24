package org.ctyc.mgt.summercamp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.ctyc.mgt.model.Believer;
import org.ctyc.mgt.model.FamilyGroup;
import org.ctyc.mgt.model.Gender;
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
				
		Collection<Participant> assignedParticipants = new HashSet<Participant>();		
		Collection<DineTableGroup> dineTableGroups = this.createEmptyTableGroupList();
		
		assignFamilyGroupToTable(participants, assignedParticipants, dineTableGroups);
		assignGroupMentorToTable(participants, assignedParticipants, dineTableGroups);
		assignThreeSundayClassmatesToTables(participants, assignedParticipants, dineTableGroups);
		assignParticipantToTable(participants, assignedParticipants, dineTableGroups);
		
		this.plan.getDineTableGroups().addAll(dineTableGroups);
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
				
				if (familyGroup.getBelieverIds().size() > emptySeat){
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
		
		Collection<Participant> groupMentors = new ArrayList<Participant>();
		for (Participant participant : participants){
			if (participant.isGroupMentor() && !assignedParticipants.contains(participant)){
				groupMentors.add(participant);
			}
		}
		
		for (Participant groupMentor : groupMentors){
			DineTableGroup minimumMentorDineTable = this.randomlyPickMinimumGroupMentorTable(dineTableGroups);
			minimumMentorDineTable.getParticipants().add(groupMentor);
			assignedParticipants.add(groupMentor);
		}
		
	}
	
	private void assignThreeSundayClassmatesToTables(
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
		
		/* Create a map according to participants' Sunday class*/
		Map<String, Collection<Participant>> sundayClassParticipantMap = new HashMap<String, Collection<Participant>>();
		for (Participant unassignedParticipant : unassignedParticipants){
			
			Collection<Participant> sundayClassParticipants = sundayClassParticipantMap.get(unassignedParticipant.getSundaySchoolClass());
			if (sundayClassParticipants == null){
				sundayClassParticipants = new ArrayList<Participant>();
				sundayClassParticipants.add(unassignedParticipant);
				sundayClassParticipantMap.put(unassignedParticipant.getSundaySchoolClass(), sundayClassParticipants);
			}else {
				sundayClassParticipants.add(unassignedParticipant);
			}
		}
		
		Collection<DineTableGroup> oddTableGroups = new ArrayList<DineTableGroup>();
		for (DineTableGroup dineTableGroup : dineTableGroups){
			if (this.isEvenNumber(this.tableCapacity - dineTableGroup.getParticipants().size())){
				continue;
			}
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
		
		for (int i=0; i<10000; i++){
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
}
