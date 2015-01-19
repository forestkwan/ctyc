package org.ctyc.mgt.model.summercamp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.ctyc.mgt.model.Gender;

public class DineTableGroup implements Serializable{

	private static final long serialVersionUID = 5189900740930218925L;
	private int tableNumber;
	private Collection<Participant> participants;
	private double cost;
	
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
	
	public int getNoOfGroupMentor(){
		int count = 0;
		for (Participant participant : this.getParticipants()){
			if (participant.isGroupMentor()){
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
}
