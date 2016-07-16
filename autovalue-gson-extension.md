# Divide by two your JSON parsing time

Few years ago, when Android started we had to parse JSON APIs by hand, it wasn't as worse as parsing the XML but it was a boring and long task. Then some (De)Serializers were created or imported from java to Android: Gson, Jackson... Most of them were using reflection to transform JSON into an object and vice-versa... No more boring task or code to change everywhere when something changed in the API but at the cost of performance. Reflection is a heavy process and if you use it to parse numerous files it can slow your app. So we had to consider performance cost vs time to code.  

>"Performance cost" versus "time to code" is not a choice we have to do anymore.

As seen previously, [AutoValue][autovalue-dubedout] is a library written by Google that helps us to avoid writing all the boilerplate of value objects. The great thing is that it's possible to create extensions for it and [AutoValue-Gson from Ryan Harter][auto-value-gson] is a great example on how to simplify your developer's life. It will generate for you the custom type adapters you need.

It means that you can use the power of your JSON serializers without impact in your app performance, while staying very simple to use. 

# Setup your build.gradle
First, you need to setup your build.gradle files.

root/build.gradle
```
buildscript {
  dependencies {
    //[...] 
    classpath 'com.neenbedankt.gradle.plugins:android-apt:1.8'
  }
}
```

root/app/build.gradle 
```
dependencies {
  apt 'com.google.auto.value:auto-value:1.2'
  provided 'com.jakewharton.auto.value:auto-value-annotations:1.2-update1'
  apt 'com.ryanharter.auto.value:auto-value-gson:0.3.1'
}
```

If you encounter ```java.lang.NoSuchMethodError: com.squareup.javapoet.TypeName.isBoxedPrimitive()Z``` while compiling. It means that gradle is not able to resolve the correct JavaPoet version. Add ```apt 'com.squareup:javapoet:1.7.0'``` before your Dagger apt and it should solve it. 

# JSON file
To be able to test, I've created data we will play with. You can do the same by using the website http://www.json-generator.com. It's an array with 50 objects. 

```java
public class DummyJsonProvider {
    public static String DUMMY_JSON = "[\n" +
            "  {\n" +
            "    \"_id\": \"576869ed835bc5884bf7179e\",\n" +
            "    \"index\": 0,\n" +
            "    \"guid\": \"5f4b447c-4911-4ee2-bdc4-8d71c7f2a6d4\",\n" +
            "    \"picture\": \"http://placehold.it/32x32\",\n" +
            "    \"age\": 31,\n" +
            "    \"eyeColor\": \"green\",\n" +
            "    \"name\": \"Gwendolyn Riggs\",\n" +
            "    \"gender\": \"female\",\n" +
            "    \"company\": \"XOGGLE\",\n" +
            "    \"email\": \"gwendolynriggs@xoggle.com\",\n" +
            "    \"phone\": \"+1 (983) 458-3698\",\n" +
            "    \"address\": \"923 Calyer Street, Toftrees, Oklahoma, 2959\",\n" +
            "    \"about\": \"Consectetur dolor sit duis laboris incididunt non ex qui. Dolore cillum Lorem consectetur consequat sint id amet ullamco pariatur irure. Elit amet eu occaecat qui ad. Ex amet mollit commodo reprehenderit eiusmod. Laboris ad irure consectetur eiusmod excepteur tempor consequat incididunt mollit aliquip consectetur nulla.\\r\\n\"\n" +
            "  },\n";
            // x50 static random data
}
```

# Create your object to match the JSON
Now, as usual we create the object matching the JSON. You can see a ```@Nullable``` here, this annotation is very usefull if there is mandatory field. If you field is a primitive, you will have to use his object equivalent int -> Integer, bool -> Boolean etc...

```java
@AutoValue
public abstract class UserDetail {
    public abstract String picture();
    public abstract int age();
    public abstract EyeColor eyeColor(); // enum
    public abstract String name();
    @Nullable public abstract String gender();
    public abstract String company();
    public abstract String email();
    public abstract String phone();
    public abstract String address();
    public abstract String about();
}
```

To be able to use the ```@SerializedName``` you will need to add ```compile 'com.google.code.gson:gson:2.6.2'``` to your build.gradle. ```@SerializedName("value")``` let you rename the JSON field // continue here
```java
public enum EyeColor {
    @SerializedName("blue") BLUE,
    @SerializedName("brown") BROWN,
    @SerializedName("green") GREEN
}
```
***Explain serializedName goal***
The extension needs a public static method that returns a TypeAdapter. Let's add it to UserDetail.

```java
@AutoValue
public abstract class UserDetail {
    //[...]
    public abstract String about();
    
    public static TypeAdapter<UserDetail> typeAdapter(Gson gson) {
        return new AutoValue_UserDetail.GsonTypeAdapter(gson);
    }
}
```

Now the AutoValue Gson will be able to generate all the code needed to serialize and deserialize the object for us.

## Android Studio live template

To avoid typing the type adapter data, We can create a live template that will do the work for us.

Open your Android Studio preferences, then navigate to Editor > Live Templates

```java
public static TypeAdapter<$class$> typeAdapter(Gson gson) {
    return new AutoValue_$class$.GsonTypeAdapter(gson);
}
```

![Type Adapter Live template setup][livetemplate]

![Type Adapter Live template setup][livetemplate_animated]


## Pre-0.3.0, create the AutoValueGsonTypeAdapterFactory

If you are using a version before 0.3.0, you will need to create a Type Adapter Factory (or update, make yourself a favor, do it)

```java
public class AutoValueGsonTypeAdapterFactory implements TypeAdapterFactory {

    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        Class<? super T> rawType = type.getRawType();

        if (rawType.equals(SignIn.class)) {
            return (TypeAdapter<T>) SignIn.typeAdapter(gson);
        } 

        return null;
    }
}
```

# Parsing JSON
## Dummy file
I've created some data using the website http://www.json-generator.com

```java
public class DummyJsonProvider {
    public static String DUMMY_JSON = "[\n" +
            "  {\n" +
            "    \"_id\": \"576869ed835bc5884bf7179e\",\n" +
            "    \"index\": 0,\n" +
            "    \"guid\": \"5f4b447c-4911-4ee2-bdc4-8d71c7f2a6d4\",\n" +
            "    \"picture\": \"http://placehold.it/32x32\",\n" +
            "    \"age\": 31,\n" +
            "    \"eyeColor\": \"green\",\n" +
            "    \"name\": \"Gwendolyn Riggs\",\n" +
            "    \"gender\": \"female\",\n" +
            "    \"company\": \"XOGGLE\",\n" +
            "    \"email\": \"gwendolynriggs@xoggle.com\",\n" +
            "    \"phone\": \"+1 (983) 458-3698\",\n" +
            "    \"address\": \"923 Calyer Street, Toftrees, Oklahoma, 2959\",\n" +
            "    \"about\": \"Consectetur dolor sit duis laboris incididunt non ex qui. Dolore cillum Lorem consectetur consequat sint id amet ullamco pariatur irure. Elit amet eu occaecat qui ad. Ex amet mollit commodo reprehenderit eiusmod. Laboris ad irure consectetur eiusmod excepteur tempor consequat incididunt mollit aliquip consectetur nulla.\\r\\n\"\n" +
            "  },\n";
            // x50 static random data
}
```

## Use the converter to get your object
### With Reflection
```java
Gson gson = new GsonBuilder().create();
Type type = new TypeToken<ArrayList<UserDetailWithoutAutoValue>>() {}.getType();
for (int i = 0; i < loopNumber; i++) {
    List<UserDetailWithoutAutoValue> userDetail = gson.fromJson(DummyJsonProvider.DUMMY_JSON, type);
}
```

### Without reflection (using the auto-value-gson factory)
```java
Gson gson = new GsonBuilder()
        .registerTypeAdapterFactory(new AutoValueGsonTypeAdapterFactory())
        .create();
Type type = new TypeToken<ArrayList<UserDetailAutoValued>>() {}.getType();
for (int i = 0; i < loopNumber; i++) {
    List<UserDetailAutoValued> userDetail = gson.fromJson(DummyJsonProvider.DUMMY_JSON, type);
}
```

## Comparing parsing reflection vs. without
I wrote a little app to test the difference. You can find it on [this AutoValue GitHub project][self-autovalue-project].

Here I deserialize using the gson reflection, 
```java
long startTimer = System.currentTimeMillis();

Gson gson = new GsonBuilder().create();
Type type = new TypeToken<ArrayList<UserDetailWithoutAutoValue>>() {}.getType();
for (int i = 0; i < loopNumber; i++) {
    List<UserDetailWithoutAutoValue> userDetail = gson.fromJson(DummyJsonProvider.DUMMY_JSON, type);
}

float totalDuration = System.currentTimeMillis() - startTimer;
```

and I compare against the one with the AutoValue Adapter Factory.
```java
long startTimer = System.currentTimeMillis();

Gson gson = new GsonBuilder()
        .registerTypeAdapterFactory(new AutoValueGsonTypeAdapterFactory())
        .create();
Type type = new TypeToken<ArrayList<UserDetailAutoValued>>() {}.getType();
for (int i = 0; i < loopNumber; i++) {
    List<UserDetailAutoValued> userDetail = gson.fromJson(DummyJsonProvider.DUMMY_JSON, type);
}

float totalDuration = System.currentTimeMillis() - startTimer;
```

Then I write the number of loops I want to test in the UI, and here we go.

![Compare-reflection-vs-not][comparison-reflection-vs-not]

As you can see in the image above, we can see that our (not) scientific test result show an, at least, 200% time decrease for the deserialization.


# Bonus: Using it with Retrofit 
## Gson converter 
```java
GsonConverterFactory gsonConverterFactory = GsonConverterFactory.create(
        new GsonBuilder()
                .registerTypeAdapterFactory(new AutoValueGsonTypeAdapterFactory())
                .create());
```

## Retrofit Builder
```java
Retrofit retrofit = new Retrofit
        .Builder()
        .addConverterFactory(gsonConverterFactory)
        .baseUrl("http://url.com/")
        .build()
```

# Conclusion
We have seen here that by using AutoValue and the Gson Extension, we can reduce a lot the time needed to parse JSON files. It can be very useful when your app depends on this kind of format. I've been using it for the last three months, and object creation is just so easy that I have to take care not to create another object instead of using an existing one.  
  
On a side note, you should use the Parcelable extension too... Add "implements Parcelable" and here you go.

PS: [Spiral Clock][SpiralClock] by cat-machine, [Creative Commons Attribution-Noncommercial-Share Alike 3.0 License][creative-commons]

<!-- Images -->
[livetemplate]: images/autovalue-gson-typeadapter.png
[livetemplate_animated]: images/livetemplate_typeadapter.gif
[comparison-reflection-vs-not]: images/comparison-reflection-vs-not.png


<!-- Links -->
[autovalue-dubedout]: http://dubedout.eu/2016/05/22/google-autovalue-immutability/
[auto-value-gson]: https://github.com/rharter/auto-value-gson
[self-autovalue-project]: https://github.com/ViBlog/autovalues/tree/master/code/AutoValues
[SpiralClock]: http://cat-machine.deviantart.com/art/Spiral-Clock-1-110993506
[creative-commons]: https://creativecommons.org/licenses/by-nc-sa/3.0/
