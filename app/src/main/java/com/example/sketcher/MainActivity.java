/*
 * @author: Rachel Stinnett
 * @file: MainActivity.java
 * @assignment: Programming Assignment 8- Sketcher
 * @course: CSC 317; Spring 2022
 * @description: The purpose of this programming assignment is to
 * create an application that allows the user to draw pictures, and
 * share pictures with people from their contacts using email. When
 * the application begins, the user is presented with a simple drawing
 * that has 4 colors, 3 stroke withs, and two action buttons in the user
 * interface. The user interface allows the user to select their color,
 * and stroke width, and draw on a canvas below. After drawing a picture,
 * the user can either clear the canvas, or share the picture with
 * someone from their contact list using their email. If there is not
 * an email within the contact list than the user can manually put one
 * in when drafting the email. In this MainActivity.java there is only
 * the onCreate() method because most of the functionality in this
 * application is done in the two fragments. This MainActivity is
 * only responsible for creating the first fragment to be called.
 */
package com.example.sketcher;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    /**
     * The purpose of this method is to be the place where the
     * activity starts for the java program. The onCreate()
     * method is supposed to initialize the activity. The
     * setContentView is set to R.layout.activity_main. In
     * this onCreate() method the drawing fragment is created
     * which creates the four colors, three stroke sizes,
     * the clear button, share button, as well as the
     * canvas itself. This method does this by using the
     * getSupportFragmentManager() and the beginTransaction()
     * method in order to create a transaction so the fragment
     * could begin.
     * @param savedInstanceState = A Bundle object used to
     * re-create the activity so that prior information is not
     * lost.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DrawingFragment drawingFragment = new DrawingFragment();
        drawingFragment.setContainerActivity(this);
        //Creates the transaction
        getSupportFragmentManager().beginTransaction().
                add(R.id.mainLayoutContainerInner,drawingFragment).
                addToBackStack(null).commit();
    }
}