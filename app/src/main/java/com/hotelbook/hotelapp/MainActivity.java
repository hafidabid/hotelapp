package com.hotelbook.hotelapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.awt.font.TextAttribute;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    TextView hellotext;
    boolean doubleBackToExitPressedOnce = false;
    ArrayList<daftarHotel> dfhtl;
    RecyclerView lv;
    hotelVA hotelva;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //requesting permission to send sms
        if(!this.isSmsPermissionGranted()){
            this.reqSMSendPermission();
        }

        //check shared preferences session
        sharepref.sp = getSharedPreferences("app_sess",Context.MODE_PRIVATE);
        sharepref.speditor = sharepref.sp.edit();
        int sess_status = sharepref.sp.getInt(sharepref.SP_SESS,99);

        String nama_pengguna = sharepref.sp.getString(sharepref.SP_FULLNAME,"-");
        if(sess_status==99){
            sharepref.initializeSP();
            System.out.println("a");
            startActivity(new Intent(MainActivity.this,LoginPage.class));
            finish();
        }else{
            if(sess_status==0){
                startActivity(new Intent(MainActivity.this,LoginPage.class));
                finish();
            }
            System.out.println("b");
        }

        String fullUserName = sharepref.sp.getString(sharepref.SP_FULLNAME,"NO NAME USER ERROR");
        TextView hellouser = findViewById(R.id.sayHello);
        hellouser.setText(fullUserName);

        //action bar
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#309D95"));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);

        //FOR LISTVIEW ANDROID
        lv = findViewById(R.id.scroll_hotel);
        dfhtl = daftarHotel.buatArrlist(this);
        hotelva = new hotelVA(dfhtl);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(MainActivity.this);
        lv.setLayoutManager(lm);
        lv.setAdapter(hotelva);

        //FOR DEBUG PURPOSE ONLY

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater  MI= getMenuInflater();
        MI.inflate(R.menu.menuawal,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.item_logout){
            startActivity(new Intent(MainActivity.this,LoginPage.class));
            sharepref.loggingOUT();
            finish();
        }else if(item.getItemId()==R.id.item_pembelianku){
            startActivity(new Intent(MainActivity.this,MyOrder.class));
        }


        return super.onOptionsItemSelected(item);
    }

    public static String moneyConverter(String money){
        String newmoney = "";
        int y= money.length();
        int counter=0;
        int newlen = (y/3)+(y%3>0?1:0)-1;
        boolean dot = false;

        if(y%3==0){
            for(int x=0;x<y+newlen;x++){
                if(x>0 && x==3+(4*(x/4))){
                    newmoney = newmoney+".";
                }else {
                    newmoney = newmoney+money.charAt(counter);
                    counter++;
                }
            }
        }else{
            for(int x=0;x<y+newlen;x++){
                if(x>0 && x==(y%3)+(4*(x/4))){
                    newmoney = newmoney+".";
                }else {
                    newmoney = newmoney+money.charAt(counter);
                    counter++;
                }
            }
        }


        return newmoney;
    }

    public boolean isSmsPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    public void reqSMSendPermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {}

        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.SEND_SMS},
                101
        );
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            onDestroy();
            //System.out.println("backbutton diklik dua kali");
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}