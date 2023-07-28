package com.johansvartdal.landlord.commands;

import com.johansvartdal.landlord.*;
import com.johansvartdal.landlord.chatentities.ErrorChat;
import com.johansvartdal.landlord.events.Preparations;
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
            Tools.tellPlayer(new ErrorChat(), player, LangDict.getString(LangDict.YOU_ARE_NOT_ALLOWED), ChatColor.RED);
            return true;
        }

        // If you only write /landlord
        if (strings.length < 1) {
            Tools.printMenuHeader(player, "COMMANDS");
            Tools.printMenuOption(player, "/landlord", "config");
            Tools.printMenuOption(player, "/landlord", "start");
            return true;
        }

        // Understand args
        if (strings[0].equalsIgnoreCase("start")) { // start game

            // check if game is already running
            if (Main.properties.gameHasStarted()) {
                commandSender.sendMessage(LangDict.getString("events.preparations.gameAlreadyRunning"));
                return true;
            }

            // make sure player has already configured the game
            if (player.getWorld().equals(Bukkit.getWorld("lladv"))) {
                Tools.tellPlayer(new ErrorChat(), player, LangDict.getString("events.preparations.youMustConfigFirst"), ChatColor.RED);
                return true;
            }

            startGame(player);
        }else if (strings[0].equals("config")) { // config game
            configure(player);
            return true;
        }
        return false;
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
                Tools.broadcastMessage(opPlayer.getDisplayName()+ LangDict.getString("events.preparations.isNowLookingForLoc"), new Player[]{opPlayer});

                Tools.tellPlayer(opPlayer, LangDict.getString("events.preparations.pleaseFindLoc"));
                Tools.tellPlayer(opPlayer, LangDict.getString("events.preparations.runLandlordStart"));
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
