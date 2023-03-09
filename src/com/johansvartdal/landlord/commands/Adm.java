package com.johansvartdal.landlord.commands;

import com.johansvartdal.landlord.LangDict;
import com.johansvartdal.landlord.Main;
import com.johansvartdal.landlord.Properties;
import com.johansvartdal.landlord.Tools;
import org.bukkit.Bukkit;
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

        if (strings[0].equals("lladv")) {
            player.teleport(new Location(Bukkit.getWorld("lladv"), 194, 81, -112));
            Tools.tellPlayer(player, "Welcome to lladv");
            player.setGameMode(GameMode.CREATIVE);
        }else {
            Tools.printMenuHeader(player, "COMMANDS");
            Tools.printMenuOption(player, "/adm", "lladv");
        }
        return true;
    }
}
