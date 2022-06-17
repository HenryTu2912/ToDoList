package com.example.todolist;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.SystemClock;
import android.provider.AlarmClock;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.EventListener;
import java.util.List;
import java.util.Objects;

public class AddNewTask extends AppCompatActivity{
    private TaskDBHelper db;
    private Cursor cursor;
    public Button btnAdd;
    public Button btnCancel;
    public EditText edTitle;
    public EditText edDetails;
    public ImageView imgBell;
    public ImageView imgCamera;
    public String timeSet;
    public int iHour;
    public int iMinute;
    private Calendar calendar;
    private ImageView captureImage;
    private TextView dateTime;
    private byte[] photo = null;
    private long second;

    //Constructor for AddNewTask Activity
    public AddNewTask(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("Task", "OnCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_task);
        getSupportActionBar().setTitle("TASK MANAGEMENT");
        //Set ID for items
        btnAdd = findViewById(R.id.btnAdd);
        btnCancel = findViewById(R.id.btnCancel);
        edTitle = findViewById(R.id.edTitle);
        edDetails = findViewById(R.id.edDetails);
        imgBell = findViewById(R.id.imgBell);
        imgCamera = findViewById(R.id.imgCamera);
        captureImage = findViewById(R.id.captureImage);
        dateTime = findViewById(R.id.dateTime);
        //Set click event for button and image views
        btnAdd.setOnClickListener(v->addTask());
        btnCancel.setOnClickListener(v->finish());
        imgBell.setOnClickListener(v-> setAlarm());
        imgCamera.setOnClickListener(v->takePhoto());
        //Create a database
        db = new TaskDBHelper(this);
        //Call refresh data method
        refreshData();

    }

    private void refreshData() {
        //Open database
        db.open();
        //Create a cursor from database looking for all tasks
        cursor = db.getAllTask();
        String[] cols = new String[]{db.TITLE};
        int[] views = new int[]{android.R.id.text1};
        //Create a cursor adapter from cursor with array of columns and views
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1, cursor, cols, views);
        //Close the database
        db.close();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //Check the request code for the take photo intent
        //Then get the bitmap from data and set image for Image view
        //Convert bitmap to byte array
        if(requestCode == 2){
            Bitmap bitmap =(Bitmap) data.getExtras().get("data");
            captureImage.setImageBitmap(bitmap);
            photo = getBitmapAsByteArray(bitmap);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void takePhoto() {
        //Create intent to capture the image and start activity for result with request quote is 2
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 2);
    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        //Create a output stream to compress bitmap to PNG with quality 85
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 85, outputStream);
        return outputStream.toByteArray();
    }

    private String setAlarm() {
        //Check if user select time or not
        String time = null;
        time = selectTime();

        if(time == null)
        {
            return null;
        }else{
            return time;
        }
    }

    private String selectTime() {
        //Create a calendar
        calendar = Calendar.getInstance();
        //Store present hour and minute
        int pHour = calendar.get(Calendar.HOUR_OF_DAY);
        int pMinutes = calendar.get(Calendar.MINUTE);
        //Call time picker dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int pMinute) {
                //Create timeSet string with Time format
                timeSet = FormatTime(hourOfDay, pMinute);
                //Store selected hour and minute
                iHour = hourOfDay;
                iMinute = pMinute;
                //Set calendar with selected hour and minute
                //calendar.set(0,0,0,view.getHour(), view.getMinute());
                //Set time in text for the text view
                dateTime.setText(timeSet);
                //Calculate the different between selected time and the present time
                long lastSecond = pHour*60*60+pMinutes*60;
                long setSecond =  iHour*60*60+iMinute*60;

                second = setSecond - lastSecond;
                //Get time in millisecond and calculate the different with second
                long now = Calendar.getInstance().getTimeInMillis();
                second =  now + (second * 1000);
            }
        }, pHour, pMinutes, true);

        //Show the time picker dialog
        timePickerDialog.show();
        return timeSet;
    }
    //Method to convert hour and minute into time format hh:mm
    //Display in AM and PM
    public String FormatTime(int hour, int minute) {

        String time;
        time = "";
        String formattedMinute;

        if (minute / 10 == 0) {
            formattedMinute = "0" + minute;
        } else {
            formattedMinute = "" + minute;
        }

        if (hour == 0) {
            time = "12" + ":" + formattedMinute + " AM";
        } else if (hour < 12) {
            time = hour + ":" + formattedMinute + " AM";
        } else if (hour == 12) {
            time = "12" + ":" + formattedMinute + " PM";
        } else {
            int temp = hour - 12;
            time = temp + ":" + formattedMinute + " PM";
        }
        return time;
    }
    //Method to add a task to database and display to the main screen
    private void addTask() {
        //Open database
        //Create a new task with task title, note, time, photo
        db.open();
        if(edTitle.getText().toString().equals(""))
        {
            Toast.makeText(this, "You have to enter the task title!",Toast.LENGTH_LONG).show();
        }else{
            Task task = new Task(edTitle.getText().toString(), edDetails.getText().toString());
            String time1 = timeSet;
            if(time1!= null){
                task.setDateTime(time1);
                AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
                Intent intent = new Intent(this, AlarmReceiver.class);
                intent.putExtra("notificationID", 1);
                intent.putExtra("message", edTitle.getText().toString().trim());
                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        this, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT
                );
                manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, second, pendingIntent);
            }
            if(photo != null) task.setImg(photo);
            db.insertTask(task);

            db.close();
            refreshData();

            Intent intent1 = new Intent(this,MainActivity.class);
            startActivity(intent1);
        }

    }

}