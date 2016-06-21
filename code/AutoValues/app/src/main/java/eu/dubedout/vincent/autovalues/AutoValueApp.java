package eu.dubedout.vincent.autovalues;

import android.app.Application;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.ryanharter.auto.value.gson.AutoValueGsonTypeAdapterFactory;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import eu.dubedout.vincent.autovalues.gsonpost.DummyJsonProvider;
import eu.dubedout.vincent.autovalues.gsonpost.UserDetailAutoValued;
import eu.dubedout.vincent.autovalues.gsonpost.UserDetailWithoutAutoValue;

public class AutoValueApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();


    }
}
