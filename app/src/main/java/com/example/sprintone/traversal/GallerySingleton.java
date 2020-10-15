package com.example.sprintone.traversal;

import java.util.ArrayList;

public class GallerySingleton {

    private static final GallerySingleton SINGLE_INSTANCE = new GallerySingleton();

    private ArrayList<String> photoPaths = new ArrayList<String>();
    private int galleryPointer = -1;

    private GallerySingleton() {}

    public static GallerySingleton getInstance() {
        return SINGLE_INSTANCE;
    }

    //
    //Sets the photo paths for the gallery
    //Sets the gallery pointer
    //
    public void setGallery(ArrayList<String> photoPaths, int pointer) {
        this.photoPaths = photoPaths;
        this.galleryPointer = pointer;
    }

    //Returns the gallery pointer as an int
    //
    public int getGalleryPointer() {
        return galleryPointer;
    }

    //Returns the Gallery as an ArrayLists of String filepaths
    //
    public ArrayList<String> getPhotoPaths() {
        return photoPaths;
    }

    //
    //Set current photo path to received path
    //
    public void setPhotoPath(String path) {
        this.photoPaths.set(galleryPointer, path);
    }

    //Returns the current photo path as a String
    //Throws an index out of bounds exception
    public String getPhotoPath() throws IndexOutOfBoundsException {
        return photoPaths.get(galleryPointer);
    }

    //Not sure if gonna use
    public void addToGallery(String path) {
        photoPaths.add(path);
        galleryPointer = photoPaths.size() - 1;
    }

    //
    //Removes a filepath from the Arraylist
    //If any filepath but the first, sets the new pointer to the previous image path
    //Else stays at location 0
    //
    public void removeFromGallery() throws IndexOutOfBoundsException {

        if (!photoPaths.isEmpty())
        {
            photoPaths.remove(galleryPointer);
            //if delete photo, display prev photo; else stay at photo with index 0 "New first photo"
            if (galleryPointer > 0)
                galleryPointer--;
        }
    }

    //updates the photopointer based on the updated pointer location
    //
    public void traverseGallery(int upd_pointer) {
        if (!photoPaths.isEmpty() && upd_pointer > -1 && upd_pointer < photoPaths.size())
            this.galleryPointer = upd_pointer;

    }
}
