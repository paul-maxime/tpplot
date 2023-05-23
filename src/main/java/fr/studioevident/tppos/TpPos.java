package fr.studioevident.tppos;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import fr.studioevident.tppos.commands.TpPosCommand;

public final class TpPos extends JavaPlugin {
    private boolean canTpInAnything = getConfig().getBoolean("player-can-tp-in-anything");

    @Override
    public void onEnable() {
        PluginCommand tpPosCommand = getCommand("tppos");
        if (tpPosCommand != null) tpPosCommand.setExecutor(new TpPosCommand(this));
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
}
