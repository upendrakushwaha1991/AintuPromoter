package com.cpm.aintupromoter.dailyentry;

import android.Manifest;
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
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cpm.aintupromoter.Database.AintuREDB;
import com.cpm.aintupromoter.GeoTag.GeoTagActivity;
import com.cpm.aintupromoter.GeoTag.GeoTagStoreList;
import com.cpm.aintupromoter.GetterSetter.GeotaggingBeans;
import com.cpm.aintupromoter.LoginActivity;
import com.cpm.aintupromoter.R;
import com.cpm.aintupromoter.constants.CommonString;
import com.cpm.aintupromoter.downloads.CompleteDownloadActivity;
import com.cpm.aintupromoter.messgae.AlertMessage;
import com.cpm.aintupromoter.upload.CheckoutNUpload;
import com.cpm.aintupromoter.xmlGetterSetter.CoverageBean;
import com.cpm.aintupromoter.xmlGetterSetter.JourneyPlanGetterSetter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by jeevanp on 25-07-2017.
 */
public class StoreListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    AintuREDB database;
    private SharedPreferences preferences;
    private String date;
    ListView lv;
    String store_cd, username;
    private SharedPreferences.Editor editor = null;
    private Dialog dialog;
    String user_type;
    LinearLayout nodata_linear;
    FloatingActionButton fab;
    ArrayList<JourneyPlanGetterSetter> jcplist;
    ArrayList<CoverageBean> coverage;
    String datacheck = "", app_ver;
    String[] words;
    private ProgressBar pb;
    Data data;
    String validity, mid = "";
    //Double lat=28.526349, lon=77.279482;
    Double lat = 0.0, lon = 0.0;
    private TextView percentage, message;
    LocationManager locationManager;
    private GoogleApiClient mGoogleApiClient;
    Geocoder geocoder;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private LocationRequest mLocationRequest;
    private static int UPDATE_INTERVAL = 200; // 5 sec
    private static int FATEST_INTERVAL = 100; // 1 sec
    private static int DISPLACEMENT = 2; // 10 meters
    private LocationManager locmanager = null;
    boolean enabled;
    private static final int REQUEST_LOCATION = 1;
    private Location mLastLocation;
    private GoogleApiClient client;
    private static final String TAG = StoreListActivity.class.getSimpleName();
    ArrayList<GeotaggingBeans> geolist = new ArrayList<>();


   /* private String storelat;
    private String storelongt;
    private String geotag;*/

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.storelistlayout);
        lv = (ListView) findViewById(R.id.list);
        nodata_linear = (LinearLayout) findViewById(R.id.no_data_lay);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        database = new AintuREDB(this);
        database.open();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        date = preferences.getString(CommonString.KEY_DATE, null);
        store_cd = preferences.getString(CommonString.KEY_STORE_CD, "");
        username = preferences.getString(CommonString.KEY_USERNAME, null);
        user_type = preferences.getString(CommonString.KEY_USER_TYPE, null);
        app_ver = preferences.getString(CommonString.KEY_VERSION, "");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        geocoder = new Geocoder(this);
        if (checkPlayServices()) {

            // Building the GoogleApi client
            buildGoogleApiClient();

            createLocationRequest();
        }

        locmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        enabled = locmanager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        checkgpsEnableDevice();
/*
        if (!enabled) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                    StoreListActivity.this);

            // Setting Dialog Title
            alertDialog.setTitle(getResources().getString(R.string.gps));

            // Setting Dialog Message
            alertDialog.setMessage(getResources().getString(R.string.gpsebale));

            // Setting Positive "Yes" Button
            alertDialog.setPositiveButton(getResources().getString(R.string.yes),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            Intent intent = new Intent(
                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    });

            // Setting Negative "NO" Button
            alertDialog.setNegativeButton(getResources().getString(R.string.no),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to invoke NO event

                            dialog.cancel();
                        }
                    });

            // Showing Alert Message
            alertDialog.show();

        }
*/


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Download data
                if (checkNetIsAvailable()) {
                    if (database.isCoverageDataFilled(date)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(StoreListActivity.this);
                        builder.setTitle("Parinaam");
                        builder.setMessage("Please Upload Previous Data First")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent startUpload = new Intent(StoreListActivity.this, CheckoutNUpload.class);
                                        startActivity(startUpload);
                                        finish();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    } else {
                        Intent startDownload = new Intent(StoreListActivity.this, CompleteDownloadActivity.class);
                        startActivity(startDownload);
                        finish();
                    }
                } else {
                    Snackbar.make(lv, "No Network Available", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                }
            }

        });

        client = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.notsuppoted)
                        , Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        mGoogleApiClient.connect();
        locmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        enabled = locmanager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        checkgpsEnableDevice();


        database.open();
        jcplist = database.getJCPData(date);
        coverage = database.getCoverageData(date);
        if (jcplist.size() > 0) {
            lv.setAdapter(new MyAdapter());
            lv.setOnItemClickListener(this);
            lv.setVisibility(View.VISIBLE);
            nodata_linear.setVisibility(View.GONE);
            fab.setVisibility(View.GONE);
        }

    }

    public boolean checkNetIsAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        // TODO Auto-generated method stub
        store_cd = jcplist.get(position).getStore_cd().get(0);
        final String upload_status = jcplist.get(position).getUploadStatus().get(0);
        final String checkoutstatus = jcplist.get(position).getCheckOutStatus().get(0);
        //usk
        editor = preferences.edit();
        editor.commit();
        ArrayList<CoverageBean> coverage_data = database.getCoverageData(date);

        if (upload_status.equals(CommonString.KEY_U)) {
            Snackbar.make(lv, "All Data Uploaded", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
        } else if (upload_status.equals(CommonString.KEY_D)) {
            Snackbar.make(lv, "Data Uploaded", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
        } else if (((checkoutstatus.equals(CommonString.KEY_C)))) {
            Snackbar.make(lv, "Store already checked out", Snackbar.LENGTH_SHORT).setAction("Action", null).show();

        } else {
            boolean enteryflag = true;
            if (coverage_data.size() > 0) {
                for (int i2 = 0; i2 < coverage_data.size(); i2++) {
                    if (coverage_data.get(i2).getStore_id().equalsIgnoreCase(store_cd)) {
                        if (coverage_data.get(i2).getCoverage_status().equals(CommonString.STORE_STATUS_LEAVE)) {
                            Snackbar.make(lv, "Store Already Closed", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                            enteryflag = false;
                            break;
                        }
                    }
                }
                if (enteryflag) {
                    for (int i = 0; i < coverage_data.size(); i++) {
                        if (coverage_data.get(i).getCoverage_status().equalsIgnoreCase(CommonString.KEY_VALID) ||
                                coverage_data.get(i).getCoverage_status().equalsIgnoreCase(CommonString.KEY_INVALID)) {
                            if (!coverage_data.get(i).getStore_id().equalsIgnoreCase(store_cd)) {
                                Snackbar.make(lv, "Please checkout from current store", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                                enteryflag = false;
                                break;
                            }
                        }
                    }
                }
            }
            if (enteryflag) {
                //  Toast.makeText(getApplicationContext(), "Lat>>" + lat + "Long>>" + lon, Toast.LENGTH_SHORT).show();

                showMyDialog(jcplist.get(position));
            }

        }

    }

    public boolean CheckNetAvailability() {

        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                .getState() == NetworkInfo.State.CONNECTED
                || connectivityManager.getNetworkInfo(
                ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            // we are connected to a network
            connected = true;
        }
        return connected;
    }

    public String getCurrentTime() {
        Calendar m_cal = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String cdate = formatter.format(m_cal.getTime());
        return cdate;
    }

    void showMyDialog(final JourneyPlanGetterSetter jcp) {

        final String storeCd = jcp.getStore_cd().get(0);
        final String storeName = jcp.getStore_name().get(0);
        final String checkout_status = jcp.getCheckOutStatus().get(0);
        final String storelat = jcp.getLATITUDE().get(0);
        final String storelongt = jcp.getLOGITUDE().get(0);
        final String geotag = jcp.getGeotag().get(0);

        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialogbox);
        final RadioGroup radioGroup = (RadioGroup) dialog.findViewById(R.id.radiogrpvisit);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected geolist
                if (checkedId == R.id.yes) {
                    geolist = database.getinsertGeotaggingData(storeCd);
                    if (geotag.equals("Y") || geolist.size() > 0) {

                        ArrayList<CoverageBean> specific_cdata = new ArrayList<CoverageBean>();
                        specific_cdata = database.getCoverageSpecificData(storeCd);
                        double storelat_, storelongt_;

                        if (specific_cdata.size() > 0) {
                            dialog.cancel();
                            Intent in = new Intent(StoreListActivity.this, ActivityManu.class);
                            startActivity(in);
                            overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                            finish();
                        } else {
                            dialog.cancel();
                            if (lat != 0.0 || lon != 0.0) {
                                if (enabled) {
                                    if (checkNetIsAvailable()) {
                                        // Toast.makeText(getApplicationContext(), "Lat>>" + lat + "Long>>" + lon, Toast.LENGTH_LONG).show();

                                        if (geotag.equals("Y")) {
                                            storelat_ = Double.parseDouble(storelat);
                                            storelongt_ = Double.parseDouble(storelongt);
                                        } else {
                                            storelat_ = geolist.get(0).getLatitude();
                                            storelongt_ = geolist.get(0).getLongitude();
                                        }

                                        int distance = distFrom(storelat_, storelongt_, lat, lon);
                                        //  Toast.makeText(getApplicationContext(), "distance>>" + distance, Toast.LENGTH_LONG).show();

                                        if (distance <= 100) {
                                            CoverageBean cdata = new CoverageBean();
                                            cdata.setStore_id(store_cd);
                                            cdata.setVisitDate(date);
                                            cdata.setUserId(username);
                                            cdata.setInTime(getCurrentTime());
                                            cdata.setOutTime("");
                                            cdata.setReason("");
                                            cdata.setReasonid("0");
                                            cdata.setLatitude(String.valueOf(lat));
                                            cdata.setLongitude(String.valueOf(lon));
                                            cdata.setStore_image("");
                                            cdata.setCheckout_image("");
                                            cdata.setRemark("");
                                            cdata.setCoverage_status(CommonString.KEY_INVALID);
                                            new BackgroundTask(StoreListActivity.this, cdata).execute();
                                        } else {
                                            Snackbar.make(lv,
                                                    "You need to be in the store to login/logout\n Distance from Store - " + distance, Snackbar.LENGTH_LONG).show();
                                        }
                                    } else {
                                        Snackbar.make(lv, "No Network Available", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                                    }
                                } else {

                                }
                            } else {
                                Snackbar.make(lv, "Wait for Location", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                            }

                        }


                    } else
                        Snackbar.make(radioGroup, "Please first the Geotag store", Snackbar.LENGTH_SHORT).setAction("Action", null).show();

                } else if (checkedId == R.id.no) {
                    dialog.cancel();
                    if (checkout_status.equals(CommonString.KEY_INVALID) || checkout_status.equals(CommonString.KEY_VALID)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(StoreListActivity.this);
                        builder.setMessage(CommonString.DATA_DELETE_ALERT_MESSAGE)
                                .setCancelable(false)
                                .setPositiveButton("Yes",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,
                                                                int id) {
                                                new Task().execute(storeCd);
                                            }
                                        })
                                .setNegativeButton("No",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,
                                                                int id) {
                                                dialog.cancel();
                                            }
                                        });
                        AlertDialog alert = builder.create();

                        alert.show();
                    } else {
                        UpdateData(storeCd);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(CommonString.KEY_STORE_CD, storeCd);
                        editor.putString(CommonString.KEY_STOREVISITED_STATUS, "");
                        editor.commit();
                        Intent in = new Intent(StoreListActivity.this, NonWorkingReason.class);
                        startActivity(in);
                    }
                }
            }

        });
        dialog.show();
    }

    public void UpdateData(String storeCd) {
        database.open();
        database.deleteSpecificStoreData(storeCd);
        database.updateStoreStatusOnCheckout(storeCd, jcplist.get(0).getVISIT_DATE().get(0), "N");
    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return jcplist.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.storelistrow, null);
                holder.storename = (TextView) convertView.findViewById(R.id.storelistviewxml_storename);
                holder.city = (TextView) convertView.findViewById(R.id.storelistviewxml_name);
                holder.keyaccount = (TextView) convertView.findViewById(R.id.storelistviewxml_storeaddress);
                holder.img = (ImageView) convertView.findViewById(R.id.storelistviewxml_storeico);
                holder.checkout = (Button) convertView.findViewById(R.id.chkout);
                holder.card_view = (CardView) convertView.findViewById(R.id.card_view);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.checkout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if (CheckNetAvailability()) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(StoreListActivity.this);
                        builder.setTitle("Parinaam").setMessage(R.string.alertmessage);
                        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //upendra new code
                                database.open();
                                jcplist = database.getJCPstoreId(store_cd);
                                geolist = database.getinsertGeotaggingData(store_cd);
                                final String  geotagg=jcplist.get(0).getGeotag().get(0);
                                final String  storelat = jcplist.get(0).getLATITUDE().get(0);
                                final String storelongt = jcplist.get(0).getLOGITUDE().get(0);
                              /*  if (geotag.equals("Y") || geolist.size() > 0) {*/
                                double storelat_, storelongt_;
                                if (lat != 0.0 || lon != 0.0) {
                                    if (enabled) {
                                        if (checkNetIsAvailable()) {

                                            // Toast.makeText(getApplicationContext(), "Lat>>" + lat + "Long>>" + lon, Toast.LENGTH_LONG).show();
                                            if (geotagg.equals("Y")) {
                                                storelat_ = Double.parseDouble(storelat);
                                                storelongt_ = Double.parseDouble(storelongt);
                                            } else {
                                                storelat_ = geolist.get(0).getLatitude();
                                                storelongt_ = geolist.get(0).getLongitude();
                                            }

                                            int distance = distFrom(storelat_, storelongt_, lat, lon);
                                            //  Toast.makeText(getApplicationContext(), "distance>>" + distance, Toast.LENGTH_LONG).show();

                                            if (distance <= 100) {
                                                Intent i = new Intent(StoreListActivity.this, CheckOutStoreActivity.class);
                                                i.putExtra(CommonString.KEY_STORE_CD, jcplist.get(0).getStore_cd().get(0));
                                                i.putExtra(CommonString.KEY_LATITUDE, lat);
                                                i.putExtra(CommonString.KEY_LONGITUDE, lon);
                                                startActivity(i);
                                                dialog.dismiss();


                                            } else {
                                                Snackbar.make(lv,
                                                        "You need to be in the store to login/logout\n Distance from Store - " + distance, Snackbar.LENGTH_LONG).show();
                                            }
                                        } else {
                                            Snackbar.make(lv, "No Network Available", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                                        }
                                    } else {

                                    }
                                } else {
                                    Snackbar.make(lv, "Wait for Location", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                                }
                            }/*else {
                                    Snackbar.make(lv, "Please first the Geotag store", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                                }*/

/*                                if (geotag.equals("Y") || geolist.size() > 0) {

                                    ArrayList<CoverageBean> specific_cdata = new ArrayList<CoverageBean>();
                                    specific_cdata = database.getCoverageSpecificData(store_cd);
                                    double storelat_, storelongt_;

                                    if (specific_cdata.size() > 0) {
                                        dialog.cancel();

                                        Intent in = new Intent(StoreListActivity.this, ActivityManu.class);
                                        startActivity(in);
                                        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                                        finish();

                                    } else {
                                        dialog.cancel();
                                        if (lat != 0.0 || lon != 0.0) {
                                            if (enabled) {
                                                if (checkNetIsAvailable()) {
                                                    // Toast.makeText(getApplicationContext(), "Lat>>" + lat + "Long>>" + lon, Toast.LENGTH_LONG).show();

                                                    if (geotag.equals("Y")) {
                                                        storelat_ = Double.parseDouble(storelat);
                                                        storelongt_ = Double.parseDouble(storelongt);
                                                    } else {
                                                        storelat_ = geolist.get(0).getLatitude();
                                                        storelongt_ = geolist.get(0).getLongitude();
                                                    }

                                                    int distance = distFrom(storelat_, storelongt_, lat, lon);
                                                    //  Toast.makeText(getApplicationContext(), "distance>>" + distance, Toast.LENGTH_LONG).show();

                                                    if (distance <= 100) {
                                                        Intent i = new Intent(StoreListActivity.this, CheckOutStoreActivity.class);
                                                        i.putExtra(CommonString.KEY_STORE_CD, jcplist.get(position).getStore_cd().get(0));
                                                        i.putExtra(CommonString.KEY_LATITUDE, lat);
                                                        i.putExtra(CommonString.KEY_LONGITUDE, lon);
                                                        startActivity(i);
                                                        dialog.dismiss();


                                                    } else {
                                                        Snackbar.make(lv,
                                                                "You need to be in the store to login/logout\n Distance from Store - " + distance, Snackbar.LENGTH_LONG).show();
                                                    }
                                                } else {
                                                    Snackbar.make(lv, "No Network Available", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                                                }
                                            } else {

                                            }
                                        } else {
                                            Snackbar.make(lv, "Wait for Location", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                                        }

                                    }


                                } else
                                    Snackbar.make(lv, "Please first the Geotag store", Snackbar.LENGTH_SHORT).setAction("Action", null).show();

                            }*/
                            //



                               /* Intent i = new Intent(StoreListActivity.this, CheckOutStoreActivity.class);
                                i.putExtra(CommonString.KEY_STORE_CD, jcplist.get(position).getStore_cd().get(0));
                                i.putExtra(CommonString.KEY_LATITUDE, lat);
                                i.putExtra(CommonString.KEY_LONGITUDE, lon);
                                startActivity(i);
                                dialog.dismiss();*/

                        });
                        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        builder.show();
                    } else {
                        Snackbar.make(lv, "No Network Available", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                    }

                }
            });
            String storecd = jcplist.get(position).getStore_cd().get(0);
            //  final String geotag = jcplist.get(position).getGeotag().get(0);
            ArrayList<CoverageBean> coverage_data = database.getCoverageSpecificData(storecd);

            if (jcplist.get(position).getUploadStatus().get(0).equals(CommonString.KEY_U)) {
                holder.img.setVisibility(View.VISIBLE);
                holder.img.setBackgroundResource(R.drawable.tick);
                holder.checkout.setVisibility(View.INVISIBLE);
            } else if ((jcplist.get(position).getUploadStatus().get(0).equals(CommonString.KEY_D))) {
                holder.img.setVisibility(View.INVISIBLE);
                holder.img.setBackgroundResource(R.drawable.tick_d);
                holder.img.setVisibility(View.VISIBLE);
                holder.checkout.setVisibility(View.INVISIBLE);
            } else if ((jcplist.get(position).getCheckOutStatus().get(0).equals(CommonString.KEY_C))) {
                holder.img.setVisibility(View.VISIBLE);
                holder.img.setBackgroundResource(R.drawable.exclamation);
                holder.checkout.setVisibility(View.INVISIBLE);
            } else if (coverage_data.size() > 0) {
                if (coverage_data.get(0).getCoverage_status().equals(CommonString.STORE_STATUS_LEAVE)) {
                    holder.img.setBackgroundResource(R.drawable.leave_tick);
                    holder.img.setVisibility(View.VISIBLE);
                    holder.checkout.setVisibility(View.INVISIBLE);
                } else if (coverage_data.get(0).getCoverage_status().equals(CommonString.KEY_INVALID)
                        || coverage_data.get(0).getCoverage_status().equals(CommonString.KEY_VALID)) {
                    holder.checkout.setBackgroundResource(R.drawable.checkout);
                    holder.checkout.setVisibility(View.VISIBLE);
                    holder.checkout.setEnabled(true);
                    holder.img.setVisibility(View.INVISIBLE);
                    holder.card_view.setCardBackgroundColor(Color.GREEN);
                } else {
                    holder.card_view.setCardBackgroundColor(Color.parseColor("#FFE0B2"));
                }
            } else {
                holder.checkout.setEnabled(false);
                holder.checkout.setVisibility(View.INVISIBLE);
                holder.img.setVisibility(View.VISIBLE);
                holder.img.setBackgroundResource(R.drawable.store);
                holder.card_view.setCardBackgroundColor(Color.parseColor("#FFE0B2"));
            }

            holder.storename.setText(jcplist.get(position).getStore_name().get(0));
            holder.city.setText(jcplist.get(position).getCity().get(0));
            holder.keyaccount.setText(jcplist.get(position).getStore_address().get(0));
            return convertView;
        }

        private class ViewHolder {
            TextView storename, city, keyaccount;
            ImageView img;
            Button checkout;
            CardView card_view;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        }
        return super.onOptionsItemSelected(item);
    }

    class Task extends AsyncTask<String, String, String> {
        String storeCd;

        @Override
        protected String doInBackground(String... params) {
            try {
                SoapObject request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_UPLOAD_COVERAGE_REMOVE);
                request.addProperty("STORE_CD", params[0]);
                request.addProperty("USER_ID", username);
                request.addProperty("VISIT_DATE", date);
                storeCd = params[0];
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(CommonString.URL);
                androidHttpTransport.call(CommonString.SOAP_ACTION + CommonString.METHOD_UPLOAD_COVERAGE_REMOVE, envelope);
                Object result = (Object) envelope.getResponse();
                datacheck = result.toString();
                datacheck = datacheck.replace("\"", "");
                words = datacheck.split("\\;");
                validity = (words[0]);

                if (validity.equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                    return CommonString.KEY_SUCCESS;
                } else {
                    return CommonString.KEY_FAILURE;
                }
            } catch (MalformedURLException e) {
                ShowAlert2(AlertMessage.MESSAGE_EXCEPTION);
            } catch (IOException e) {
                ShowAlert2(AlertMessage.MESSAGE_SOCKETEXCEPTION);
            } catch (Exception e) {
                ShowAlert2(AlertMessage.MESSAGE_EXCEPTION);
            }

            return "";
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //  pd.dismiss();
            if (result.equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                Toast.makeText(getApplicationContext(), "Coverage deleted Successfully", Toast.LENGTH_SHORT).show();
                UpdateData(storeCd);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(CommonString.KEY_STORE_CD, storeCd);
                editor.putString(CommonString.KEY_STOREVISITED_STATUS, "");
                editor.commit();
                Intent in = new Intent(StoreListActivity.this, NonWorkingReason.class);
                startActivity(in);
                overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
            } else if (result.equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                Toast.makeText(getApplicationContext(), "Error in deleted coverage", Toast.LENGTH_SHORT).show();
            } else if (!result.equals("")) {
                Toast.makeText(getApplicationContext(), "Coverage not deleted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void ShowAlert2(String str) {

        AlertDialog.Builder builder = new AlertDialog.Builder(StoreListActivity.this);
        builder.setTitle("Error");
        builder.setMessage(str).setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    @Override
    public void onConnected(Bundle bundle) {

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (mLastLocation != null) {
                lat = mLastLocation.getLatitude();
                lon = mLastLocation.getLongitude();
                //  Toast.makeText(getApplicationContext(), "onconnected lat-" + lat + " Long-" + lon, Toast.LENGTH_SHORT).show();

            }
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

       // Toast.makeText(this,  " WORKS_lat_lon " + latLng, Toast.LENGTH_LONG).show();
      //  updateLocation(latLng);
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    protected void onStart() {
        super.onStart();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
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
                final AlertMessage message = new AlertMessage(StoreListActivity.this, AlertMessage.MESSAGE_SOCKETEXCEPTION, "socketupload", e);
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
                database.updateStoreStatusOnCheckout(cdata.getStore_id(), date, CommonString.KEY_INVALID);
                database.close();
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(CommonString.KEY_STOREVISITED_STATUS, "");
                editor.putString(CommonString.KEY_MID, mid);
                editor.commit();
                Intent in = new Intent(StoreListActivity.this, ActivityManu.class);
                startActivity(in);

                overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                finish();
                Toast.makeText(getApplicationContext(), "Intime Successfully Uploaded", Toast.LENGTH_SHORT).show();
            } else if (result.equals("")) {
                Toast.makeText(getApplicationContext(), "Network Error Try Again", Toast.LENGTH_SHORT).show();
            } else if (!result.equals("")) {
                Toast.makeText(getApplicationContext(), "Network Error Try Again", Toast.LENGTH_SHORT).show();
            }
        }
    }

    class Data {
        int value;
        String name;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("MakeMachine", "resultCode: " + resultCode);
        switch (resultCode) {
            case REQUEST_LOCATION:
                switch (resultCode) {
                    case Activity.RESULT_CANCELED: {
                        mGoogleApiClient = null;
                    }
                    default: {
                        break;
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public static int distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        int dist = (int) (earthRadius * c);

        return dist;
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    protected void startLocationUpdates() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (mGoogleApiClient != null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
               // Toast.makeText(getApplicationContext(), "startLocation - Lat" + lat + "Long" + lon, Toast.LENGTH_LONG).show();
            }
        }

    }


    private boolean checkgpsEnableDevice() {
        boolean flag = true;
        if (!hasGPSDevice(StoreListActivity.this)) {
            Toast.makeText(StoreListActivity.this, "Gps not Supported", Toast.LENGTH_SHORT).show();
        }
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(StoreListActivity.this)) {
            enableLoc();
            flag = false;
        } else if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(StoreListActivity.this)) {
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
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

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
                            status.startResolutionForResult(StoreListActivity.this, REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                }
            }
        });
    }


    private void updateLocation(LatLng centerLatLng) {
        if (centerLatLng != null) {
            Geocoder geocoder = new Geocoder(StoreListActivity.this,
                    Locale.getDefault());
            List<Address> addresses = new ArrayList<Address>();
            try {
                addresses = geocoder.getFromLocation(centerLatLng.latitude,
                        centerLatLng.longitude, 1);

            } catch (IOException e) {
                e.printStackTrace();
            }

            if (addresses != null && addresses.size() > 0) {


                String addressIndex0 = (addresses.get(0).getAddressLine(0) != null) ? addresses
                        .get(0).getAddressLine(0) : null;
                String addressIndex1 = (addresses.get(0).getAddressLine(1) != null) ? addresses
                        .get(0).getAddressLine(1) : null;
                String addressIndex2 = (addresses.get(0).getAddressLine(2) != null) ? addresses
                        .get(0).getAddressLine(2) : null;
                String addressIndex3 = (addresses.get(0).getAddressLine(3) != null) ? addresses
                        .get(0).getAddressLine(3) : null;
                String completeAddress = addressIndex0 + "," + addressIndex1;

                if (addressIndex2 != null) {
                    completeAddress += "," + addressIndex2;
                }
                if (addressIndex3 != null) {
                    completeAddress += "," + addressIndex3;

                }
                if (completeAddress != null) {

                    Toast.makeText(getApplicationContext(), "addresslatlon" + centerLatLng, Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), "iiiiiiiiiiiiiii>>>" + addresses.get(0).getCountryName(), Toast.LENGTH_SHORT).show();
                    System.out.println("iiiiiiiiiiiiiiiiiiiiii" + addresses.get(0).getCountryName());
                    //   countryname=addresses.get(0).getCountryCode();

                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Disconnect from API onPause()
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }
}
