package com.cpm.aintupromoter.xmlGetterSetter;

import java.io.Serializable;

/**
 * Created by jeevanp on 21-12-2016.
 */

public class LoginGetterSetter implements Serializable {
    String RIGHTNAME,APP_VERSION,APP_PATH,Success,CURRENTDATE;

    public String getRIGHTNAME() {
        return RIGHTNAME;
    }

    public void setRIGHTNAME(String RIGHTNAME) {
        this.RIGHTNAME = RIGHTNAME;
    }

    public String getAPP_VERSION() {
        return APP_VERSION;
    }

    public void setAPP_VERSION(String APP_VERSION) {
        this.APP_VERSION = APP_VERSION;
    }

    public String getAPP_PATH() {
        return APP_PATH;
    }

    public void setAPP_PATH(String APP_PATH) {
        this.APP_PATH = APP_PATH;
    }

    public String getSuccess() {
        return Success;
    }

    public void setSuccess(String success) {
        Success = success;
    }

    public String getCURRENTDATE() {
        return CURRENTDATE;
    }

    public void setCURRENTDATE(String CURRENTDATE) {
        this.CURRENTDATE = CURRENTDATE;
    }
/*   String RIGHT_NAME, APP_VERSION, APP_PATH, CURRENTDATE;

    ArrayList<String> CULTURE_ID = new ArrayList<>();
    ArrayList<String> CULTURE_NAME = new ArrayList<>();
    ArrayList<String> NOTICE_URL = new ArrayList<>();


    public String getRIGHT_NAME() {
        return RIGHT_NAME;
    }

    public void setRIGHT_NAME(String rIGHT_NAME) {
        RIGHT_NAME = rIGHT_NAME;
    }

    public String getAPP_VERSION() {
        return APP_VERSION;
    }

    public void setAPP_VERSION(String aPP_VERSION) {
        APP_VERSION = aPP_VERSION;
    }

    public String getAPP_PATH() {
        return APP_PATH;
    }

    public void setAPP_PATH(String aPP_PATH) {
        APP_PATH = aPP_PATH;
    }

    public String getCURRENTDATE() {
        return CURRENTDATE;
    }

    public void setCURRENTDATE(String cURRENTDATE) {
        CURRENTDATE = cURRENTDATE;
    }

    public ArrayList<String> getCULTURE_ID() {
        return CULTURE_ID;
    }

    public void setCULTURE_ID(String CULTURE_ID) {
        this.CULTURE_ID.add(CULTURE_ID);
    }

    public ArrayList<String> getCULTURE_NAME() {
        return CULTURE_NAME;
    }

    public void setCULTURE_NAME(String CULTURE_NAME) {
        this.CULTURE_NAME.add(CULTURE_NAME);
    }

    public ArrayList<String> getNOTICE_URL() {
        return NOTICE_URL;
    }

    public void setNOTICE_URL(String NOTICE_URL) {
        this.NOTICE_URL.add(NOTICE_URL);
    }*/
}