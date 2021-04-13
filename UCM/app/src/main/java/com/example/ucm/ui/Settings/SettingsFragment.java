package com.example.ucm.ui.Settings;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.ucm.LoginPage;
import com.example.ucm.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class SettingsFragment extends Fragment {
    ImageButton imageButton;
    Uri link;
    public FirebaseUser user;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private SettingsModel slideshowViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                new ViewModelProvider(this).get(SettingsModel.class);
        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        imageButton = root.findViewById(R.id.imageButton);
        imageButton.setClipToOutline(true);
        return root;
    }


    public void logout(View view) {
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        fAuth.signOut();
        Intent i = new Intent(getContext(), LoginPage.class);
        startActivity(i, ActivityOptions.makeSceneTransitionAnimation((Activity) getContext()).toBundle());
    }



}