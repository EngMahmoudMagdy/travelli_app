package com.magdy.travelli.UI;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatRatingBar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.magdy.travelli.Data.Tour;
import com.magdy.travelli.R;

/**
 * Created by engma on 11/18/2017.
 */

public class ReviewingFragment extends DialogFragment {

    EditText editText ;
    AppCompatRatingBar bar ;
    int rate = 1 ;
    Tour tour;
    public static ReviewingFragment newInstance(Tour tour)
    {
        ReviewingFragment fragment = new ReviewingFragment();
        fragment.tour = tour;
        return  fragment;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View custom = inflater.inflate(R.layout.fragment_reviewing, null);
        editText = custom.findViewById(R.id.review_edit);
        bar=  custom.findViewById(R.id.rate);
        if (savedInstanceState!=null)
        {
            rate=savedInstanceState.getInt("r");
        }
        bar.setRating(rate);
        bar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                rate = (int) v;
            }
        });
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                enterReview();
                return true;
            }
        });

        builder.setView(custom);
        builder.setMessage(getString(R.string.enter_review));
        builder.setPositiveButton(getString(R.string.confirrm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                enterReview();
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), null);

        Dialog dialog = builder.create();
        Window window = dialog.getWindow();
        if (window != null) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
        return dialog;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("r",rate);
    }

    void enterReview()
    {
            Toast.makeText(getActivity().getBaseContext(),getString(R.string.review_submit),Toast.LENGTH_SHORT).show();
            dismiss();
    }
}
