package com.magicbox.dot;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.magicbox.dot.utils.DateUtils;

import java.util.Date;

public class LoginActivity extends AppCompatActivity {

    private static final String GRAPH_PATH = "me/permissions";
    private static final String SUCCESS = "success";

    private static final int PICK_PERMS_REQUEST = 0;

    private CallbackManager callbackManager;

    private ProfilePictureView profilePictureView;
    private TextView userNameView;
    private LoginButton fbLoginButton;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        callbackManager = CallbackManager.Factory.create();

        fbLoginButton = (LoginButton) findViewById(R.id._fb_login);
        profilePictureView = (ProfilePictureView) findViewById(R.id.user_pic);
        profilePictureView.setCropped(true);

        userNameView = (TextView) findViewById(R.id.user_name);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {

                //loginResult.getAccessToken().
                //updateUI();

                AuthCredential credential = FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken());

                mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            Toast.makeText(LoginActivity.this, "Falha no login!",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                        }
                    });
            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginActivity.this, R.string.cancel, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(final FacebookException exception) {
                Toast.makeText(LoginActivity.this, R.string.error, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    protected void onActivityResult(
            final int requestCode,
            final int resultCode,
            final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);
    }



    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            mDatabase.child("users").child(currentUser.getUid()).child("ultimoLogin").setValue(DateUtils.dataParaString(new Date()));

            Intent selectPermsIntent = new Intent(LoginActivity.this, PrincipalActivity.class);
            startActivity(selectPermsIntent);

        } else {
            profilePictureView.setProfileId(null);
            userNameView.setText(getString(R.string.welcome));
        }
    }



}

