package com.hotelbook.hotelapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import static android.os.FileUtils.copy;

public class hotelVA extends RecyclerView.Adapter<hotelVA.holdr> {

    ArrayList<daftarHotel> datalist;
    public hotelVA(ArrayList<daftarHotel> datalist){
        this.datalist = datalist;
    }

    @NonNull
    @Override
    public holdr onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater li = LayoutInflater.from(viewGroup.getContext());
        View v = li.inflate(R.layout.hotel_list_view,viewGroup,false);
        return new holdr(v);
    }

    public class holdr extends RecyclerView.ViewHolder {
        private TextView hotel_name, hotel_addr, hotel_price,hotel_star;
        private ImageView hotel_picture;
        FrameLayout flout;
        public holdr(@NonNull View itemView) {
            super(itemView);
            this.hotel_name = (TextView) itemView.findViewById(R.id.hotel_name);
            this.hotel_addr = (TextView) itemView.findViewById(R.id.hotel_addr);
            this.hotel_price = (TextView) itemView.findViewById(R.id.hotel_price);
            this.hotel_picture = (ImageView) itemView.findViewById(R.id.hotel_pict);
            this.flout = (FrameLayout) itemView.findViewById(R.id.fr_lyout);
            this.hotel_star = (TextView) itemView.findViewById(R.id.hotel_rating);
        }
    }

    @Override
    public void onBindViewHolder(final holdr viewHolder, final int i) {
        viewHolder.hotel_name.setText(datalist.get(i).hotelname);
        String moneyy = ""+datalist.get(i).pricing;
        viewHolder.hotel_price.setText("IDR "+MainActivity.moneyConverter(moneyy));
        viewHolder.hotel_addr.setText(datalist.get(i).hoteladdress);

        if(datalist.get(i).rating==2){
            viewHolder.hotel_star.setText("★★");
        }else if(datalist.get(i).rating==3){
            viewHolder.hotel_star.setText("★★★");
        }else if(datalist.get(i).rating==4){
            viewHolder.hotel_star.setText("★★★★");
        }else if(datalist.get(i).rating==5){
            viewHolder.hotel_star.setText("★★★★★");
        }

        Picasso.get().load(datalist.get(i).imgurl)
                .resize(390,450)
                .into(viewHolder.hotel_picture);

        final int angka = i;
        viewHolder.flout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),HotelDetail.class);
                intent.putExtra("infoHotel",datalist.get(angka));
                v.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return (datalist != null) ? datalist.size() : 0;
    }

}



class daftarHotel implements Parcelable{
    public String hotelname,hoteladdress,hotelphone;
    public String imgurl;
    Float latitude,longtitude;
    public int rating,pricing,idhotel;

    public daftarHotel(){}
    public daftarHotel(int id,String hotelname,int price,String phonenumber,String urlimage,String addr,Float ltd,Float alt,int rating){
        this.idhotel = id;
        this.hotelname = hotelname;
        this.pricing = price;
        this.hotelphone = phonenumber;
        this.imgurl = urlimage;
        this.hoteladdress =addr;
        this.longtitude = ltd;
        this.latitude = alt;
        this.rating = rating;
    }

    protected daftarHotel(Parcel in) {
        hotelname = in.readString();
        hoteladdress = in.readString();
        hotelphone = in.readString();
        imgurl = in.readString();
        if (in.readByte() == 0) {
            latitude = null;
        } else {
            latitude = in.readFloat();
        }
        if (in.readByte() == 0) {
            longtitude = null;
        } else {
            longtitude = in.readFloat();
        }
        rating = in.readInt();
        pricing = in.readInt();
        idhotel = in.readInt();
    }

    public static final Creator<daftarHotel> CREATOR = new Creator<daftarHotel>() {
        @Override
        public daftarHotel createFromParcel(Parcel in) {
            return new daftarHotel(in);
        }

        @Override
        public daftarHotel[] newArray(int size) {
            return new daftarHotel[size];
        }
    };

    public static daftarHotel buatClass(int i){
        Cursor c = BantuanSQL.fetchQuery("SELECT * FROM Hotel WHERE HotelId = "+i);
        if(!c.moveToFirst()){
            c.moveToFirst();
        }
        String hotelname = c.getString(c.getColumnIndex("HotelName"));
        String hoteladdr = c.getString(c.getColumnIndex("HotelAddress"));
        String hotelphone = c.getString(c.getColumnIndex("HotelPhone"));
        int hotelprice = c.getInt(c.getColumnIndex("Price"));
        Float latitude = c.getFloat(c.getColumnIndex("Latitude"));
        Float longtitude = c.getFloat(c.getColumnIndex("Longtitude"));

        String urli = "";
        if(i==1){urli="https://lh5.googleusercontent.com/p/AF1QipP2RePhsB_PJN5oh5laIgp7EYTdew3siccRq_ae=w408-h272-k-no";}
        else if(i==2){urli="https://lh4.googleusercontent.com/proxy/UUNduvFiu-iDhgdi-yHWdMPeqLVsUc4UcKCnC6Qvvsb84-4KjSLbp33Vx8cS2EkNxrQwaAfdpnznfL3-xRaq0xX05RikAONSr0IjT3VS_q3aIJOCh9vRDmnz28MAb-wFFd3zDQ4ArRSpBV_Qp33Z1mlIxq-t6w=w437-h240-k-no";}
        else if(i==3){urli="https://lh5.googleusercontent.com/p/AF1QipPiXPa0DyBZdyAFskHGIoPJI7v8-I8dsuKkcTWw=w427-h240-k-no";}
        else if(i==4){urli="https://lh5.googleusercontent.com/p/AF1QipNB3VjHwzM-gSGDN1CecgfWHlpeIIRGv_EY-4KP=w408-h306-k-no";}
        else if(i==5){urli="https://lh5.googleusercontent.com/p/AF1QipMlanJHyyRyD2yXXxrrBOYWwqWi6EfdvrloN75-=w408-h612-k-no";}

        return new daftarHotel(i,hotelname,hotelprice,hotelphone,urli,hoteladdr,longtitude,latitude,i%2==0?3:4);
    }

    public static ArrayList<daftarHotel> buatArrlist(Context cntx){
        BantuanSQL b = BantuanSQL.getDBInstance(cntx,LoginPage.NAMA_DATABASE);
        ArrayList<daftarHotel> ar = new ArrayList<daftarHotel>();
        Cursor c = BantuanSQL.fetchQuery("SELECT * FROM Hotel");
        System.out.println(c.getCount());
        for(int x=1;x<=c.getCount();x++){
            ar.add(buatClass(x));
        }
        return ar;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(hotelname);
        dest.writeString(hoteladdress);
        dest.writeString(hotelphone);
        dest.writeString(imgurl);
        if (latitude == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeFloat(latitude);
        }
        if (longtitude == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeFloat(longtitude);
        }
        dest.writeInt(rating);
        dest.writeInt(pricing);
        dest.writeInt(idhotel);
    }
}