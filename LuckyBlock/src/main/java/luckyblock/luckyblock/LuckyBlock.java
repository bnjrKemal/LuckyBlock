package luckyblock.luckyblock;

import luckyblock.luckyblock.license.License;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Date;
import java.util.HashSet;

public final class LuckyBlock extends JavaPlugin {

    private HashSet<Blok> blocks;
    private HolographicDisplaysAPI holographicDisplaysAPI;

    @Override
    public void onEnable() {
        license();
        saveDefaultConfig();
        if(!hologramAPI()){
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        pApi();
        loading();

        long start = 60 - (new Date().getTime() / 1000) % 60;
        new LBRunnable(this).runTaskTimer(this, start * 20, 20*60);
        new LBRunnable(this).run();

        Bukkit.getPluginManager().registerEvents(new LBListener(this), this);
        getCommand("luckyblock").setExecutor(new LBCommand(this));
    }

    private void license() {
        try {
            new License("https://firedia.com/licence.php", "54");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable(){
        for(Blok blok : getBlocks()){
            if(blok.isStarted()){
                blok.stop();
            }
            blok.hologram.delete();
        }
    }

    public void loading() {
        File folder = new File(getDataFolder(), "/blocks/");
        if(!folder.exists()) {
            folder.mkdirs();
            saveResource("blocks/sansliblock.yml", true);
        }
        blocks = new HashSet<>();
        YamlConfiguration.loadConfiguration(folder);
        for(File file : new File(getDataFolder(), "/blocks/").listFiles())
            new Blok(this).getBlock(file);
    }

    public void reloading(){
        for(Blok blok : getBlocks()){
            blok.stop();
            blok.hologram.delete();
        }
        loading();
        reloadConfig();
        new LBRunnable(this).run();
    }

    private boolean pApi() {
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) return false;
        new PAPI(this).register();
        return true;
    }
    private boolean hologramAPI() {
        if(!Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")){
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "You must to have HolographicDisplays. This plugin is disabled!");
            return false;
        }
        holographicDisplaysAPI = HolographicDisplaysAPI.get(this);
        return true;
    }

    public HolographicDisplaysAPI getHolographicDisplaysAPI(){return holographicDisplaysAPI;}
    public HashSet<Blok> getBlocks() {return blocks;}

}
