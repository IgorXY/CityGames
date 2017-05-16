package com.tigerware.citygames;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.tigerware.citygames.Entity.Game;
import com.tigerware.citygames.Entity.GameProgress;
import com.tigerware.citygames.Entity.Hint;
import com.tigerware.citygames.Entity.HintStatus;
import com.tigerware.citygames.Entity.Note;
import com.tigerware.citygames.Entity.Task;
import com.tigerware.citygames.Entity.User;
import com.tigerware.citygames.JSON.JSONResourceReader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class GameActivity extends AppCompatActivity {

    private LocationListener locationListener;
    private LocationManager locationManager;
    private Button takePictureButton;
    private boolean track = false;
    private Button prevButton;
    private Button nextButton;
    private Button getHintButton;
    private EditText editNote;
    private User user;
    public GameProgress gameProgress;
    public ArrayList<Note> noteArrayList;
    private ImageView imageView;
    private  static  String filenameNotes = "notes";
    private  static  String filenameHintStatus = "hintstatuses";
    private HashMap<String, Bitmap> pictureCache = new HashMap<String, Bitmap>();
    private ArrayList<HintStatus> hintStatusArrayList;
    private Hint[] hints;
    private TextView stageTextView;
    private TextView descTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        gameProgress = (GameProgress) getIntent().getSerializableExtra("GameProgress");
        user = (User) getIntent().getSerializableExtra("User");
        JSONResourceReader reader = new JSONResourceReader(getResources(), R.raw.hints);
        hints = reader.constructUsingGson(Hint[].class);
        ////////////////////////
       /* noteArrayList = new ArrayList<Note>();
        for(Task task:gameProgress.getGame().getTaskList()){
            Note note  = new Note();
            note.setId(noteArrayList.size());
            note.setFinished(false);
            note.setNote("");
            note.setTaskID(task.getId());
            note.setUserID(user.getId());

            noteArrayList.add(note);

        }
        SaveNoteList();*/
        ////////////////////////
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
        /////////////////////////
        LoadHintStatusList();
        if(hintStatusArrayList == null) {
            ////////////////////////
            hintStatusArrayList = new ArrayList<HintStatus>();
            HintStatus hintStatus = new HintStatus();
            hintStatus.setGameID(gameProgress.getGame().getId());
            hintStatus.setUserID(user.getId());
            hintStatus.setHintLeft(3);

            hintStatusArrayList.add(hintStatus);


            SaveHintStatusList();
            LoadHintStatusList();
        }


        prevButton = (Button)findViewById(R.id.prevButton);
        nextButton = (Button)findViewById(R.id.nextButton);
        getHintButton = (Button)findViewById(R.id.hintButton);
        imageView = (ImageView) findViewById(R.id.imageView3);
        takePictureButton = (Button)findViewById(R.id.CheckButton);
        stageTextView = (TextView) findViewById(R.id.StageTextView);
        descTextView = (TextView) findViewById(R.id.DecriptionTextView);
        takePictureButton.setEnabled(false);
        editNote = (EditText) findViewById(R.id.noteEditText);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                TextView textView = (TextView) findViewById(R.id.DistabceTextView);
                Task task = gameProgress.getGame().getTaskList().get(gameProgress.getStage() - 1);

                gameProgress.setDestination((int) gameProgress.calculateDistance(
                        location.getLatitude(), location.getLongitude(),
                        task.getLatitude(), task.getLongitude()));
                textView.setText("Цель: " + gameProgress.getDestination());

                if (gameProgress.getDestination().equals("достигнута")) {
                    takePictureButton.setEnabled(true);

                }else{
                    takePictureButton.setEnabled(false);
                }

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };


        ConfigureGPS();

        fillInfo();

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
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                break;
            default: break;
        }
    }

    public void  ConfigureGPS(){
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.INTERNET}
                        ,10);
            }

            return;
        }
        // this code won't execute IF permissions are not allowed, because in the line above there is return statement.
        try {
            int off = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
            if (off == 0) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setMessage("Включите GPS, чтобы отслеживать место цели.");
                alertDialog.setTitle("City Games");
                alertDialog.setCancelable(true);
                alertDialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        Intent onGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(onGPS);


                    }
                });
                alertDialog.setPositiveButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                alertDialog.create().show();
            }

        }
        catch (Settings.SettingNotFoundException e) {

        }

        //noinspection MissingPermission
        locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);


    }

    public void fillInfo(){
        Game curGame = gameProgress.getGame();
        Note curNote = getNote(curGame.getTaskList().get(gameProgress.getStage() - 1).getId());

        stageTextView.setText("Этап "+ gameProgress.getStage());
        String hint = getNote(-1*curGame.getTaskList().get(gameProgress.getStage() - 1).getId()).getNote();
        editNote.setText(curNote.getNote());
        if(!hint.equals("")){
            getHintButton.setEnabled(false);
            descTextView.setText("Задача: " + curGame.getTaskList().get(gameProgress.getStage() - 1).getDescription() +"("+hint+")");
        }
        else{
            getHintButton.setEnabled(true);
            descTextView.setText("Задача: " + curGame.getTaskList().get(gameProgress.getStage() - 1).getDescription());
        }
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

    public void saveNote(View view){
        getNote(gameProgress.getGame().getTaskList().get(gameProgress.getStage()-1).getId()).setNote(
                String.valueOf(editNote.getText()));
        SaveNoteList();
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
            Log.getStackTraceString(e);
        }
    }
    private void LoadNoteList(){
        try{
            FileInputStream fileInputStream = getApplicationContext().openFileInput(filenameNotes);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            noteArrayList = (ArrayList<Note>) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
        }
        catch (IOException e) {
            Log.getStackTraceString(e);
        }
        catch (ClassNotFoundException e){
        }
    }

    private  void SaveHintStatusList(){
        try {
            FileOutputStream fileOutputStream = getApplicationContext().openFileOutput(filenameHintStatus, Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(hintStatusArrayList);
            objectOutputStream.close();
            fileOutputStream.close();
        }
        catch (IOException e){
            Log.getStackTraceString(e);
        }
    }
    private void LoadHintStatusList(){
        try{
            FileInputStream fileInputStream = getApplicationContext().openFileInput(filenameHintStatus);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            hintStatusArrayList = (ArrayList<HintStatus>) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
        }
        catch (IOException e) {
            Log.getStackTraceString(e);
        }
        catch (ClassNotFoundException e){
        }
    }

    public void onTakePictureClick(View view){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != 0) {
            String s = data.getDataString();
            getNote(gameProgress.getGame().getTaskList().get(gameProgress.getStage() - 1).getId()).setImageUri(s);
            SaveNoteList();
            fillInfo();
        }
    }

    public void getHintClick(View view) {
        int taskID = gameProgress.getGame().getTaskList().get(gameProgress.getStage()-1).getId();
        Hint h = null;
        for(int i=0; i<hints.length; i++) {
            if (hints[i].getTaskID() == taskID) {
                h = hints[i];
                break;
            }
        }
        if(h != null){
            boolean hintStatusFound = false;
            for(HintStatus hintStatus: hintStatusArrayList){
                if(hintStatus.getUserID()== user.getId() && hintStatus.getGameID()== gameProgress.getGame().getId()){
                    hintStatusFound=true;
                    if(hintStatus.getHintLeft()>0){
                        getNote(-1*taskID).setNote(h.getHint());
                        hintStatus.setHintLeft(hintStatus.getHintLeft()-1);
                        break;
                    }
                    else{
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                        alertDialog.setMessage("Вы уже использовали все подсказки!");
                        alertDialog.setTitle("City Games");
                        alertDialog.setCancelable(true);
                        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });

                        alertDialog.create().show();
                    }
                }
            }
            if(!hintStatusFound){
                HintStatus hintStatus= new HintStatus();
                hintStatus.setGameID(gameProgress.getGame().getId());
                hintStatus.setUserID(user.getId());
                hintStatus.setHintLeft(2);
                getNote(-1*taskID).setNote(h.getHint());
            }
            SaveHintStatusList();
            SaveNoteList();
            fillInfo();
    }
    }
}
