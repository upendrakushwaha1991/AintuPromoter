package com.cpm.aintupromoter.constants;

import android.os.Environment;

/**
 * Created by jeevanp on 24-07-2016.
 */

public class CommonString {
    public static final String FILE_PATH = Environment.getExternalStorageDirectory() + "/AintuPromoter_Images/";
    // preferenec keys
    public static final String KEY_STATUS = "STATUS";
    public static final String METHOD_UPLOAD_COVERAGE_REMOVE = "UPLOAD_COVERAGE_REMOVE";
    public static final String MEHTOD_UPLOAD_COVERAGE_STATUS = "UploadCoverage_Status";
    public static final String METHOD_UPLOAD_DR_STORE_COVERAGE = "UPLOAD_COVERAGENEW";
    public static final String METHOD_CHECKOUT_STATUS = "Upload_Store_ChecOut_Status";
    public static final String ONBACK_ALERT_MESSAGE = "Unsaved data will be lost - Do you want to continue?";
    public static final String METHOD_UPLOAD_XML = "DrUploadXml";
    public static final String SOAP_ACTION = "http://tempuri.org/";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_DATE = "date";
    public static final String KEY_STOREVISITED_STATUS = "STOREVISITED_STATUS";
    public static final String KEY_PATH = "path";
    public static final String KEY_MID = "MID";
    public static final String KEY_VERSION = "APP_VERSION";
        public static final String KEY_NOTICE_BOARD_LINK = "NOTICE_BOARD_LINK";
    public static final String KEY_STORE_ID = "STORE_ID";
        public static final String KEY_STORE_NAME = "STORE_NAME";
    public static final String KEY_STORE_CD = "STORE_CD";
    public static final String KEY_CHECKOUT_STATUS = "CHECKOUT_STATUS";
    public static final String KEY_STORE_IN_TIME = "STORE_IN_TIME";
    public static final String KEY_USER_TYPE = "RIGHTNAME";
    public static final String KEY_USER_ID = "USER_ID";
    public static final String KEY_IN_TIME = "IN_TIME";
    public static final String KEY_OUT_TIME = "OUT_TIME";
    public static final String KEY_LATITUDE = "LATITUDE";
    public static final String KEY_LONGITUDE = "LONGITUDE";
    public static final String KEY_COVERAGE_STATUS = "Coverage";
    public static final String KEY_REASON_ID = "REASON_ID";
    public static final String KEY_REASON = "REASON";
    public static final String KEY_COVERAGE_REMARK = "REMARK";
    public static final String KEY_CHECKOUT_IMAGE = "Checkout_Image";
    public static final String KEY_INTIME_IMAGE = "INTIME_IMAGE";
    public static final String KEY_ID = "KEY_ID";
    public static final String KEY_VISIT_DATE = "VISIT_DATE";
    public static final String KEY_P = "P";
    public static final String KEY_D = "D";
    public static final String KEY_U = "U";
    public static final String KEY_C = "Y";
    public static final String KEY_INVALID = "INVALID";
    public static final String STORE_STATUS_LEAVE = "L";
    public static final String KEY_VALID = "Valid";
    public static final String DATA_DELETE_ALERT_MESSAGE = "Saved data will be lost - Do you want to continue?";
       // webservice constants
    public static final String KEY_SUCCESS = "Success";
    public static final String KEY_FAILURE = "Failure";
    public static final String KEY_FALSE = "False";
    public static final String KEY_CHANGED = "Changed";
    public static final String URL = "http://aintu.parinaam.in/Aintuwebservice.asmx";
    public static final String URLFORRETROFIT = "http://aintu.parinaam.in/Aintuwebservice.asmx/";
    public static final String NAMESPACE = "http://tempuri.org/";
    public static final String METHOD_LOGIN = "UserLoginDetail";
    public static final String SOAP_ACTION_LOGIN = "http://tempuri.org/" + METHOD_LOGIN;
    public static final String METHOD_NAME_UNIVERSAL_DOWNLOAD = "Download_Universal";
    public static final String SOAP_ACTION_UNIVERSAL = "http://tempuri.org/" + METHOD_NAME_UNIVERSAL_DOWNLOAD;
    public static final String MESSAGE_FAILURE = "Server Error.Please Access After Some Time";
    public static final String MESSAGE_FALSE = "Invalid User";
    public static final String MESSAGE_CHANGED = "Invalid UserId Or Password / Password Has Been Changed.";
    public static final String MESSAGE_EXCEPTION = "Problem Occured : Report The Problem To Parinaam ";
    public static final String MESSAGE_SOCKETEXCEPTION = "Network Communication Failure. Check Your Network Connection";
    public static final String TABLE_COVERAGE_DATA = "COVERAGE_DATA";
    public static final String CREATE_TABLE_COVERAGE_DATA = "CREATE TABLE  IF NOT EXISTS " + TABLE_COVERAGE_DATA
            + " ("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + KEY_STORE_ID + " VARCHAR,"
            + KEY_USER_ID + " VARCHAR,"
            + KEY_IN_TIME + " VARCHAR,"
            + KEY_OUT_TIME + " VARCHAR,"
            + KEY_VISIT_DATE + " VARCHAR,"
            + KEY_LATITUDE + " VARCHAR,"
            + KEY_LONGITUDE + " VARCHAR,"
            + KEY_COVERAGE_STATUS + " VARCHAR,"
            + KEY_INTIME_IMAGE + " VARCHAR,"
            + KEY_CHECKOUT_IMAGE + " VARCHAR,"
            + KEY_COVERAGE_REMARK + " VARCHAR,"
            + KEY_REASON_ID + " VARCHAR,"
            + KEY_REASON + " VARCHAR)";

    public static final String TABLE_INSERT_POSM_DATA = "POSM_data";
    public static final String TABLE_INSERT_DEMOS_DATA = "DEMOS_data";
    public static final String CREATE_TABLE_INSERT_DEMOS_DATA = "CREATE TABLE IF NOT EXISTS "
            + TABLE_INSERT_DEMOS_DATA
            + "("
            + "KEY_ID"
            + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + "NAME"
            + " VARCHAR,"
            + "GENDER"
            + " VARCHAR,"
            + "APPLICATION_DEMO"
            + " VARCHAR,"
            + "DOWNLODED"
            + " VARCHAR,"
            + "AVAILED_OFFER"
            + " VARCHAR,"
            + "MOBILE"
            + " INTEGER,"
            + "REMARK"
            + " VARCHAR,"
            + "FLAG"
            + " VARCHAR,"

            + "VISIT_DATE"
            + " VARCHAR,"
            + "STATUS"
            + " VARCHAR,"
            + "USER_ID"
            + " VARCHAR,"

            + "DEDICATED_PHONE"
            + " VARCHAR,"
            + "DEDICATED_PHONE_REMARK"
            + " VARCHAR,"

            + "STORE_NETWORK_ISSUE"
            + " VARCHAR,"
            + "STORE_NETWORK_ISSUE_REMARK"
            + " VARCHAR,"
            + "BRAND_STOCK_REMARK"
            + " VARCHAR,"
            + "ASSET_REMARK"
            + " VARCHAR,"



            + "STORE_CD" + " INTEGER)";


    public static final String CREATE_TABLE_INSERT_POSM_DATA = "CREATE TABLE IF NOT EXISTS "
            + TABLE_INSERT_POSM_DATA
            + "("
            + "KEY_ID"
            + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + "POSM"
            + " VARCHAR,"
            + "POSM_CD"
            + " INTEGER,"
            + "QUANTITY"
            + " INTEGER,"
            + "POSM_IMG"
            + " VARCHAR,"
            + "USER_ID"
            + " VARCHAR,"
            + "VISIT_DATE"
            + " VARCHAR,"
            + "FLAG"
            + " VARCHAR,"
            + "REMARK"
            + " VARCHAR,"
            + "STATUS"
            + " VARCHAR,"
            + "STORE_CD" + " INTEGER)";

    public static final String TABLE_STORE_GEOTAGGING = "STORE_GEOTAGGING";
    public static final String CREATE_TABLE_STORE_GEOTAGGING = "CREATE TABLE IF NOT EXISTS "
            + TABLE_STORE_GEOTAGGING
            + " ("
            + "KEY_ID"
            + " INTEGER PRIMARY KEY AUTOINCREMENT ,"

            + "STORE_ID"
            + " VARCHAR,"

            + "LATITUDE"
            + " VARCHAR,"

            + "LONGITUDE"
            + " VARCHAR,"

            + "GEO_TAG"
            + " VARCHAR,"

            + "STATUS"
            + " VARCHAR,"

            + "FRONT_IMAGE" + " VARCHAR)";

    public static final String KEY_TRAINING_MODE_CD = "TRAINING_MODE_CD";
    public static final String KEY_MANAGED = "MANAGED";
}