package com.example.sprintone.Gallery;

import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class GallerySingleton {

    private static final GallerySingleton SINGLE_INSTANCE = new GallerySingleton();

    private ArrayList<String> photoPaths = null;
    private int galleryPointer = -1;

    private GallerySingleton() {}

    public static GallerySingleton getInstance() {
        return SINGLE_INSTANCE;
    }

    public void setGallery(ArrayList<String> photoPaths, int pointer) {
        this.photoPaths = photoPaths;
        this.galleryPointer = pointer;
    }

    public int getGalleryPointer() {
        return galleryPointer;
    }

    public ArrayList<String> getPhotoPaths() {
        return photoPaths;
    }

    public String getPhotoPath() throws IndexOutOfBoundsException {
        return photoPaths.get(galleryPointer);
    }

    public void addToGallery(String path) {
        photoPaths.add(path);
        galleryPointer = photoPaths.size() - 1;
    }

    public void removeFromGallery(int index) throws IndexOutOfBoundsException {
        photoPaths.remove(index);
    }

    public void traverseGallery(int upd_pointer) {
        if (upd_pointer > -1 && upd_pointer < photoPaths.size())
            this.galleryPointer = upd_pointer;

        Log.d("huh", "huh: " + galleryPointer);
        Log.d("heh", "heh arr size: " + photoPaths.size());
    }
}
