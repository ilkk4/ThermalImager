package com.example.thermalimager;

import androidx.appcompat.app.AppCompatActivity;
import com.flir.flironesdk.Device;
import com.flir.flironesdk.Frame;
import com.flir.flironesdk.FrameProcessor;
import com.flir.flironesdk.RenderedImage;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Timer;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, Device.Delegate, FrameProcessor.Delegate, Device.PowerUpdateDelegate, Device.StreamDelegate{

    private TextView textDisplay;
    Device flirDevice;
    private ImageView imageView;
    private FrameProcessor frameProcessor;
    private TextView flirStats;

    private boolean ranOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textDisplay = findViewById(R.id.textView);
        imageView = findViewById(R.id.imageView);
        flirStats = findViewById(R.id.textViewFlirStats);

        Button B = findViewById(R.id.buttonStart);

        B.setOnClickListener(this);

        // Blended visual + thermal
        frameProcessor = new FrameProcessor(this, this,
                EnumSet.of(RenderedImage.ImageType.BlendedMSXRGBA8888Image));

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(flirDevice != null){
            flirDevice.startFrameStream(this);
        }else{
            Device.startDiscovery(this, this);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        Device.stopDiscovery();
    }
    @Override
    public void onClick(View v) {
        //textDisplay.setText("Button pressed!");

    }
    @Override
    public void onTuningStateChanged(Device.TuningState tuningState) {

    }
    @Override
    public void onAutomaticTuningChanged(boolean b) {

    }
    @Override
    public void onDeviceDisconnected(Device device) {

    }

    @Override
    public void onDeviceConnected(Device device) {
        flirDevice = device;
        flirDevice.setAutomaticTuning(false);
        flirDevice.setPowerUpdateDelegate(this);
        flirDevice.startFrameStream(this);

    }

    @Override
    public void onFrameReceived(Frame frame) {
        frameProcessor.processFrame(frame);
    }

    @Override
    public void onFrameProcessed(final RenderedImage renderedImage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imageView.setImageBitmap(renderedImage.getBitmap());
            }
        });

    }

    @Override
    public void onBatteryChargingStateReceived(Device.BatteryChargingState batteryChargingState) {
        flirStats.setText("FLIR:"+batteryChargingState.toString());
    }

    @Override
    public void onBatteryPercentageReceived(byte b) {
        flirStats.setText("FLIR:"+b);

    }


}