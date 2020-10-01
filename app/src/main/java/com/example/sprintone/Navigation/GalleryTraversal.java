package com.example.sprintone.Navigation;

import android.util.Log;

import com.example.sprintone.R;

import java.util.ArrayList;

public class GalleryTraversal {

    private String currentPhotoPath = null;
    private ArrayList<String> photoPaths = null;
    private int photoPointer = 0;

    //
    //Paths
    //
    public GalleryTraversal(ArrayList<String> paths) {
        if (paths != null)
        {
            photoPaths = paths;
            photoPointer = photoPaths.size() - 1;
            currentPhotoPath = photoPaths.get(photoPointer);
        }
    }

    public void setPhotoPaths(ArrayList<String> photoPaths) {
        this.photoPaths = photoPaths;
    }

    public void setCurrentPhotoPaths(String path) {
        photoPaths.set(photoPointer, path);
    }

    public int getPhotoPointer() {
        return photoPointer;
    }

    public String getCurrentPhotoPath() {
        return currentPhotoPath;
    }

    public ArrayList<String> getPhotoPaths() {
        return photoPaths;
    }

    //setter for photoPointer and currentPhotoPath
    public void traverseGallery(int upd_pointer_loc) {
        if (upd_pointer_loc > -1 && upd_pointer_loc < photoPaths.size())
        {
            photoPointer = upd_pointer_loc;
            currentPhotoPath = photoPaths.get(photoPointer);
        }
    }
}
