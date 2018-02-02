package com.cpm.aintupromoter.xmlGetterSetter;
import java.util.ArrayList;

/**
 * Created by jeevanp on 25-07-2017.
 */

public class POSMDATAGetterSetter {

    ArrayList<String> POSM_CD = new ArrayList<>();
    ArrayList<String> POSM = new ArrayList<>();

    public ArrayList<String> getSEQUENCE() {
        return SEQUENCE;
    }

    public void setSEQUENCE(String SEQUENCE) {
        this.SEQUENCE.add(SEQUENCE);
    }

    ArrayList<String> SEQUENCE = new ArrayList<>();
    public String getPosmRemark() {
        return posmRemark;
    }

    public void setPosmRemark(String posmRemark) {
        this.posmRemark = posmRemark;
    }

    public String posmRemark="";

    public ArrayList<String> getFLAG() {
        return FLAG;
    }

    public void setFLAG(String FLAG) {
        this.FLAG.add(FLAG);
    }
    ArrayList<String> FLAG = new ArrayList<>();
    public String getPosm_img() {
        return posm_img;
    }

    public void setPosm_img(String posm_img) {
        this.posm_img = posm_img;
    }

    String posm_img;

    public String getVisit_date() {
        return visit_date;
    }

    public void setVisit_date(String visit_date) {
        this.visit_date = visit_date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    String visit_date,status="N";
    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    String quantity;

    String mapping_POSM_table;

    public String getMapping_POSM_table() {
        return mapping_POSM_table;
    }

    public void setMapping_POSM_table(String mapping_POSM_table) {
        this.mapping_POSM_table = mapping_POSM_table;
    }

    public ArrayList<String> getPOSM_CD() {
        return POSM_CD;
    }

    public void setPOSM_CD(String POSM_CD) {
        this.POSM_CD.add(POSM_CD);
    }

    public ArrayList<String> getPOSM() {
        return POSM;
    }

    public void setPOSM(String POSM) {
        this.POSM.add(POSM);
    }


}
