package raihana.msd.rgl;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //setting timer untuk 1 detik
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Pergi ke activity lain
                Intent gogetstarted = new Intent(SplashActivity.this,LoginActivity.class);
                startActivity(gogetstarted);
                finish();
            }
        },1000);
        //1000 ms = 1s
    }

}