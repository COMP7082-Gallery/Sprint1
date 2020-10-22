package com.example.sprintone.facade;

import android.view.View;

public interface Actions {
    void editCaption (View view);
    void sharePhoto (View view);
    void deletePhoto (View view);
    void traverseGallery (View view);
    void dispatchTakePictureIntent (View view);
    void saveCaption (View view);
}
