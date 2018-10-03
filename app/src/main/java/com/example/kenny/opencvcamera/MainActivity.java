package com.example.kenny.opencvcamera;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;


public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    static final int MY_PERMISSIONS_REQUEST_CAMERA = 1242;

    private static String TAG = "MainActivity: ";
    private Handler mUiHandler = new Handler();
    private MyWorkerThread myWorkerThread;

    int ch;


    JavaCameraView javaCameraView;
    Mat mGray;

    BaseLoaderCallback mLoaderCallBack = new BaseLoaderCallback(this) {

        @Override
        public void onManagerConnected(int status) {
            switch(status){
                case BaseLoaderCallback.SUCCESS:{
                    javaCameraView.enableView();
                    break;
                }
                default:{
                    super.onManagerConnected(status);
                    break;
                }
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        javaCameraView = (JavaCameraView)findViewById(R.id.java_camera_view);
        javaCameraView.setVisibility(SurfaceView.VISIBLE);
        javaCameraView.setCvCameraViewListener(this);



    }



    @Override
    protected void onPause() {
        super.onPause();
        if (javaCameraView!=null)
            javaCameraView.disableView();
    }

    @Override
    protected void onDestroy(){
        myWorkerThread.quit();
        super.onDestroy();
        if (javaCameraView!=null)
            javaCameraView.disableView();
    }

    @Override
    protected void onResume() {
        super.onResume();

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
                Toast.makeText(getApplicationContext(), "request permission", Toast.LENGTH_SHORT).show();
            }
        }

        if (OpenCVLoader.initDebug()) {
            Log.i(TAG, "Opencv loaded successfully");
            mLoaderCallBack.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
        else {
            Log.i(TAG, "Opencv not loaded");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this, mLoaderCallBack);
        }
    }


    @Override
    public void onCameraViewStarted(int width, int height) {
        mGray = new Mat(1920, 1080, CvType.CV_8UC1);

    }

    @Override
    public void onCameraViewStopped() {
        mGray.release();
    }


    int a = 0;
    int k = 0;
    String Result = "";
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        mGray = inputFrame.gray();

        Mat mGrayT = mGray.t();
        Core.flip(mGray.t(), mGrayT, 1);
        Imgproc.resize(mGrayT, mGrayT, mGray.size());

        Size sizemGray = mGrayT.size();

        Mat mGrayInnerWindow;

        int rows = (int) sizemGray.height; //строки
        int cols = (int) sizemGray.width; //столбцы

        int left = cols / 2;
        int top = rows / 2;

        int width = cols;
        int height = rows;

        mGrayInnerWindow = mGrayT.submat(rows / 2, rows / 2 + 1, 0, cols);

        rows = mGrayInnerWindow.rows();
        cols = mGrayInnerWindow.cols();
        ch = mGrayInnerWindow.channels();

        int countBlack = 0;
        int countWhite = 0;
        double pixel = 0;
        int count = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double[] data = mGrayInnerWindow.get(i, j); //Сохраняет элемент в массиве
                pixel = data[0] + pixel;
                count++;
                data[0] = 255 - data[0];
                mGrayInnerWindow.put(i, j, data); //вставляет элемент обратно в матрицу
            }
        }

        //                    TextView Text = findViewById(R.id.textView);
        int prov = 0;
        pixel = pixel / count;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double[] data = mGrayInnerWindow.get(i, j); //Сохраняет элемент в массиве
            }
        }
        mGrayInnerWindow.release();
//        if (k < 60) {
//            k++;
//            myWorkerThread = new MyWorkerThread("myWorkerThread");
//            Runnable task = new Runnable() {
//                @Override
//                public void run() {
//                    TextView Text = findViewById(R.id.textView);
//                    Mat mGray1;
//                    mGray1 = mGray;
//                    String result = "";
//                    float res = 0;
//                    float min = 10000000;
//                    float max = 0;
//                    for (int i = 0; i < mGray1.rows(); i++) {
//                        if (mGray1.get(i, 50)[0] < min) {
//                            min = Float.parseFloat(String.valueOf(mGray1.get(i, 50)[0]));
//                        }
//                        if (mGray1.get(i, 50)[0] > max) {
//                            max = Float.parseFloat(String.valueOf(mGray1.get(i, 50)[0]));
//                        }
//                        result += Float.parseFloat(String.valueOf(mGray1.get(i, 50)[0])) + "  ";
//                    }
////                    for (int i = 0; i < mGray1.rows(); i++) {
////                        res = Float.parseFloat(String.valueOf(mGray1.get(i, 50)[0]));
////                        res = res - min;
//                        if (Float.parseFloat(String.valueOf(mGray1.get(i, 50)[0])) == min) {
//                            result += "0 ";
//                        }
//                        if (Float.parseFloat(String.valueOf(mGray1.get(i, 50)[0])) == max) {
//                            result += "1 ";
//                        }
//                    }
//                    float a = 0;
//                    float b = 0;
//                    float c = 0;
//                    float d = 0;
//                    for (int i = 0; i < mGray1.rows()-4; i++){
//                        a = Float.parseFloat(String.valueOf(mGray1.get(i, 50)[0]));
//                        b = Float.parseFloat(String.valueOf(mGray1.get(i+1, 50)[0]));
//                        c = Float.parseFloat(String.valueOf(mGray1.get(i+2, 50)[0]));
//                        d = Float.parseFloat(String.valueOf(mGray1.get(i+3, 50)[0]));
////                        if (b <= a & c <= a & d < a) {
////                            result += "1 ";
////                        }
//                        if (a >= b & a >= c & a >= d) {
//                            result += "1 ";
//                        } else {
//                            result += "1 ";
//                        }
//                    }
//
//
//                    Text.setText(result);
//                    Result += result;
//                    Text.setText(Result);
//                    mUiHandler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                          TextView Text = findViewById(R.id.textView);
//                          Text.setText("potok - " + String.valueOf(k));
//                        }
//                    });
//                }
//            };
//            myWorkerThread.start();
//            myWorkerThread.prepareHandler();
//            myWorkerThread.postTask(task);
//        }


        return mGrayT;
    }

    public static Boolean writeToSDFile(String directory, String file_name, String text) {

        File root = Environment.getExternalStorageDirectory();
        File dir = new File(root.getAbsolutePath() + "/" + directory);
        dir.mkdirs();
        File file = new File(dir, file_name);
        try {
            FileOutputStream f = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(f);
            pw.print(text);
            pw.flush();
            pw.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }



}

class MyWorkerThread extends HandlerThread {

    private Handler myWorkerHandler;

    public MyWorkerThread(String name) {
        super(name);
    }

    public void postTask(Runnable task) {
        myWorkerHandler.post(task);
    }

    public void prepareHandler(){

        myWorkerHandler = new Handler(getLooper());
    }
}
