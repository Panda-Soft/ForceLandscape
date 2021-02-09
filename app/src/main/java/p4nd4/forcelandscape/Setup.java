package p4nd4.forcelandscape;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.net.Uri;



public class Setup extends Activity {
    final public static int OVERLAY_PERMISSION_REQ_CODE = 1;
    Button btn2;
    Button btn3;
    Switch sw1;
    Switch sw2;
    boolean notificationState;
    boolean autoHideTile;
    SharedPreferences AppData;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setup);
        WebView webView = (WebView) findViewById(R.id.gif1);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.loadUrl("file:///android_asset/howto.htm");
        sw1 = (Switch) findViewById(R.id.switch1);

        AppData = PreferenceManager.getDefaultSharedPreferences(this);
        notificationState = AppData.getBoolean("notificationState", true);



        sw1.setChecked(notificationState);
        sw1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                notificationState=!notificationState;
                editor = AppData.edit();
                editor.putBoolean("notificationState", notificationState);
                editor.commit();

                if (!notificationState) Toast.makeText(getApplicationContext(), "In case of low resources, background service may be killed by system during lock and original orientation may resume.", Toast.LENGTH_LONG).show();


            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            sw1.setVisibility(View.GONE);
        }

        sw2 = (Switch) findViewById(R.id.switch2);

        autoHideTile = AppData.getBoolean("autoHideTile", true);


        sw2.setChecked(autoHideTile);
        sw2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                autoHideTile=!autoHideTile;
                editor = AppData.edit();
                editor.putBoolean("autoHideTile", autoHideTile);
                editor.commit();

                if (!autoHideTile) Toast.makeText(getApplicationContext(), "Quick Settings will remain open after taping this tile.", Toast.LENGTH_LONG).show();
                else Toast.makeText(getApplicationContext(), "Quick Settings will hide after taping this tile.", Toast.LENGTH_LONG).show();


            }
        });

        Button btn1 = (Button) findViewById(R.id.btn1);
        btn1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
                //System.exit(0);
            }
        });

        btn2 = (Button) findViewById(R.id.btn2);
        btn2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://play.google.com/store/apps/dev?id=5847423621940926942"));
                startActivity(i);
            }
        });
        btn3 = (Button) findViewById(R.id.btn3);
        btn3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://github.com/Panda-Soft");
                Intent i = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(i);

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPermissionOverlay();


    }

    @TargetApi(Build.VERSION_CODES.M)
    public void checkPermissionOverlay() {
        TextView PermissionsText;
        Intent intentSettings;
        Log.d("Status", Boolean.toString(Settings.canDrawOverlays(this)));
        if (((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) && (!Settings.canDrawOverlays(this))) || ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) && (!Settings.System.canWrite(this)))) {
            Toast.makeText(this, "Please select " + getString(R.string.app_name) + " and allow permissions first!", Toast.LENGTH_LONG).show();
            if (!Settings.canDrawOverlays(this)){

                 intentSettings = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intentSettings.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intentSettings, OVERLAY_PERMISSION_REQ_CODE);
            }
            if (!Settings.System.canWrite(Setup.this)) {
                intentSettings = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intentSettings.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentSettings);
            }

            PermissionsText = (TextView) findViewById(R.id.permissions);
            PermissionsText.setText(R.string.perm);
            PermissionsText.setTextSize(TypedValue.COMPLEX_UNIT_SP,24);

        } else {
            PermissionsText = (TextView) findViewById(R.id.permissions);
            PermissionsText.setText("");
            PermissionsText.setTextSize(TypedValue.COMPLEX_UNIT_SP,0);
        }

    }

}
