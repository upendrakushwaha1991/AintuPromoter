package com.cpm.aintupromoter.dailyentry;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.cpm.aintupromoter.Database.AintuREDB;
import com.cpm.aintupromoter.R;
import com.cpm.aintupromoter.constants.CommonString;
import com.cpm.aintupromoter.xmlGetterSetter.JourneyPlanGetterSetter;
import com.cpm.aintupromoter.xmlGetterSetter.POSMDATAGetterSetter;

import java.util.ArrayList;

public class ActivityManu extends AppCompatActivity implements View.OnClickListener {
    private SharedPreferences preferences;
    ImageView img_demos, img_posm;
    String store_id, user_name, visit_date;
    AintuREDB db;
    boolean poasm_flag = false, demos_flag = false;
    ArrayList<POSMDATAGetterSetter> posmlist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        img_demos = (ImageView) findViewById(R.id.img_demos);
        img_posm = (ImageView) findViewById(R.id.img_posm);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        store_id = preferences.getString(CommonString.KEY_STORE_CD, null);
        visit_date = preferences.getString(CommonString.KEY_DATE, null);
        user_name = preferences.getString(CommonString.KEY_USERNAME, null);
        img_demos.setOnClickListener(this);
        img_posm.setOnClickListener(this);
        db = new AintuREDB(this);
        db.open();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        db.open();
        posmlist = db.getinsertedposmdata(store_id);
        if (checkallData()) {
            db.updateCoverageCheckoutStatus(store_id, visit_date, CommonString.KEY_VALID);
        }
    }
    public boolean checkallData() {
        boolean alldata_flag = true;
        //demos cagegory
        demos_flag = db.isdemosDataFilled(store_id);
        if (demos_flag) {
            img_demos.setBackgroundResource(R.drawable.demo_done);
        } else {
            alldata_flag = false;
        }


        //  posm data image
        if (posmlist.size() > 0) {
            poasm_flag = db.isPOSMDataFilled(store_id);
            if (posmlist.get(0).getStatus().equals(CommonString.KEY_U)) {
                img_posm.setBackgroundResource(R.drawable.posm_uploaded);
            } else if (poasm_flag) {
                img_posm.setBackgroundResource(R.drawable.posm_done);
            } else {
                alldata_flag = false;
            }
        }
        return alldata_flag;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.img_demos:
                Intent intent = new Intent(this, ActivtyDemos.class);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                break;
            case R.id.img_posm:
                if (posmlist.size() > 0 && posmlist.get(0).getStatus().equals(CommonString.KEY_U)) {
                    Snackbar.make(img_posm, "Already uploaded posm data", Snackbar.LENGTH_LONG).show();
                    break;
                } else {
                    Intent po = new Intent(this, POSMActivity.class);
                    startActivity(po);
                    overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                }
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            startActivity(new Intent(ActivityManu.this, StoreListActivity.class));
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(ActivityManu.this, StoreListActivity.class));
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        finish();
    }
}
