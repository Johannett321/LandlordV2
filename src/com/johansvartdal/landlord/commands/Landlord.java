package com.johansvartdal.landlord.commands;

import com.johansvartdal.landlord.ChunkBuilder;
import com.johansvartdal.landlord.Main;
import com.johansvartdal.landlord.OnLandlordEventEndListener;
import com.johansvartdal.landlord.Properties;
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
                commandSender.sendMessage("--- Commands ---");
                commandSender.sendMessage("/landlord start");
                commandSender.sendMessage("/landlord end");
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
                    player.getLocation().getBlockX(),
                    player.getLocation().getBlockY(),
                    player.getLocation().getBlockZ());

            Preparations preparationsEvent = new Preparations(plugin);
            preparationsEvent.setOnEventEndListener(() -> {
                Main.levelManager.startLevel1();
            });
            preparationsEvent.setMainWorld(player.getWorld());
            preparationsEvent.startEvent();
        }
        return true;
    }

    public String getPlayerFacingDirection(Player player) {
        int yaw = (int) player.getLocation().getYaw();

        // normalize the angle to be between 0 and 359
        yaw = yaw % 360;
        if (yaw < 0) {
            yaw += 360;
        }

        // determine the direction based on the angle
        if (yaw >= 45 && yaw < 135) {
            return "west"; // west
        } else if (yaw >= 135 && yaw < 225) {
            return "north";  // north
        } else if (yaw >= 225 && yaw < 315) {
            return "east";  // east
        } else {
            return "south";  // south
        }
    }
}
