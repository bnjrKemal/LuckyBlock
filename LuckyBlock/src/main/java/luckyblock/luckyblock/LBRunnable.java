package luckyblock.luckyblock;

import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LBRunnable extends BukkitRunnable {

    LuckyBlock main;

    public LBRunnable(LuckyBlock instance) {
        this.main = instance;
    }

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");

    @Override
    public void run() {
        Date date = new Date(System.currentTimeMillis());
        String now = simpleDateFormat.format(date);

        for (Blok blok : main.getBlocks()) {
            if(blok.getLocation() == null) return;
            blok.hologram();
            for (String time : blok.getTimes()) {
                if (now.equals(time)) {
                    if (!blok.isStarted()) {
                        blok.start();
                    }
                    break;
                }
            }
        }
    }
}
