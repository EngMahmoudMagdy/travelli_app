package com.magdy.travelli.UI;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.magdy.travelli.R;
import com.magdy.travelli.Services.LoginRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class SignInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        final EditText etlmailorphone=findViewById(R.id.emailText);
        final EditText etlpassword=findViewById(R.id.passwordText);
        final Button login=findViewById(R.id.login);
        final Button signuplink =findViewById(R.id.signup);
        signuplink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent signupIntent =new Intent(getBaseContext(),SignUpActivity.class);
                startActivity(signupIntent);

            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String mailorphone = etlmailorphone.getText().toString();
                final String password =etlpassword.getText().toString();

                /*Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonresponse = new JSONObject(response);
                            boolean success = jsonresponse.getBoolean("success");

                            if(success){
                                String name=jsonresponse.getString("name");
                                String userID=jsonresponse.getString("id");
                                String email =jsonresponse.getString("email");
                                String phone =jsonresponse.getString("phone_num");

                                Intent intent=new Intent(getBaseContext(),SignInActivity.class);
                                intent.putExtra("name",name);
                                intent.putExtra("id",userID);
                                intent.putExtra("email",email);
                                intent.putExtra("phone_num",phone);
                                startActivity(intent);

                            }else{
                                etlmailorphone.setText("");
                                etlpassword.setText("");
                                AlertDialog.Builder builder = new AlertDialog.Builder(getBaseContext());
                                builder.setMessage("Login failed").setNegativeButton("Retry", null).create().show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                LoginRequest loginRequest=new LoginRequest(mailorphone,password,responseListener);
                RequestQueue queue = Volley.newRequestQueue(getBaseContext());
                queue.add(loginRequest);*/
        }
        });
    }
}