package org.ctyc.mgt.model.summercamp;

import java.io.Serializable;

public class CanteenTable implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int number;
	private int capacity;
	private String location;

	public CanteenTable(int number, int capacity, String location){
		this.number = number;
		this.capacity = capacity;
		this.location = location;
	}
	
	public int getNumber() {
		return number;
	}
	
	public int getCapacity() {
		return capacity;
	}
	
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
}
