package com.magdy.travelli.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.magdy.travelli.R;


public class MessageAlert {

    private Dialog dialog;

    public void dismiss() {
        if (dialog != null)
            dialog.dismiss();
    }

    private TextView text, textBody, positiveButton, negativeButton;
    private ImageView imageView;

    public MessageAlert(Activity activity) {
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.sucess);
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        text = dialog.findViewById(R.id.messageTitle);
        textBody = dialog.findViewById(R.id.messageBody);
        imageView = dialog.findViewById(R.id.messageImage);
        positiveButton = dialog.findViewById(R.id.messagePositiveButton);
        negativeButton = dialog.findViewById(R.id.messageNegativeButton);
        negativeButton.setVisibility(View.GONE);
    }

    public void setPositiveButton(String positiveString, View.OnClickListener onClickListener) {
        positiveButton.setText(positiveString);
        positiveButton.setOnClickListener(onClickListener);
    }

    public void setNegativeButton(String negativeString, View.OnClickListener onClickListener) {
        negativeButton.setText(negativeString);
        if (onClickListener == null)
            negativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        else
            negativeButton.setOnClickListener(onClickListener);
        negativeButton.setVisibility(View.VISIBLE);
    }

    public void showDialog(String msgTitle, String msgBody, int imageResId) {


        text.setText(msgTitle);
        textBody.setText(msgBody);
        imageView.setImageResource(imageResId);
        dialog.show();
    }

    public void showDialog(String msgTitle, String msgBody, int imageResId, String positiveString, View.OnClickListener onClickListener) {

        text.setText(msgTitle);
        textBody.setText(msgBody);
        imageView.setImageResource(imageResId);
        positiveButton.setText(positiveString);
        positiveButton.setOnClickListener(onClickListener);

        dialog.show();
    }

    public void showDialog(String msgTitle, String msgBody,
                           int imageResId,
                           String positiveString,
                           View.OnClickListener onPositiveClickListener,
                           String negativeString,
                           View.OnClickListener onNegativeClickListener) {


        negativeButton.setVisibility(View.VISIBLE);

        text.setText(msgTitle);
        textBody.setText(msgBody);
        imageView.setImageResource(imageResId);
        positiveButton.setOnClickListener(onPositiveClickListener);
        positiveButton.setText(positiveString);
        if (onNegativeClickListener == null)
            negativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        else
            negativeButton.setOnClickListener(onNegativeClickListener);
        negativeButton.setText(negativeString);

        dialog.show();
    }


    public Dialog getDialog() {
        return dialog;
    }
}
