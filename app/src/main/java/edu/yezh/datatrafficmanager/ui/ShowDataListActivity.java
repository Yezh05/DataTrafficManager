package edu.yezh.datatrafficmanager.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import edu.yezh.datatrafficmanager.R;
import edu.yezh.datatrafficmanager.adapter.ListViewAdapter;
import edu.yezh.datatrafficmanager.dao.BucketDao;
import edu.yezh.datatrafficmanager.dao.BucketDaoImpl;

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
        String subscriberID = bundle.getString("subscriberID");
        int networkType = bundle.getInt("networkType");
        BucketDao bucketDao = new BucketDaoImpl();
        ListView listViewData = (ListView)findViewById(R.id.ListViewData);
        ListViewAdapter adapter = new ListViewAdapter(ShowDataListActivity.this,subscriberID,networkType,bucketDao.getTrafficDataOfLastTwelveMonths(this,subscriberID,bundle.getInt("dataPlanStartDay"),networkType));
        listViewData.setAdapter(adapter);

    }

}
