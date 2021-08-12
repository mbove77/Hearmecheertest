package com.bove.martin.hearmecheer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    WebView webView;
    private PermissionRequest myRequest;
    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 101;
    String serviceURL = "https://ttpsrts.blob.core.windows.net/embed/hearmecheer/index.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = this.findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

        webView.setWebChromeClient(new WebChromeClient() {
               @Override
               public void onPermissionRequest(final PermissionRequest request) {
                   myRequest = request;

                   for (String permission : request.getResources()) {
                       switch (permission) {
                           case "android.webkit.resource.AUDIO_CAPTURE": {
                               askForPermission(request.getOrigin().toString(), Manifest.permission.RECORD_AUDIO, MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
                               break;
                           }
                       }
                   }
               }
           });

         webView.loadUrl(serviceURL);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_RECORD_AUDIO: {
                Log.d("CHEER", "PERMISSION FOR AUDIO");
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    myRequest.grant(myRequest.getResources());
                    webView.loadUrl(serviceURL);
                }
            }
        }
    }


    public void askForPermission(String origin, String permission, int requestCode) {
        Log.d("CHEER", "inside askForPermission for" + origin + "with" + permission);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), permission) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permission)) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
            }
        } else {
            myRequest.grant(myRequest.getResources());
        }
    }

}