package com.hotelbook.hotelapp;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class HotelDetail extends AppCompatActivity implements OnMapReadyCallback {
    TextView hotelname, hoteladdr, hotelphone, hotelprice,hotelstar;
    ImageView imagehotel;
    Button bookButton, cekin_picker, cekout_picker;
    daftarHotel hotelku;
    Calendar kalender;
    SimpleDateFormat simpleDateFormat;
    String today, cek_in_date, cek_out_date,date_temporary;
    DatePickerDialog dpd;
    boolean readyToCheckOut = false;
    int hotel_ppn, total_price=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_detail);

        //initialize
        hotelname = findViewById(R.id.textView25);
        hotelstar = findViewById(R.id.textView22);
        hoteladdr = findViewById(R.id.textView9_addr);
        hotelphone = findViewById(R.id.textView10_phone);
        //hotelprice = findViewById(R.id.textView11_price);
        imagehotel = findViewById(R.id.imageView2);
        cekin_picker = findViewById(R.id.cekindate);
        cekin_picker.setText("select check in date");
        cekout_picker = findViewById(R.id.cekoutdate);
        cekout_picker.setText("select check out date");
        bookButton = findViewById(R.id.button_booking);
        today = this.getTodayDate();
        cek_in_date = today;
        cek_out_date=today;

        //action bar
        getSupportActionBar().setTitle("Hotel Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //getExtras
        Bundle bundle = getIntent().getExtras();
        hotelku = bundle.getParcelable("infoHotel");
        hotel_ppn = hotelku.pricing;
        basicPrice();
                //layout fill

        Picasso.get().load(hotelku.imgurl)
                .resize(getScreenWidth(),175)
                .into(imagehotel);
        hotelname.setText(hotelku.hotelname);
        hoteladdr.setText(hotelku.hoteladdress);
        hotelphone.setText(hotelku.hotelphone);
        if(hotelku.rating==3){
            hotelstar.setText("★★★");
        }else if(hotelku.rating==4){
            hotelstar.setText("★★★★");
        }

        //Configure map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        kalender = Calendar.getInstance();

        //DATE PICKER
        try {
            openCalenderPicker(cekin_picker,cekout_picker);
            openCalenderPicker(cekout_picker);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        bookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(cekOrderAgain()){
                        dealOrder();
                    }else{
                        AlertDialog.Builder abd = new AlertDialog.Builder(HotelDetail.this);
                        abd.setTitle("Something Went Wrong");
                        abd.setMessage("Check your order again sir !")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //finish();
                                    }
                                });
                        abd.show();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        //DEBUGGING PURPOSE ONLY
        //this.getTodayDate();

    }

    public void openCalenderPicker(final Button butt,final Button butt2) throws ParseException {
        kalender = Calendar.getInstance();
        final Date nowadays = new SimpleDateFormat("dd-MM-yyyy").parse(today);
        final Date[] dateOfCheckIn = new Date[1];
        butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int yr = kalender.get(Calendar.YEAR);
                final int mo = kalender.get(Calendar.MONTH);
                final int day = kalender.get(Calendar.DAY_OF_MONTH);

                dpd = new DatePickerDialog(HotelDetail.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        date_temporary = dayOfMonth+"-"+(month+1)+"-"+year;
                        butt.setText("Check In\n"+date_temporary);
                        try {
                            dateOfCheckIn[0] = new SimpleDateFormat("dd-MM-yyyy").parse(date_temporary);
                            if(!compareDate(dateOfCheckIn[0],nowadays)){
                                butt.setText("select check in date");
                                butt2.setText("select check out date");
                                basicPrice();
                                AlertDialog.Builder abd = new AlertDialog.Builder(HotelDetail.this);
                                abd.setTitle("Error");
                                abd.setMessage("Date can't be earlier than today "+today)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //finish();
                                            }
                                        });
                                abd.show();
                            }else {
                                cek_in_date = date_temporary;
                                butt2.setText("select check out date");
                                basicPrice();
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                },yr,mo,day);
                dpd.show();
            }

        });
    }

    public void openCalenderPicker(final Button butt) throws ParseException {
        kalender = Calendar.getInstance();
        if(this.cek_in_date.length()<8){
            this.cek_in_date = today;
        }
        final Date nowadays = new SimpleDateFormat("dd-MM-yyyy").parse(today);
        final Date[] dateOfCheckout = new Date[1];

        butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int yr = kalender.get(Calendar.YEAR);
                final int mo = kalender.get(Calendar.MONTH);
                final int day = kalender.get(Calendar.DAY_OF_MONTH);
                //System.out.println(cek_in_date);
                dpd = new DatePickerDialog(HotelDetail.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        date_temporary = dayOfMonth+"-"+(month+1)+"-"+year;
                        butt.setText("Check Out\n"+date_temporary);
                        try {
                            Date cekInDate = new SimpleDateFormat("dd-MM-yyyy").parse(cek_in_date);
                            dateOfCheckout[0] = new SimpleDateFormat("dd-MM-yyyy").parse(date_temporary);
                            if(!(compareDate(nowadays,cekInDate,dateOfCheckout[0]))){
                                butt.setText("select check out date");
                                basicPrice();
                                AlertDialog.Builder abd = new AlertDialog.Builder(HotelDetail.this);
                                abd.setTitle("Error");
                                abd.setMessage("Date can't be earlier than your check in date ")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //finish();
                                            }
                                        });
                                abd.show();
                            }else {
                                cek_out_date = date_temporary;
                                total_price = hotel_ppn*getSelisihHari(cek_in_date,cek_out_date);
                                updatedPrice(total_price,getSelisihHari(cek_in_date,cek_out_date));
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                },yr,mo,day);
                dpd.show();
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        CameraPosition cam = CameraPosition.builder()
                .target(new LatLng(hotelku.latitude,hotelku.longtitude))
                .zoom(16)
                .bearing(0)
                .tilt(45)
                .build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cam));

        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(hotelku.latitude,hotelku.longtitude))
                .title("Hotel "+hotelku.hotelname));
    }

    private int getScreenWidth(){
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        return width;
    }

    private String getTodayDate(){
        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        kalender = Calendar.getInstance();
        String s = simpleDateFormat.format(kalender.getTime());
        System.out.println(s);
        return s;
    }

    private boolean compareDate(Date today, Date checkIn, Date checkOut){
        int score =0;
        if(compareDate(checkIn,today)&&compareDate(checkOut,today)){
            score++;
        }
        if(checkOut.after(checkIn)){
            score++;
        }

        //System.out.println(score);
        if(score==2){
            return true;
        }else {
            return false;
        }

    }

    private boolean compareDate(Date checkIn, Date today){
        if(checkIn.after(today)||checkIn.compareTo(today)==0){
            return true;
        }else{
            return false;
        }
    }

    private int getSelisihHari(String ckin,String ckout) throws ParseException {
        int day;
        Date d1 = new SimpleDateFormat("dd-MM-yyyy").parse(ckin);
        Date d2 = new SimpleDateFormat("dd-MM-yyyy").parse(ckout);
        long x = d2.getTime()-d1.getTime();

        if(x>0){
            return (int) (x/86400000);
        }else if(x==0){
            return 0;
        }else{
            return -1;
        }
    }

    private void dealOrder(){
        BantuanSQL b = BantuanSQL.getDBInstance(HotelDetail.this,LoginPage.NAMA_DATABASE);
        Cursor c = BantuanSQL.fetchQuery("SELECT * FROM Booking");
        int order_id = 1;
        while(BantuanSQL.fetchQuery("SELECT * FROM Booking where BookingId ="+order_id).getCount()>0){
            order_id++;
        }
        int member_id = sharepref.sp.getInt(sharepref.SP_USER_ID,0);

        String queri = "INSERT INTO Booking ('BookingId', 'MemberId', 'HotelId', 'StartDate', 'EndDate', 'TotalPrice') " +
                "VALUES ("+order_id+", "+member_id+", "+hotelku.idhotel+", '"+this.cek_in_date+"', '"+cek_out_date+"',"+total_price+")";
        BantuanSQL.doQuery(queri);

        AlertDialog.Builder abd = new AlertDialog.Builder(HotelDetail.this);

        String phonenumb = sharepref.sp.getString(sharepref.SP_PHONE,"");

        //CHANGE YOUR MESSAGE HERE !!!!
        String message = "Congrats, your order for hotel "+hotelku.hotelname
                +" from "+cek_in_date+" until "+cek_out_date+" is success "
                +"more details check in My Order at hotelBooking app";

        //FOR MESSAGE SERVICE
        MessageService messageService = new MessageService(message,phonenumb,2);
        messageService.sendNow();

        abd.setTitle("Congratulation");
        abd.setMessage("Your order in hotel "+hotelku.hotelname+" successfully done, check your order in My Booking pages")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        abd.show();
    }

    private boolean cekOrderAgain() throws ParseException {
        int score =0;
        Date a = new SimpleDateFormat("dd-MM-yyyy").parse(today);
        Date b = new SimpleDateFormat("dd-MM-yyyy").parse(cek_in_date);
        Date c = new SimpleDateFormat("dd-MM-yyyy").parse(cek_out_date);
        if(getSelisihHari(cek_in_date,cek_out_date)>0){
            score++;
        }
        if(total_price>0){
            score++;
        }
        if(compareDate(a,b,c)){
            score++;
        }
        if(readyToCheckOut){
            score++;
        }

        if(score==4){
            return true;
        }else {
            return false;
        }


    }

    private void basicPrice(){
        readyToCheckOut = false;
        if(hotelku!=null){
            String pricee = ""+hotelku.pricing;
            TextView t1 = findViewById(R.id.textView29);
            TextView t2 = findViewById(R.id.textView30);
            t1.setText("Price per Night");
            t2.setText("IDR "+MainActivity.moneyConverter(pricee));
        }
    }

    private void updatedPrice(int newprice, int day){
        if(hotelku!=null){
            readyToCheckOut = true;
            String pricee = ""+newprice;
            TextView t1 = findViewById(R.id.textView29);
            TextView t2 = findViewById(R.id.textView30);
            if(day>1){
                t1.setText("Total for "+day+" nights :");
                t2.setText("IDR "+MainActivity.moneyConverter(pricee));
            }else if(day==1){
                t1.setText("Total for "+day+" night :");
                t2.setText("IDR "+MainActivity.moneyConverter(pricee));
            }else{
                basicPrice();
            }
        }
    }
}
