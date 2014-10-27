package org.ctyc.mgt.summercamp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Random;
import java.util.Stack;

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
	private int tableCapacity;
	
	// Private calculation object
	private Random randomObj;
	
	public DineAssignmentManager(
			Collection<Participant> participants,
			int tableCapacity,
			Collection<AbstractCostFunction> costFunctions,
			Collection<AbstractCostFunction> constraintFunctions){
		
		this.participants = participants;
		this.tableCapacity = tableCapacity;
		this.plan = new DineAssignmentPlan();
		this.randomObj = new Random();
		
		this.evaluator = new DineAssignmentEvaluator(costFunctions, constraintFunctions);
		
	}
	
	public DineAssignmentManager(
			Collection<Participant> participants,
			int tableCapacity,
			Collection<AbstractCostFunction> costFunctions,
			Collection<AbstractCostFunction> constraintFunctions,
			int seed){
		
		this(participants, tableCapacity, costFunctions, constraintFunctions);
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
		
		Stack<Participant> unassignedParticipants = new Stack<Participant>();
		unassignedParticipants.addAll(participants);
		
		Collection<DineTableGroup> dineTableGroupList = this.createEmptyTableGroupList();
		
		for (DineTableGroup dineTableGroup : dineTableGroupList){
			
			while (!isTableFull(dineTableGroup) && !CollectionUtils.isEmpty(unassignedParticipants)){
				Participant unassignedParticipant = RandomnessUtils.popRandomParticipant(unassignedParticipants, this.randomObj);
				dineTableGroup.getParticipants().add(unassignedParticipant);
			}
		}
		
		this.plan.getPlan().addAll(dineTableGroupList);
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
		if (this.plan == null || this.plan.getPlan() == null || this.plan.getPlan().size() < 2){
			return;
		}

		DineTableGroup dineTableGroup1 = RandomnessUtils.pickRandomDineTableGroup(this.plan.getPlan(), this.randomObj);
		DineTableGroup dineTableGroup2 = RandomnessUtils.pickRandomDineTableGroup(this.plan.getPlan(), this.randomObj);
		
		while (dineTableGroup1 == dineTableGroup2){
			dineTableGroup1 = RandomnessUtils.pickRandomDineTableGroup(this.plan.getPlan(), this.randomObj);
			dineTableGroup2 = RandomnessUtils.pickRandomDineTableGroup(this.plan.getPlan(), this.randomObj);
		}
		
		double originCost1 = dineTableGroup1.getCost();
		double originCost2 = dineTableGroup2.getCost();
		
		Participant participant1 = RandomnessUtils.pickRandomParticipant(dineTableGroup1.getParticipants(), randomObj);
		Participant participant2 = RandomnessUtils.pickRandomParticipant(dineTableGroup2.getParticipants(), randomObj);
		
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
}
