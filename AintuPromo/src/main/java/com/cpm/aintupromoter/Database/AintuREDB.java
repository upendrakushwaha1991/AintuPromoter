package com.cpm.aintupromoter.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.cpm.aintupromoter.GetterSetter.GeotaggingBeans;
import com.cpm.aintupromoter.constants.CommonString;
import com.cpm.aintupromoter.xmlGetterSetter.CoverageBean;
import com.cpm.aintupromoter.xmlGetterSetter.DemosGetterSetter;
import com.cpm.aintupromoter.xmlGetterSetter.JourneyPlanGetterSetter;
import com.cpm.aintupromoter.xmlGetterSetter.NonWorkingReasonGetterSetter;
import com.cpm.aintupromoter.xmlGetterSetter.POSMDATAGetterSetter;
import com.cpm.aintupromoter.xmlGetterSetter.TableBean;

import java.util.ArrayList;

/**
 * Created by jeevanp on 25-07-2017.
 */
public class AintuREDB extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "AINTUPROMOTER_DATABASE_3";
    public static final int DATABASE_VERSION = 3;
    private SQLiteDatabase db;
    Context context;

    public AintuREDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public void open() {
        try {
            db = this.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(TableBean.getJcptable());
            db.execSQL(TableBean.getMappingposmtable());
            db.execSQL(TableBean.getNonworkingtable());
            db.execSQL(CommonString.CREATE_TABLE_COVERAGE_DATA);
            db.execSQL(CommonString.CREATE_TABLE_INSERT_POSM_DATA);
            db.execSQL(CommonString.CREATE_TABLE_INSERT_DEMOS_DATA);

            //usk
            db.execSQL(CommonString.CREATE_TABLE_STORE_GEOTAGGING);
        } catch (SQLException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error -" + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteSpecificStoreData(String storeid) {
        db.delete(CommonString.TABLE_COVERAGE_DATA, CommonString.KEY_STORE_ID + "='" + storeid + "'", null);
        db.delete(CommonString.TABLE_INSERT_POSM_DATA, CommonString.KEY_STORE_CD + "='" + storeid + "'", null);
        db.delete(CommonString.TABLE_INSERT_DEMOS_DATA, CommonString.KEY_STORE_CD + "='" + storeid + "'", null);
       // db.delete(CommonString.TABLE_STORE_GEOTAGGING, CommonString.KEY_STORE_ID + "='" + storeid + "'", null);
    }


    public void deleteAllTables() {
        db.delete(CommonString.TABLE_COVERAGE_DATA, null, null);
        db.delete(CommonString.TABLE_INSERT_POSM_DATA, null, null);
        db.delete(CommonString.TABLE_INSERT_DEMOS_DATA, null, null);

    }

    public long updateStoreStatusOnLeave(String storeid, String visitdate, String status) {
        long l = 0;
        try {
            ContentValues values = new ContentValues();
            values.put("UPLOAD_STATUS", status);
            l = db.update("JOURNEY_PLAN", values, CommonString.KEY_STORE_CD + "='" + storeid + "' AND " + CommonString.KEY_VISIT_DATE + "='" + visitdate + "'", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return l;
    }

    /// get store Status
    public JourneyPlanGetterSetter getStoreStatus(String id) {

        Log.d("FetchingStoredata--------------->Start<------------",
                "------------------");

        JourneyPlanGetterSetter sb = new JourneyPlanGetterSetter();

        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("SELECT  * from  JOURNEY_PLAN" + "  WHERE STORE_CD = '" + id + "'", null);
            if (dbcursor != null) {
                int numrows = dbcursor.getCount();
                dbcursor.moveToFirst();
                for (int i = 0; i < numrows; i++) {
                    sb.setStore_cd(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_STORE_CD)));
                    sb.setCheckOutStatus((dbcursor.getString(dbcursor.getColumnIndexOrThrow("CHECKOUT_STATUS"))));
                    sb.setUploadStatus(dbcursor.getString(dbcursor.getColumnIndexOrThrow("UPLOAD_STATUS")));
                    dbcursor.moveToNext();
                }
                dbcursor.close();

            }

        } catch (Exception e) {
            Log.d("Exception when fetching Records!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());
        }

        Log.d("FetchingStoredat---------------------->Stop<-----------",
                "-------------------");
        return sb;

    }


    public long updateflagevalueinDemosData(String common_id, String flag) {
        long l = 0;
        try {
            ContentValues values = new ContentValues();
            values.put("FLAG", flag);
            l = db.update(CommonString.TABLE_INSERT_DEMOS_DATA, values, " KEY_ID = " + common_id, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return l;
    }

    public long updateCoverageCheckoutstoreimage(String store_cd, String checkout_image) {
        long l = 0;
        try {
            ContentValues values = new ContentValues();
            values.put(CommonString.KEY_CHECKOUT_IMAGE, checkout_image);
            l = db.update(CommonString.TABLE_COVERAGE_DATA, values, CommonString.KEY_STORE_ID + "=" + store_cd, null);
        } catch (Exception e) {

        }
        return l;
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertJCPData(JourneyPlanGetterSetter data) {
        db.delete("JOURNEY_PLAN", null, null);
        ContentValues values = new ContentValues();
        try {
            for (int i = 0; i < data.getStore_cd().size(); i++) {
                values.put("STORE_CD", Integer.parseInt(data.getStore_cd().get(i)));
                values.put("EMP_CD", Integer.parseInt(data.getEmp_cd().get(i)));
                values.put("VISIT_DATE", data.getVISIT_DATE().get(i));
                values.put("STORE_NAME", data.getStore_name().get(i));
                values.put("STORE_ADDRESS", data.getStore_address().get(i));
                values.put("CITY", data.getCity().get(i));
                values.put("UPLOAD_STATUS", data.getUploadStatus().get(i));
                values.put("CHECKOUT_STATUS", data.getCheckOutStatus().get(i));
                values.put("GEOTAG", data.getGeotag().get(i));
                values.put("LATITUDE", data.getLATITUDE().get(i));
                values.put("LOGITUDE", data.getLOGITUDE().get(i));
                db.insert("JOURNEY_PLAN", null, values);


            }

        } catch (Exception ex) {
            Log.d("Database Exception while Insert JCP Data ",
                    ex.toString());
        }

    }

    public void updateCoverageStatus(int mid, String status) {
        try {
            ContentValues values = new ContentValues();
            values.put(CommonString.KEY_COVERAGE_STATUS, status);
            db.update(CommonString.TABLE_COVERAGE_DATA, values, CommonString.KEY_ID + "=" + mid, null);
        } catch (Exception e) {

        }
    }

    public ArrayList<JourneyPlanGetterSetter> getAllJCPData() {
        Log.d("FetchingStoredata--------------->Start<------------",
                "------------------");
        ArrayList<JourneyPlanGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("SELECT * from JOURNEY_PLAN ", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    JourneyPlanGetterSetter sb = new JourneyPlanGetterSetter();
                    sb.setStore_cd(dbcursor.getString(dbcursor.getColumnIndexOrThrow("STORE_CD")));
                    sb.setStore_name((dbcursor.getString(dbcursor.getColumnIndexOrThrow("STORE_NAME"))));
                    sb.setEmp_cd((dbcursor.getString(dbcursor.getColumnIndexOrThrow("EMP_CD"))));
                    sb.setVISIT_DATE((dbcursor.getString(dbcursor.getColumnIndexOrThrow("VISIT_DATE"))));
                    sb.setStore_name((dbcursor.getString(dbcursor.getColumnIndexOrThrow("STORE_NAME"))));
                    sb.setStore_address((dbcursor.getString(dbcursor.getColumnIndexOrThrow("STORE_ADDRESS"))));
                    sb.setCity((dbcursor.getString(dbcursor.getColumnIndexOrThrow("CITY"))));
                    sb.setUploadStatus((dbcursor.getString(dbcursor.getColumnIndexOrThrow("UPLOAD_STATUS"))));
                    sb.setCheckOutStatus((dbcursor.getString(dbcursor.getColumnIndexOrThrow("CHECKOUT_STATUS"))));
                    sb.setGeotag((dbcursor.getString(dbcursor.getColumnIndexOrThrow("GEOTAG"))));
                    sb.setLOGITUDE((dbcursor.getString(dbcursor.getColumnIndexOrThrow("LOGITUDE"))));
                    sb.setLATITUDE((dbcursor.getString(dbcursor.getColumnIndexOrThrow("LATITUDE"))));


                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching JCP!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());
            return list;
        }

        Log.d("FetchingJCP data---------------------->Stop<-----------",
                "-------------------");
        return list;

    }


    //get JCP Data
    public ArrayList<JourneyPlanGetterSetter> getJCPData(String date) {

        Log.d("FetchingStoredata--------------->Start<------------",
                "------------------");
        ArrayList<JourneyPlanGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("SELECT * from JOURNEY_PLAN where VISIT_DATE = '" + date + "'"
                    , null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    JourneyPlanGetterSetter sb = new JourneyPlanGetterSetter();
                    sb.setStore_cd(dbcursor.getString(dbcursor.getColumnIndexOrThrow("STORE_CD")));
                    sb.setStore_name((dbcursor.getString(dbcursor.getColumnIndexOrThrow("STORE_NAME"))));
                    sb.setEmp_cd((dbcursor.getString(dbcursor.getColumnIndexOrThrow("EMP_CD"))));
                    sb.setVISIT_DATE((dbcursor.getString(dbcursor.getColumnIndexOrThrow("VISIT_DATE"))));
                    sb.setStore_name((dbcursor.getString(dbcursor.getColumnIndexOrThrow("STORE_NAME"))));
                    sb.setStore_address((dbcursor.getString(dbcursor.getColumnIndexOrThrow("STORE_ADDRESS"))));
                    sb.setCity((dbcursor.getString(dbcursor.getColumnIndexOrThrow("CITY"))));
                    sb.setUploadStatus((dbcursor.getString(dbcursor.getColumnIndexOrThrow("UPLOAD_STATUS"))));
                    sb.setCheckOutStatus((dbcursor.getString(dbcursor.getColumnIndexOrThrow("CHECKOUT_STATUS"))));
                    sb.setGeotag((dbcursor.getString(dbcursor.getColumnIndexOrThrow("GEOTAG"))));
                    sb.setLOGITUDE((dbcursor.getString(dbcursor.getColumnIndexOrThrow("LOGITUDE"))));
                    sb.setLATITUDE((dbcursor.getString(dbcursor.getColumnIndexOrThrow("LATITUDE"))));


                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching JCP!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());
            return list;
        }

        Log.d("FetchingJCP data---------------------->Stop<-----------",
                "-------------------");
        return list;

    }


    public ArrayList<JourneyPlanGetterSetter> getJCPstoreId(String store_id) {

        Log.d("FetchingStoredata--------------->Start<------------",
                "------------------");
        ArrayList<JourneyPlanGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("SELECT * from JOURNEY_PLAN where STORE_CD = '" + store_id + "'"
                    , null);



            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    JourneyPlanGetterSetter sb = new JourneyPlanGetterSetter();
                    sb.setStore_cd(dbcursor.getString(dbcursor.getColumnIndexOrThrow("STORE_CD")));
                    sb.setStore_name((dbcursor.getString(dbcursor.getColumnIndexOrThrow("STORE_NAME"))));
                    sb.setEmp_cd((dbcursor.getString(dbcursor.getColumnIndexOrThrow("EMP_CD"))));
                    sb.setVISIT_DATE((dbcursor.getString(dbcursor.getColumnIndexOrThrow("VISIT_DATE"))));
                    sb.setStore_name((dbcursor.getString(dbcursor.getColumnIndexOrThrow("STORE_NAME"))));
                    sb.setStore_address((dbcursor.getString(dbcursor.getColumnIndexOrThrow("STORE_ADDRESS"))));
                    sb.setCity((dbcursor.getString(dbcursor.getColumnIndexOrThrow("CITY"))));
                    sb.setUploadStatus((dbcursor.getString(dbcursor.getColumnIndexOrThrow("UPLOAD_STATUS"))));
                    sb.setCheckOutStatus((dbcursor.getString(dbcursor.getColumnIndexOrThrow("CHECKOUT_STATUS"))));
                    sb.setGeotag((dbcursor.getString(dbcursor.getColumnIndexOrThrow("GEOTAG"))));
                    sb.setLOGITUDE((dbcursor.getString(dbcursor.getColumnIndexOrThrow("LOGITUDE"))));
                    sb.setLATITUDE((dbcursor.getString(dbcursor.getColumnIndexOrThrow("LATITUDE"))));


                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching JCP!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());
            return list;
        }

        Log.d("FetchingJCP data---------------------->Stop<-----------",
                "-------------------");
        return list;

    }

    public void insertPOSMata(POSMDATAGetterSetter data) {
        db.delete("POSM_MASTER", null, null);
        ContentValues values = new ContentValues();
        try {
            for (int i = 0; i < data.getPOSM_CD().size(); i++) {
                values.put("POSM_CD", Integer.parseInt(data.getPOSM_CD().get(i)));
                values.put("POSM", data.getPOSM().get(i));
                values.put("FLAG_MANDATORY", data.getFLAG().get(i));
                values.put("SEQUENCE", data.getSEQUENCE().get(i));

                db.insert("POSM_MASTER", null, values);
            }

        } catch (Exception ex) {
            Log.d("Database Exception while Insert Mapping Data ", ex.toString());
        }
    }


    public long updateposmStatus(String storeid, String visitdate, String status) {
        long l = 0;
        try {
            ContentValues values = new ContentValues();
            values.put("STATUS", status);
            l = db.update("POSM_data", values, CommonString.KEY_STORE_CD + "='" + storeid + "' AND VISIT_DATE ='" + visitdate + "'", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return l;
    }

    public long insertposmdatawithposmimg(String user_name, String store_cd, String visit_date, ArrayList<POSMDATAGetterSetter> list) {
        db.delete(CommonString.TABLE_INSERT_POSM_DATA, "STORE_CD" + "='" + store_cd + "'AND VISIT_DATE ='" + visit_date + "'", null);
        long l = 0;
        ContentValues values = new ContentValues();
        try {
            for (int i = 0; i < list.size(); i++) {
                values.put("STORE_CD", store_cd);
                values.put("VISIT_DATE", visit_date);
                values.put("USER_ID", user_name);
                values.put("STATUS", "N");
                values.put("REMARK", list.get(i).getPosmRemark());
                values.put("FLAG", list.get(i).getFLAG().get(0));
                values.put("POSM_IMG", list.get(i).getPosm_img());
                values.put("QUANTITY", list.get(i).getQuantity());
                values.put("QUANTITY", list.get(i).getQuantity());
                values.put("POSM_CD", list.get(i).getPOSM_CD().get(0));
                values.put("POSM", list.get(i).getPOSM().get(0));
                l = db.insert(CommonString.TABLE_INSERT_POSM_DATA, null, values);
            }
        } catch (Exception ex) {
            Log.d("Database Exception while Insert Facing Competition Data ", ex.toString());
        }
        return l;
    }

    public void remove(String user_id) {
        db.execSQL("DELETE FROM DEMOS_data WHERE KEY_ID = '" + user_id + "'");
    }


    public long updateDemosDataStatusByStoreid(String storeid, String visit_date, String status) {
        long l = 0;
        try {
            ContentValues values = new ContentValues();
            values.put("STATUS", status);
            l = db.update(CommonString.TABLE_INSERT_DEMOS_DATA, values, CommonString.KEY_STORE_CD + "='" + storeid + "' AND VISIT_DATE ='" + visit_date + "'", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return l;
    }


    public long updateDemosDataStatus(String storeid, String key_id, String status) {
        long l = 0;
        try {
            ContentValues values = new ContentValues();
            values.put("STATUS", status);
            l = db.update(CommonString.TABLE_INSERT_DEMOS_DATA, values, CommonString.KEY_STORE_CD + "='" + storeid + "' AND KEY_ID ='" + key_id + "'", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return l;
    }


    public long insertDemosdata(String store_cd, String user_name, String visit_date, ArrayList<DemosGetterSetter> list) {
        db.delete(CommonString.TABLE_INSERT_DEMOS_DATA, "STORE_CD" + "='" + store_cd + "'AND VISIT_DATE='" + visit_date + "'", null);
        long l = 0;
        ContentValues values = new ContentValues();
        try {
            for (int i = 0; i < list.size(); i++) {
                values.put("STORE_CD", store_cd);
                values.put("USER_ID", user_name);
                values.put("FLAG", list.get(i).getFlag());
                values.put("VISIT_DATE", visit_date);
                values.put("STATUS", list.get(i).getStatus());
                values.put("NAME", list.get(i).getName());
                values.put("GENDER", list.get(i).getGender());
                values.put("APPLICATION_DEMO", list.get(i).getApp_demosG());
                values.put("DOWNLODED", list.get(i).getDownloded());
                values.put("AVAILED_OFFER", list.get(i).getAvailed_offer());
                values.put("MOBILE", list.get(i).getMobile_no());
                values.put("REMARK", list.get(i).getRemark());

                values.put("DEDICATED_PHONE", list.get(i).getDedicated_phone());
                values.put("DEDICATED_PHONE_REMARK", list.get(i).getDedicated_remark());
                values.put("STORE_NETWORK_ISSUE", list.get(i).getStore_network());
                values.put("STORE_NETWORK_ISSUE_REMARK", list.get(i).getStore_network_remark());
                values.put("BRAND_STOCK_REMARK", list.get(i).getBrand_s_remark());
                values.put("ASSET_REMARK", list.get(i).getAsset_remark());

                l = db.insert(CommonString.TABLE_INSERT_DEMOS_DATA, null, values);
            }


        } catch (Exception ex) {
            Log.d("Database Exception while Insert Facing Competition Data ",
                    ex.toString());
        }
        return l;
    }

    public ArrayList<DemosGetterSetter> getinsertedDemosdata(String store_id) {
        ArrayList<DemosGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT  * from " + CommonString.TABLE_INSERT_DEMOS_DATA + " where " + CommonString.KEY_STORE_CD + "='" + store_id + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    DemosGetterSetter sb = new DemosGetterSetter();
                    sb.setName(((dbcursor.getString(dbcursor.getColumnIndexOrThrow("NAME")))));
                    sb.setGender((((dbcursor.getString(dbcursor.getColumnIndexOrThrow("GENDER"))))));
                    sb.setApp_demosG(((dbcursor.getString(dbcursor.getColumnIndexOrThrow("APPLICATION_DEMO")))));
                    sb.setDownloded(((dbcursor.getString(dbcursor.getColumnIndexOrThrow("DOWNLODED")))));
                    sb.setAvailed_offer((((dbcursor.getString(dbcursor.getColumnIndexOrThrow("AVAILED_OFFER"))))));
                    sb.setMobile_no((((dbcursor.getString(dbcursor.getColumnIndexOrThrow("MOBILE"))))));
                    sb.setRemark((((dbcursor.getString(dbcursor.getColumnIndexOrThrow("REMARK"))))));
                    sb.setId(dbcursor.getString(dbcursor.getColumnIndexOrThrow("KEY_ID")));
                    sb.setFlag(dbcursor.getString(dbcursor.getColumnIndexOrThrow("FLAG")));
                    sb.setStatus(dbcursor.getString(dbcursor.getColumnIndexOrThrow("STATUS")));
                    sb.setVisit_date(dbcursor.getString(dbcursor.getColumnIndexOrThrow("VISIT_DATE")));


                    sb.setDedicated_phone(dbcursor.getString(dbcursor.getColumnIndexOrThrow("DEDICATED_PHONE")));
                    sb.setDedicated_remark(dbcursor.getString(dbcursor.getColumnIndexOrThrow("DEDICATED_PHONE_REMARK")));
                    sb.setStore_network(dbcursor.getString(dbcursor.getColumnIndexOrThrow("STORE_NETWORK_ISSUE")));
                    sb.setStore_network_remark(dbcursor.getString(dbcursor.getColumnIndexOrThrow("STORE_NETWORK_ISSUE_REMARK")));
                    sb.setBrand_s_remark(dbcursor.getString(dbcursor.getColumnIndexOrThrow("BRAND_STOCK_REMARK")));
                    sb.setAsset_remark(dbcursor.getString(dbcursor.getColumnIndexOrThrow("ASSET_REMARK")));


                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching Coverage Data!!!!!!!!!!!!!!!!!!!!!", e.toString());
        }
        return list;

    }


    public ArrayList<POSMDATAGetterSetter> getinsertedposmdata(String store_id) {
        ArrayList<POSMDATAGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT  * from " + CommonString.TABLE_INSERT_POSM_DATA + " where " + CommonString.KEY_STORE_CD + "='" + store_id + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    POSMDATAGetterSetter sb = new POSMDATAGetterSetter();
                    sb.setPosm_img(dbcursor.getString(dbcursor.getColumnIndexOrThrow("POSM_IMG")));
                    sb.setQuantity(dbcursor.getString(dbcursor.getColumnIndexOrThrow("QUANTITY")));
                    sb.setPOSM_CD(dbcursor.getString(dbcursor.getColumnIndexOrThrow("POSM_CD")));
                    sb.setPOSM(dbcursor.getString(dbcursor.getColumnIndexOrThrow("POSM")));
                    sb.setStatus(dbcursor.getString(dbcursor.getColumnIndexOrThrow("STATUS")));
                    sb.setVisit_date(dbcursor.getString(dbcursor.getColumnIndexOrThrow("VISIT_DATE")));
                    sb.setPosmRemark(dbcursor.getString(dbcursor.getColumnIndexOrThrow("REMARK")));
                    sb.setFLAG(dbcursor.getString(dbcursor.getColumnIndexOrThrow("FLAG")));
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching Coverage Data!!!!!!!!!!!!!!!!!!!!!", e.toString());
        }
        return list;

    }


    public ArrayList<POSMDATAGetterSetter> getPOSMDATA() {
        //Log.d("FetchingStoredata--------------->Start<------------", "------------------");
        ArrayList<POSMDATAGetterSetter> list1 = new ArrayList<POSMDATAGetterSetter>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT  distinct * from POSM_MASTER ORDER BY SEQUENCE", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    POSMDATAGetterSetter crowndata = new POSMDATAGetterSetter();
                    crowndata.setPOSM_CD(dbcursor.getString(dbcursor.getColumnIndexOrThrow("POSM_CD")));
                    crowndata.setPOSM(dbcursor.getString(dbcursor.getColumnIndexOrThrow("POSM")));
                    crowndata.setFLAG(dbcursor.getString(dbcursor.getColumnIndexOrThrow("FLAG_MANDATORY")));
                    crowndata.setPosmRemark("");
                    crowndata.setQuantity("");
                    crowndata.setPosm_img("");
                    list1.add(crowndata);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list1;
            }

        } catch (Exception e) {
            return list1;
        }


        return list1;

    }


    public long updateCoverageStoreOutTime(String StoreId, String VisitDate, String status) {
        long l = 0;
        try {
            ContentValues values = new ContentValues();
            values.put(CommonString.KEY_COVERAGE_STATUS, status);
            l = db.update(CommonString.TABLE_COVERAGE_DATA, values,
                    CommonString.KEY_STORE_ID + "='" + StoreId + "' AND " + CommonString.KEY_VISIT_DATE + "='" + VisitDate + "'", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return l;
    }


    public long updateStoreStatusOnCheckout(String storeid, String visitdate, String status) {
        long l = 0;
        try {
            ContentValues values = new ContentValues();
            values.put(CommonString.KEY_CHECKOUT_STATUS, status);
            l = db.update("JOURNEY_PLAN", values, CommonString.KEY_STORE_CD + "='" + storeid + "' AND VISIT_DATE ='" + visitdate + "'", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return l;
    }


    public void insertNonWorkingReasonData(NonWorkingReasonGetterSetter data) {
        db.delete("NON_WORKING_REASON", null, null);
        ContentValues values = new ContentValues();

        try {

            for (int i = 0; i < data.getReason_cd().size(); i++) {
                values.put("REASON_CD", Integer.parseInt(data.getReason_cd().get(i)));
                values.put("REASON", data.getReason().get(i));
                values.put("ENTRY_ALLOW", data.getEntry_allow().get(i));
                values.put("IMAGE_ALLOW", data.getIMAGE_ALLOW().get(i));


                db.insert("NON_WORKING_REASON", null, values);

            }

        } catch (Exception ex) {
            Log.d("Database Exception while Insert Non Working Data ",
                    ex.toString());
        }

    }


    public long InsertCoverageData(CoverageBean data) {
        long l = 0;
        ContentValues values = new ContentValues();
        try {
            values.put(CommonString.KEY_STORE_ID, data.getStore_id());
            values.put(CommonString.KEY_USER_ID, data.getUserId());
            values.put(CommonString.KEY_IN_TIME, data.getInTime());
            values.put(CommonString.KEY_OUT_TIME, data.getOutTime());
            values.put(CommonString.KEY_VISIT_DATE, data.getVisitDate());
            values.put(CommonString.KEY_LATITUDE, data.getLatitude());
            values.put(CommonString.KEY_LONGITUDE, data.getLongitude());
            values.put(CommonString.KEY_REASON_ID, data.getReasonid());
            values.put(CommonString.KEY_REASON, data.getReason());
            values.put(CommonString.KEY_COVERAGE_STATUS, data.getCoverage_status());
            values.put(CommonString.KEY_INTIME_IMAGE, data.getStore_image());
            values.put(CommonString.KEY_CHECKOUT_IMAGE, data.getCheckout_image());
            values.put(CommonString.KEY_COVERAGE_REMARK, data.getRemark());
            l = db.insert(CommonString.TABLE_COVERAGE_DATA, null, values);
        } catch (Exception ex) {
            Log.d("Database Exception while Insert Closes Data ",
                    ex.toString());
        }
        return l;
    }


    public ArrayList<CoverageBean> getCoverageSpecificData(String store_id) {
        ArrayList<CoverageBean> list = new ArrayList<CoverageBean>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT  * from " + CommonString.TABLE_COVERAGE_DATA + " where " + CommonString.KEY_STORE_ID + "='" + store_id + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    CoverageBean sb = new CoverageBean();
                    sb.setStore_id(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_STORE_ID)));
                    sb.setUserId((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_USER_ID))));
                    sb.setInTime(((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IN_TIME)))));
                    sb.setOutTime(((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_OUT_TIME)))));
                    sb.setVisitDate((((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_VISIT_DATE))))));
                    sb.setLatitude(((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_LATITUDE)))));
                    sb.setLongitude(((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_LONGITUDE)))));
                    sb.setCoverage_status((((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_COVERAGE_STATUS))))));
                    sb.setStore_image((((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_INTIME_IMAGE))))));
                    sb.setCheckout_image((((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_CHECKOUT_IMAGE))))));
                    sb.setReasonid((((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_REASON_ID))))));
                    sb.setRemark((((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_COVERAGE_REMARK))))));
                    sb.setMID(Integer.parseInt(((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_ID))))));
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching Coverage Data!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());

        }

        return list;

    }


    // getCoverageData
    public ArrayList<CoverageBean> getCoverageData(String visitdate) {
        ArrayList<CoverageBean> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT  * from " + CommonString.TABLE_COVERAGE_DATA + " where " + CommonString.KEY_VISIT_DATE + "='" + visitdate + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    CoverageBean sb = new CoverageBean();
                    sb.setStore_id(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_STORE_ID)));
                    sb.setUserId((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_USER_ID))));
                    sb.setInTime(((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IN_TIME)))));
                    sb.setOutTime(((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_OUT_TIME)))));
                    sb.setVisitDate((((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_VISIT_DATE))))));
                    sb.setLatitude(((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_LATITUDE)))));
                    sb.setLongitude(((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_LONGITUDE)))));
                    sb.setCoverage_status((((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_COVERAGE_STATUS))))));
                    sb.setStore_image((((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_INTIME_IMAGE))))));
                    sb.setCheckout_image((((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_CHECKOUT_IMAGE))))));
                    sb.setReasonid((((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_REASON_ID))))));
                    sb.setRemark((((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_COVERAGE_REMARK))))));
                    sb.setMID(Integer.parseInt(((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_ID))))));
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching Coverage Data!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());

        }

        return list;

    }


    // get Asset data
    public ArrayList<NonWorkingReasonGetterSetter> getNonWorkingData() {
        Log.d("FetchingAssetdata--------------->Start<------------",
                "------------------");
        ArrayList<NonWorkingReasonGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;

        try {

            dbcursor = db.rawQuery("SELECT * FROM NON_WORKING_REASON", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    NonWorkingReasonGetterSetter sb = new NonWorkingReasonGetterSetter();
                    sb.setReason_cd(dbcursor.getString(dbcursor.getColumnIndexOrThrow("REASON_CD")));
                    sb.setReason(dbcursor.getString(dbcursor.getColumnIndexOrThrow("REASON")));
                    sb.setEntry_allow(dbcursor.getString(dbcursor.getColumnIndexOrThrow("ENTRY_ALLOW")));
                    sb.setIMAGE_ALLOW(dbcursor.getString(dbcursor.getColumnIndexOrThrow("IMAGE_ALLOW")));
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching Non working!!!!!!!!!!!",
                    e.toString());
            return list;
        }

        Log.d("Fetching non working data---------------------->Stop<-----------",
                "-------------------");
        return list;
    }


    //check if table is empty
    public boolean isCoverageDataFilled(String visit_date) {
        boolean filled = false;

        Cursor dbcursor = null;

        try {

            dbcursor = db
                    .rawQuery(
                            "SELECT * FROM COVERAGE_DATA " + "where "
                                    + CommonString.KEY_VISIT_DATE + "<>'" + visit_date + "'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                int icount = dbcursor.getInt(0);
                dbcursor.close();
                if (icount > 0) {
                    filled = true;
                } else {
                    filled = false;
                }

            }

        } catch (Exception e) {
            Log.d("Exception when fetching Records!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());
            return filled;
        }

        return filled;
    }

    //check if table is empty
    public boolean isPOSMDataFilled(String storeId) {
        boolean filled = false;
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT * FROM POSM_data WHERE STORE_CD= '" + storeId + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                int icount = dbcursor.getInt(0);
                dbcursor.close();
                if (icount > 0) {
                    filled = true;
                } else {
                    filled = false;
                }

            }

        } catch (Exception e) {
            Log.d("Exception when fetching Records!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());
            return filled;
        }

        return filled;
    }

    //check if table is empty
    public boolean isdemosDataFilled(String storeId) {
        boolean filled = false;

        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("SELECT * FROM DEMOS_data WHERE STORE_CD= '" + storeId + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                int icount = dbcursor.getInt(0);
                dbcursor.close();
                if (icount > 0) {
                    filled = true;
                } else {
                    filled = false;
                }

            }

        } catch (Exception e) {
            Log.d("Exception when fetching Records!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());
            return filled;
        }

        return filled;
    }

    public void updateCoverageCheckoutStatus(String storeid, String visitdate, String status) {
        try {
            ContentValues values = new ContentValues();
            values.put(CommonString.KEY_COVERAGE_STATUS, status);
            db.update(CommonString.TABLE_COVERAGE_DATA, values, CommonString.KEY_STORE_ID + "='" + storeid + "' AND " + CommonString.KEY_VISIT_DATE + "='" + visitdate + "'", null);
        } catch (Exception e) {

        }
    }

    public void InsertSTOREgeotag(String storeid, double lat, double longitude, String path, String status) {

        ContentValues values = new ContentValues();

        try {
            values.put("STORE_ID", storeid);
            values.put("LATITUDE", Double.toString(lat));
            values.put("LONGITUDE", Double.toString(longitude));
            values.put("FRONT_IMAGE", path);
            values.put("GEO_TAG", status);
            values.put("STATUS", status);
            db.insert(CommonString.TABLE_STORE_GEOTAGGING, null, values);

        } catch (Exception ex) {
            Log.d("Database Exception ", ex.toString());
        }

    }

    public ArrayList<GeotaggingBeans> getinsertGeotaggingData(String store_id) {


        ArrayList<GeotaggingBeans> geodata = new ArrayList<GeotaggingBeans>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT  * from " + CommonString.TABLE_STORE_GEOTAGGING +
                    "  WHERE STORE_ID = '" + store_id + "'", null);
            if (dbcursor != null) {
                int numrows = dbcursor.getCount();

                dbcursor.moveToFirst();
                for (int i = 1; i <= numrows; ++i) {

                    GeotaggingBeans data = new GeotaggingBeans();

                    data.setStoreid(dbcursor.getString(dbcursor.getColumnIndexOrThrow("STORE_ID")));
                    data.setLatitude(Double.parseDouble(dbcursor.getString(dbcursor.getColumnIndexOrThrow("LATITUDE"))));
                    data.setLongitude(Double.parseDouble(dbcursor.getString(dbcursor.getColumnIndexOrThrow("LONGITUDE"))));
                    data.setUrl1(dbcursor.getString(dbcursor.getColumnIndexOrThrow("FRONT_IMAGE")));
                    data.setGEO_TAG(dbcursor.getString(dbcursor.getColumnIndexOrThrow("GEO_TAG")));

                    geodata.add(data);
                    dbcursor.moveToNext();
                }

                dbcursor.close();

            }

        } catch (Exception e) {

        } finally {
            if (dbcursor != null && !dbcursor.isClosed()) {
                dbcursor.close();
            }
        }


        return geodata;

    }


    public void updateGeoTagData(String storeid, String status) {

        try {
            ContentValues values = new ContentValues();
            values.put("GEO_TAG", status);
            int l = db.update(CommonString.TABLE_STORE_GEOTAGGING, values, CommonString.KEY_STORE_ID + "='" +storeid+"'", null);
            System.out.println("update : " + l);
        } catch (Exception e) {
            Log.d("Database Data ", e.toString());

        }
    }

    public void updateDataStatus(String id, String status) {
        ContentValues values = new ContentValues();
        try {
            values.put("GEOTAG", status);
            db.update("JOURNEY_PLAN", values, CommonString.KEY_STORE_CD + "='" + id + "'", null);

        } catch (Exception ex) {
            Log.d("Database Data ", ex.toString());

        }

    }


    public ArrayList<JourneyPlanGetterSetter> getSpecificStoreData(String store_cd) {
        Log.d("FetchingStoredata--------------->Start<------------",
                "------------------");
        ArrayList<JourneyPlanGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("SELECT  * from  JOURNEY_PLAN" + "  WHERE STORE_CD = '" + store_cd + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    JourneyPlanGetterSetter sb = new JourneyPlanGetterSetter();
                    sb.setStore_cd(dbcursor.getString(dbcursor.getColumnIndexOrThrow("STORE_CD")));
                    sb.setStore_name((dbcursor.getString(dbcursor.getColumnIndexOrThrow("STORE_NAME"))));
                    sb.setEmp_cd((dbcursor.getString(dbcursor.getColumnIndexOrThrow("EMP_CD"))));
                    sb.setVISIT_DATE((dbcursor.getString(dbcursor.getColumnIndexOrThrow("VISIT_DATE"))));
                    sb.setStore_name((dbcursor.getString(dbcursor.getColumnIndexOrThrow("STORE_NAME"))));
                    sb.setStore_address((dbcursor.getString(dbcursor.getColumnIndexOrThrow("STORE_ADDRESS"))));
                    sb.setCity((dbcursor.getString(dbcursor.getColumnIndexOrThrow("CITY"))));
                    sb.setUploadStatus((dbcursor.getString(dbcursor.getColumnIndexOrThrow("UPLOAD_STATUS"))));
                    sb.setCheckOutStatus((dbcursor.getString(dbcursor.getColumnIndexOrThrow("CHECKOUT_STATUS"))));
                    sb.setGeotag((dbcursor.getString(dbcursor.getColumnIndexOrThrow("GEOTAG"))));
                    sb.setLOGITUDE((dbcursor.getString(dbcursor.getColumnIndexOrThrow("LOGITUDE"))));
                    sb.setLATITUDE((dbcursor.getString(dbcursor.getColumnIndexOrThrow("LATITUDE"))));


                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching JCP!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());
            return list;
        }

        Log.d("FetchingJCP data---------------------->Stop<-----------",
                "-------------------");
        return list;

    }
    public void updateCoverageStoreOutTime1(String StoreId, String VisitDate, String outtime, String status) {
        try {
            ContentValues values = new ContentValues();
            values.put(CommonString.KEY_OUT_TIME, outtime);
            values.put(CommonString.KEY_COVERAGE_STATUS, status);
            db.update(CommonString.TABLE_COVERAGE_DATA, values, CommonString.KEY_STORE_CD + "='" + StoreId + "' AND " + CommonString.KEY_VISIT_DATE + "='" + VisitDate + "'", null);
        } catch (Exception e) {

        }
    }

    public int CheckMid(String currdate, String storeid) {

        Cursor dbcursor = null;
        int mid = 0;
        try {
            dbcursor = db.rawQuery("SELECT  * from "
                    + CommonString.TABLE_COVERAGE_DATA + "  WHERE "
                    + CommonString.KEY_VISIT_DATE + " = '" + currdate
                    + "' AND " + CommonString.KEY_STORE_CD + " ='" + storeid
                    + "'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();

                mid = dbcursor.getInt(dbcursor
                        .getColumnIndexOrThrow(CommonString.KEY_ID));

                dbcursor.close();

            }

        } catch (Exception e) {
            Log.d("Exception mid",
                    e.toString());
        }

        return mid;
    }

}
