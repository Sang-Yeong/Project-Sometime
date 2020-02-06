package com.example.user.summerproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

import javax.security.auth.callback.Callback;

public class Weekly extends View{

    Button w_btnRed,w_btnOrange,w_btnGreen,w_btnReset;

    public Weekly(Context context) {
        super(context);
    }

    public Weekly(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Weekly(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


}