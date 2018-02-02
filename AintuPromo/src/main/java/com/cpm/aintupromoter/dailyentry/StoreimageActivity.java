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
import android.provider.Settings;
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
import com.cpm.aintupromoter.LoginActivity;
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
import java.util.Calendar;
import java.util.List;

import static com.cpm.aintupromoter.retrofit.RetrofitClass.context;

/**
 * Created by ashishc on 31-05-2016.
 */
public class StoreimageActivity extends AppCompatActivity implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    ImageView img_cam, img_clicked;
    Button btn_save;
    String _pathforcheck, _path, str;
    private Dialog dialog;
    private ProgressBar pb;
    Data data;
    String store_cd, visit_date, username, intime;
    private SharedPreferences preferences;
    AlertDialog alert;
    String img_str;
    private AintuREDB database;
    String lat = "0.0", lon = "0.0", app_ver;
    GoogleApiClient mGoogleApiClient;
    private TextView percentage, message;
    String datacheck = "";
    String[] words;
    String validity, mid = "";
    private GoogleApiClient googleApiClient;
    private static final int REQUEST_LOCATION = 1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storeimage);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        img_cam = (ImageView) findViewById(R.id.img_selfie);
        img_clicked = (ImageView) findViewById(R.id.img_cam_selfie);

        btn_save = (Button) findViewById(R.id.btn_save_selfie);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        store_cd = preferences.getString(CommonString.KEY_STORE_CD, null);
        visit_date = preferences.getString(CommonString.KEY_DATE, null);
        username = preferences.getString(CommonString.KEY_USERNAME, null);
        intime = preferences.getString(CommonString.KEY_STORE_IN_TIME, "");
        app_ver = preferences.getString(CommonString.KEY_VERSION, "");
        str = CommonString.FILE_PATH;
        database = new AintuREDB(this);
        database.open();
        img_cam.setOnClickListener(this);
        img_clicked.setOnClickListener(this);
        btn_save.setOnClickListener(this);
        checkgpsEnableDevice();
        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
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
    @Override
    public void onBackPressed() {
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        finish();
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.img_cam_selfie:
                _pathforcheck = store_cd + "_INTIME_IMG_" + visit_date.replace("/", "") + "_" + getCurrentTime().replace(":", "") + ".jpg";
                _path = CommonString.FILE_PATH + _pathforcheck;
                intime = getCurrentTime();
                startCameraActivity();
                break;
            case R.id.btn_save_selfie:
                if (checkgpsEnableDevice())
                    if (img_str != null) {
                        if (checkNetIsAvailable()) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                    StoreimageActivity.this);
                            builder.setMessage("Do you want to save the data ")
                                    .setCancelable(false)
                                    .setPositiveButton("OK",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    alert.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                                                    CoverageBean cdata = new CoverageBean();
                                                    cdata.setStore_id(store_cd);
                                                    cdata.setVisitDate(visit_date);
                                                    cdata.setUserId(username);
                                                    cdata.setInTime(getCurrentTime());
                                                    cdata.setOutTime("");
                                                    cdata.setReason("");
                                                    cdata.setReasonid("0");
                                                    cdata.setLatitude(lat);
                                                    cdata.setLongitude(lon);
                                                    cdata.setStore_image(img_str);
                                                    cdata.setCheckout_image("");
                                                    cdata.setRemark("");
                                                    cdata.setCoverage_status(CommonString.KEY_INVALID);
                                                    new BackgroundTask(StoreimageActivity.this, cdata).execute();
                                                }
                                            })
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        public void onClick(
                                                DialogInterface dialog,
                                                int id) {
                                            dialog.cancel();
                                        }
                                    });
                            alert = builder.create();
                            alert.show();
                        } else {
                            Snackbar.make(btn_save, "No Network Available", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                        }
                    } else {
                        Snackbar.make(btn_save, "Please click the image", Snackbar.LENGTH_SHORT).show();

                    }

                break;

        }

    }

    public boolean checkNetIsAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    protected void startCameraActivity() {
        try {
            Log.i("MakeMachine", "startCameraActivity()");
            File file = new File(_path);
            Uri outputFileUri = Uri.fromFile(file);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            startActivityForResult(intent, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private boolean checkgpsEnableDevice() {
        boolean flag = true;
        googleApiClient = null;
        if (!hasGPSDevice(StoreimageActivity.this)) {
            Toast.makeText(StoreimageActivity.this, "Gps not Supported", Toast.LENGTH_SHORT).show();
        }
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(StoreimageActivity.this)) {
            enableLoc();
            flag = false;
        } else if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(StoreimageActivity.this)) {
            flag = true;
        }
        return flag;
    }


    private boolean hasGPSDevice(Context context) {
        final LocationManager mgr = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        if (mgr == null)
            return false;
        final List<String> providers = mgr.getAllProviders();
        if (providers == null)
            return false;
        return providers.contains(LocationManager.GPS_PROVIDER);
    }

    private void enableLoc() {

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle bundle) {

                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            googleApiClient.connect();
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult connectionResult) {

                            Log.d("Location error", "Location error " + connectionResult.getErrorCode());
                        }
                    }).build();
            googleApiClient.connect();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
            builder.setAlwaysShow(true);
            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(StoreimageActivity.this, REQUEST_LOCATION);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                    }
                }
            });
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
                        //Set Clicked image to Imageview
                        img_str = _pathforcheck;
                        _pathforcheck = "";
                    }
                }
                break;
            case REQUEST_LOCATION:
                switch (resultCode) {
                    case Activity.RESULT_CANCELED: {
                        googleApiClient = null;
                    }
                    default: {
                        break;
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public String getCurrentTime() {
        Calendar m_cal = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String cdate = formatter.format(m_cal.getTime());
        return cdate;
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            lat = String.valueOf(mLastLocation.getLatitude());
            lon = String.valueOf(mLastLocation.getLongitude());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }


    private class BackgroundTask extends AsyncTask<Void, Data, String> {
        private Context context;
        private CoverageBean cdata;

        BackgroundTask(Context context, CoverageBean coverageBean) {
            this.cdata = coverageBean;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            dialog = new Dialog(context);
            dialog.setContentView(R.layout.custom_upload);
            dialog.setTitle("Sending Intime Store Data");
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
                data.name = "Intime Uploading";
                publishProgress(data);
                String onXML = "[DATA][USER_DATA][STORE_CD]"
                        + cdata.getStore_id()
                        + "[/STORE_CD]" + "[VISIT_DATE]"
                        + cdata.getVisitDate()
                        + "[/VISIT_DATE][LATITUDE]"
                        + cdata.getLatitude()
                        + "[/LATITUDE][APP_VERSION]"
                        + app_ver
                        + "[/APP_VERSION][LONGITUDE]"
                        + cdata.getLongitude()
                        + "[/LONGITUDE][IN_TIME]"
                        + "00:00:00"
                        + "[/IN_TIME][OUT_TIME]"
                        + "00:00:00"
                        + "[/OUT_TIME][UPLOAD_STATUS]"
                        + "N"
                        + "[/UPLOAD_STATUS][USER_ID]" + cdata.getUserId()
                        + "[/USER_ID][IMAGE_URL]" + cdata.getStore_image()
                        + "[/IMAGE_URL][REASON_ID]"
                        + cdata.getReasonid()
                        + "[/REASON_ID]"
                        + "[REASON_REMARK]"
                        + cdata.getRemark()
                        + "[/REASON_REMARK]"
                        + "[CHECKOUT_IMAGE]"
                        + cdata.getCheckout_image()
                        + "[/CHECKOUT_IMAGE]"
                        + "[/USER_DATA][/DATA]";

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
                    mid = (words[1]);
                    data.value = 100;
                    data.name = "Uploading..";
                    publishProgress(data);
                    return CommonString.KEY_SUCCESS;
                } else {
                    if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                        return CommonString.METHOD_UPLOAD_DR_STORE_COVERAGE;
                    }
                }
            } catch (IOException e) {
                final AlertMessage message =
                        new AlertMessage(StoreimageActivity.this,
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
            if (result.equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                database.open();
                database.InsertCoverageData(cdata);
                database.close();
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(CommonString.KEY_STOREVISITED_STATUS, "");
                editor.putString(CommonString.KEY_MID, mid);
                editor.commit();
                Intent in = new Intent(StoreimageActivity.this, ActivityManu.class);
                startActivity(in);
                finish();
                Toast.makeText(getApplicationContext(), "Intime Successfully Uploaded", Toast.LENGTH_SHORT).show();
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

}
