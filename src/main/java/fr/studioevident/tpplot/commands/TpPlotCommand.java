package fr.studioevident.tpplot.commands;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import fr.studioevident.tpplot.TpPlot;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
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

        plugin.getPlayersWithPlot(world);

        if (player.getGameMode() != GameMode.CREATIVE) {
            plugin.sendMessage(player, "§cVous ne pouvez pas exécuter cette commande !");
            return true;
        }

        if (args.length != 2) {
            plugin.sendMessage(player, "§cNombre d'arguments invalide !");
            return true;
        }

        Map<String, ProtectedRegion> regions = plugin.getRegionsOfWorld(world);
        String plotName = args[1];

        ProtectedRegion plot = regions.get(plotName);
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
            for (Player p : plugin.getPlayersWithPlot(player.getWorld())) {
                names.add(p.getName());
            }

            return names;
        }

        if (args.length == 2) {
            Player playerTabbed = Bukkit.getPlayer(args[0]);
            if (playerTabbed == null) return new ArrayList<>();

            return plugin.getPlotsNamesOfPlayer(player.getWorld(), playerTabbed);
        }

        return new ArrayList<>();
    }
}
