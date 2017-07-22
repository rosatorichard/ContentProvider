package com.batchmates.android.contentprovider;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private String[] projections= new String[]{"id","name"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ButterKnife.bind(this);

    }

    @BindView(R.id.etEditText)
    EditText editText;

    @BindView(R.id.tvShowNames)
    TextView textView;


    public void addName(View view) {

        String name =editText.getText().toString();

        ContentValues values=new ContentValues();
        values.put(ContactProvider.name,name);

        Uri uri=getContentResolver().insert(ContactProvider.CONTENT_URL,values);
        Toast.makeText(this, "Added: "+name, Toast.LENGTH_SHORT).show();
        editText.setText(null);
    }

    public void showNames(View view) {
        Cursor cursor= getContentResolver().query(ContactProvider.CONTENT_URL,projections,null,null,null);
        String contactList="";

        if (cursor.moveToFirst())
        {
            do {
                String id=cursor.getString(cursor.getColumnIndex("id"));
                String name=cursor.getString(cursor.getColumnIndex("name"));
                contactList= contactList+id+":"+name+"\n";
            }while(cursor.moveToNext());
        }
        textView.setText(contactList);
    }
}
