package fr.studioevident.tpplot;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import fr.studioevident.tpplot.commands.TpPlotCommand;

import java.util.*;

public final class TpPlot extends JavaPlugin {
    private WorldGuard worldGuard;
    private boolean canTpInAnything = getConfig().getBoolean("player-can-tp-in-anything");

    private final HashMap<String, UUID> namesToIdsCache = new HashMap<>();
    private final HashMap<UUID, String> idsToNamesCache = new HashMap<>();

    @Override
    public void onEnable() {
        worldGuard = WorldGuard.getInstance();

        PluginCommand tpPlotCommand = getCommand("tpplot");
        if (tpPlotCommand != null) tpPlotCommand.setExecutor(new TpPlotCommand(this));
    }

    public void sendMessage(Player player, String message) {
        if (message == null) return;
        player.spigot().sendMessage(TextComponent.fromLegacyText(message));
    }

    public boolean isNumericInt(String str) {
        if (str == null) return false;

        try {
            int i = Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }

    public boolean canTpInto(Block block) {
        if (canTpInAnything) return true;

        Material mat = block.getType();
        return mat == Material.AIR || mat == Material.WATER;
    }

    public Map<String, ProtectedRegion> getRegionsOfWorld(World worldMc) {
        com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(worldMc);

        Map<String, ProtectedRegion> regions = new HashMap<>(
                Objects.requireNonNull(
                        worldGuard.getPlatform().getRegionContainer().get(world)
                ).getRegions()
        );
        regions.remove("__global__");
        return regions;
    }

    public List<String> getPlayersWithPlot(World world) {
        List<String> playerNames = new ArrayList<>();
        Map<String, ProtectedRegion> regions = getRegionsOfWorld(world);

        List<UUID> uuids = new ArrayList<>();
        for (String regionName : regions.keySet()) {
            ProtectedRegion region = regions.get(regionName);
            for (UUID uuid : region.getOwners().getUniqueIds()) {
                if (!uuids.contains(uuid)) uuids.add(uuid);
            }
        }

        for (UUID uuid : uuids) {
            Player onlinePlayer = Bukkit.getPlayer(uuid);
            if (onlinePlayer != null) {
                idsToNamesCache.put(onlinePlayer.getUniqueId(), onlinePlayer.getName());
                namesToIdsCache.put(onlinePlayer.getName(), onlinePlayer.getUniqueId());
                playerNames.add(onlinePlayer.getName());
            } else if (idsToNamesCache.containsKey(uuid)) {
                playerNames.add(idsToNamesCache.get(uuid));
            } else {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                idsToNamesCache.put(offlinePlayer.getUniqueId(), offlinePlayer.getName());
                namesToIdsCache.put(offlinePlayer.getName(), offlinePlayer.getUniqueId());
                playerNames.add(offlinePlayer.getName());
            }
        }

        return playerNames;
    }

    public List<String> getPlotsNamesOfPlayer(World world, String playerName) {
        if (playerName == null || playerName.isEmpty()) {
            return List.of();
        }
        if (!namesToIdsCache.containsKey(playerName)) {
            return List.of();
        }
        UUID playerId = namesToIdsCache.get(playerName);

        Map<String, ProtectedRegion> regions = getRegionsOfWorld(world);

        List<String> regionsNames = new ArrayList<>();
        for (String regionName : regions.keySet()) {
            ProtectedRegion region = regions.get(regionName);
            if (region.getOwners().contains(playerId)) regionsNames.add(regionName);
        }

        return regionsNames;
    }

    public Location getTpPoint(World world, ProtectedRegion plot) {
        BlockVector2 pos = plot.getPoints().get(0);
        int x = pos.getX();
        int z = pos.getZ();

        Location loc = new Location(world, x, world.getMaxHeight(), z);
        for (int i=world.getMaxHeight()-1 ; i>0 ; i--) {
            loc.setY(i);
            if (!canTpInto(loc.getBlock())) {
                loc.setY(i+1);
                return loc;
            }
        }

        return loc;
    }
}
