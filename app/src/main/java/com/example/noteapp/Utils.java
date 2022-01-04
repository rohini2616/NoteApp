package com.example.noteapp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Utils {


    public static String currentDateTime(){
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm a");
        String date = df.format(Calendar.getInstance().getTime());
        return date;
    }
}
