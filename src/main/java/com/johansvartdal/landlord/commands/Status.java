package com.johansvartdal.landlord.commands;

import com.johansvartdal.landlord.Bank;
import com.johansvartdal.landlord.LangDict;
import com.johansvartdal.landlord.Main;
import com.johansvartdal.landlord.Tools;
import com.johansvartdal.landlord.chatentities.ErrorChat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Status implements CommandExecutor {
    private Main plugin;

    public Status(Main plugin) {
        this.plugin = plugin;
        plugin.getCommand("status").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be executed by players");
            return true;
        }

        Player player = (Player) sender;
        if (!Main.playerDataManager.playerExists(player)) {
            Tools.tellPlayer(player, LangDict.getString(LangDict.CMD_NOT_NOW), ChatColor.RED);
            return true;
        }

        // make sure we are not in prep
        if (!Main.properties.gameHasStarted()) {
            Tools.tellPlayer(new ErrorChat(), player, LangDict.getString(LangDict.CMD_NOT_NOW));
            return true;
        }

        // print status
        Tools.printMenuHeader(player, LangDict.getString("playerStatus.status"));
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (!Main.playerDataManager.playerExists(player)) {
                continue;
            }
            Tools.printMenuOption(player, onlinePlayer.getDisplayName() + ":", Main.playerDataManager.getPlayerData(onlinePlayer).getStatus());
        }
        return true;
    }
}
