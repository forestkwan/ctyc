package org.ctyc.mgt.test;

import java.util.Collection;

import org.ctyc.mgt.model.summercamp.Participant;
import org.ctyc.mgt.utils.CsvReader;

public class ReadAndWriteFileTester {

	public void testWriteParticipants(){
		
		System.out.println("Test Write Participants");
		Collection<Participant> campAParticipants = CsvReader.readParticipantCsv("c:\\CTYCSave\\campA_panticipants.csv");
	}
}
