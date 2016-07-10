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
	
	public boolean isForSpecialGroup(){
		return (this.specialGroup != null && this.specialGroup > 0);
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
	
	public Collection<Participant> getMaleParticipants(){
		Collection<Participant> maleParticipants = new ArrayList<Participant>();
		for (Participant participant : this.getParticipants()){
			if (participant.getGender() == Gender.MALE){
				maleParticipants.add(participant);
			}
		}
		return maleParticipants;
	}
	
	public Collection<Participant> getFemaleParticipants(){
		Collection<Participant> femaleParticipants = new ArrayList<Participant>();
		for (Participant participant : this.getParticipants()){
			if (participant.getGender() == Gender.FEMALE){
				femaleParticipants.add(participant);
			}
		}
		return femaleParticipants;
	}
	
	public int countMale(){
		return this.getMaleParticipants().size();
	}
	
	public int countFemale(){
		return this.getFemaleParticipants().size();
	}
	
	public int getNetGenderBalance(){
		
		/*
		 * If return positive, it means male is more than female 
		 * If return negative, it means female is more than male
		 * If return zero, it means the numbers of male and female are the same*/
		return this.countMale() - this.countFemale();
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
	
	public int countSundaySchoolClass(String sundayClass){
		int count = 0;
		
		for (Participant participant : this.getParticipants()){
			if (StringUtils.equalsIgnoreCase(participant.getSundaySchoolClass(), sundayClass)){
				count++;
			}
		}
		
		return count;
	}
	
	public int countGroupNumber(int groupNumber){
		int count = 0;
		
		for (Participant participant : this.getParticipants()){
			if (groupNumber == participant.getGroupNumber()){
				count++;
			}
		}
		
		return count;
	}
}
