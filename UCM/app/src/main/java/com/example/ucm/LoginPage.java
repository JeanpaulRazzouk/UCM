package com.example.ucm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.ucm.Fragments.Home;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginPage extends AppCompatActivity {
VideoView videoView;
EditText editText,editText2;
ImageButton imageButton;
private FirebaseAuth mAuth;
    FirebaseUser user ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // //
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null ) {
            boolean emailVerified = user.isEmailVerified();
            if (emailVerified == true) {
                // User is signed in
                Intent i = new Intent(LoginPage.this, HomePage.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        } else {

        }
        setContentView(R.layout.activity_login_page);
        editText = findViewById(R.id.editText);
        editText2 = findViewById(R.id.editText2);
        videoView = findViewById(R.id.videoView);
        imageButton = findViewById(R.id.imageButton);
        //
        Intro_Video();
        //
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoView.setBackgroundColor(Color.TRANSPARENT);
            }
        });

        //
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (editText.getText().toString().isEmpty() && editText2.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), " Please fill empty fields. ", Toast.LENGTH_LONG).show();
                }
                else if (editText2.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), " Please fill Password field. ", Toast.LENGTH_LONG).show();
                }
                else if (editText.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), " Please fill Email field. ", Toast.LENGTH_LONG).show();
                }
                else{
                    Sign_In();
                }
            }
        });
        //
    }



    public void Sign_In(){
         String val_username = editText.getText().toString();
         String val_password = editText2.getText().toString();
//
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(val_username, val_password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(LoginPage.this, "Authentication Success.",
                                    Toast.LENGTH_SHORT).show();
                            //
                            Intent i = new Intent(LoginPage.this, HomePage.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            //
                        } else {
                            Toast.makeText(LoginPage.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void Intro_Video(){
        videoView = findViewById(R.id.videoView);
        Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.test);
        videoView.setVideoURI(uri);
        videoView.start();
    }
}