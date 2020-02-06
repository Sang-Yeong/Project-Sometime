package com.example.user.summerproject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.NetworkInterface;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static android.content.ContentValues.TAG;

public class MainActivity extends FragmentActivity {

    //버튼이 눌려지는 횟수(안드로이드 스튜디오에서 기본적으로 0으로 초기화)
    int cnt_reset=0,cnt_red,cnt_orange,cnt_green,cnt_save;
    //버튼 색 정수로 저장 변수(red=1,orange=2,green=3)
    int storeColor;

    //70칸의 시간대 textView 배열 생성과 선언
    TextView[] tv_w=new TextView[70];
    TextView[] tv_s=new TextView[70];

    //===============login=========
    Button btn_Login, btn_Join;
    TextView Login_et_ID, Login_et_PW;


    //ConstraintLayout배열 선언
    private ConstraintLayout[] m_lytFragments;
    //ImageView배열 선언
    private ImageView[] m_imgMenu;
    //메뉴가 눌렸을때, 안눌렸을때의 이미지를 표현하기 위한 bitmap배열
    private Bitmap[] m_bmpMenuDisable;
    private Bitmap[] m_bmpMenuEnable;
    //weekly에 있는 5가지 버튼
    private Button w_btnRed,w_btnOrange,w_btnGreen,w_btnReset,w_btnSave;
    private Button s_btnFriendEye;
    private Button s_btnoverlapEye;


    //============파이어베이스 저장위한 선언>>mac주소로 저장
    DatabaseReference store_weeklyColor = FirebaseDatabase.getInstance().getReference(getMACAddress("wlan0").toString());
    DatabaseReference find_sliding_ID = FirebaseDatabase.getInstance().getReference();


    String[] main_weekly_tvColor=new String[70]; //weekly textview 색 저장 배열
    String[] call_weekly_tvColor=new String[70]; //언제한번 에 저장된 색 받아주는 배열
    String[] Overlap_weekly_tvColor=new String[70]; //두 사용자 합친 색 받아주는 배열

    //=============share페이지================
    private EditText s_sliding_edit_inputID;
    private Button s_sliding_check;
    private String[] s_sliding_store_id=new String[2];
    int i=0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //================login페이지 find,set============
        btn_Login = findViewById(R.id.Login_btn_Login);
        btn_Join = findViewById(R.id.Login_btn_Join);
        Login_et_ID=findViewById(R.id.Login_et_ID);
        Login_et_PW=findViewById(R.id.Login_et_PW);

        btn_Login.setOnClickListener(login);
        btn_Join.setOnClickListener(join);


        //=============== // 데이터베이스 읽기 ValueEventListener=====================================
        // 데이터베이스 읽기 #1. ValueEventListener

        FirebaseDatabase.getInstance().getReference(getMACAddress("wlan0").toString().equals("90:97:F3:9B:AF:86")? "E0:99:71:5E:11:36":"90:97:F3:9B:AF:86").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(i<70){
                        call_weekly_tvColor[i++]= snapshot.getValue().toString();

                    }
                }


               /* for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(getMACAddress("wlan0").toString().equals("90:97:F3:9B:AF:86")){
                        if(i<70){
                            call_weekly_tvColor[i++]= snapshot.child("E0:99:71:5E:11:36").child(""+(i++)).getValue().toString();
                            //child에 선택한 상대방의 mac주소 넣기
                        }
                    }
                    else if(getMACAddress("wlan0").toString().equals("E0:99:71:5E:11:36")){
                        if(i<70){
                            call_weekly_tvColor[i++]= snapshot.child("90:97:F3:9B:AF:86").child(""+(i++)).getValue().toString();
                            //child에 선택한 상대방의 mac주소 넣기
                        }
                    }

                }
                */


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //=============share페이지 '친구부르기'버튼사용==============
        s_btnFriendEye=findViewById(R.id.image_share_friend_eye);
        s_btnFriendEye.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
            @Override
            public void onClick(View v) {
                SlidingDrawer drawer = (SlidingDrawer)findViewById(R.id.slide_share_friend_eye);
                drawer.bringToFront();
                drawer.animateClose();

            }
        });

        //====================weekly textView 색깔 모두 white로 초기화=======================
        for(int i=0;i<70;i++){
            main_weekly_tvColor[i]="white";
        }

        //==============weekly init=============

        //XML 파일의 TextVIew 아이디와 메인자바파일에서 지정해준 변수를 연결
        w_btnRed=findViewById(R.id.w_btnRed);
        w_btnOrange=findViewById(R.id.w_btnOrange);
        w_btnGreen=findViewById(R.id.w_btnGreen);
        w_btnReset=findViewById(R.id.w_btnReset);
        w_btnSave=findViewById(R.id.w_btnSave);
        //onclick을 구현하기 위한 방법 - 클릭리스너를 객체로 생성
        w_btnRed.setOnClickListener(btnColor);
        w_btnOrange.setOnClickListener(btnColor);
        w_btnGreen.setOnClickListener(btnColor);
        w_btnReset.setOnClickListener(btnColor);
        w_btnSave.setOnClickListener(btnColor);

        //======================================


        //====================weekly페이지 시간표 칸==============
        //시간대별로 70칸에 아이디를 부여하기위해 반복문 실행
        for(int i=0;i<70;i++){
            //시간대를 나타내는 textview 인 tv배열에 각각 id값을 부여
            tv_w[i] = findViewById(0x7f070109+i);
            //onclick을 구현하기 위한 방법 - 클릭리스너를 객체로 생성
            tv_w[i].setOnClickListener(tvColor);
        }

        //=========================================================

        //====================share페이지 시간표 칸==============
        for(int i=0;i<70;i++){
            tv_s[i]=findViewById(0x7f070087+i);
            tv_s[i].setOnClickListener(s_btnOverlap);

        }

        //=================share페이지 버튼=====================
        s_sliding_check=findViewById(R.id.share_sliding_Select_Check_Button);
        s_sliding_check.setOnClickListener(s_btnOverlap);
        s_btnoverlapEye=findViewById(R.id.OverlapEye);
        s_btnoverlapEye.setOnClickListener(s_btnOverlap);




        //==========================================================
        //ConstraintLayout배열 객체 생성
        m_lytFragments = new ConstraintLayout[] {
                //weekly버튼 눌렀을때 띄어지는 창의 id값(xml에 정의되어있음) findview
                findViewById(R.id.MainActivity_Layout_Weekly),
                //share버튼 눌렀을때 띄어지는 창의 id값 findview
                findViewById(R.id.MainActivity_Layout_Share),
                findViewById(R.id.MainActivity_Layout_Info),
                findViewById(R.id.MainActivity_Layout_Google)
        };
        //ImageView배열 객체 생성
        m_imgMenu = new ImageView[] {
                //weekly버튼의 id값 findview
                findViewById(R.id.MainActivity_ImageView_Weekly),
                //share버튼의 id값 findview
                findViewById(R.id.MainActivity_ImageView_Share),
                findViewById(R.id.MainActivity_ImageView_Info),
                findViewById(R.id.MainActivity_ImageView_Google)
        };
        //메뉴가 눌려지지 않았을때 bitmap 객체 배열 생성
        m_bmpMenuDisable = new Bitmap[] {
                //BitmapFactory.decodeResource() : Resource 폴더에 저장된 그림파일을 Bitmap 으로 만들어 리턴해주기
                //weekly,share 메뉴 각각 눌려지지 않았을때 그림파일을 bitmap으로 만들어 리턴
                BitmapFactory.decodeResource(getResources(), R.drawable.ic_weekly_not_select),
                BitmapFactory.decodeResource(getResources(), R.drawable.ic_share_not_select),
                BitmapFactory.decodeResource(getResources(), R.drawable.ic_info_not_select),
                BitmapFactory.decodeResource(getResources(), R.drawable.ic_google_not_select)
        };
        //메뉴가 눌렸을때 bitmap 객체 배열 생성
        m_bmpMenuEnable = new Bitmap[] {
                //weekly,share 메뉴 각각 눌렸을때 그림파일을 bitmap으로 만들어 리턴
                BitmapFactory.decodeResource(getResources(), R.drawable.ic_weekly_select),
                BitmapFactory.decodeResource(getResources(), R.drawable.ic_share_select),
                BitmapFactory.decodeResource(getResources(), R.drawable.ic_info_select),
                BitmapFactory.decodeResource(getResources(), R.drawable.ic_google_select)
        };
        //ImageView 의 변수를 iv로 설정하고 m_imgMenu(=배열)의 항목수만큼 반복문 실행(for-each문)
        for (ImageView iv : m_imgMenu)
            //
            iv.setOnClickListener(ButtonListener);
    }

    private void onSelectMenu(int number) {
        for (int i = 0; i < 4; i++) {
            m_lytFragments[i].setVisibility(i == number ? View.VISIBLE : View.INVISIBLE);
            m_imgMenu[i].setImageBitmap(i == number ? m_bmpMenuEnable[i] : m_bmpMenuDisable[i]);
        }
    }
    private final View.OnClickListener ButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.MainActivity_ImageView_Weekly:
                    onSelectMenu(0);
                    break;
                case R.id.MainActivity_ImageView_Share:
                    onSelectMenu(1);
                    break;
                case R.id.MainActivity_ImageView_Info:
                    onSelectMenu(2);
                    break;
                case R.id.MainActivity_ImageView_Google:
                    onSelectMenu(3);
                    break;
            }
        }
    };
    //========================share페이지 버튼 ===================================
    private View.OnClickListener s_btnOverlap=new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            switch (view.getId()){
                case R.id.OverlapEye: //중첩하기 버튼
                    for(int i=0;i<70;i++){
                        if("white".equals(main_weekly_tvColor[i])&&call_weekly_tvColor[i].equals("white"))
                            Overlap_weekly_tvColor[i]="white";

                        else if((main_weekly_tvColor[i].equals("green")&&call_weekly_tvColor[i].equals("green"))
                                //흰+초
                                ||(main_weekly_tvColor[i].equals("white")&&call_weekly_tvColor[i].equals("green"))
                                ||(main_weekly_tvColor[i].equals("green")&&call_weekly_tvColor[i].equals("white")))
                            Overlap_weekly_tvColor[i]="green";

                        else if((main_weekly_tvColor[i].equals("orange")&&call_weekly_tvColor[i].equals("orange"))
                                //주+초 ,주+흰
                                ||(main_weekly_tvColor[i].equals("orange")&&call_weekly_tvColor[i].equals("white"))
                                ||(main_weekly_tvColor[i].equals("orange")&&call_weekly_tvColor[i].equals("green"))

                                ||(main_weekly_tvColor[i].equals("white")&&call_weekly_tvColor[i].equals("orange"))
                                ||(main_weekly_tvColor[i].equals("green")&&call_weekly_tvColor[i].equals("orange")))
                            Overlap_weekly_tvColor[i]="orange";
                        else
                            Overlap_weekly_tvColor[i]="red";
                    }

                    for(int i = 0; i < 70; i++) {
                        if(Overlap_weekly_tvColor[i].equals("white"))
                            tv_s[i].setBackgroundColor(Color.parseColor("#FFFFFF"));
                        else if(Overlap_weekly_tvColor[i].equals("red"))
                            tv_s[i].setBackgroundColor(Color.parseColor("#e45d49"));
                        else if(Overlap_weekly_tvColor[i].equals("orange"))
                            tv_s[i].setBackgroundColor(Color.parseColor("#ffcb10"));
                        else if(Overlap_weekly_tvColor[i].equals("green"))
                            tv_s[i].setBackgroundColor(Color.parseColor("#0a8034"));

                    }
                    break;

                case R.id.share_sliding_Select_Check_Button:  //슬라이딩드로어 안에 있는 아이디 검색 확인 버튼
                    //edittext에 입력된 아이디 저장
                    //for(int i=0;i<s_sliding_store_id.length;i++){
                    //    s_sliding_store_id[i]=s_sliding_edit_inputID.getText().toString();
                    //}
                    //SlidingDrawer drawer_2 = (SlidingDrawer)findViewById(R.id.slide_share_friend_eye);
                    //drawer_2.animateClose();
                    Toast.makeText(MainActivity.this, s_sliding_edit_inputID.getText().toString()+"님이 선택되었습니다.", Toast.LENGTH_SHORT).show();
                    break;

                case R.id.image_share_friend_eye:  //share페이지 '친구부르기'버튼
                    SlidingDrawer drawer = (SlidingDrawer)findViewById(R.id.slide_share_friend_eye);
                    drawer.bringToFront();
                    drawer.animateClose();
                    break;

            }


        }
    };

    //===============================weekly button clicked================================
    //onclick을 구현하기 위한 방법 - 클릭리스너를 객체로 생성
    private View.OnClickListener btnColor=new View.OnClickListener(){
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override

        public void onClick(View view) {
            switch (view.getId()){
                //리셋버튼의 id 값이면
                case R.id.w_btnReset:
                    //리셋버튼의 색은 0으로 설정
                    storeColor=0;
                    //초기값 0에서 1증가
                    ++cnt_reset;
                    //1이면
                    if (cnt_reset== 1) {
                        //리셋버튼의 Background(배경)을 ic_weekly_select_reset(회색)으로 set
                        w_btnReset.setBackground(getDrawable(R.drawable.ic_weekly_select_reset));
                    }
                    //리셋버튼이 2번 눌리면
                    else if(cnt_reset== 2){
                        //리셋버튼의 배경을 검정색으로 set
                        w_btnReset.setBackground(getDrawable(R.drawable.ic_weekly_all_reset));
                        //TextView 를 배열로 만든 tv를 시간대 칸 만큼 반복문 실행
                        for(int i=0;i<70;i++){
                            //70칸 모두를 흰 색(#FFFFFF)으로 BackgroundColor(배경색) set
                            tv_w[i].setBackgroundColor(Color.parseColor("#FFFFFF"));
                        }
                    }
                    //빨간버튼,주황버튼,초록버튼,저장버튼의 Background(배경)을 흰색으로 set
                    w_btnRed.setBackground(getDrawable(R.drawable.ic_weekly_not_red));
                    w_btnOrange.setBackground(getDrawable(R.drawable.ic_weekly_not_orange));
                    w_btnGreen.setBackground(getDrawable(R.drawable.ic_weekly_not_green));
                    w_btnSave.setBackground(getDrawable(R.drawable.ic_weekly_not_save));
                    break;

                //빨간버튼의 id값이면
                case R.id.w_btnRed:
                    //빨간버튼의 색은 1로 설정
                    storeColor=1;
                    //초기값 0에서 1로 증가
                    ++cnt_red;
                    //1이면
                    if (cnt_red == 1) {
                        //빨간버튼의 Background(배경)을 ic_weekly_select_red(빨간)으로 set
                        w_btnRed.setBackground(getDrawable(R.drawable.ic_weekly_select_red));
                        //주황,초록,리셋 버튼 0으로 초기화
                        cnt_orange=0;
                        cnt_green=0;
                        cnt_reset = 0;
                    }
                    //빨간버튼,주황버튼,초록버튼,저장버튼의 Background(배경)을 흰색으로 set
                    w_btnOrange.setBackground(getDrawable(R.drawable.ic_weekly_not_orange));
                    w_btnGreen.setBackground(getDrawable(R.drawable.ic_weekly_not_green));
                    w_btnReset.setBackground(getDrawable(R.drawable.ic_weekly_not_reset));
                    w_btnSave.setBackground(getDrawable(R.drawable.ic_weekly_not_save));
                    break;


                //주황버튼의 id값이면
                case R.id.w_btnOrange:
                    //주황버튼의 색은 2로 설정
                    storeColor=2;
                    //초기값 0에서 1로 증가
                    ++cnt_orange;
                    //1이면
                    if (cnt_orange == 1) {
                        //주황버튼의 Background(배경)을 ic_weekly_select_orange(주황)으로 set
                        w_btnOrange.setBackground(getDrawable(R.drawable.ic_weekly_select_orange));
                        //빨간,초록,리셋 버튼 0으로 초기화
                        cnt_red=0;
                        cnt_green=0;
                        cnt_reset = 0;
                    }
                    //빨간버튼,초록버튼,리셋버튼,저장버튼의 Background(배경)을 흰색으로 set
                    w_btnRed.setBackground(getDrawable(R.drawable.ic_weekly_not_red));
                    w_btnGreen.setBackground(getDrawable(R.drawable.ic_weekly_not_green));
                    w_btnReset.setBackground(getDrawable(R.drawable.ic_weekly_not_reset));
                    w_btnSave.setBackground(getDrawable(R.drawable.ic_weekly_not_save));
                    break;

                //초록버튼의 id값이면
                case R.id.w_btnGreen:
                    //초록버튼의 색은 3으로 설정
                    storeColor=3;
                    //초기값 0에서 1로 증가
                    ++cnt_green;
                    //1이면
                    if (cnt_green == 1) {
                        //초록버튼의 Background(배경)을 ic_weekly_select_green(초록)으로 set
                        w_btnGreen.setBackground(getDrawable(R.drawable.ic_weekly_select_green));
                        //빨간,주황,리셋 버튼 0으로 초기화
                        cnt_red=0;
                        cnt_orange=0;
                        cnt_reset = 0;
                    }
                    //빨간버튼,주황버튼,리셋버튼,저장버튼의 Background(배경)을 흰색으로 set
                    w_btnRed.setBackground(getDrawable(R.drawable.ic_weekly_not_red));
                    w_btnOrange.setBackground(getDrawable(R.drawable.ic_weekly_not_orange));
                    w_btnReset.setBackground(getDrawable(R.drawable.ic_weekly_not_reset));
                    w_btnSave.setBackground(getDrawable(R.drawable.ic_weekly_not_save));
                    break;

                //저장버튼의 id값이면
                case R.id.w_btnSave:
                    //초기값 0에서 1로 증가
                    ++cnt_save;
                    //저장버튼을 짝수횟수만큼 누르면
                    if (cnt_save % 2 == 0) {
                        //저장버튼의 Background(배경)을 흰색으로 set
                        w_btnSave.setBackground(getDrawable(R.drawable.ic_weekly_not_save));
                    }
                    //저장버튼을 홀수횟수만큼 누르면
                    else {
                        //저장버튼의 Background(배경)을 ic_weekly_select_save(살색)으로 set
                        w_btnSave.setBackground(getDrawable(R.drawable.ic_weekly_select_save));
                    }
                    //빨간버튼,주황버튼,리셋버튼,초록버튼의 Background(배경)을 흰색으로 set
                    w_btnRed.setBackground(getDrawable(R.drawable.ic_weekly_not_red));
                    w_btnOrange.setBackground(getDrawable(R.drawable.ic_weekly_not_orange));
                    w_btnReset.setBackground(getDrawable(R.drawable.ic_weekly_not_reset));
                    w_btnGreen.setBackground(getDrawable(R.drawable.ic_weekly_not_green));


                    //-------------------textView 색 저장----------------------------------

                    for(int i=0;i<70;i++){
                        store_weeklyColor.child(""+i).setValue(main_weekly_tvColor[i]);
                    }

                    break;
            }

        }
    };
    //=======================================================================

    //================textView 색칠하기=================
    //onclick을 구현하기 위한 방법 - 클릭리스너를 객체로 생성
    private final View.OnClickListener tvColor = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            switch (storeColor) {
                //리셋버튼(storecolor=0)이면
                case 0:
                    //시간대별로 70칸 모두 반복문 실행
                    for (int i = 0; i < 70; i++) {
                        //선택된 id 값이 70칸에 부여된 값과 같으면
                        if (view.getId() == 0x7f070109+i) {
                            //tv 배열의 BackgroundColor(배경)을 흰색으로 set
                            tv_w[i].setBackgroundColor(Color.parseColor("#FFFFFF"));
                            main_weekly_tvColor[i]="white";
                        }
                    }
                    break;
                //빨간버튼(storecolor=1)이면
                case 1:
                    for (int i = 0; i < 70; i++) {
                        if (view.getId() == 0x7f070109+i) {
                            //tv 배열의 BackgroundColor(배경)을 빨간색으로 set
                            tv_w[i].setBackgroundColor(Color.parseColor("#e45d49"));
                            main_weekly_tvColor[i]="red";
                        }
                    }
                    break;
                //주황버튼(storecolor=2)이면
                case 2:
                    for (int i = 0; i < 70; i++) {
                        if (view.getId() == 0x7f070109+i) {
                            //tv 배열의 BackgroundColor(배경)을 주황색으로 set
                            tv_w[i].setBackgroundColor(Color.parseColor("#ffcb10"));
                            main_weekly_tvColor[i]="orange";
                        }
                    }
                    break;
                //초록버튼(storecolor=3)이면
                case 3:
                    for (int i = 0; i < 70; i++) {
                        if (view.getId() == 0x7f070109+i) {
                            //tv 배열의 BackgroundColor(배경)을 초록색으로 set
                            tv_w[i].setBackgroundColor(Color.parseColor("#0a8034"));
                            main_weekly_tvColor[i]="green";
                        }
                    }
                    break;
            }

        }

    };


    //=================== wifi mac address를 가져오는 방법================
    public static String getMACAddress(String interfaceName) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (interfaceName != null) {
                    if (!intf.getName().equalsIgnoreCase(interfaceName)) continue;
                }
                byte[] mac = intf.getHardwareAddress();
                if (mac==null) return "";
                StringBuilder buf = new StringBuilder();
                for (int idx=0; idx<mac.length; idx++)
                    buf.append(String.format("%02X:", mac[idx]));
                if (buf.length()>0) buf.deleteCharAt(buf.length()-1);
                return buf.toString();
            }
        } catch (Exception ex) { } // for now eat exceptions
        return "";
    }
    //String macAddress = getMACAddress("wlan0");
    //=====================================================================================================
    private View.OnClickListener login = new View.OnClickListener(){ // 로그인버튼을 누른 경우
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.Login_btn_Login) {
                FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if ((dataSnapshot.child(getMACAddress("wlan0").toString()).child("id").getValue().toString())
                                .equals(Login_et_ID.getText().toString())
                                && (dataSnapshot.child(getMACAddress("wlan0").toString()).child("pw").getValue().toString())
                                .equals(Login_et_PW.getText().toString())) { // 저장된 아이디와 비번 모두 일치하는 경우
                            Toast.makeText(getApplicationContext(), "로그인 되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                        else // 아이디나 비번이 틀린 경우
                            Toast.makeText(getApplicationContext(), "아이디나 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        }
    };

    private View.OnClickListener join = new View.OnClickListener(){ // 회원가입 버튼을 누른 경우
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.Login_btn_Join) {
                FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if((dataSnapshot.child(getMACAddress("wlan0").toString()).child("id").getValue().toString())
                                .equals(Login_et_ID.getText().toString())){ // 저장된 아이디값과 입력한 아이디값이 같은경우
                            Toast.makeText(getApplicationContext(), "이미 존재하는 아이디입니다.", Toast.LENGTH_SHORT).show();
                        }
                        else { // 아이디가 같지 않으면 => 회원가입
                            store_weeklyColor.child("id").setValue(Login_et_ID.getText().toString());
                            store_weeklyColor.child("pw").setValue(Login_et_PW.getText().toString());
                            Toast.makeText(getApplicationContext(), "회원가입 되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        }
    };

}

