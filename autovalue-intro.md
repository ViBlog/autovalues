Written for the blog [dubedout.eu](http://dubedout.eu), let me know if you want to use this material.

# AutoValue
[AutoValue][AutoValueLibrary] is a Java library created by Google and released as version 1.0 on January 2015. His goal is to generate immutable value classes, backwards compatible with Java 1.6, and to avoid to write all the boiler plate code. But what is a value object class?

## Java Value Objects

From the Oracle documentation on Java 8 we can find that a [value type class][ValueBasedClasses]:
- is final and immutable,
- implements equals, hashcode and toString that are based on instance state and not identity,
- do not use identity-sensitive equality, or hashcode and are considered equal based solely on equals() method,
- do not have accessible constructors but are instantiated through factory methods which make no commitment as to identity of returned instances,

## What's identity
Object identity is the instance id or reference. When you write ```someObject = originObject```, you are sharing the instance of originObject in someObject. If you modify someObject, it's actually originObject that you are modifying (does not apply for primitives).

In Java we can verify that two objects are the same instance by using the operator ```==```. On the other side, the ```equals()``` is normally used for a comparison on the  object's values (as shown on the example below).

```java
@Test
public void testStringIdentity() {
    String object1 = new String("banana");
    String object2 = new String("banana");
    String object3 = object2;
    
    // Comparison on identity, object 2 has not the same reference id than object 1
    assertThat(object1 == object2).isFalse(); 
    
    // Comparison on identity, object3 has the same reference id than object 2
    assertThat(object2 == object3).isTrue(); 
    
    // Comparison on value, this assertion is valid too
    assertThat(object1.equals(object2)).isTrue(); 
}
```

## Why using immutability

>*"Classes should be immutable unless there's a very good reason to make them mutable....If a class cannot be made immutable, limit its mutability as much as possible."* - Joshua Bloch in [Effective Java][EffectiveJavaBook]

So let's do it... Well, not so fast, why we should do this?

[Immutable objects][ImmutableObjects] simplify a lot our programming headaches :
- they are simple to construct, test, and use
- they are thread safe
- they don't need a copy constructor
- they don't need an implementation of clone
- they allow hashCode to use lazy initialization, and cache its return value as it will never change
- make good Map keys and Set elements (these objects must not change state while in the collection)

As Martin Fowler show us with his [little example below][ImmutableObjectThreadSafeTy], thread safety is a serious problem that can be avoided simply by using immutable objects.  

<!-- Put back ```java when done -->
```
task1.setStartDate(new Date("1 Jan 98");
task2.setStartDate(task1.getTaskDate());
//then somewhere in the task class
void delay(int delayDays) {
_startDate.setDate(_startDate.getDate() + delayDays);

// then somewhere 
task2.delay(5);
// now you find task1's start date has changed
```

# Writing an immutable class is (not) fun
 *Ryan Harter, one of the contributors of the AutoValue library have written good posts on [introducing AutoValue][RHarterIntroAutoValue] and [creating extensions][RHarterCreateExtentions] for this library that you have to read if you want to learn more about it.*

But let's see what an immutable class needs for existing.

## Base class
Let's start by creating a User class with his name, email and location: 5 Lines of code.

```java
public class User {
    public String name;
    public String email;
    public Location location;
}
```

## Immutability

We want instance's values to be immutable. We use an access modifier ```private final``` to avoid the objects to be re-assigned. Same for the class that needs not to be subclass-able. Finally, we have to add a constructor.

```java
public final class User {
    private final String name;
    private final String email;
    private final Location location;

    public User(String name, String email, Location location) {
        this.name = name;
        this.email = email;
        this.location = location;
    }
}
```

## Getters
To access the instance's values we have to create getters. 

```java
[...]
private String getName() {
    return name;
}

private String getEmail() {
    return email;
}

private Location getLocation() {
    return location;
}
[...]
```

On a side note, if you return a Mutable Object in the getter, Java will return his reference. Doing so, you will be able to be modify it breaking the advantages of the Immutability. To avoid that you will have to return a clone of the object. You should also take care of the memory cost of doing so. Not much more choices there, choose carefully.

```java
private Location getLocation() {
    return location.clone();
}
```

## Hashcode() & Equals()
To handle the equals by values and not by reference we have to generate the equals() and hashcode(). As with the getters, IntelliJ is a good software and let's you generate this code by using ```Ctrl+Enter``` (mac). Unfortunately you will have to never forget to delete and redo it each time you add a new variable. Not really mistake proof. Ow, and that's 22 more lines. 41 lines now...

```java
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
```

## Parcelable
Welcome in Android world, if you want to share your object through an ```Intent``` by example you will have to implement the parcelable class. BOOM 29 more lines -> 70 lines written / generated.

```java
@Override
public int describeContents() {
    return 0;
}

@Override
public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.name);
    dest.writeString(this.email);
    dest.writeParcelable(this.location, flags);
}

protected User(Parcel in) {
    this.name = in.readString();
    this.email = in.readString();
    this.location = in.readParcelable(Location.class.getClassLoader());
}

public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
    @Override
    public User createFromParcel(Parcel source) {
        return new User(source);
    }

    @Override
    public User[] newArray(int size) {
        return new User[size];
    }
};
```

## toString
For aesthetics, we can add a bit more code. ```toString()``` methods exists to display a human readable object for your logs or debugging session by example. What's the score now? 78 lines. Not that bad for only one object and three fields.

```java
@Override
public String toString() {
    return "User{" +
            "name='" + name + '\'' +
            ", email='" + email + '\'' +
            ", location=" + location +
            '}';
}
```
## Half way conclusion
We had to write and generate 78 lines to create a Three Fields Value Object. We will have to take care to modify 30% of the code if we add or delete a new variable. It's a mistake that can cause problems and are difficult to debug because we tend to slip over those methods without really reading them.

On a side note, we can create this kind of class easily in kotlin by using a [data class][kotlinDataClass]. (Parcelable will not be part of the deal though)

```
data class User(val name: String, val email: String, val location: Location)
```

# AutoValue
As said in the begginning, Google saved our sanity by creating this library with the help of some collaborators. A very good job have been done on the documentation. It's the [best documented library][autoValueDoc] I have ever used.

## Setting It Up
You will need to add the APT processor on your main build.gradle.

```Project:Autovalues - build.gradle```
```
dependencies {
    classpath 'com.android.tools.build:gradle:2.1.0'
    classpath 'com.neenbedankt.gradle.plugins:android-apt:1.8'
```

Then add the library to your app/build.gradle. Take care to add the auto-value-annotations from Jake Wharton to avoid leaking the whole annotation processors into our classpath. 

```Module:app - build.gradle```
```
apply plugin: 'com.neenbedankt.android-apt'

dependencies {
  apt 'com.google.auto.value:auto-value:1.2'
  provided 'com.jakewharton.auto.value:auto-value-annotations:1.2-update1'
}
```

## Simple Example
So this is what we have to write to have the same class as above (without the Parcelable). 9 lines... I trully think that's awesome. 

```java
@AutoValue
public abstract class User {
    public abstract String name();
    public abstract String email();
    public abstract Location location();

    public static User create(String name, String email, Location location) {
        return new AutoValue_User(name, email, location);
    }
}
```

As the time of writting this, I'm working on a new project for the Montreal Agency [Tractr][tractr] and I have created dozens of those objects for the API Client and it saved me a lot of time and headaches using immutability.

### Create object live template
When creating those object I often had trouble while writing the creator because the AutoValue_class was not yet generated. To avoid typos, I created this little live template to help. If you find a way to add the abstract methods directly in the constructor, let me know.

```java
public static $class$ create($parameters$) {
    return new AutoValue_$class$($createparameters$);
}
```
![Live template to create object][liveTemplateCreate]

## Parcelable
To have exactly the same object as before we need to add the parcelable code. Fortunately for us, an extension have been created that does exactly that. Add it to your gradle build file.

```Module:app - build.gradle```
```
dependencies {
  [...]
  apt 'com.ryanharter.auto.value:auto-value-parcel:0.2.1'
  [...]
}
```

Make the User implement Parcelable and here we go, our class generated will handle it. No more code needed.
```java 
public abstract class User implements Parcelable { 
  [...] 
}
```

  <!-- Article references -->
  [AutoValueLibrary]:https://github.com/google/auto/blob/master/value/userguide/index.md
  [ValueBasedClasses]:https://docs.oracle.com/javase/8/docs/api/java/lang/doc-files/ValueBased.html
  [EffectiveJavaBook]:https://www.amazon.ca/Effective-Java-2nd-Joshua-Bloch/dp/0321356683/ref=sr_1_1?ie=UTF8&qid=1462074150&sr=8-1&keywords=effective+java
  [ImmutableObjects]:http://www.javapractices.com/topic/TopicAction.do?Id=29
  [ImmutableObjectThreadSafeTy]:http://c2.com/cgi/wiki?ValueObjectsShouldBeImmutable
  [RHarterIntroAutoValue]:http://ryanharter.com/blog/2016/03/22/autovalue/
  [RHarterCreateExtentions]:http://ryanharter.com/blog/2016/04/08/autovalue-deep-dive/
  [kotlinDataClass]:https://kotlinlang.org/docs/reference/data-classes.html
  [autoValueDoc]:https://github.com/google/auto/blob/master/value/userguide/index.md
  [tractr]:http://tractr.net/
  
  <!-- Images -->
  [liveTemplateCreate]:images/liveTemplateCreate.png
  
  Sources:  
  [Immutability Oracle JavaSE](https://docs.oracle.com/javase/tutorial/essential/concurrency/immutable.html)  
  [Value Types OpenJDK](http://cr.openjdk.java.net/~jrose/values/values-0.html)  
  [Value objects Should be immutable](http://c2.com/cgi/wiki?ValueObjectsShouldBeImmutable)  
  [Immutability Java Practices](http://www.javapractices.com/topic/TopicAction.do?Id=29)  
  [Intro to Autovalue by Ryan Harter, writer of many extentions](http://ryanharter.com/blog/2016/03/22/autovalue/)  