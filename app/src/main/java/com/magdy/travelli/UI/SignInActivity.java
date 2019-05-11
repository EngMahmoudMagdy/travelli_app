package com.magdy.travelli.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.magdy.travelli.R;
import com.magdy.travelli.helpers.StaticMembers;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.magdy.travelli.helpers.StaticMembers.TOKEN;

public class SignInActivity extends AppCompatActivity {

    @BindView(R.id.email)
    TextInputEditText emailText;
    @BindView(R.id.emailLayout)
    TextInputLayout emailLayout;
    @BindView(R.id.password)
    TextInputEditText passwordText;
    @BindView(R.id.passwordLayout)
    TextInputLayout passwordLayout;
    @BindView(R.id.progress)
    RelativeLayout progress;
    String instanceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);
        final Button login = findViewById(R.id.login);
        final Button signuplink = findViewById(R.id.signup);
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                instanceId = instanceIdResult.getToken();
            }
        });
        if (instanceId == null)
            instanceId = FirebaseInstanceId.getInstance().getToken();
        else if (instanceId.isEmpty())
            instanceId = FirebaseInstanceId.getInstance().getToken();

        signuplink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), SignUpActivity.class));
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StaticMembers.CheckTextInputEditText(emailText, emailLayout, "Email is empty") &&
                        StaticMembers.CheckTextInputEditText(passwordText, passwordLayout, "Password is empty")) {
                    progress.setVisibility(View.VISIBLE);
                    final String email = emailText.getText().toString();
                    final String password = passwordText.getText().toString();
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                changeToken(instanceId);
                            } else {
                                progress.setVisibility(View.GONE);
                                StaticMembers.toastMessageShort(getBaseContext(), task.getException() != null ?
                                        task.getException().getLocalizedMessage() : getString(R.string.connection_error));
                            }
                        }
                    });
                }
            }
        });
    }

    void changeToken(String token) {
        if (FirebaseAuth.getInstance().getUid() != null)
            FirebaseDatabase.getInstance().getReference(StaticMembers.USERS).child(FirebaseAuth.getInstance().getUid())
                    .child(TOKEN).setValue(token).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    StaticMembers.startActivityOverAll(SignInActivity.this, MainActivity.class);
                    progress.setVisibility(View.GONE);
                }
            });
        else {
            progress.setVisibility(View.GONE);
            StaticMembers.startActivityOverAll(SignInActivity.this, MainActivity.class);
        }
    }
}