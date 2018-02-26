package com.example.malabarista.formic;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;

/**
 * Created by malabarista on 11/10/2016.
 */

public class ActivitySettings extends Activity {
    DatabaseHelper myDb;
    EditText myTextPattern;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_settings);

       // myDb = new DatabaseHelper(this);//creates an object from our database class over in DatabaseHelper

        //this is where we cast the variables above to the matching stuff in the layout
       // myTextPattern = (EditText)findViewById(R.id.textPattern);


    }
}
