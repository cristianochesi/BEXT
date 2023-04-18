package com.example.bexttool;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.AlteredCharSequence;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.DataFormatException;

import static android.Manifest.permission.BODY_SENSORS;
import static android.Manifest.permission.INTERNET;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.content.pm.PackageManager;

import com.google.gson.Gson;
import com.example.bexttool.json.DataPoint;

public class MainActivity extends Activity {

    public static final int MAX_SENSOR_VALUES = 6;
    private static final int PERMISSION_REQUEST_CODE = 200;

    private SensorManager sensorManager;
    private Sensor sensor;
    private SensorEventListener listener;
    private Sensor[] sensorArray;
    private int sensorIndex;
    private TextView viewDeviceType;
    private TextView viewSensorType;
    private TextView viewSensorDetails;
    private TextView viewSensorAccuracy;
    private TextView viewSensorRate;
    private TextView viewSensorRaw;
    //private BarView[] viewBarArray;
    //private LinearLayout viewSensorBarLayout;
    private Button viewSensorNext;
    private Button viewSensorPrev;
    //private GraphView viewSensorGraph;
    private RelativeLayout viewMainLayout;
    private DecimalFormat decimalFormat;
    private boolean stopHandler;
    private Handler uiThreadHandler;
    private Object lockSensorRate;
    private Runnable uiRunnableUpdate;
    private int samplesCount;
    private int samplesSeconds;
    private AlteredCharSequence Snackbar;

    public static BextPublisher bp;
    public static BextConsumer bc;
    public static ArrayList<DataPoint> sessionData;
    public boolean recordingFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        decimalFormat = new DecimalFormat("+@@@@;-@@@@"); // 4 significant figures

        viewDeviceType = (TextView)findViewById(R.id.deviceType);
        viewSensorType = (TextView)findViewById(R.id.sensorType);
        viewSensorDetails = (TextView)findViewById(R.id.sensorDetails);
        viewSensorAccuracy = (TextView)findViewById(R.id.sensorAccuracy);
        viewSensorRate = (TextView)findViewById(R.id.sensorRate);
        viewSensorRaw = (TextView)findViewById(R.id.sensorRaw);
        //viewSensorBarLayout = (LinearLayout)findViewById(R.id.sensorBarLayout);
        //viewSensorNext = (Button)findViewById(R.id.sensorNext);
        //viewSensorPrev = (Button)findViewById(R.id.sensorPrev);
        viewMainLayout = (RelativeLayout)findViewById(R.id.mainLayout);

        /*
        viewBarArray = new BarView[MAX_SENSOR_VALUES];
        for (int i = 0; i < viewBarArray.length; i++) {
            viewBarArray[i] = new BarView(this, null);
            viewSensorBarLayout.addView(viewBarArray[i]);
        }
        */

        /*
        viewSensorGraph = new GraphView(this, null);
        viewMainLayout.addView(viewSensorGraph, 0); // Add underneath everything else
        */

        requestPermission();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        //List<Sensor> list = sensorManager.getSensorList(Sensor.TYPE_ALL);
        List<Sensor> list = sensorManager.getSensorList(Sensor.TYPE_HEART_RATE);
        if (list.size() < 1) {
            Toast.makeText(this, "No sensors returned from getSensorList", Toast.LENGTH_SHORT);
            Logging.fatal("No sensors returned from getSensorList");
        }
        sensorArray = list.toArray(new Sensor[list.size()]);
        for (int i = 0; i < sensorArray.length; i++) {
            Logging.debug("Found sensor " + i + " " + sensorArray[i].toString());
        }
        sensorIndex = 0;
        sensor = sensorArray[sensorIndex];

        /*
        // Implement the ability to cycle through the sensor list with next/prev buttons
        viewSensorNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSensor();
                //changeSensor(+1);
            }
        });
        viewSensorPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopSensor();
                // changeSensor(-1);
            }
        });
        */

        /*
        BextConsumer.init();
        try {
            BextConsumer.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        */


        bc = new BextConsumer(this);
        bc.start();
        bp = new BextPublisher(this);
        bp.start();
        //bp.sendCommand("Pippo");

        // Implement a runnable that updates the rate statistics once per second. Note
        // that if we change sensors, it will take 1 second to adjust to the new speed.
        uiThreadHandler = new Handler();
        lockSensorRate = new Object();
        samplesCount = -1;
        samplesSeconds = 0;
        stopHandler = false;
        uiRunnableUpdate = new Runnable() {
            @Override
            public void run() {
                Logging.debug("Updating the UI every second, count is " + samplesCount);
                if (samplesCount == -1)
                    viewSensorRate.setText("Waiting for first sample ...");
                else if (samplesCount == 0) {
                    samplesSeconds ++;
                    viewSensorRate.setText("No update after " + samplesSeconds + " seconds");
                } else {
                    samplesSeconds = 0;
                    viewSensorRate.setText("" + samplesCount + "/sec at " + (1000 / samplesCount) + " msec");
                    samplesCount = 0;
                }

                if (!stopHandler) {
                    uiThreadHandler.postDelayed(this, 1000);
                }
            }
        };
    }

    /*
    public void changeSensor(int ofs) {
        sensorIndex += ofs;
        if (sensorIndex >= sensorArray.length)
            sensorIndex = 0;
        else if (sensorIndex < 0)
            sensorIndex = sensorArray.length-1;
        sensor = sensorArray[sensorIndex];
        stopSensor();
        startSensor();
    }
    */

    @Override
    public void onStart() {
        super.onStart();
        startSensor();
        uiThreadHandler.post(uiRunnableUpdate);
    }


    public void startSensor() {
        /*
        String type = "#" + (sensorIndex+1) + ", type " + sensor.getType();
        if (Build.VERSION.SDK_INT >= 20)
            type = type + "=" + sensor.getStringType();
        Logging.debug("Opened up " + type + " - " + sensor.toString());
        viewDeviceType.setText(android.os.Build.DEVICE + " " + android.os.Build.ID);
        viewSensorType.setText(type);
        viewSensorDetails.setText(sensor.toString().replace("{Sensor ", "").replace("}", ""));
        */

        /*
        for (int i = 0; i < viewBarArray.length; i++) {
            viewBarArray[i].setMaximum(sensor.getMaximumRange());
        }
        */
        //viewSensorGraph.resetMaximum(sensor.getMaximumRange());
        viewSensorRaw.setText("Waiting for sensor data ...");
        viewSensorAccuracy.setText("Waiting for sensor accuracy ...");
        samplesCount = 0;

        listener = new SensorEventListener() {

            public String getStrFromFloat(float in) {
                if ((in > -0.00001) && (in < 0.00001))
                    in = 0;
                if (in == Math.rint(in))
                    return Integer.toString((int)in);
                else
                    return decimalFormat.format(in);
            }

            public int min(int a, int b) { if (a < b) { return a; } else { return b; } }

            public void onSensorChanged(SensorEvent sensorEvent) {

                if (sensorEvent.sensor.getType() == sensor.getType()) {
                    Logging.detailed("Sensor update: " + Arrays.toString(sensorEvent.values));
                    samplesCount++;

                    String raw = "";
                    for (int i = 0; i < sensorEvent.values.length; i++) {
                        String str = getStrFromFloat(sensorEvent.values[i]);
                        if (raw.length() != 0)
                            raw = raw + "\n";
                        raw = raw + str;
                    }
                    viewSensorRaw.setText(raw);

                    if(recordingFlag == true) {
                        System.out.println("Recording value : " + raw);
                        DataPoint dp = new DataPoint(System.currentTimeMillis(), Integer.valueOf(raw));
                        MainActivity.sessionData.add(dp);
                    }


                    /*
                    if (sensorEvent.values.length != min(sensorEvent.values.length, viewBarArray.length))
                        Logging.debug("Sensor update contained " + sensorEvent.values.length + " which is larger than expected " + viewBarArray.length);
                    for (int i = 0; i < min(sensorEvent.values.length, viewBarArray.length); i++) {
                        viewBarArray[i].setValue(sensorEvent.values[i]);
                    }
                    for (int i = sensorEvent.values.length; i < viewBarArray.length; i++) {
                        viewBarArray[i].setValue(0);
                    }
                    */
                    //viewSensorGraph.setSize(sensorEvent.values.length);
                    //viewSensorGraph.setValues(sensorEvent.values);
                }
            }

            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                Logging.detailed("Accuracy update: " + accuracy);
                viewSensorAccuracy.setText("Accuracy=" + accuracy);
            }
        };
        sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI);
    }

    public void sendDataToServer() {
        Gson gson = new Gson();
        bp.sendData(gson.toJson(sessionData));
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), BODY_SENSORS);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{BODY_SENSORS, INTERNET}, PERMISSION_REQUEST_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean bodySensorAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean internetAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    
                    if (bodySensorAccepted && internetAccepted)
                        Toast.makeText(getApplicationContext(), "Permission Granted, Now you can access body sensors data.", Toast.LENGTH_LONG).show();
                    else {
                        Toast.makeText(getApplicationContext(), "Permission Denied, You cannot access body sensors data.", Toast.LENGTH_LONG).show();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(BODY_SENSORS)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{BODY_SENSORS},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }

                    }
                }


                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


    @Override
    public void onStop() {
        super.onStop();
        stopSensor();
        uiThreadHandler.removeCallbacks(uiRunnableUpdate);
    }

    private void stopSensor() {
        sensorManager.unregisterListener(listener);
    }
}
