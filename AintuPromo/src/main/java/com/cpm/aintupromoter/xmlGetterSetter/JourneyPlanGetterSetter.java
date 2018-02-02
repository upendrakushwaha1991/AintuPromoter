package com.cpm.aintupromoter.xmlGetterSetter;

import java.util.ArrayList;

public class JourneyPlanGetterSetter {


	String table_journey_plan;

	ArrayList<String> store_cd = new ArrayList<String>();
	ArrayList<String> emp_cd = new ArrayList<String>();
	ArrayList<String> VISIT_DATE = new ArrayList<String>();
	ArrayList<String> store_name = new ArrayList<String>();
	ArrayList<String> geotag = new ArrayList<String>();
	ArrayList<String> LATITUDE = new ArrayList<String>();
	ArrayList<String> LOGITUDE = new ArrayList<String>();

	public ArrayList<String> getLATITUDE() {
		return LATITUDE;
	}

	public void setLATITUDE(String LATITUDE) {
		this.LATITUDE.add(LATITUDE);
	}

	public ArrayList<String> getLOGITUDE() {
		return LOGITUDE;
	}

	public void setLOGITUDE(String LOGITUDE) {
		this.LOGITUDE.add(LOGITUDE);
	}

	public ArrayList<String> getGeotag() {
		return geotag;
	}

	public void setGeotag(String geotag) {
		this.geotag.add(geotag);
	}

	public ArrayList<String> getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city.add(city);
	}

	ArrayList<String> city = new ArrayList<String>();
	ArrayList<String> uploadStatus = new ArrayList<String>();
	ArrayList<String> checkOutStatus = new ArrayList<String>();
	ArrayList<String> store_address = new ArrayList<String>();
	public ArrayList<String> getStore_address() {
		return store_address;
	}

	public void setStore_address(String store_address) {
		this.store_address.add(store_address);
	}




	public ArrayList<String> getCheckOutStatus() {
		return checkOutStatus;
	}
	public void setCheckOutStatus(String checkOutStatus) {
		this.checkOutStatus.add(checkOutStatus);
	}
	public ArrayList<String> getVISIT_DATE() {
		return VISIT_DATE;
	}
	public void setVISIT_DATE(String vISIT_DATE) {
		this.VISIT_DATE.add(vISIT_DATE);
	}
	public ArrayList<String> getStore_cd() {
		return store_cd;
	}
	public void setStore_cd(String store_cd) {
		this.store_cd.add(store_cd);
	}
	public ArrayList<String> getEmp_cd() {
		return emp_cd;
	}
	public void setEmp_cd(String emp_cd) {
		this.emp_cd.add(emp_cd);
	}
	public ArrayList<String> getStore_name() {
		return store_name;
	}
	public void setStore_name(String store_name) {
		this.store_name.add(store_name);
	}
	public ArrayList<String> getUploadStatus() {
		return uploadStatus;
	}
	public void setUploadStatus(String uploadStatus) {
		this.uploadStatus.add(uploadStatus);
	}
	public String getTable_journey_plan() {
		return table_journey_plan;
	}
	public void setTable_journey_plan(String table_journey_plan) {
		this.table_journey_plan = table_journey_plan;
	}

}
