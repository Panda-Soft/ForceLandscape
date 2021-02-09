package p4nd4.forcelandscape;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.graphics.drawable.Icon;
        import android.os.Build;
        import android.os.Handler;
        import android.preference.PreferenceManager;
        import android.provider.Settings;
        import android.service.quicksettings.Tile;
        import android.service.quicksettings.TileService;

        import java.lang.reflect.InvocationTargetException;
        import java.lang.reflect.Method;


public class ForceLandscapeTile extends TileService  {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
    }



    Handler collapseNotificationHandler;
    @Override
    public void onClick() {

        super.onClick();

        Intent svc;
        SharedPreferences AppData;

        AppData = PreferenceManager.getDefaultSharedPreferences(this);
        if(AppData.getBoolean("autoHideTile", true))  collapseNow();

        if ((!Settings.canDrawOverlays(this)) || (!Settings.System.canWrite(this))) {
            Intent startSetup = new Intent(this, Setup.class);
            startSetup.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startSetup);
        } else {


                Tile tile = getQsTile();
                if (tile.getLabel().equals("Stop")) {
                    tile.setIcon(Icon.createWithResource(this, R.drawable.ic_portrait));
                    tile.setLabel("Force Landscape");
                    svc = new Intent(this, AlwaysOnTopService.class);
                    stopService(svc);


                } else {
                    tile.setIcon(Icon.createWithResource(this, R.drawable.ic_landscape));
                    svc = new Intent(this, AlwaysOnTopService.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        startForegroundService(svc);
                    } else {
                        startService(svc);
                    }
                    tile.setLabel("Stop");

                }
                tile.updateTile();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void collapseNow() {

        if (collapseNotificationHandler == null) {
            collapseNotificationHandler = new Handler();
        }

        Runnable collapse = new Runnable() {

            @Override
            public void run() {

                Object statusBarService = getSystemService("statusbar");
                Class<?> statusBarManager = null;

                try {
                    statusBarManager = Class.forName("android.app.StatusBarManager");
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                Method collapseStatusBar = null;

                try {
                    if (Build.VERSION.SDK_INT > 16) {
                        collapseStatusBar = statusBarManager .getMethod("collapsePanels");
                    } else {
                        collapseStatusBar = statusBarManager .getMethod("collapse");
                    }
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }

                collapseStatusBar.setAccessible(true);

                try {
                    collapseStatusBar.invoke(statusBarService);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        };

        collapseNotificationHandler.postDelayed(collapse, 200L);

    }
}
