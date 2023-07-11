package luckyblock.luckyblock;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;

public class PAPI extends PlaceholderExpansion {

    LuckyBlock main;

    public PAPI(LuckyBlock instance) {
        this.main = instance;
    }

    @Override
    public String getIdentifier() {
        return "luckyblock";
    }

    @Override
    public String getAuthor() {
        return "donsuzturk";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {

        for(Blok blok : main.getBlocks()){
            if(params.equals(blok.getFileName())){
                return blok.remainingTime();
            }
        }
        return null;
    }

}
