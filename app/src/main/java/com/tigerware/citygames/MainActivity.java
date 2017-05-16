package com.tigerware.citygames;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.tigerware.citygames.Adapter.GameAdapter;
import com.tigerware.citygames.Entity.Game;
import com.tigerware.citygames.Entity.GameProgress;
import com.tigerware.citygames.Entity.Task;
import com.tigerware.citygames.Entity.User;
import com.tigerware.citygames.JSON.JSONResourceReader;

import java.util.ArrayList;
import java.util.Date;


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

        final Intent intentGame = new Intent(this, GameActivity.class);
        final Intent intentStory = new Intent(this, GameStoryActivity.class);
       listView = (ListView) findViewById(R.id.listView);
        gameAdapter = new GameAdapter(this, R.layout.list_item, gameList);
        listView.setAdapter(gameAdapter);

        AdapterView.OnItemClickListener itemListener= new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Game game = (Game)parent.getItemAtPosition(position);
                Date date = new Date();
                if (date.after(game.getStart_date()) || date.equals(game.getStart_date())) {
                    GameProgress gameProgress = new GameProgress();
                    gameProgress.setGame(game);
                    gameProgress.setStage(1);
                    if(date.before(game.getFinishDate())) {
                        intentGame.putExtra("GameProgress", gameProgress);
                        intentGame.putExtra("User", user);
                        startActivity(intentGame);
                    }
                    else{
                        intentStory.putExtra("GameProgress", gameProgress);
                        intentStory.putExtra("User", user);
                        startActivity(intentStory);
                    }
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



