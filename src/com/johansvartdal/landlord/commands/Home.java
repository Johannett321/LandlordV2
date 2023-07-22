package com.johansvartdal.landlord.commands;

import com.johansvartdal.landlord.*;
import com.johansvartdal.landlord.LevelManager;
import com.johansvartdal.landlord.chatentities.ErrorChat;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
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

        // end event if player is in one
        if (PlayerEventManager.playerIsInEvent(player)) {
            if (!PlayerEventManager.getEventForPlayer(player).playerTPAwayAllowed()) {
                Tools.tellPlayer(player, LangDict.getString(LangDict.CMD_NOT_NOW), ChatColor.RED);
                return true;
            }
            PlayerEventManager.forceEndPlayerEvent(player);
        }

        // get home location
        PlayerData pd = Main.playerDataManager.getPlayerData(player);
        Location location = pd.getHomeLocation();

        // get home head location
        Location headLocation = pd.getHomeLocation();
        headLocation.setY(headLocation.getY()+1);

        // make sure player can stand there, and inform if not
        if (location.getBlock().getType() != Material.AIR || headLocation.getBlock().getType() != Material.AIR) {
            location = Tools.highestStandingPoint(location);
            Tools.tellPlayer(new ErrorChat(), player, LangDict.getString("obstructedHome"), ChatColor.RED);
        }
        player.teleport(location);
        Tools.tellPlayer(player, LangDict.getString("teleportedHome"));

        // set game mode
        player.setGameMode(GameMode.SURVIVAL);
        return true;
    }
}
