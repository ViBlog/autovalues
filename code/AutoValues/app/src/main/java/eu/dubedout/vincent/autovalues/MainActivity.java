package eu.dubedout.vincent.autovalues;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

public class MainActivity extends AppCompatActivity {
    private String TAG = "AutoValueApp";
    private TextView textView;
    private EditText loopNumberEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.activity_main_text);
        loopNumberEdit = (EditText) findViewById(R.id.activity_main_edit_loop_number);
        Button button = (Button) findViewById(R.id.activity_main_start_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        startGsonTest();
                    }
                }).start();
            }
        });
    }

    public void startGsonTest() {
        int loopNumber = Integer.parseInt(loopNumberEdit.getText().toString());

        final StringBuilder results = new StringBuilder();

        long durationWithoutAutoValue = getDeserializationWithoutAutoValueDuration(loopNumber);
        results.append("Without factory:"+durationWithoutAutoValue+"ms ");

        long durationAutoValued = getDeserializationAutoValuedDuration(loopNumber);
        results.append("| With factory:"+durationAutoValued+"ms ");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(results.toString());
            }
        });
    }

    private long getDeserializationWithoutAutoValueDuration(int loopNumber) {
        Log.d(TAG, "onCreate: start basic deserialization");

        long startTimer = System.currentTimeMillis();

        Gson gson = new GsonBuilder().create();
        Type type = new TypeToken<ArrayList<UserDetailWithoutAutoValue>>() {}.getType();
        for (int i = 0; i < loopNumber; i++) {
            List<UserDetailWithoutAutoValue> userDetail = gson.fromJson(DummyJsonProvider.DUMMY_JSON, type);
        }

        long totalDuration = System.currentTimeMillis() - startTimer;

        Log.d(TAG, "onCreate: end basic deserialization. Duration="+totalDuration);

        return totalDuration;
    }

    private long getDeserializationAutoValuedDuration(int loopNumber) {
        Log.d(TAG, "onCreate: start autovalue deserialization");

        long startTimer = System.currentTimeMillis();

        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(new AutoValueGsonTypeAdapterFactory())
                .create();
        Type type = new TypeToken<ArrayList<UserDetailAutoValued>>() {}.getType();
        for (int i = 0; i < loopNumber; i++) {
            List<UserDetailAutoValued> userDetail = gson.fromJson(DummyJsonProvider.DUMMY_JSON, type);
        }

        long totalDuration = System.currentTimeMillis() - startTimer;

        Log.d(TAG, "onCreate: end autovalue deserialization. Duration="+totalDuration);

        return totalDuration;
    }
}
