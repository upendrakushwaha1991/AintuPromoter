package com.cpm.aintupromoter.GeoTag;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cpm.aintupromoter.Database.AintuREDB;
import com.cpm.aintupromoter.GetterSetter.GeotaggingBeans;
import com.cpm.aintupromoter.LoginActivity;
import com.cpm.aintupromoter.R;
import com.cpm.aintupromoter.constants.CommonString;
import com.cpm.aintupromoter.xmlGetterSetter.JourneyPlanGetterSetter;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by ashishc on 27-12-2016.
 */

public class GeoTagStoreList extends AppCompatActivity {
    private SharedPreferences preferences;
    ArrayList<JourneyPlanGetterSetter> storelist = new ArrayList<>();
    String date, visit_status;
    AintuREDB db;
    // ListView list;
    ValueAdapter adapter;
    RecyclerView recyclerView;
    LinearLayout linearlay;
    FloatingActionButton fab;
    Toolbar toolbar;
    ArrayList<GeotaggingBeans> geolist = new ArrayList<>();
    String store_cd;

    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.geotagstorelistfab);
        recyclerView = (RecyclerView) findViewById(R.id.recycle_geoT);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        date = preferences.getString(CommonString.KEY_DATE, null);
        // store_cd = preferences.getString(CommonString.KEY_STORE_CD, "");
        db = new AintuREDB(GeoTagStoreList.this);
        db.open();

        storelist = db.getJCPData(date);
        if (storelist.size() > 0) {
            adapter = new ValueAdapter(getApplicationContext(), storelist);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

        } else {
            recyclerView.setVisibility(View.INVISIBLE);
            linearlay.setVisibility(View.VISIBLE);
            fab.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {

            finish();

            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
       /* Intent intent = new Intent(GeoTagStoreList.this, MainActivity.class);

        startActivity(intent);*/
        GeoTagStoreList.this.finish();
    }


    public class ValueAdapter extends RecyclerView.Adapter<ValueAdapter.MyViewHolder> {

        private LayoutInflater inflator;

        List<JourneyPlanGetterSetter> data = Collections.emptyList();

        public ValueAdapter(Context context, List<JourneyPlanGetterSetter> data) {

            inflator = LayoutInflater.from(context);
            this.data = data;

        }

        @Override
        public ValueAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int i) {
            View view = inflator.inflate(R.layout.geotagstorelist, parent, false);

            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final ValueAdapter.MyViewHolder viewHolder, final int position) {
            final JourneyPlanGetterSetter current = data.get(position);
            viewHolder.txt.setText(current.getStore_name().get(0));
            viewHolder.txt_storeAddress.setText(current.getCity().get(0));

            if (current.getGeotag().get(0).equalsIgnoreCase("Y")) {

                viewHolder.geolistviewxml_storeico.setBackgroundResource(R.drawable.geo_tag_icon);

            } else {

                geolist = db.getinsertGeotaggingData(current.getStore_cd().get(0));

                if (geolist.size() > 0) {

                    viewHolder.geolistviewxml_storeico.setBackgroundResource(R.drawable.geo_tag_icon);

                }

            }

            viewHolder.relativelayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (current.getGeotag().get(0).equalsIgnoreCase("Y") || db.getinsertGeotaggingData(current.getStore_cd().get(0)).size() > 0) {
                        Snackbar.make(v, R.string.title_geo_tag_activity_geo_already_done, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    } else {
                        Intent in = new Intent(GeoTagStoreList.this, GeoTagActivity.class);
                        in.putExtra(CommonString.KEY_STORE_ID, current.getStore_cd().get(0));
                        startActivity(in);
                        finish();

                    }

                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView txt, txt_storeAddress;
            ImageView icon;
            RelativeLayout relativelayout;
            ImageView imageview, geolistviewxml_storeico;

            public MyViewHolder(View itemView) {
                super(itemView);
                txt = (TextView) itemView.findViewById(R.id.geolistviewxml_storename);
                relativelayout = (RelativeLayout) itemView.findViewById(R.id.relativelayout);
                imageview = (ImageView) itemView.findViewById(R.id.imageView1);
                geolistviewxml_storeico = (ImageView) itemView.findViewById(R.id.geolistviewxml_storeico);
                txt_storeAddress = (TextView) itemView.findViewById(R.id.txt_storeAddress);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        toolbar.setTitle(getString(R.string.title_activity_store_geotag));
    }



}
