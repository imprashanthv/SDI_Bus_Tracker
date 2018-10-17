package dstudios.ml.sdibustracker;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

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

        ListAdapter adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,routes){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView tv = (TextView) view.findViewById(android.R.id.text1);
                tv.setTextColor(Color.WHITE);
                return view;
            }
        };

        ListView listView = (ListView)findViewById(R.id.listViewUser);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent driver = new Intent(getApplicationContext(),userActivity.class);
                ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        layout_user.this,findViewById(R.id.textView7),"user");
                driver.putExtra("routeno", i+1);
                startActivity(driver);
            }
        });
    }

}
