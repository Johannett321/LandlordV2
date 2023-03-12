package com.johansvartdal.landlord.commands;

import com.johansvartdal.landlord.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Adm implements CommandExecutor {

    private Main plugin;

    public Adm(Main plugin) {
        this.plugin = plugin;
        plugin.getCommand("adm").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player) commandSender;
        if (!player.isOp() || !Properties.DEBUG_MODE) {
            Tools.tellPlayer(player, LangDict.getString(LangDict.CMD_NOT_NOW));
            return true;
        }

        if (strings.length == 0) {
            Tools.printMenuHeader(player, "COMMANDS");
            Tools.printMenuOption(player, "/adm", "lladv");
            Tools.printMenuOption(player, "/adm", "motherload");
            Tools.printMenuOption(player, "/adm", "forceup");
            return true;
        }

        if (strings[0].equals("lladv")) {
            player.teleport(new Location(Bukkit.getWorld("lladv"), 194, 81, -112));
            Tools.tellPlayer(player, "Welcome to lladv");
            player.setGameMode(GameMode.CREATIVE);
        }else if (strings[0].equals("motherload")) {
            Bank.depositPlayerWithoutTax(player, 20000);
            Tools.tellPlayer(player, "Money reloaded!", ChatColor.GREEN);
        }else if (strings[0].equals("forceup")) {
            Tools.tellPlayer(player, "Forcing upgrade!", ChatColor.YELLOW);
            LevelManager.forceProceedToNextLevel();
        }
        return true;
    }
}
