import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class WeatherService extends Service {
  
  final String LOG_TAG = "myLogs";
NotificationManager nm;
  public void onCreate() {
    super.onCreate();
    Log.d(LOG_TAG, "onCreate");
	nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
  }
  
  public int onStartCommand(Intent intent, int flags, int startId) {
    Log.d(LOG_TAG, "onStartCommand");
    someTask();
    return super.onStartCommand(intent, flags, startId);
  }

  public void onDestroy() {
    super.onDestroy();
    Log.d(LOG_TAG, "onDestroy");
  }

  public IBinder onBind(Intent intent) {
    Log.d(LOG_TAG, "onBind");
    return null;
  }
  
  void someTask() {
	 new Thread(new Runnable() {
      public void run() {
        for (int i = 1; i<=5; i++) {
          Log.d(LOG_TAG, "i = " + i);
          try {
            TimeUnit.SECONDS.sleep(1);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
		try {
		  TimeUnit.SECONDS.sleep(5);
		} catch (InterruptedException e) {
		  e.printStackTrace();
		}
		sendNotif();
        // stopSelf();
      }
    }).start();
  }
  void sendNotif() {
    // 1-я часть
    Notification notif = new Notification(R.drawable.ic_launcher, "Text in status bar", 
      System.currentTimeMillis());
    
    // 3-я часть
    Intent intent = new Intent(this, MainActivity.class);
    // intent.putExtra(MainActivity.FILE_NAME, "somefile");
    PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
    
    // 2-я часть
    notif.setLatestEventInfo(this, "Notification's title", "Notification's text", pIntent);
    
    // ставим флаг, чтобы уведомление пропало после нажатия
    notif.flags |= Notification.FLAG_AUTO_CANCEL;
    
    // отправляем
    nm.notify(1, notif);
  }
}