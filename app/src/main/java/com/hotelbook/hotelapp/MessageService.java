package com.hotelbook.hotelapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.InetAddresses;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;

public class MessageService {
    String pesan="";
    String phonenumber;
    int serviceOpt = 0;
    Context context;

    public MessageService (String pesan,String phone, int ServiceOption){
        this.pesan=pesan;
        phonenumber = phone;
        serviceOpt = ServiceOption;
    }

    public MessageService (Context c,String pesan,String phone, int ServiceOption){
        this.pesan=pesan;
        phonenumber = phone;
        serviceOpt = ServiceOption;
        this.context = c;
    }

    public void setMessage(String m){this.pesan = m;}
    public void setPhoneNumber(String m){this.phonenumber = m;}

    public void sendNow(){
        if(serviceOpt==1){
            this.intentMethod();
        } else if(serviceOpt==2){
            this.smsMgrMethod();
        }
    }

    public void intentMethod(){
        try{
            Uri uri= Uri.parse("smsto:"+phonenumber);
            Intent i = new Intent(Intent.ACTION_SENDTO,uri);
            i.putExtra("HotelBooking",pesan);
            i.setType("vnd.android-dir/mms-sms");
            context.startActivity(i);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println(e);
        }
    }

    public void smsMgrMethod(){
        try {
            SmsManager sm = SmsManager.getDefault();
            sm.sendTextMessage(phonenumber,null,pesan,null,null);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println(e);
        }
    }

}
