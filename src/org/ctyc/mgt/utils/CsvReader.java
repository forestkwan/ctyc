package org.ctyc.mgt.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.ctyc.mgt.model.FamilyGroup;
import org.ctyc.mgt.model.Gender;
import org.ctyc.mgt.model.summercamp.DineAvailability;
import org.ctyc.mgt.model.summercamp.DineTimeSlot;
import org.ctyc.mgt.model.summercamp.Participant;

public class CsvReader {

	public static Collection<Participant> readParticipantCsv(String filepath){
		try {
			return readParticipantCsvFromStream(new FileInputStream(filepath));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static Collection<Participant> readParticipantCsvFromStream(InputStream inputStream){
		
		BufferedReader bufferedReader = null;
		String line = "";
		String separator = "::";

		Collection<Participant> participants = new ArrayList<Participant>();
		try {

			bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
			Map<String, FamilyGroup> familyGroupMap = new HashMap<String, FamilyGroup>();
			
			while ((line = bufferedReader.readLine()) != null) {

				String[] tokens = line.split(separator);
				
				if (tokens.length <= 1){
					break;
				}
				
				Participant participant = new Participant();
				participant.setId(tokens[1].replace("\"", ""));
				participant.setName(tokens[2].replace("\"", ""));
				
				if (StringUtils.isNotBlank(tokens[3])){
					Gender sex = (StringUtils.equals(tokens[3].replace("\"", ""), "M")) ? Gender.MALE : Gender.FEMALE;
					participant.setGender(sex);
				}
				
				if (StringUtils.isNotBlank(tokens[6])){
					participant.setSundaySchoolClass(tokens[6].replace("\"", ""));
				}
				
				if (StringUtils.isNotBlank(tokens[7])){
					boolean isGroupMentor = (StringUtils.equals(tokens[7].replace("\"", ""), "1")) ? true : false;
					participant.setGroupMentor(isGroupMentor);
					participant.setMentor(isGroupMentor);
				}

				if (StringUtils.isNotBlank(tokens[8])){
					participant.setGroupNumber(convertToGroupNumber(tokens[8].replace("\"", "")));
				}
				
				if (StringUtils.isNotBlank(tokens[35]) && StringUtils.contains(tokens[35], "family")){
					
					String familyKey = tokens[35].replace("\"", "");
					FamilyGroup familyGroup = familyGroupMap.get(familyKey);
					
					if (familyGroup == null){
						familyGroup = new FamilyGroup(familyKey);
						familyGroup.getBelieverIds().add(participant.getId());
						familyGroupMap.put(familyKey, familyGroup);
						
						participant.setFamilyGroup(familyGroup);
					}else {
						familyGroup.getBelieverIds().add(participant.getId());
						participant.setFamilyGroup(familyGroup);
					}
					
				}
				
				if (StringUtils.isNotBlank(tokens[16])){
					boolean isDine = (StringUtils.equals(tokens[16].replace("\"", ""), "1")) ? true : false;
					participant.getDineAvailabilitys().add(new DineAvailability(1, DineTimeSlot.TimeOfDay.NIGHT.toString(), isDine));
				}
				
				if (StringUtils.isNotBlank(tokens[17])){
					boolean isDine = (StringUtils.equals(tokens[17].replace("\"", ""), "1")) ? true : false;
					participant.getDineAvailabilitys().add(new DineAvailability(1, DineTimeSlot.TimeOfDay.MORNING.toString(), isDine));
				}
				
				if (StringUtils.isNotBlank(tokens[18])){
					boolean isDine = (StringUtils.equals(tokens[18].replace("\"", ""), "1")) ? true : false;
					participant.getDineAvailabilitys().add(new DineAvailability(1, DineTimeSlot.TimeOfDay.NOON.toString(), isDine));
				}
				
				if (StringUtils.isNotBlank(tokens[19])){
					boolean isDine = (StringUtils.equals(tokens[19].replace("\"", ""), "1")) ? true : false;
					participant.getDineAvailabilitys().add(new DineAvailability(2, DineTimeSlot.TimeOfDay.NIGHT.toString(), isDine));
				}
				
				if (StringUtils.isNotBlank(tokens[20])){
					boolean isDine = (StringUtils.equals(tokens[20].replace("\"", ""), "1")) ? true : false;
					participant.getDineAvailabilitys().add(new DineAvailability(2, DineTimeSlot.TimeOfDay.MORNING.toString(), isDine));
				}
				
				if (StringUtils.isNotBlank(tokens[21])){
					boolean isDine = (StringUtils.equals(tokens[21].replace("\"", ""), "1")) ? true : false;
					participant.getDineAvailabilitys().add(new DineAvailability(2, DineTimeSlot.TimeOfDay.NOON.toString(), isDine));
				}
				
				if (StringUtils.isNotBlank(tokens[22])){
					boolean isDine = (StringUtils.equals(tokens[22].replace("\"", ""), "1")) ? true : false;
					participant.getDineAvailabilitys().add(new DineAvailability(3, DineTimeSlot.TimeOfDay.NIGHT.toString(), isDine));
				}
				
				if (StringUtils.isNotBlank(tokens[23])){
					boolean isDine = (StringUtils.equals(tokens[23].replace("\"", ""), "1")) ? true : false;
					participant.getDineAvailabilitys().add(new DineAvailability(3, DineTimeSlot.TimeOfDay.MORNING.toString(), isDine));
				}
				
				if (StringUtils.isNotBlank(tokens[24])){
					boolean isDine = (StringUtils.equals(tokens[24].replace("\"", ""), "1")) ? true : false;
					participant.getDineAvailabilitys().add(new DineAvailability(3, DineTimeSlot.TimeOfDay.NOON.toString(), isDine));
				}
				
				if (StringUtils.isNotBlank(tokens[25])){
					boolean isDine = (StringUtils.equals(tokens[25].replace("\"", ""), "1")) ? true : false;
					participant.getDineAvailabilitys().add(new DineAvailability(4, DineTimeSlot.TimeOfDay.NIGHT.toString(), isDine));
				}
				
				if (StringUtils.isNotBlank(tokens[26])){
					boolean isDine = (StringUtils.equals(tokens[26].replace("\"", ""), "1")) ? true : false;
					participant.getDineAvailabilitys().add(new DineAvailability(4, DineTimeSlot.TimeOfDay.MORNING.toString(), isDine));
				}
				
				if (StringUtils.isNotBlank(tokens[27])){
					boolean isDine = (StringUtils.equals(tokens[27].replace("\"", ""), "1")) ? true : false;
					participant.getDineAvailabilitys().add(new DineAvailability(4, DineTimeSlot.TimeOfDay.NOON.toString(), isDine));
				}
				
				if (StringUtils.isNotBlank(tokens[34])){
					Integer specialGroup = Integer.parseInt(tokens[34].replace("grp-", "").replace("\"", ""));
					participant.setSpecialGroup(specialGroup);
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
	
	public static Map<String, Integer> readPreassignedTable(InputStream inputStream) {
		BufferedReader bufferedReader = null;
		String line = "";
		String separator = "::";
		
		Map<String, Integer> result = new HashMap<String, Integer>();

		try {

			bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
			while ((line = bufferedReader.readLine()) != null) {
				
				String[] tokens = line.split(separator);
				
				if (tokens.length <= 1){
					break;
				}
				String id = tokens[1].replace("\"", "");
				
				if (StringUtils.isNotBlank(tokens[36]) && StringUtils.isNumeric(tokens[36].replace("\"", ""))){
					Integer preassignedTableNumebr = Integer.parseInt(tokens[36].replace("\"", ""));
					result.put(id, preassignedTableNumebr);
				}
				
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

		return result;
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
