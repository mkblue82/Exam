package foodloss;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class DiscountNotificationScheduler {

    private static Timer timer;

    public static void startScheduler() {
        if (timer != null) {
            timer.cancel();
        }

        timer = new Timer(true);

        // 毎日6:00に実行するタスク
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                checkAndSendDiscountNotifications();
            }
        };

        // 初回実行時刻を計算（明日の6:00）
        Calendar firstTime = Calendar.getInstance();
        firstTime.set(Calendar.HOUR_OF_DAY, 6);
        firstTime.set(Calendar.MINUTE, 0);
        firstTime.set(Calendar.SECOND, 0);

        if (firstTime.before(Calendar.getInstance())) {
            firstTime.add(Calendar.DATE, 1); // 今日の6時を過ぎていたら明日
        }

        // 24時間ごとに実行
        timer.scheduleAtFixedRate(task, firstTime.getTime(), 24 * 60 * 60 * 1000);

        System.out.println("✅ 値引き通知スケジューラー起動: 毎日6:00に実行");
    }

    public static void stopScheduler() {
        if (timer != null) {
            timer.cancel();
            timer = null;
            System.out.println("⏹ 値引き通知スケジューラー停止");
        }
    }

    private static void checkAndSendDiscountNotifications() {
        try {
            DiscountNotificationService.checkAndNotify();
        } catch (Exception e) {
            System.err.println("❌ スケジュール実行エラー");
            e.printStackTrace();
        }
    }
}