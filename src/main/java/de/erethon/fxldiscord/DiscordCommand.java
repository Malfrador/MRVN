package de.erethon.fxldiscord;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DiscordCommand implements CommandExecutor {

    FXLDC plugin = FXLDC.getPlugin();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;
        if (args.length == 0) {
            player.sendMessage(ChatColor.GRAY + "Discord: " + ChatColor.BLUE + plugin.getConfig().getString("discordLink"));
            plugin.getLink().updateFactions(player);
            return true;
        }
        if (!args[0].contains("#")) {
            player.sendMessage(ChatColor.RED + "Kein gültiger Discord-Name.");
            return true;
        }
        if (plugin.getLP().getPlayerAdapter(Player.class).getUser(player).getCachedData().getMetaData().getMeta().containsKey("discord-id")) {
            player.sendMessage(ChatColor.RED + "Dein Account wurde bereits gelinkt.");
            return true;
        }
        plugin.getLink().startLinking(player, args[0]);
        return true;
    }


}
