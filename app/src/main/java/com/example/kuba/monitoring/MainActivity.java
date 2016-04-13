package com.example.kuba.monitoring;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    SitesDictController controller;
    MainCursorAdapter cursorAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //get cursor
        controller = new SitesDictController(this);
        controller.open();
        Cursor cursor = controller.readAll();

        //set cursoradapter for listview
        ListView listView = (ListView) findViewById(R.id.listView);
        cursorAdapter = new MainCursorAdapter(this, cursor);
        listView.setAdapter(cursorAdapter);

        //set onClickListener
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                DeleteDialogFragment deleteDialogFragment = new DeleteDialogFragment();
                deleteDialogFragment.controller = controller;
                deleteDialogFragment.myId = (int) id;
                deleteDialogFragment.cursorAdapter = cursorAdapter;
                deleteDialogFragment.show(getFragmentManager(),"Confirm");
                return false;//System.out.println();
            }
        });

        //set Alarm Manager
        setAlarmManager();
    }

    private void setAlarmManager() {
        Calendar cal = Calendar.getInstance();
        Intent intent = new Intent(this, MyService.class);
        PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 6*1000, pintent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void addRecord(View view) {
        TextView input = (TextView) findViewById(R.id.editText);
        String site_name =  input.getText().toString();
        if(site_name != null && site_name.length() > 0)
            controller.insert(site_name,"state");
        input.setText("");
        cursorAdapter.refresh();
    }

    public class MainCursorAdapter extends CursorAdapter {
        public MainCursorAdapter(Context context, Cursor c) {
            super(context, c, 0);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(R.layout.site_record, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView site_name = (TextView) view.findViewById(R.id.site_name);
            site_name.setText(cursor.getString(1));
        }

        public void refresh() {
            cursorAdapter.changeCursor(controller.readAll());
            cursorAdapter.notifyDataSetChanged();

        }
    }
}

