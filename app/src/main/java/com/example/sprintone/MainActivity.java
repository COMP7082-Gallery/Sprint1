package com.example.sprintone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private ImageView imageView;
    private String currentPhotoPath = null;
    private String[] photoPaths = null;
    private int photoPointer = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.image_view);
        photoPaths = getPhotoPaths();
        if (photoPaths.length > 0) {
            updateCurrentPhoto(photoPaths.length - 1);
        }
    }


    private void updateCurrentPhoto(int pointer) {
        if (pointer > 0 && pointer < photoPaths.length)
        {
            photoPointer = pointer;
            currentPhotoPath = photoPaths[pointer];
            setPic();
        }
    }

    //
    //Handler for the camera function of the app
    //Delegates to the android camera app
    //
    public void dispatchTakePictureIntent(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            File imageFile = null;
            try {
                imageFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (imageFile != null) {
                Uri imageUri = FileProvider.getUriForFile(this, "com.example.sprintone.android.fileprovider", imageFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    //
    //Creates a temporary file for the new image
    //
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        //file directory must match file_path.xml
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        Log.d("d", "createImageFile: " + storageDir + " + " + imageFileName);

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    //
    //creates a high-res image
    //creates the images based on the currentPhotoPath
    //
    private void setPic() {
        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(currentPhotoPath, bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        //divide by zero problem when loading the most recent photo
        //oncreate
        //int scaleFactor = Math.max(1, Math.min(photoW / targetW, photoH / targetH));

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        //bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        imageView.setImageBitmap(bitmap);
    }

    //
    //Runs when the android camera app finishes
    //Updates the gallery with the existing photo's, and the image that was just captured
    //Sets the currentPhotoPath to the image just captured
    //Sets the photoPointer to the end of the gallery
    //
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK && currentPhotoPath != null) {
            photoPaths = getPhotoPaths();
            updateCurrentPhoto(photoPaths.length - 1);
        }

    }

    //
    //Returns all the the photo file paths as a String array
    //
    private String [] getPhotoPaths() {
        String dir_path = getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString();
        Log.d("Path", "Whats the path: " + getExternalFilesDir(Environment.DIRECTORY_PICTURES));

        File directory = new File(dir_path);
        File[] files = directory.listFiles();
        String [] paths = new String[files.length];

        for (int i = 0; i < files.length; i++) {
            Log.d("Files", "FileName:" + files[i].getAbsolutePath());
            paths[i] = files[i].getAbsolutePath();
        }
        return paths;
    }

    //
    //Allows users to traverse through their gallery of images
    //The currentPhotoPath is assigned with the current image to view
    //The photoPointer is updated to the index of the currentPhotoPath
    //
    public void traverseGallery(View view) {
        if (view.getId() == R.id.prev_btn && photoPointer > 0) {
            updateCurrentPhoto(--photoPointer);
            //currentPhotoPath = updatePhotoPath(--photoPointer);
            Log.d("Traversal", "Pointer at: " + photoPointer);
        }
        else if (view.getId() == R.id.next_btn && photoPointer < photoPaths.length - 1) {
            updateCurrentPhoto(++photoPointer);
            //currentPhotoPath = updatePhotoPath(++photoPointer);
            Log.d("Traversal", "Pointer at: " + photoPointer);
        }
        setPic();
    }

}