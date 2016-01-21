package ft.findandtravel;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        ImageButton buttonMap = (ImageButton) findViewById(R.id.mapButton);

        buttonMap.setOnClickListener(startMap());
    }

    private View.OnClickListener startMap() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainMenu.this,MapsActivity.class);

                startActivity(intent);

            }
        };
    }
}
