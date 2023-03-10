package com.johansvartdal.landlord.commands;

import com.johansvartdal.landlord.*;
import com.johansvartdal.landlord.LevelManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Home implements CommandExecutor {

    private Main plugin;

    public Home(Main plugin) {
        this.plugin = plugin;
        plugin.getCommand("home").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (Tools.stateNotNormal(sender)) {
            Tools.tellPlayer(sender, LangDict.getString(LangDict.CMD_NOT_NOW), ChatColor.RED);
            return true;
        }

        Player player = (Player) sender;

        if (PlayerEventManager.playerIsInEvent(player)) {
            if (!PlayerEventManager.getEventForPlayer(player).playerTPAwayAllowed()) {
                Tools.tellPlayer(player, "You cannot teleport at the moment", ChatColor.RED);
                return true;
            }
            PlayerEventManager.forceEndPlayerEvent(player);
        }

        PlayerData pd = Main.playerDataManager.getPlayerData(player);
        Location location = pd.getHomeLocation();
        location = Tools.highestStandingPoint(location);
        player.teleport(location);
        Tools.tellPlayer(player, "You have magically been teleported home!");
        return true;
    }
}
