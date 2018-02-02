package com.cpm.aintupromoter.xmlGetterSetter;

import java.util.ArrayList;

public class NonWorkingReasonGetterSetter {
	
	String nonworking_table;
	
	ArrayList<String> reason_cd=new ArrayList<String>();
	ArrayList<String> reason=new ArrayList<String>();
	ArrayList<String> entry_allow=new ArrayList<>();

	public ArrayList<String> getIMAGE_ALLOW() {
		return IMAGE_ALLOW;
	}

	public void setIMAGE_ALLOW(String IMAGE_ALLOW) {
		this.IMAGE_ALLOW.add(IMAGE_ALLOW);
	}

	ArrayList<String> IMAGE_ALLOW =new ArrayList<>();

	public String getNonworking_table() {
		return nonworking_table;
	}
	public void setNonworking_table(String nonworking_table) {
		this.nonworking_table = nonworking_table;
	}
	public ArrayList<String> getReason_cd() {
		return reason_cd;
	}
	public void setReason_cd(String reason_cd) {
		this.reason_cd.add(reason_cd);
	}
	public ArrayList<String> getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason.add(reason);
	}

	public ArrayList<String> getEntry_allow() {
		return entry_allow;
	}

	public void setEntry_allow(String entry_allow) {
		this.entry_allow.add(entry_allow);
	}
}
