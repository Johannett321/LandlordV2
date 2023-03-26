package com.johansvartdal.landlord.commands;

import com.johansvartdal.landlord.*;
import com.johansvartdal.landlord.events.Preparations;
import com.johansvartdal.landlord.LevelManager;
import org.bukkit.*;
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
        if (!player.isOp()) {
            commandSender.sendMessage("You are not allowed to use this command");
            return true;
        }

        if (strings.length < 1) {
            Tools.printMenuHeader(player, "COMMANDS");
            Tools.printMenuOption(player, "/landlord", "init");
            Tools.printMenuOption(player, "/landlord", "start");
            return true;
        }

        // START GAME
        if (strings[0].equalsIgnoreCase("start")) {
            if (Main.properties.gameHasStarted()) {
                commandSender.sendMessage("The game is already running!");
                return true;
            }

            if (player.getWorld().equals(Bukkit.getWorld("lladv"))) {
                Tools.tellPlayer(player, "You must run '/landlord config' before you can run this command", ChatColor.RED);
                return true;
            }

            startGame(player);
        }else if (strings[0].equals("config")) {
            configure(player);
        }
        return true;
    }

    private void configure(Player opPlayer) {
        // get location of the highest standing point
        Location location = Tools.highestStandingPoint(new Location(Bukkit.getWorld("world"), 0,0,0));
        opPlayer.teleport(location);

        // fix weather and time
        World world = Bukkit.getWorld("world");
        world.setTime(0);
        world.setStorm(false);

        // transform player to spectator
        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                Tools.broadcastMessage(opPlayer.getDisplayName()+ " is now looking for a suitable location for the game. Please be patient!", new Player[]{opPlayer});

                Tools.tellPlayer(opPlayer, "You are now in spectator mode. Please find a location you would like to start the game");
                Tools.tellPlayer(opPlayer, "When you are ready, run '/landlord start'");
                opPlayer.setGameMode(GameMode.SPECTATOR);
            }
        }, Tools.secToTicks(2));
    }

    public void startGame(Player player) {
        Location location = player.getLocation();
        location.setX(player.getLocation().getChunk().getX()*16+8+0.5);
        location.setY(0);
        location.setZ(player.getLocation().getChunk().getZ()*16+8+0.5);
        location = Tools.highestStandingPoint(location);

        Main.tradeCenter.setLocation(location);

        Preparations preparationsEvent = new Preparations(plugin);
        preparationsEvent.setMainWorld(player.getWorld());
        LandlordEventManager.startEvent(preparationsEvent);
    }
}
