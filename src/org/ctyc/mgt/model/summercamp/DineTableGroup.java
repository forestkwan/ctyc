package org.ctyc.mgt.model.summercamp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

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
}
