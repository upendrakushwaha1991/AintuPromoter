package com.cpm.aintupromoter.dailyentry;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.cpm.aintupromoter.Database.AintuREDB;
import com.cpm.aintupromoter.GeoTag.GeoTagActivity;
import com.cpm.aintupromoter.GetterSetter.GeotaggingBeans;
import com.cpm.aintupromoter.constants.CommonString;
import com.cpm.aintupromoter.xmlGetterSetter.JourneyPlanGetterSetter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import com.cpm.aintupromoter.R;
import com.cpm.aintupromoter.xmlGetterSetter.NavMenuGetterSetter;

public class RouteTrainingActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ValueAdapter adapter;
    private SharedPreferences preferences;
    String store_cd, training_mode_cd, manned, visit_date;
    ArrayList<JourneyPlanGetterSetter> jcpspecific = new ArrayList<>();
    ArrayList<GeotaggingBeans> geodata = new ArrayList<GeotaggingBeans>();
    AintuREDB database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routetraining);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        store_cd = preferences.getString(CommonString.KEY_STORE_CD, null);
        visit_date = preferences.getString(CommonString.KEY_DATE, null);
        training_mode_cd = preferences.getString(CommonString.KEY_TRAINING_MODE_CD, null);
        manned = preferences.getString(CommonString.KEY_MANAGED, null);
        database = new AintuREDB(this);
        database.open();
    }

    @Override
    protected void onResume() {
        super.onResume();
        database.open();
        jcpspecific = database.getSpecificStoreData(store_cd);
        geodata = database.getinsertGeotaggingData(store_cd);

        recyclerView = (RecyclerView) findViewById(R.id.rec_menu);
        List<NavMenuGetterSetter> data = new ArrayList<>();
        NavMenuGetterSetter recData = new NavMenuGetterSetter();
        if (training_mode_cd.equals("2")) {
            recData.setIconImg(R.drawable.route_training_icon);
            data.add(recData);
            recData = new NavMenuGetterSetter();
            recData.setIconImg(R.drawable.performance_ico);
            data.add(recData);
        } else {
            if (jcpspecific.get(0).getGeotag().get(0).equals("Y") && database.isPOSMDataFilled(store_cd)) {
                data.clear();
                recData.setIconImg(R.drawable.route_training_done);
                data.add(recData);
                recData = new NavMenuGetterSetter();
                recData.setIconImg(R.drawable.performance_ico);
                data.add(recData);
                recData = new NavMenuGetterSetter();
                recData.setIconImg(R.drawable.geo_tag_done);
                data.add(recData);
            } else if (database.isPOSMDataFilled(store_cd) && geodata.size() > 0) {
                    if (geodata.get(0).getGEO_TAG().equals("Y") && jcpspecific.get(0).getGeotag().get(0).equals("Y")) {
                        data.clear();
                        recData.setIconImg(R.drawable.route_training_done);
                        data.add(recData);
                        recData = new NavMenuGetterSetter();
                        recData.setIconImg(R.drawable.performance_ico);
                        data.add(recData);
                        recData = new NavMenuGetterSetter();
                        recData.setIconImg(R.drawable.geo_tag_done);
                        data.add(recData);
                    }
            } else if (jcpspecific.get(0).getGeotag().get(0).equals("Y")) {
                data.clear();
                recData.setIconImg(R.drawable.route_training_icon);
                data.add(recData);
                recData = new NavMenuGetterSetter();
                recData.setIconImg(R.drawable.performance_ico);
                data.add(recData);
                recData = new NavMenuGetterSetter();
                recData.setIconImg(R.drawable.geo_tag_done);
                data.add(recData);
            } else if (geodata.size() > 0) {
                if (geodata.get(0).getGEO_TAG().equals("Y")) {
                    recData.setIconImg(R.drawable.route_training_icon);
                    data.add(recData);
                    recData = new NavMenuGetterSetter();
                    recData.setIconImg(R.drawable.performance_ico);
                    data.add(recData);
                    recData = new NavMenuGetterSetter();
                    recData.setIconImg(R.drawable.geo_tag_done);
                    data.add(recData);
                }

            } else if (database.isPOSMDataFilled(store_cd)) {
                data.clear();
                recData.setIconImg(R.drawable.route_training_done);
                data.add(recData);
                recData = new NavMenuGetterSetter();
                recData.setIconImg(R.drawable.performance_ico);
                data.add(recData);
                recData = new NavMenuGetterSetter();
                recData.setIconImg(R.drawable.geo_tag_icon);
                data.add(recData);
            } else {
                data.clear();
                recData.setIconImg(R.drawable.route_training_icon);
                data.add(recData);
                recData = new NavMenuGetterSetter();
                recData.setIconImg(R.drawable.performance_ico);
                data.add(recData);
                recData = new NavMenuGetterSetter();
                recData.setIconImg(R.drawable.geo_tag_icon);
                data.add(recData);
            }
        }
        adapter = new ValueAdapter(getApplicationContext(), data);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        if (validate()) {
            database.updateCoverageStoreOutTime1(store_cd, visit_date, getCurrentTime(), CommonString.KEY_VALID);
        }
    }

    public class ValueAdapter extends RecyclerView.Adapter<ValueAdapter.MyViewHolder> {
        private LayoutInflater inflator;
        List<NavMenuGetterSetter> data = Collections.emptyList();

        public ValueAdapter(Context context, List<NavMenuGetterSetter> data) {
            inflator = LayoutInflater.from(context);
            this.data = data;

        }

        @Override
        public ValueAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int i) {

            View view = inflator.inflate(R.layout.custom_menu_row, parent, false);

            MyViewHolder holder = new MyViewHolder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(final ValueAdapter.MyViewHolder viewHolder, final int position) {
            final NavMenuGetterSetter current = data.get(position);
            viewHolder.icon.setImageResource(current.getIconImg());
            viewHolder.icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){

                } /*{
                    if (current.getIconImg() == R.drawable.route_training_icon || current.getIconImg() == R.drawable.route_training_done) {
                        Intent in = new Intent(getApplicationContext(), StoreIsdActivity.class);
                        in.putExtra(CommonString.KEY_TRAINING_MODE_CD, training_mode_cd);
                        in.putExtra(CommonString.KEY_MANAGED, manned);
                        startActivity(in);
                        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                        finish();
                    } else if (current.getIconImg() == R.drawable.geo_tag_icon ||
                            current.getIconImg() == R.drawable.geo_tag_done) {
                        if (geodata.size() > 0) {
                            if (geodata.get(0).getGEO_TAG().equals("Y")) {
                                Snackbar.make(recyclerView, "GoeTag Already Done", Snackbar.LENGTH_SHORT).show();
                            }
                        } else if (jcpspecific.get(0).getGeotag().get(0).equals("Y")) {
                            Snackbar.make(recyclerView, "GoeTag Already Done", Snackbar.LENGTH_SHORT).show();
                        } else {
                            Intent in = new Intent(getApplicationContext(), GeoTagActivity.class);
                            in.putExtra(CommonString.KEY_TRAINING_MODE_CD, training_mode_cd);
                            startActivity(in);
                            overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                        }
                    } else {
                        Intent in = new Intent(getApplicationContext(), ISDPerformanceActivity.class);
                        startActivity(in);
                        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                        finish();
                    }
                }*/
            });

        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView icon;

            public MyViewHolder(View itemView) {
                super(itemView);
                icon = (ImageView) itemView.findViewById(R.id.list_icon);
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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

    private boolean validate() {
        boolean flag = true;
        if (geodata.size() > 0) {
            if (geodata.get(0).getGEO_TAG().equals("N") || jcpspecific.get(0).getGeotag().get(0).equals("N")) {
                flag = false;
            }
        } else {
            if (jcpspecific.get(0).getGeotag().get(0).equals("N")) {
                flag = false;
            }
        }
        if (flag) {
            if (!database.isPOSMDataFilled(store_cd)) {
                flag = false;
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

}
