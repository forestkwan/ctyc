package org.ctyc.mgt.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.ctyc.mgt.model.summercamp.DineTableGroup;
import org.ctyc.mgt.model.summercamp.DineTimeSlot;
import org.ctyc.mgt.model.summercamp.Participant;
import org.ctyc.mgt.summercamp.DineAssignmentPlan;
import org.springframework.util.CollectionUtils;

public class FileUtils {
	
	private static String ENCODING = "UTF-8";
	private static String DEFAULT_FILE_PATH = "ctycTempOutput.txt";
	
	public static void writeObjectToFile(Object object, String filePath){
		
		if (StringUtils.isBlank(filePath)){
			filePath = DEFAULT_FILE_PATH;
		}
		
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(filePath);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
			objectOutputStream.writeObject(object);
			objectOutputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		  
	}
	
	public static <T> T readFileToObject(String filePath){
		
		if (StringUtils.isBlank(filePath)){
			filePath = DEFAULT_FILE_PATH;
		}
		
		try {

			FileInputStream fileInputStream = new FileInputStream(filePath);
			ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
			T object = (T) objectInputStream.readObject();
			objectInputStream.close();
			return object;
		} catch (Exception ex) {
			return null;
		}
	}
	
	public static void writeDineAssignmentPlan(DineTimeSlot dineTimeSlot, DineAssignmentPlan dineAssignmentPlan, String filePath){
		
		if (dineTimeSlot == null || CollectionUtils.isEmpty(dineAssignmentPlan.getDineTableGroups())){
			System.out.println("Dine assignment plan is empty. No file is outputed");
		}
		
		if (StringUtils.isBlank(filePath)){
			filePath = DEFAULT_FILE_PATH;
		}
		
		try {
			PrintWriter printWriter = new PrintWriter(filePath, ENCODING);
			
			printWriter.printf("Day %d, %s", dineTimeSlot.getNumberOfDay(), dineTimeSlot.getTimeOfDay().toString());
			printWriter.println();
			for (DineTableGroup dineTableGroup : dineAssignmentPlan.getDineTableGroups()){
				
				printWriter.printf("Table%d [%d人][Cost=%.2f][Mentor Number=%d]: ",
						dineTableGroup.getTableNumber(),
						dineTableGroup.getParticipants().size(),
						dineTableGroup.getCost(),
						dineTableGroup.getNoOfGroupMentor());
				
				for (Participant participant : dineTableGroup.getParticipants()){
					String familyId = (participant.getFamilyGroup() != null) ? participant.getFamilyGroup().getFamilyId() : "";
					printWriter.printf("%s(%s)[%s]\t", participant.getName(), participant.getSundaySchoolClass(), familyId);
				}
				
				printWriter.println();
			}
			
			printWriter.printf("Total Cost = %.2f", dineAssignmentPlan.getCost());
			
			printWriter.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}