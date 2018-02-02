package com.cpm.aintupromoter.dailyentry;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cpm.aintupromoter.Database.AintuREDB;
import com.cpm.aintupromoter.R;
import com.cpm.aintupromoter.constants.CommonString;
import com.cpm.aintupromoter.messgae.AlertMessage;
import com.cpm.aintupromoter.retrofit.RetrofitClass;
import com.cpm.aintupromoter.upload.UploadDataActivity;
import com.cpm.aintupromoter.xmlGetterSetter.CoverageBean;
import com.cpm.aintupromoter.xmlGetterSetter.POSMDATAGetterSetter;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class POSMActivity extends AppCompatActivity {
    ArrayList<POSMDATAGetterSetter> posmlist = new ArrayList<>();
    AintuREDB db;
    String store_cd, visit_date, username;
    private SharedPreferences preferences;
    String _pathforcheck, _path, str;
    RecyclerView posmlistV;
    AlertDialog alert;
    PosmAdapter adapter;
    String img_str = "";
    static int child_position = -1;
    private Dialog dialog;
    private ProgressBar pb;
    private TextView percentage, message, tv_title;
    String app_ver;
    protected Data data;
    String datacheck = "";
    String[] words;
    String validity;
    ArrayList<CoverageBean> cDatalist = new ArrayList<>();
    FloatingActionButton fab;
    boolean statusflag = false;
    boolean uploaded_flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.posm_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        posmlistV = (RecyclerView) findViewById(R.id.posm_list);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        db = new AintuREDB(getApplicationContext());
        db.open();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        store_cd = preferences.getString(CommonString.KEY_STORE_CD, null);
        visit_date = preferences.getString(CommonString.KEY_DATE, null);
        username = preferences.getString(CommonString.KEY_USERNAME, null);
        app_ver = preferences.getString(CommonString.KEY_VERSION, "");
        str = CommonString.FILE_PATH;
        posmlist = db.getinsertedposmdata(store_cd);
        if (posmlist.size() > 0) {
        } else {
            posmlist = db.getPOSMDATA();
        }
        adapter = new PosmAdapter(this, posmlist);
        posmlistV.setAdapter(adapter);
        posmlistV.setLayoutManager(new LinearLayoutManager(this));
        posmlistV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0)
                    fab.hide();
                else if (dy < 0)
                    fab.show();
            }
        });
        fab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                posmlistV.clearFocus();
                posmlistV.invalidate();
                if (posmlist.size() > 0) {
                    if (validate1()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(POSMActivity.this);
                        builder.setMessage("Do you want to save and upload posm data").setCancelable(false)
                                .setPositiveButton("OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                alert.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                                                db.open();
                                                long l = 0;
                                                if (!statusflag) {
                                                    l = db.insertposmdatawithposmimg(username, store_cd, visit_date, posmlist);
                                                    Snackbar.make(fab, "Data has been saved", Snackbar.LENGTH_SHORT).show();
                                                    if (checkNetIsAvailable()) {
                                                        new PosmuploadTask(POSMActivity.this, posmlist).execute();
                                                        dialog.dismiss();
                                                    } else {
                                                        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                                                        finish();
                                                    }
                                                } else {
                                                    if (posmlist.size() > 0) {
                                                        if (checkNetIsAvailable()) {
                                                            new PosmuploadTask(POSMActivity.this, posmlist).execute();
                                                            dialog.dismiss();
                                                        } else {
                                                            Snackbar.make(fab, "No internet connection", Snackbar.LENGTH_LONG).show();
                                                            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                                                            finish();
                                                        }
                                                    }
                                                }


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


                    }
                }
            }
        });
    }

    public boolean checkNetIsAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    public String getCurrentTime() {
        Calendar m_cal = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String cdate = formatter.format(m_cal.getTime());
        return cdate;
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
                                POSMActivity.this.finish();
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
                                    POSMActivity.this.finish();
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
        return super.onOptionsItemSelected(item);
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
                        img_str = _pathforcheck;
                        posmlistV.clearFocus();
                        posmlistV.invalidate();
                        adapter.notifyDataSetChanged();
                        _pathforcheck = "";
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class PosmAdapter extends RecyclerView.Adapter<PosmAdapter.MyViewHolder> {
        private LayoutInflater inflator;
        Context context;
        ArrayList<POSMDATAGetterSetter> list;

        public PosmAdapter(Context context, ArrayList<POSMDATAGetterSetter> list) {
            inflator = LayoutInflater.from(context);
            this.context = context;
            this.list = list;
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflator.inflate(R.layout.secondaryplac_mt_adapter, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;

        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            holder.posm_cam.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    child_position = position;
                    _pathforcheck = store_cd + "_POSM_IMG_" + list.get(position).getPOSM_CD().get(0) + "_" + visit_date.replace("/", "") + "_" + getCurrentTime().replace(":", "") + ".jpg";
                    _path = str + _pathforcheck;
                    startCameraActivity();
                }
            });
            holder.posm_remark.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        final EditText Caption = (EditText) v;
                        String value = Caption.getText().toString().replaceAll("&#@,^0+([?!$])", " ");
                        if (!value.equals("")) {
                            list.get(position).setPosmRemark(value);
                        } else {
                            list.get(position).setPosmRemark("");
                        }
                    }
                }
            });
            holder.quantity_val.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        final EditText Caption = (EditText) v;
                        String value1 = Caption.getText().toString().replaceFirst("^0+(?!$)", "");
                        if (!value1.equals("")) {
                            list.get(position).setQuantity(value1);
/*
                           try {
                                int aaa = Integer.parseInt(value1);
                                if (aaa > 5) {
                                    list.get(position).setQuantity("5");
                                } else {
                                    list.get(position).setQuantity(value1);
                                }
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
*/
                        } else {
                            list.get(position).setQuantity("");
                        }
                    }
                }
            });
            if (!img_str.equalsIgnoreCase("")) {
                if (child_position == position) {
                    list.get(position).setPosm_img(img_str);
                    img_str = "";
                }
            }
            if (!list.get(position).getPosm_img().equalsIgnoreCase("")) {
                holder.posm_cam.setImageResource(R.drawable.cam_icon_done);
            } else if (list.get(position).getPosm_img().equals("") && list.get(position).getFLAG().get(0).equals("1")) {
                holder.posm_cam.setImageResource(R.drawable.menditory_camera_icon);
            } else {
                holder.posm_cam.setImageResource(R.drawable.cam_icon);
            }

            holder.posm_remark.setText(list.get(position).getPosmRemark());
            holder.quantity_val.setText(list.get(position).getQuantity());
            holder.POSMName.setText(list.get(position).getPOSM().get(0));
            holder.posm_remark.setId(position);
            holder.quantity_val.setId(position);
            holder.posm_cam.setId(position);
            holder.POSMName.setId(position);
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView POSMName;
            EditText quantity_val, posm_remark;
            ImageView posm_cam;
            LinearLayout posm_rl;

            public MyViewHolder(View convertView) {
                super(convertView);
                POSMName = (TextView) convertView.findViewById(R.id.posm_name);
                quantity_val = (EditText) convertView.findViewById(R.id.quantity_val);
                posm_cam = (ImageView) convertView.findViewById(R.id.posm_cam);
                posm_rl = (LinearLayout) convertView.findViewById(R.id.posm_rl);
                posm_remark = (EditText) convertView.findViewById(R.id.posm_remark);
            }
        }


    }

    public void showMessage(String message) {
        Snackbar.make(posmlistV, message, Snackbar.LENGTH_LONG).show();

    }

    public boolean validate1() {
        boolean flag = true;
        if (posmlist.size() > 0) {
            for (int i = 0; i < posmlist.size(); i++) {
                if (posmlist.get(i).getFLAG().get(0).equals("1") && !posmlist.get(i).getQuantity().equalsIgnoreCase("") && posmlist.get(i).getPosm_img().equalsIgnoreCase("")) {
                    showMessage("Please Capture " + posmlist.get(i).getPOSM().get(0) + " Posm Image");
                    flag = false;
                    break;
                } else if (posmlist.get(i).getFLAG().get(0).equals("1") && posmlist.get(i).getQuantity().equalsIgnoreCase("") && !posmlist.get(i).getPosm_img().equalsIgnoreCase("")) {
                    showMessage("Please Enter " + posmlist.get(i).getPOSM().get(0) + " Posm Quantity");
                    flag = false;
                    break;
                } else if (posmlist.get(i).getFLAG().get(0).equals("1") && posmlist.get(i).getQuantity().replaceFirst("^0+(?!$)", "").equalsIgnoreCase("")) {
                    showMessage("Please Enter Posm Quantity");
                    flag = false;
                    break;
                } /*else if (posmlist.get(i).getPosm_img() != null && !posmlist.get(i).getPosm_img().equals("")
                        && posmlist.get(i).getPosmRemark().equals("")) {
                    showMessage("Please Fill Remark of " + posmlist.get(i).getPOSM().get(0));
                    flag = false;
                    break;
                }*/
            }
        }
        return flag;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    class Data {
        int value;
        String name;
    }

    private class PosmuploadTask extends AsyncTask<Void, Data, String> {
        boolean flag = true;
        private Context context;
        ArrayList<POSMDATAGetterSetter> list;

        PosmuploadTask(Context context, ArrayList<POSMDATAGetterSetter> list) {
            this.context = context;
            this.list = list;
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
                            return CommonString.METHOD_UPLOAD_DR_STORE_COVERAGE;
                        }

                    }
                    String final_xml = "";
                    final int mid = Integer.parseInt((words[1]));
                    data.value = 30;
                    data.name = "Coverage data Uploading";
                    publishProgress(data);

                    //posm data
                    final_xml = "";
                    onXML = "";
                    if (mid > 0) {
                        if (posmlist.size() > 0) {
                            for (int j = 0; j < posmlist.size(); j++) {
                                uploaded_flag = false;
                                if (!posmlist.get(j).getStatus().equals(CommonString.KEY_U)) {
                                    uploaded_flag = true;
                                    onXML = "[POSM_DATA][MID]"
                                            + mid
                                            + "[/MID]"
                                            + "[CREATED_BY]"
                                            + username
                                            + "[/CREATED_BY]"
                                            + "[QUANTITY]"
                                            + posmlist.get(j).getQuantity()
                                            + "[/QUANTITY]"
                                            + "[POSM_IMG]"
                                            + posmlist.get(j).getPosm_img()
                                            + "[/POSM_IMG]"
                                            + "[REMARK]"
                                            + posmlist.get(j).getPosmRemark()
                                            + "[/REMARK]"
                                            + "[FLAG_MANDATORY]"
                                            + posmlist.get(j).getFLAG().get(0)
                                            + "[/FLAG_MANDATORY]"
                                            + "[POSM_CD]"
                                            + posmlist.get(j).getPOSM_CD().get(0)
                                            + "[/POSM_CD]"
                                            + "[/POSM_DATA]";
                                    final_xml = final_xml + onXML;

                                }
                            }
                            if (uploaded_flag) {
                                final String sos_xml = "[DATA]" + final_xml + "[/DATA]";
                                request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_UPLOAD_XML);
                                request.addProperty("XMLDATA", sos_xml);
                                request.addProperty("KEYS", "POSM_DATA");
                                request.addProperty("USERNAME", username);
                                request.addProperty("MID", mid);
                                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                                envelope.dotNet = true;
                                envelope.setOutputSoapObject(request);
                                androidHttpTransport = new HttpTransportSE(CommonString.URL);
                                androidHttpTransport.call(CommonString.SOAP_ACTION + CommonString.METHOD_UPLOAD_XML, envelope);
                                result = (Object) envelope.getResponse();
                                if (result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                    for (int k = 0; k < posmlist.size(); k++) {
                                        statusflag = true;
                                        posmlist.get(k).setStatus(CommonString.KEY_U);
                                    }
                                }
                                data.value = 50;
                                data.name = "POSM_DATA";
                                publishProgress(data);
                            }
                        }
                    }

                    File dir = new File(CommonString.FILE_PATH);
                    ArrayList<String> list = new ArrayList();
                    list = getFileNames(dir.listFiles());
                    if (list.size() > 0) {
                        for (int i1 = 0; i1 < list.size(); i1++) {
                            if (list.get(i1).contains("_POSM_IMG_")) {
                                File originalFile = new File(CommonString.FILE_PATH + list.get(i1));
                                result = RetrofitClass.UploadImageByRetrofit(POSMActivity.this, originalFile.getName(), "POSMImages");
                                if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                    return result.toString();
                                }

                            }
                        }
                        db.open();
                        long l = db.updateposmStatus(store_cd, visit_date, CommonString.KEY_U);
                        data.value = 90;
                        data.name = "POSMImages";
                        publishProgress(data);
                        return CommonString.KEY_SUCCESS;
                    }
                }
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
                if (result.toString().equals(CommonString.KEY_SUCCESS)) {
                    db.open();
                    Toast.makeText(POSMActivity.this, "Posm data upload successfully", Toast.LENGTH_LONG).show();
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

    public ArrayList<String> getFileNames(File[] file) {
        ArrayList<String> arrayFiles = new ArrayList<String>();
        if (file.length > 0) {
            for (int i = 0; i < file.length; i++)
                arrayFiles.add(file[i].getName());
        }
        return arrayFiles;
    }

    public void showAlert(String str) {
        AlertDialog.Builder builder = new AlertDialog.Builder(POSMActivity.this);
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
