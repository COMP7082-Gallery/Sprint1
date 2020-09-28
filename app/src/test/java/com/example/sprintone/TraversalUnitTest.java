package com.example.sprintone;

import com.example.sprintone.Navigation.GalleryTraversal;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class TraversalUnitTest {

    private ArrayList<String> paths = new ArrayList<>(Arrays.asList("One", "Two", "Three", "Four", "Five"));

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void prev_button_at_end() {
        GalleryTraversal gt = new GalleryTraversal(paths);
        int end = paths.size() - 1;
        gt.traverseGallery(gt.getPhotoPointer() - 1);
        assertEquals(end - 1, gt.getPhotoPointer());
    }

    @Test
    public void prev_button_at_start() {
        GalleryTraversal gt = new GalleryTraversal(paths);
        int start = 0;
        //set pointer to start
        gt.traverseGallery(start);
        gt.traverseGallery(gt.getPhotoPointer() - 1);
        assertEquals(start, gt.getPhotoPointer());
    }

    @Test
    public void next_button_at_end() {
        GalleryTraversal gt = new GalleryTraversal(paths);
        int end = paths.size() - 1;
        gt.traverseGallery(gt.getPhotoPointer() + 1);
        assertEquals(end, gt.getPhotoPointer());
    }

    @Test
    public void next_button_at_start() {
        GalleryTraversal gt = new GalleryTraversal(paths);
        int start = 0;
        //set pointer to start
        gt.traverseGallery(start);
        gt.traverseGallery(gt.getPhotoPointer() + 1);
        assertEquals(start + 1, gt.getPhotoPointer());
    }



    @Test
    public void prev_button_at_end_path() {
        GalleryTraversal gt = new GalleryTraversal(paths);
        int end = paths.size() - 1;
        gt.traverseGallery(gt.getPhotoPointer() - 1);
        assertEquals(paths.get(end - 1), gt.getCurrentPhotoPath());
    }

    @Test
    public void prev_button_at_start_path() {
        GalleryTraversal gt = new GalleryTraversal(paths);
        int start = 0;
        //set pointer to start
        gt.traverseGallery(start);
        gt.traverseGallery(gt.getPhotoPointer() - 1);
        assertEquals(paths.get(start), gt.getCurrentPhotoPath());
    }

    @Test
    public void next_button_at_end_path() {
        GalleryTraversal gt = new GalleryTraversal(paths);
        int end = paths.size() - 1;
        gt.traverseGallery(gt.getPhotoPointer() + 1);
        assertEquals(paths.get(end), gt.getCurrentPhotoPath());
    }

    @Test
    public void next_button_at_start_path() {
        GalleryTraversal gt = new GalleryTraversal(paths);
        int start = 0;
        //set pointer to start
        gt.traverseGallery(start);
        gt.traverseGallery(gt.getPhotoPointer() + 1);
        assertEquals(paths.get(start + 1), gt.getCurrentPhotoPath());
    }
}
