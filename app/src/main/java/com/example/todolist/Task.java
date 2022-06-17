package com.example.todolist;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Task{
    public long id;
    public String title;
    public String details;
    public String dateTime;
    public byte[] image;
    public boolean isDone;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Task() {

    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone() {
        this.isDone = true;
    }
    public void setUnDone() {
        this.isDone = false;
    }

    public Task(int id, String title, String detail) {
        this.id = id;
        this.title = title;
        this.details = detail;
        this.isDone = false;
    }

    public Task(String title, String detail) {
        this.title = title;
        this.details = detail;
        this.isDone = false;
    }

    @Override
    public String toString() {
        return "Task{" +
                "title='" + title + '\'' +
                ", detail='" + details + '\'' +
                ", dateTime='" + dateTime + '\'' +
                ", img=" + image +
                '}';
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return details;
    }

    public void setDetail(String detail) {
        this.details = detail;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public byte[] getImg() {
        return image;
    }

    public void setImg(byte[] img) {
        this.image = img;
    }

    public int getMinute(){
        String temp = this.dateTime;
        if(temp == null || temp.length() == 0){
            return 0;
        }else{
            int length = temp.length();
            int hour = Integer.parseInt(temp.substring(0, temp.indexOf(":")));
            int minute = Integer.parseInt(temp.substring(temp.indexOf(":")+1, temp.indexOf(" ")));
            String dayTime = temp.substring(temp.indexOf(" ")+1, length);
            int total = 0;
            if(dayTime.equals("AM")){
                total = hour*60+minute;
            }else {
                total = (hour+12)*60+minute;
            }
            return total;
        }
    }


}
