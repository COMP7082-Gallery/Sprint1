package com.example.sprintone.gallery;

import android.util.Log;
import android.view.View;

import com.example.sprintone.traversal.GallerySingleton;

import java.io.File;

public class GalleryActivityPresenter implements GalleryActivityMVP.Presenter {

    private GalleryActivityMVP.View view;
    private GalleryActivityMVP.Model model;

    public GalleryActivityPresenter(GalleryActivityMVP.Model model) {
        this.model = model;
    }

    public void setView(GalleryActivityMVP.View view) { this.view = view; }


    public void addPhoto(GallerySingleton gallery, String path) {
        //add a path to the gallery
        gallery.addToGallery(path);
        //add a Picture to the db
    }


    //deletes an image
    //remove logic from db
    //if delete failed return -1
    public int deletePhoto(GallerySingleton gallery) {
        Log.d("Delete", "In Delete");

        //add logic to remove from db
        try {
            File file = new File(gallery.getPhotoPath());
            boolean deleted = file.delete();
            if (deleted) {
                gallery.removeFromGallery();
                Log.d("Delete success", "Success Deleting from file system");
                return gallery.getGalleryPointer();
            }
        } catch (IndexOutOfBoundsException e) {
            Log.d("Delete out of bounds", "Tried to remove out of bounds");
        }
        return -1;
    }

    public void saveCaption(GallerySingleton gallery, String edit_caption) {
        String[] attr = gallery.getPhotoPath().split("_");
        Log.d("Files", "CurrentFileName:" + gallery.getPhotoPath());
        if (attr.length == 7 && edit_caption != "") {
            File newName = new File(attr[0] + "_" + edit_caption + "_" + attr[2] + "_" + attr[3] + "_" + attr[4] + "_" + attr[5] + "_" + attr[6]);
            Log.d("Files", "NewFileName:" + newName.getAbsolutePath());
            File oldName = new File(gallery.getPhotoPath());
            Log.d("Files", "OldFileName:" + gallery.getPhotoPath());
            oldName.renameTo(newName);
            gallery.setPhotoPath(newName.getAbsolutePath());
        }
    }
}
