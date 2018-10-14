package dstudios.ml.sdibustracker;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MasterPassword extends AppCompatActivity {

    TextView mp;
    Button sub;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_password);
        mp=findViewById(R.id.masterpasswordid);
        sub=findViewById(R.id.submitid);
        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int masterpass = Integer.parseInt(mp.getText().toString());
                //TODO: Handle NumberFormatException properly
                try {
                    if (masterpass == 159753) {
                        Intent driverActInt = new Intent(getApplicationContext(), layout_driver.class);
                        startActivity(driverActInt);
                    } else {
                        mp.clearComposingText();
                        mp.setText("");
                        mp.setHint("Please try again");
                    }
                }catch(NumberFormatException ne){
                    mp.setText("");
                    mp.setHint("Please try again");
                }catch(Exception e){
                    mp.setText("");
                    mp.setHint("Please try again");
                    mp.setHintTextColor(Color.WHITE);
                }
            }
        });
    }
}
