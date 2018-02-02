package com.cpm.aintupromoter.upload;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cpm.aintupromoter.Database.AintuREDB;
import com.cpm.aintupromoter.GetterSetter.GeotaggingBeans;
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
import java.util.Calendar;

public class CheckoutNUpload extends Activity {
    ArrayList<JourneyPlanGetterSetter> jcplist;
    AintuREDB database;
    private SharedPreferences preferences;
    private String username, visit_date, store_id, prev_date, result;
    private Dialog dialog;
    private ProgressBar pb;
    private TextView percentage, message;
    private Data data;
    ArrayList<CoverageBean> coverageBean;
    private ArrayList<CoverageBean> coverageBeanlist = new ArrayList<>();
    ArrayList<POSMDATAGetterSetter> posmlist = new ArrayList<>();
    private ArrayList<DemosGetterSetter> insertedlist_Data = new ArrayList<>();
    JourneyPlanGetterSetter journeyPlanGetterSetter;
    String app_ver;
    String datacheck = "";
    String[] words;
    String validity;
    int mid;
    String Path;
    boolean flag_status = false;
    ArrayList<GeotaggingBeans> geotaglist = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout_n_upload);
        database = new AintuREDB(this);
        database.open();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        username = preferences.getString(CommonString.KEY_USERNAME, "");
        app_ver = preferences.getString(CommonString.KEY_VERSION, "");
        visit_date = preferences.getString(CommonString.KEY_DATE, null);
        Path = CommonString.FILE_PATH;
        if (!isCheckoutDataExist()) {
            new UploadTask(this).execute();
        }
    }

    public boolean isCheckoutDataExist() {
        boolean flag = false;
        jcplist = database.getAllJCPData();
        for (int i = 0; i < jcplist.size(); i++) {
            if (!jcplist.get(i).getVISIT_DATE().get(0).equals(visit_date)) {
                prev_date = jcplist.get(i).getVISIT_DATE().get(0);
            }
            coverageBean = database.getCoverageSpecificData(jcplist.get(i).getStore_cd().get(0));
            if (coverageBean.size() > 0) {
                for (int i1 = 0; i1 < coverageBean.size(); i1++) {
                    if (coverageBean.get(i1).getCoverage_status().equals(CommonString.KEY_INVALID) || coverageBean.get(i1).getCoverage_status().equals(CommonString.KEY_VALID)) {
                        flag = true;
                        new BackgroundTask(this).execute();
                        break;
                    }
                }
            }
            if (flag) {
                break;
            }
        }

        return flag;
    }

    private class BackgroundTask extends AsyncTask<Void, Data, String> {
        private Context context;

        BackgroundTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            dialog = new Dialog(context);
            dialog.setContentView(R.layout.custom);
            dialog.setTitle("Uploading Checkout Data");
            dialog.setCancelable(false);
            dialog.show();
            pb = (ProgressBar) dialog.findViewById(R.id.progressBar1);
            percentage = (TextView) dialog.findViewById(R.id.percentage);
            message = (TextView) dialog.findViewById(R.id.message);
        }

        @SuppressWarnings("deprecation")
        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub
            try {
                data = new Data();
                data.value = 20;
                data.name = "Checked out Data Uploading";
                publishProgress(data);
                if (coverageBean.size() > 0) {
                    String onXML = "[STORE_CHECK_OUT_STATUS][USER_ID]"
                            + username
                            + "[/USER_ID]" + "[STORE_ID]"
                            + coverageBean.get(0).getStore_id()
                            + "[/STORE_ID][LATITUDE]"
                            + coverageBean.get(0).getLatitude()
                            + "[/LATITUDE][LOGITUDE]"
                            + coverageBean.get(0).getLongitude()
                            + "[/LOGITUDE][CHECKOUT_DATE]"
                            + coverageBean.get(0).getVisitDate()
                            + "[/CHECKOUT_DATE][CHECK_OUTTIME]"
                            + "00:00:00"
                            + "[/CHECK_OUTTIME][CHECK_INTIME]"
                            + "00:00:00"
                            + "[/CHECK_INTIME][CREATED_BY]"
                            + username
                            + "[/CREATED_BY][/STORE_CHECK_OUT_STATUS]";

                    final String sos_xml = "[DATA]" + onXML + "[/DATA]";
                    SoapObject request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_CHECKOUT_STATUS);
                    request.addProperty("onXML", sos_xml);
                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.dotNet = true;
                    envelope.setOutputSoapObject(request);
                    HttpTransportSE androidHttpTransport = new HttpTransportSE(CommonString.URL);
                    androidHttpTransport.call(CommonString.SOAP_ACTION + CommonString.METHOD_CHECKOUT_STATUS, envelope);
                    Object result = (Object) envelope.getResponse();
                    if (result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                        database.open();
                        database.updateCoverageStoreOutTime(coverageBean.get(0).getStore_id(), coverageBean.get(0).getVisitDate(), CommonString.KEY_C);
                        long l1 = database.updateStoreStatusOnCheckout(coverageBean.get(0).getStore_id(), coverageBean.get(0).getVisitDate(), CommonString.KEY_C);
                        if (l1 == 0 || l1 == -1) {
                            database.updateStoreStatusOnCheckout(coverageBean.get(0).getStore_id(), coverageBean.get(0).getVisitDate(), CommonString.KEY_C);
                        }
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(CommonString.KEY_STORE_CD, "");
                        editor.putString(CommonString.KEY_STOREVISITED_STATUS, "");
                        editor.commit();
                        data.value = 100;
                        data.name = "Checkout Done";
                        publishProgress(data);
                        return CommonString.KEY_SUCCESS;
                    } else {
                        if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                            return "Upload_Store_ChecOut_Status";
                        }
                    }
                }

            } catch (IOException e) {
                showAlert(AlertMessage.MESSAGE_SOCKETEXCEPTION);
            } catch (Exception e) {
                showAlert(AlertMessage.MESSAGE_EXCEPTION);
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
            if (result.equals(CommonString.KEY_SUCCESS)) {
                new UploadTask(CheckoutNUpload.this).execute();
            } else if (!result.equals("")) {
                Toast.makeText(getApplicationContext(), "Network Error Try Again", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    class Data {
        int value;
        String name;
    }

    public String getCurrentTime() {
        Calendar m_cal = Calendar.getInstance();
        String intime = m_cal.get(Calendar.HOUR_OF_DAY) + ":" + m_cal.get(Calendar.MINUTE) + ":" + m_cal.get(Calendar.SECOND);
        return intime;
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
                coverageBeanlist = database.getCoverageData(prev_date);
                if (coverageBeanlist.size() > 0) {
                    for (int i = 0; i < coverageBeanlist.size(); i++) {
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

                            } else {
                                if (!validity.equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                    continue;
                                }

                            }
                            mid = Integer.parseInt((words[1]));
                            data.value = 30;
                            data.name = "Coverage data Uploading";
                            publishProgress(data);
                            String final_xml = "";

//posm data upload
                            final_xml = "";
                            onXML = "";
                            posmlist = database.getinsertedposmdata(coverageBeanlist.get(i).getStore_id());
                            if (posmlist.size() > 0) {
                                flag_status = false;
                                for (int j = 0; j < posmlist.size(); j++) {
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
                                        // continue;
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
                                for (int j = 0; j < insertedlist_Data.size(); j++) {
                                    String flag = "";
                                    flag_status = false;
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

                                                +"[DEDICATED_PHONE_REMARK]"
                                                + insertedlist_Data.get(j).getDedicated_remark()
                                                +"[/DEDICATED_PHONE_REMARK]"

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
                                        database.updateDemosDataStatusByStoreid(coverageBeanlist.get(i).getStore_id(),
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



                            // SET COVERAGE STATUS

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
                            data.value = 70;
                            data.name = "COVERAGE_STATUS";
                            publishProgress(data);
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
                            result = RetrofitClass.UploadImageByRetrofit(CheckoutNUpload.this,
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
                            result = RetrofitClass.UploadImageByRetrofit(CheckoutNUpload.this,
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
                            result = RetrofitClass.UploadImageByRetrofit(CheckoutNUpload.this,
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
                    return AlertMessage.MESSAGE_SOCKETEXCEPTION;
                } else {
                    return CommonString.KEY_SUCCESS;
                }


            } catch (IOException e) {
                final AlertMessage message = new AlertMessage(CheckoutNUpload.this,
                        AlertMessage.MESSAGE_SOCKETEXCEPTION, "socket_upload", e);

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        message.showMessage();

                    }
                });
            } catch (Exception e) {
                final AlertMessage message = new AlertMessage(
                        CheckoutNUpload.this,
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
                AlertMessage message = new AlertMessage(CheckoutNUpload.this, AlertMessage.MESSAGE_UPLOAD_DATA, "success", null);
                message.showMessage();
            } else if (!result.equals("")) {
                AlertMessage message = new AlertMessage(CheckoutNUpload.this, "Error in uploading :" + result, "success", null);
                message.showMessage();
            } else if (result.equals("")) {
                AlertMessage message = new AlertMessage(CheckoutNUpload.this, "Error in uploading :" + result, "success", null);
                message.showMessage();
            }
        }
    }


    public ArrayList<String> getFileNames(File[] file) {
        ArrayList<String> arrayFiles = new ArrayList<String>();
        // return null;
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
            coverageBeanlist = database.getCoverageData(prev_date);
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
                            database.deleteSpecificStoreData(coverageBeanlist.get(i).getStore_id());
                            database.updateStoreStatusOnLeave(coverageBeanlist.get(i).getStore_id(), coverageBeanlist.get(i).getVisitDate(), CommonString.KEY_U);
                        }
                        if (!result1.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                            return "COVERAGE_STATUS";
                        }
                        data.value = 100;
                        data.name = "COVERAGE_STATUS";
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

    public void showAlert(String str) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CheckoutNUpload.this);
        builder.setTitle("Parinaam");
        builder.setMessage(str).setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

}
