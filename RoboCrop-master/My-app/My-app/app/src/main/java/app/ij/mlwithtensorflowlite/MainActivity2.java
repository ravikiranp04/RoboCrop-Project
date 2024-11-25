package app.ij.mlwithtensorflowlite;
import android.net.Uri;
import android.util.Log;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView; // Import TextView
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // Find the button and subtext TextView by their IDs
        Button buttonScan = findViewById(R.id.buttonNavigate1);

        TextView scanSubtext = findViewById(R.id.scanSubtext);

        Button monitorCar = findViewById(R.id.monitorCar);

        // Set subtext for each button
        scanSubtext.setText("(Scan and Get the Plant Details)");


        // Set an OnClickListener for the buttons
        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity2", "Scan button clicked");
                Intent intent = new Intent(MainActivity2.this, MainActivity.class);
                startActivity(intent);
            }
        });





        monitorCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity2", "4th button clicked");

                // Create an Intent to open the URL in a web browser
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://192.168.4.1"));

                // Start the activity
                startActivity(intent);
            }
        });

    }
}
