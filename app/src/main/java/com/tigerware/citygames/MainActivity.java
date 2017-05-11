package com.tigerware.citygames;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.tigerware.citygames.Entity.*;
import com.tigerware.citygames.JSON.JSONResourceReader;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Game> gameList;
    private User user;
   /* private ArrayList<Task> GetTaskList(int gameID){
        //TODO: get task list from server
        ArrayList<Task> taskList = new ArrayList<Task>();
        Task task = new Task();

        task.setId(1);
        task.setDescription("Туда, не знаю куда");
        task.setLatitude(27.345435);
        task.setLongitude(52.344534);
        taskList.add(task);



        task = new Task();
        task.setId(2);
        task.setDescription("Сюда");
        task.setLatitude(27.345435);
        task.setLongitude(52.344534);
        taskList.add(task);

        task = new Task();
        task.setId(3);
        task.setDescription("В никуда");
        task.setLatitude(27.345435);
        task.setLongitude(52.344534);
        taskList.add(task);
        return taskList;
    }*/

    private ArrayList<Game> GetGameList(){
        //TODO: get game list from server

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
        /*Game game = new Game();
        game.setId(1);

        game.setTitle("Minsk1");
        game.setStart_date( java.sql.Date.valueOf("2017-04-16"));
        game.setFinishDate(java.sql.Date.valueOf("2017-04-20"));
        game.setStageAmount(1);

       /* game.setTaskList(GetTaskList((game.getId())));

        gameList.add(game);

        */

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
        RelativeLayout rl = new RelativeLayout(this);
        ScrollView scroll = new ScrollView(this);
        HorizontalScrollView hscroll = new HorizontalScrollView(this);
        TableLayout table = new TableLayout(this);

       table.setStretchAllColumns(true);
       // table.setShrinkAllColumns(true);
        Date date = new Date();
        final Intent intent = new Intent(this, GameActivity.class);
        for (final Game game: gameList) {
            TableRow row = new TableRow(this);
            row.setPadding(50, 50, 50, 5);

            TextView title = new TextView(this);
            title.setText(game.getTitle());

            TextView start = new TextView(this);
            start.setText(new SimpleDateFormat("dd/MM/yyyy").format(game.getStart_date() ));
            TextView end = new TextView(this);
            end.setText(new SimpleDateFormat("dd/MM/yyyy").format(game.getFinishDate()));

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
            }
            else
                button.setEnabled(false);

            row.addView(title);
            row.addView(start);
            row.addView(end);
            row.addView(button);

            table.addView(row);


        }
        hscroll.addView(table);
        scroll.addView(hscroll);
        rl.addView(scroll);
        setContentView(rl);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        user = (User) getIntent().getSerializableExtra("User");
        InitializeActivity();
    }

    public void GameActivityButtonClick(View view){

    }



}

