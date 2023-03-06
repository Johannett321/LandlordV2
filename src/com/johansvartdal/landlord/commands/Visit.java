package com.johansvartdal.landlord.commands;

import com.johansvartdal.landlord.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Visit implements CommandExecutor {

    private Main plugin;
    private ArrayList<WantsVisit> wantsVisits;

    public Visit(Main plugin) {
        this.plugin = plugin;
        wantsVisits = new ArrayList<>();
        plugin.getCommand("visit").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        Player player = (Player) commandSender;

        if (Main.levelManager.getCurrentDisplayLevelNum() < 11 && !Properties.DEBUG_MODE) {
            Tools.tellPlayer(player, "This command has not been unlocked yet!", ChatColor.RED);
            return true;
        }

        // display commands
        if (args.length < 1) {
            Tools.printMenuHeader(player, "VISIT");
            Tools.printMenuOption(player, "/visit", "accept");
            Tools.printMenuOption(player, "/visit", "reject");
            Tools.printMenuOption(player, "/visit", "[username]");
            return true;
        }

        // accept
        if (args[0].equals("accept")) {
            acceptVisit(player);
            return true;
        }

        // reject
        if (args[0].equals("reject")) {
            rejectVisit(player);
            return true;
        }

        String teleportToString = args[0];
        Player teleportTo = Bukkit.getPlayer(teleportToString);

        // make sure player exists
        if (teleportTo == null) {
            Tools.tellPlayer(player, "Could not find player: " + teleportToString, ChatColor.RED);
            return true;
        }

        // dont visit yourself
        if (teleportTo == player) {
            Tools.tellPlayer(player, "You cannot visit yourself", ChatColor.RED);
            return true;
        }

        if (!Bank.playerCanAfford(player, StaticValues.VISIT_PRICE)) {
            Tools.tellPlayer(player,"You cannot afford a visit. The price is " + StaticValues.VISIT_PRICE + " + tax", ChatColor.RED);
            return true;
        }

        addVisitWaiter(teleportTo, player);
        Tools.tellPlayer(player, "You are now waiting for " + args[0] + " to reply.", ChatColor.GREEN);
        Tools.tellPlayer(teleportTo,"You have a new visit request from " + player.getDisplayName() + "! Type '/visit [accept | reject]' to answer", ChatColor.GREEN);
        return true;
    }

    private void addVisitWaiter(Player host, Player visitor) {
        WantsVisit wantsVisit = new WantsVisit();
        wantsVisit.host = host;
        wantsVisit.visitor = visitor;
        wantsVisits.add(wantsVisit);
        scheduleRemovalOfRequest(wantsVisit);
    }

    private void scheduleRemovalOfRequest(WantsVisit wantsVisit) {
        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                wantsVisits.remove(wantsVisit);
            }
        }, 20*15);
    }

    private void acceptVisit(Player player) {
        for (int i = 0; i < wantsVisits.size(); i++) {
            // find the right player
            if (!wantsVisits.get(i).host.getUniqueId().toString().equals(player.getUniqueId().toString())) {
                continue;
            }

            if (!Bank.playerCanAfford(wantsVisits.get(i).visitor, StaticValues.VISIT_PRICE)) {
                Tools.tellPlayer(player,"You cannot afford a visit. The price is " + StaticValues.VISIT_PRICE + " + tax", ChatColor.RED);
                return;
            }

            Bank.withdrawPlayer(player, StaticValues.VISIT_PRICE);
            Bank.depositPlayer(player, StaticValues.VISIT_PRICE);
            wantsVisits.get(i).visitor.setGameMode(GameMode.ADVENTURE);
            wantsVisits.get(i).visitor.teleport(wantsVisits.get(i).host);
            Tools.tellPlayer(wantsVisits.get(i).host, wantsVisits.get(i).visitor.getDisplayName() + " is now visiting you! The visitor paid you " + StaticValues.VISIT_PRICE + " as a visit fee", ChatColor.GREEN);
            Tools.tellPlayer(wantsVisits.get(i).visitor,"You are now visiting " + wantsVisits.get(i).host.getDisplayName() + ". You paid the host " + StaticValues.VISIT_PRICE + " as a visit fee", ChatColor.GREEN);
            wantsVisits.remove(i);
            return;
        }
        Tools.tellPlayer(player,"You have no visit requests", ChatColor.RED);
    }

    private void rejectVisit(Player player) {
        for (int i = 0; i < wantsVisits.size(); i++) {
            if (wantsVisits.get(i).host.getUniqueId().toString().equals(player.getUniqueId().toString())) {
                Tools.tellPlayer(wantsVisits.get(i).host, wantsVisits.get(i).visitor.getDisplayName() + " will not be visiting you!", ChatColor.RED);
                Tools.tellPlayer(wantsVisits.get(i).visitor,"You were not allowed to visit " + wantsVisits.get(i).host.getDisplayName(), ChatColor.RED);
                return;
            }
        }
        Tools.tellPlayer(player,"You have no visit requests", ChatColor.RED);
    }

    private class WantsVisit {
        public Player visitor;
        public Player host;
    }
}
