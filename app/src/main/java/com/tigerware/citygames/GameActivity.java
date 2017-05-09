package com.tigerware.citygames;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.tigerware.citygames.Entity.Game;
import com.tigerware.citygames.Entity.GameProgress;
import com.tigerware.citygames.Entity.Task;

public class GameActivity extends AppCompatActivity {

    private LocationListener locationListener;
    private LocationManager locationManager;
    private Button b;
    private boolean track = false;
    private Button prevButton;
    private Button nextButton;
    private EditText editNote;

    public GameProgress gameProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        gameProgress = (GameProgress) getIntent().getSerializableExtra("GameProgress");

        prevButton = (Button)findViewById(R.id.prevButton);
        nextButton = (Button)findViewById(R.id.nextButton);

        editNote = (EditText) findViewById(R.id.noteEditText);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                TextView textView = (TextView) findViewById(R.id.DistabceTextView);
                Task task =  gameProgress.getGame().getTaskList().get(gameProgress.getStage()-1);
                gameProgress.setDestination((int) gameProgress.calculateDistance(
                        location.getLatitude(), location.getLongitude(),
                       task.getLatitude(), task.getLongitude()));
                textView.setText("Цель: "+ gameProgress.getDestination());
                if(gameProgress.getDestination().equals("достигнута")){
                    b.setEnabled(true);
                    if(gameProgress.getStage() == gameProgress.getGame().getStageAmount()){
                        b.setText("Закончить");
                    }
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

        b = (Button)findViewById(R.id.CheckButton);
        b.setEnabled(false);
        ConfigureGPS();

        fillInfo();

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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET}
                        ,10);
            }
            return;
        }
        // this code won't execute IF permissions are not allowed, because in the line above there is return statement.

        //noinspection MissingPermission
        locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);

    }

    public void fillInfo(){
        Game curGame = gameProgress.getGame();
        TextView textView = (TextView) findViewById(R.id.StageTextView);
        textView.setText("Этап "+ gameProgress.getStage());
        textView = (TextView) findViewById(R.id.DecriptionTextView);
        textView.setText("Задача: " + curGame.getTaskList().get(gameProgress.getStage() - 1).getDescription());
        editNote.setText(gameProgress.getNoteList().get(gameProgress.getStage()-1));

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
        gameProgress.getNoteList().set(gameProgress.getStage()-1, String.valueOf(editNote.getText()));
    }


}
