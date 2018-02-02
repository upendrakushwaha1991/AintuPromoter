package com.cpm.aintupromoter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.cpm.aintupromoter.Database.AintuREDB;
import com.cpm.aintupromoter.GeoTag.GeoTagStoreList;
import com.cpm.aintupromoter.constants.CommonString;
import com.cpm.aintupromoter.dailyentry.StoreListActivity;
import com.cpm.aintupromoter.downloads.CompleteDownloadActivity;
import com.cpm.aintupromoter.messgae.AlertMessage;
import com.cpm.aintupromoter.upload.CheckoutNUpload;
import com.cpm.aintupromoter.upload.UploadDataActivity;
import com.cpm.aintupromoter.xmlGetterSetter.CoverageBean;
import com.cpm.aintupromoter.xmlGetterSetter.DemosGetterSetter;
import com.cpm.aintupromoter.xmlGetterSetter.JourneyPlanGetterSetter;
import com.cpm.aintupromoter.xmlGetterSetter.POSMDATAGetterSetter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MainManuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    WebView webView;
    ImageView imageView;
    String date, url;
    private SharedPreferences preferences = null;
    String user_name, error_msg;
    Toolbar toolbar;
    View headerView;
    AintuREDB database;
    ArrayList<JourneyPlanGetterSetter> jcplist;
    boolean poasm_flag = false, demos_flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_manu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        imageView = (ImageView) findViewById(R.id.img_main);
        webView = (WebView) findViewById(R.id.webview);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        date = preferences.getString(CommonString.KEY_DATE, null);
        user_name = preferences.getString(CommonString.KEY_USERNAME, null);
        url = preferences.getString(CommonString.KEY_NOTICE_BOARD_LINK, "");
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        headerView = LayoutInflater.from(this).inflate(R.layout.nav_header_main, navigationView, false);
        TextView tv_username = (TextView) headerView.findViewById(R.id.nav_user_name);
        tv_username.setText(user_name);
        navigationView.addHeaderView(headerView);
        navigationView.setNavigationItemSelectedListener(this);
        database = new AintuREDB(this);
        database.open();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public boolean validate_data() {
        boolean result = false;
        database.open();
        ArrayList<CoverageBean> cdata;
        JourneyPlanGetterSetter storestatus;
        cdata = database.getCoverageData(date);
        if (cdata.size() > 0) {
            for (int i = 0; i < cdata.size(); i++) {
                storestatus = database.getStoreStatus(cdata.get(i).getStore_id());
                if (!storestatus.getUploadStatus().get(0).equalsIgnoreCase(CommonString.KEY_U)) {
                    if ((storestatus.getCheckOutStatus().get(0).equalsIgnoreCase(CommonString.KEY_C)
                            || storestatus.getUploadStatus().get(0).equalsIgnoreCase(CommonString.KEY_P)
                            || storestatus.getUploadStatus().get(0).equalsIgnoreCase(CommonString.STORE_STATUS_LEAVE)
                            || cdata.get(i).getCoverage_status().equalsIgnoreCase(CommonString.STORE_STATUS_LEAVE))
                            || storestatus.getUploadStatus().get(0).equalsIgnoreCase(CommonString.KEY_D)) {
                        result = true;
                        break;
                    }
                }
            }
        }

        return result;
    }

    public boolean checkNetIsAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    public boolean validate_dataFor_image() {
        boolean result = false;
        database.open();
        JourneyPlanGetterSetter storestatus;
        jcplist = database.getJCPData(date);
        if (jcplist.size() > 0) {
            for (int i = 0; i < jcplist.size(); i++) {
                storestatus = database.getStoreStatus(jcplist.get(i).getStore_cd().get(0));
                if (storestatus.getUploadStatus().get(0).equalsIgnoreCase(CommonString.KEY_D) ||
                        storestatus.getUploadStatus().get(0).equalsIgnoreCase(CommonString.STORE_STATUS_LEAVE)) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    public ArrayList<String> getFileNames(File[] file) {
        ArrayList<String> arrayFiles = new ArrayList<String>();
        if (file.length > 0) {
            for (int i = 0; i < file.length; i++)
                arrayFiles.add(file[i].getName());
        }
        return arrayFiles;
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_route_plan) {
            Intent startDownload = new Intent(this, StoreListActivity.class);
            startActivity(startDownload);
            overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

        } else if (id == R.id.nav_download) {
            if (checkNetIsAvailable()) {
                if (database.isCoverageDataFilled(date)) {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                    builder.setTitle("Parinaam");
                    builder.setMessage("Please Upload Previous Data First")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent startUpload = new Intent(MainManuActivity.this, CheckoutNUpload.class);
                                    startActivity(startUpload);
                                    finish();

                                }
                            });

                    android.app.AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    Intent startDownload = new Intent(getApplicationContext(), CompleteDownloadActivity.class);
                    startActivity(startDownload);
                    finish();
                }
            } else {
                Snackbar.make(webView, "No Network Available", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            }

        } else if (id == R.id.search_data) {
            showExportDialog();

        }  else if (id==R.id.nav_geoT){
            jcplist = database.getJCPData(date);
            if (jcplist.size()>0){
                Intent in=new Intent(this, GeoTagStoreList.class);
                startActivity(in);
            }else {
                Snackbar.make(webView, "Please Download Data First", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            }
        }else if (id == R.id.nav_upload) {
            if (checkNetIsAvailable()) {
                boolean flag = true;
                jcplist = database.getJCPData(date);
                ArrayList<CoverageBean> cdata = new ArrayList<>();
                cdata = database.getCoverageData(date);
                if (jcplist.size() == 0) {
                    Snackbar.make(webView, "Please Download Data First", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                } else {
                    if (cdata.size() > 0) {
                        if (checkoutValidate()) {
                            Intent i = new Intent(getBaseContext(), UploadDataActivity.class);
                            startActivity(i);
                            finish();
                        } else if (checkallData()) {
                            Intent i = new Intent(getBaseContext(), UploadDataActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            Snackbar.make(webView, "No data for upload!", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                        }
                    } else {
                        if ((validate_dataFor_image())) {
                            File dir = new File(CommonString.FILE_PATH);
                            ArrayList<String> list = new ArrayList();
                            list = getFileNames(dir.listFiles());
                            if (list.size() > 0) {
                                Intent i = new Intent(getBaseContext(), UploadDataActivity.class);
                                startActivity(i);
                                finish();
                            } else {
                                Snackbar.make(webView, AlertMessage.MESSAGE_NO_DATA, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                            }
                        } else {
                            Snackbar.make(webView, AlertMessage.MESSAGE_NO_DATA, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                        }
                    }
                }
            } else {
                Snackbar.make(webView, "No Network Available", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            }


        } else if (id == R.id.nav_exit) {
            Intent startDownload = new Intent(this, LoginActivity.class);
            startActivity(startDownload);
            finish();
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public boolean checkallData() {
        boolean alldata_flag = false;
        database.open();
        ArrayList<CoverageBean> cdata;
        ArrayList<DemosGetterSetter> demolist;
        ArrayList<POSMDATAGetterSetter> posmlist;
        cdata = database.getCoverageData(date);
        if (cdata.size() > 0) {
            for (int i = 0; i < cdata.size(); i++) {
                demolist = database.getinsertedDemosdata(cdata.get(i).getStore_id());
                posmlist = database.getinsertedposmdata(cdata.get(i).getStore_id());
                if (demolist.size() > 0) {
                    for (int i1 = 0; i1 < demolist.size(); i1++) {
                        if (!demolist.get(i1).getStatus().equals(CommonString.KEY_U)) {
                            alldata_flag = true;
                            break;
                        }
                    }
                }
                if (posmlist.size() > 0) {
                    if (!posmlist.get(0).getStatus().equals(CommonString.KEY_U)) {
                        alldata_flag = true;
                        break;
                    }
                }
                if (cdata.get(i).getCoverage_status().equals(CommonString.STORE_STATUS_LEAVE)) {
                    alldata_flag = true;
                    break;
                }
            }
        }
        return alldata_flag;
    }

    public boolean checkoutValidate() {
        boolean alldata_flag = false;
        database.open();
        ArrayList<CoverageBean> cdata;
        ArrayList<JourneyPlanGetterSetter> jcplist;
        jcplist = database.getJCPData(date);
        if (jcplist.size() > 0) {
            for (int i = 0; i < jcplist.size(); i++) {
                cdata = database.getCoverageSpecificData(jcplist.get(i).getStore_cd().get(0));
                if (cdata.size() > 0) {
                    if (jcplist.get(i).getCheckOutStatus().get(0).equals(CommonString.KEY_C) || cdata.get(0).getCoverage_status().equals(CommonString.KEY_C)) {
                        alldata_flag = true;
                        break;
                    }
                }
            }
        }
        return alldata_flag;

    }


    public void showExportDialog() {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainManuActivity.this);
        builder1.setMessage(R.string.Areyou_sure_take_backup)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @SuppressWarnings("resource")
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            File file = new File(Environment.getExternalStorageDirectory(), "Aintupromoter_backup");
                            if (!file.isDirectory()) {
                                file.mkdir();
                            }
                            File sd = Environment.getExternalStorageDirectory();
                            File data = Environment.getDataDirectory();
                            if (sd.canWrite()) {
                                long date = System.currentTimeMillis();
                                SimpleDateFormat sdf = new SimpleDateFormat("MMM/dd/yy");
                                String dateString = sdf.format(date);
                                String currentDBPath = "//data//com.cpm.aintupromoter//databases//" + AintuREDB.DATABASE_NAME;
                                String backupDBPath = "Aintupromoter_Database_backup" + dateString.replace('/', '-');
                                String path = Environment.getExternalStorageDirectory().getPath() + "/Aintupromoter_backup";
                                File currentDB = new File(data, currentDBPath);
                                File backupDB = new File(path, backupDBPath);
                                Snackbar.make(webView, "Database Exported Successfully", Snackbar.LENGTH_SHORT).show();
                                if (currentDB.exists()) {
                                    @SuppressWarnings("resource")
                                    FileChannel src = new FileInputStream(currentDB).getChannel();
                                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                                    dst.transferFrom(src, 0, src.size());
                                    src.close();
                                    dst.close();
                                }
                            }
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert1 = builder1.create();
        alert1.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.setWebViewClient(new MyWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        if (!url.equals("")) {
            webView.loadUrl(url);

        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            imageView.setVisibility(View.INVISIBLE);
            webView.setVisibility(View.VISIBLE);
            super.onPageFinished(view, url);
            view.clearCache(true);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

    }


}
