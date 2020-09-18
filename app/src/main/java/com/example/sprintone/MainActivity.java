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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sprintone.Navigation.GalleryTraversal;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;



//TODO: Link caption to image

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private ImageView imageView;
    //private TextView caption;
    //private TextView caption;
    protected EditText editText;
    private GalleryTraversal traversal;
    private Date currentPhotoDate = null;
    private Date[] photoDates = null;
    private int photoPointer;
    //private LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.image_view);
        editText = (EditText) findViewById(R.id.editCaptionView);

        hideEditCaption(editText);
        traversal = new GalleryTraversal(getPhotoPathsFromDir());
        if (traversal.getPhotoPaths().length > 0) {
            //update to most recent photo
            updateCurrentPhoto(traversal.getPhotoPaths().length - 1);
        }
    }

    /* Initially hides edit text box */
    public void hideEditCaption(View view){
        editText.setVisibility(View.GONE);
    }

    /* Edit caption for existing photo */
    public void editCaption(View view){
        //caption = (TextView) findViewById(R.id.textView2);
        TextView captionText = (TextView) findViewById(R.id.textView2);
        editText = (EditText) findViewById(R.id.editCaptionView);

        captionText.setText("Hello");

        captionText.setVisibility(View.GONE);
        editText.setVisibility(View.VISIBLE);
        editText.setText("Hello");
    }

    //
    // Update current photo
    // Update date time of the current photo
    //
    private void updateCurrentPhoto(int pointer) {
        //moves the pointer the the updated location and sets the picture
        traversal.traverseGallery(pointer);
        photoPointer = pointer;
        setDate(photoPointer);
        setPic();
    }

    //Handler for the filter function of the app
    //Directs to the Filter Activity
    public void startFilter(View v) {
        Intent intent = new Intent(this, FilterGalleryActivity.class);
        startActivity(intent);
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
        //traversal.getCurrentPhotoPath() = image.getAbsolutePath();
        return image;
    }
    //
    // Set the textView1 with the date of image in yyyy-MM-dd hh:mm:ss format.
    //
    private void setDate(int pointer) {
        currentPhotoDate = photoDates[pointer];
        TextView dateText = (TextView)findViewById(R.id.dateTime);
        dateText.setText(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(currentPhotoDate));
    }
    //
    //creates a high-res image
    //creates the images based on the traversal.getCurrentPhotoPath()
    //
    private void setPic() {
        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(traversal.getCurrentPhotoPath(), bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        //divide by zero problem when loading the most recent photo
        //oncreate
        //int scaleFactor = Math.max(1, Math.min(photoW / targetW, photoH / targetH));

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        //bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(traversal.getCurrentPhotoPath(), bmOptions);
        imageView.setImageBitmap(bitmap);
    }

    //
    //Runs when the android camera app finishes
    //Updates the gallery with the existing photo's, and the image that was just captured
    //Sets the traversal.getCurrentPhotoPath() to the image just captured
    //Sets the photoPointer to the end of the gallery
    //
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK && traversal.getCurrentPhotoPath() != null) {
            traversal.setPhotoPaths(getPhotoPathsFromDir());
            updateCurrentPhoto(traversal.getPhotoPaths().length - 1);
        }

    }

    //
    //Returns all the the photo file paths as a String array
    //
    private String [] getPhotoPathsFromDir() {
        String dir_path = getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString();
        Log.d("Path", "Whats the path: " + getExternalFilesDir(Environment.DIRECTORY_PICTURES));

        File directory = new File(dir_path);
        File[] files = directory.listFiles();
        String [] paths = new String[files.length];
        if (files.length > 0) {
            photoDates = getPhotoDates(files);
        }else {
            Toast.makeText(MainActivity.this, "Images not found",
                    Toast.LENGTH_SHORT).show();
        }
        for (int i = 0; i < files.length; i++) {
            Log.d("Files", "FileName:" + files[i].getAbsolutePath());
            paths[i] = files[i].getAbsolutePath();
        }
        return paths;
    }
    //
    //Returns all the the photo's last modified date as a Date array
    //
    private Date [] getPhotoDates(File[] files){
        Date [] dates = new Date[files.length];
        for (int i = 0; i < files.length; i++) {
            Log.d("Dates", "Dates:" + files[i].lastModified());
            dates[i] = new Date(files[i].lastModified());
        }
        return dates;
    }
    //
    //Allows users to traverse through their gallery of images
    //The traversal.getCurrentPhotoPath() is assigned with the current image to view
    //The photoPointer is updated to the index of the traversal.getCurrentPhotoPath()
    //
    public void traverseGallery(View view) {
        if (view.getId() == R.id.prev_btn) {
            if (traversal.getPhotoPointer() > 0) {
            updateCurrentPhoto(traversal.getPhotoPointer() - 1);
            //traversal.getCurrentPhotoPath() = updatePhotoPath(--photoPointer);
            Log.d("Traversal", "Pointer at: " + traversal.getPhotoPointer());
            }
            else {
                Toast.makeText(MainActivity.this, "First image",
                        Toast.LENGTH_SHORT).show();
            }
        }
        else if (view.getId() == R.id.next_btn) {
            if (traversal.getPhotoPointer() < traversal.getPhotoPaths().length - 1) {
                updateCurrentPhoto(traversal.getPhotoPointer() + 1);
                //traversal.getCurrentPhotoPath() = updatePhotoPath(++photoPointer);
                Log.d("Traversal", "Pointer at: " + traversal.getPhotoPointer());
            }
            else {
                Toast.makeText(MainActivity.this, "Last image",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

}