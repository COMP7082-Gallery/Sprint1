package com.example.sprintone;

import android.util.Log;

import com.example.sprintone.Gallery.GallerySingleton;
import com.example.sprintone.Navigation.GalleryTraversal;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TraversalUnitTest {

    private ArrayList<String> paths = new ArrayList<>(Arrays.asList("One", "Two", "Three", "Four", "Five"));

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void prev_button_at_end() {
        GallerySingleton gallery = GallerySingleton.getInstance();
        int end = paths.size() - 1;
        gallery.setGallery(paths, end);

        gallery.traverseGallery(gallery.getGalleryPointer() - 1);
        assertEquals(end - 1, gallery.getGalleryPointer());
    }

    @Test
    public void prev_button_at_start() {
        GallerySingleton gallery = GallerySingleton.getInstance();
        int start = 0;
        gallery.setGallery(paths, start);

        gallery.traverseGallery(gallery.getGalleryPointer() - 1);
        assertEquals(start, gallery.getGalleryPointer());
    }

    @Test
    public void next_button_at_end() {
        GallerySingleton gallery = GallerySingleton.getInstance();
        int end = paths.size() - 1;
        gallery.setGallery(paths, end);

        gallery.traverseGallery(gallery.getGalleryPointer() + 1);
        assertEquals(end, gallery.getGalleryPointer());
    }

    @Test
    public void next_button_at_start() {
        GallerySingleton gallery = GallerySingleton.getInstance();
        int start = 0;
        gallery.setGallery(paths, start);

        gallery.traverseGallery(gallery.getGalleryPointer() + 1);
        assertEquals(start + 1, gallery.getGalleryPointer());
    }

    @Test
    public void prev_button_at_end_path() {
        GallerySingleton gallery = GallerySingleton.getInstance();
        int end = paths.size() - 1;
        gallery.setGallery(paths, end);

        gallery.traverseGallery(gallery.getGalleryPointer() - 1);
        assertEquals(paths.get(end - 1), gallery.getPhotoPath());
    }

    @Test
    public void prev_button_at_start_path() {
        GallerySingleton gallery = GallerySingleton.getInstance();
        int start = 0;
        gallery.setGallery(paths, start);

        gallery.traverseGallery(gallery.getGalleryPointer() - 1);
        assertEquals(paths.get(start), gallery.getPhotoPath());
    }

    @Test
    public void next_button_at_end_path() {
        GallerySingleton gallery = GallerySingleton.getInstance();
        int end = paths.size() - 1;
        gallery.setGallery(paths, end);

        gallery.traverseGallery(gallery.getGalleryPointer() + 1);
        assertEquals(paths.get(end), gallery.getPhotoPath());
    }

    @Test
    public void next_button_at_start_path() {
        GallerySingleton gallery = GallerySingleton.getInstance();
        int start = 0;
        gallery.setGallery(paths, start);

        gallery.traverseGallery(gallery.getGalleryPointer() + 1);
        assertEquals(paths.get(start + 1), gallery.getPhotoPath());
    }


    //test delete
    @Test
    public void delete_last() {
        System.out.println("Delete Last Element");
        GallerySingleton gallery = GallerySingleton.getInstance();
        ArrayList<String> old_list = new ArrayList<String>(paths);
        int end = paths.size() - 1;
        int size_before = paths.size();
        gallery.setGallery(paths, end);

        gallery.removeFromGallery();
        assertEquals(size_before - 1, gallery.getPhotoPaths().size());

        //assert that the removed item is no longer in the list
        //print out all members of the updated gallery
        for (int i = 0; i < gallery.getPhotoPaths().size(); i++)
        {
            System.out.println(old_list.get(end) + " != " + gallery.getPhotoPaths().get(i));
            assertNotEquals(old_list.get(end), gallery.getPhotoPaths().get(i));
        }
    }

    @Test
    public void delete_first() {
        System.out.println("Delete First Element");
        GallerySingleton gallery = GallerySingleton.getInstance();
        ArrayList<String> old_list = new ArrayList<String>(paths);
        int start = 0;
        int size_before = paths.size();
        gallery.setGallery(paths, start);

        gallery.removeFromGallery();
        assertEquals(size_before - 1, gallery.getPhotoPaths().size());

        //assert that the removed item is no longer in the list
        //print out all members of the updated gallery
        for (int i = 0; i < gallery.getPhotoPaths().size(); i++)
        {
            System.out.println(old_list.get(start) + " != " + gallery.getPhotoPaths().get(i));
            assertNotEquals(old_list.get(start), gallery.getPhotoPaths().get(i));
        }
    }

    @Test
    public void delete_middle() {
        System.out.println("Delete Middle Element");
        GallerySingleton gallery = GallerySingleton.getInstance();
        ArrayList<String> old_list = new ArrayList<String>(paths);
        int middle = paths.size() / 2;
        int size_before = paths.size();
        gallery.setGallery(paths, middle);

        gallery.removeFromGallery();
        assertEquals(size_before - 1, gallery.getPhotoPaths().size());

        //assert that the removed item is no longer in the list
        //print out all members of the updated gallery
        for (int i = 0; i < gallery.getPhotoPaths().size(); i++)
        {
            System.out.println(old_list.get(middle) + " != " + gallery.getPhotoPaths().get(i));
            assertNotEquals(old_list.get(middle), gallery.getPhotoPaths().get(i));
        }
    }
}
