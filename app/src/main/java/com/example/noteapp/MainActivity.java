package com.example.noteapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.example.noteapp.db.AppDatabase;
import com.example.noteapp.db.Note;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnDeleteNote{
    private FloatingActionButton floating;
    AppDatabase appDatabase;
    ArrayList<Note> noteList;
    RecyclerView rvRecycler;
    private EditText etSearch;
    RecyclerAdapter recyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rvRecycler=findViewById(R.id.rvRecycler);
        floating=findViewById(R.id.faFloat);
        etSearch=findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        floating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, NoteActivity.class);
                intent.putExtra("MAIN","ADDNOTE");
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        noteList = (ArrayList<Note>) AppDatabase.getInstance(this).noteDao().getNoteSortByDESCLastModifiedTime();
        rvRecycler.setLayoutManager(new LinearLayoutManager(this));
        recyclerAdapter=new RecyclerAdapter(noteList,this);
        rvRecycler.setAdapter(recyclerAdapter);
    }
    private  void  filter(String text){
        ArrayList<Note> temp=new ArrayList<Note>();
        for(int i=0; i< noteList.size();i++){
            if(noteList.get(i).getNoteTitle().toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT))){
                Note note=new Note();
                note.setId(noteList.get(i).getId());
                note.setNoteTitle(noteList.get(i).getNoteTitle());
                note.setNote(noteList.get(i).getNote());
                note.setCreatedTime(noteList.get(i).getCreatedTime());
                note.setLastModifiedTime(noteList.get(i).getLastModifiedTime());
                temp.add(note);
            }
        }
        recyclerAdapter=new RecyclerAdapter(temp,this);
        rvRecycler.setAdapter(recyclerAdapter);

    }

    @Override
    public void onDelete(int id) {
        AppDatabase.getInstance(this).noteDao()
                .deleteNote(id);
        noteList = (ArrayList<Note>) AppDatabase.getInstance(this).noteDao().getNoteSortByDESCLastModifiedTime();
        rvRecycler.setLayoutManager(new LinearLayoutManager(this));
        recyclerAdapter=new RecyclerAdapter(noteList,this);
        rvRecycler.setAdapter(recyclerAdapter);
    }
}