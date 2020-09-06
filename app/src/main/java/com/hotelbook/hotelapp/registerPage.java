package com.hotelbook.hotelapp;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class registerPage extends AppCompatActivity {
    Button submit_registration;
    TextView goLogin;
    EditText formNama,formEmail,formTelp;
    TextInputLayout formPass;
    BantuanSQL b = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);
        b = BantuanSQL.getDBInstance(this,LoginPage.NAMA_DATABASE);
        goLogin = findViewById(R.id.goto_login_text);
        submit_registration = findViewById(R.id.submitRegister);
        formNama = findViewById(R.id.editText);
        formEmail = findViewById(R.id.editText6);
        formPass = findViewById(R.id.newpass);
        formTelp = findViewById(R.id.editText5);

        //action bar hide && bar transparent
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
            if(targetSdkVersion>=Build.VERSION.SDK_INT){
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }

        getSupportActionBar().hide();
        goLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(registerPage.this,LoginPage.class));
                finish();
            }
        });

        submit_registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(verifyRegist(formNama,formEmail,formTelp,formPass)){
                    setordata(formNama,formEmail,formTelp,formPass.getEditText());
                    Cursor cursor = null;
                    cursor = BantuanSQL.fetchQuery("SELECT * FROM Member where Email='"+formEmail.getText().toString().toLowerCase()+"'AND Password ='"+formPass.getEditText().getText().toString()+"'");
                    if (cursor.getCount()>0){
                        sharepref.loggingIN(cursor);
                    }

                    startActivity(new Intent(registerPage.this,MainActivity.class));
                    finish();
                }
            }
        });



    }

    protected boolean verifyRegist(EditText name,EditText mail,EditText phone,TextInputLayout pswd){
        int score =0;
        ayoREGEX ay = new ayoREGEX();

        if(ay.checkname(name.getText().toString())){
            score++;
        }else {
            name.setError("Full Name must contain at least 2 words");
        }

        if(ay.validatePsswd(pswd.getEditText().getText().toString())){
            score++;
        }else {
            pswd.getEditText().setError(ay.getPassErrorMsg());
        }

        if(ay.validateMail(mail.getText().toString())&&this.cekMail(mail.getText().toString())){
            score++;

        }else if(!this.cekMail(mail.getText().toString())){
            mail.setError("this email has taken with another");

        }else{
            mail.setError("email must contains @ and valid domain");
        }

        if(ay.checkPhoneNumber(phone.getText().toString())){
            score++;
        }else{
            phone.setError("phone number must be started with +62");
        }



        if(score==4){
            return true;
        }else {
            return false;
        }
    }

    protected void setordata(EditText name,EditText mail,EditText phone,EditText pswd){
        Cursor c1 = BantuanSQL.fetchQuery("SELECT * FROM Member");
        int userid= c1.getCount()+1;
        String namauser = name.getText().toString();
        String emailuser = mail.getText().toString();
        String phoneuser = phone.getText().toString();
        String passuser = pswd.getText().toString();
        String query = "INSERT INTO 'Member' ('MemberId', 'Fullname', 'Email', 'Password', 'Phone') VALUES ("+userid+",'"+namauser+"', '"+emailuser.toLowerCase()+"', '"+passuser+"', '"+phoneuser+"')";
        BantuanSQL.doQuery(query);
    }
    protected boolean cekMail (String s){
        boolean res=false;

        if(b!=null){
            Cursor c = BantuanSQL.fetchQuery("SELECT * FROM Member  where Email='"+s.toLowerCase()+"'");
            res = c.getCount()==0? true : false;
        }

        return res;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(registerPage.this,LoginPage.class));
        finish();
    }
}

class ayoREGEX {
    protected int errorcode=0;
    public boolean validateMail(String s){
        String thepattern = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+(?:\\.[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+)*@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";
        Pattern pattern = Pattern.compile(thepattern);
        Matcher matcher = pattern.matcher(s);

        return matcher.matches();

    }

    public boolean validatePsswd(String s){
        String thepattern = "[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]{5,}";
        Pattern pattern = Pattern.compile(thepattern);
        Matcher matcher = pattern.matcher(s);

        if(!matcher.matches()){
            this.errorcode = 1;
            return false;
        }else{
            int countchar=0;
            int countint=0;
            for(int x=0;x<s.length();x++){
                if(s.charAt(x)>='0' && s.charAt(x)<='9'){
                    countint++;
                }else if(s.charAt(x)>='a' && s.charAt(x)<='z') countchar++;
                else if(s.charAt(x)>='A' && s.charAt(x)<='Z') countchar++;
            }

            if(countchar==0){
                this.errorcode=3;
                return false;
            } else if(countint==0){
                this.errorcode=2;
                return false;
            }else{
                this.errorcode=0;
                return true;
            }
        }

    }

    public String getPassErrorMsg(){
        int a = this.errorcode;
        if(a==1){ //error panjang password <5
            return "password must contain minimun of 5 character";
        }else if(a==2){ //error tanpa angka
            return "password must contain minimum one of numeric 0-9";
        }else if(a==3){ //error tanpa karakter
            return "password must contain minimum one of character a-z or A-Z";
        }else{
            return "";
        }
    }

    public boolean checkPhoneNumber(String pn){
        String thepattern = "[+][6][2][0-9]{6,}";
        Pattern pattern = Pattern.compile(thepattern);
        Matcher matcher = pattern.matcher(pn);

        return matcher.matches();
    }

    public boolean checkname(String s){
        boolean status = false;

        for(int x=0;x<s.length()-1;x++){
            if(s.charAt(x)==' ' && ((s.charAt(x-1)>='a' && s.charAt(x-1)<='z')||(s.charAt(x-1)>='A' && s.charAt(x-1)<='Z'))&&((s.charAt(x+1)>='a' && s.charAt(x+1)<='z')||(s.charAt(x+1)>='A' && s.charAt(x+1)<='Z'))){
                status=true;
                break;
            }
        }

        return status;
    }
}

