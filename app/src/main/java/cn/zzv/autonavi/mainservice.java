package cn.zzv.autonavi;
        import java.io.BufferedReader;
        import java.io.DataOutputStream;
        import java.io.IOException;
        import java.io.InputStreamReader;
        import java.net.HttpURLConnection;
        import java.net.URL;
        import java.util.List;
        import java.util.Timer;
        import java.util.TimerTask;

        import android.app.ActivityManager;
        import android.app.ActivityManager.RunningAppProcessInfo;
        import android.app.Service;
        import android.content.BroadcastReceiver;
        import android.content.Context;
        import android.content.Intent;
        import android.content.IntentFilter;
        import android.os.BatteryManager;
        import android.os.Handler;
        import android.os.IBinder;
        import android.os.Message;
        import android.os.StrictMode;
        import android.util.Log;
        import android.widget.Toast;

public class mainservice extends Service {
    private static final String TAG="Test";
    private int fcounttime=0;
    private int fisnaviing=0;

    @Override
    //Service时被调用
    public void onCreate()
    {
        Log.i(TAG, "Service onCreate--->");
        super.onCreate();
        fisnaviing=0;
        //--------------
        registerReceiver(mbatteryReceiver, new IntentFilter(Intent.ACTION_BOOT_COMPLETED));
        registerReceiver(mbatteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        registerReceiver(mbatteryReceiver, new IntentFilter(Intent.ACTION_MEDIA_MOUNTED));
    }



    @Override
    //当调用者使用startService()方法启动Service时，该方法被调用
    public void onStart(Intent intent, int startId)
    {
        Log.i(TAG, "Service onStart--->");
        //super.onStart(intent, startId);
    }

    @Override
    //当Service不在使用时调用
    public void onDestroy()
    {
        Log.i(TAG, "Service onDestroy--->");
        super.onDestroy();
    }

    @Override
    //当使用startService()方法启动Service时，方法体内只需写return null
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    private BroadcastReceiver mbatteryReceiver=new BroadcastReceiver()
    {
        public void onReceive(Context context, Intent intent)
        {
            String action =intent.getAction();
            if(Intent.ACTION_BATTERY_CHANGED.equals(action))
            {
                int status=intent.getIntExtra("status",BatteryManager.BATTERY_STATUS_UNKNOWN);
                //String fstrstatus=Integer.toString(status);
                if(status==BatteryManager.BATTERY_STATUS_CHARGING)
                {
                    fcounttime=0;
                    if(isRunning(mainservice.this,"com.baidu.BaiduMap")==false)
                    {
                        Toast.makeText(getApplicationContext(), "没启动",Toast.LENGTH_SHORT).show();
                        fisnaviing=startrecording();//启动录像程序
                    }
                    else
                        Toast.makeText(getApplicationContext(), "已经启动",Toast.LENGTH_SHORT).show();

                    if(isRunning(mainservice.this,"cn.zzv.navi")==false)
                    {
                        //Intent newIntent = new Intent(context, MainActivity.class);
                        //newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  //注意，必须添加这个标记，否则启动会失败
                        //context.startActivity(newIntent);
                    }

                    //Toast.makeText(getApplicationContext(), "电量变化",Toast.LENGTH_SHORT).show();

                }
                else if(status==BatteryManager.BATTERY_STATUS_FULL)
                {
                    fcounttime=0;
                    if(isRunning(mainservice.this,"com.baidu.BaiduMap")==false)
                    {
                        Toast.makeText(getApplicationContext(), "没启动22",Toast.LENGTH_SHORT).show();
                        fisnaviing=startrecording();//启动录像程序
                    }
                    else
                        Toast.makeText(getApplicationContext(), "已经启动22",Toast.LENGTH_SHORT).show();


                    if(isRunning(mainservice.this,"cn.zzv.navi")==false)
                    {
                        //Intent newIntent = new Intent(context, MainActivity.class);
                        //newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  //注意，必须添加这个标记，否则启动会失败
                        //context.startActivity(newIntent);
                    }

                    //Toast.makeText(getApplicationContext(), "电量满",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    //如果未充电就是未插入电源，则关机
                    //Toast.makeText(getApplicationContext(), "电量其他",Toast.LENGTH_SHORT).show();
                    //if(fcounttime<=20)
                    //{
                    //	fcounttime=fcounttime+1;
                    //	Toast.makeText(getApplicationContext(), ("不在充电"+fcounttime),Toast.LENGTH_SHORT).show();
                    //}
                    //else
                    //{
                    Toast.makeText(getApplicationContext(), "不在充电！",Toast.LENGTH_SHORT).show();
                    if (fcounttime==0)
                    {
                        new Handler().postDelayed(new Runnable(){
                            public void run() {
                                //execute the task
                                if(isRunning(mainservice.this,"com.baidu.BaiduMap")==true)
                                    shutdown();
                            }

                        }, 10000);
                        fcounttime=1;
                    }
                    //}

                }

            }
            else if (Intent.ACTION_BOOT_COMPLETED.equals(action))
            {
                Toast.makeText(getApplicationContext(), "开机启动！",Toast.LENGTH_SHORT).show();
                //Intent newIntent = new Intent(context, MainActivity.class);
                //newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  //注意，必须添加这个标记，否则启动会失败
                //context.startActivity(newIntent);
            }
        }
    };

    private int startrecording(){
        //启动行车记录
        Intent newIntent = getPackageManager().getLaunchIntentForPackage("com.baidu.BaiduMap");
        // 这里如果intent为空，就说名没有安装要跳转的应用嘛
        if (newIntent != null) {
            // 这里跟Activity传递参数一样的嘛，不要担心怎么传递参数，还有接收参数也是跟Activity和Activity传参数一样
            //intent.putExtra("name", "Liu xiang");
            //intent.putExtra("birthday", "1983-7-13");
            startActivity(newIntent);
            return 1;
        } else {
            // 没有安装要跳转的app应用，提醒一下
            //Toast.makeText(getApplicationContext(), "哟，赶紧下载安装这个APP吧", Toast.LENGTH_LONG).show();
            return 0;
        }
    }

    /**
     * 判断指定包名的进程是否运行
     * @param context
     * @param packageName 指定包名
     * @return 是否运行
     */
    public static boolean isRunning(Context context,String packageName){
        ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> infos = am.getRunningAppProcesses();
        for(RunningAppProcessInfo rapi : infos){
            if(rapi.processName.equals(packageName))
                return true;
        }
        return false;
    }


    private void shutdown() {
        try {
            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream out = new DataOutputStream(
                    process.getOutputStream());
            out.writeBytes("reboot -p\n"); //关机
            //out.writeBytes("reboot \n");//重启
            out.writeBytes("exit\n");
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //等待多长时间
    final Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    //recLen++;
                    //recTime.setText(GetRecTime(recLen));
                    break;
            }
            super.handleMessage(msg);
        }
    };
    TimerTask task = new TimerTask(){
        public void run() {
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
        }
    };

    /*
    //判定主程序是否在运行
    private boolean isRunning() {
    	String processName = "com.yhj.autopowerclose";

    	ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
    	KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

    	if (activityManager == null)
    	{
    		Toast.makeText(getApplicationContext(), "不在运行00",Toast.LENGTH_SHORT).show();
    		return false;
    	}
    	// get running application processes
    	List<ActivityManager.RunningAppProcessInfo> processList = activityManager.getRunningAppProcesses();
    	// 保存所有正在运行的包名 以及它所在的进程信息
        Map<String, ActivityManager.RunningAppProcessInfo> pgkProcessAppMap = new HashMap<String, ActivityManager.RunningAppProcessInfo>();

    	for (ActivityManager.RunningAppProcessInfo process : processList) {
    	if (process.processName.startsWith(processName)) {
    		Toast.makeText(getApplicationContext(), "正在在运行",Toast.LENGTH_SHORT).show();
    		String[] pkgNameList = process.pkgList; // 获得运行在该进程里的所有应用程序包

            // 输出所有应用程序的包名
            for (int i = 0; i < pkgNameList.length; i++) {
                String pkgName = pkgNameList[i];
                Log.i(TAG, "packageName " + pkgName + " at index " + i+ " in process " );
                Toast.makeText(getApplicationContext(),("正在在运行packageName " + pkgName),Toast.LENGTH_SHORT).show();
                // 加入至map对象里
                pgkProcessAppMap.put(pkgName, process);
            }
    	}
    	else
    	{

    		//Toast.makeText(getApplicationContext(), "不在运行01",Toast.LENGTH_SHORT).show();
    	}
    	}
    	//Toast.makeText(getApplicationContext(), "不在运行02",Toast.LENGTH_SHORT).show();
    	return false;
    	}
    	*/
    //http请求
    public String executeHttpGet(String furl) {
        String result = "9999";
        URL url = null;
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        HttpURLConnection connection = null;
        InputStreamReader in = null;
        try {
            url = new URL(furl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(8000);
            in = new InputStreamReader(connection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(in);
            StringBuffer strBuffer = new StringBuffer();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                strBuffer.append(line);
            }
            result = strBuffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return result;
    }
}
