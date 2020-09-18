package com.example.sprintone.Navigation;

import android.util.Log;

import com.example.sprintone.R;

public class GalleryTraversal {

    private String currentPhotoPath = null;
    private String[] photoPaths = null;
    private int photoPointer = -1;

    //
    //Paths
    //
    public GalleryTraversal(String[] paths) {
        if (paths.length > 0)
        {
            photoPaths = paths;
            photoPointer = photoPaths.length - 1;
            currentPhotoPath = photoPaths[photoPointer];
        }
    }


    public void setPhotoPaths(String[] photoPaths) {
        this.photoPaths = photoPaths;
    }


    public int getPhotoPointer() {
        return photoPointer;
    }

    public String getCurrentPhotoPath() {
        return currentPhotoPath;
    }

    public String[] getPhotoPaths() {
        return photoPaths;
    }


    //setter for photoPointer and currentPhotoPath
    public void traverseGallery(int upd_pointer_loc) {
        if (upd_pointer_loc > -1 && upd_pointer_loc < photoPaths.length)
        {
            photoPointer = upd_pointer_loc;
            currentPhotoPath = photoPaths[photoPointer];
        }
    }
}
