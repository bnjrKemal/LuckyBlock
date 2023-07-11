package luckyblock.luckyblock;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

public class LBCommand implements CommandExecutor {

    LuckyBlock main;
    String prefix;

    public LBCommand(LuckyBlock instance) {
        this.main = instance;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        prefix = main.getConfig().getString("prefix");

        if(!(sender instanceof Player)){
            main.reloading();
            sender.sendMessage(main.getConfig().getString("reload").replace("{prefix}", prefix));
            return false;
        }

        if(args.length == 1 && args[0].equalsIgnoreCase("reload")){
            main.reloading();
            sender.sendMessage(main.getConfig().getString("reload").replace("{prefix}", prefix));
            return false;
        }

        Player player = (Player) sender;

        if(args.length < 1 || (args.length >= 1 && !args[0].equalsIgnoreCase("set"))){
            main.getConfig().getStringList("help-command").forEach(message -> player.sendMessage(message.replace("{prefix}", prefix)));
            return false;
        }

        if(args.length < 2){
            player.sendMessage(main.getConfig().getString("usage-command").replace("{prefix}", prefix));
            StringBuilder stringBuilder = new StringBuilder();
            for(Blok blok : main.getBlocks())
                stringBuilder.append(blok.getFileName() + "");
            player.sendMessage(main.getConfig().getString("class-listing").replace("{list}", stringBuilder.toString()).replace("{prefix}", prefix));
            return false;
        }

        Blok thisBlok = null;

        for(Blok blok : main.getBlocks()){
            if(args[1].equals(blok.getFileName()))
                thisBlok = blok;
        }

        if(thisBlok == null){
            player.sendMessage(main.getConfig().getString("no-found-class"));
            StringBuilder stringBuilder = new StringBuilder();
            for(Blok blok : main.getBlocks())
                stringBuilder.append(blok.getFileName() + "");
            player.sendMessage(main.getConfig().getString("class-listing").replace("{list}", stringBuilder.toString()).replace("{prefix}", prefix));
            return false;
        }

        Location loc = player.getTargetBlock(null,5).getLocation();

        if(loc == null){
            player.sendMessage(main.getConfig().getString("no-found-location").replace("{prefix}", prefix));
            return false;
        }

        thisBlok.setLocation(loc);

        try {
            thisBlok.getYaml().save(thisBlok.getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

        player.sendMessage(main.getConfig().getString("update").replace("{file}", thisBlok.getFileName()).replace("{prefix}", prefix));

        return false;
    }
}
