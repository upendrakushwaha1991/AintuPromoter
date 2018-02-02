package com.cpm.aintupromoter.xmlGetterSetter;

/**
 * Created by jeevanp on 28-12-2016.
 */

public class TableBean {
    public static String jcptable;
    public static String nonworkingtable;
    public static String mappingposmtable;
    public static String getNonworkingtable() {
        return nonworkingtable;
    }
    public static void setNonworkingtable(String nonworkingtable) {
        TableBean.nonworkingtable = nonworkingtable;
    }
    public static String getJcptable() {
        return jcptable;
    }
    public static void setJcptable(String jcptable) {
        TableBean.jcptable = jcptable;
    }
    public static String getMappingposmtable() {
        return mappingposmtable;
    }

    public static void setMappingposmtable(String mappingposmtable) {
        TableBean.mappingposmtable = mappingposmtable;
    }



}
