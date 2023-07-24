package com.johansvartdal.landlord.commands;

import com.johansvartdal.landlord.LangDict;
import com.johansvartdal.landlord.Main;
import com.johansvartdal.landlord.Tools;
import com.johansvartdal.landlord.chatentities.ErrorChat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SendHome implements CommandExecutor {

    private Main plugin;

    public SendHome(Main plugin) {
        this.plugin = plugin;
        plugin.getCommand("sendhome").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        Player player = (Player) commandSender;
        if (args.length != 1) {
            return false;
        }

        if (args[0].equalsIgnoreCase(player.getDisplayName())) {
            Tools.tellPlayer(new ErrorChat(), player, LangDict.getString(LangDict.CANNOT_USE_ON_YOURSELF), ChatColor.RED);
            return true;
        }

        sendHomePlayer(player, args[0]);
        return true;
    }

    private void sendHomePlayer(Player owningPlayer, String playerName) {
        for (Player player:Bukkit.getOnlinePlayers()) {
            if (!player.getDisplayName().equalsIgnoreCase(playerName)) {
                continue;
            }

            if(!Main.playerDataManager.getPlayerData(owningPlayer).ownsChunk(player.getLocation().getChunk())) {
                Tools.tellPlayer(new ErrorChat(), owningPlayer, LangDict.getString("visit.playerNotInChunk"), ChatColor.RED);
                return;
            }

            Location homeLoc = Main.playerDataManager.getPlayerData(player).getHomeLocation();
            player.teleport(homeLoc);
            player.setGameMode(GameMode.SURVIVAL);

            Tools.tellPlayer(owningPlayer, LangDict.getString("visit.playerSentHome"), ChatColor.GREEN);
            Tools.tellPlayer(player, LangDict.getString("visit.sentHomeByOwner"), ChatColor.RED);
            return;
        }

        Tools.tellPlayer(new ErrorChat(), owningPlayer, LangDict.getString("visit.couldNotFindPlayer") + playerName, ChatColor.RED);
    }
}
