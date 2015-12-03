package wy.androidchat;

import android.app.Application;
import android.content.res.Configuration;

/**
 * Created by wangyao5 on 15/12/3.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        TcpThread runnable = new TcpThread(null,null,this);
        Thread t = new Thread(runnable);
        t.start();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }
}
