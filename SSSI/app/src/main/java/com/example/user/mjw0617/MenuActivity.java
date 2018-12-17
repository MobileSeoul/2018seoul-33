package com.example.user.mjw0617;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by sekyo on 2016-08-28.
 */
public class MenuActivity extends AppCompatActivity {

    private Button mapBtn;
    private Button myPageBtn;
    private Button reviewBtn;
    private Button spBtn;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        mapBtn = (Button) findViewById(R.id.mapBtn);
        myPageBtn = (Button) findViewById(R.id.myPageBtn);
        reviewBtn = (Button) findViewById(R.id.reviewBtn);
//        spBtn = (Button) findViewById(R.id.spBtn);

        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(intent);
            }
        });
//        mapBtn.setOnTouchListener(new View.OnTouchListener() {
//            public boolean onTouch(View v, MotionEvent event) {
//                switch(event.getAction()){
//                    case MotionEvent.ACTION_DOWN:
//                        mapBtn.setBackgroundResource(R.drawable.nadeule1_clicked);
//                        //버튼이 눌렸을때 버튼의 좌표를 옮기거나 하는 액션
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        //버튼이 눌렸다 떼어졌을때 되돌아오거나 하는 액션
//                        mapBtn.setBackgroundResource(R.drawable.nadeule1);
//                        break;
//                }
//                return false;
//            }
//        });

        myPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
//        myPageBtn.setOnTouchListener(new View.OnTouchListener(){
//            public boolean onTouch(View v, MotionEvent event){
//                switch (event.getAction()){
//                    case MotionEvent.ACTION_DOWN:
//                        myPageBtn.setBackgroundResource(R.drawable.mpage1_clicked);
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        myPageBtn.setBackgroundResource(R.drawable.mpage1);
//                        break;
//                }
//                return false;
//            }
//        });
        reviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), info.class);
                startActivity(intent);
            }
        });
    }
}
//        reviewBtn.setOnTouchListener(new View.OnTouchListener() {
//            public boolean onTouch(View v, MotionEvent event) {
//                switch(event.getAction()){
//                    case MotionEvent.ACTION_DOWN:
//                        reviewBtn.setBackgroundResource(R.drawable.review1_clicked);
//                        //버튼이 눌렸을때 버튼의 좌표를 옮기거나 하는 액션
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        //버튼이 눌렸다 떼어졌을때 되돌아오거나 하는 액션
//                        reviewBtn.setBackgroundResource(R.drawable.review1);
//                        break;
//                }
//                return false;
//            }
//        });
//        spBtn.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getApplicationContext(), InfoOfdeveloper.class);
//                startActivity(intent);
//            }
//        });
////        spBtn.setOnTouchListener(new View.OnTouchListener(){
////            public boolean onTouch(View v, MotionEvent event){
////                switch (event.getAction()){
////                    case MotionEvent.ACTION_DOWN:
////                        spBtn.setBackgroundResource(R.drawable.us1_clicked);
////                        break;
////                    case MotionEvent.ACTION_UP:
////                        spBtn.setBackgroundResource(R.drawable.us1);
////                        break;
////                }
////                return false;
////            }
////        });
//    }
//}