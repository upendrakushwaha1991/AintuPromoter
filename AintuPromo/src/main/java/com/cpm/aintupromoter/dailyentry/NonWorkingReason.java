package com.cpm.aintupromoter.dailyentry;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.cpm.aintupromoter.Database.AintuREDB;
import com.cpm.aintupromoter.R;
import com.cpm.aintupromoter.constants.CommonString;
import com.cpm.aintupromoter.xmlGetterSetter.CoverageBean;
import com.cpm.aintupromoter.xmlGetterSetter.JourneyPlanGetterSetter;
import com.cpm.aintupromoter.xmlGetterSetter.NonWorkingReasonGetterSetter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import com.google.android.gms.location.ActivityRecognition;

public class NonWorkingReason extends AppCompatActivity implements
        OnItemSelectedListener, OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    ArrayList<NonWorkingReasonGetterSetter> reasondata = new ArrayList<>();
    private Spinner reasonspinner;
    private AintuREDB database;
    String reasonname = "", reasonid = "", entry_allow = "", image_allow = "", intime = "";
    Button save;
    private ArrayAdapter<CharSequence> reason_adapter;
    protected String _path, str;
    protected String _pathforcheck = "";
    private String image1 = "";
    private SharedPreferences preferences;
    String _UserId, visit_date, store_id;
    protected boolean status = true;
    EditText text;
    AlertDialog alert;
    ImageButton camera;
    RelativeLayout reason_lay, rel_cam;
    boolean leave_flag = false;
    ArrayList<JourneyPlanGetterSetter> jcp;
    private GoogleApiClient mGoogleApiClient = null;;
    String lat = "0.0", lon = "0.0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nonworking);
        reasonspinner = (Spinner) findViewById(R.id.spinner2);
        camera = (ImageButton) findViewById(R.id.imgcam);
        save = (Button) findViewById(R.id.save);
        text = (EditText) findViewById(R.id.reasontxt);
        reason_lay = (RelativeLayout) findViewById(R.id.layout_reason);
        rel_cam = (RelativeLayout) findViewById(R.id.relimgcam);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        _UserId = preferences.getString(CommonString.KEY_USERNAME, "");
        visit_date = preferences.getString(CommonString.KEY_DATE, null);
        store_id = preferences.getString(CommonString.KEY_STORE_CD, "");
        database = new AintuREDB(this);
        database.open();
        str = CommonString.FILE_PATH;
        reasondata = database.getNonWorkingData();
        intime = getCurrentTime();
        camera.setOnClickListener(this);
        save.setOnClickListener(this);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();


/*
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addApi(ActivityRecognition.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
*/
        }

        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        reason_adapter = new ArrayAdapter<CharSequence>(this, R.layout.spinner_custom_item);
        reason_adapter.add("Select Reason");
        for (int i = 0; i < reasondata.size(); i++) {
            reason_adapter.add(reasondata.get(i).getReason().get(0));
        }
        reasonspinner.setAdapter(reason_adapter);
        reason_adapter.setDropDownViewResource(R.layout.spinner_custom_item);
        reasonspinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        finish();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position,
                               long arg3) {
        // TODO Auto-generated method stub

        switch (arg0.getId()) {
            case R.id.spinner2:
                if (position != 0) {
                    reasonname = reasondata.get(position - 1).getReason().get(0);
                    reasonid = reasondata.get(position - 1).getReason_cd().get(0);
                    entry_allow = reasondata.get(position - 1).getEntry_allow().get(0);
                    image_allow = reasondata.get(position - 1).getIMAGE_ALLOW().get(0);
                    if (image_allow.equals("1")) {
                        rel_cam.setVisibility(View.VISIBLE);
                        reason_lay.setVisibility(View.VISIBLE);
                    } else {
                        if (image_allow.equals("0"))
                            rel_cam.setVisibility(View.GONE);
                        reason_lay.setVisibility(View.VISIBLE);
                    }
                } else {
                    reasonname = "";
                    reasonid = "";
                    image_allow = "";
                    entry_allow = "";
                }
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }

    protected void startCameraActivity() {

        try {
            Log.i("MakeMachine", "startCameraActivity()");
            File file = new File(_path);
            Uri outputFileUri = Uri.fromFile(file);
            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            startActivityForResult(intent, 0);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
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
                        camera.setImageDrawable(getResources().getDrawable(R.drawable.cam_icon_done));
                        //camera.setBackgroundResource(R.drawable.camera_list_tick);
                        image1 = _pathforcheck;
                        _pathforcheck = "";
                    }
                }

                break;
        }
    }

    public boolean imageAllowed() {
        boolean result = true;
        //if (image_allow.equalsIgnoreCase("")) {
        if (image_allow.equals("1")) {
            if (image1.equalsIgnoreCase("")) {
                result = false;
            }
        }

        return result;

    }

    public boolean textAllowed() {
        boolean result = true;
        if (text.getText().toString().trim().equals("")) {
            result = false;
        }

        return result;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v.getId() == R.id.imgcam) {
            _pathforcheck = store_id + "_NONWORK_IMG_" + visit_date.replace("/", "") + "_" + getCurrentTime().replace(":", "") + ".jpg";
            _path = CommonString.FILE_PATH + _pathforcheck;
            startCameraActivity();
        }
        if (v.getId() == R.id.save) {
            if (validatedata()) {
                if (imageAllowed()) {
                    if (textAllowed()) {
                        if (isDataUploaded()) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(NonWorkingReason.this);
                            builder.setMessage("Do you want to save the data ")
                                    .setCancelable(false)
                                    .setPositiveButton("OK",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface dialog,
                                                        int id) {
                                                    alert.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                                                    if (entry_allow.equals("0")) {
                                                        database.deleteAllTables();
                                                        jcp = database.getJCPData(visit_date);
                                                        for (int i = 0; i < jcp.size(); i++) {
                                                            String stoteid = jcp.get(i).getStore_cd().get(0);
                                                            CoverageBean cdata = new CoverageBean();
                                                            cdata.setStore_id(store_id);
                                                            cdata.setVisitDate(visit_date);
                                                            cdata.setUserId(_UserId);
                                                            cdata.setInTime(intime);
                                                            cdata.setOutTime(getCurrentTime());
                                                            cdata.setReason(reasonname);
                                                            cdata.setReasonid(reasonid);
                                                            cdata.setLatitude(lat);
                                                            cdata.setLongitude(lon);
                                                            cdata.setStore_image(image1);
                                                            cdata.setCheckout_image("");
                                                            cdata.setRemark(text
                                                                    .getText()
                                                                    .toString()
                                                                    .replaceAll(
                                                                            "[&^<>{}'$]",
                                                                            " "));
                                                            cdata.setCoverage_status(CommonString.STORE_STATUS_LEAVE);
                                                            database.InsertCoverageData(cdata);
                                                            database.updateStoreStatusOnLeave(store_id, visit_date, CommonString.STORE_STATUS_LEAVE);
                                                            SharedPreferences.Editor editor = preferences.edit();
                                                            editor.putString(CommonString.KEY_STOREVISITED_STATUS + stoteid, "No");
                                                            editor.putString(CommonString.KEY_STOREVISITED_STATUS, "");
                                                            editor.commit();
                                                        }

                                                    } else {
                                                        CoverageBean cdata = new CoverageBean();
                                                        cdata.setStore_id(store_id);
                                                        cdata.setVisitDate(visit_date);
                                                        cdata.setUserId(_UserId);
                                                        cdata.setInTime(intime);
                                                        cdata.setOutTime(getCurrentTime());
                                                        cdata.setReason(reasonname);
                                                        cdata.setReasonid(reasonid);
                                                        cdata.setLatitude(lat);
                                                        cdata.setLongitude(lon);
                                                        cdata.setStore_image(image1);
                                                        cdata.setCheckout_image("");
                                                        cdata.setRemark(text
                                                                .getText()
                                                                .toString()
                                                                .replaceAll(
                                                                        "[&^<>{}'$]",
                                                                        " "));
                                                        cdata.setCoverage_status(CommonString.STORE_STATUS_LEAVE);
                                                        database.InsertCoverageData(cdata);
                                                        database.updateStoreStatusOnLeave(store_id, visit_date, CommonString.STORE_STATUS_LEAVE);
                                                        SharedPreferences.Editor editor = preferences.edit();
                                                        editor.putString(CommonString.KEY_STOREVISITED_STATUS + store_id, "No");
                                                        editor.putString(CommonString.KEY_STOREVISITED_STATUS, "");
                                                        editor.commit();
                                                    }
                                                    finish();
                                                }
                                            })
                                    .setNegativeButton("Cancel",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface dialog,
                                                        int id) {
                                                    dialog.cancel();
                                                }
                                            });

                            alert = builder.create();
                            alert.show();
                        } else {
                            Snackbar.make(reasonspinner,
                                    "Data has been uploaded for some" + " stores, please select another reason", Snackbar.LENGTH_LONG).show();
                        }

                    } else {
                        Snackbar.make(reasonspinner,
                                "Please enter required remark reason", Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    Snackbar.make(reasonspinner,
                            "Please Capture Image", Snackbar.LENGTH_SHORT).show();
                }
            } else {
                Snackbar.make(reasonspinner,
                        "Please Select a Reason", Snackbar.LENGTH_SHORT).show();

            }
        }

    }

    public boolean validatedata() {
        boolean result = false;
        if (reasonid != null && !reasonid.equalsIgnoreCase("")) {
            result = true;
        }

        return result;

    }

    public boolean isDataUploaded() {
        jcp = database.getJCPData(visit_date);
        boolean flag = true;
        if (entry_allow.equals("0")) {
            if (jcp.size() > 0) {
                for (int i = 0; i < jcp.size(); i++) {
                    if (jcp.get(i).getUploadStatus().get(0).equalsIgnoreCase(CommonString.KEY_U) ||
                            jcp.get(i).getUploadStatus().get(0).equalsIgnoreCase(CommonString.KEY_D)) {
                        flag = false;
                        break;
                    }
                }
            }
        }

        return flag;
    }

    public String getCurrentTime() {
        Calendar m_cal = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String cdate = formatter.format(m_cal.getTime());
        return cdate;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        }
        return super.onOptionsItemSelected(item);
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
        if (mGoogleApiClient!=null){
            mGoogleApiClient.connect();
        }
        super.onStart();
      /*  mGoogleApiClient.connect();
        super.onStart();*/
    }

    protected void onStop() {
       /* mGoogleApiClient.disconnect();
        super.onStop();*/
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

}
