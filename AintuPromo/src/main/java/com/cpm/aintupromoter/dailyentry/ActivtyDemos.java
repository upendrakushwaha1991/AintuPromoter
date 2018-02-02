package com.cpm.aintupromoter.dailyentry;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.cpm.aintupromoter.Database.AintuREDB;
import com.cpm.aintupromoter.R;
import com.cpm.aintupromoter.constants.CommonString;
import com.cpm.aintupromoter.messgae.AlertMessage;
import com.cpm.aintupromoter.xmlGetterSetter.CoverageBean;
import com.cpm.aintupromoter.xmlGetterSetter.DemosGetterSetter;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class ActivtyDemos extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    EditText user_name, mobile, remark, demos_network_remark, brand_edtTxt, asset_edtTxt, dedicated_remark;
    ToggleButton downloded, availed_offer, app_demo, dedicated_phone_toggle, store_network_iToggle;
    LinearLayout demos_network_rl, dedicated_rl;
    Spinner gender;
    FloatingActionButton btn_add, save_fab;
    RecyclerView demos_list;
    AintuREDB db;
    String store_cd, visit_date, username, validateYesNo;
    private SharedPreferences preferences;
    private ArrayAdapter<CharSequence> gender_adapter;
    String downlodedSpin_value = "NO", availed_offerSpin_value = "NO", app_demoSpin_value = "NO", dedicated_phone_Value = "NO", store_network_Value = "YES";
    ArrayList<DemosGetterSetter> insertedlist_Data = new ArrayList<>();
    String app_ver;
    String datacheck = "";
    String[] words;
    String validity;
    private Dialog dialog;
    private ProgressBar pb;
    protected Data data;
    private TextView percentage, message, tv_title;
    ArrayList<CoverageBean> cDatalist = new ArrayList<>();
    MyAdapter adapter;
    NestedScrollView scroll;
    boolean saveflag = false;
    boolean uploadstatusflag = false;
    boolean addeddemoflag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activty_demos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        user_name = (EditText) findViewById(R.id.user_name);
        mobile = (EditText) findViewById(R.id.mobile);
        remark = (EditText) findViewById(R.id.remark);
        downloded = (ToggleButton) findViewById(R.id.downloded);
        availed_offer = (ToggleButton) findViewById(R.id.availed_offer);


        dedicated_phone_toggle = (ToggleButton) findViewById(R.id.dedicated_phone_toggle);
        store_network_iToggle = (ToggleButton) findViewById(R.id.store_network_iToggle);
        dedicated_remark = (EditText) findViewById(R.id.dedicated_remark);

        demos_network_remark = (EditText) findViewById(R.id.demos_network_remark);
        brand_edtTxt = (EditText) findViewById(R.id.brand_edtTxt);
        asset_edtTxt = (EditText) findViewById(R.id.asset_edtTxt);
        demos_network_rl = (LinearLayout) findViewById(R.id.demos_network_rl);
        dedicated_rl = (LinearLayout) findViewById(R.id.dedicated_rl);


        app_demo = (ToggleButton) findViewById(R.id.app_demo);
        gender = (Spinner) findViewById(R.id.gender);
        btn_add = (FloatingActionButton) findViewById(R.id.btn_add);
        save_fab = (FloatingActionButton) findViewById(R.id.save_fab);
        demos_list = (RecyclerView) findViewById(R.id.demos_list);
        scroll = (NestedScrollView) findViewById(R.id.scroll);
        db = new AintuREDB(getApplicationContext());
        db.open();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        store_cd = preferences.getString(CommonString.KEY_STORE_CD, null);
        visit_date = preferences.getString(CommonString.KEY_DATE, null);
        username = preferences.getString(CommonString.KEY_USERNAME, null);
        app_ver = preferences.getString(CommonString.KEY_VERSION, "");
        gender_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        gender_adapter.add("-Select Gender-");
        gender_adapter.add("Male");
        gender_adapter.add("Female");
        gender.setAdapter(gender_adapter);
        gender_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setOnItemSelectedListener(this);
        downloded.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    downlodedSpin_value = "YES";
                    downloded.setChecked(true);
                    availed_offer.setEnabled(true);
                } else {
                    downloded.setChecked(false);
                    if (!isChecked) {
                        availed_offer.setEnabled(false);
                        availed_offer.setChecked(false);
                    }
                    downlodedSpin_value = "NO";
                }
            }
        });
        availed_offer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!downloded.isChecked()) {
                    availed_offer.setChecked(false);
                    availed_offer.setEnabled(false);
                    availed_offerSpin_value = "NO";
                } else {
                    if (isChecked) {
                        availed_offer.setChecked(true);
                        availed_offerSpin_value = "YES";
                    } else {
                        availed_offer.setChecked(false);
                        availed_offerSpin_value = "NO";
                    }
                }
            }
        });
        app_demo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    app_demo.setChecked(true);
                    app_demoSpin_value = "YES";
                } else {
                    app_demo.setChecked(false);
                    app_demoSpin_value = "NO";
                }
            }
        });
        if (!downloded.isChecked()) {
            availed_offer.setEnabled(false);
        } else {
            availed_offer.setEnabled(true);
        }

        //new cham=nges
        dedicated_phone_toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    dedicated_phone_toggle.setChecked(true);
                    dedicated_phone_Value = "YES";
                    dedicated_rl.setVisibility(View.GONE);
                } else {
                    dedicated_phone_toggle.setChecked(false);
                    dedicated_phone_Value = "NO";
                    dedicated_rl.setVisibility(View.VISIBLE);
                }
            }
        });

        store_network_iToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    store_network_iToggle.setChecked(true);
                    demos_network_rl.setVisibility(View.VISIBLE);
                    store_network_Value = "YES";
                } else {
                    store_network_iToggle.setChecked(false);
                    demos_network_rl.setVisibility(View.GONE);
                    store_network_Value = "NO";
                }
            }
        });
        setDataToListView();

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validation()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivtyDemos.this);
                    builder.setMessage("Are you sure you want to add")
                            .setCancelable(false)
                            .setPositiveButton("Yes",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            db.open();
                                            DemosGetterSetter secdata = new DemosGetterSetter();
                                            secdata.setGender(gender.getSelectedItem().toString());
                                            //new add
                                            secdata.setDedicated_phone(dedicated_phone_Value);
                                            secdata.setStore_network(store_network_Value);
                                            secdata.setStore_network_remark(demos_network_remark.getText().toString().replaceAll("[-@.?/|=+_#%:;^*()!&^<>{},'$0]", ""));
                                            secdata.setBrand_s_remark(brand_edtTxt.getText().toString().replaceAll("[-@.?/|=+_#%:;^*()!&^<>{},'$0]", ""));
                                            secdata.setAsset_remark(asset_edtTxt.getText().toString().replaceAll("[-@.?/|=+_#%:;^*()!&^<>{},'$0]", ""));
                                            secdata.setDedicated_remark(dedicated_remark.getText().toString().replaceAll("[-@.?/|=+_#%:;^*()!&^<>{},'$0]", ""));
                                            secdata.setApp_demosG(app_demoSpin_value);
                                            secdata.setAvailed_offer(availed_offerSpin_value);
                                            secdata.setDownloded(downlodedSpin_value);
                                            secdata.setName(user_name.getText().toString().replaceAll("[-@.?/|=+_#%:;^*()!&^<>{},'$0]", ""));
                                            secdata.setMobile_no(mobile.getText().toString());
                                            secdata.setRemark(remark.getText().toString().replaceAll("[-@.?/|=+_#%:;^*()!&^<>{},'$0]", ""));
                                            String flag = "NO";
                                            secdata.setFlag(flag);
                                            secdata.setStatus("N");
                                            insertedlist_Data.add(secdata);
                                            Collections.reverse(insertedlist_Data);
                                            adapter = new MyAdapter(ActivtyDemos.this, insertedlist_Data);
                                            demos_list.setAdapter(adapter);
                                            demos_list.setLayoutManager(new LinearLayoutManager(ActivtyDemos.this));
                                            adapter.notifyDataSetChanged();
                                            Snackbar.make(btn_add, "Data has been saved", Snackbar.LENGTH_SHORT).show();
                                            user_name.setText("");
                                            user_name.setHint("Name");
                                            remark.setText("");
                                            remark.setHint("Remark");
                                            mobile.setText("");
                                            mobile.setHint("Mobile");
                                            demos_network_remark.setText("");
                                            demos_network_remark.setHint("Remark");
                                            brand_edtTxt.setText("");
                                            brand_edtTxt.setHint("");
                                            asset_edtTxt.setHint("");
                                            asset_edtTxt.setText("");
                                            dedicated_remark.setHint("Remark");
                                            dedicated_remark.setText("");
                                            app_demo.setChecked(false);
                                            downloded.setChecked(false);
                                            availed_offer.setChecked(false);
                                            availed_offerSpin_value = "NO";
                                            downlodedSpin_value = "NO";
                                            app_demoSpin_value = "NO";
                                            dedicated_phone_Value = "NO";
                                            store_network_Value = "YES";
                                            gender.setSelection(0);
                                            addeddemoflag = true;
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
                }
            }

        });
        save_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (insertedlist_Data.size() > 0) {
                    boolean flag = false;
                    for (int i = 0; i < insertedlist_Data.size(); i++) {
                        if (insertedlist_Data.get(i).getStatus().equals("N")) {
                            flag = true;
                            break;
                        }
                    }
                    if (addeddemoflag && flag) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ActivtyDemos.this);
                        builder.setMessage("Are you sure you want to save demos data")
                                .setCancelable(false)
                                .setPositiveButton("Yes",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                db.open();
                                                long l = db.insertDemosdata(store_cd, username, visit_date, insertedlist_Data);
                                                if (l > 0) {
                                                    saveflag = true;
                                                    boolean flag = false;
                                                    if (checkNetIsAvailable()) {
                                                        dialog.dismiss();
                                                        for (int i = 0; i < insertedlist_Data.size(); i++) {
                                                            if (insertedlist_Data.get(i).getStatus().equals("N")) {
                                                                flag = true;
                                                                break;
                                                            }
                                                        }
                                                        if (flag) {
                                                            new DemosuploadTask(ActivtyDemos.this).execute();
                                                        } else {
                                                            Snackbar.make(btn_add, "No data upload", Snackbar.LENGTH_SHORT).show();
                                                        }
                                                    } else {
                                                        addeddemoflag=false;
                                                       // Snackbar.make(btn_add, "Check internet connection", Snackbar.LENGTH_SHORT).show();
                                                    }
                                                }
                                                Snackbar.make(btn_add, "Data has been saved", Snackbar.LENGTH_SHORT).show();

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
                        Snackbar.make(btn_add, "Please add new demos data", Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    Snackbar.make(btn_add, "Data not found", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        scroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY) {
                    save_fab.hide();
                } else {
                    save_fab.show();
                }
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.gender:
                if (position != 0) {
                    validateYesNo = gender.getSelectedItem().toString();
                    break;
                }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    public void showMessage(String message) {
        Snackbar.make(btn_add, message, Snackbar.LENGTH_SHORT).show();
    }

    public boolean validation() {
        boolean value = true;
        if (user_name.getText().toString().isEmpty()) {
            value = false;
            showMessage("Please Enter Name");
        } else if (mobile.getText().toString().isEmpty()) {
            value = false;
            showMessage("Please Enter Mobile Number");
        } else if (mobile.getText().toString().length() != 10) {
            value = false;
            showMessage("Please Enter 10 Digit Mobile Number");
        } else if (gender.getSelectedItem().toString().equalsIgnoreCase("-Select Gender-")) {
            value = false;
            showMessage("Please Select gender Dropdown");
        }/* else if (dedicated_phone_Value.equals("NO") && dedicated_remark.getText().toString().isEmpty()) {
            value = false;
            showMessage("Please Enter Store Dedicated Phone Remark");
        }*//* else if (store_network_Value.equals("YES") && demos_network_remark.getText().toString().isEmpty()) {
            value = false;
            showMessage("Please Enter Store Network Issue Remark");
        }*//* else if (brand_edtTxt.getText().toString().isEmpty()) {
            value = false;
            showMessage("Please Enter Brand Out Of Stock Remark");
        } else if (asset_edtTxt.getText().toString().isEmpty()) {
            value = false;
            showMessage("Please Enter Out Of Stock Asset Remark");
        }*//* else if (remark.getText().toString().isEmpty()) {
            value = false;
            showMessage("Please Enter Remark");
        }*/

        return value;
    }


    public void setDataToListView() {
        try {
            insertedlist_Data = db.getinsertedDemosdata(store_cd);
            if (insertedlist_Data.size() > 0) {
                Collections.reverse(insertedlist_Data);
                adapter = new MyAdapter(this, insertedlist_Data);
                demos_list.setAdapter(adapter);
                demos_list.setLayoutManager(new LinearLayoutManager(ActivtyDemos.this));
                adapter.notifyDataSetChanged();
            }

        } catch (Exception e) {
            Log.d("Exception when fetching", e.toString());
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        private LayoutInflater inflator;
        Context context;
        ArrayList<DemosGetterSetter> insertedlist_Data;

        MyAdapter(Context context, ArrayList<DemosGetterSetter> insertedlist_Data) {
            inflator = LayoutInflater.from(context);
            this.context = context;
            this.insertedlist_Data = insertedlist_Data;

        }

        @Override
        public int getItemCount() {
            return insertedlist_Data.size();

        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflator.inflate(R.layout.secondary_adapter, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            holder.checkbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (saveflag) {
                        String fla = insertedlist_Data.get(position).getFlag();
                        if (fla.equals("YES")) {
                            holder.checkbox.setChecked(true);
                        } else {
                            holder.checkbox.setChecked(false);
                        }
                        showMessage("If demos data saved,Then not able to update wrong");
                    } else {
                        if (holder.checkbox.isChecked()) {
                            android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(ActivtyDemos.this, R.style.ThemeDialogCustom);
                            dialog.setTitle("Parinaam").setMessage("Do You Want To Update Wrong");
                            dialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (holder.checkbox.isChecked()) {
                                        String flag = "YES";
                                        db.open();
                                        insertedlist_Data.get(position).setFlag(flag);
                                        showMessage("Wrong updated successfully");
                                    } else {
                                        notifyDataSetChanged();
                                        holder.checkbox.setChecked(true);
                                        showMessage("If Wrong is checked Then, not able to update Wrong ");
                                    }

                                }
                            });
                            dialog.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    if (insertedlist_Data.get(position).getFlag().equalsIgnoreCase("YES")) {
                                        holder.checkbox.setChecked(true);
                                    } else {
                                        holder.checkbox.setChecked(false);
                                    }
                                }
                            });
                            dialog.show();
                        } else {
                            if (saveflag) {
                                showMessage("If demos data saved,Then not able to update wrong");
                            } else {
                                notifyDataSetChanged();
                                holder.checkbox.setChecked(true);
                                showMessage("If Wrong is checked Then, not able to update Wrong ");
                            }
                        }
                    }


                }

            });

            if (insertedlist_Data.get(position).getFlag().equalsIgnoreCase("YES")) {
                holder.checkbox.setChecked(true);
            } else {
                holder.checkbox.setChecked(false);
            }
            if (insertedlist_Data.get(position).getStatus().equals(CommonString.KEY_U)) {
                holder.checkbox.setEnabled(false);
            }
            holder.txt_name.setText(insertedlist_Data.get(position).getName());
            holder.txt_gender.setText(insertedlist_Data.get(position).getGender());
            holder.txt_mobile.setText(insertedlist_Data.get(position).getMobile_no());
            holder.txt_status.setText(insertedlist_Data.get(position).getStatus());
            holder.txt_name.setId(position);
            holder.txt_gender.setId(position);
            holder.txt_mobile.setId(position);
            holder.checkbox.setId(position);
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView txt_name, txt_gender, txt_mobile, txt_status;
            CheckBox checkbox;

            public MyViewHolder(View convertView) {
                super(convertView);
                txt_status = (TextView) convertView.findViewById(R.id.txt_status);
                txt_name = (TextView) convertView.findViewById(R.id.txt_name);
                txt_gender = (TextView) convertView.findViewById(R.id.txt_gender);
                txt_mobile = (TextView) convertView.findViewById(R.id.txt_mobile);
                checkbox = (CheckBox) convertView.findViewById(R.id.imgDelRow);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(CommonString.ONBACK_ALERT_MESSAGE)
                    .setCancelable(false)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialog, int id) {
                                    overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                                    ActivtyDemos.this.finish();
                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    dialog.cancel();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();

        }
        return super.onOptionsItemSelected(item);
    }

    public boolean checkNetIsAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(CommonString.ONBACK_ALERT_MESSAGE)
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog, int id) {
                                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                                ActivtyDemos.this.finish();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }


    private class DemosuploadTask extends AsyncTask<Void, Data, String> {
        boolean flag = true;
        private Context context;

        DemosuploadTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            dialog = new Dialog(context);
            dialog.setContentView(R.layout.custom_upload);
            dialog.setTitle("Uploading Posm Data..");
            dialog.setCancelable(false);
            dialog.show();
            pb = (ProgressBar) dialog.findViewById(R.id.progressBar1);
            percentage = (TextView) dialog.findViewById(R.id.percentage);
            message = (TextView) dialog.findViewById(R.id.message);
            tv_title = (TextView) dialog.findViewById(R.id.tv_title);
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                data = new Data();
                data.value = 10;
                data.name = "Uploading";
                publishProgress(data);
                db.open();
                cDatalist = db.getCoverageSpecificData(store_cd);
                if (cDatalist.size() > 0) {
                    String onXML = "[DATA][USER_DATA][STORE_CD]"
                            + cDatalist.get(0).getStore_id()
                            + "[/STORE_CD]" + "[VISIT_DATE]"
                            + cDatalist.get(0).getVisitDate()
                            + "[/VISIT_DATE][LATITUDE]"
                            + cDatalist.get(0).getLatitude()
                            + "[/LATITUDE][APP_VERSION]"
                            + app_ver
                            + "[/APP_VERSION][LONGITUDE]"
                            + cDatalist.get(0).getLongitude()
                            + "[/LONGITUDE][IN_TIME]"
                            + "00:00:00"
                            + "[/IN_TIME][OUT_TIME]"
                            + "00:00:00"
                            + "[/OUT_TIME][UPLOAD_STATUS]"
                            + "N"
                            + "[/UPLOAD_STATUS][USER_ID]" + username
                            + "[/USER_ID][IMAGE_URL]" + cDatalist.get(0).getStore_image()
                            + "[/IMAGE_URL]"
                            + "[REASON_ID]"
                            + cDatalist.get(0).getReasonid()
                            + "[/REASON_ID]"
                            + "[CHECKOUT_IMAGE]"
                            + cDatalist.get(0).getCheckout_image()
                            + "[/CHECKOUT_IMAGE]"
                            + "[REASON_REMARK]"
                            + cDatalist.get(0).getRemark()
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
                        if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                        }
                    }
                    String final_xml = "";
                    final int mid = Integer.parseInt((words[1]));
                    data.value = 30;
                    data.name = "Coverage data Uploading";
                    publishProgress(data);
                    //demos data
                    final_xml = "";
                    onXML = "";
                    insertedlist_Data = db.getinsertedDemosdata(store_cd);
                    if (insertedlist_Data.size() > 0) {
                        uploadstatusflag = false;
                        for (int j = 0; j < insertedlist_Data.size(); j++) {
                            String flag = "";
                            if (!insertedlist_Data.get(j).getStatus().equals(CommonString.KEY_U)) {
                                uploadstatusflag = true;
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
                                        + "[DEDICATED_PHONE_REMARK]"
                                        + insertedlist_Data.get(j).getDedicated_remark()
                                        + "[/DEDICATED_PHONE_REMARK]"
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
                        if (uploadstatusflag) {
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
                                for (int i = 0; i < insertedlist_Data.size(); i++) {
                                    long l = db.updateDemosDataStatus(store_cd, insertedlist_Data.get(i).getId(), CommonString.KEY_U);
                                }
                                return CommonString.KEY_SUCCESS;
                            }
                            data.value = 70;
                            data.name = "100";
                            publishProgress(data);
                        }
                    }
                }
                return CommonString.KEY_SUCCESS;

            } catch (IOException e) {
                flag = false;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showAlert(AlertMessage.MESSAGE_SOCKETEXCEPTION);
                    }
                });

            } catch (Exception e) {
                flag = false;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showAlert(AlertMessage.MESSAGE_EXCEPTION);
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
            if (flag) {
                if (result.contains(CommonString.KEY_SUCCESS)) {
                    db.open();
                    Snackbar.make(btn_add, "Demos data upload successfully", Snackbar.LENGTH_SHORT).show();
                    overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                    finish();
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showAlert(AlertMessage.MESSAGE_SOCKETEXCEPTION);
                        }
                    });
                }
            }

        }

    }

    class Data {
        int value;
        String name;
    }

    public void showAlert(String str) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivtyDemos.this);
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
