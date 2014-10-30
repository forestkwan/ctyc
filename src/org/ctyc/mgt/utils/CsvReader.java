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
					Sex sex = (StringUtils.equals(tokens[2], "男")) ? Sex.MALE : Sex.FEMALE;
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
		
		if (StringUtils.contains(name, "三十")){
			return 30;
		}
		if (StringUtils.contains(name, "廿九") || StringUtils.contains(name, "二十九")){
			return 29;
		}
		if (StringUtils.contains(name, "廿八") || StringUtils.contains(name, "二十八")){
			return 28;
		}
		if (StringUtils.contains(name, "廿七") || StringUtils.contains(name, "二十七")){
			return 27;
		}
		if (StringUtils.contains(name, "廿六") || StringUtils.contains(name, "二十六")){
			return 26;
		}
		if (StringUtils.contains(name, "廿五") || StringUtils.contains(name, "二十五")){
			return 25;
		}
		if (StringUtils.contains(name, "廿四") || StringUtils.contains(name, "二十四")){
			return 24;
		}
		if (StringUtils.contains(name, "廿三") || StringUtils.contains(name, "二十三")){
			return 23;
		}
		if (StringUtils.contains(name, "廿二") || StringUtils.contains(name, "二十二")){
			return 22;
		}
		if (StringUtils.contains(name, "廿一") || StringUtils.contains(name, "二十一")){
			return 21;
		}
		if (StringUtils.contains(name, "廿") || StringUtils.contains(name, "二十")){
			return 20;
		}
		if (StringUtils.contains(name, "十九")){
			return 19;
		}
		if (StringUtils.contains(name, "十八")){
			return 18;
		}
		if (StringUtils.contains(name, "十七")){
			return 17;
		}
		if (StringUtils.contains(name, "十六")){
			return 16;
		}
		if (StringUtils.contains(name, "十五")){
			return 15;
		}
		if (StringUtils.contains(name, "十四")){
			return 14;
		}
		if (StringUtils.contains(name, "十三")){
			return 13;
		}
		if (StringUtils.contains(name, "十二")){
			return 12;
		}
		if (StringUtils.contains(name, "十一")){
			return 11;
		}
		if (StringUtils.contains(name, "十")){
			return 10;
		}
		if (StringUtils.contains(name, "九")){
			return 9;
		}
		if (StringUtils.contains(name, "八")){
			return 8;
		}
		if (StringUtils.contains(name, "七")){
			return 7;
		}
		if (StringUtils.contains(name, "六")){
			return 6;
		}
		if (StringUtils.contains(name, "五")){
			return 5;
		}
		if (StringUtils.contains(name, "四")){
			return 4;
		}
		if (StringUtils.contains(name, "三")){
			return 3;
		}
		if (StringUtils.contains(name, "二")){
			return 2;
		}
		if (StringUtils.contains(name, "一")){
			return 1;
		}
		return 0;
	}
}
