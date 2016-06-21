package eu.dubedout.vincent.autovalues.gsonpost;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

@AutoValue
public abstract class UserDetailAutoValued {
    public abstract String picture();
    public abstract int age();
    public abstract EyeColor eyeColor();
    public abstract String name();
    public abstract String gender();
    public abstract String company();
    public abstract String email();
    public abstract String phone();
    public abstract String address();
    public abstract String about();

    public static TypeAdapter<UserDetailAutoValued> typeAdapter(Gson gson) {
        return new AutoValue_UserDetailAutoValued.GsonTypeAdapter(gson);
    }
}
