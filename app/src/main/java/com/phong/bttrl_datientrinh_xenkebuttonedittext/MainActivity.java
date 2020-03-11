package com.phong.bttrl_datientrinh_xenkebuttonedittext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity {

    Button btnDraw;
    EditText edtDraw;
    LinearLayout layoutveButtonView;
    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT );
    int n = 0;
    Random random = new Random();
    AtomicBoolean atomicBoolean = null;
    int sizeHalf = 0;

    //Tiến trình CHÍNH xử lý ở đây: Main Thread
    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            //Nhận nhãn của Button được gửi về từ tiến trình con
            int v = random.nextInt(100);
            String nhan_button = v + "";
            //khởi tạo 1 Button
            View vv = null;
            if (msg.arg1 % 2 == 0){
                vv = new Button(MainActivity.this);
                ((Button) vv).setText(nhan_button);
            } else {
                vv = new EditText(MainActivity.this);
                ((EditText) vv).setText(nhan_button);
            }
            vv.setLayoutParams(layoutParams);
            //đưa Button vào layoutveButtonView
            layoutveButtonView.addView(vv);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addControls();
        addEvetns();
    }

    private void addEvetns() {
        btnDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                xuLyVeButtonView();
            }
        });
    }

    private void xuLyVeButtonView() {
        n = Integer.parseInt(edtDraw.getText().toString());
        layoutveButtonView.removeAllViews();
        atomicBoolean = new AtomicBoolean(false);
        //Tiến trình CON xử lý ở đây: Background Thread
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //Trong này ko đc phép truy suất đến các biến control trên giao diện
                for (int i = 0; i < n && atomicBoolean.get(); i++){
                    //Lấy message từ Main Background:
                    Message message = handler.obtainMessage();
                    //Từ message này sẽ thay đổi thông tin rồi gửi ngược lại Main Background:
                    //gán dữ liệu cho message Mainthread, lưu vào biến object
                    //chú ý ta có thể lưu bất kỳ kiểu dữ liệu nào vào object
                    message.arg1 = i;
                    //gửi trả lại message cho Mainthread
                    handler.sendMessage(message);
                    //nghỉ 200 mili second
                    SystemClock.sleep(200);
                }
            }
        });
        atomicBoolean.set(true);
        //thực thi tiến trình
        thread.start();
    }

    private void addControls() {
        btnDraw = findViewById(R.id.btnDraw);
        edtDraw = findViewById(R.id.edtDraw);
        layoutveButtonView = findViewById(R.id.llButtonView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        sizeHalf = size.x/2;
    }

    private boolean isPrime(int n){
        if (n < 2){
            return  false;
        }
        for (int i = 2; i <= Math.sqrt(n); i++){
            if (n % i == 0){
                return  false;
            }
        }
        return true;
    }
}
