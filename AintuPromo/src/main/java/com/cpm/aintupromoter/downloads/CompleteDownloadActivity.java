package com.cpm.aintupromoter.downloads;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cpm.aintupromoter.Database.AintuREDB;
import com.cpm.aintupromoter.R;
import com.cpm.aintupromoter.constants.CommonString;
import com.cpm.aintupromoter.messgae.AlertMessage;
import com.cpm.aintupromoter.xmlGetterSetter.JourneyPlanGetterSetter;
import com.cpm.aintupromoter.xmlGetterSetter.NonWorkingReasonGetterSetter;
import com.cpm.aintupromoter.xmlGetterSetter.POSMDATAGetterSetter;
import com.cpm.aintupromoter.xmlGetterSetter.TableBean;
import com.cpm.aintupromoter.xmlHandlers.XMLHandlers;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;

/**
 * Created by jeevanp on 25-07-2017.
 */

public class CompleteDownloadActivity  extends AppCompatActivity{
    private Dialog dialog;
    private ProgressBar pb;
    private TextView percentage, message;
    private Data data;
    int eventType;
    JourneyPlanGetterSetter jcpgettersetter;
    POSMDATAGetterSetter posmgettersetter;
    NonWorkingReasonGetterSetter nonworkinggettersetter;
    TableBean tb;
    String _UserId;
    private SharedPreferences preferences;
    AintuREDB db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_manu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        _UserId = preferences.getString(CommonString.KEY_USERNAME, "");
        tb = new TableBean();
        db = new AintuREDB(this);
        new BackgroundTask(this).execute();
    }
    class Data {
        int value;
        String name;
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
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.custom);
            dialog.setCancelable(false);
            dialog.show();
            pb = (ProgressBar) dialog.findViewById(R.id.progressBar1);
            percentage = (TextView) dialog.findViewById(R.id.percentage);
            message = (TextView) dialog.findViewById(R.id.message);

        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub
            String resultHttp = "";
            try {

                data = new Data();

                // JCP

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();
                SoapObject request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD);
                request.addProperty("UserName", _UserId);
                request.addProperty("Type", "JOURNEY_PLAN");
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(CommonString.URL);

                androidHttpTransport.call(CommonString.SOAP_ACTION_UNIVERSAL, envelope);

                Object result = (Object) envelope.getResponse();

                if (result.toString() != null) {
                    xpp.setInput(new StringReader(result.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();

                    jcpgettersetter = XMLHandlers.JCPXMLHandler(xpp, eventType);

                    if (jcpgettersetter.getStore_cd().size() > 0) {
                        resultHttp = CommonString.KEY_SUCCESS;
                        String jcpTable = jcpgettersetter.getTable_journey_plan();
                        TableBean.setJcptable(jcpTable);
                    } else {
                        return "JOURNEY_PLAN";
                    }
                    data.value = 10;
                    data.name = "JCP Data Downloading";
                }
                publishProgress(data);

             // POSM_MASTER_WITH_FLAG data
                request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD);
                request.addProperty("UserName", _UserId);
                request.addProperty("Type", "POSM_MASTER_WITH_FLAG");
               // request.addProperty("Type", "POSM_MASTER");
                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                androidHttpTransport = new HttpTransportSE(CommonString.URL);
                androidHttpTransport.call(CommonString.SOAP_ACTION_UNIVERSAL, envelope);
                Object resultmapping = (Object) envelope.getResponse();
                if (resultmapping.toString() != null) {

                    xpp.setInput(new StringReader(resultmapping.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();
                    posmgettersetter = XMLHandlers.mappingpromotXML(xpp, eventType);
                    if (posmgettersetter.getMapping_POSM_table() != null) {
                        String mappingtable = posmgettersetter.getMapping_POSM_table();
                        TableBean.setMappingposmtable(mappingtable);
                    }
                    if (posmgettersetter.getPOSM_CD().size() > 0) {
                        resultHttp = CommonString.KEY_SUCCESS;
                        data.value = 55;
                        data.name = "POSM_MASTER Data Downloading";
                    } else {
                       return "POSM_MASTER";
                    }

                }
                publishProgress(data);


                //Non Working Reason data
            request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD);
                request.addProperty("UserName", _UserId);
                request.addProperty("Type", "NON_WORKING_REASON");
                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                androidHttpTransport = new HttpTransportSE(CommonString.URL);
                androidHttpTransport.call(CommonString.SOAP_ACTION_UNIVERSAL, envelope);
                Object resultnonworking = (Object) envelope.getResponse();
                if (resultnonworking.toString() != null) {

                    xpp.setInput(new StringReader(resultnonworking.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();
                    nonworkinggettersetter = XMLHandlers.nonWorkinReasonXML(xpp, eventType);
                    if (nonworkinggettersetter.getReason_cd().size() > 0) {
                        resultHttp = CommonString.KEY_SUCCESS;
                        String nonworkingtable = nonworkinggettersetter.getNonworking_table();
                        TableBean.setNonworkingtable(nonworkingtable);

                    }else {
                        return "NON_WORKING_REASON";
                    }
                    data.value = 90;
                    data.name = "Non Working Reason Downloading";
                    publishProgress(data);
                }

                db.open();
                db.insertJCPData(jcpgettersetter);
                db.insertPOSMata(posmgettersetter);
                db.insertNonWorkingReasonData(nonworkinggettersetter);
                data.value = 100;
                data.name = "Finishing";
                publishProgress(data);
                return resultHttp;
            } catch (MalformedURLException e) {
                final AlertMessage message = new AlertMessage(
                        CompleteDownloadActivity.this,
                        AlertMessage.MESSAGE_EXCEPTION, "download", e);
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        message.showMessage();
                    }
                });

            } catch (IOException e) {
                final AlertMessage message = new AlertMessage(
                        CompleteDownloadActivity.this,
                        AlertMessage.MESSAGE_SOCKETEXCEPTION, "socket", e);

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        message.showMessage();

                    }
                });

            } catch (Exception e) {
                final AlertMessage message = new AlertMessage(
                        CompleteDownloadActivity.this,
                        AlertMessage.MESSAGE_EXCEPTION + e, "download", e);

                e.getMessage();
                e.printStackTrace();
                e.getCause();
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

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
            if (result.equals(CommonString.KEY_SUCCESS)) {
                AlertMessage message = new AlertMessage(CompleteDownloadActivity.this,
                        AlertMessage.MESSAGE_DOWNLOAD, "success", null);
                message.showMessage();
            } else {
                AlertMessage message = new AlertMessage(CompleteDownloadActivity.this,
                        AlertMessage.MESSAGE_JCP_FALSE + result, "success", null);
                message.showMessage();
            }


        }

    }

}
