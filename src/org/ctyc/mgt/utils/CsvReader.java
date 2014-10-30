package org.ctyc.mgt.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.ctyc.mgt.model.FamilyGroup;
import org.ctyc.mgt.model.Sex;
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
			Map<String, FamilyGroup> familyGroupMap = new HashMap<String, FamilyGroup>();
			
			while ((line = bufferedReader.readLine()) != null) {

				count++;
				if (count == 1){
					continue;
				}
				
				String[] tokens = line.split(separator);
				
				Participant participant = new Participant();
				participant.setId(tokens[0]);
				participant.setName(tokens[1]);
				
				if (StringUtils.isNotBlank(tokens[2])){
					Sex sex = (StringUtils.equals(tokens[2], "�k")) ? Sex.MALE : Sex.FEMALE;
					participant.setSex(sex);
				}
				
				if (StringUtils.isNotBlank(tokens[5])){
					participant.setSundaySchoolClass(tokens[5]);
				}
				
				if (StringUtils.isNotBlank(tokens[6])){
					boolean isGroupMentor = (StringUtils.equals(tokens[6], "Y")) ? true : false;
					participant.setGroupMentor(isGroupMentor);
					participant.setMentor(isGroupMentor);
				}

				if (StringUtils.isNotBlank(tokens[7])){
					participant.setGroupNumber(convertToGroupNumber(tokens[7]));
				}
				
				if (StringUtils.isNotBlank(tokens[13])){
					
					FamilyGroup familyGroup = familyGroupMap.get(tokens[13]);
					
					if (familyGroup == null){
						familyGroup = new FamilyGroup(tokens[13]);
						familyGroup.getBelievers().add(participant);
						familyGroupMap.put(tokens[13], familyGroup);
						
						participant.setFamilyGroup(familyGroup);
					}else {
						familyGroup.getBelievers().add(participant);
						participant.setFamilyGroup(familyGroup);
					}
					
				}
				
				participants.add(participant);
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
	
	private static int convertToGroupNumber(String name){
		
		if (StringUtils.isBlank(name)){
			return 0;
		}
		
		if (StringUtils.contains(name, "�T�Q")){
			return 30;
		}
		if (StringUtils.contains(name, "�ܤE") || StringUtils.contains(name, "�G�Q�E")){
			return 29;
		}
		if (StringUtils.contains(name, "�ܤK") || StringUtils.contains(name, "�G�Q�K")){
			return 28;
		}
		if (StringUtils.contains(name, "�ܤC") || StringUtils.contains(name, "�G�Q�C")){
			return 27;
		}
		if (StringUtils.contains(name, "�ܤ�") || StringUtils.contains(name, "�G�Q��")){
			return 26;
		}
		if (StringUtils.contains(name, "�ܤ�") || StringUtils.contains(name, "�G�Q��")){
			return 25;
		}
		if (StringUtils.contains(name, "�ܥ|") || StringUtils.contains(name, "�G�Q�|")){
			return 24;
		}
		if (StringUtils.contains(name, "�ܤT") || StringUtils.contains(name, "�G�Q�T")){
			return 23;
		}
		if (StringUtils.contains(name, "�ܤG") || StringUtils.contains(name, "�G�Q�G")){
			return 22;
		}
		if (StringUtils.contains(name, "�ܤ@") || StringUtils.contains(name, "�G�Q�@")){
			return 21;
		}
		if (StringUtils.contains(name, "��") || StringUtils.contains(name, "�G�Q")){
			return 20;
		}
		if (StringUtils.contains(name, "�Q�E")){
			return 19;
		}
		if (StringUtils.contains(name, "�Q�K")){
			return 18;
		}
		if (StringUtils.contains(name, "�Q�C")){
			return 17;
		}
		if (StringUtils.contains(name, "�Q��")){
			return 16;
		}
		if (StringUtils.contains(name, "�Q��")){
			return 15;
		}
		if (StringUtils.contains(name, "�Q�|")){
			return 14;
		}
		if (StringUtils.contains(name, "�Q�T")){
			return 13;
		}
		if (StringUtils.contains(name, "�Q�G")){
			return 12;
		}
		if (StringUtils.contains(name, "�Q�@")){
			return 11;
		}
		if (StringUtils.contains(name, "�Q")){
			return 10;
		}
		if (StringUtils.contains(name, "�E")){
			return 9;
		}
		if (StringUtils.contains(name, "�K")){
			return 8;
		}
		if (StringUtils.contains(name, "�C")){
			return 7;
		}
		if (StringUtils.contains(name, "��")){
			return 6;
		}
		if (StringUtils.contains(name, "��")){
			return 5;
		}
		if (StringUtils.contains(name, "�|")){
			return 4;
		}
		if (StringUtils.contains(name, "�T")){
			return 3;
		}
		if (StringUtils.contains(name, "�G")){
			return 2;
		}
		if (StringUtils.contains(name, "�@")){
			return 1;
		}
		return 0;
	}
}
