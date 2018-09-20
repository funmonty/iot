package com.anoram.homecontrol;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    Button login ;
    String username,password;
    EditText username_edit,password_edit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        login = (Button) findViewById(R.id.login_button);
        username_edit = (EditText) findViewById(R.id.username);
        password_edit = (EditText) findViewById(R.id.password);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                username = username_edit.getText().toString();
                password = password_edit.getText().toString();
                Intent myIntent = new Intent(LoginActivity.this, NavBarActivity.class);

                
                if(username.equalsIgnoreCase("smart") && password.equalsIgnoreCase("admin"))
                {
                    //Intent myIntent = new Intent(LoginActivity.this, NavBarActivity.class);

                    LoginActivity.this.startActivity(myIntent);

                }
                else
                {
                    Toast.makeText(LoginActivity.this, "Invalid Username or Password",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
