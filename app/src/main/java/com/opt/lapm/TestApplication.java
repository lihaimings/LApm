package com.opt.lapm;

import android.app.Application;
import android.content.Context;

import com.opt.android_startup.time.AppMethodBeat;

public class TestApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        AppMethodBeat.attachBaseContext();
    }

}
