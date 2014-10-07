package org.ctyc.mgt.test;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.ctyc.mgt.model.summercamp.CampSite;
import org.ctyc.mgt.model.summercamp.DineTableGroup;
import org.ctyc.mgt.model.summercamp.DineTimeSlot;
import org.ctyc.mgt.model.summercamp.Participant;
import org.ctyc.mgt.summercamp.DineAssignmentManager;
import org.ctyc.mgt.utils.CsvReader;
import org.ctyc.mgt.utils.FileUtils;

import junit.framework.TestCase;

public class DineAssignmentTester extends TestCase {
	
	public DineAssignmentTester(String arg0){
		super(arg0);
	}

	public void testDineAssignment(){
		
		System.out.println("testDineAssignment");
		
		CampSite campSite = TestObjectGenerator.generateTestCampSite();
		FileUtils.writeObjectToFile(campSite, "c:\\CTYCSave\\CampSite.ser");
		
		Collection<Participant> campAParticipants = CsvReader.readParticipantCsv();
		DineAssignmentManager dineAssignmentManager = new DineAssignmentManager(campAParticipants, 8);
		dineAssignmentManager.doAssignment();
		Map<DineTimeSlot, Collection<DineTableGroup>> dineAssignmentPlan = dineAssignmentManager.getAssignmentPlan();
		
		for (Entry<DineTimeSlot, Collection<DineTableGroup>> entrySet : dineAssignmentPlan.entrySet()){
			DineTimeSlot dineTimeSlot = entrySet.getKey();
			Collection<DineTableGroup> dineTableGroups = entrySet.getValue();
			
			String filename = "Day" + dineTimeSlot.getNumberOfDay() + dineTimeSlot.getTimeOfDay().toString() + ".txt";
			
			FileUtils.writeDineAssignmentPlan(dineTimeSlot, dineTableGroups, "C:\\CTYCSave\\" + filename);
		}
		
		CampSite campSite2 = FileUtils.readFileToObject("c:\\CTYCSave\\CampSite.ser");
		
		System.out.println(campSite2);
	}
	
}
