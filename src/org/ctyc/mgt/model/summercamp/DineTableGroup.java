package org.ctyc.mgt.model.summercamp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.ctyc.mgt.model.Gender;

public class DineTableGroup implements Serializable{

	private static final long serialVersionUID = 5189900740930218925L;
	private int tableNumber;
	private Collection<Participant> participants;
	private double cost;
	private Map<String, Double> evaluationResultMap;
	private Integer specialGroup;

	public int getTableNumber() {
		return tableNumber;
	}
	
	public void setTableNumber(int tableNumber) {
		this.tableNumber = tableNumber;
	}
	
	public Collection<Participant> getParticipants() {
		if (this.participants == null){
			this.participants = new ArrayList<Participant>();
		}
		return participants;
	}
	
	public double getCost() {
		return cost;
	}
	
	public void setCost(double cost) {
		this.cost = cost;
	}
	
	public Integer getSpecialGroup() {
		return specialGroup;
	}

	public void setSpecialGroup(Integer specialGroup) {
		this.specialGroup = specialGroup;
	}
	
	public int getNoOfGroupMentor(){
		int count = 0;
		for (Participant participant : this.getParticipants()){
			if (participant.isGroupMentor() || participant.isMentor() || StringUtils.contains(participant.getSundaySchoolClass(), "導師")){
				count++;
			}
		}
		return count;
	}
	
	public int getNetGenderBalance(){
		
		/*
		 * If return positive, it means male is more than female 
		 * If return negative, it means female is more than male
		 * If return zero, it means the numbers of male and female are the same*/
		
		int numberOfMale = 0;
		int numberOfFemale = 0;
		
		for (Participant participant : this.getParticipants()){
			if (participant.getGender() == Gender.MALE){
				numberOfMale ++;
			}else {
				numberOfFemale++;
			}
		}
		
		return numberOfMale - numberOfFemale;
	}
	
	public boolean removeParticipant(String id){
		Participant target = null;
		for (Participant participant : this.getParticipants()){
			if (StringUtils.equalsIgnoreCase(id, participant.getId())){
				target = participant;
				break;
			}
		}
		
		if (target == null){
			return false;
		}else {
			this.getParticipants().remove(target);
			return true;
		}
	}
	
	public Map<String, Double> getEvaluationResultMap() {
		if (this.evaluationResultMap == null){
			this.evaluationResultMap = new HashMap<String, Double>();
		}
		return evaluationResultMap;
	}
	
	public int countParticipantForParticularDine(int numberOfDay, String timeOfDay){
		int count = 0;
		for (Participant participant : this.getParticipants()){
			
			for (DineAvailability dineAvailability : participant.getDineAvailabilitys()){
				
				if (dineAvailability.getNumberOfDay() != numberOfDay ||
						!StringUtils.equalsIgnoreCase(dineAvailability.getTimeOfDay(), timeOfDay)){
					continue;
				}
				
				if (dineAvailability.isJoin()){
					count++;
				}
			}
		}
		
		return count;
	}
}
