package com.cpm.aintupromoter.upload;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cpm.aintupromoter.Database.AintuREDB;
import com.cpm.aintupromoter.GetterSetter.GeotaggingBeans;
import com.cpm.aintupromoter.MainManuActivity;
import com.cpm.aintupromoter.R;
import com.cpm.aintupromoter.constants.CommonString;
import com.cpm.aintupromoter.messgae.AlertMessage;
import com.cpm.aintupromoter.retrofit.RetrofitClass;
import com.cpm.aintupromoter.xmlGetterSetter.CoverageBean;
import com.cpm.aintupromoter.xmlGetterSetter.DemosGetterSetter;
import com.cpm.aintupromoter.xmlGetterSetter.JourneyPlanGetterSetter;
import com.cpm.aintupromoter.xmlGetterSetter.POSMDATAGetterSetter;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

@SuppressWarnings("deprecation")
public class UploadDataActivity extends Activity {
    private Dialog dialog;
    private ProgressBar pb;
    private TextView percentage, message, tv_title;
    String app_ver;
    private String visit_date, username;
    private SharedPreferences preferences;
    private AintuREDB database;
    String datacheck = "";
    String[] words;
    String validity;
    int mid;
    Data data;
    private ArrayList<CoverageBean> coverageBeanlist = new ArrayList<>();
    ArrayList<POSMDATAGetterSetter> posmlist = new ArrayList<>();
    private ArrayList<DemosGetterSetter> insertedlist_Data = new ArrayList<>();
    String result;
    String Path;
    JourneyPlanGetterSetter journeyPlanGetterSetter;
    private boolean nonworking_flag = false;
    boolean flag_status = false;
    ArrayList<GeotaggingBeans> geotaglist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_manu);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        visit_date = preferences.getString(CommonString.KEY_DATE, null);
        username = preferences.getString(CommonString.KEY_USERNAME, null);
        app_ver = preferences.getString(CommonString.KEY_VERSION, "");
        database = new AintuREDB(this);
        database.open();
        Path = CommonString.FILE_PATH;
        new UploadTask(this).execute();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        database.close();
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        Intent i = new Intent(this, MainManuActivity.class);
        startActivity(i);
        UploadDataActivity.this.finish();
    }

    private class UploadTask extends AsyncTask<Void, Data, String> {
        private Context context;

        UploadTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            dialog = new Dialog(context);
            dialog.setContentView(R.layout.custom_upload);
            dialog.setTitle("Uploading Data");
            dialog.setCancelable(false);
            dialog.show();
            pb = (ProgressBar) dialog.findViewById(R.id.progressBar1);
            percentage = (TextView) dialog.findViewById(R.id.percentage);
            message = (TextView) dialog.findViewById(R.id.message);
            tv_title = (TextView) dialog.findViewById(R.id.tv_title);
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub
            try {
                data = new Data();
                data.value = 10;
                data.name = "Uploading";
                publishProgress(data);
                database.open();
                coverageBeanlist = database.getCoverageData(visit_date);
                if (coverageBeanlist.size() > 0) {
                    for (int i = 0; i < coverageBeanlist.size(); i++) {
                        nonworking_flag = false;
                        journeyPlanGetterSetter = database.getStoreStatus(coverageBeanlist.get(i).getStore_id());
                        if (!journeyPlanGetterSetter.getUploadStatus().get(0).equalsIgnoreCase(CommonString.KEY_U)
                                && !journeyPlanGetterSetter.getUploadStatus().get(0).equalsIgnoreCase(CommonString.KEY_D)) {
                            String onXML = "[DATA][USER_DATA][STORE_CD]"
                                    + coverageBeanlist.get(i).getStore_id()
                                    + "[/STORE_CD]" + "[VISIT_DATE]"
                                    + coverageBeanlist.get(i).getVisitDate()
                                    + "[/VISIT_DATE][LATITUDE]"
                                    + coverageBeanlist.get(i).getLatitude()
                                    + "[/LATITUDE][APP_VERSION]"
                                    + app_ver
                                    + "[/APP_VERSION][LONGITUDE]"
                                    + coverageBeanlist.get(i).getLongitude()
                                    + "[/LONGITUDE][IN_TIME]"
                                    + "00:00:00"
                                    + "[/IN_TIME][OUT_TIME]"
                                    + "00:00:00"
                                    + "[/OUT_TIME][UPLOAD_STATUS]"
                                    + "N"
                                    + "[/UPLOAD_STATUS][USER_ID]" + username
                                    + "[/USER_ID][IMAGE_URL]" + coverageBeanlist.get(i).getStore_image()
                                    + "[/IMAGE_URL]"
                                    + "[REASON_ID]"
                                    + coverageBeanlist.get(i).getReasonid()
                                    + "[/REASON_ID]"
                                    + "[CHECKOUT_IMAGE]"
                                    + coverageBeanlist.get(i).getCheckout_image()
                                    + "[/CHECKOUT_IMAGE]"
                                    + "[REASON_REMARK]"
                                    + coverageBeanlist.get(i).getRemark()
                                    + "[/REASON_REMARK][/USER_DATA][/DATA]";


                            SoapObject request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_UPLOAD_DR_STORE_COVERAGE);
                            request.addProperty("onXML", onXML);
                            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                            envelope.dotNet = true;
                            envelope.setOutputSoapObject(request);
                            HttpTransportSE androidHttpTransport = new HttpTransportSE(CommonString.URL);
                            androidHttpTransport.call(CommonString.SOAP_ACTION + CommonString.METHOD_UPLOAD_DR_STORE_COVERAGE, envelope);
                            Object result = (Object) envelope.getResponse();
                            datacheck = result.toString();
                            datacheck = datacheck.replace("\"", "");
                            words = datacheck.split("\\;");
                            validity = (words[0]);
                            if (validity.equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                if (coverageBeanlist.get(i).getCoverage_status().equals(CommonString.STORE_STATUS_LEAVE)) {
                                    nonworking_flag = true;
                                }
                                // uploaded_flag = true;
                                final int finalI = i;
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        String value = "Upload Store Data " + (finalI + 1) + " of " + coverageBeanlist.size();
                                        tv_title.setText(value);
                                    }
                                });

                            } else {
                                if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                    continue;
                                }

                            }
                            String final_xml = "";
                            mid = Integer.parseInt((words[1]));
                            data.value = 30;
                            data.name = "Coverage data Uploading";
                            publishProgress(data);

//posm data
                            final_xml = "";
                            onXML = "";
                            posmlist = database.getinsertedposmdata(coverageBeanlist.get(i).getStore_id());
                            if (posmlist.size() > 0) {
                                for (int j = 0; j < posmlist.size(); j++) {
                                    flag_status = false;
                                    if (!posmlist.get(j).getStatus().equals(CommonString.KEY_U)) {
                                        flag_status = true;
                                        onXML = "[POSM_DATA][MID]"
                                                + mid
                                                + "[/MID]"
                                                + "[CREATED_BY]"
                                                + username
                                                + "[/CREATED_BY]"
                                                + "[QUANTITY]"
                                                + posmlist.get(j).getQuantity()
                                                + "[/QUANTITY]"
                                                + "[POSM_IMG]"
                                                + posmlist.get(j).getPosm_img()
                                                + "[/POSM_IMG]"
                                                + "[REMARK]"
                                                + posmlist.get(j).getPosmRemark()
                                                + "[/REMARK]"
                                                + "[FLAG_MANDATORY]"
                                                + posmlist.get(j).getFLAG().get(0)
                                                + "[/FLAG_MANDATORY]"
                                                + "[POSM_CD]"
                                                + posmlist.get(j).getPOSM_CD().get(0)
                                                + "[/POSM_CD]"
                                                + "[/POSM_DATA]";
                                        final_xml = final_xml + onXML;

                                    }
                                }

                                if (flag_status) {
                                    final String sos_xml = "[DATA]" + final_xml + "[/DATA]";
                                    request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_UPLOAD_XML);
                                    request.addProperty("XMLDATA", sos_xml);
                                    request.addProperty("KEYS", "POSM_DATA");
                                    request.addProperty("USERNAME", username);
                                    request.addProperty("MID", mid);
                                    envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                                    envelope.dotNet = true;
                                    envelope.setOutputSoapObject(request);
                                    androidHttpTransport = new HttpTransportSE(CommonString.URL);
                                    androidHttpTransport.call(CommonString.SOAP_ACTION + CommonString.METHOD_UPLOAD_XML, envelope);
                                    result = (Object) envelope.getResponse();
                                    if (result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                        database.open();
                                        long l = database.updateposmStatus(coverageBeanlist.get(i).getStore_id(), coverageBeanlist.get(i).getVisitDate(), CommonString.KEY_U);

                                    }
                                    data.value = 50;
                                    data.name = "POSM_DATA";
                                    publishProgress(data);
                                }

                            }
                            //demos data
                            final_xml = "";
                            onXML = "";
                            insertedlist_Data = database.getinsertedDemosdata(coverageBeanlist.get(i).getStore_id());
                            if (insertedlist_Data.size() > 0) {
                                flag_status = false;
                                for (int j = 0; j < insertedlist_Data.size(); j++) {
                                    String flag = "";
                                    if (!insertedlist_Data.get(j).getStatus().equals(CommonString.KEY_U)) {
                                        flag_status = true;
                                        if (insertedlist_Data.get(j).getFlag().equalsIgnoreCase("YES")) {
                                            flag = "1";
                                        } else {
                                            flag = "0";
                                        }
                                        onXML = "[DEMOS_DATA][MID]"
                                                + mid
                                                + "[/MID]"
                                                + "[CREATED_BY]"
                                                + username
                                                + "[/CREATED_BY]"
                                                + "[MOBILE_NO]"
                                                + insertedlist_Data.get(j).getMobile_no()
                                                + "[/MOBILE_NO]"
                                                + "[GENDER]"
                                                + insertedlist_Data.get(j).getGender()
                                                + "[/GENDER]"
                                                + "[APPLICATION_DEMO_GIVEN]"
                                                + insertedlist_Data.get(j).getApp_demosG()
                                                + "[/APPLICATION_DEMO_GIVEN]"
                                                + "[AVAILED_OFFER]"
                                                + insertedlist_Data.get(j).getAvailed_offer()
                                                + "[/AVAILED_OFFER]"
                                                + "[DOWNLOADED]"
                                                + insertedlist_Data.get(j).getDownloded()
                                                + "[/DOWNLOADED]"
                                                + "[NAME]"
                                                + insertedlist_Data.get(j).getName()
                                                + "[/NAME]"
                                                + "[FLAG]"
                                                + flag
                                                + "[/FLAG]"
                                                + "[REMARK]"
                                                + insertedlist_Data.get(j).getRemark()
                                                + "[/REMARK]"

                                                + "[DEDICATED_PHONE]"
                                                + insertedlist_Data.get(j).getDedicated_phone()
                                                + "[/DEDICATED_PHONE]"

                                                + "[DEDICATED_PHONE_REMARK]"
                                                + insertedlist_Data.get(j).getDedicated_remark()
                                                + "[/DEDICATED_PHONE_REMARK]"

                                                + "[STORE_NETWORK_ISSUE]"
                                                + insertedlist_Data.get(j).getStore_network()
                                                + "[/STORE_NETWORK_ISSUE]"
                                                + "[STORE_NETWORK_ISSUE_REMARK]"
                                                + insertedlist_Data.get(j).getStore_network_remark()
                                                + "[/STORE_NETWORK_ISSUE_REMARK]"
                                                + "[BRAND_STOCK_REMARK]"
                                                + insertedlist_Data.get(j).getBrand_s_remark()
                                                + "[/BRAND_STOCK_REMARK]"
                                                + "[ASSET_REMARK]"
                                                + insertedlist_Data.get(j).getAsset_remark()
                                                + "[/ASSET_REMARK]"


                                                + "[/DEMOS_DATA]";
                                        final_xml = final_xml + onXML;

                                    }

                                }
                                if (flag_status) {
                                    final String sos_xml = "[DATA]" + final_xml + "[/DATA]";
                                    request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_UPLOAD_XML);
                                    request.addProperty("XMLDATA", sos_xml);
                                    request.addProperty("KEYS", "DEMOS_DATA");
                                    request.addProperty("USERNAME", username);
                                    request.addProperty("MID", mid);
                                    envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                                    envelope.dotNet = true;
                                    envelope.setOutputSoapObject(request);
                                    androidHttpTransport = new HttpTransportSE(CommonString.URL);
                                    androidHttpTransport.call(CommonString.SOAP_ACTION + CommonString.METHOD_UPLOAD_XML, envelope);
                                    result = (Object) envelope.getResponse();
                                    if (result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                        long l = database.updateDemosDataStatusByStoreid(coverageBeanlist.get(i).getStore_id(),
                                                coverageBeanlist.get(i).getVisitDate(), CommonString.KEY_U);
                                    }
                                    data.value = 70;
                                    data.name = "DEMOS_DATA";
                                    publishProgress(data);
                                }

                            }


                            geotaglist = database.getinsertGeotaggingData(coverageBeanlist.get(i).getStore_id());
                            String geo_xml = "";
                            boolean geotag_status = false;
                            if (geotaglist.size() > 0) {
                                for (int j = 0; j < geotaglist.size(); j++) {

                                    if (!geotaglist.get(j).getGEO_TAG().equals("Y")) {
                                        geotag_status=true;
                                        String onXML1 = "[GeoTag_DATA][STORE_ID]"
                                                + geotaglist.get(j).getStoreid()
                                                + "[/STORE_ID]"
                                                + "[LATTITUDE]"
                                                + geotaglist.get(j).getLatitude()
                                                + "[/LATTITUDE]"
                                                + "[LONGITUDE]"
                                                + geotaglist.get(j).getLongitude()
                                                + "[/LONGITUDE]"
                                                + "[FRONT_IMAGE]"
                                                + geotaglist.get(j).getUrl1()
                                                + "[/FRONT_IMAGE]"
                                                + "[CREATED_BY]" + username
                                                + "[/CREATED_BY][/GeoTag_DATA]";

                                        geo_xml = geo_xml + onXML1;

                                    }
                                }
                                if (geotag_status){
                                    geo_xml = "[DATA]" + geo_xml + "[/DATA]";
                                    request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_UPLOAD_XML);
                                    request.addProperty("MID", "0");
                                    request.addProperty("KEYS", "GEOTAG_NEW_DATA");
                                    request.addProperty("USERNAME", username);
                                    request.addProperty("XMLDATA", geo_xml);
                                    envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                                    envelope.dotNet = true;
                                    envelope.setOutputSoapObject(request);
                                    androidHttpTransport = new HttpTransportSE(CommonString.URL);
                                    androidHttpTransport.call(CommonString.SOAP_ACTION + CommonString.METHOD_UPLOAD_XML, envelope);
                                    result = (Object) envelope.getResponse();
                                    if (result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {

                                    } else {
                                        return CommonString.METHOD_UPLOAD_XML;

                                    }

                                }

                            }
//end

                            if (journeyPlanGetterSetter.getCheckOutStatus().get(0).equals(CommonString.KEY_C)) {
                                final_xml = "";
                                onXML = "";
                                onXML = "[COVERAGE_STATUS][STORE_ID]"
                                        + coverageBeanlist.get(i).getStore_id()
                                        + "[/STORE_ID]"
                                        + "[VISIT_DATE]"
                                        + coverageBeanlist.get(i).getVisitDate()
                                        + "[/VISIT_DATE]"
                                        + "[USER_ID]"
                                        + coverageBeanlist.get(i).getUserId()
                                        + "[/USER_ID]"
                                        + "[STATUS]"
                                        + CommonString.KEY_D
                                        + "[/STATUS]"
                                        + "[/COVERAGE_STATUS]";
                                final_xml = final_xml + onXML;

                                final String sos_xml = "[DATA]" + final_xml + "[/DATA]";
                                SoapObject request1 = new SoapObject(CommonString.NAMESPACE, CommonString.MEHTOD_UPLOAD_COVERAGE_STATUS);
                                request1.addProperty("onXML", sos_xml);
                                SoapSerializationEnvelope envelope1 = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                                envelope1.dotNet = true;
                                envelope1.setOutputSoapObject(request1);
                                HttpTransportSE androidHttpTransport1 = new HttpTransportSE(CommonString.URL);
                                androidHttpTransport1.call(CommonString.SOAP_ACTION + CommonString.MEHTOD_UPLOAD_COVERAGE_STATUS, envelope1);
                                Object result1 = (Object) envelope1.getResponse();
                                if (result1.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                    database.open();
                                    database.updateCoverageStatus(coverageBeanlist.get(i).getMID(), CommonString.KEY_D);
                                    database.updateStoreStatusOnLeave(coverageBeanlist.get(i).getStore_id(), coverageBeanlist.get(i).getVisitDate(), CommonString.KEY_D);
                                }
                                if (!result1.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                    continue;
                                }
                                data.value = 78;
                                data.name = "COVERAGE_STATUS";
                                publishProgress(data);
                            } else {
                                if (nonworking_flag) {
                                    final_xml = "";
                                    onXML = "";
                                    onXML = "[COVERAGE_STATUS][STORE_ID]"
                                            + coverageBeanlist.get(i).getStore_id()
                                            + "[/STORE_ID]"
                                            + "[VISIT_DATE]"
                                            + coverageBeanlist.get(i).getVisitDate()
                                            + "[/VISIT_DATE]"
                                            + "[USER_ID]"
                                            + coverageBeanlist.get(i).getUserId()
                                            + "[/USER_ID]"
                                            + "[STATUS]"
                                            + CommonString.KEY_D
                                            + "[/STATUS]"
                                            + "[/COVERAGE_STATUS]";
                                    final_xml = final_xml + onXML;
                                    final String sos_xml = "[DATA]" + final_xml + "[/DATA]";
                                    SoapObject request1 = new SoapObject(CommonString.NAMESPACE, CommonString.MEHTOD_UPLOAD_COVERAGE_STATUS);
                                    request1.addProperty("onXML", sos_xml);
                                    SoapSerializationEnvelope envelope1 = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                                    envelope1.dotNet = true;
                                    envelope1.setOutputSoapObject(request1);
                                    HttpTransportSE androidHttpTransport1 = new HttpTransportSE(CommonString.URL);
                                    androidHttpTransport1.call(CommonString.SOAP_ACTION + CommonString.MEHTOD_UPLOAD_COVERAGE_STATUS, envelope1);
                                    Object result1 = (Object) envelope1.getResponse();
                                    if (result1.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                        database.open();
                                        database.updateCoverageStatus(coverageBeanlist.get(i).getMID(), CommonString.KEY_D);
                                        database.updateStoreStatusOnLeave(coverageBeanlist.get(i).getStore_id(), coverageBeanlist.get(i).getVisitDate(), CommonString.KEY_D);
                                    }
                                    if (!result1.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                        continue;
                                    }
                                    data.value = 78;
                                    data.name = "COVERAGE_STATUS";
                                    publishProgress(data);
                                }

                            }
                        }
                    }
                }

                File dir = new File(CommonString.FILE_PATH);
                ArrayList<String> list = new ArrayList();
                list = getFileNames(dir.listFiles());
                if (list.size() > 0) {
                    for (int i1 = 0; i1 < list.size(); i1++) {
                        if (list.get(i1).contains("_INTIME_IMG_") || list.get(i1).contains("_OUTTIME_IMG_")
                                || list.get(i1).contains("_NONWORK_IMG_")) {
                            File originalFile = new File(Path + list.get(i1));
                            result = RetrofitClass.UploadImageByRetrofit(UploadDataActivity.this,
                                    originalFile.getName(), "StoreImages");
                            if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                return result.toString();
                            }
                            data.value = 80;
                            data.name = "StoreImages";
                            publishProgress(data);
                        }

                        if (list.get(i1).contains("_POSM_IMG_")) {
                            File originalFile = new File(Path + list.get(i1));
                            result = RetrofitClass.UploadImageByRetrofit(UploadDataActivity.this,
                                    originalFile.getName(), "POSMImages");
                            if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                return result.toString();
                            }
                            data.value = 90;
                            data.name = "POSMImages";
                            publishProgress(data);
                        }

                        if (list.get(i1).contains("_GeoTag_")) {
                            File originalFile = new File(Path + list.get(i1));
                            result = RetrofitClass.UploadImageByRetrofit(UploadDataActivity.this,
                                    originalFile.getName(), "GEOTagImages");
                            if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                return result.toString();
                            }
                            data.value = 95;
                            data.name = "GEOTagImages";
                            publishProgress(data);
                        }

                    }
                    data.value = 100;
                    data.name = "COVERAGE_STATUS";
                    publishProgress(data);
                }
                String response = updateStatus();
                if (!response.equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                    return response.toString();
                } else {
                    return CommonString.KEY_SUCCESS;
                }

            } catch (IOException e) {
                final AlertMessage message = new AlertMessage(UploadDataActivity.this,
                        AlertMessage.MESSAGE_SOCKETEXCEPTION, "socket_upload", e);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        message.showMessage();
                    }
                });
            } catch (Exception e) {
                final AlertMessage message = new AlertMessage(
                        UploadDataActivity.this,
                        AlertMessage.MESSAGE_EXCEPTION, "uploaded", e);
                e.getMessage();
                e.printStackTrace();
                e.getCause();
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        message.showMessage();
                    }
                });
            }
            return "";
        }

        @Override
        protected void onProgressUpdate(Data... values) {
            // TODO Auto-generated method stub
            pb.setProgress(values[0].value);
            percentage.setText(values[0].value + "%");
            message.setText(values[0].name);
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            dialog.dismiss();
            if (result.contains(CommonString.KEY_SUCCESS)) {
                AlertMessage message = new AlertMessage(UploadDataActivity.this, AlertMessage.MESSAGE_UPLOAD_DATA, "success", null);
                message.showMessage();
            } else if (!result.equals("")) {
                AlertMessage message = new AlertMessage(UploadDataActivity.this, "Error in uploading :" + result, "success", null);
                message.showMessage();
            } else if (result.equals("")) {
                AlertMessage message = new AlertMessage(UploadDataActivity.this, "Error in uploading :" + AlertMessage.MESSAGE_SOCKETEXCEPTION, "success", null);
                message.showMessage();
            }
        }
    }

    class Data {
        int value;
        String name;
    }


    public ArrayList<String> getFileNames(File[] file) {
        ArrayList<String> arrayFiles = new ArrayList<String>();
        if (file.length > 0) {
            for (int i = 0; i < file.length; i++)
                arrayFiles.add(file[i].getName());
        }
        return arrayFiles;
    }


    private String updateStatus() throws IOException, XmlPullParserException {
        String final_xml = "";
        String onXML = "";
        Object result1 = "";
        try {
            coverageBeanlist = database.getCoverageData(visit_date);
            if (coverageBeanlist.size() > 0) {
                for (int i = 0; i < coverageBeanlist.size(); i++) {
                    journeyPlanGetterSetter = database.getStoreStatus(coverageBeanlist.get(i).getStore_id());
                    if (journeyPlanGetterSetter.getUploadStatus().get(0).equalsIgnoreCase(CommonString.KEY_D)) {
                        onXML = "[COVERAGE_STATUS][STORE_ID]"
                                + coverageBeanlist.get(i).getStore_id()
                                + "[/STORE_ID]"
                                + "[VISIT_DATE]"
                                + coverageBeanlist.get(i).getVisitDate()
                                + "[/VISIT_DATE]"
                                + "[USER_ID]"
                                + coverageBeanlist.get(i).getUserId()
                                + "[/USER_ID]"
                                + "[STATUS]"
                                + CommonString.KEY_U
                                + "[/STATUS]"
                                + "[/COVERAGE_STATUS]";
                        final_xml = final_xml + onXML;

                        final String sos_xml = "[DATA]" + final_xml + "[/DATA]";
                        SoapObject request1 = new SoapObject(CommonString.NAMESPACE, CommonString.MEHTOD_UPLOAD_COVERAGE_STATUS);
                        request1.addProperty("onXML", sos_xml);
                        SoapSerializationEnvelope envelope1 = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                        envelope1.dotNet = true;
                        envelope1.setOutputSoapObject(request1);
                        HttpTransportSE androidHttpTransport1 = new HttpTransportSE(CommonString.URL);
                        androidHttpTransport1.call(CommonString.SOAP_ACTION + CommonString.MEHTOD_UPLOAD_COVERAGE_STATUS, envelope1);
                        result1 = (Object) envelope1.getResponse();
                        if (result1.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                            database.open();
                            database.updateStoreStatusOnLeave(coverageBeanlist.get(i).getStore_id(),
                                    coverageBeanlist.get(i).getVisitDate(), CommonString.KEY_U);
                            database.deleteSpecificStoreData(coverageBeanlist.get(i).getStore_id());
                        }
                        if (!result1.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                            return "COVERAGE_STATUS";
                        }
                    }
                }
                return CommonString.KEY_SUCCESS;
            } else {
                return CommonString.KEY_SUCCESS;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result1.toString();

    }
}


