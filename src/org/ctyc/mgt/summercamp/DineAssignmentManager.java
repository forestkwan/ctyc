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
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.ctyc.mgt.model.FamilyGroup;
import org.ctyc.mgt.model.Gender;
import org.ctyc.mgt.model.Pair;
import org.ctyc.mgt.model.summercamp.CampName;
import org.ctyc.mgt.model.summercamp.CampSite;
import org.ctyc.mgt.model.summercamp.DineAvailability;
import org.ctyc.mgt.model.summercamp.DineTableGroup;
import org.ctyc.mgt.model.summercamp.DineTimeSlot;
import org.ctyc.mgt.model.summercamp.Participant;
import org.ctyc.mgt.model.summercamp.TableAndParticipant;
import org.ctyc.mgt.summercamp.costfunction.AbstractCostFunction;
import org.ctyc.mgt.utils.RandomnessUtils;
import org.springframework.util.CollectionUtils;

public class DineAssignmentManager {
	
	// Static constant
	private static Collection<DineTimeSlot> ALL_DINE_TIME_SLOT = createAllDineTimeSlot();
	private final static int FUNE_TUNE_TRIAL = 100;
	// Dine Assignment Object
	private DineAssignmentPlan plan;
	private DineAssignmentEvaluator evaluator;
	
	// Input Object
	private CampSite campSite;
	private Collection<Participant> participants;
	private Map<String, Participant> participantMap;
	private Map<CampName, Integer> campTableCapacityMap;
	private static String SAVE_HOME;
	
	
	// Private calculation object
	private Random randomObj;
	
	public DineAssignmentManager(
			String campSiteName,
			int day,
			CampSite campSite,
			Collection<AbstractCostFunction> costFunctions,
			Collection<AbstractCostFunction> constraintFunctions){
		
		this.campSite = campSite;
		this.participants = campSite.getParticipants();
		this.plan = new DineAssignmentPlan(campSiteName, day);
		this.randomObj = new Random();
		
		this.participantMap = new HashMap<String, Participant>();
		for(Participant participant : this.participants){
			this.participantMap.put(participant.getId(), participant);
		}
		
		this.campTableCapacityMap = new HashMap<CampName, Integer>();
		this.campTableCapacityMap.put(CampName.METHODIST, 11);
		this.campTableCapacityMap.put(CampName.RECREATION, 8);
		
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
			Collection<AbstractCostFunction> costFunctions,
			Collection<AbstractCostFunction> constraintFunctions,
			int seed){
		
		this(campSiteName, day, campSite, costFunctions, constraintFunctions);
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
		
		Collection<Participant> methodistParticipants = unassignedParticipants.stream()
				.filter(participant -> participant.getAccommodationCamp().equals(CampName.METHODIST))
				.collect(Collectors.toList());
		
		Collection<Participant> recreationParticipants = unassignedParticipants.stream()
				.filter(participant -> participant.getAccommodationCamp().equals(CampName.RECREATION))
				.collect(Collectors.toList());

		/* Create all dine tables */
		List<DineTableGroup> methodistDineTableGroups = this.assignParticipantToDineTableGroup(methodistParticipants, CampName.METHODIST);
		List<DineTableGroup> recreationDineTableGroups = this.assignParticipantToDineTableGroup(recreationParticipants, CampName.RECREATION);
		
		this.plan.getDineTableGroups().addAll(methodistDineTableGroups);
		this.plan.getDineTableGroups().addAll(recreationDineTableGroups);
	}
	
	private List<DineTableGroup> assignParticipantToDineTableGroup(
			Collection<Participant> unassignedParticipants,
			CampName campName){
		
		int tableCapacity = this.campTableCapacityMap.get(campName);
		
		List<DineTableGroup> allDineTableGroups = new ArrayList<DineTableGroup>();
		int totalTableNeeded = unassignedParticipants.size() / tableCapacity;
		if ((unassignedParticipants.size() % tableCapacity) > 0){
			totalTableNeeded++;
		}
		
		for (int i=0; i< totalTableNeeded; i++){
			DineTableGroup dineTableGroup = new DineTableGroup();
			dineTableGroup.setTableNumber(i + 1);
			dineTableGroup.setCampName(campName);
			allDineTableGroups.add(dineTableGroup);
		}
		/* End of create all dine tables */
		
		/*
		 * Sequence of Assignment
		 * 1. Assign the preassigned participants and their family
		 * 2. Assign the Family Group to special table
		 * 3. Assign the special group participants to special table
		 * 4. Assign the Family Group to normal table
		 * 5. Assign the normal participant to special table to fill up the space
		 * 6. Assign the normal participant to normal table
		 * 7. Assign the remaining participant (if any) to any table with space */
		
		listOutParticipants(unassignedParticipants);
		
		Collection<Participant> assignedParticipants = new HashSet<Participant>();
		printOutAssignmentStatus("Before preassigning special group", unassignedParticipants, assignedParticipants);
		assignPreassignedAssignmentAndFamily(unassignedParticipants, assignedParticipants, allDineTableGroups);
		printOutAssignmentStatus("After preassigning special group", unassignedParticipants, assignedParticipants);
		
		Collection<DineTableGroup> specialDineTableGroups = this.getSpecialDineTables(allDineTableGroups, unassignedParticipants, tableCapacity);
		
		printOutAssignmentStatus("Before assigning special group family", unassignedParticipants, assignedParticipants);
		assignFamilyGroupToSpecialGroupTable(unassignedParticipants, assignedParticipants, specialDineTableGroups, tableCapacity);
		printOutAssignmentStatus("After assigning special group family", unassignedParticipants, assignedParticipants);
		
		printOutAssignmentStatus("Before assigning special group participants", unassignedParticipants, assignedParticipants);
		assignSpecialGroupToTable(unassignedParticipants, assignedParticipants, specialDineTableGroups, tableCapacity);
		printOutAssignmentStatus("After assigning special group participants", unassignedParticipants, assignedParticipants);
		
		Collection<DineTableGroup> normalDineTableGroups = allDineTableGroups.subList(0, allDineTableGroups.size() - specialDineTableGroups.size());
		
		printOutAssignmentStatus("Before assigning normal group family", unassignedParticipants, assignedParticipants);
		assignFamilyGroupToTable(unassignedParticipants, assignedParticipants, normalDineTableGroups, tableCapacity);
		printOutAssignmentStatus("After assigning normal group family", unassignedParticipants, assignedParticipants);
		
		printOutAssignmentStatus("Before assigning special group participants", unassignedParticipants, assignedParticipants);
		assignParticipantToTable(unassignedParticipants, assignedParticipants, specialDineTableGroups, tableCapacity);
		printOutAssignmentStatus("After assigning special group participants", unassignedParticipants, assignedParticipants);
		
		printOutAssignmentStatus("Before assigning special group participants", unassignedParticipants, assignedParticipants);
		assignParticipantToTable(unassignedParticipants, assignedParticipants, normalDineTableGroups, tableCapacity);
		printOutAssignmentStatus("After assigning special group participants", unassignedParticipants, assignedParticipants);
		
		/* Assign unassigned Participants to any table with empty seat regardless of their type */
		printOutAssignmentStatus("Before assigning special group participants", unassignedParticipants, assignedParticipants);
		assignParticipantToTable(unassignedParticipants, assignedParticipants, allDineTableGroups, tableCapacity);
		printOutAssignmentStatus("After assigning special group participants", unassignedParticipants, assignedParticipants);
		
		listOutParticipants(assignedParticipants);
		
		return allDineTableGroups;
	}
	
	private void fineTuneInitialAssignment() {
		
		for (int i = 0 ; i < FUNE_TUNE_TRIAL; i++){
//			this.fineTuneTableParticipantNumber();
		}
		
		this.fineTuneGenderBalance();
	}
	
	private void fineTuneGenderBalance(){
		
		List<DineTableGroup> genderDominatedTables = new ArrayList<DineTableGroup>();
		List<DineTableGroup> genderBalancedTables = new ArrayList<DineTableGroup>();
		
		for (DineTableGroup dineTableGroup : this.plan.getDineTableGroups()){
			if (Math.abs(dineTableGroup.getNetGenderBalance()) > 0){
				genderDominatedTables.add(dineTableGroup);
				continue;
			}
			
			genderBalancedTables.add(dineTableGroup);
		}
		
		Collections.sort(genderDominatedTables, new DineTableGenderBalanceComparator());
		
		for (int i = 0 ; i < FUNE_TUNE_TRIAL ; i++){
			
			DineTableGroup dominatedTable = genderDominatedTables.iterator().next();
			
			this.swapAndBalanceGender(dominatedTable);
			
			Collections.sort(genderDominatedTables, new DineTableGenderBalanceComparator());
		}
		
	}

	private void swapAndBalanceGender(DineTableGroup domainatedTable) {
		
		if (domainatedTable == null || CollectionUtils.isEmpty(domainatedTable.getParticipants())){
			return;
		}
		
		int domainatedNetGender = domainatedTable.getNetGenderBalance();
		
		if (domainatedNetGender == 0){
			return;
		}
		
		Collection<Pair<TableAndParticipant, TableAndParticipant>> sameGroupSwapPairs =
				new ArrayList<Pair<TableAndParticipant, TableAndParticipant>>();
		
		Collection<Pair<TableAndParticipant, TableAndParticipant>> sameSundayClassSwapPairs =
				new ArrayList<Pair<TableAndParticipant, TableAndParticipant>>();
		
		/* Construct potential swap pairs */
		for (DineTableGroup potentialSwappedTable : this.plan.getDineTableGroups()){
			/* Find Potential Swappable tables */
			
			if (potentialSwappedTable.isForSpecialGroup() || CollectionUtils.isEmpty(potentialSwappedTable.getParticipants())){
				/* Does not swap if the potential table is for special group or no participant*/
				continue;
			}
			
			if (domainatedNetGender > 0 && potentialSwappedTable.getNetGenderBalance() > 0){
				/* Does not swap if both tables are male dominating */
				continue;
			}
			
			if (domainatedNetGender < 0 && potentialSwappedTable.getNetGenderBalance() < 0){
				/* Does not swap if both tables are female dominating */
				continue;
			}
			
			for (Participant swappingParticipant : domainatedTable.getParticipants()){
				
				if (domainatedNetGender > 0 && Gender.FEMALE == swappingParticipant.getGender()){
					continue;
				}
				
				if (domainatedNetGender < 0 && Gender.MALE == swappingParticipant.getGender()){
					continue;
				}
				
				if (swappingParticipant.isSpecialParticipant()){
					continue;
				}
				
				if (swappingParticipant.hasFamilyGroup()){
					continue;
				}
				
				if (swappingParticipant.isMentor()
						|| swappingParticipant.isGroupMentor()
						|| StringUtils.contains(swappingParticipant.getSundaySchoolClass(), "導師")){
					continue;
				}
				
				for (Participant swappedParticipant : potentialSwappedTable.getParticipants()){
					
					if (swappedParticipant.isSpecialParticipant()){
						continue;
					}
					
					if (swappedParticipant.hasFamilyGroup()){
						continue;
					}
					
					if (swappingParticipant.getGender() == swappedParticipant.getGender()){
						continue;
					}
					
					if (swappedParticipant.isMentor()
							|| swappedParticipant.isGroupMentor()
							|| StringUtils.contains(swappedParticipant.getSundaySchoolClass(), "導師")){
						continue;
					}
					
					if (swappingParticipant.getGroupNumber() == swappedParticipant.getGroupNumber()){
						
						TableAndParticipant swappingTableAndParticipant = new TableAndParticipant(domainatedTable, swappingParticipant);
						TableAndParticipant swappedTableAndParticipant = new TableAndParticipant(potentialSwappedTable, swappedParticipant);
						
						sameGroupSwapPairs.add(new Pair<TableAndParticipant, TableAndParticipant>(swappingTableAndParticipant, swappedTableAndParticipant));
						
						continue;
					}
					
					if (StringUtils.equalsIgnoreCase(swappingParticipant.getSundaySchoolClass(), swappedParticipant.getSundaySchoolClass())){
						
						TableAndParticipant swappingTableAndParticipant = new TableAndParticipant(domainatedTable, swappingParticipant);
						TableAndParticipant swappedTableAndParticipant = new TableAndParticipant(potentialSwappedTable, swappedParticipant);
						
						sameSundayClassSwapPairs.add(new Pair<TableAndParticipant, TableAndParticipant>(swappingTableAndParticipant, swappedTableAndParticipant));
						
						continue;
					}
				}
			}
		}
		
		Pair<TableAndParticipant, TableAndParticipant> swapPair = RandomnessUtils.ramdomPickFromCollection(sameGroupSwapPairs, this.randomObj);
		if (swapPair == null){
			swapPair = RandomnessUtils.ramdomPickFromCollection(sameSundayClassSwapPairs, this.randomObj);
		}
		
		if (swapPair == null){
			return;
		}
		
		this.swapTable(swapPair.getLeft().getDineTableGroup(), swapPair.getRight().getDineTableGroup(),
				swapPair.getLeft().getParticipant(), swapPair.getRight().getParticipant());
	}
	
//	private void fineTuneTableParticipantNumber(){
//		
//		Collection<DineTableGroup> deficitTables = new ArrayList<DineTableGroup>();
//		
//		/* Find table needs to add participant */
//		for (DineTableGroup dineTableGroup : this.plan.getDineTableGroups()){
//			if (this.tableCapacity - dineTableGroup.getParticipants().size() <= 1 ){
//				continue;
//			}
//			
//			if (dineTableGroup.isForSpecialGroup()){
//				continue;
//			}
//			
//			deficitTables.add(dineTableGroup);
//		}
//		
//		if (CollectionUtils.isEmpty(deficitTables)){
//			return;
//		}
//		
//		/* Random pick a table */
//		DineTableGroup deficitTable = RandomnessUtils.ramdomPickFromCollection(deficitTables, this.randomObj);
//		Collection<TableAndParticipant> sameSundayClassTableAndParticipants =	new ArrayList<TableAndParticipant>();
//		Collection<TableAndParticipant> sameGroupTableAndParticipants =	new ArrayList<TableAndParticipant>();
//		Collection<TableAndParticipant> lessPreferredTableAndParticipants =	new ArrayList<TableAndParticipant>();
//		
//		/* Find a participant that is able to move to the picked table */
//		for (DineTableGroup potentialTable : this.plan.getDineTableGroups()){
//			
//			if (potentialTable.getTableNumber() == deficitTable.getTableNumber()){
//				continue;
//			}
//			
//			if (potentialTable.getParticipants().size() < this.tableCapacity){
//				continue;
//			}
//			
//			if (potentialTable.isForSpecialGroup()){
//				continue;
//			}
//			
//			int potentialTableNetGenderBalance = potentialTable.getNetGenderBalance();
//			
//			for (Participant potentialParticipant : potentialTable.getParticipants()){
//				
//				if (potentialTableNetGenderBalance > 0 && Gender.MALE == potentialParticipant.getGender()){
//					continue;
//				}
//				
//				if (potentialTableNetGenderBalance < 0 && Gender.FEMALE == potentialParticipant.getGender()){
//					continue;
//				}
//				
//				if (potentialParticipant.isSpecialParticipant()){
//					continue;
//				}
//				
//				if (potentialParticipant.hasFamilyGroup()){
//					continue;
//				}
//				
//				if (potentialParticipant.isMentor()
//						|| potentialParticipant.isGroupMentor()
//						|| StringUtils.contains(potentialParticipant.getSundaySchoolClass(), "導師")){
//					continue;
//				}
//				
//				/* Do not move this participant if the movement causes his/her groupmate alone */
//				if (potentialTable.countGroupNumber(potentialParticipant.getGroupNumber()) <= 2
//						&& potentialTable.countSundaySchoolClass(potentialParticipant.getSundaySchoolClass()) <= 2){
//					continue;
//				}
//				
//				String sundaySchoolClass = potentialParticipant.getSundaySchoolClass();
//				int groupNumber = potentialParticipant.getGroupNumber();
//				
//				for (Participant deficitTableParticipant : deficitTable.getParticipants()){
//					
//					if (deficitTableParticipant.getGroupNumber() == groupNumber){
//						sameGroupTableAndParticipants.add(new TableAndParticipant(potentialTable, potentialParticipant));
//						break;
//					}
//					
//					if (StringUtils.equalsIgnoreCase(deficitTableParticipant.getSundaySchoolClass(), sundaySchoolClass)){
//						sameSundayClassTableAndParticipants.add(new TableAndParticipant(potentialTable, potentialParticipant));
//						break;
//					}
//				}
//				
//				lessPreferredTableAndParticipants.add(new TableAndParticipant(potentialTable, potentialParticipant));
//			}
//		}
//		
//		TableAndParticipant migratedTableAndParticipant = RandomnessUtils.ramdomPickFromCollection(sameGroupTableAndParticipants, this.randomObj);
//		if (migratedTableAndParticipant == null){
//			migratedTableAndParticipant = RandomnessUtils.ramdomPickFromCollection(sameSundayClassTableAndParticipants, this.randomObj);
//		}
//		
//		if (migratedTableAndParticipant == null){
//			migratedTableAndParticipant = RandomnessUtils.ramdomPickFromCollection(lessPreferredTableAndParticipants, this.randomObj);
//		}
//		
//		if (migratedTableAndParticipant == null){
//			return;
//		}
//		
//		this.moveParticipantToAnotherTable(migratedTableAndParticipant.getDineTableGroup(), deficitTable, migratedTableAndParticipant.getParticipant());
//	}
	
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
	
	private void moveParticipantToAnotherTable(
			DineTableGroup originalTable,
			DineTableGroup newTable,
			Participant participant){
		
		originalTable.getParticipants().remove(participant);
		newTable.getParticipants().add(participant);
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
	
	private void assignFamilyGroupToSpecialGroupTable(
			Collection<Participant> unassignedParticipants,
			Collection<Participant> assignedParticipants,
			Collection<DineTableGroup> dineTableGroups,
			int tableCapacity) {
		
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
				
				int emptySeat = tableCapacity - dineTableGroup.getParticipants().size();
				
				if (familyGroup.getBelieverIds().size() + 2 > emptySeat){
					continue;
				}
				
				availableTables.add(dineTableGroup);
			}
			
			if (availableTables.size() == 0){
				for (DineTableGroup dineTableGroup : dineTableGroups){
					
					int emptySeat = tableCapacity - dineTableGroup.getParticipants().size();
					
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
	
	private void assignSpecialGroupToTable(
			Collection<Participant> unassignedParticipants,
			Collection<Participant> assignedParticipants,
			Collection<DineTableGroup> dineTableGroups,
			int tableCapacity) {
		
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
				remainingSpace = tableCapacity - dineTableGroup.getParticipants().size();
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
				remainingSpace = tableCapacity - dineTableGroup.getParticipants().size();
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
			Collection<DineTableGroup> dineTableGroups,
			int tableCapacity) {
		
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
		
		System.out.printf("Unassigned:%d, Assigned:%d\n", unassignedParticipants.size(), assignedParticipants.size());
		
		for (Entry<String, FamilyGroup> entry : familyGroupMap.entrySet()){
			FamilyGroup familyGroup = entry.getValue();
			
			DineTableGroup tempTableGroup = null;
			
			Collection<DineTableGroup> availableTables = new ArrayList<DineTableGroup>();
			for (DineTableGroup dineTableGroup : dineTableGroups){
				
				if (dineTableGroup.getParticipants().size() > 0){
					continue;
				}
				
				int emptySeat = tableCapacity - dineTableGroup.getParticipants().size();
				
				if (familyGroup.getBelieverIds().size() + 2 > emptySeat){
					continue;
				}
				
				availableTables.add(dineTableGroup);
			}
			
			if (availableTables.size() == 0){
				for (DineTableGroup dineTableGroup : dineTableGroups){
					
					int emptySeat = tableCapacity - dineTableGroup.getParticipants().size();
					
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
		
		System.out.printf("Unassigned:%d, Assigned:%d\n", unassignedParticipants.size(), assignedParticipants.size());
		
		unassignedParticipants.removeAll(operatedParticipants);
		
		System.out.printf("Unassigned:%d, Assigned:%d\n", unassignedParticipants.size(), assignedParticipants.size());
		assignedParticipants.addAll(operatedParticipants);
		
		System.out.printf("Unassigned:%d, Assigned:%d\n", unassignedParticipants.size(), assignedParticipants.size());
	}
	
	private void assignParticipantToTable(
			Collection<Participant> unassignedParticipants,
			Collection<Participant> assignedParticipants,
			Collection<DineTableGroup> dineTableGroups,
			int tableCapacity) {
				
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
				remainingSpace = tableCapacity - dineTableGroup.getParticipants().size();
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
				remainingSpace = tableCapacity - dineTableGroup.getParticipants().size();
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
		this.fineTuneInitialAssignment();
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
			
			participant1 = RandomnessUtils.ramdomPickFromCollection(dineTableGroup1.getParticipants(), randomObj);
			participant2 = RandomnessUtils.ramdomPickFromCollection(dineTableGroup2.getParticipants(), randomObj);
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
	
	public void doPlanEvaluation(){
		this.evaluator.evaluatePlan(this.plan);
	}
	
	private Collection<DineTableGroup> getSpecialDineTables(
			Collection<DineTableGroup> dineTableGroups,
			Collection<Participant> unassignedParticipants,
			int tableCapacity){
		
		if (CollectionUtils.isEmpty(unassignedParticipants)){
			return new ArrayList<DineTableGroup>();
		}
		
		Collection<Participant> countedParticipants = new HashSet<Participant>();
		
		for (Participant participant : unassignedParticipants){
			
			if (participant.isNormalParticipant() || participant.countAvailableDine(this.plan.getDay()) <= 0){
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
		System.out.printf("Special Participant Count: %d\n", countedParticipants.size());
		
		if (countedParticipants.size() == 0){
			return new ArrayList<DineTableGroup>();
		}
		
		int unassignedSpecialParticipantCount = countedParticipants.size();
		List<DineTableGroup> specialDineTables = new ArrayList<DineTableGroup>();
		
		List<DineTableGroup> allDineTableList = new ArrayList<DineTableGroup>();
		allDineTableList.addAll(dineTableGroups);
		
		for (int i = allDineTableList.size() - 1 ; i > 0; i--){
			DineTableGroup dineTableGroup = allDineTableList.get(i);
			
			int emptySeat = tableCapacity - dineTableGroup.getParticipants().size();
			
			dineTableGroup.setSpecialGroup(1);
			specialDineTables.add(dineTableGroup);
			unassignedSpecialParticipantCount -= emptySeat;
			
			if (unassignedSpecialParticipantCount <= 0){
				break;
			}
		}
		
		Collections.sort(specialDineTables, new DineTableNumberComparator());
		
		return specialDineTables;
	}
	
	private void printCurrentAssignmentInfo(){
		System.out.printf("Current Assignment: [Camp=%s][Day=%d]\n", this.plan.getCampName(), this.plan.getDay());
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
			System.out.printf("[ID:%s][Name:%s][GroupNumber:%d][FamilyGroup:%s]\n",
					participant.getId(),
					participant.getName(),
					participant.getGroupNumber(),
					(participant.getFamilyGroup() == null) ? "" : participant.getFamilyGroup().getFamilyId());
		}
	}
	
	private void listOutDineTables(Collection<DineTableGroup> dineTableGroups) {
		if (CollectionUtils.isEmpty(dineTableGroups)){
			return;
		}
		
		for (DineTableGroup dineTableGroup : dineTableGroups){
			System.out.printf("[Table Number:%d][Participant:%d][Male:%d][Female:%d]\n",
					dineTableGroup.getTableNumber(),
					dineTableGroup.getParticipants().size(),
					dineTableGroup.countMale(),
					dineTableGroup.countFemale());
		}
	}
}
