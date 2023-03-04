package com.johansvartdal.landlord.commands;

import com.johansvartdal.landlord.*;
import com.johansvartdal.landlord.events.Preparations;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Landlord implements CommandExecutor {

    private Main plugin;

    public Landlord(Main plugin) {
        this.plugin = plugin;
        plugin.getCommand("landlord").setExecutor(this);;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player) commandSender;
        if (strings.length < 1) {
            if (!player.isOp()) {
                commandSender.sendMessage("You are not allowed to use this command");
            }else {
                Tools.printMenuHeader(player, "COMMANDS");
                Tools.printMenuOption(player, "/landlord", "start");
            }
            return true;
        }

        // START GAME
        if (strings[0].equalsIgnoreCase("start")) {
            if (Main.properties.gameHasStarted()) {
                commandSender.sendMessage("The game is already running!");
                return true;
            }

            Main.tradeCenter.setLocation(player.getWorld(),
                    player.getLocation().getChunk().getX()*16+8+0.5,
                    player.getLocation().getY(),
                    player.getLocation().getChunk().getZ()*16+8+0.5);

            Preparations preparationsEvent = new Preparations(plugin);
            preparationsEvent.setOnEventEndListener(() -> {
                Main.levelManager.startLevel1();
            });
            preparationsEvent.setMainWorld(player.getWorld());
            preparationsEvent.startEvent();
        }
        return true;
    }
}
