package com.coolweather.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.coolweather.BuildConfig;
import com.coolweather.R;
import com.facebook.stetho.Stetho;

public class MainActivity extends Activity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);
        if(preferences.getString("weather",null)!=null){
            Intent intent=new Intent(this,WeatherActivity.class);
            startActivity(intent);
            finish();
        }
    }

    //    Button btn_dbcreate;
//    Button btn_tabledata;
//    Button btn_query;
//    DBHelper helper;
//    SQLiteDatabase db;
//
//    private static final String TAG="mainActivity";
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        if(BuildConfig.DEBUG){
//            Stetho.initializeWithDefaults(this);
//        }
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        bindViews();
//        helper = new DBHelper(MainActivity.this, "BookStore.db", null, 1);
//        btn_dbcreate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                db = helper.getWritableDatabase();
//                Connector.getDatabase();
//            }
//        });
//        btn_tabledata.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                db=helper.getWritableDatabase();
//                ContentValues values = new ContentValues();
//                values.put("author", "Herry");
//                values.put("price", 16.99);
//                values.put("page", 443);
//                values.put("name", "Dal Vic code");
//                db.insert("book", null, values);
//                values.clear();
//                values.put("author", "Ronald");
//                values.put("price", 13.99);
//                values.put("page", 343);
//                values.put("name", "Encrypt secret code");
//                db.insert("book", null, values);
//            }
//        });
//        btn_query.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Cursor cursor=db.query("book",null, null,
//                        null, null, null, null);
//                if(cursor.moveToFirst()){
//                    String author,name;
//                    float price;
//                    int page;
//                    do{
//                        author=cursor.getString(cursor.getColumnIndex("author"));
//                        name=cursor.getString(cursor.getColumnIndex("name"));
//                        price=cursor.getFloat(cursor.getColumnIndex("price"));
//                        page=cursor.getInt(cursor.getColumnIndex("page"));
//                        Log.i(TAG, "author="+author+",name="+name+",price="+price+",page="+page);
//                    }while (cursor.moveToNext());
//                }
//                cursor.close();
//            }
//        });
//    }
//
//    private void bindViews() {
//        btn_dbcreate = findViewById(R.id.btn_dbcreate);
//        btn_tabledata = findViewById(R.id.btn_tabledata);
//        btn_query = findViewById(R.id.btn_query);
//    }

}
