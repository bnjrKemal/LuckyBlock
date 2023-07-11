package luckyblock.luckyblock;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class LBListener implements Listener {

    LuckyBlock main;

    public LBListener(LuckyBlock instance) {
        this.main = instance;
    }

    @EventHandler
    public void onClick(BlockBreakEvent e){

        for(Blok blok : main.getBlocks()){
            if(blok.getLocation() == null) return;
            if(!blok.getLocation().getBlock().getLocation().equals(e.getBlock().getLocation())) continue;
            if(blok.getLocation().getBlock().getLocation().equals(e.getBlock().getLocation())) e.setCancelled(true);
            if(blok.isStarted()) blok.work(e.getPlayer());
        }
    }
}
