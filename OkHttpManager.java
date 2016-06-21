package com.neil2hw.www.okhttpmanager;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Project: OkHttpManager
 * Date   : 2016/6/21
 * Author : neil2wm
 */
public class OkHttpManager {

    public static final String TAG = "OkHttpManager";
    public static final MediaType MEDIA_TYPE_ALL = MediaType.parse("application/octet-stream");
    public static final long CONNECT_TIME_OUT = 5;
    public static final long READ_TIME_OUT = 5;
    public static final long WRITE_TIME_OUT = 5;

    private static OkHttpManager mOkHttpManager = null;
    private OkHttpClient mOkHttpClient = null;

    /*************************************** Open Methods ****************************************/

    public static Response syncHttpGet(String url) throws IOException {
        return getInstance()._syncHttpGet(url);
    }

    public static void asyncHttpGet(String url, Callback callback) {
        getInstance()._asyncHttpGet(url, callback);
    }

    public static Response syncHttpPost(String url, StringParameter[] stringParameters) throws IOException {
        return getInstance()._syncHttpPost(url, stringParameters);
    }

    public static void asyncHttpPost(String url, StringParameter[] stringParameters, Callback callback) {
        getInstance()._asyncHttpPost(url, stringParameters, callback);
    }

    public static Response syncHttpPost(String url, FileParameter[] fileParameters) throws IOException {
        return getInstance()._syncHttpPost(url, fileParameters);
    }

    public static void asyncHttpPost(String url, FileParameter[] fileParameters, Callback callback) {
        getInstance()._asyncHttpPost(url, fileParameters, callback);
    }

    public static Response syncHttpPost(String url, StringParameter[] stringParameters, FileParameter[] fileParameters) throws IOException {
        return getInstance()._syncHttpPost(url, stringParameters, fileParameters);
    }

    public static void asyncHttpPost(String url, StringParameter[] stringParameters, FileParameter[] fileParameters, Callback callback) {
        getInstance()._asyncHttpPost(url, stringParameters, fileParameters, callback);
    }

    /*************************************** Close Methods ***************************************/

    private OkHttpManager() {
        mOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIME_OUT, TimeUnit.SECONDS)
                .build();
    }

    private static OkHttpManager getInstance() {
        if (mOkHttpManager == null) {
            synchronized (OkHttpManager.class) {
                if (mOkHttpManager == null) {
                    mOkHttpManager = new OkHttpManager();
                }
            }
        }
        return mOkHttpManager;
    }

    private Response _syncHttpGet(String url) throws IOException {
        return mOkHttpClient.newCall(buildRequest(url)).execute();
    }

    private void _asyncHttpGet(String url, Callback callback) {
        mOkHttpClient.newCall(buildRequest(url)).enqueue(callback);
    }

    private Response _syncHttpPost(String url, StringParameter[] stringParameters) throws IOException {
        return _syncHttpPost(url, stringParameters, null);
    }

    private void _asyncHttpPost(String url, StringParameter[] stringParameters, Callback callback) {
        _asyncHttpPost(url, stringParameters, null, callback);
    }

    private Response _syncHttpPost(String url, FileParameter[] fileParameters) throws IOException {
        return _syncHttpPost(url, null, fileParameters);
    }

    private void _asyncHttpPost(String url, FileParameter[] fileParameters, Callback callback) {
        _asyncHttpPost(url, null, fileParameters, callback);
    }

    private Response _syncHttpPost(String url, StringParameter[] stringParameters, FileParameter[] fileParameters) throws IOException {
        return mOkHttpClient.newCall(buildRequest(url, stringParameters, fileParameters)).execute();
    }

    private void _asyncHttpPost(String url, StringParameter[] stringParameters, FileParameter[] fileParameters, Callback callback) {
        mOkHttpClient.newCall(buildRequest(url, stringParameters, fileParameters)).enqueue(callback);
    }

    private Request buildRequest(String url) {
        return new Request.Builder().url(url).build();
    }

    private Request buildRequest(String url, StringParameter[] stringParameters, FileParameter[] fileParameters) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        if (stringParameters != null && stringParameters.length > 0) {
            for (StringParameter stringParameter : stringParameters) {
                builder.addFormDataPart(stringParameter.getKey(), stringParameter.getValue());
            }
        }
        if (fileParameters != null && fileParameters.length > 0) {
            for (FileParameter fileParameter : fileParameters) {
                builder.addFormDataPart(fileParameter.getKey(), fileParameter.getFileName(),
                        RequestBody.create(MEDIA_TYPE_ALL , fileParameter.getFile()));
            }
        }
        return new Request.Builder().url(url).post(builder.build()).build();
    }

    /***************************************** Open Class ****************************************/

    public static class StringParameter {

        private String key;
        private String value;

        public StringParameter(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }
    }

    public static class FileParameter {

        private String key;
        private String fileName;
        private File file;

        public FileParameter(String key, String fileName, File file) {
            this.key = key;
            this.fileName = fileName;
            this.file = file;
        }

        public String getKey() {
            return key;
        }

        public String getFileName() {
            return fileName;
        }

        public File getFile() {
            return file;
        }

    }

}
