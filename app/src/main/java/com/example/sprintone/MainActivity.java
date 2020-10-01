package com.example.sprintone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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

import com.example.sprintone.Gallery.GallerySingleton;
import com.example.sprintone.Navigation.GalleryTraversal;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int FILTER_ACTIVITY_REQUEST_CODE = 2;
    public static final int LOCATION_CODE = 301;

    private LocationManager locationManager;
    private String locationProvider = null;
    private ImageView imageView;
    private TextView timeStamp;
    private TextView coordinates;
    private EditText editCaption;
    private TextView captionText;
    private LinearLayout captionArea;
    private GalleryTraversal traversal;

    private GallerySingleton gallery = GallerySingleton.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.image_view);
        timeStamp = findViewById(R.id.dateTime);
        captionArea = findViewById(R.id.captionArea);
        captionText = findViewById(R.id.captionText);
        editCaption = findViewById(R.id.editCaptionView);
        coordinates = findViewById(R.id.coordinate);

        hideEditCaption(captionArea);


        ArrayList<String> files = getPhotoPathsFromDir(new Date(Long.MIN_VALUE), new Date(), "");
        if (files.size() > 0) {
            gallery.setGallery(files, files.size() - 1);
            traversal = new GalleryTraversal(files);
            updateCurrentPhoto(gallery.getGalleryPointer());
        } else {
            setPic(null);
        }
        askForPermission();
    }

    private void askForPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_CODE);
            Log.d("Ask permission", "true");
        }
    }

    private ArrayList<String> getLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        ArrayList<String> coordinates = new ArrayList<>();

        if (providers.contains(LocationManager.GPS_PROVIDER)) {
            locationProvider = LocationManager.GPS_PROVIDER;
        } else {
            locationProvider = LocationManager.NETWORK_PROVIDER;
        }

        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                || (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_CODE);
            Log.d("Ask permission", "true");
        } else {
            locationManager.requestLocationUpdates(locationProvider, 1000, 0, locationListener);
            Location location = locationManager.getLastKnownLocation(locationProvider);
            if (location != null) {
                coordinates.add(String.valueOf(location.getLatitude()));
                coordinates.add(String.valueOf(location.getLongitude()));
                Log.d("Coordinates: ", location.getLongitude() + " " + location.getLatitude() + "");
            }
            else{
                coordinates = new ArrayList<>(Arrays.asList("0.0", "0.0"));
            }
        }
        locationManager.removeUpdates(locationListener);
        return coordinates;
    }

    public LocationListener locationListener = new LocationListener() {
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                Log.d("Coordinates: listen", location.getLongitude() + " " + location.getLatitude() + "");
            }
        }
    };

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
        //traversal.traverseGallery(pointer);
        gallery.traverseGallery(pointer);
        //photoPointer = pointer;
        setPic(gallery.getPhotoPath());
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
                Log.e("imageFile", imageFile.getAbsolutePath());
                //setExif(imageUri);
            }
        }
    }

    //
    //Creates a temporary file for the new image
    //
    private File createImageFile() throws IOException {
        ArrayList<String> coordinate = getLocation();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        // Create an image file name
        String imageFileName = "image_caption_" + timeStamp + "_" + coordinate.get(0) + "_" + coordinate.get(1) + "_";

        //file directory must match file_path.xml
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        Log.d("d", "createImageFile: " + storageDir + " + " + imageFileName);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpeg",  /* suffix */
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
            coordinates.setText("");
            captionText.setText("No Picture Found");
        } else {
            Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);
            imageView.setImageBitmap(bitmap);
            String[] attr = path.split("_");

            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
            String dateTime = attr[2] + attr[3];
            Date date = null;
            try {
                date = format.parse(dateTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            SimpleDateFormat newFormat = new SimpleDateFormat("MMM d, yyyy h:mm a");
            String formatDateTime = newFormat.format(date);

            //------------------------------------------------------------------
            // This section can be removed once we implement our delete function
            //------------------------------------------------------------------
            if (attr.length < 6) {
                timeStamp.setText(formatDateTime);
                coordinates.setText("Location");
                captionText.setText(attr[1]);
            } else {
            //------------------------------------------------------------------
                timeStamp.setText(formatDateTime);
                coordinates.setText(attr[4] + ",  " + attr[5]);
                captionText.setText(attr[1]);
            }
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
                DateFormat format = new SimpleDateFormat("MMM d, yyyyHHmmss");
                Date startTimestamp , endTimestamp;
                try {
                    String from = data.getStringExtra("STARTTIMESTAMP") + "000000";
                    String to = data.getStringExtra("ENDTIMESTAMP") + "235959";
                    startTimestamp = format.parse(from);
                    endTimestamp = format.parse(to);
                } catch (Exception ex) {
                    startTimestamp = null;
                    endTimestamp = null;
                }
                String keywords = (String) data.getStringExtra("KEYWORDS");
                //traversal.setPhotoPaths(getPhotoPathsFromDir(startTimestamp, endTimestamp, keywords));
                gallery.setGallery(getPhotoPathsFromDir(startTimestamp, endTimestamp, keywords), gallery.getGalleryPointer());
                Log.d("Photo Path", "Photo Path:" + traversal.getPhotoPaths());
                if (traversal.getPhotoPaths().size() == 0) {
                    Log.d("Set null", "No Picture Found");
                    setPic(null);
                } else {
                    //updateCurrentPhoto(traversal.getPhotoPaths().size() - 1);
                    updateCurrentPhoto(gallery.getPhotoPaths().size() - 1);
                    Log.d("Set pic", "Picture Found");
                }
            }
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {

            //traversal.setPhotoPaths(getPhotoPathsFromDir(new Date(Long.MIN_VALUE), new Date(), ""));


            Log.d("Intent value: ", data.toString());
            ArrayList<String> files = getPhotoPathsFromDir(new Date(Long.MIN_VALUE), new Date(), "");
            if (files.size() > 0) {
                gallery.setGallery(getPhotoPathsFromDir(new Date(Long.MIN_VALUE), new Date(), ""), gallery.getGalleryPointer());
                updateCurrentPhoto(gallery.getPhotoPaths().size() - 1);
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

                    return lastModifiedO2 > lastModifiedO1 ? -1 : 0;
                }
            });
        }
        for (File file : files) {
            if (((startTimestamp == null && endTimestamp == null) || (file.lastModified() >= startTimestamp.getTime() && file.lastModified() <= endTimestamp.getTime()))
                    && (keywords == "" || file.getPath().contains(keywords))) {
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
            if (gallery.getGalleryPointer() > 0) {
                //updateCurrentPhoto(traversal.getPhotoPointer() - 1);
                updateCurrentPhoto(gallery.getGalleryPointer() - 1);
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
            if (gallery.getGalleryPointer() < gallery.getPhotoPaths().size() - 1) {
                //updateCurrentPhoto(traversal.getPhotoPointer() + 1);
                updateCurrentPhoto(gallery.getGalleryPointer() + 1);
                Log.d("Traversal", "Pointer at: " + traversal.getPhotoPointer());
            }
            else {
                Toast.makeText(MainActivity.this, "Last image",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    //deletes an image
    public void deletePhoto(View view) {
        Log.d("Delete", "In Delete");

        try {
            File file = new File(gallery.getPhotoPath());
            boolean deleted = file.delete();
            if (deleted) {
                gallery.removeFromGallery();
                updateCurrentPhoto(gallery.getGalleryPointer());
            }
            Log.d("Delete?", "Deleted: " + deleted);
        } catch (IndexOutOfBoundsException e) {
            Log.d("Delete out of bounds", "Tried to remove out of bounds");
        }
    }
}