package com.tigerware.citygames.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tigerware.citygames.Entity.Game;
import com.tigerware.citygames.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by User on 13.05.2017.
 */

public class GameAdapter extends ArrayAdapter<Game> {
    private LayoutInflater inflater;
    private int layout;
    private ArrayList<Game> games;

    public GameAdapter(Context context, int resource, ArrayList<Game> games) {
        super(context, resource, games);
        this.games = games;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
    }
    public View getView(int position, View convertView, ViewGroup parent) {

        View view=inflater.inflate(this.layout, parent, false);

        Date date = new Date();

        TextView titleText = (TextView) view.findViewById(R.id.titleText);
        TextView preDateText = (TextView) view.findViewById(R.id.preDateText);
        TextView dateText = (TextView) view.findViewById(R.id.dateText);

        Game game = games.get(position);

        if ((date.after(game.getStart_date()) || date.equals(game.getStart_date())) && date.before(game.getFinishDate())){

            preDateText.setText("до:");
            preDateText.setTextColor(Color.GREEN);
            dateText.setText(new SimpleDateFormat("dd/MM/yyyy").format(game.getFinishDate()));
        }
        else {
            if (date.after(game.getFinishDate())) {
                preDateText.setText("");
                dateText.setText("закончена ");
                dateText.setTextColor(Color.RED);

            } else {
                dateText.setText(new SimpleDateFormat("dd/MM/yyyy").format(game.getStart_date()));
                preDateText.setText("c:");
                preDateText.setTextColor(Color.BLUE);
            }
        }



        titleText.setText(game.getTitle());


        return view;
    }
}