package dstudios.ml.sdibustracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

public class layout_user extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout_user);
        String[] routes = {
                "Route-1",
                "Route-2",
                "Route-3",
                "Route-4",
                "Route-5",
                "Route-6",
                "Route-7",
                "Route-8",
                "Route-9",
                "Route-10",
                "Route-11",
                "Route-12",
                "Route-13",
                "Route-14",
                "Route-15",
                "Route-16",
                "Route-17"};

        ListAdapter adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,routes);
        ListView listView = (ListView)findViewById(R.id.listViewUser);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent driver = new Intent(getApplicationContext(),userActivity.class);
                driver.putExtra("routeno", i+1);
                startActivity(driver);
            }
        });
    }

}
