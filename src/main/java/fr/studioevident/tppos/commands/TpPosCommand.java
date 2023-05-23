package fr.studioevident.tppos.commands;

import fr.studioevident.tppos.TpPos;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class TpPosCommand implements CommandExecutor, TabCompleter {
    private final TpPos plugin;

    public TpPosCommand(TpPos plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, final Command command, final String alias, final String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player)sender;

        if (player.getGameMode() != GameMode.CREATIVE) {
            plugin.sendMessage(player, "§cVous ne pouvez pas exécuter cette commande !");
            return true;
        }

        if (args.length != 3) {
            plugin.sendMessage(player, "§cArguments trop " + (args.length < 3 ? "peu " : "") + "nombreux !");
            return true;
        }

        for (String arg : args) {
            if (!plugin.isNumericInt(arg)) return true;
        }

        World world = player.getWorld();
        Location tpLoc = new Location(world, Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
        Block footBlock = world.getBlockAt(tpLoc);
        Block headBlock = world.getBlockAt(tpLoc.add(0, 1, 0));

        if (plugin.canTpInto(footBlock) && plugin.canTpInto(headBlock)) {
            player.teleport(tpLoc.setDirection(player.getLocation().getDirection()));
            plugin.sendMessage(player, "§aVous avez été téléporté en " + args[0] + " " + args[1] + " " + args[2] + " !");
        } else plugin.sendMessage(player, "§cTéléportation impossible, le lieu de téléportation est obstrué !");

        return true;
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args) {
        return Arrays.asList("0", "500", "1000", "1500", "2000", "2500");
    }
}
