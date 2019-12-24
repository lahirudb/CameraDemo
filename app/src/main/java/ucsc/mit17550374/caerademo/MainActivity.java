package ucsc.mit17550374.caerademo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
    private static  String TAG = "CameraTAg ";
    Button camera;
    ImageView image;

    Uri fileUri;

    MainActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        camera = findViewById(R.id.camera);
        image = findViewById(R.id.image);

        mainActivity = this;

        checkAccessPermissions();

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkPermissionForExternalStorage(mainActivity)) {
                    requestStoragePermission(mainActivity, 200);
                } else {

                    camera_call();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 200) {
            try {
                System.out.println("image uri - " + fileUri.getPath());

                Intent galleryIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                File f = new File(fileUri.getPath());

                f = compressImge(getApplicationContext(), f);
                Uri picUri = Uri.fromFile(f);
                galleryIntent.setData(picUri);
                this.sendBroadcast(galleryIntent);

                Toast.makeText(getApplicationContext(), "Image saved in folder called Shehani in device storage", Toast.LENGTH_LONG).show();

                try {
                    image.setImageURI(fileUri);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void camera_call() {
        fileUri = getOutputMediaFileUri(1);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // start the image capture Intent
//        if(isFragment) {
//            current_fragment.startActivityForResult(intent, 100);
//        } else {
            this.startActivityForResult(intent, 200);
//        }
    }

    public Uri getOutputMediaFileUri(int type) {
        if (Build.VERSION.SDK_INT > 23) {
            return FileProvider.getUriForFile(this,
                    BuildConfig.APPLICATION_ID + ".provider",
                    getOutputMediaFile(type));
        } else {
            return Uri.fromFile(getOutputMediaFile(type));
        }


    }

    private static File getOutputMediaFile(int type) {
        // External sdcard location
        //File mediaStorageDir = new File( Environment.getExternalStorageDirectory() + File.separator + Constants.DIRECTORY_NAME);
        File mediaStorageDir = new File( Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + File.separator + "Shehani");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {

                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyy_MM_dd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == 1) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        }
//        else if (type == Constants.MEDIA_TYPE_VIDEO) {
//            mediaFile = new File(mediaStorageDir.getPath() + File.separator
//                    + "VID_" + timeStamp + ".mp4");
//        }
        else {
            return null;
        }
        System.out.println("file pagth - " + mediaFile.getAbsolutePath());
        return mediaFile;
    }

    boolean checkPermissionForExternalStorage(Activity activity) {
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);

        int result2 = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);


        int result3 = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED && result3 == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    boolean requestStoragePermission(Activity activity, int CAMRA_PERMISSION) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity.requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMRA_PERMISSION);
            }
        } else {
        }
        return false;
    }
    boolean requestStoragePermissionWrite(Activity activity, int CAMRA_PERMISSION) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity.requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMRA_PERMISSION);
            }
        } else {
        }
        return false;
    }

    boolean requestStoragePermissionRead(Activity activity, int READ_STORAGE_PERMISSION) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_STORAGE_PERMISSION);
            }
        } else {
        }
        return false;
    }

    public boolean checkAccessPermissions() {
        try {
            if (android.os.Build.VERSION.SDK_INT >= 23) {
                return requestPermissionForAll(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;

    }

    public boolean requestPermissionForAll(MainActivity activity) {

        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET, Manifest.permission.RECORD_AUDIO, Manifest.permission.MODIFY_AUDIO_SETTINGS};

        if (!hasPermissions(this, PERMISSIONS)) {

            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
            return true;
        }else{
            return false;
        }

    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= 23 && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    private File compressImge(Context context, File imageFile){
        File mSaveBit; // Your image file
        OutputStream os = null;
        try {
            try {
                Log.d(TAG, "compressImge original : " + imageFile.getName() + ": " + (imageFile.length()/1024) + "kb");
                if ((imageFile.length()/1024) < 1024){
                    //image already in low quality
                    return  imageFile;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            String filePath = imageFile.getPath();
            Uri fileUri = Uri.fromFile(imageFile);
            System.out.println("tempImages.ge - " + fileUri.getPath());
            System.out.println("tempImages.ge - " + Uri.decode(fileUri.toString()));
            System.out.println("tempImages.ge - " + filePath);
            Bitmap bitmap = null;

            System.out.println("crashhhh : 3" );
            try {
                bitmap = BitmapFactory.decodeFile(filePath);
            } catch (Exception e) {
                e.printStackTrace();
            }


            ExifInterface oldExif = null;
            String exifOrientation = null;

            System.out.println("crashhhh : 4" );
            try {
                // oldExif = new ExifInterface(imageFile.getPath());
                //exifOrientation = oldExif.getAttribute(ExifInterface.TAG_ORIENTATION);
            } catch (Exception e) {
                e.printStackTrace();
            }

            String imageName = imageFile.getName();
            imageName = imageName.replaceAll(" ", "");
            mSaveBit = new File(context.getCacheDir(), imageFile.getName());
            os = new BufferedOutputStream(new FileOutputStream(mSaveBit));

            System.out.println("crashhhh : 5.3 - "  + bitmap.getWidth());
            System.out.println("crashhhh : 5.3 - "  + bitmap.getHeight());

            if (bitmap.getHeight()> 4000) {
                DisplayMetrics displayMetrics = new DisplayMetrics();
                this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int height = displayMetrics.heightPixels;
                int width = displayMetrics.widthPixels;
                System.out.println("display metrics - " + height + " - " + width);
                bitmap = resize(bitmap, width, height);
            }

            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, os);
            if (exifOrientation != null) {
                System.out.println("crashhhh : 5.5" );
                ExifInterface newExif = new ExifInterface(mSaveBit.getPath());
                newExif.setAttribute(ExifInterface.TAG_ORIENTATION, exifOrientation);
                newExif.saveAttributes();
            }
            os.close();
            try {
                Log.d(TAG, "compressImge compress : " + mSaveBit.getName() + ":" + (mSaveBit.length()/1024) + "kb");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mSaveBit;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "compressImge: error" + e);
        } finally {
            try {
                if(os!=null){
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        Log.d(TAG, "compressImge: return original");
        return imageFile;
    }


    private Bitmap resize(Bitmap image, int maxWidth, int maxHeight) {
        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > ratioBitmap) {
                finalWidth = (int) ((float)maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float)maxWidth / ratioBitmap);
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            return image;
        } else {
            return image;
        }
    }

}
