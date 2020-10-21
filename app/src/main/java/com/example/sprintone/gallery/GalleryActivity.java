package com.example.sprintone.gallery;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.example.sprintone.facade.Actions;
import com.example.sprintone.filter_gallery.FilterGalleryActivity;
import com.example.sprintone.R;
import com.example.sprintone.traversal.GallerySingleton;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static java.lang.Double.parseDouble;
import static java.lang.Float.parseFloat;
import static java.lang.Math.toIntExact;

public class GalleryActivity extends AppCompatActivity implements GalleryActivityMVP.View, Actions {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int FILTER_ACTIVITY_REQUEST_CODE = 2;
    public static final int LOCATION_CODE = 301;

    private String captured_image_path;

    private ImageView imageView;
    private TextView timeStamp;
    private TextView coordinates;
    private EditText editCaption;
    private TextView captionText;
    private LinearLayout captionArea;
    private Photographer photographer = new Photographer();

    private GallerySingleton gallery = GallerySingleton.getInstance();
    private GalleryActivityPresenter presenter;

    GalleryActivity that = this;

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

        presenter = new GalleryActivityPresenter(null);

        hideEditCaption(captionArea);

        ArrayList<String> files = getPhotoPathsFromDir(new Date(Long.MIN_VALUE), new Date(), "", "", "", "", "", "");
        if (files.size() > 0) {
            gallery.setGallery(files, files.size() - 1);
            updateCurrentPhoto(gallery.getGalleryPointer());
        } else {
            setPic(null);
        }
        askForPermission();
    }

    //
    // Ask permission from user for both GPS and Network access.
    //
    private void askForPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_CODE);
            Log.d("Ask permission", "true");
        }
    }

    //
    // Get the location from GPS or Network provider.
    //
    private ArrayList<String> getLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        ArrayList<String> coordinates = new ArrayList<>();
        String locationProvider;
        // Use GPS as the first choice, if not, use network data
        if (providers.contains(LocationManager.GPS_PROVIDER)) {
            locationProvider = LocationManager.GPS_PROVIDER;
        } else {
            locationProvider = LocationManager.NETWORK_PROVIDER;
        }

        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                || (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            askForPermission();
            Log.d("Ask permission", "true");
        }
        LocationListener locationListener = new LocationListener() {
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
        Location location = locationManager.getLastKnownLocation(locationProvider);
        locationManager.requestLocationUpdates(locationProvider, 1000, 0, locationListener);
        while(location == null){
            location = locationManager.getLastKnownLocation(locationProvider);
        }
        coordinates.add(String.valueOf(location.getLatitude()));
        coordinates.add(String.valueOf(location.getLongitude()));
        Log.d("Coordinates: ", location.getLongitude() + " " + location.getLatitude());
        locationManager.removeUpdates(locationListener);
        return coordinates;
    }


    /* Initially hides edit text box */
    public void hideEditCaption(View view){
        captionArea.setVisibility(View.GONE);
    }



    //PRESENTER
    //BC DATABASE


    //
    // Update current photo
    // Update date time of the current photo
    //
    private void updateCurrentPhoto(int pointer) {
        //moves the pointer the the updated location and sets the picture
        gallery.traverseGallery(pointer);
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
                ".jpg",  /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        return image;
    }

    //
    //creates a high-res image
    //creates the image, time stamp and caption based on the
    //
    private void setPic(String path) {
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        //bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        if (path == null || Objects.equals(path, "")) {
            imageView.setImageResource(R.mipmap.ic_launcher);
            timeStamp.setText("");
            coordinates.setText("");
            captionText.setText("No Picture Found");
        } else {
            Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);
            imageView.setImageBitmap(bitmap);
            Log.d("setPic", path);
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
            timeStamp.setText(formatDateTime);
            coordinates.setText(locationFormat(attr[4], attr[5]));
            captionText.setText(attr[1]);
        }
    }

    //
    // Add city name to location tag before the coordinates.
    // If cannot get city, return only coordinates.
    //
    public String locationFormat (String lat, String lon) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> address = geocoder.getFromLocation(parseDouble(lat), parseDouble(lon), 1);
            String city = address.get(0).getLocality();
            return city + " " + lat + ", " + lon;
        } catch (IOException e){
            e.printStackTrace();
            return lat + ", " + lon;
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

                String tlLat = data.getStringExtra("TOPLEFTLATITUDE");
                String tlLon = data.getStringExtra("TOPLEFTLONGITUDE");
                String brLat = data.getStringExtra("BTMRIGHTLATITUDE");
                String brLon = data.getStringExtra("BTMRIGHTLONGITUDE");
                String keywords = data.getStringExtra("KEYWORDS");
                String shape = locationFilter(tlLat, tlLon, brLat, brLon);

                if (shape.equals("invalid")){
                    gallery.setGallery(getPhotoPathsFromDir(startTimestamp, endTimestamp, keywords, "", "","", "",""), gallery.getGalleryPointer());
                    Toast.makeText(com.example.sprintone.gallery.GalleryActivity.this, "Invalid Coordinates", Toast.LENGTH_SHORT).show();
                } else {
                    gallery.setGallery(getPhotoPathsFromDir(startTimestamp, endTimestamp, keywords, tlLat, tlLon, brLat, brLon, shape), gallery.getGalleryPointer());
                }
                Log.d("Photo Path", "Photo Path:" + gallery.getPhotoPaths());
                if (gallery.getPhotoPaths().size() == 0) {
                    Log.d("Set null", "No Picture Found");
                    setPic(null);
                } else {
                    //updateCurrentPhoto(traversal.getPhotoPaths().size() - 1);
                    updateCurrentPhoto(gallery.getPhotoPaths().size() - 1);
                    Log.d("Set pic", "Picture Found");
                }
            }
        }
        //
        //adding a photo to gallery/db
        //
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK && captured_image_path != null) {
            presenter.addPhoto(gallery, captured_image_path);

            if (gallery.getPhotoPaths().size() > 0)
                updateCurrentPhoto(gallery.getPhotoPaths().size() - 1);
            else
                setPic(null);
            captured_image_path = null;
        }
        else
        {
            Log.e("Error", "Error adding photo to the gallery");
        }
    }

    //
    //Returns all the the photo file paths as a String array
    //
    //will access db
    //PRESENTER
    private ArrayList<String> getPhotoPathsFromDir(Date startTimestamp, Date endTimestamp, String keywords, String tlLat, String tlLon, String brLat, String brLon, String shape) {
        String dir_path = getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString();
        Log.d("Path", "Whats the path: " + getExternalFilesDir(Environment.DIRECTORY_PICTURES));
        File directory = new File(dir_path);
        File[] files = directory.listFiles();
        ArrayList<String> paths = new ArrayList<>();

        //Use a lambda expression to sort the images by last modified date
        if (files != null && files.length > 1) {
            Arrays.sort(files, (o1, o2) -> {
                long lastModifiedO1 = o1.lastModified();
                long lastModifiedO2 = o2.lastModified();
                return lastModifiedO2 > lastModifiedO1 ? -1 : 0;
            });
        }
        if (files != null) {
            for (File file : files) {
                if (((startTimestamp == null && endTimestamp == null)
                        || (file.lastModified() >= startTimestamp.getTime() && file.lastModified() <= endTimestamp.getTime()))
                        && (keywords.equals("") || file.getPath().contains(keywords))) {
                    if (!shape.equals("none") && !shape.equals("")) {
                        String[] attr = file.getPath().split("_");
                        Log.d("image search loop:", parseFloat(tlLat) + " and " + parseFloat(brLat) + " and " + parseFloat(tlLon) + " and " + parseFloat(brLon) + " and " + attr[4] + " and " +  attr[5] + " and " + shape);
                        if ((shape.equals("x1") && attr[4].equals(tlLat)) || (shape.equals("y1") && attr[5].equals(tlLon))
                                || (shape.equals("x2") && attr[4].equals(brLat)) || (shape.equals("y2") && attr[5].equals(brLon))
                                || (shape.equals("x1y1") && attr[4].equals(tlLat) && attr[5].equals(tlLon))
                                || (shape.equals("x1x2") && (parseFloat(attr[4]) <= parseFloat(tlLat)) && (parseFloat(attr[4]) >= parseFloat(brLat)))
                                || (shape.equals("x1y2") && attr[4].equals(tlLat) && attr[5].equals(brLon))
                                || (shape.equals("x2y1") && attr[4].equals(brLat) && attr[5].equals(tlLon))
                                || (shape.equals("x2y2") && attr[4].equals(brLat) && attr[5].equals(brLon))
                                || (shape.equals("y1y2") && (parseFloat(attr[5]) <= parseFloat(brLon)) && (parseFloat(attr[5]) >= parseFloat(tlLon)))
                                || (shape.equals("x1x2y1") && attr[5].equals(tlLon) && (parseFloat(attr[4]) <= parseFloat(tlLat)) && (parseFloat(attr[4]) >= parseFloat(brLat)))
                                || (shape.equals("x1y1y2") && attr[4].equals(tlLat) && (parseFloat(attr[5]) <= parseFloat(brLon)) && (parseFloat(attr[5]) >= parseFloat(tlLon)))
                                || (shape.equals("x2y1y2") && attr[4].equals(brLat) && (parseFloat(attr[5]) <= parseFloat(brLon)) && (parseFloat(attr[5]) >= parseFloat(tlLon)))
                                || (shape.equals("x1x2y2") && attr[5].equals(brLon) && (parseFloat(attr[4]) <= parseFloat(tlLat)) && (parseFloat(attr[4]) >= parseFloat(brLat)))
                                || (shape.equals("x1x2y1y2") && (parseFloat(attr[4]) <= parseFloat(tlLat)) && (parseFloat(attr[4]) >= parseFloat(brLat))
                                && (parseFloat(attr[5]) <= parseFloat(brLon)) && (parseFloat(attr[5]) >= parseFloat(tlLon)))) {
                            paths.add(file.getAbsolutePath());
                        }
                    } else {
                        paths.add(file.getAbsolutePath());
                        Log.d("Files", "FileName:" + file.getAbsolutePath() + " modified on " + file.lastModified());
                    }
                }
            }
        }
        Log.d("Paths", "PathLength:" + paths.size());
        return paths;
    }

    //
    //Filter the coordinates entered by user.
    //Calculate the number of values
    //Based on different cases, assign different values to shape(a point, line, or surface).
    //
    public String locationFilter(String x1, String y1, String x2, String y2) {
        //Use lambda expression with Stream API to count the empty strings.
        List<String> coordinates = Arrays.asList(x1, y1, x2, y2);
        int count = toIntExact(coordinates.stream().filter(String::isEmpty).count());
        String shape = "invalid";
        Log.d("Count", String.valueOf(count));
        switch (count) {
            case 4:
                shape = "none";
                break;
            case 3:
                if (!x1.equals("")) {
                    shape = "x1";
                } else if  (!x2.equals("")) {
                    shape = "x2";
                } else if  (!y1.equals("")) {
                    shape = "y1";
                } else if  (!y2.equals("")) {
                    shape = "y2";
                }
                break;
            case 2:
                if (!x1.equals("") && !y1.equals("")) {
                    shape = "x1y1";
                } else if (!x1.equals("") && !y2.equals("")) {
                    shape = "x1y2";
                } else if (!x2.equals("") && !y2.equals("")) {
                    shape = "x2y2";
                } else if (!x2.equals("") && !y1.equals("")) {
                    shape = "x2y1";
                } else if (!x1.equals("") && !x2.equals("") && (parseFloat(x1) >= parseFloat(x2))) {
                    shape = "x1x2";
                }else if (!y1.equals("") && !y2.equals("") && (parseFloat(y2) >= parseFloat(y1))) {
                    shape = "y1y2";
                }
                break;
            case 1:
                if (x1.equals("") && (parseFloat(y2) >= parseFloat(y1))) {
                    shape = "x2y1y2";
                } else if (y1.equals("") && (parseFloat(x1) >= parseFloat(x2))) {
                    shape = "x1x2y2";
                } else if (x2.equals("") && (parseFloat(y2) >= parseFloat(y1))) {
                    shape = "x1y1y2";
                } else if (y2.equals("") && (parseFloat(x1) >= parseFloat(x2))) {
                    shape = "x1x2y1";
                }
                break;
            case 0:
                if(!x1.equals("") && !x2.equals("") && !y1.equals("") && !y2.equals("")
                        && (parseFloat(x1) >= parseFloat(x2)) && (parseFloat(y2) >= parseFloat(y1))){
                    shape = "x1x2y1y2";
                }
                break;
            default:
                shape = "invalid";
                break;
        }
        Log.d("Shape", shape);
        return shape;
    }

    @Override
    public void saveCaption(View view) {
        photographer.saveCaption(view);
    }

    @Override
    public void dispatchTakePictureIntent(View view) {
        photographer.dispatchTakePictureIntent(view);
    }


    @Override
    public void traverseGallery(View view) {
        photographer.traverseGallery(view);
    }

    @Override
    public void deletePhoto(View view) {
        photographer.deletePhoto(view);
    }

    @Override
    public void sharePhoto(View view) {
        photographer.sharePhoto(view);
    }

    @Override
    public void editCaption(View view) {
        photographer.editCaption(view);
    }

    public class Photographer implements Actions {
        public void editCaption(View view){
            editCaption.setText(captionText.getText().toString());
            captionText.setVisibility(View.GONE);
            captionArea.setVisibility(View.VISIBLE);
        }

        public void sharePhoto (View view)
        {
            Intent send  = new Intent();
            File file    = new File (gallery.getPhotoPath());
            Uri imageUri = FileProvider.getUriForFile(that, "com.example.sprintone.android.fileprovider", file);

            send.setAction(Intent.ACTION_SEND);
            send.putExtra(Intent.EXTRA_TITLE, "COMP 7082");
            send.putExtra(Intent.EXTRA_SUBJECT, "Picture to Send");
            send.putExtra(Intent.EXTRA_STREAM, imageUri);
            send.setType("image/*");

            if (file != null) Log.d ( "Pass", "File Exists" );
            else Log.d ("File Error", "File does not exist");

            startActivity(Intent.createChooser(send, null));
        }

        public void deletePhoto(View view) {
            if (presenter.deletePhoto(gallery) > -1)
            {
                updateCurrentPhoto(gallery.getGalleryPointer());
            }
            else
            {
                Log.d("d", "There was a problem deleting the image");
            }
        }

        public void saveCaption(View view) {
            captionText.setText(editCaption.getText().toString());
            presenter.saveCaption(gallery, editCaption.getText().toString());

            hideEditCaption(captionArea);
            captionText.setVisibility(View.VISIBLE);
        }

        public void traverseGallery(View view) {
            if (view.getId() == R.id.prev_btn) {
                hideEditCaption(captionArea);
                captionText.setVisibility(View.VISIBLE);
                if (gallery.getGalleryPointer() > 0) {
                    updateCurrentPhoto(gallery.getGalleryPointer() - 1);
                }
                else {
                    Toast.makeText(com.example.sprintone.gallery.GalleryActivity.this, "First image",
                            Toast.LENGTH_SHORT).show();
                }
            }
            else if (view.getId() == R.id.next_btn) {
                hideEditCaption(captionArea);
                captionText.setVisibility(View.VISIBLE);
                if (gallery.getGalleryPointer() < gallery.getPhotoPaths().size() - 1) {
                    updateCurrentPhoto(gallery.getGalleryPointer() + 1);

                }
                else {
                    Toast.makeText(com.example.sprintone.gallery.GalleryActivity.this, "Last image",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }

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
                    Uri imageUri = FileProvider.getUriForFile(that, "com.example.sprintone.android.fileprovider", imageFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    captured_image_path = imageFile.getAbsolutePath();
                    Log.e("imageFile", imageFile.getAbsolutePath());
                    //setExif(imageUri);
                }
            }
        }
    }
}

