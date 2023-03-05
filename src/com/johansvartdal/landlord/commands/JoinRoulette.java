package com.johansvartdal.landlord.commands;

import com.johansvartdal.landlord.Main;
import com.johansvartdal.landlord.RouletteGame;
import com.johansvartdal.landlord.Tools;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JoinRoulette implements CommandExecutor {

    private Main plugin;

    public JoinRoulette(Main plugin) {
        this.plugin = plugin;
        plugin.getCommand("joinroulette").setExecutor(this);

        if (Main.levelManager.getCurrentDisplayLevelNum() <= 3) {
            new RouletteGame(plugin);
        }
    }
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player) commandSender;

        if (!Main.properties.gameStateIsNormal()) {
            Tools.tellPlayer(player, "You cannot execute this command at the moment", ChatColor.RED);
            return true;
        }

        if (Main.levelManager.getCurrentDisplayLevelNum() > 3) {
            Tools.tellPlayer(player, "This command is not unlocked yet", ChatColor.RED);
            return true;
        }

        RouletteGame.addToGame(player);
        return true;
    }
}
