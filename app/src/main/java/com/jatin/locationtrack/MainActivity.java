package com.jatin.locationtrack;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    public static final String RECEIVE_JSON = "com.jatin.locationtrack.RECEIVE_JSON";
    Button btnLocationSharing;
    TextView txtCoordinates, txtAddress;
    Double Latitude, Longitude;
    String Provider;
    BroadcastReceiver receiver;
    EditText inp_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initReference();
        initListener();

        receiver  = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(RECEIVE_JSON)) {
                    Provider = intent.getStringExtra("Provider");
                    Latitude = (Double)intent.getExtras().get("Latitude");
                    Longitude = (Double)intent.getExtras().get("Longitude");
                    txtAddress.setText("Provider : "+Provider);
                    txtCoordinates.setText("Lat:" + Latitude + " ,Long:" + Longitude);

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference userRef = database.getReference("users");
                    String user_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                    userRef.child(user_id).setValue(new Location(Latitude,Longitude,inp_name.getText().toString(),user_id));
                }
            }
        };

        LocalBroadcastManager bManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RECEIVE_JSON);
        bManager.registerReceiver(receiver, intentFilter);

    }

    private void initReference() {
        btnLocationSharing = (Button)findViewById(R.id.btnLocationSharing);
        txtAddress = (TextView) findViewById(R.id.txtAddress);
        txtCoordinates = (TextView) findViewById(R.id.txtCoordinates);
        inp_name = (EditText) findViewById(R.id.inp_name);
    }

    private void initListener() {
        btnLocationSharing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btnLocationSharing.getText().toString().equalsIgnoreCase("Start location sharing")){
                    if (inp_name.getText().toString().equals("")){
                        Toast.makeText(MainActivity.this,"Please enter a name to contimue",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                    }
                    hideSoftKeyboard();
                }else{
                    btnLocationSharing.setText("Start location sharing");
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            }
        });
        txtAddress = (TextView) findViewById(R.id.txtAddress);
        txtCoordinates = (TextView) findViewById(R.id.txtCoordinates);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(MainActivity.this, LocationService.class);
                    startService(intent);
                    btnLocationSharing.setText("Stop location sharing");

                } else {
                    Toast.makeText(getApplicationContext(),"Permission Denied",Toast.LENGTH_SHORT).show();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}
