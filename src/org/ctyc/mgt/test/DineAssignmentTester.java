package org.ctyc.mgt.test;

import java.util.ArrayList;
import java.util.Collection;

import org.ctyc.mgt.model.summercamp.CampSite;
import org.ctyc.mgt.model.summercamp.DineTimeSlot;
import org.ctyc.mgt.model.summercamp.Participant;
import org.ctyc.mgt.summercamp.DineAssignmentManager;
import org.ctyc.mgt.summercamp.DineAssignmentPlan;
import org.ctyc.mgt.summercamp.costfunction.AbstractCostFunction;
import org.ctyc.mgt.summercamp.costfunction.SameSundayClassCostFunction;
import org.ctyc.mgt.utils.CsvReader;
import org.ctyc.mgt.utils.FileUtils;

public class DineAssignmentTester {
	
	public DineAssignmentTester(String arg0){
//		super(arg0);
	}

	public void testDineAssignment(){
		
		System.out.println("testDineAssignment");
		
		CampSite campSite = TestObjectGenerator.generateTestCampSite();
		FileUtils.writeObjectToFile(campSite, "CTYCSave/CampSite.ser");
		
		Collection<Participant> campAParticipants = CsvReader.readParticipantCsv("CTYCSave/campA_panticipants.csv");
		campSite.getParticipants().addAll(campAParticipants);
		
		Collection<AbstractCostFunction> costFunctions = new ArrayList<AbstractCostFunction>();
//		costFunctions.add(new GenderBalanceCostFunction(1, 1));
//		costFunctions.add(new SameGroupCostFunction(1, 1));
		costFunctions.add(new SameSundayClassCostFunction(1, 1));
		
		Collection<AbstractCostFunction> constraintFunctions = new ArrayList<AbstractCostFunction>();
//		constraintFunctions.add(new MentorInTableCostFunction(1, 1));
//		constraintFunctions.add(new FamilyGroupCostFunction(1, 1));
		
		DineAssignmentManager dineAssignmentManager = new DineAssignmentManager("A", 1, campSite, costFunctions, constraintFunctions, 1);
		
		dineAssignmentManager.doAssignment();
		DineAssignmentPlan dineAssignmentPlan = dineAssignmentManager.getAssignmentPlan();
		
		DineTimeSlot dineTimeSlot = new DineTimeSlot(1, DineTimeSlot.TimeOfDay.NIGHT);
		String filename = "Day" + dineTimeSlot.getNumberOfDay() + dineTimeSlot.getTimeOfDay().toString() + ".txt";
			
		FileUtils.writeDineAssignmentPlan(dineTimeSlot, dineAssignmentPlan, "CTYCSave/" + filename);
		
		CampSite campSite2 = FileUtils.readFileToObject("CTYCSave/CampSite.ser");
		
		System.out.println(campSite2);
	}
	
}
