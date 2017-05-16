package com.tigerware.citygames;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tigerware.citygames.Entity.Game;
import com.tigerware.citygames.Entity.GameProgress;
import com.tigerware.citygames.Entity.Note;
import com.tigerware.citygames.Entity.Task;
import com.tigerware.citygames.Entity.User;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class GameStoryActivity extends AppCompatActivity {
    private Button prevButton;
    private Button nextButton;
    public GameProgress gameProgress;
    public ArrayList<Note> noteArrayList;
    private ImageView imageView;
    private  static  String filenameNotes = "notes";
    private User user;
    private TextView stageTextView;
    private TextView descTextView;
    private TextView noteTextView;

    private HashMap<String, Bitmap> pictureCache = new HashMap<String, Bitmap>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_story);

        gameProgress = (GameProgress) getIntent().getSerializableExtra("GameProgress");
        user = (User) getIntent().getSerializableExtra("User");

        prevButton = (Button)findViewById(R.id.prevStoryButton);
        nextButton = (Button)findViewById(R.id.nextStoryButton);
        imageView = (ImageView) findViewById(R.id.imageStoryView);
        stageTextView = (TextView) findViewById(R.id.StageStoryTextView);
        descTextView = (TextView) findViewById(R.id.DecriptionStoryTextView);
        noteTextView = (TextView)findViewById(R.id.noteStoryTextView);

        LoadNoteList();
        if(noteArrayList == null) {
            ////////////////////////
            noteArrayList = new ArrayList<Note>();
            for (Task task : gameProgress.getGame().getTaskList()) {
                Note note = new Note();
                note.setId(noteArrayList.size());
                note.setNote("");
                note.setTaskID(task.getId());
                note.setUserID(user.getId());
                note.setImageUri("");

                noteArrayList.add(note);

            }
            SaveNoteList();
            LoadNoteList();
        }
        fillInfo();
    }

    private  void SaveNoteList(){
        try {
            FileOutputStream fileOutputStream = getApplicationContext().openFileOutput(filenameNotes, Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(noteArrayList);
            objectOutputStream.close();
            fileOutputStream.close();
        }
        catch (IOException e){

        }
    }
    private void LoadNoteList() {
        try {
            FileInputStream fileInputStream = getApplicationContext().openFileInput(filenameNotes);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            noteArrayList = (ArrayList<Note>) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
        } catch (IOException e) {
        } catch (ClassNotFoundException e) {
        }
    }
    private Note getNote(int taskID){
        for(Note note:noteArrayList){
            if(note.getTaskID() == taskID && note.getUserID() == user.getId()){
                return note;
            }
        }
        Note note  = new Note();
        note.setId(noteArrayList.size());
        note.setNote("");
        note.setTaskID(taskID);
        note.setUserID(user.getId());
        note.setImageUri("");
        noteArrayList.add(note);
        return  note;
    }

    public void fillInfo(){
        Game curGame = gameProgress.getGame();
        Note curNote = getNote(curGame.getTaskList().get(gameProgress.getStage() - 1).getId());
        noteTextView.setText(curNote.getNote());
        stageTextView.setText("Этап "+ gameProgress.getStage());
        descTextView.setText("Задача: " + curGame.getTaskList().get(gameProgress.getStage() - 1).getDescription());


        if(gameProgress.getStage() == 1){
            prevButton.setEnabled(false);
        }
        else
        if(gameProgress.getGame().getTaskList().size() > 1){
            prevButton.setEnabled(true);
        }
        if(gameProgress.getGame().getTaskList().size() == gameProgress.getStage()){
            nextButton.setEnabled(false);
        }
        else
        if(gameProgress.getGame().getTaskList().size() > 1){
            nextButton.setEnabled(true);
        }
        if(!curNote.getImageUri().equals("")) {
            Bitmap bitmap;
            if (pictureCache.containsKey(curNote.getImageUri())) {
                imageView.setImageBitmap(pictureCache.get(curNote.getImageUri()));
            } else {
                try {
                    Uri contentURI = Uri.parse(curNote.getImageUri());
                    ContentResolver contentResolver = getContentResolver();
                    InputStream inputStream = contentResolver.openInputStream(contentURI);
                    //BitmapFactory.Options options = new BitmapFactory.Options();
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    pictureCache.put(curNote.getImageUri(), bitmap);
                    imageView.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    curNote.setImageUri("");
                }

            }
        }
        else{
            imageView.setImageBitmap(null);
        }

    }

    public void previousClick(View view){
        gameProgress.setStage(gameProgress.getStage() - 1);
        fillInfo();
    }

    public void nextClick(View view){
        gameProgress.setStage(gameProgress.getStage() + 1);
        fillInfo();
    }
}
