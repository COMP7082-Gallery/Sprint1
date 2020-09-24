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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sprintone.Navigation.GalleryTraversal;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;


//TODO: Link caption to image

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int FILTER_ACTIVITY_REQUEST_CODE = 2;

    private ImageView imageView;
    private TextView timeStamp;
    protected EditText editCaption;
    private TextView captionText;
    private LinearLayout captionArea;
    private GalleryTraversal traversal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.image_view);
        timeStamp = (TextView) findViewById(R.id.dateTime);
        captionArea = (LinearLayout) findViewById(R.id.captionArea);
        captionText = (TextView) findViewById(R.id.captionText);
        editCaption = (EditText) findViewById(R.id.editCaptionView);

        hideEditCaption(captionArea);
        ArrayList<String> files = getPhotoPathsFromDir(new Date(Long.MIN_VALUE), new Date(), "");
        if (files != null && files.size() > 0) {
            traversal = new GalleryTraversal(files);
            //ArrayList<String> paths = traversal.getPhotoPaths();
            //update to most recent photo
            updateCurrentPhoto(traversal.getPhotoPaths().size() - 1);
        }
        else{
            setPic(null);
        }
    }

    /* Initially hides edit text box */
    public void hideEditCaption(View view){
        captionArea.setVisibility(View.GONE);
    }

    /* Edit caption for existing photo */
    public void editCaption(View view){
        //Log.d("String", "String" + captionText.getText().toString());
        editCaption.setText(captionText.getText().toString());
        captionText.setVisibility(View.GONE);
        captionArea.setVisibility(View.VISIBLE);
    }
    public void saveCaption(View view){
        captionText.setText(editCaption.getText().toString());
        String[] attr = traversal.getCurrentPhotoPath().split("_");

        if (attr.length >= 3 && editCaption.getText().toString() != "") {
            File newName = new File(attr[0] + "_" + editCaption.getText().toString() + "_" + attr[2] + "_" + attr[3] + "_" + attr[4]);
            Log.d("Files", "NewFileName:" + newName.getAbsolutePath());
            File oldName = new File(traversal.getCurrentPhotoPath());
            Log.d("Files", "OldFileName:" + traversal.getCurrentPhotoPath());
            oldName.renameTo(newName);
            traversal.setCurrentPhotoPaths(newName.getAbsolutePath());
        }
        hideEditCaption(captionArea);
        captionText.setVisibility(View.VISIBLE);
    }

    //
    // Update current photo
    // Update date time of the current photo
    //
    private void updateCurrentPhoto(int pointer) {
        //moves the pointer the the updated location and sets the picture
        traversal.traverseGallery(pointer);
        //photoPointer = pointer;
        setPic(traversal.getCurrentPhotoPath());
    }

    //Handler for the filter function of the app
    //Directs to the Filter Activity
    public void startFilter(View view) {
        Intent intent = new Intent(this, FilterGalleryActivity.class);
        startActivityForResult(intent, FILTER_ACTIVITY_REQUEST_CODE);

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
        String imageFileName = "image_caption_" + timeStamp + "_";

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
    //creates a high-res image
    //creates the image, time stamp and caption based on the traversal.getCurrentPhotoPath()
    //
    private void setPic(String path) {
        // Get the dimensions of the View
        //int targetW = imageView.getWidth();
        //int targetH = imageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        //int photoW = bmOptions.outWidth;
        //int photoH = bmOptions.outHeight;

        //divide by zero problem when loading the most recent photo
        //oncreate
        //int scaleFactor = Math.max(1, Math.min(photoW / targetW, photoH / targetH));

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        //bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        if (path == null || path == "") {
            imageView.setImageResource(R.mipmap.ic_launcher);
            timeStamp.setText("");
            captionText.setText("No Picture Found");
        } else {
            Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);
            imageView.setImageBitmap(bitmap);
            String[] attr = path.split("_");
            timeStamp.setText(attr[2]+" "+attr[3]);
            captionText.setText(attr[1]);
        }
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

        if (requestCode == FILTER_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                DateFormat format = new SimpleDateFormat("yyyy‐MM‐dd HH:mm:ss");
                Date startTimestamp , endTimestamp;
                try {
                    String from = (String) data.getStringExtra("STARTTIMESTAMP");
                    String to = (String) data.getStringExtra("ENDTIMESTAMP");
                    startTimestamp = format.parse(from);
                    endTimestamp = format.parse(to);
                } catch (Exception ex) {
                    startTimestamp = null;
                    endTimestamp = null;
                }
                String keywords = (String) data.getStringExtra("KEYWORDS");
                traversal.setPhotoPaths(getPhotoPathsFromDir(startTimestamp, endTimestamp, keywords));
                //Log.d("Photo Path", "Photo Path:" + traversal.getPhotoPaths());
                if (traversal.getPhotoPaths().size() == 0) {
                    Log.d("Set null", "No Picture Found");
                    setPic(null);
                } else {
                    updateCurrentPhoto(traversal.getPhotoPaths().size() - 1);
                    Log.d("Set pic", "Picture Found");
                }
            }
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            ArrayList<String> files = getPhotoPathsFromDir(new Date(Long.MIN_VALUE), new Date(), "");
            if (files != null && files.size() > 0) {
                traversal = new GalleryTraversal(files);
                //ArrayList<String> paths = traversal.getPhotoPaths();
                //update to most recent photo
                updateCurrentPhoto(traversal.getPhotoPaths().size() - 1);
            }
            else{
                setPic(null);
            }
        }
    }

    //
    //Returns all the the photo file paths as a String array
    //
    private ArrayList<String> getPhotoPathsFromDir(Date startTimestamp, Date endTimestamp, String keywords) {
        String dir_path = getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString();
        Log.d("Path", "Whats the path: " + getExternalFilesDir(Environment.DIRECTORY_PICTURES));

        File directory = new File(dir_path);
        File[] files = directory.listFiles();
        ArrayList<String> paths = new ArrayList<>();
        //Log.d("Files", "FileLength:" + files);
        if (files != null && files.length > 1) {
            Arrays.sort(files, new Comparator<File>() {
                public int compare(File o1, File o2) {
                    long lastModifiedO1 = o1.lastModified();
                    long lastModifiedO2 = o2.lastModified();

                    return (lastModifiedO2 > lastModifiedO1) ? -1 : ((lastModifiedO1 < lastModifiedO2) ? 1 : 0);
                }
            });
        }

        for (File file : files) {
            if (((startTimestamp == null && endTimestamp == null) || (file.lastModified() >= startTimestamp.getTime() && file.lastModified() <= endTimestamp.getTime())) && (keywords == "" || file.getPath().contains(keywords))) {
                Log.d("Files", "FileName:" + file.getAbsolutePath() + " modified on " + file.lastModified());
                paths.add(file.getAbsolutePath());
            }
        }

        Log.d("Paths", "PathLength:" + paths.size());
        return paths;
    }

    //
    //Allows users to traverse through their gallery of images
    //The traversal.getCurrentPhotoPath() is assigned with the current image to view
    //The photoPointer is updated to the index of the traversal.getCurrentPhotoPath()
    //
    public void traverseGallery(View view) {
        if (view.getId() == R.id.prev_btn) {
            hideEditCaption(captionArea);
            captionText.setVisibility(View.VISIBLE);
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
            hideEditCaption(captionArea);
            captionText.setVisibility(View.VISIBLE);
            if (traversal.getPhotoPointer() < traversal.getPhotoPaths().size() - 1) {
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