package com.hotelbook.hotelapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginPage extends AppCompatActivity {
    BantuanSQL b = null;
    public static final String NAMA_DATABASE = "hotelbook.db";
    EditText email_field;
    TextInputLayout pass_field;
    String email,password;
    Cursor cr;
    TextView err_mail,err_pass;
    boolean doubleBackToExitPressedOnce=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        b = BantuanSQL.getDBInstance(this,NAMA_DATABASE);
        Button goRegister = findViewById(R.id.regist_button);
        Button goLogin = findViewById(R.id.login_button);
        email_field = findViewById(R.id.usernameField);
        pass_field = findViewById(R.id.pswd);
        err_mail = findViewById(R.id.error_mail);
        err_pass = findViewById(R.id.error_password);
        goRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginPage.this,registerPage.class));
                finish();
            }
        });


        //actiobar set && transparent bar
        getSupportActionBar().hide();
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int targetSdkVersion = 0;
            PackageInfo packageInfo = null;
            try {
                packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                targetSdkVersion = packageInfo.applicationInfo.targetSdkVersion;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }


        goLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = email_field.getText().toString();
                password = pass_field.getEditText().getText().toString();

                if(email.length()>0 && password.length()>0){
                    if(verifyAcc(email,password)){
                        sharepref.loggingIN(cr);
                        startActivity(new Intent(LoginPage.this,MainActivity.class));
                        finish();
                    }else{
                        err_pass.setText("");
                        err_mail.setText("");

                        AlertDialog.Builder abd = new AlertDialog.Builder(LoginPage.this);
                        abd.setTitle("Error");
                        abd.setMessage("email atau password anda salah atau tidak terdaftar")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //finish();
                                    }
                                });
                        abd.show();
                    }
                }else{
                    boolean bb = false;
                    if(email.length()==0){
                        err_mail.setText("Email tidak boleh kosong !");
                        err_pass.setText("");
                        bb = true;
                    }
                    if(password.length()==0){
                        err_pass.setText("Password tidak boleh kosong !");
                        if(bb){
                            err_mail.setText("Email tidak boleh kosong !");
                        }else{
                            err_mail.setText("");
                        }
                    }
                }


            }
        });



    }

    protected boolean verifyAcc (String mail, String pass){
        boolean res = false;
        if(b!=null){
            cr = BantuanSQL.fetchQuery("SELECT * FROM Member  where Email='"+mail.toLowerCase()+"'AND Password ='"+pass+"'");
            System.out.println(cr.getCount());
            res = cr.getCount()==1? true : false;
        }

        return res;
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
