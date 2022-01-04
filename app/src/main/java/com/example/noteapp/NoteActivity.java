package com.example.noteapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.noteapp.db.AppDatabase;
import com.example.noteapp.db.Note;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

public class NoteActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {
    String noteTitle;
    String note;
    String lastModifiedTime;
    String date;
    String reminder;
    int year;
    int month;
    int day;
    int hour;
    int minute;
    private ImageView back;
    private ImageView alarm;
    private EditText etNoteTitle;
    private EditText etNote;
    private ImageView save;
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    private TextView tvReminder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        back = findViewById(R.id.ivBack);
        etNoteTitle = findViewById(R.id.etNoteTitle);
        etNote = findViewById(R.id.etNote);
        save = findViewById(R.id.ivDone);
        alarm = findViewById(R.id.ivAlarm);
        tvReminder = findViewById(R.id.tvReminder);
        alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        NoteActivity.this,
                        now.get(Calendar.YEAR), // Initial year selection
                        now.get(Calendar.MONTH), // Initial month selection
                        now.get(Calendar.DAY_OF_MONTH) // Inital day selection
                );

                dpd.show(getSupportFragmentManager(), "Datepickerdialog");

            }
        });


        String from = getIntent().getStringExtra("MAIN");
        Log.e("Note", "onCreate: " + from);
        if (from.equals("ADDNOTE")) {
            //add button
        } else {
            //from on row click
            noteTitle = getIntent().getStringExtra("TITLE");
            note = getIntent().getStringExtra("NOTE");
            lastModifiedTime = getIntent().getStringExtra("LASTTIME");

            etNoteTitle.setText(noteTitle);
            etNote.setText(note);
        }

        save.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (from.equals("UPDATE")) {
                    AppDatabase.getInstance(NoteActivity.this).noteDao().update(etNoteTitle.getText().toString()
                            , etNote.getText().toString(), Utils.currentDateTime());
                    finish();
                } else {

                    String title = etNoteTitle.getText().toString();
                    String notes = etNote.getText().toString();
                    if (!title.equals("") && !notes.equals("")) {
                        Note note = new Note();
                        note.setNoteTitle(title);
                        note.setNote(notes);
                        if (tvReminder.getText().toString().isEmpty()) {
                            note.setReminder("");
                        } else {
                            note.setReminder(reminder);
                        }
                        note.setCreatedTime(Utils.currentDateTime());
                        note.setLastModifiedTime(Utils.currentDateTime());
                        AppDatabase.getInstance(NoteActivity.this).noteDao().insertUser(note);


                        alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        Intent intent = new Intent(NoteActivity.this, ReminderReceiver.class);
                        alarmIntent = PendingIntent.getBroadcast(NoteActivity.this, 0, intent, 0);
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(System.currentTimeMillis());
                        calendar.set(Calendar.DAY_OF_MONTH, day);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.HOUR_OF_DAY, hour);
                        calendar.set(Calendar.MINUTE, minute);
                        alarmMgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);

                        finish();
                    } else {
                        Toast.makeText(NoteActivity.this, "Please add note", Toast.LENGTH_LONG).show();
                    }

                }

            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        this.year = year;
        this.month = monthOfYear + 1;
        this.day = dayOfMonth;
        date = "" + dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
        Log.e("TAG", "onDateSet: " + date);
        TimePickerDialog timePickerDialog =
                TimePickerDialog.newInstance(NoteActivity.this, false);
        timePickerDialog.show(getSupportFragmentManager(), "TimerDialog");
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {

        this.hour = hourOfDay;
        this.minute = minute;
        Log.e("TAG", "onTimeSet: " + hourOfDay);
        String time = "" + hourOfDay + ":" + minute;
        reminder = "" + date + " " + time;

        tvReminder.setText("Reminder at - " + date + "  " + time);
    }

}