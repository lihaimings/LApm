package com.opt.lapm;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.opt.android_startup.time.AppMethodBeat;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppMethodBeat.attachBaseContext();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        AppMethodBeat.at(this, hasFocus);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
