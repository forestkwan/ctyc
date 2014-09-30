package org.ctyc.mgt.model.summercamp;

import java.io.Serializable;

public class CanteenTable implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int number;
	private int capacity;
	
	public CanteenTable(int number, int capacity){
		this.number = number;
		this.capacity = capacity;
	}
	
	public int getNumber() {
		return number;
	}
	
	public int getCapacity() {
		return capacity;
	}
}
