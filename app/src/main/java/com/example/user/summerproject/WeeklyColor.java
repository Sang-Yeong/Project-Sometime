package com.example.user.summerproject;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

public class WeeklyColor {
    DatabaseReference arrayStore_firebase = FirebaseDatabase.getInstance().getReference();

    String s_WeeklyColor[]=new String[70];

    //main에서 tv배경색 저장해준 String[] main_weekly_tvColor배열 받아서 s_WeeklyColor배열에 복사해주기
    public void method_storeWeekly(String[] main_weekly_tvColor){
        System.arraycopy(main_weekly_tvColor,0, s_WeeklyColor, 0, main_weekly_tvColor.length);

    }

}