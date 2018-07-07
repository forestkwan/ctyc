package org.ctyc.mgt.model.summercamp;

public class DineTableStatistics {
	
	private int tableNumber;
	private CampName campName;
	private int day1Count;
	private int day2Count;
	private int day3Count;
	private int day4Count;
	
	public DineTableStatistics(int tableNumber, CampName campName) {
		super();
		this.tableNumber = tableNumber;
		this.campName = campName;
		this.day1Count = -1;
		this.day2Count = -1;
		this.day3Count = -1;
		this.day4Count = -1;
	}
	
	public int getTableNumber() {
		return tableNumber;
	}
	public void setTableNumber(int tableNumber) {
		this.tableNumber = tableNumber;
	}
	public CampName getCampName() {
		return campName;
	}
	public void setCampName(CampName campName) {
		this.campName = campName;
	}
	public int getDay1Count() {
		return day1Count;
	}
	public void setDay1Count(int day1Count) {
		this.day1Count = day1Count;
	}
	public int getDay2Count() {
		return day2Count;
	}
	public void setDay2Count(int day2Count) {
		this.day2Count = day2Count;
	}
	public int getDay3Count() {
		return day3Count;
	}
	public void setDay3Count(int day3Count) {
		this.day3Count = day3Count;
	}
	public int getDay4Count() {
		return day4Count;
	}
	public void setDay4Count(int day4Count) {
		this.day4Count = day4Count;
	}
}
