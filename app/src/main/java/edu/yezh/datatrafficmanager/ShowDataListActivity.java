package edu.yezh.datatrafficmanager;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import edu.yezh.datatrafficmanager.Dao.BucketDao;
import edu.yezh.datatrafficmanager.Dao.BucketDaoImpl;
import edu.yezh.datatrafficmanager.adapter.ListViewAdapter;

public class ShowDataListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_data_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        BucketDao bucketDao = new BucketDaoImpl();
        ListView listViewData = (ListView)findViewById(R.id.ListViewData);
        ListViewAdapter adapter = new ListViewAdapter(ShowDataListActivity.this,bucketDao.getLastSixMonthsTrafficData(this,bundle.getString("subscriberID"),bundle.getInt("dataPlanStartDay"), ConnectivityManager.TYPE_MOBILE));
        listViewData.setAdapter(adapter);


        /*FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

}
