package com.hotelbook.hotelapp;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class hotelBookingsAdapter extends BaseAdapter {
    ArrayList<Bookings> booklist;
    ArrayList<daftarHotel> dafhot;
    Context mycontext;

    public hotelBookingsAdapter(@NonNull Context context, ArrayList<Bookings> b) {
        booklist = b;
        dafhot = daftarHotel.buatArrlist(context);
        this.mycontext = context;
    }

    @Override
    public int getCount() {
        return booklist.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(booklist.get(position).enabled){
            ViewHolder mViewHolder = new ViewHolder();
            if(convertView==null){
                LayoutInflater mInflater = (LayoutInflater) mycontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = mInflater.inflate(R.layout.myorder_list_view,parent,false);
                mViewHolder.HtlName = convertView.findViewById(R.id.textView11);
                mViewHolder.HtlAddr =convertView.findViewById(R.id.textView13);
                mViewHolder.HtlOrder=convertView.findViewById(R.id.textView10);
                mViewHolder.HtlRating =convertView.findViewById(R.id.textView14);
                mViewHolder.HtlPrice =convertView.findViewById(R.id.textView15);
                mViewHolder.INdate = convertView.findViewById(R.id.textView18);
                mViewHolder.OUTdate =convertView.findViewById(R.id.textView19);
                mViewHolder.cancelBook = convertView.findViewById(R.id.cancel_book);
                convertView.setTag(mViewHolder);
            }else {
                mViewHolder = (ViewHolder) convertView.getTag();
            }


            mViewHolder.HtlOrder.setText("order id = "+booklist.get(position).order_id);
            mViewHolder.HtlName.setText("at Hotel "+dafhot.get(booklist.get(position).hotel_id-1).hotelname);
            mViewHolder.HtlAddr.setText(dafhot.get(booklist.get(position).hotel_id-1).hoteladdress);
            mViewHolder.INdate.setText(booklist.get(position).startdate);
            mViewHolder.OUTdate.setText(booklist.get(position).enddate);
            mViewHolder.HtlPrice.setText("Total Payment = IDR "+MainActivity.moneyConverter(booklist.get(position).totalprice));

            if(dafhot.get(booklist.get(position).hotel_id-1).rating==3){
                mViewHolder.HtlRating.setText("★★★");
            } else if(dafhot.get(booklist.get(position).hotel_id-1).rating==4){
                mViewHolder.HtlRating.setText("★★★★");
            }

            mViewHolder.cancelBook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder abd = new AlertDialog.Builder(mycontext);
                    abd.setTitle("Warning !");
                    abd.setMessage("Are you sure wanna cancel this order ? ")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    hasbeenCancelled(position);
                                    //finish();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    abd.show();
                }
            });
        }

        return convertView;
    }

    static class ViewHolder {
        TextView HtlName,HtlOrder,HtlRating,HtlAddr,HtlPrice,INdate,OUTdate;
        Button cancelBook;
    }

    private void deleteOrder(Bookings bookings){
        BantuanSQL b = BantuanSQL.getDBInstance(mycontext,LoginPage.NAMA_DATABASE);
        if(bookings != null){
            BantuanSQL.doQuery("DELETE FROM Booking WHERE BookingId = "+bookings.order_id);
        }
    }

    private void hasbeenCancelled(int pos){
        deleteOrder(booklist.get(pos));
        booklist.remove(pos);
        AlertDialog.Builder abd = new AlertDialog.Builder(mycontext);
        abd.setTitle("Cancellation has been granted");
        abd.setMessage("thank you for having us, have a nice day")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //finish();
                        notifyDataSetChanged();
                    }
                });
        abd.show();
    }
}
