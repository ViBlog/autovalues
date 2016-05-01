package eu.dubedout.vincent.autovalues.objects;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class User {
    public abstract String name();
    public abstract String email();
    public abstract Location location();

    public static User create(String name, String email, Location location) {
        return new AutoValue_User(name, email, location);
    }
}
