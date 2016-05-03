package com.example.isa.privatekeep;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.drive.DriveScopes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;

public class DisplayActivity extends AppCompatActivity {

    // private RetrivalData data =null;
    GoogleAccountCredential mCredential;
    private static final String[] SCOPES = {DriveScopes.DRIVE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        Intent intent = this.getIntent();
        String fileId = intent.getStringExtra("FileId");
        String accountName = intent.getStringExtra("AccountName");
        Toast.makeText(this, fileId,Toast.LENGTH_SHORT).show();

        initCredntials(accountName);
        RetrivalData retrivalData = new RetrivalData(mCredential, fileId);
        new DownloadAndDisplay().execute(retrivalData);
    }

    private void initCredntials(String accountName){
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

        mCredential.setSelectedAccountName(accountName);
    }

    private class DownloadAndDisplay extends AsyncTask<RetrivalData,Void,String>{
        private static final String TAG = "DownloadData";
        private com.google.api.services.drive.Drive mService = null;

        private com.google.api.services.drive.Drive initService(GoogleAccountCredential mCredential){
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.drive.Drive.Builder(
                    transport, jsonFactory, mCredential)
                    .setApplicationName("Drive API Android Quickstart")
                    .build();
            return mService;
        }

        @Override
        protected String doInBackground(RetrivalData... retrivalDatas) {
            if(retrivalDatas.length == 0)return null;
            RetrivalData data = retrivalDatas[0];

            com.google.api.services.drive.Drive mService = null;
            mService = initService(data.getCredential());
            String result = download(mService, data.getFileId());
            return result;
        }

        private String download(com.google.api.services.drive.Drive mService,String fileId){
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            String result = null;
            try {
                mService.files().get(fileId).executeMediaAndDownloadTo(os);
                result = new String(os.toByteArray(), "UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            TextView textView = (TextView) findViewById(R.id.textView);
            if(result != null){
                textView.setText(result);
            }else{
                textView.setText("NO DATA");
            }
        }
    }
}

class RetrivalData implements Serializable{
    private GoogleAccountCredential credential=null;
    private String fileId = null;
    public RetrivalData(GoogleAccountCredential mCredential,String fileId){
        this.credential = mCredential;
        this.fileId = fileId;
    }

    public GoogleAccountCredential getCredential(){
        return credential;
    }
    public String getFileId(){
        return fileId;
    }
}