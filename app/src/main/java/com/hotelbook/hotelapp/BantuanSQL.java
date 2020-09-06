package com.hotelbook.hotelapp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class BantuanSQL extends SQLiteOpenHelper {
    private Context context;
    static Cursor cursor = null;
    private static BantuanSQL b;
    private static SQLiteDatabase sqliteDB;


    public BantuanSQL(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public SQLiteDatabase getDatabase(){
        return sqliteDB;
    }
    public static final BantuanSQL getDBInstance (Context c, String dbname){
        insialisasi(c,dbname);
        return b;
    }

    private static void insialisasi(Context c, String dbname){
        if(b==null){
            if(!checksql(c,dbname)){
                try{
                    copyDataBase(c,dbname);
                }catch (IOException e){
                    System.out.println("insialisasi gagal gan");
                }
            }

            b = new BantuanSQL(c,dbname,null,1);
            sqliteDB = b.getWritableDatabase();
            System.out.println("instance db dari "+dbname+" telah sukses dibuat!!!");
        }
    }

    private static boolean checksql (Context c,String dbname){
        SQLiteDatabase cdb = null;
        try {
            String aPath = getDbPath(c,dbname);
            cdb = SQLiteDatabase.openDatabase(aPath,null,SQLiteDatabase.OPEN_READONLY);
            cdb.close();
        }catch (SQLiteException sqe){
            System.out.println("databasenya ngga ada gan!!!");
        }
        return  cdb != null ? true : false;
    }

    private static String getDbPath(Context aContext, String databaseName) {
        return "/data/data/" + aContext.getPackageName() + "/databases/"
                + databaseName;
    }

    private static void copyDataBase(Context aContext, String databaseName) throws IOException {

        InputStream myInput = aContext.getAssets().open(databaseName);

        String outFileName = getDbPath(aContext, databaseName);

        File f = new File("/data/data/" + aContext.getPackageName()
                + "/databases/");
        if (!f.exists())
            f.mkdir();

        OutputStream myOutput = new FileOutputStream(outFileName);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        myOutput.flush();
        myOutput.close();
        myInput.close();

        System.out.println("database berhasil di kopi");
    }

    public static Cursor fetchQuery (String aQuery){
        try{
            if(sqliteDB.isOpen()){
                sqliteDB.close();
            }
            sqliteDB = b.getWritableDatabase();
            cursor = null;
            cursor = sqliteDB.rawQuery(aQuery,null);

        }catch (Exception e){
            System.out.println("sorri your db ada masalah ==> "+e.getMessage());
            e.printStackTrace();
        }
        return cursor;
    }
    public static void doQuery (String aQuery){
        try{
            if(sqliteDB.isOpen()){
                sqliteDB.close();
            }
            sqliteDB = b.getWritableDatabase();
            sqliteDB.execSQL(aQuery);

        }catch (Exception e){
            System.out.println("sorri your db ada masalah ==> "+e.getMessage());
            e.printStackTrace();
        }
    }

}

class sharepref{
    public static SharedPreferences sp;
    public static SharedPreferences.Editor speditor;
    public static final String SP_EMAIL = "email";
    public static final String SP_PHONE = "phone";
    public static final String SP_FULLNAME = "fullname";
    public static final String SP_SALDO = "saldo";
    public static final String SP_SESS = "onSession";
    public static final String SP_USER_ID = "user_id";

    BantuanSQL b;

    public static void initializeSP(){
        //speditor = sp.edit();
        speditor.putInt(SP_SESS,0);
        speditor.putString(SP_EMAIL,"");
        speditor.putString(SP_PHONE,"");
        speditor.putString(SP_FULLNAME,"");
        speditor.putInt(SP_SALDO,0);
        speditor.commit();

    }

    public static void loggingIN (Cursor c){
        if(c.getCount()==1){
            if(!c.moveToFirst()){
                c.moveToFirst();
            }
            speditor.putInt(SP_SESS,1);
            speditor.putInt(SP_USER_ID,c.getInt(c.getColumnIndex("MemberId")));
            speditor.putString(SP_EMAIL,c.getString(c.getColumnIndex("Email")));
            speditor.putString(SP_PHONE,c.getString(c.getColumnIndex("Phone")));
            speditor.putString(SP_FULLNAME,c.getString(c.getColumnIndex("Fullname")));
            speditor.putInt(SP_SALDO,0);
            speditor.commit();
        }
    }

    public static void loggingOUT(){
        initializeSP();
    }

}
