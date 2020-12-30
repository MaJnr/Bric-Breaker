package sample;

import java.util.TimerTask;

public class CustomTimerTask extends TimerTask {

    boolean aBoolean;
    int weaponBonusDuration;
    boolean isTimerEnded = false;

    public CustomTimerTask(boolean aBoolean, int shootInterval) {
        this.aBoolean = aBoolean;
        this.weaponBonusDuration = shootInterval;
    }

    @Override
    public void run() {
        aBoolean = true;
//        System.out.println("Can shoot");
        completeTask();
    }

    private void completeTask() {
        try {
            Thread.sleep(weaponBonusDuration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (weaponBonusDuration == 1) {
            isTimerEnded = true;
            this.cancel();
        }
        weaponBonusDuration--;
    }
}
