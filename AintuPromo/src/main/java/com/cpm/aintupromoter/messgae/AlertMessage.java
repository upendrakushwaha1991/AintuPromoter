package com.cpm.aintupromoter.messgae;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import com.cpm.aintupromoter.LoginActivity;
import com.cpm.aintupromoter.MainManuActivity;
import com.cpm.aintupromoter.R;
import com.cpm.aintupromoter.dailyentry.ActivityManu;
import com.cpm.aintupromoter.downloads.CompleteDownloadActivity;

/**
 * Created by jeevanp on 25-07-2017.
 */

public class AlertMessage {
    public static final String MESSAGE_DATA_NOT = "Data Not Uploaded , ";
    public static final String MESSAGE_DELETE = "Do You Want To deletecross This Record";
    public static final String MESSAGE_SAVE = "Do You Want To Save The Data ";
    public static final String MESSAGE_FAILURE = "Server Eroor.Please Access After Some Time";
    public static final String MESSAGE_JCP_FALSE = "Data is not found in ";
    public static final String MESSAGE_INVALID_DATA = "Enter Data";
    public static final String MESSAGE_DUPLICATE_DATA = "Data Already Exist";
    public static final String MESSAGE_DOWNLOAD = "Data Downloaded Successfully";
    public static final String MESSAGE_UPLOAD_DATA = "Data Uploaded Successfully";
    public static final String MESSAGE_UPLOAD_IMAGE = "Images Uploaded Successfully";
    public static final String MESSAGE_FALSE = "Invalid User";
    public static final String MESSAGE_CHANGED = "Invalid UserId Or Password / Password Has Been Changed.";
    public static final String MESSAGE_EXIT = "Do You Want To Exit";
    public static final String MESSAGE_BACK = "Use Back Button";
    public static final String MESSAGE_EXCEPTION = "Problem Occured : Report The Problem To Parinaam";
    public static final String MESSAGE_SOCKETEXCEPTION = "Network Communication Failure. Check Your Network Connection";
    public static final String MESSAGE_NO_DATA = "No Data For Upload";
    public static final String MESSAGE_NO_IMAGE = "No Image For Upload";
    public static final String MESSAGE_DATA_FIRST = "Upload Data First";
    public static final String MESSAGE_IMAGE_UPLOAD = "Upload Images";
    public static final String MESSAGE_PARTIAL_UPLOAD = "Data Partially Uploaded";
    public static final String MESSAGE_DATA_UPLOAD = "Data Uploaded";
    public static final String MESSAGE_CHECKOUT_UPLOAD = "Store Already Checkedout";
    public static final String MESSAGE_UPLOAD = "All Data Uploaded";
    public static final String MESSAGE_LEAVE_UPLOAD = "Leave Data Uploaded";
    public static final String MESSAGE_ERROR = "Network Error , ";
    public static final String MESSAGE_NO_UPDATE = "No Update Available";
    public static final String MESSAGE_LEAVE = "On Leave";
    public static final String MESSAGE_CHECKOUT = "Store Successfully Checkedout";

    private Exception exception;
    String value;
    private String data, condition;
    private Activity activity;

    public AlertMessage(Activity activity, String data, String condition,
                        Exception exception) {
        this.activity = activity;
        this.data = data;
        this.condition = condition;
        this.exception = exception;
    }
    public void showMessage() {
        // TODO Auto-generated method stub
        if (condition.equals("socket_login")) {
            socket_login(data);
        } else if (condition.equals("socketupload")) {
            covrageupload(data);
        } else if (condition.equals("checkout")) {
            checkout(data);
        } else if (condition.equals("posmupload")) {
            posmupload(data);
        } else if (condition.equals("socket")) {
            socket(data);
        } else if (condition.equals("socket_upload")) {
            socket_upload(data);
        } else if (condition.equals("socket_uploadall")) {
            socket_uploadall(data);
        } else if (condition.equals("socket_uploadimage")) {
            socket_uploadimage(data);
        } else if (condition.equals("socket_uploadimagesall")) {

            socket_uploadallimage(data);

        } else if (condition.equals("download")) {

            acra(data);

        } else if (condition.equals("login")) {

            ShowAlert2(data);
        } else if (condition.equals("success")) {

            ShowAlert1(data);
        } else if (condition.equals("upload_all")) {

            //	uploadall(data);
        } else if (condition.equals("exit")) {
            doExit(data);
        } else if (condition.equals("update")) {
            update(data);
        } else if (condition.equals("store")) {
            ShowAlert1(data);
        }

    }

    public void acra(String str) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Parinaam");
        builder.setMessage(str)
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent(activity, MainManuActivity.class);
                                activity.startActivity(i);
                                activity.finish();
                            }
                        })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Intent i = new Intent(activity, MainManuActivity.class);
                        activity.startActivity(i);
                        activity.finish();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();

    }


    public void ShowAlert1(String str) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Parinaam");
        builder.setMessage(str).setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        //	Intent i = new Intent(activity, MainMenuActivity.class);

                        Intent i = new Intent(activity, MainManuActivity.class);
                        activity.startActivity(i);

                        activity.finish();

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }

    public void ShowAlert2(String str) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Parinaam");
        builder.setMessage(str).setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }

    public void doExit(String str) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(str).setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Intent i = new Intent(activity, LoginActivity.class);
                        activity.startActivity(i);

                        activity.finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                    }
                });

        AlertDialog alert = builder.create();
        alert.show();

    }

    public void update(String str) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(str)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent i = new Intent(activity, MainManuActivity.class);
                        activity.startActivity(i);
                        activity.finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Intent i = new Intent(activity, MainManuActivity.class);
                        activity.startActivity(i);

                        activity.finish();

                    }
                });

        AlertDialog alert = builder.create();
        alert.show();

    }


    public void socket(String str) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Parinaam");
        builder.setMessage(str)
                .setCancelable(false)
                .setPositiveButton("Try Again",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.cancel();
                                Intent i = new Intent(activity,
                                        CompleteDownloadActivity.class);
                                activity.startActivity(i);

                                activity.finish();
                            }
                        })

                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                Intent i = new Intent(activity,
                                        MainManuActivity.class);
                                activity.startActivity(i);

                                activity.finish();
                            }
                        });

        AlertDialog alert = builder.create();
        alert.show();

    }

    public void checkout(String str) {


        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Parinaam");
        builder.setMessage(str).setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Intent i = new Intent(activity, MainManuActivity.class);
                        activity.startActivity(i);

                        activity.finish();
                    }
                });

    }


    public void posmupload(String str) {


        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Parinaam");
        builder.setMessage(str).setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(activity, ActivityManu.class);
                        activity.startActivity(intent);
                        dialog.cancel();
                        activity.finish();
                    }
                });

    }


    public void covrageupload(String str) {


        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Parinaam");
        builder.setMessage(str).setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        activity.finish();
                    }
                });

    }


    public void socket_upload(String str) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Parinaam");
        builder.setMessage(str).setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                        Intent i = new Intent(activity, MainManuActivity.class);
                        activity.startActivity(i);

                        activity.finish();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();

    }

    public void socket_uploadall(String str) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Parinaam");
        builder.setMessage(str)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                        Intent i = new Intent(activity, MainManuActivity.class);
                        activity.startActivity(i);

                        activity.finish();
                    }
                })

                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                Intent i = new Intent(activity,
                                        MainManuActivity.class);
                                activity.startActivity(i);

                                activity.finish();
                            }
                        });

        AlertDialog alert = builder.create();
        alert.show();

    }

    public void socket_uploadimage(String str) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Parinaam");
        builder.setMessage(str)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                        Intent i = new Intent(activity, MainManuActivity.class);
                        activity.startActivity(i);

                        activity.finish();
                    }
                })

                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                Intent i = new Intent(activity,
                                        MainManuActivity.class);
                                activity.startActivity(i);

                                activity.finish();
                            }
                        });

        AlertDialog alert = builder.create();
        alert.show();

    }

    public void socket_uploadallimage(String str) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Parinaam");
        builder.setMessage(str)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                        Intent i = new Intent(activity, MainManuActivity.class);
                        activity.startActivity(i);

                        activity.finish();
                    }
                })

                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                Intent i = new Intent(activity,
                                        MainManuActivity.class);
                                activity.startActivity(i);

                                activity.finish();
                            }
                        });

        AlertDialog alert = builder.create();
        alert.show();

    }

    public void socket_login(String str) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Parinaam");
        builder.setMessage(str)
                .setCancelable(false)
                .setPositiveButton("OK ",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.cancel();

                            }
                        })

                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                            }
                        });

        AlertDialog alert = builder.create();
        alert.show();

    }


}





