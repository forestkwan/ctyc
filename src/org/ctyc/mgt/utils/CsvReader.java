package org.ctyc.mgt.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.ctyc.mgt.model.summercamp.Participant;

public class CsvReader {

	public static Collection<Participant> readParticipantCsv(){
		
		String csvFileToRead = "c:\\CTYCSave\\campA_panticipants.csv";
		BufferedReader bufferedReader = null;
		String line = "";
		String separator = ",";

		Collection<Participant> participants = new ArrayList<Participant>();
		try {

			bufferedReader = new BufferedReader(new FileReader(csvFileToRead));
			int count = 0;
			while ((line = bufferedReader.readLine()) != null) {

				count++;
				if (count == 1){
					continue;
				}
				
				String[] tokens = line.split(separator);
				
				Participant participant = new Participant();
				participant.setName(tokens[1]);

			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return participants;
	}
}
