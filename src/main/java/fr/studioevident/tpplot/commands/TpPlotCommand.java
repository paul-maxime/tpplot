package fr.studioevident.tpplot.commands;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.studioevident.tpplot.TpPlot;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;

public class TpPlotCommand implements CommandExecutor, TabCompleter {
    private final TpPlot plugin;

    public TpPlotCommand(TpPlot plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, final Command command, final String alias, final String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player)sender;
        World world = player.getWorld();

        if (player.getGameMode() != GameMode.CREATIVE) {
            plugin.sendMessage(player, "§cVous ne pouvez pas exécuter cette commande !");
            return true;
        }

        if (args.length != 1 && args.length != 2) {
            plugin.sendMessage(player, "§cNombre d'arguments invalide !");
            return true;
        }

        Map<String, ProtectedRegion> allRegions = plugin.getRegionsOfWorld(world);
        List<String> regions = plugin.getPlotsNamesOfPlayer(world, args[0]);

        ProtectedRegion plot;
        if (args.length == 2)
        {
            String plotName = args[1];
            plot = allRegions.get(plotName);
        }
        else if (regions.size() == 1)
        {
            plot = allRegions.get(regions.get(0));
        }
        else if (regions.size() == 0)
        {
            plugin.sendMessage(player, "§cCe joueur n'a aucun plot !");
            return true;
        }
        else
        {
            plugin.sendMessage(player, "§cCe joueur a plusieurs plots: " + ChatColor.GOLD + String.join(", ", regions));
            return true;
        }

        if (plot == null) {
            plugin.sendMessage(player, "§cPlot introuvable !");
            return true;
        }

        player.teleport(plugin.getTpPoint(world, plot));
        return true;
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args) {
        Player player = (Player)sender;

        if (args.length == 1) {
            List<String> names = new ArrayList<>();
            for (String name : plugin.getPlayersWithPlot(player.getWorld())) {
                if (name.toLowerCase().startsWith(args[0].toLowerCase())) {
                    names.add(name);
                }
            }
            return names;
        }

        if (args.length == 2) {
            List<String> plots = new ArrayList<>();
            for (String plot : plugin.getPlotsNamesOfPlayer(player.getWorld(), args[0])) {
                if (plot.toLowerCase().startsWith(args[1].toLowerCase())) {
                    plots.add(plot);
                }
            }
            return plots;
        }

        return new ArrayList<>();
    }
}
