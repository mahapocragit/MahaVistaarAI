package in.gov.mahapocra.mahavistaarai.ai_disc_tp_imp.identify;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import in.gov.mahapocra.mahavistaarai.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class CAM2 extends AppCompatActivity {
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    private static final String TAG = "CAM2";
    TextView tips;
    int rt=1;
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }
    private Size imageDimension;
    FrameLayout dfg;
    public static final String CAMERA_FRONT = "1";
    public static final String CAMERA_BACK = "0";
    protected CaptureRequest.Builder ca;
    private String cameraId = CAMERA_BACK;
    Camera camera;
    View viewToIncreaseHeight;
    protected CaptureRequest.Builder capturebuilder;
    ImageView torch, zoomin,zoomout;
    CameraManager cameraManager;
    Button getpicture;
    private Size previewsize;
    private Size jpegSizes[] = null;
    private TextureView textureView;
    private CameraDevice cameraDevice;
    static int tr=0;
    CameraCharacteristics characteristics;
//    private android.graphics.Rect zoom = null;
    private CaptureRequest.Builder previewBuilder;
    private CameraCaptureSession previewSession;
    Camera.Parameters params;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public void onOpened(CameraDevice camera) {
            cameraDevice = camera;

            startCamera();
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
        }

        @Override
        public void onError(CameraDevice camera, int error) {
        }
    };
    private TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {

            openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }
    };

    int currentZoomLevel ;
    int maxZoomLevel ;
    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "NIBPPApp");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                //Log.d("NIBPPApp", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");
        return mediaFile;
    }

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cam2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("AI-DISC");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textureView = (TextureView) findViewById(R.id.textureview);
        textureView.setSurfaceTextureListener(surfaceTextureListener);
        ZoomControls zoomControls = (ZoomControls) findViewById(R.id.CAMERA_ZOOM_CONTROLS);
        zoomControls.setVisibility(View.GONE);
        camera=Camera.open();
        params=camera.getParameters();
        if(params.isZoomSupported()){
            maxZoomLevel = params.getMaxZoom();

            zoomControls.setIsZoomInEnabled(true);
            zoomControls.setIsZoomOutEnabled(true);

            zoomControls.setOnZoomInClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(currentZoomLevel > 0){
                        currentZoomLevel--;
                        camera.startSmoothZoom(currentZoomLevel);
                    }
                }
            });
            zoomControls.setOnZoomOutClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(currentZoomLevel > 0){
                        currentZoomLevel--;
                        camera.startSmoothZoom(currentZoomLevel);
                    }
                }
            });


        }
        torch=findViewById(R.id.torch);
        cameraManager= (CameraManager) getSystemService(CAMERA_SERVICE);
        camera=Camera.open();
         params= camera.getParameters();

        getpicture = (Button) findViewById(R.id.getpicture);
        tips = (TextView) findViewById(R.id.blinktips);
        dfg=findViewById(R.id.frame_layout);
        viewToIncreaseHeight=findViewById(R.id.vi);
         Animation anim = AnimationUtils.loadAnimation(this, R.anim.cam_view_anim);
        viewToIncreaseHeight.startAnimation(anim);

        if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)){
            if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)){
                    int f=0;

            }else{
                Toast.makeText(CAM2.this, "NO FLASH", Toast.LENGTH_SHORT).show();
                torch.setEnabled(false);
            }
        }else{
            Toast.makeText(CAM2.this, "NO CAMERA", Toast.LENGTH_SHORT).show();
            torch.setEnabled(false);
        }

        torch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tr==0){
                    Toast.makeText(CAM2.this, "Flash On", Toast.LENGTH_SHORT).show();
                    torch.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_flash_on_24, getApplicationContext().getTheme()));
                        tr=1;

                }
                else{
                    Toast.makeText(CAM2.this, "Flash Off", Toast.LENGTH_SHORT).show();
                    torch.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_flash_off_24, getApplicationContext().getTheme()));
                        tr=0;

                }
            }
        });

        dfg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                rt=3;
                return false;
            }
        });
       blink();

        getpicture.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                rt=2;
                tips.setVisibility(View.INVISIBLE);
                viewToIncreaseHeight.setVisibility(View.INVISIBLE);

                torch.setVisibility(View.INVISIBLE);
                getPicture();
            }
        });
    }

    private void blink(){
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int timeToBlink = 500;    //in milissegunds
                try{Thread.sleep(timeToBlink);}catch (Exception e) {}
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (rt==1){

                        if(viewToIncreaseHeight.getVisibility() == View.VISIBLE){
                            //tips.setVisibility(View.INVISIBLE);
                            viewToIncreaseHeight.setVisibility(View.INVISIBLE);
                        }else{
                            //tips.setVisibility(View.VISIBLE);
                            viewToIncreaseHeight.setVisibility(View.VISIBLE);
                        }
                        blink();
                    }
                    else{
                            tips.setVisibility(View.INVISIBLE);
                            viewToIncreaseHeight.setVisibility(View.VISIBLE);
                        }}
                });
            }
        }).start();
    }

    private Uri saveImage(byte[] bytes, int angle) throws IOException {
        OutputStream fos = null;
        File imageFile = null;
        Uri imageUri = null;
        Bitmap bitmap = null;

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "IMG_" + timeStamp + ".jpg";

        String folderName = "AI-DISC";
        //Log.d(TAG, "saveImage sdk : " + Build.VERSION.SDK_INT);
        //Log.d(TAG, "saveImage q: " + Build.VERSION_CODES.Q);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
           // Log.d(TAG, "saveImage: inside if  q ");
            ContentResolver resolver = getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + folderName);
            imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            fos = resolver.openOutputStream(imageUri);


        } else {

           // Log.d(TAG, "saveImage: inside else ");
            String imagesDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES).toString() + File.separator + folderName;
            imageFile = new File(imagesDir);

            //Log.d(TAG, "saveImage: file   " + imageFile.exists());
            if (!imageFile.exists()) {
                imageFile.mkdir();
                //Log.d(TAG, "saveImage: file created  " + imageFile.exists());
            }
            imageFile = new File(imagesDir, fileName + ".jpg");
            fos = new FileOutputStream(imageFile);


        }


        bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // Create a new image
        Bitmap bitmap1 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        boolean saved = bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        fos.flush();
        fos.close();


        if (imageFile != null)  // pre Q
        {
            MediaScannerConnection.scanFile(getBaseContext(), new String[]{imageFile.toString()}, null, null);
            imageUri = Uri.fromFile(imageFile);
        }


        return imageUri;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void getPicture() {
        if (cameraDevice == null) {
            return;
        }
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {

            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDevice.getId());
            if (characteristics != null) {
                jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
            }

            int width = 640, height = 480;
            if (jpegSizes != null && jpegSizes.length > 0) {
                width = jpegSizes[0].getWidth();
                height = jpegSizes[0].getHeight();
            }
            ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
            List<Surface> outputSurfaces = new ArrayList<Surface>(2);
            outputSurfaces.add(reader.getSurface());
            outputSurfaces.add(new Surface(textureView.getSurfaceTexture()));
             capturebuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            capturebuilder.addTarget(reader.getSurface());
            capturebuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

            if (tr==1){
                capturebuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_TORCH);
            }
            if (tr==0){
                capturebuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_OFF);
            }

            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            capturebuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));

            ImageReader.OnImageAvailableListener imageAvailableListener = new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    Image image = null;
                    try {
                        image = reader.acquireLatestImage();
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.capacity()];
                        buffer.get(bytes);
                        //save(bytes);
                        save1(bytes, ORIENTATIONS.get(rotation));
                    } catch (Exception ee) {
                    } finally {
                        if (image != null)
                            image.close();
                    }
                }

                /*
                void save(byte[] bytes) {
                    File file12 = getOutputMediaFile();
                    OutputStream outputStream = null;
                    try {
                        outputStream = new FileOutputStream(file12);
                        outputStream.write(bytes);

                        Intent intent = new Intent();
                        intent.putExtra("url", String.valueOf(Uri.fromFile(file12)));
                        setResult(Activity.RESULT_OK, intent);
                        finish();//finishing activity
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (outputStream != null)
                                outputStream.close();
                        } catch (Exception e) {
                        }
                    }
                }

                 */

                void save1(byte[] bytes, int angle)
                {

                    //Log.d(TAG, "save1: ");
                    try {
                       // Log.d(TAG, "save2: ");
                        Uri image = saveImage(bytes, 0);
                        Intent intent = new Intent();
                        intent.putExtra("url", String.valueOf(image));
                        setResult(Activity.RESULT_OK, intent);
                        finish();//finishing activity
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                       // Log.d(TAG, "save3: ");
                    }
                }
            };
            HandlerThread handlerThread = new HandlerThread("takepicture");
            handlerThread.start();
            final Handler handler = new Handler(handlerThread.getLooper());
            reader.setOnImageAvailableListener(imageAvailableListener, handler);
            final CameraCaptureSession.CaptureCallback previewSSession = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureStarted(CameraCaptureSession session, CaptureRequest request, long timestamp, long frameNumber) {
                    super.onCaptureStarted(session, request, timestamp, frameNumber);
                }

                @Override
                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    startCamera();
                }
            };


            cameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {

                    try {
                        session.capture(capturebuilder.build(), previewSSession, handler);
                    } catch (Exception e) {
                    }
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                }
            }, handler);
        } catch (Exception e) {
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void openCamera()
    {
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            String camerId = manager.getCameraIdList()[0];
             characteristics= manager.getCameraCharacteristics(camerId);


                    StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];
            previewsize = map.getOutputSizes(SurfaceTexture.class)[0];

//            params.setPreviewSize(imageDimension.getWidth(), imageDimension.getHeight());
            if ((ContextCompat.checkSelfPermission(CAM2.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(CAM2.this, new String[]{Manifest.permission.CAMERA}, 1);
                //  ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,}, 0);

                return;
            }

            manager.openCamera(camerId, stateCallback, null);


        } catch (Exception e) {
        }
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void onPause() {
        super.onPause();
        if (cameraDevice != null) {
            cameraDevice.close();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void startCamera() {
        if (cameraDevice == null || !textureView.isAvailable() || previewsize == null) {
            return;
        }
        SurfaceTexture texture = textureView.getSurfaceTexture();
        if (texture == null) {
            return;
        }
        texture.setDefaultBufferSize(previewsize.getWidth(), previewsize.getHeight());
        Surface surface = new Surface(texture);
        try {
            previewBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

        } catch (Exception e) {
        }
        previewBuilder.addTarget(surface);

        try {
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    previewSession = session;
                    getChangedPreview();
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                }
            }, null);
        } catch (Exception e) {
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void getChangedPreview() {
        if (cameraDevice == null) {
            return;
        }
        previewBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        HandlerThread thread = new HandlerThread("changed Preview");
        thread.start();
        Handler handler = new Handler(thread.getLooper());
        try {
            previewSession.setRepeatingRequest(previewBuilder.build(), null, handler);
        } catch (Exception e) {
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:


                finish();


                break;
            default:
                break;

        }

        return true;
    }


}



