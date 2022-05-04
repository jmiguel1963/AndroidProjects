package com.example.expensemanager;

import android.content.Context;
import android.content.SharedPreferences;

import android.icu.text.DisplayContext;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.lang.reflect.Type;
import java.util.ArrayList;

public class SharedPrefConfig {

    private static final String LIST_KEY="list_key";

    public static void writeListPref(Context context, ArrayList<User> users){
        Gson gson =new Gson();
        String jsonString = gson.toJson(users);
        SharedPreferences pref= PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor= pref.edit();
        editor.putString(LIST_KEY,jsonString);
        editor.apply();
    }

    public static ArrayList<User> readListPref(Context context){
        SharedPreferences pref= PreferenceManager.getDefaultSharedPreferences(context);
        String jsonString =pref.getString(LIST_KEY,"");
        Gson gson=new Gson();
        Type type= new TypeToken<ArrayList<User>>() {}.getType();
        ArrayList<User> users=gson.fromJson(jsonString,type);
        return users;
    }
}
