package com.cpm.aintupromoter.retrofit;

import android.content.Context;
import android.widget.Toast;

import com.cpm.aintupromoter.constants.CommonString;
import com.cpm.aintupromoter.messgae.AlertMessage;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

import static com.cpm.aintupromoter.compressimage.Utilities.saveBitmapToFile;

/**
 * Created by jeevanp on 19-05-2017.
 */

public class RetrofitClass {
    public static Context context;
    private static Retrofit adapter, adapter1;
    public static RequestBody body1, body2;
    public static String result = "", result1 = "";
    static boolean isvalid = false, isvalid1 = false;

    public static synchronized String UploadImageByRetrofit(final Context context, final String file_name, String folder_name) {
        isvalid = false;
        result = "";
        File originalFile = new File(CommonString.FILE_PATH + file_name);
        final File finalFile = saveBitmapToFile(originalFile);
        RequestBody photo = RequestBody.create(MediaType.parse("application/octet-stream"), finalFile);
        body1 = new MultipartBuilder().type(MultipartBuilder.FORM)
                .addFormDataPart("file", finalFile.getName(), photo)
                .addFormDataPart("FolderName", folder_name)
                .build();
        adapter = new Retrofit.Builder()
                .baseUrl(CommonString.URLFORRETROFIT)
                .addConverterFactory(new StringConverterFactory())
                .build();
        PostApi api = adapter.create(PostApi.class);
        Call<String> call = api.getUploadImage(body1);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Response<String> response) {
                if (response != null) {
                    if (response.body().contains(CommonString.KEY_SUCCESS)) {
                        isvalid = true;
                        result = CommonString.KEY_SUCCESS;
                        finalFile.delete();
                    } else {
                        result = "Servererror!";
                    }
                } else {
                    result = "Servererror!";
                }
            }
            @Override
            public void onFailure(Throwable t) {
                isvalid = true;
                if (t instanceof SocketException) {
                    result = AlertMessage.MESSAGE_SOCKETEXCEPTION;
                } else {
                    result = AlertMessage.MESSAGE_SOCKETEXCEPTION;
                }
                Toast.makeText(context, finalFile.getName() + " not uploaded", Toast.LENGTH_SHORT).show();

            }
        });

        try {
            while (isvalid == false) {
                synchronized (context) {
                    context.wait(25);
                }
            }
            if (isvalid) {
                synchronized (context) {
                    context.notify();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (result.equals("")) {
            result = AlertMessage.MESSAGE_SOCKETEXCEPTION;
        }else {
            return  result;
        }
        return result;
    }


}
