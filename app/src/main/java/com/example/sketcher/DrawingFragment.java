/*
 * @author: Rachel Stinnett
 * @file: DrawingFragment.java
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
 * in when drafting the email. In this DrawingFragment.java file is where
 * the Ui for the canvas part of the application is updated programmatically.
 * The first part of this file contains the DrawingView object which is a
 * custom class that creates a drawing path on a canvas bitmap. Then
 * after this private class there are the fragment methods for the
 * button functionality. Like when the different colors are clicked the
 * drawing path color is change and same for if the stroke size buttons
 * are clicked. Then if the clear button is clicked a new drawing view
 * is started. If the share button is clicked all of information needed
 * such as a saved image of the canvas and a contact list is passed to
 * the next fragment using a bundle.
 */
package com.example.sketcher;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class DrawingFragment extends Fragment {
    private Activity containerActivity;
    private View view;
    private DrawingView drawingView = null;
    private ArrayList<String> contactList = new ArrayList<>();
    private ArrayList<String> contactIdList = new ArrayList<>();

    /**
     * This is a private class used to create a Drawing View object
     * that has a canvas, canvas bitmap, draw path, and draw paint.
     */
    private class DrawingView extends View {
        private Path drawPath;
        private Paint drawPaint;
        private Canvas drawCanvas;
        private Bitmap canvasBitmap;

        /**
         * The purpose of this method is to be a constructor for
         * the Drawing View object. It is used to initialize objects
         * when an object of a class is created. It can be used to
         * set initial values for object attributes. This method
         * just sets up the drawing and sets the background color.
         * @param context = A context object that represents the
         * environment this class is in.
         * @param attrs = A collection of unique attributes.
         * This is a read-only, immutable interface.
         */
        public DrawingView(Context context, AttributeSet attrs){
            super(context, attrs);
            setupDrawing();
            this.setBackgroundColor(0xFFFFFFFF);
        }

        /**
         * The purpose of this method is to set up the
         * drawing and the paint stroke properties. This
         * method does this by creating a path an paint object
         * and then using the various set method. Like setStyle()
         * setColor(), etc.
         */
        private void setupDrawing(){
            drawPath = new Path();
            drawPaint = new Paint();
            drawPaint.setColor(0xFF000000);
            drawPaint.setAntiAlias(true);
            drawPaint.setStrokeWidth(15.0f);
            drawPaint.setStyle(Paint.Style.STROKE);
            drawPaint.setStrokeJoin(Paint.Join.ROUND);
            drawPaint.setStrokeCap(Paint.Cap.ROUND);
        }

        /**
         * The purpose of this method is to reset the paint color
         * based on the color that was clicked on by using the
         * setColor() method.
         * @param color = A hex value that represents the color
         * that needs to be set.
         */
        protected void setColor(int color){
            drawPaint.setColor(color);
        }

        /**
         * The purpose of this method to reset the stroke color based on
         * what size was clicked on. This method does this by using if
         * statements to check what size and the the setStrokeWidth()
         * method.
         * @param size = A string that represents what size button
         * was clicked on.
         */
        protected void setStrokeSize(String size){
            if(size.equals("small")){
                drawPaint.setStrokeWidth(5.0f);
            }
            if(size.equals("medium")){
                drawPaint.setStrokeWidth(25.0f);
            }
            if(size.equals("large")){
                drawPaint.setStrokeWidth(45.0f);
            }
        }

        /**
         * The purpose of this method is to set a size assigned
         * to the view when the size changes. This method does
         * this by creating a new bitmap with the proper
         * parameters and then initializing a new canvas.
         * @param w = An integer representing the canvas width.
         * @param h = An integer representing the canvas height.
         * @param oldw = An integer representing the old canvas width.
         * @param oldh = An integer representing the old canvas height.
         */
        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            drawCanvas = new Canvas(canvasBitmap);
        }

        /**
         * the purpose of this method is to draw the view
         * whenever there is a touch event since this method
         * will be called after the touch event. This method
         * does this using the drawBitmap() and drawPath()
         * methods.
         * @param canvas = A canvas object that represents the
         * current canvas context.
         */
        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawBitmap(canvasBitmap, 0, 0, drawPaint);
            canvas.drawPath(drawPath, drawPaint);
        }

        /**
         * The purpose of this method is to register the
         * user touches on the screen as a drawing action.
         * This allows for the Ui to respond to up, down,
         * and move events. This allows the drawing to
         * be created smoothly as if a user was to draw
         * on paper. This method does this by using the
         * lineTo(), moveTo(), and drawPath() methods.
         * @param event = A MotionEvent object that
         * represents strokes on the screen.
         * @return boolean = A boolean that represents
         * if a drawing was invalidated.
         */
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();
            //respond to down, move and up events
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    drawPath.moveTo(x,y);
                    break;
                case MotionEvent.ACTION_UP:
                    drawPath.lineTo(x,y);
                    drawCanvas.drawPath(drawPath, drawPaint);
                    drawPath.reset();
                    break;
                case MotionEvent.ACTION_MOVE:
                    drawPath.lineTo(x,y);
                    break;
                default:break;
            }
            //redraw
            invalidate();
            return true;
        }


        /**
         * The purpose of this method is to start a new
         * drawing whenever the clear button is clicked
         * on. This method does this by using the
         * drawColor() method and the invalidate()
         * method.
         */
        public void startNew(){
            drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
            invalidate();
        }

    }

    /**
     * The purpose of this method is to create a context of an activity within
     * the fragment to keep track of the container activity from main. This
     * makes the process of completing certain tasks a lot easier when there is
     * a context of the main activity.
     * @param containerActivity = An activity that represents the container
     * activity which is main.
     */
    public void setContainerActivity(Activity containerActivity){
        this.containerActivity = containerActivity;
    }

    /**
     * The purpose of this method is to be called to have the
     * fragment instantiate its user interface view. This method
     * first inflates the view which is an important aspect apart
     * of all onCreateView() methods in fragments. In this method
     * there are four helper methods that are called to change
     * the stroke size and color, and then to clear the canvas
     * if the clear button is clicked. And then if the share
     * button is clicked the next fragment is created. The linear
     * layout is also accessed in order to set the drawing on the
     * screen.
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
        view = inflater.inflate(R.layout.fragment_drawing, container, false);
        drawingView = new DrawingView(containerActivity, null);
        checkColorButtons();
        checkStrokeButtons();
        checkClear();
        checkSharing();
        LinearLayout linearLayout = view.findViewById(R.id.drawingCanvas);
        linearLayout.addView(drawingView);
        return view;
    }

    /**
     * The purpose of this method is to change the stroke color
     * if the button is clicked on. This method does this by using
     * the .onClickListener() method on all four image view colored
     * buttons and then using the setColor() method with the hexadecimal
     * value passed in. The actual color change occurs within the
     * DrawingView class.
     */
    public void checkColorButtons(){
        ImageView imageColorRed = (ImageView) view.findViewById(R.id.redButton);
        imageColorRed.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                drawingView.setColor(0xFFFF0000);
            }
        });
        ImageView imageColorGreen = (ImageView) view.findViewById(R.id.greenButton);
        imageColorGreen.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                drawingView.setColor(0xFF00FF00);
            }
        });
        ImageView imageColorBlue = (ImageView) view.findViewById(R.id.blueButton);
        imageColorBlue.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                drawingView.setColor(0xFF0000FF);
            }
        });
        ImageView imageColorPurple = (ImageView) view.findViewById(R.id.purpleButton);
        imageColorPurple.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                drawingView.setColor(0xFF800080);
            }
        });
    }

    /**
     * The purpose of this method is to change the stoke size
     * if the button is clicked on. This method does this by using
     * the .onClickListener() method on all three text view sized
     * buttons and then using the setStrokeSize() method with the
     * string designating the size small medium or large. The actual
     * size change occurs within the DrawingView class.
     */
    public void checkStrokeButtons(){
        TextView textSmall = (TextView) view.findViewById(R.id.smallButton);
        textSmall.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                drawingView.setStrokeSize("small");
            }
        });
        TextView textMedium = (TextView) view.findViewById(R.id.mediumButton);
        textMedium.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                drawingView.setStrokeSize("medium");
            }
        });
        TextView textLarge = (TextView) view.findViewById(R.id.largeButton);
        textLarge.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                drawingView.setStrokeSize("large");
            }
        });

    }

    /**
     * The purpose of this method is to clear the drawing on the
     * canvas id the clear button is clicked on. This method does
     * this by using an setOnClickListener() method on the clear
     * button text view. And then this button calls the startNew()
     * method within the DrawingView class. So the actual clearing
     * of the drawing occurs in the DrawingView class.
     */
    public void checkClear(){
        TextView textClear = (TextView) view.findViewById(R.id.clearButton);
        textClear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                drawingView.startNew();
            }
        });
    }

    /**
     * The purpose of this method is to start the next contact list
     * fragment if the share button is clicked on. This method does
     * this by using the setOnClickListener() method to check if the
     * button was clicked on. Then this method uses a helper to
     * save the canvas image as a string filepath. Then the cursor
     * object is used in order to access the contact list of the user
     * on their phone. This method does this by using the cursor
     * methods and a while loop. This method saved the contact id
     * and the name into a contact ArrayList. The contact id is also
     * saved into a separate ArrayList. Then this method creates
     * a new ContactListFragment and passes in the file path, contact
     * ArrayList, and the contact id ArrayList into the bundle object.
     * Then the fragment transaction is used to call the next fragment.
     */
    public void checkSharing(){
        TextView textClear = (TextView) view.findViewById(R.id.shareButton);
        textClear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                String filePath = saveImageHelper();
                Cursor cursor = containerActivity.getContentResolver().query(
                        ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
                while (cursor.moveToNext()) {
                    int idNum = cursor.getColumnIndex(ContactsContract.Contacts._ID);
                    String contactId = cursor.getString(idNum);
                    int givenNum = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                    String given = cursor.getString(givenNum);
                    //ArrayList will be used in the next fragment for ArrayAdapter
                    contactList.add(given + " :: " + contactId);
                    contactIdList.add(contactId);
                }
                cursor.close();
                ContactListFragment contactListFragment = new ContactListFragment();
                contactListFragment.setContainerActivity(containerActivity);
                Bundle args = new Bundle();
                args.putString("imageFilePath", filePath);
                args.putStringArrayList("contactInfo", contactList);
                args.putStringArrayList("contactIdList", contactIdList);
                contactListFragment.setArguments(args);
                //Creates transaction
                getFragmentManager().beginTransaction().
                        replace(R.id.mainLayoutContainerInner,contactListFragment).
                        addToBackStack(null).commit();
            }
        });
    }

    /**
     * The purpose of this method is to be a helper for the
     * checkSharing() method. This method creates a bitmap
     * and new canvas in order to compress the bitmap to create
     * a file path. This method does this by using the .createBitmap()
     * method, .compress() method, and the insertImage() method. Then
     * the filepath string is returned so that it can be sent in an
     * email.
     * @return filepath = A string that represents the image file path.
     */
    public String saveImageHelper(){
        //Same idea from Collage Maker Pa
        Bitmap bitmap = Bitmap.createBitmap(view.findViewById(R.id.drawingCanvas).getWidth(),
                view.findViewById(R.id.drawingCanvas).getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.findViewById(R.id.drawingCanvas).draw(canvas);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String filePath = MediaStore.Images.Media.insertImage(containerActivity.getContentResolver(),
                bitmap,getString(R.string.filePathTitle),null);
        return filePath;
    }
}
