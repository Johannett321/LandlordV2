package com.johansvartdal.landlord.commands;

import com.johansvartdal.landlord.*;
import com.johansvartdal.landlord.chatentities.ErrorChat;
import com.johansvartdal.landlord.chatentities.InfoChat;
import com.johansvartdal.landlord.events.LandlordEventManager;
import com.johansvartdal.landlord.events.Preparations;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Landlord implements CommandExecutor {

    private Main plugin;

    private boolean configExecuted = false;

    private ArrayList<String> allowUsernamesToJoin = new ArrayList<>();

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
            if (!Main.properties.gameHasStarted()) {
                Tools.printMenuHeader(player, "COMMANDS");
                Tools.printMenuOption(player, "/landlord", "config");
                Tools.printMenuOption(player, "/landlord", "start");
            }else {
                Tools.printMenuHeader(player, "COMMANDS");
                Tools.printMenuOption(player, "/landlord", "addplayer <USERNAME>");
            }
            return true;
        }

        // check if game is already running
        if (Main.properties.gameHasStarted()) {
            if (strings[0].equalsIgnoreCase("addplayer")) {
                if (strings.length == 2) {
                    allowUsernamesToJoin.add(strings[1].toLowerCase().trim());
                    Tools.tellPlayer(new InfoChat(), player, strings[1].toLowerCase().trim() + " " + LangDict.getString("commandResponses.successMessages.playerAdded"));
                    return true;
                }else {
                    Tools.tellPlayer(new ErrorChat(), player, "/landlord addplayer <username>");
                }
            }
        }else {
            // Understand args
            if (strings[0].equalsIgnoreCase("start")) { // start game
                // make sure player has exected /landlord config
                if (!configExecuted) {
                    Tools.tellPlayer(new ErrorChat(), player, LangDict.getString("events.preparations.youMustConfigFirst"), ChatColor.RED);
                    return true;
                }

                // make sure player has already configured the game
                if (player.getWorld().equals(Bukkit.getWorld("lladv"))) {
                    Tools.tellPlayer(new ErrorChat(), player, LangDict.getString("events.preparations.youMustConfigFirst"), ChatColor.RED);
                    return true;
                }

                startGame(player);
                return true;
            }else if (strings[0].equals("config")) { // config game
                configure(player);
                return true;
            }
        }

        if (Main.properties.gameHasStarted()) {
            Tools.tellPlayer(new ErrorChat(), player, "/Landlord [ addplayer ]");
        }else {
            Tools.tellPlayer(new ErrorChat(), player, "/Landlord [ start | config ]");
        }
        return true;
    }

    private void configure(Player opPlayer) {
        configExecuted = true;

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
        Main.properties.notifyGameStarted();

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

    public boolean playerHasBeenAllowed(Player joinedPlayer) {
        return allowUsernamesToJoin.contains(joinedPlayer.getDisplayName().toLowerCase().trim());
    }
}
