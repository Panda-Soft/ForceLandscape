package p4nd4.forcelandscape;
        import android.app.Notification;
        import android.app.NotificationChannel;
        import android.app.NotificationManager;
        import android.app.Service;
        import android.content.Context;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.content.pm.ActivityInfo;
        import android.content.res.Resources;
        import android.graphics.Color;
        import android.graphics.PixelFormat;
        import android.os.Build;
        import android.os.IBinder;
        import android.preference.PreferenceManager;
        import android.provider.Settings;
        import android.view.View;
        import android.view.WindowManager;


public class AlwaysOnTopService extends Service {

    private View topLeftView;
    WindowManager.LayoutParams topLeftParams;
    boolean notificationState;

    private WindowManager wm;
    Integer accelerometrState;
    Notification notif;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        SharedPreferences AppData = PreferenceManager.getDefaultSharedPreferences(this);
        notificationState = AppData.getBoolean("notificationState", true);
            accelerometrState = 0;
            try {
                accelerometrState = Settings.System.getInt(this.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION);
                Settings.System.putInt(this.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 1);
            } catch (Settings.SettingNotFoundException e) {
            }

            int width = getScreenWidth();
            int height = getScreenHeight();
            topLeftView = new View(this);
            topLeftView.setBackgroundColor(0x00000000);
            topLeftParams = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    getWindowType(), //Temporary Nougat+Oreo
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, PixelFormat.TRANSLUCENT);
             topLeftParams.x = 0;
            topLeftParams.y = 0;
            topLeftParams.width = 0;
            topLeftParams.height = 0;
            topLeftParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE;
            wm.addView(topLeftView, topLeftParams);

            if (notificationState) {


                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    NotificationManager mNotificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    String id = "my_channel_01";
                    CharSequence name = "default";
                    String description = "default";
                    int importance = NotificationManager.IMPORTANCE_NONE;
                    NotificationChannel mChannel = new NotificationChannel(id, name, importance);
                    mChannel.setDescription(description);
                    mChannel.enableLights(false);
                    mChannel.setLightColor(Color.BLUE);
                    mChannel.enableVibration(false);
                    mChannel.setVibrationPattern(new long[]{100});
                    mNotificationManager.createNotificationChannel(mChannel);
                    Notification.Builder builder = new Notification.Builder(this, "my_channel_01")
                            .setContentTitle(getString(R.string.app_name))
                            .setContentText("Background service running...")
                            .setAutoCancel(true);

                    notif = builder.build();

                } else {
                    notif = new Notification.Builder(this)
                            .setSmallIcon(R.drawable.ic_landscape)
                            .setOngoing(true)
                            .build();
                }
                startForeground(1, notif);
            }

    }


    public static int getWindowType() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {return WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;}
        else {return  WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;}

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
            Settings.System.putInt( this.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, accelerometrState);
        if(topLeftView.getWindowToken() != null){ wm.removeView(topLeftView); }
        System.exit(0);
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }


}