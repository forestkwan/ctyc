package org.ctyc.mgt.model;

import java.util.Collection;
import java.util.HashSet;

public class FamilyGroup {

	private String familyId;	/* The Believer ID of the family leader */
	private Collection<Believer> believers;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((familyId == null) ? 0 : familyId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof FamilyGroup)) {
			return false;
		}
		FamilyGroup other = (FamilyGroup) obj;
		if (familyId == null) {
			if (other.familyId != null) {
				return false;
			}
		} else if (!familyId.equals(other.familyId)) {
			return false;
		}
		return true;
	}

	public FamilyGroup(String familyId){
		this.familyId = familyId;
		this.believers = new HashSet<Believer>();
	}
	
	public FamilyGroup(String familyId, Collection<Believer> believers){
		this.familyId = familyId;
		this.believers = new HashSet<Believer>();
		this.believers.addAll(believers);
	}

	public String getFamilyId() {
		return familyId;
	}

	public Collection<Believer> getBelievers() {
		if (this.believers == null){
			this.believers = new HashSet<Believer>();
		}
		return believers;
	}
}
