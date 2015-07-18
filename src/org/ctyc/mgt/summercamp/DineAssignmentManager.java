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
import org.apache.commons.lang3.SystemUtils;
import org.ctyc.mgt.model.FamilyGroup;
import org.ctyc.mgt.model.Gender;
import org.ctyc.mgt.model.summercamp.DineAvailability;
import org.ctyc.mgt.model.summercamp.DineTableGroup;
import org.ctyc.mgt.model.summercamp.DineTimeSlot;
import org.ctyc.mgt.model.summercamp.Participant;
import org.ctyc.mgt.summercamp.costfunction.AbstractCostFunction;
import org.ctyc.mgt.utils.FileUtils;
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
	private static Map<String, Map<Integer, String>> campTableMentorMap;
	private static Map<String, Integer> campAPreassignedMap;
	private static Map<String, Integer> campBPreassignedMap;
	private static String MENTOR_TABLE_PATH = "CTYCSave/MentorTableMap.txt";
	private static String SAVE_HOME;
	
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
		SAVE_HOME = System.getenv("SAVE_HOME");
		
		if (SystemUtils.IS_OS_WINDOWS){
			
			if (SAVE_HOME == null){
				SAVE_HOME = "c:\\CTYCSave";
			}
			
			MENTOR_TABLE_PATH = SAVE_HOME + "\\MentorTableMap.txt";
			
		}else if (SystemUtils.IS_OS_MAC){
			
			if (SAVE_HOME == null){
				SAVE_HOME = "CTYCSave";
			}
			
			MENTOR_TABLE_PATH = SAVE_HOME + "/MentorTableMap.txt";
		}
		
//		campTableMentorMap = FileUtils.readFileToObject(MENTOR_TABLE_PATH);
		
		campAPreassignedMap = new HashMap<String, Integer>();
		campAPreassignedMap.put("黃耀銓", 1);
		campAPreassignedMap.put("張運生", 2);
		campAPreassignedMap.put("梁婉心", 3);
		campAPreassignedMap.put("馬楊玲慶", 3);
		campAPreassignedMap.put("譚明輝", 4);
		campAPreassignedMap.put("陳錦雄", 4);
		campAPreassignedMap.put("朱惠慈", 5);
		campAPreassignedMap.put("朱建雄", 5);
		campAPreassignedMap.put("黃文傑", 6);
		campAPreassignedMap.put("蔡劉慧賢", 6);
		campAPreassignedMap.put("盧偉傑", 7);
		campAPreassignedMap.put("馬錦雄", 7);
		campAPreassignedMap.put("鄭陳美儀", 8);
		campAPreassignedMap.put("關文健", 8);
		campAPreassignedMap.put("江壽如", 9);
		campAPreassignedMap.put("蘇麥敏慧", 9);
		campAPreassignedMap.put("黃黃惠芬", 10);
		campAPreassignedMap.put("顧李小娟", 11);
		campAPreassignedMap.put("何碧翠", 11);
		campAPreassignedMap.put("莊伍愛萍", 11);
		campAPreassignedMap.put("梁志勤", 12);
		campAPreassignedMap.put("鄭俊威", 12);
		campAPreassignedMap.put("顧德華", 13);
		campAPreassignedMap.put("譚陳麗華", 14);
		campAPreassignedMap.put("梁陳長儀", 15);
		campAPreassignedMap.put("黃徐曉恩", 15);
		campAPreassignedMap.put("余愛萍", 16);
		campAPreassignedMap.put("周家航", 16);
		campAPreassignedMap.put("陳佩儀", 17);
		campAPreassignedMap.put("徐葉偉雲", 17);
		campAPreassignedMap.put("曾陳芳苗", 18);
		campAPreassignedMap.put("譚家豪", 18);
		campAPreassignedMap.put("蔡曾桂芳", 19);
		campAPreassignedMap.put("袁黃倩兒", 20);
		campAPreassignedMap.put("袁陳玉玲", 21);
		campAPreassignedMap.put("莊李玉芬", 22);
		campAPreassignedMap.put("何偉明", 23);
		campAPreassignedMap.put("洪秉賢", 24);
		campAPreassignedMap.put("胡曄敏", 25);
		campAPreassignedMap.put("鄭文玉", 26);
		campAPreassignedMap.put("陳小東", 27);
		campAPreassignedMap.put("李卓聲", 28);
		campAPreassignedMap.put("駱倩鳴", 29);
		
		campBPreassignedMap = new HashMap<String, Integer>();
		campBPreassignedMap.put("黃耀銓", 1);
		campBPreassignedMap.put("張運生", 2);
		campBPreassignedMap.put("梁婉心", 3);
		campBPreassignedMap.put("黃偉強", 4);
		campBPreassignedMap.put("羅敏儀", 5);
		campBPreassignedMap.put("袁黎艷萍", 5);
		campBPreassignedMap.put("伍詠慈", 6);
		campBPreassignedMap.put("溫家軒", 7);
		campBPreassignedMap.put("黃傅琳娜", 8);
		campBPreassignedMap.put("黃黃惠芬", 9);
		campBPreassignedMap.put("陳子敏", 10);
		campBPreassignedMap.put("黃陳小妹", 10);
		campBPreassignedMap.put("李偉明", 11);
		campBPreassignedMap.put("謝志樂", 12);
		campBPreassignedMap.put("洪穎芝", 13);
		campBPreassignedMap.put("袁慧琴", 14);
		campBPreassignedMap.put("黃陳芳婷", 15);
		campBPreassignedMap.put("謝關小玲", 15);
		campBPreassignedMap.put("文家銘", 16);
		campBPreassignedMap.put("徐向忠", 17);
		campBPreassignedMap.put("彭蘇貴英", 18);
		campBPreassignedMap.put("陳光宗", 19);
		campBPreassignedMap.put("甄碩翔", 20);
		campBPreassignedMap.put("林振成", 21);
		campBPreassignedMap.put("林志偉", 22);
		campBPreassignedMap.put("李龍波", 23);
		campBPreassignedMap.put("張智堯", 24);
		campBPreassignedMap.put("鄭黃妙裕", 25);
		campBPreassignedMap.put("李翠婷", 26);
		campBPreassignedMap.put("李錦嬋", 27);

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
		Collection<DineTableGroup> dineTableGroups = this.createEmptyTableGroupList(filteredParticipants);
		
		int specialTableStartingIndex = dineTableGroups.size();
		Collection<DineTableGroup> specialDineTableGroups = this.createSpecialEmptyTableGroupList(specialTableStartingIndex);
		
		assignPreassignedAssignment(filteredParticipants, assignedParticipants, dineTableGroups);
		assignPreassignedAssignment(filteredParticipants, assignedParticipants, specialDineTableGroups);
		
//		assignTableMentor(filteredParticipants, assignedParticipants, dineTableGroups);
//		assignTableMentor(filteredParticipants, assignedParticipants, specialDineTableGroups);
		
		assignFamilyGroupToSpecialGroupTable(filteredParticipants, assignedParticipants, specialDineTableGroups);
		assignMentorToSpecialGroupTable(filteredParticipants, assignedParticipants, specialDineTableGroups);
		assignSpecialGroupToTable(filteredParticipants, assignedParticipants, specialDineTableGroups);
		assignFamilyGroupToTable(filteredParticipants, assignedParticipants, dineTableGroups);
		assignGroupMentorToTable(filteredParticipants, assignedParticipants, dineTableGroups);
		assignThreeSameGroupParticipantsToTables(filteredParticipants, assignedParticipants, dineTableGroups);
		assignParticipantToTable(filteredParticipants, assignedParticipants, dineTableGroups);
		assignParticipantToTable(filteredParticipants, assignedParticipants, specialDineTableGroups);
		
		this.plan.getDineTableGroups().addAll(dineTableGroups);
		this.plan.getDineTableGroups().addAll(specialDineTableGroups);
		
//		if (campTableMentorMap == null){
//			campTableMentorMap = new HashMap<String, Map<Integer, String>>();
//		}
//		
//		if (campTableMentorMap.get(this.plan.getCampName()) == null){
//			Map<Integer, String> tableMentorMap = constructTableMentorMap();
//			campTableMentorMap.put(this.plan.getCampName(), tableMentorMap);
//			
//			FileUtils.writeObjectToFile(campTableMentorMap, MENTOR_TABLE_PATH);
//		}
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
			
			if (assignedParticipants.contains(participant)){
				continue;
			}
			
			//Assign Dr.Wong to table 1
			if (this.getPreassignedTable(participant.getName()) != null){
				
				int preAssignedTable = this.getPreassignedTable(participant.getName()).intValue();
				
				for (DineTableGroup dineTableGroup : dineTableGroups){
					
					if (dineTableGroup.getTableNumber() == preAssignedTable){
						
						dineTableGroup.getParticipants().add(participant);
						assignedParticipants.add(participant);
						
						Collection<Participant> familyMembers = findFamilyMembers(participant);
						if (!CollectionUtils.isEmpty(familyMembers)){
							dineTableGroup.getParticipants().addAll(familyMembers);
							assignedParticipants.addAll(familyMembers);
						}
						
						break;
					}
				}
				
			}
		}
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
				assignedParticipants.add(tempParticipant);
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
				
				DineTableGroup selectedDineTable = randomPickTableForGroupParticipantAssignment(dineTableGroups, groupNumber, selectedParticipants);
				
				if (selectedDineTable == null){
					remainingParticipants.addAll(selectedParticipants);
					break;
				}
				
				selectedDineTable.getParticipants().addAll(selectedParticipants);
				assignedParticipants.addAll(selectedParticipants);
				groupedParticipants.removeAll(selectedParticipants);
			}
		}
		
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
				assignedParticipants.add(remainingParticipant);
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
				
				DineTableGroup selectedDineTable = randomPickTableForGroupParticipantAssignment(dineTableGroups, groupNumber, selectedParticipants);
				
				if (selectedDineTable == null){
					break;
				}
				
				selectedDineTable.getParticipants().addAll(selectedParticipants);
				assignedParticipants.addAll(selectedParticipants);
				groupedParticipants.removeAll(selectedParticipants);
			}
		}
	}
	
	private DineTableGroup randomPickTableForGroupParticipantAssignment(
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
	
	private Integer getPreassignedTable(String name){
		if (StringUtils.isEmpty(this.plan.getCampName())){
			return null;
		}
		
		Map<String, Integer> preassignedMap = new HashMap<String, Integer>();
		if (StringUtils.equalsIgnoreCase("A", this.plan.getCampName())){
			preassignedMap = campAPreassignedMap;
		}else if (StringUtils.equalsIgnoreCase("B", this.plan.getCampName())){
			preassignedMap = campBPreassignedMap;
		}
		
		return preassignedMap.get(name);
	}
}
