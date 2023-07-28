package com.johansvartdal.landlord.commands;

import com.johansvartdal.landlord.*;
import com.johansvartdal.landlord.chatentities.ErrorChat;
import org.bukkit.*;
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
                Tools.tellPlayer(new ErrorChat(), player, LangDict.getString(LangDict.CMD_NOT_NOW), ChatColor.RED);
                return true;
            }
            PlayerEventManager.forceEndPlayerEvent(player);
        }

        // get home location
        PlayerData pd = Main.playerDataManager.getPlayerData(player);
        Location location = pd.getHomeLocation();
        Location teleportTo = location;

        // get home head location
        Location headLocation = new Location(Bukkit.getWorld("world"), location.getX(), location.getY() + 1, location.getZ());

        // make sure player can stand there, and inform if not
        if (location.getBlock().getType() != Material.AIR || headLocation.getBlock().getType() != Material.AIR) {
            Location tallestStandingPoint = new Location(Bukkit.getWorld("world"), location.getX(), location.getY(), location.getZ());
            teleportTo = Tools.highestStandingPoint(tallestStandingPoint);
            Tools.tellPlayer(new ErrorChat(), player, LangDict.getString("home.obstructedHome"), ChatColor.RED);
        }
        player.teleport(teleportTo);

        // update status & tell player
        PlayerDataManager.updatePlayerStatus(player, LangDict.getString("playerStatus.home"));
        Tools.tellPlayer(player, LangDict.getString("home.teleportedHome"));

        // set game mode
        player.setGameMode(GameMode.SURVIVAL);
        return true;
    }
}
