package eu.dubedout.vincent.autovalues.objects;

public final class User {
    private final String name;
    private final String email;
    private final Location location;

    public User(String name, String email, Location location) {
        this.name = name;
        this.email = email;
        this.location = location;
    }

    private String getName() {
        return name;
    }

    private String getEmail() {
        return email;
    }

    private Location getLocation() {
        return location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (name != null ? !name.equals(user.name) : user.name != null) return false;
        if (email != null ? !email.equals(user.email) : user.email != null) return false;
        if (location != null ? !location.equals(user.location) : user.location != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        return result;
    }


}
