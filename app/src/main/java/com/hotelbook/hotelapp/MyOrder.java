package com.hotelbook.hotelapp;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class MyOrder extends AppCompatActivity {
    ArrayList<Bookings> myBookingList;
    ArrayList<daftarHotel> myHotelList;

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);

        //action bar
        getSupportActionBar().setTitle("My Orders");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.myHotelList = daftarHotel.buatArrlist(this);
        this.myBookingList = Bookings.getArrlist(this);

        listView = findViewById(R.id.lystvyew);
        hotelBookingsAdapter hba = new hotelBookingsAdapter(MyOrder.this,myBookingList);
        listView.setAdapter(hba);


    }
}

class Bookings{
    int order_id,hotel_id;
    String totalprice;
    String startdate,enddate;
    boolean enabled = true;

    public Bookings(){}
    public Bookings(int order_id,int hotel_id,String totalprice,String start,String end){
        this.order_id=order_id;
        this.hotel_id=hotel_id;
        this.totalprice=totalprice;
        this.startdate = start;
        this.enddate = end;
    }

    public void deleteThis(Context c){
        BantuanSQL b = BantuanSQL.getDBInstance(c,LoginPage.NAMA_DATABASE);
        enabled=false;
        BantuanSQL.doQuery("DELETE from Booking WHERE BookingId ="+this.order_id);
    }

    public static ArrayList<Bookings> getArrlist(Context c){
        ArrayList<Bookings> book = new ArrayList<Bookings>();
        BantuanSQL b = BantuanSQL.getDBInstance(c,LoginPage.NAMA_DATABASE);
        int id= sharepref.sp.getInt(sharepref.SP_USER_ID,0);
        Cursor cursor = BantuanSQL.fetchQuery("SELECT * FROM Booking WHERE MemberId="+id);

        if(cursor!=null && cursor.getCount()>0){
            if(cursor.moveToFirst()){
                do{
                    book.add(
                            new Bookings(
                                    cursor.getInt(cursor.getColumnIndex("BookingId")),
                                    cursor.getInt(cursor.getColumnIndex("HotelId")),
                                    cursor.getString(cursor.getColumnIndex("TotalPrice")),
                                    cursor.getString(cursor.getColumnIndex("StartDate")),
                                    cursor.getString(cursor.getColumnIndex("EndDate"))
                            )
                    );
                }while (cursor.moveToNext());
            }
        }

        return book;
    }
}