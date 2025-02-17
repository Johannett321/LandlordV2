package com.johansvartdal.landlord.commands;

import com.johansvartdal.landlord.*;
import com.johansvartdal.landlord.chatentities.ErrorChat;
import com.johansvartdal.landlord.playerevents.PlayerEventManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Trade implements CommandExecutor {

    private Main plugin;

    public Trade(Main plugin) {
        this.plugin = plugin;
        plugin.getCommand("trade").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (Tools.stateNotNormal(sender)) {
            Tools.tellPlayer(sender, LangDict.getString(LangDict.CMD_NOT_NOW), ChatColor.RED);
            return true;
        }

        Player player = (Player) sender;

        // check if player is allowed to teleport
        if (PlayerEventManager.playerIsInEvent(player)) {
            if (!PlayerEventManager.getEventForPlayer(player).playerTPAwayAllowed()) {
                Tools.tellPlayer(new ErrorChat(), player, LangDict.getString(LangDict.CMD_NOT_NOW), ChatColor.RED);
                return true;
            }
            PlayerEventManager.forceEndPlayerEvent(player);
        }

        Location location = Main.tradeCenter.getLocation();
        location = Tools.highestStandingPoint(location);
        player.teleport(location);

        // update status & tell player
        PlayerDataManager.updatePlayerStatus(player, LangDict.getString("playerStatus.inTrade"));
        Tools.tellPlayer(player, LangDict.getString("trade.teleportTrade"));
        return true;
    }
}
