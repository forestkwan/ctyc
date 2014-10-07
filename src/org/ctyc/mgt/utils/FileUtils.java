package org.ctyc.mgt.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.ctyc.mgt.model.summercamp.DineTableGroup;
import org.ctyc.mgt.model.summercamp.DineTimeSlot;
import org.ctyc.mgt.model.summercamp.Participant;
import org.springframework.util.CollectionUtils;

public class FileUtils {
	
	private static String ENCODING = "UTF-8";
	private static String DEFAULT_FILE_PATH = "c:\\ctycTempOutput.txt";
	
	public static void writeObjectToFile(Serializable object, String filePath){
		
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
			ex.printStackTrace();
			return null;
		}
	}
	
	public static void writeDineAssignmentPlan(DineTimeSlot dineTimeSlot, Collection<DineTableGroup> dineTableGroups, String filePath){
		
		if (dineTimeSlot == null || CollectionUtils.isEmpty(dineTableGroups)){
			System.out.println("Dine assignment plan is empty. No file is outputed");
		}
		
		if (StringUtils.isBlank(filePath)){
			filePath = DEFAULT_FILE_PATH;
		}
		
		try {
			PrintWriter printWriter = new PrintWriter(filePath, ENCODING);
			
			printWriter.printf("Day %d, %s\n", dineTimeSlot.getNumberOfDay(), dineTimeSlot.getTimeOfDay().toString());
			for (DineTableGroup dineTableGroup : dineTableGroups){
				
				printWriter.printf("Table%d: ", dineTableGroup.getTableNumber());
				
				for (Participant Participant : dineTableGroup.getParticipants()){
					printWriter.printf("%s\t", Participant.getName());
				}
				
				printWriter.println();
			}
			
			printWriter.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
