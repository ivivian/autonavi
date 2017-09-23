package cn.zzv.autonavi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Toast toast = Toast.makeText(MainActivity.this, "简单的提示信息", Toast.LENGTH_SHORT);
        //toast.show();

        //Sleep(10000);
        //registerReceiver(mbatteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        //registerReceiver(mbatteryReceiver, new IntentFilter(Intent.ACTION_BOOT_COMPLETED));
        //registerReceiver(mbatteryReceiver, new IntentFilter(Intent.ACTION_MEDIA_MOUNTED));
        //int myproid=android.os.Process.myPid();
        //启动服务
        Intent intentservice=new Intent(MainActivity.this, mainservice.class);
        startService(intentservice);
        finish();
    }
}
