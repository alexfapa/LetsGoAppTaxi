package br.com.tutorialandroid.splashscreen.HoraDeTrabalho;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import br.com.tutorialandroid.splashscreen.MainMapActivity;
import br.com.tutorialandroid.splashscreen.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;


public class MainHoraDeTrabalho extends AppCompatActivity {
    private static final String PREF_ = "LoginActivityPreferences";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_hora_de_trabalho);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // VERIFY SHAREDPREFERENCES
        SharedPreferences sp = getSharedPreferences(PREF_, MODE_PRIVATE);
        String login = sp.getString("login", "");
        String password = sp.getString("password", "");

        if(login.equals("") && password.equals("")){
            Intent intent = new Intent(MainHoraDeTrabalho.this, MainMapActivity.class);
            startActivity(intent);
        }

    }

    public void signIn(View view){
        EditText etLogin = (EditText) findViewById(R.id.editText2);
        EditText etPassword = (EditText) findViewById(R.id.editText3);
        CheckBox saveLogin = (CheckBox) findViewById(R.id.checkBox);

        String login = etLogin.getText().toString();
        String password = etPassword.getText().toString();

        if(login.equals("") && password.equals("")){

            // VERIFY CHECKBOX FOR SHAREDPREFERENCES
            if(saveLogin.isChecked()){
                SharedPreferences sp = getSharedPreferences(PREF_, MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();

                editor.putString("login", login);
                editor.putString("password", password);
                editor.commit();
            }

            Intent intent = new Intent(MainHoraDeTrabalho.this, MainMapActivity.class);
            startActivity(intent);
        }
        else{
            Toast.makeText(MainHoraDeTrabalho.this, "Dados errados!", Toast.LENGTH_SHORT).show();
        }
    }

}
