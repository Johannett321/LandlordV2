package com.johansvartdal.landlord.commands;

import com.johansvartdal.landlord.LangDict;
import com.johansvartdal.landlord.Main;
import com.johansvartdal.landlord.playerevents.PlayerEventManager;
import com.johansvartdal.landlord.Tools;
import com.johansvartdal.landlord.chatentities.ErrorChat;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetTrade implements CommandExecutor {

    private Main plugin;

    public SetTrade(Main plugin) {
        this.plugin = plugin;
        plugin.getCommand("settrade").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (Tools.stateNotNormal(sender)) {
            Tools.tellPlayer(sender, LangDict.getString(LangDict.CMD_NOT_NOW), ChatColor.RED);
            return true;
        }

        Player player = (Player) sender;

        // make sure player is OP
        if (!player.isOp()) {
            Tools.tellPlayer(new ErrorChat(), player, LangDict.getString(LangDict.YOU_ARE_NOT_ALLOWED), ChatColor.RED);
            return true;
        }

        // make sure in trade
        if (!Main.tradeCenter.getLocation().getChunk().equals(player.getLocation().getChunk())) {
            Tools.tellPlayer(new ErrorChat(), player, LangDict.getString("trade.tradeCommandOnlyInTrade"), ChatColor.RED);
            return true;
        }

        // check if player is flying
        if (PlayerEventManager.playerIsInFlyingEvent(player)) {
            Tools.tellPlayer(new ErrorChat(), player, LangDict.getString(LangDict.END_FLIGHT_FIRST));
            return true;
        }

        Main.tradeCenter.setLocation(player.getLocation());
        Tools.tellPlayer(player, LangDict.getString("trade.tradeUpdated"), ChatColor.GREEN);
        return true;
    }
}
