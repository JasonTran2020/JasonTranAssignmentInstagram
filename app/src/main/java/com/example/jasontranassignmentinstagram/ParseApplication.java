package com.example.jasontranassignmentinstagram;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //Register the parse models
        ParseObject.registerSubclass(Post.class);


        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("APPLICATION_ID_HERE")
                .clientKey("CLIENT_KEY_HERE")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
