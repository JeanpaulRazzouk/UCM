package com.example.ucm.ui.Settings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SettingsModel extends ViewModel {

    private MutableLiveData<String> mText;

    public SettingsModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Settings fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}