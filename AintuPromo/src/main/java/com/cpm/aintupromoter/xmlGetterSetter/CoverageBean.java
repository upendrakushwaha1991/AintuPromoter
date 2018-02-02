package com.cpm.aintupromoter.xmlGetterSetter;

/**
 * Created by jeevanp on 25-07-2017.
 */

public class CoverageBean {


    public int getMID() {
        return MID;
    }

    public void setMID(int MID) {
        this.MID = MID;
    }

    protected int MID;

    protected String userId;
    protected String inTime;
    protected String outTime;
    protected String visitDate;
    private String latitude;
    private String longitude;
    private String reasonid = "";
    protected String Remark;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    protected String reason;

    public String getStore_id() {
        return store_id;
    }

    public void setStore_id(String store_id) {
        this.store_id = store_id;
    }

    protected String store_id;

    public String getCoverage_status() {
        return coverage_status;
    }

    public void setCoverage_status(String coverage_status) {
        this.coverage_status = coverage_status;
    }

    public String getStore_image() {
        return store_image;
    }

    public void setStore_image(String store_image) {
        this.store_image = store_image;
    }

    public String getCheckout_image() {
        return checkout_image;
    }

    public void setCheckout_image(String checkout_image) {
        this.checkout_image = checkout_image;
    }

    private String coverage_status;
    private String store_image = "";
    private String checkout_image = "";

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getInTime() {
        return inTime;
    }

    public void setInTime(String inTime) {
        this.inTime = inTime;
    }

    public String getOutTime() {
        return outTime;
    }

    public void setOutTime(String outTime) {
        this.outTime = outTime;
    }

    public String getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(String visitDate) {
        this.visitDate = visitDate;
    }
    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getReasonid() {
        return reasonid;
    }

    public void setReasonid(String reasonid) {
        this.reasonid = reasonid;
    }

}
