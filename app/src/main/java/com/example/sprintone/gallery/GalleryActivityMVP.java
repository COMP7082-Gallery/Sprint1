package com.example.sprintone.gallery;

import com.example.sprintone.traversal.GallerySingleton;

public interface GalleryActivityMVP {

    interface Model {

    }

    interface View {

    }

    interface Presenter {
        void setView(GalleryActivityMVP.View view);

        void addPhoto();

        int deletePhoto(GallerySingleton gallery);

        void editCaption(GallerySingleton gallery, String edit_caption);


    }



}
