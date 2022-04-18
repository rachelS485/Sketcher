/*
 * @author: Rachel Stinnett
 * @file: ContactListFragment.java
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
 * in when drafting the email. In this ContactListFragment.java there
 * are only four main methods that take care of the contact list
 * as well as the email that occurs when the user clicks on the
 * contact. Then an email is formatted with the drawing image
 * and the user can choose to send it.
 */
package com.example.sketcher;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;


public class ContactListFragment extends Fragment {

    private String imageFile;
    private ArrayList<String> contactList;
    private ArrayList<String> contactIdList;
    private Activity containerActivity;
    private String email = null;

    /**
     * The purpose of this method is to create a context of an activity within
     * the fragment to keep track of the container activity from main. This
     * makes the process of completing certain tasks a lot easier when there is
     * a context of the main activity.
     * @param containerActivity = An activity that represents the container
     * activity which is main.
     */
    public void setContainerActivity(Activity containerActivity) {
        this.containerActivity = containerActivity;
    }

    /**
     * This method is responsible for gathering the information
     * that was saved into the bundle in the previous fragment.
     * This method does this by using the getArguments().getString()
     * method to gather the key value pairs saved. This method
     * saves those as class variables to use in the onCreateView()
     * method.
     * @param savedInstanceState = A Bundle object used to
     * re-create the activity so that prior information is not
     * lost.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            imageFile = getArguments().getString("imageFilePath");
            contactList = getArguments().getStringArrayList("contactInfo");
            contactIdList = getArguments().getStringArrayList("contactIdList");
        }
    }

    /**
     * This method is responsible for creating the contacts list
     * as well as creating an email when the user clicks on the
     * specific contact. This method does this by using an ArrayAdapter
     * to place the ArrayList of strings onto the list view using the
     * setAdapter() method. Then this method uses the setOnItemClickListener()
     * in order to know what contact within the list view was clicked on. Then
     * this method uses the Cursor object with its associated methods to
     * get the right email from that corresponding contact. The .getContentResolver()
     * with the combination of CommonDataKinds() method makes this achievable.
     * After the email is gathered then a helper method is used to create the
     * actual email intent need to send the email.
     * @param inflater = An inflater used to inflate the fragment.
     * @param container = The view container from main activity that can
     *  have elements added to it or replaced.
     * @param savedInstanceState = A Bundle object used to
     * re-create the activity so that prior information is not
     * lost.
     * @return view = A view returned so that the fragment can
     * be correctly placed onto the container.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_list, container, false);
        ListView listView = view.findViewById(R.id.listView);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(containerActivity,
                R.layout.custom_list_view, R.id.firstText, contactList);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> list, View v, int pos, long id) {
                Cursor emails = getActivity().getContentResolver().query(
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = "
                                + contactIdList.get(pos), null, null);
                if (emails.moveToNext()) {
                    //Gets email from contact
                    int emailNum = emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS);
                    email = emails.getString(emailNum);
                }
                emails.close();
                //Does the email intent
                sendDrawingEmail();
            }
        });
        return view;
    }

    /**
     * The purpose of this method is to create an intent to
     * send an email with either the email from the contact or
     * if there is not an email the user can still manually put one.
     * This method does this by first checking if an email was in the
     * contact with an if statement. Then this method uses the ACTION_SEND
     * intent and then the intent methods to put the extra information
     * such as .putExtra() with the email and the the file path. To
     * start the intent the startActivity() method is used.
     */
    public void sendDrawingEmail(){
        if (email == null){
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType(getString(R.string.setType));
            intent.putExtra(android.content.Intent.EXTRA_STREAM, Uri.parse(imageFile));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        }
        else{
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType(getString(R.string.setType));
            intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] {email});
            intent.putExtra(android.content.Intent.EXTRA_STREAM, Uri.parse(imageFile));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        }
    }
}
