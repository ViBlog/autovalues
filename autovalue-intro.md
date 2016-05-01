# AutoValue
[AutoValue][AutoValueLibrary] is a Java library created by Google and released as version 1 on January 2015. His goal was to generate immutable value classes for Java 1.6 (for our dear Android developers blocked on Java 6) and to avoid to write all the boiler plate code. But what is a value object class?

## Java Value Objects

From the Oracle documentation on Java 8 we can find that a [value type class][ValueBasedClasses]:
- is final and immutable,
- implements equals, hashcode and toString that are based on instance state and not identity,
- do not use identity-sensitive equality, or hashcode and are considered equal based solely on equals() method,
- do not have accessible constructors but are instantiated through factory methods which make no commitment as to identity of returned instances,

## What's identity
Identity is what I used to name reference id. An object instance have an id that can be shared around. Whenever this object is modified, all the other variable pointing to this id will get the modifications.

In Java we can verify that two objects points are the same instance by using the operator ```==```. On the other side, the ```equals()``` is normally used for a comparison on the  object's values (as shown on the example below).

```java
@Test
public void testStringIdentity() {
    String object1 = new String("banana");
    String object2 = new String("banana");
    String object3 = object2;
    
    // Comparison on identity, object 2 has not the same reference id than object 1
    assertThat(object1 == object2).isFalse(); 
    
    // Comparison on identity, object3 has the same reference id than object2
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

But let's see what an immutable class needs to exist

## Base class
Let's start by creating a User class with his name, email and location. We have 5 Lines of code.

```java
public class User {
    public String name;
    public String email;
    public Location location;
}
```

## Immutability

We want this instance's values to be immutable so we use the ```private final``` to avoid them to be re-assigned. Same for the class that needs to not be subclass-able. We have to add a constructor

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

That's 10 lines.

### Getters
To access the instance's values we have to create getters. Let's add 9 more lines to make it 19 in total.

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
On a side note, if you return a mutable object in the getter, Java will return the reference to the object. Doing so, this object will be able to be modified breaking the advantages of the Immutability. To avoid that you will have to return a clone of the object.

```java
private Location getLocation() {
    return location.clone();
}
```

### Hashcode() & Equals()
To handle the equals by values and not by reference we have to generate the equals() and hashcode(). As with the getters, IntelliJ is a good software and let's you generate this code by using ```Ctrl+Enter``` (mac). Unfortunately you will have to never forget to delete and redo it each time you add a new variable else it will forget to compare the new ones. Not really mistake proof. Ow, and that's 22 more lines. 41 lines now...

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

### toString

# AutoValue
## Set It Up

## Simple Example
 livetemplate create
## Factory

## Builder

## Parcelable
gradle

## Gson
gradle
### SerializableName('')

livetemplate typeAdapter

  <!-- Article references -->
  [AutoValueLibrary]:https://github.com/google/auto/blob/master/value/userguide/index.md
  [ValueBasedClasses]:https://docs.oracle.com/javase/8/docs/api/java/lang/doc-files/ValueBased.html
  [EffectiveJavaBook]:https://www.amazon.ca/Effective-Java-2nd-Joshua-Bloch/dp/0321356683/ref=sr_1_1?ie=UTF8&qid=1462074150&sr=8-1&keywords=effective+java
  [ImmutableObjects]:http://www.javapractices.com/topic/TopicAction.do?Id=29
  [ImmutableObjectThreadSafeTy]:http://c2.com/cgi/wiki?ValueObjectsShouldBeImmutable
  [RHarterIntroAutoValue]:http://ryanharter.com/blog/2016/03/22/autovalue/
  [RHarterCreateExtentions]:http://ryanharter.com/blog/2016/04/08/autovalue-deep-dive/
  
  
  Sources:  
  [Immutability Oracle JavaSE](https://docs.oracle.com/javase/tutorial/essential/concurrency/immutable.html)  
  [Value Types OpenJDK](http://cr.openjdk.java.net/~jrose/values/values-0.html)  
  [Value objects Should be immutable](http://c2.com/cgi/wiki?ValueObjectsShouldBeImmutable)  
  [Immutability Java Practices](http://www.javapractices.com/topic/TopicAction.do?Id=29)  
  [Intro to Autovalue by Ryan Harter, writer of many extentions](http://ryanharter.com/blog/2016/03/22/autovalue/)  