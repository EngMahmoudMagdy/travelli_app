package com.magdy.travelli.UI;

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
import com.magdy.travelli.Data.User;
import com.magdy.travelli.R;
import com.magdy.travelli.helpers.PrefManager;
import com.magdy.travelli.helpers.StaticMembers;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignUpActivity extends AppCompatActivity {

    @BindView(R.id.email)
    TextInputEditText emailText;
    @BindView(R.id.emailLayout)
    TextInputLayout emailLayout;
    @BindView(R.id.name)
    TextInputEditText nameText;
    @BindView(R.id.nameLayout)
    TextInputLayout nameLayout;
    @BindView(R.id.password)
    TextInputEditText passwordText;
    @BindView(R.id.passwordLayout)
    TextInputLayout passwordLayout;
    @BindView(R.id.confirmPassword)
    TextInputEditText confirmPassword;
    @BindView(R.id.confirmPasswordLayout)
    TextInputLayout confirmPasswordLayout;
    @BindView(R.id.progress)
    RelativeLayout progress;
    String instanceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
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

        final Button signup = findViewById(R.id.register);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StaticMembers.CheckTextInputEditText(emailText, emailLayout, getString(R.string.email_empty)) &&
                        StaticMembers.CheckTextInputEditText(nameText, nameLayout, getString(R.string.full_name_empty)) &&
                        StaticMembers.CheckTextInputEditText(passwordText, passwordLayout, getString(R.string.password_empty)) &&
                        StaticMembers.CheckTextInputEditText(confirmPassword, confirmPasswordLayout, getString(R.string.confirm_password_empty))) {
                    final String name = nameText.getText().toString();
                    final String email = emailText.getText().toString();
                    final String password = passwordText.getText().toString();
                    final String confirm = confirmPassword.getText().toString();
                    if (!password.equals(confirm)) {
                        StaticMembers.toastMessageShort(getBaseContext(), R.string.password_doesnt_match);
                        return;
                    }
                    progress.setVisibility(View.VISIBLE);
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                User user = new User(name, email, instanceId);
                                PrefManager.getInstance(getBaseContext()).setObject(StaticMembers.USER, user);
                                signUp(user);
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

    void signUp(User user) {
        if (FirebaseAuth.getInstance().getUid() != null)
            FirebaseDatabase.getInstance().getReference(StaticMembers.USERS).child(FirebaseAuth.getInstance().getUid())
                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    StaticMembers.startActivityOverAll(SignUpActivity.this, MainActivity.class);
                    progress.setVisibility(View.GONE);
                }
            });
        else {
            progress.setVisibility(View.GONE);
            StaticMembers.startActivityOverAll(SignUpActivity.this, MainActivity.class);
        }
    }
}
