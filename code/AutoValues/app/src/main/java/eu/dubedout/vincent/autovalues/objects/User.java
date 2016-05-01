package eu.dubedout.vincent.autovalues.objects;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class User implements Parcelable{
    public abstract String name();
    public abstract String email();
    public abstract Location location();

    public static User create(String name, String email, Location location) {
        return new AutoValue_User(name, email, location);
    }
}
