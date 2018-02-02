package com.cpm.aintupromoter.dailyentry;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;


import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cpm.aintupromoter.Database.AintuREDB;
import com.cpm.aintupromoter.R;
import com.cpm.aintupromoter.constants.CommonString;
import com.cpm.aintupromoter.messgae.AlertMessage;
import com.cpm.aintupromoter.xmlGetterSetter.CoverageBean;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@SuppressWarnings("deprecation")
public class CheckOutStoreActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private Dialog dialog;
    private ProgressBar pb;
    private TextView percentage, message;
    private String username, visit_date, store_id;
    double lattt,lonnnn;
    private Data data;
    private AintuREDB db;
    private SharedPreferences preferences = null;
    ImageView img_cam, img_clicked;
    ArrayList<CoverageBean> list_coverage = new ArrayList<>();
    String _pathforcheck, _path, str, img_str;

    private static final int REQUEST_LOCATION = 1;
   private   Double lat = 0.0, lon = 0.0;
   private GoogleApiClient mGoogleApiClient = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chckoutstore);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        username = preferences.getString(CommonString.KEY_USERNAME, "");
        visit_date = preferences.getString(CommonString.KEY_DATE, null);


        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

        }
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            return;
        }


        db = new AintuREDB(this);
        db.open();
        store_id = getIntent().getStringExtra(CommonString.KEY_STORE_CD);

        lattt= getIntent().getDoubleExtra(CommonString.KEY_LATITUDE,0.0);
        lonnnn= getIntent().getDoubleExtra(CommonString.KEY_LONGITUDE,0.0);



        list_coverage = db.getCoverageSpecificData(store_id);
        str = CommonString.FILE_PATH;
/*
        if (checkgpsEnableDevice() && !lat.toString().equals("0.0")) {
            if (list_coverage.size() > 0) {
                new BackgroundTask(CheckOutStoreActivity.this).execute();
            }
        }
*/
        if (list_coverage.size() > 0) {
            new BackgroundTask(CheckOutStoreActivity.this).execute();
        }


    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.i("MakeMachine", "resultCode: " + resultCode);
        switch (resultCode) {
            case 0:
                Log.i("MakeMachine", "User cancelled");
                break;
            case -1:
                if (_pathforcheck != null && !_pathforcheck.equals("")) {
                    if (new File(str + _pathforcheck).exists()) {
                        Bitmap bmp = BitmapFactory.decodeFile(str + _pathforcheck);
                        img_cam.setImageBitmap(bmp);
                        img_clicked.setVisibility(View.GONE);
                        img_cam.setVisibility(View.VISIBLE);
                        img_str = _pathforcheck;
                        _pathforcheck = "";
                    }
                }
                break;
            /*case REQUEST_LOCATION:
                switch (resultCode) {
                    case Activity.RESULT_CANCELED: {
                        mGoogleApiClient = null;
                    }
                    default: {
                        break;
                    }
                }
                break;*/
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    private boolean checkgpsEnableDevice() {
        boolean flag = true;
        // mGoogleApiClient = null;
        if (!hasGPSDevice(CheckOutStoreActivity.this)) {
            Toast.makeText(CheckOutStoreActivity.this, "Gps not Supported", Toast.LENGTH_SHORT).show();
        }
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(CheckOutStoreActivity.this)) {
            enableLoc();
            flag = false;
        } else if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(CheckOutStoreActivity.this)) {
            flag = true;
        }
        return flag;
    }

    private boolean hasGPSDevice(Context context) {
        final LocationManager mgr = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (mgr == null)
            return false;
        final List<String> providers = mgr.getAllProviders();
        if (providers == null)
            return false;
        return providers.contains(LocationManager.GPS_PROVIDER);
    }

    private void enableLoc() {
        //  mGoogleApiClient = null;
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle bundle) {
                            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                            if (mLastLocation != null) {
                                lat = mLastLocation.getLatitude();
                                lon = mLastLocation.getLongitude();
                            }
                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            mGoogleApiClient.connect();
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult connectionResult) {

                            Log.d("Location error", "Location error " + connectionResult.getErrorCode());
                        }
                    }).build();
            mGoogleApiClient.connect();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
            builder.setAlwaysShow(true);
            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(CheckOutStoreActivity.this, REQUEST_LOCATION);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                    }
                }
            });
        }


    }
    @Override
    public void onConnected(Bundle bundle) {
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            lat = mLastLocation.getLatitude();
            lon = mLastLocation.getLongitude();

        }

    }



    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    protected void onStart() {

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        super.onStart();

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
            dialog.setTitle("Sending Checkout Data");
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
                if (list_coverage.size() > 0) {
                    data = new Data();
                    data.value = 40;
                    data.name = "Checked out Data Uploading";
                    publishProgress(data);
                    String onXML = "[STORE_CHECK_OUT_STATUS][USER_ID]"
                            + username
                            + "[/USER_ID]" + "[STORE_ID]"
                            + store_id
                            + "[/STORE_ID][LATITUDE]"
                            //+ list_coverage.get(0).getLatitude()
                            + lattt
                            + "[/LATITUDE][LOGITUDE]"
                            //+ list_coverage.get(0).getLongitude()
                            + lonnnn
                            + "[/LOGITUDE][CHECKOUT_DATE]"
                            + visit_date
                            + "[/CHECKOUT_DATE][CHECK_OUTTIME]"
                            + "00:00:00"
                            + "[/CHECK_OUTTIME]" +
                            "[CHECK_INTIME]"
                            + "00:00:00"
                            + "[/CHECK_INTIME]" +
                            "[CREATED_BY]"
                            + username
                            + "[/CREATED_BY]" +
                            "[/STORE_CHECK_OUT_STATUS]";

                    final String sos_xml = "[DATA]" + onXML + "[/DATA]";
                    SoapObject request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_CHECKOUT_STATUS);
                    request.addProperty("onXML", sos_xml);
                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.dotNet = true;
                    envelope.setOutputSoapObject(request);
                    HttpTransportSE androidHttpTransport = new HttpTransportSE(CommonString.URL);
                    androidHttpTransport.call(CommonString.SOAP_ACTION + CommonString.METHOD_CHECKOUT_STATUS, envelope);
                    Object result_Upload_Store_ChecOut_Status = (Object) envelope.getResponse();
                    if (result_Upload_Store_ChecOut_Status.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                        db.open();
                        db.updateCoverageStoreOutTime(store_id, visit_date, CommonString.KEY_C);
                        long l1 = db.updateStoreStatusOnCheckout(store_id, visit_date, CommonString.KEY_C);
                        if (l1 == 0 || l1 == -1) {
                            db.updateStoreStatusOnCheckout(store_id, visit_date, CommonString.KEY_C);
                        }
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(CommonString.KEY_STORE_CD, "");
                        editor.commit();
                        data.value = 100;
                        data.name = "Checkout Done";
                        publishProgress(data);
                        return CommonString.KEY_SUCCESS;
                    } else {
                        if (!result_Upload_Store_ChecOut_Status.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                            return CommonString.METHOD_CHECKOUT_STATUS;
                        }
                    }
                }
            } catch (IOException e) {
                final AlertMessage message = new AlertMessage(CheckOutStoreActivity.this,
                        AlertMessage.MESSAGE_SOCKETEXCEPTION, "socketupload", e);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        message.showMessage();
                        // TODO Auto-generated method stub
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
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
                AlertMessage message = new AlertMessage(CheckOutStoreActivity.this, "Successfully Checked out", "checkout", null);
                message.showMessage();
                finish();
            } else if (result.equals("")) {
                Toast.makeText(getApplicationContext(), "Network Error Please Try Again", Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Network Error Please Try Again", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    class Data {
        int value;
        String name;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        db.open();
        enableLoc();
    }


}
