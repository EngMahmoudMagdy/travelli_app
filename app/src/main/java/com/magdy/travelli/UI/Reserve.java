package com.magdy.travelli.UI;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.magdy.travelli.R;

public class Reserve extends FragmentActivity {
    EditText name;
    EditText email;
    EditText tele;
    EditText num_ppl;
    Button book;
    String nameData,emailData,teleData,num_pplData;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reserv);

        name = (EditText)findViewById(R.id.reserve_name);
        email = (EditText)findViewById(R.id.reserv_email);
        tele = (EditText)findViewById(R.id.reserv_phone);
        num_ppl = (EditText)findViewById(R.id.numppl);
        book = (Button)findViewById(R.id.book);
        book.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                nameData = name.getText().toString();
                emailData = email.getText().toString();
                teleData =tele.getText().toString();
                num_pplData = num_ppl.getText().toString();
                //ابقي سيف بقي الدنيا دي في الداتا بيز
                // بعد كده بقي يا مجدي ابقي اتشك اذا كان الاسم او الايميل ا التليفون موحودين قبل كده في الداتا بيز
                AlertDialog alertDialog = new AlertDialog.Builder(Reserve.this).create(); //Read Update
                alertDialog.setTitle("thanks");
                alertDialog.setMessage("Booking complete");

                alertDialog.setButton(Dialog.BUTTON_POSITIVE,"OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // here you can add functions
                    }
                });

                alertDialog.show();  //<-- See This!
            }

        });


    }
}