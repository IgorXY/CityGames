package com.tigerware.citygames;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.tigerware.citygames.Adapter.GameAdapter;
import com.tigerware.citygames.Entity.*;
import com.tigerware.citygames.JSON.JSONResourceReader;
import com.tigerware.citygames.WEB.GetFromWeb;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URI;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class MainActivity extends AppCompatActivity {

    private ArrayList<Game> gameList;
    private User user;
    private ListView listView;
    private GameAdapter gameAdapter;


    private ArrayList<Game> GetGameList(){


        JSONResourceReader reader = new JSONResourceReader(getResources(), R.raw.game1);
        Game[] gameArray = reader.constructUsingGson(Game[].class);
        reader = new JSONResourceReader(getResources(), R.raw.tasks);
        Task[] taskArray = reader.constructUsingGson(Task[].class);

        ArrayList<Game> gameList = new ArrayList<Game>();
        for(int i = 0; i<gameArray.length; i++){
            gameArray[i].setTaskList(new ArrayList<Task>());
            for(int j = 0; j<taskArray.length;j++){
                if(gameArray[i].getId()==taskArray[j].getGameID()){
                    gameArray[i].getTaskList().add(taskArray[j]);
                }
            }
            gameList.add(gameArray[i]);
        }

        return gameList;
    }

    private void InitializeActivity(){
        gameList = GetGameList();

       /* TextView textView = (TextView) findViewById(R.id.startDateTextView);
        textView.setText(textView.getText() + " " +curGame.getStart_date().toString());
        textView = (TextView) findViewById(R.id.endDateTextView);
        textView.setText(textView.getText() + "   " +curGame.getFinishDate().toString());
        textView = (TextView) findViewById(R.id.StageAmountTextView);
        textView.setText(textView.getText() + " " +curGame.getStageAmount());

        Date date = new Date();
        Button button = (Button) findViewById(R.id.StartGameButton);
        if ((date.after(curGame.getStart_date()) || date.equals(curGame.getStart_date())) && date.before(curGame.getFinishDate())){
            button.setEnabled(true);
        }
        else
            button.setEnabled(false);*/
       /* RelativeLayout rl = new RelativeLayout(this);
        ScrollView scroll = new ScrollView(this);
        HorizontalScrollView hscroll = new HorizontalScrollView(this);
        TableLayout table = new TableLayout(this);

       table.setStretchAllColumns(true);
       // table.setShrinkAllColumns(true);
        Date date = new Date();

        for (final Game game: gameList) {
            TableRow row = new TableRow(this);
            row.setPadding(50, 50, 50, 5);

            TextView title = new TextView(this);
            title.setText(game.getTitle());
            title.setTextColor(Color.BLACK);

            TextView dateView = new TextView(this);
            //start.setText(new SimpleDateFormat("dd/MM/yyyy").format(game.getStart_date() ));
            TextView preDateView = new TextView(this);
            //end.setText(new SimpleDateFormat("dd/MM/yyyy").format(game.getFinishDate()));

            Button button = new Button(this);
            button.setText("Начать");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GameProgress gameProgress = new GameProgress();
                    gameProgress.setGame(game);
                    gameProgress.setStage(1);



                    intent.putExtra("GameProgress", gameProgress);
                    intent.putExtra("User", user);
                    startActivity(intent);
                }
            });
         //   button.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            if ((date.after(game.getStart_date()) || date.equals(game.getStart_date())) && date.before(game.getFinishDate())){
                button.setEnabled(true);
                preDateView.setText("до:");
                dateView.setText(new SimpleDateFormat("dd/MM/yyyy").format(game.getFinishDate()));
            }
            else {
                button.setEnabled(false);
                if (date.after(game.getFinishDate())) {
                    dateView.setText("");
                    preDateView.setText(" закончена ");
                    preDateView.setTextColor(Color.RED);

                } else {
                    dateView.setText(new SimpleDateFormat("dd/MM/yyyy").format(game.getStart_date()));
                    preDateView.setText(" начнется: ");
                    preDateView.setTextColor(Color.BLUE);
                }
            }
            row.addView(title);
            row.addView(preDateView);
            dateView.setTextColor(Color.BLACK);
            row.addView(dateView);
            row.addView(button);

            table.addView(row);


        }
        hscroll.addView(table);
        scroll.addView(hscroll);
        rl.addView(scroll);
        setContentView(rl);*/
        final Intent intent = new Intent(this, GameActivity.class);
       listView = (ListView) findViewById(R.id.listView);
        gameAdapter = new GameAdapter(this, R.layout.list_item, gameList);
        listView.setAdapter(gameAdapter);

        AdapterView.OnItemClickListener itemListener= new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Game game = (Game)parent.getItemAtPosition(position);
                Date date = new Date();
                if ((date.after(game.getStart_date()) || date.equals(game.getStart_date()))
                        && date.before(game.getFinishDate())) {
                    GameProgress gameProgress = new GameProgress();
                    gameProgress.setGame(game);
                    gameProgress.setStage(1);

                    intent.putExtra("GameProgress", gameProgress);
                    intent.putExtra("User", user);
                    startActivity(intent);
                }
            }
        };

        listView.setOnItemClickListener(itemListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user = (User) getIntent().getSerializableExtra("User");
        /*try {
            GetFromWeb getFromWeb = new GetFromWeb();
            String s = getFromWeb.getInternetData("http://localhost:8808/ReCourse/api/games");
        }catch(Exception e){};*/
        InitializeActivity();
    }

    public void GameActivityButtonClick(View view){

    }


}



