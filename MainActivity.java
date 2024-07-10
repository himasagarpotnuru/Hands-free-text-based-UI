package com.example.sensors;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.w3c.dom.Text;

import java.util.EventListener;


public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private SensorManager sensorManager;
    private Sensor accelerometer, proximity;
    private TextView xtView, ytView, ztView, ptView;
    private boolean existenceA = false, notinitialstate = false;
    private float pX, pY, pZ;
    private float cX, cY, cZ, dX, dY, dZ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);


        xtView = findViewById(R.id.xtextView);
        ytView = findViewById(R.id.ytextView);
        ztView = findViewById(R.id.ztextView);
        ptView = findViewById(R.id.ptextView);

        if(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            existenceA = true;
        }

        else{

            Toast.makeText(this, "Accelerometer Sensor is not available in your device", Toast.LENGTH_SHORT).show();
        }

//        if(sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null){
//
//            proximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
//            existenceP = true;
//
//
//        }
//
//        else{
//
//            Toast.makeText(this,"Proximity sensor is not available in your device",Toast.LENGTH_SHORT).show();
//        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onSensorChanged(SensorEvent event) {

        //ptView.setText("initial");

        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER && existenceA){

            xtView.setText("Acceleration in x: "+event.values[0] + "m/s2");
            ytView.setText("Acceleration in y: "+event.values[1] + "m/s2");
            ztView.setText("Acceleration in z: "+event.values[2] + "m/s2");

            cX = event.values[0];
            cY = event.values[1];
            cZ = event.values[2];

            dX = Math.abs(cX - pX);
            dY = Math.abs(cY - pY);
            dZ = Math.abs(cZ - pZ);

            if(notinitialstate){

                if(dX >= 3 || dY >= 3 || dZ >= 3){

                    if(cX > 0.2){
                        Toast toast = Toast.makeText(this,"Left tilt",Toast.LENGTH_SHORT);
                        toast.show();

                        // 1 second
                        int customDuration = 1000;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                toast.cancel();
                            }
                        }, customDuration);



                    }

                    else if(cX < -0.2){
                        Toast toast = Toast.makeText(this,"Right tilt",Toast.LENGTH_SHORT);
                        toast.show();

                        // 1 second
                        int customDuration = 1000;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                toast.cancel();
                            }
                        }, customDuration);
                    }
                }

                if(cY > 7){ //very basic case for phone to be in vertical

                    if(cZ < pZ && dZ >= 2){

//                        Toast toast = Toast.makeText(this,"Towards You",Toast.LENGTH_SHORT);
//                        toast.show();
//
//                        // 1 second
//                        int customDuration = 1000;
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                toast.cancel();
//                            }
//                        }, customDuration);
                        ptView.setText("towards you!!");
                    }

                    else if(cZ > pZ && dZ > 2){

//                        Toast toast = Toast.makeText(this,"Away from you",Toast.LENGTH_SHORT);
//                        toast.show();
//
//                        // 1 second
//                        int customDuration = 1000;
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                toast.cancel();
//                            }
//                        }, customDuration);
                        ptView.setText("away from you!!");
                    }


                }

            }


            pX = cX;
            pY = cY;
            pZ = cZ;

            notinitialstate = true;
        }
//no use with proximity sensor
//        if(event.sensor.getType() == Sensor.TYPE_PROXIMITY && existenceP){
//
//            ptView.setText(event.values[0] + "cm");
//        }

        //ptView.setText("Away or Towards");

    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(existenceA){
            sensorManager.registerListener(this,accelerometer,SensorManager.SENSOR_DELAY_NORMAL);
        }

//        if(existenceP){
//            sensorManager.registerListener(this,proximity,SensorManager.SENSOR_DELAY_NORMAL);
//        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        if(existenceA){
            sensorManager.unregisterListener(this);
        }

    }
}