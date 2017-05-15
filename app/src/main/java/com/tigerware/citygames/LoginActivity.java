package com.tigerware.citygames;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;

import com.tigerware.citygames.Entity.User;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    private static String filename = "users";
    private EditText loginET;
    private EditText passwordET;
    private EditText emailET;
    private ArrayList<User> userArrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        LoadUserList();
        if(userArrayList == null){
            /////////////////////////////
            User user = new User();
            user.setEmail("some@mail.com");
            user.setId(1);
            user.setUsername("test");
            user.setHash("12345".hashCode());

            userArrayList = new ArrayList<User>();
            userArrayList.add(user);
            SaveUserList();
            ////////////////////////////
            LoadUserList();
        }
        loginET = (EditText)findViewById(R.id.loginEditText);
        passwordET = (EditText)findViewById(R.id.passwordEditText);
        emailET = (EditText)findViewById(R.id.emailEditText);
    }
    private  void SaveUserList(){
        try {
            FileOutputStream fileOutputStream = getApplicationContext().openFileOutput(filename, Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(userArrayList);
            objectOutputStream.close();
            fileOutputStream.close();
        }
        catch (IOException e){

        }
    }
    private void LoadUserList(){
        try{
            FileInputStream fileInputStream = getApplicationContext().openFileInput(filename);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            userArrayList = (ArrayList<User>) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
        }
        catch (IOException e) {
        }
        catch (ClassNotFoundException e){
        }
    }

    public void onSignInClick(View view){
        String login = loginET.getText().toString();
        String password = passwordET.getText().toString();
        int hash = password.hashCode();
        for (User user:userArrayList) {
            if(user.getUsername().equals(login) && user.getHash() == hash) {
                GoToGameAsUser(user);
                return;
            }
        }
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("Неверный логин или пароль.");
        alertDialog.setTitle("City Games");
        alertDialog.setCancelable(true);
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialog.create().show();
    }

    private String VerifyNewUser(String login, String email, String password){
        if(!login.equals("")){
            if(!password.equals("")){
                if(!email.equals("")){
                    if(Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        int hash = password.hashCode();
                        for (User user : userArrayList) {
                            if (user.getUsername().equals(login)) {
                                return "Такой логин уже используется";
                            } else {
                                if (user.getEmail().equals(email)) {
                                    return "Такой email уже используется";
                                }


                            }
                        }
                    }
                    else{
                        return "Введите действительный email";
                    }
                }
                else{
                    return "Введите ваш email";
                }
            }
            else{
                return "Введите ваш пароль";
            }
        }
        else{
            return "Введите ваш логин";
        }
        return "";
    }

    private  void GoToGameAsUser(User user){
        final Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("User", user);
        startActivity(intent);
    }

    public void onRegisterClick(View view){
        String login = loginET.getText().toString();
        String password = passwordET.getText().toString();
        String email = emailET.getText().toString();
        String error = VerifyNewUser(login, email, password);

        if(error.equals("")){
            User user = new User();
            user.setHash(password.hashCode());
            user.setUsername(login);
            user.setEmail(email);
            user.setId(userArrayList.size()+1);
            userArrayList.add(user);
            SaveUserList();
            GoToGameAsUser(user);
        }
        else {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setMessage(error);
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
