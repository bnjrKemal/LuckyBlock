package luckyblock.luckyblock;

import me.filoghost.holographicdisplays.api.hologram.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Blok {

    private File file;
    private HashMap<UUID, Integer> breaks;
    private String fileName;
    private YamlConfiguration yaml;
    private String name;
    private int health;
    private int maxhealth;
    private List<String> times;
    private List<String> rewards;
    public Hologram hologram;
    private Location location;
    private double locationHeight;
    private boolean started;
    private int terminateTime;
    LuckyBlock main;

    public Blok(LuckyBlock instance) {this.main = instance;}

    public Blok getBlock(File file){
        String[] arrays = file.getName().split("/");
        this.file = file;
        fileName = arrays[arrays.length - 1].replace(".yml", "");
        yaml = YamlConfiguration.loadConfiguration(file);
        breaks = new HashMap<>();
        name = getYaml().getString("name");
        maxhealth = getYaml().getInt("health");
        health = maxhealth;
        times = getYaml().getStringList("times").stream().sorted().collect(Collectors.toList());
        rewards = getYaml().getStringList("rewards");
        locationHeight = getYaml().getDouble("location-height");
        location = getYaml().get("location") == null ? null : (Location)getYaml().get("location");
        if(getHologramLocation() != null) hologram = main.getHolographicDisplaysAPI().createHologram(getHologramLocation());
        started = false;
        terminateTime = getYaml().getInt("terminate-time");
        main.getBlocks().add(this);
        return this;
    }
    //=========================================================================================
    public void start(){
        for(String startMessage : getYaml().getStringList("start-message"))
            Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(startMessage));

        setStarted(true);
        hologram();

        new BukkitRunnable() {
            @Override
            public void run() {
                if(started) stop();
            }
        }.runTaskLater(main, 20*60*getTerminateTime());
    }
    public void stop() {
        setStarted(false);
        setHealth(maxhealth);
        if(getWinner() != null) {
            for (String rewards : rewards) {
                rewards = rewards.replace("{player}", getWinner().getName());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), rewards);
            }
            for (String finishMessage : getYaml().getStringList("finish-message"))
                Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(finishMessage.replace("{player}", getWinner().getName())));
        }
        breaks = new HashMap<>();
        hologram();
    }

    public void work(Player breaker){
        setHealth(--health);
        addPoint(breaker.getUniqueId());
        hologram();
        if(health <= 0) //0 veya aşağda olduğunda
            stop();
    }
    public void hologram(){

        if(getLocation() == null) return;

        if(hologram != null) hologram.delete();
        hologram = main.getHolographicDisplaysAPI().createHologram(getHologramLocation());

        if(started) {
            for (String line : getYaml().getStringList("holo-in-working")) {
                line = line
                        .replace("{name}", name)
                        .replace("{health}", maxhealth + "")
                        .replace("{progress}", health + "")
                        .replace("{player}", getWinner() == null ? "~" : getWinner().getName());
                hologram.getLines().appendText(line);
            }
        } else if (!started){
            for (String line : getYaml().getStringList("holo-waiter")) {
                line = line
                        .replace("{time}", buildTime())
                        .replace("{times}", times.toString());
                hologram.getLines().appendText(line);
            }
        }
    }
    public void addPoint(UUID uuid){
        int value = breaks.getOrDefault(uuid, 0);
        breaks.put(uuid, value + 1);
    }
    //=========================================================================================

    private void setStarted(boolean started) {this.started = started;}
    private void setHealth(int health) {this.health = health;}
    public void setLocation(Location location) {
        getYaml().set("location", location);
        this.location = location;
    }

    private String diffTimeToString(Date firstDate, Date lastDate){
        long diff = lastDate.getTime() - firstDate.getTime();
        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffDays = diff / (24 * 60 * 60 * 1000);
        StringBuilder str = new StringBuilder();
        if(diffDays > 0) str.append(diffDays + " " + "gun" + " ");
        if(diffHours > 0) str.append(diffHours + " " + "saat" + " ");
        if(diffMinutes > 0) str.append(diffMinutes + " " + "dakika" + " ");
        if(diffSeconds > 0) str.append(diffSeconds + " " + "saniye");
        return str.toString();
    }
    private String buildTime() {

        SimpleDateFormat nowFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String now = nowFormat.format(date);

        SimpleDateFormat dayMountYearFormat = new SimpleDateFormat("dd:MM:yyyy");
        String dayMountYear = dayMountYearFormat.format(date);

        int nowHour = Integer.parseInt(now.split(":")[0]);
        int nowMinute = Integer.parseInt(now.split(":")[1]);
        int nowSecond = Integer.parseInt(now.split(":")[2]);

        for (String nextTime : times) {
            int nextHour = Integer.parseInt(nextTime.split(":")[0]);
            int nextMinute = Integer.parseInt(nextTime.split(":")[1]);
            int nextSecond = Integer.parseInt("00");

            if (nowHour < nextHour) return dayMountYear + " " + nextTime;
            else if (nowHour == nextHour && nowMinute < nextMinute) return dayMountYear + " " + nextTime;
            else if (nowHour == nextHour && nowMinute == nextMinute && nowSecond < nextSecond) return dayMountYear + " " + nextTime;
        }

        SimpleDateFormat nextFormat = new SimpleDateFormat("dd:MM:yyyy HH:mm");
        Date nextDate = null;
        try {nextDate = nextFormat.parse(dayMountYear + " " + times.get(0));} catch (ParseException e) {throw new RuntimeException(e);}

        if(new Date().getTime() > nextDate.getTime()) nextDate = new Date(nextDate.getTime() + 86400*1000);

        return nextFormat.format(nextDate);
    }
    public String remainingTime(){
        SimpleDateFormat nextFormat = new SimpleDateFormat("dd:MM:yyyy HH:mm");
        try {
            return diffTimeToString(new Date(), nextFormat.parse(buildTime()));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isStarted() {return started;}
    public YamlConfiguration getYaml() {return yaml;}
    public Location getLocation() {return location;}
    public String getFileName() {return fileName;}
    public File getFile() {return file;}
    public List<String> getTimes() {return times;}
    public int getTerminateTime() {return terminateTime;}

    public Location getHologramLocation() {
        if(getLocation() == null) return null;
        Location holoLoc = getLocation().clone().add(0.0D, this.locationHeight, 0.0D);
        return holoLoc.add(0.5D, 0.0D, 0.5D);
    }

    public Player getWinner() {
        List<UUID> winners = SortUtil.mapTop(breaks, false);
        if(winners == null) return null;
        return Bukkit.getPlayer(winners.get(0));
    }
}
