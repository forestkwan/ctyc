package org.ctyc.mgt.test;

import java.util.Collection;

import org.ctyc.mgt.model.summercamp.CampSite;
import org.ctyc.mgt.model.summercamp.Participant;
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
		
		CampSite campSite2 = FileUtils.readFileToObject("c:\\CTYCSave\\CampSite.ser");
		
		System.out.println(campSite2);
	}
	
}
