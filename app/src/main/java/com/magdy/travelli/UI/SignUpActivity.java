package com.magdy.travelli.UI;

import android.content.DialogInterface;
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
import com.magdy.travelli.Services.RegisterRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        final EditText etusername=findViewById(R.id.name);
        final EditText etemail = findViewById(R.id.email);
        final EditText etphone = findViewById(R.id.phone);
        final EditText etpassword =findViewById(R.id.pass);
        final EditText etconfirm =findViewById(R.id.conf_pass);

        final Button bsignup=findViewById(R.id.register);
        bsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String name = etusername.getText().toString();
                final String email= etemail.getText().toString();
                final String phone= etphone.getText().toString();
                final String password = etpassword.getText().toString();
                final String confirm = etconfirm.getText().toString();
                if(name.equals("")||email.equals("")||password.equals("")||confirm.equals("")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getBaseContext());
                    builder.setMessage("please fill the required data").setNegativeButton("Retry", null).create().show();
                }else if(!password.equals(confirm)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getBaseContext());
                    builder.setMessage("your passwords doesnot match").setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            etpassword.setText("");
                            etconfirm.setText("");
                        }
                    }).create().show();

                }

                else{
                    final Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {
                                JSONObject jsonResponse = new JSONObject(response);

                                boolean  code = jsonResponse.getBoolean("success");
                                String msg = jsonResponse.getString("message");

                                if (code) {
                                    //Intent intent = new Intent(signup.this, MainActivity.class);
                                    // signup.this.startActivity(intent);
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getBaseContext());
                                    builder.setMessage(msg).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();

                                        }
                                    }).create().show();



                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getBaseContext());
                                    builder.setMessage(msg).setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            etusername.setText("");
                                            etemail.setText("");
                                            etphone.setText("");
                                            etpassword.setText("");
                                            etconfirm.setText("");
                                        }
                                    }).create().show();

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    };

                    RegisterRequest registerRequest = new RegisterRequest(name, email, phone, password, responseListener);
                    MyApp.getInstance().addToRequestQueue(registerRequest);

                }


            }
        });

    }
}
