package com.example.sprintone.filter_gallery;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
public class FilterGalleryAspect {
    @Before("execution(* com.example.sprintone.filter_gallery.FilterGalleryActivity.onCreate(*))")
    public void beforeCreateCall() {
        System.out.println("About to make call to create Filter Gallery");
    }

    @After("execution(* com.example.sprintone.filter_gallery.FilterGalleryActivity.onCreate(*))")
    public void afterCreateCall() {
        System.out.println("Just made call to create Filter Gallery");
    }

    @Before("execution(* com.example.sprintone.filter_gallery.FilterGalleryActivity.onCancel(*))")
    public void beforeOnCancelCall() {
        System.out.println("About to make call to cancel Filter Gallery");
    }

    @After("execution(* com.example.sprintone.filter_gallery.FilterGalleryActivity.onCancel(*))")
    public void afterOnCancelCall() {
        System.out.println("Just made call to cancel Filter Gallery");
    }

    @Before("execution(* com.example.sprintone.filter_gallery.FilterGalleryActivity.onFilter(*))")
    public void beforeOnFilterCall() {
        System.out.println("About to make call to update Gallery");
    }

    @After("execution(* com.example.sprintone.filter_gallery.FilterGalleryActivity.onFilter(*))")
    public void afterOnFilterCall() {
        System.out.println("Just made call to update Gallery");
    }
}
