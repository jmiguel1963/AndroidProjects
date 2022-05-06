package com.example.expensemanager;

import android.content.Context;
import android.widget.Toast;

//class used with methods to be used in every activity
public class Utilities {

    public static boolean checkFormat(Context context,String input){
        //checking format
        boolean isValidFormat = input.matches("([0-9]{2})/([0-9]{2})/([0-9]{4})");
        if (isValidFormat){
            String[] parts = input.split("/");
            int day=Integer.parseInt(parts[0]);
            int month=Integer.parseInt(parts[1]);
            int year=Integer.parseInt(parts[2]);
            //valid month checking
            if(month>12 || month<1){
                Toast.makeText(context,"Month is not correct",Toast.LENGTH_SHORT).show();
                return false;
            }
            //valid day checking
            if (day>31 || day<1){
                Toast.makeText(context,"Day is not correct",Toast.LENGTH_SHORT).show();
                return false;
            }
            //30 days months checking
            if (month==4 || month==6 || month==9 || month==11){
                if (day>30){
                    Toast.makeText(context,"Day is not correct",Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            //february in normal or leap year checking
            if (month==2){
                if (year%4!=0){
                    if (day>28){

                    }
                }else{
                    if (day>29){
                        Toast.makeText(context,"Day is not correct",Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
            }
        }else{
            Toast.makeText(context,"Format is not correct",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public static boolean checkAmountFormat(Context context,String input){
        return input.matches("[0-9]+");
    }
}
