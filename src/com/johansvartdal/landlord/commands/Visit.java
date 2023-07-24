package com.johansvartdal.landlord.commands;

import com.johansvartdal.landlord.*;
import com.johansvartdal.landlord.LevelManager;
import com.johansvartdal.landlord.chatentities.ErrorChat;
import com.johansvartdal.landlord.playerevents.PlayerEvent;
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
        if (Tools.stateNotNormal(commandSender)) {
            Tools.tellPlayer(commandSender, LangDict.getString(LangDict.CMD_NOT_NOW), ChatColor.RED);
            return true;
        }

        Player player = (Player) commandSender;

        if (!LevelManager.featureUnlocked("visit")) {
            Tools.tellPlayer(new ErrorChat(), player, LangDict.getString(LangDict.CMD_NOT_UNLOCKED), ChatColor.RED);
            return true;
        }

        // display commands
        if (args.length < 1) {
            Tools.printMenuHeader(player, LangDict.getString("commands"));
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
            Tools.tellPlayer(new ErrorChat(), player, LangDict.getString("couldNotFindPlayer") + teleportToString, ChatColor.RED);
            return true;
        }

        // don't visit yourself
        if (teleportTo == player) {
            Tools.tellPlayer(new ErrorChat(), player, LangDict.getString(LangDict.CANNOT_USE_ON_YOURSELF), ChatColor.RED);
            return true;
        }

        // make sure visitor can afford
        if (!Bank.playerCanAfford(player, StaticValues.VISIT_PRICE)) {
            Tools.tellPlayer(new ErrorChat(), player,LangDict.getString(LangDict.YOU_NEED) + StaticValues.VISIT_PRICE + LangDict.getString("plusTax") + LangDict.getString("visit.toVisit"), ChatColor.RED);
            return true;
        }

        addVisitWaiter(teleportTo, player);
        Tools.tellPlayer(player, LangDict.getString("visit.waitingForReply") + args[0] + LangDict.getString("visit.toReply"), ChatColor.GREEN);
        Tools.tellPlayer(teleportTo,LangDict.getString("visit.newVisitRequest") + player.getDisplayName() + LangDict.getString("visit.visitReqInstructions"), ChatColor.GREEN);
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
        Bukkit.getScheduler().runTaskLater(plugin, () -> wantsVisits.remove(wantsVisit), Tools.secToTicks(15));
    }

    private void acceptVisit(Player player) {
        for (int i = 0; i < wantsVisits.size(); i++) {
            // find the right player
            if (!wantsVisits.get(i).host.getUniqueId().toString().equals(player.getUniqueId().toString())) {
                continue;
            }

            // make sure visitor can still afford
            if (!Bank.playerCanAfford(wantsVisits.get(i).visitor, StaticValues.VISIT_PRICE)) {
                Tools.tellPlayer(new ErrorChat(), player,LangDict.getString(LangDict.YOU_NEED) + StaticValues.VISIT_PRICE + LangDict.getString("plusTax") + LangDict.getString("visit.toVisit"), ChatColor.RED);
                return;
            }

            // make sure visitor is not in event that can't cancel
            PlayerEvent playerEvent = PlayerEventManager.getEventForPlayer(wantsVisits.get(i).visitor);
            if (playerEvent != null) {
                // can tp away?
                if (!playerEvent.playerTPAwayAllowed()) {
                    Tools.tellPlayer(new ErrorChat(), wantsVisits.get(i).visitor, LangDict.getString(LangDict.CMD_NOT_NOW));
                    return;
                }

                // end event
                playerEvent.endEvent();
            }

            // withdraw player
            Bank.withdrawPlayer("a visit", player, StaticValues.VISIT_PRICE);
            Bank.depositPlayer(player, StaticValues.VISIT_PRICE);

            // perform teleportation
            wantsVisits.get(i).visitor.setGameMode(GameMode.ADVENTURE);
            wantsVisits.get(i).visitor.teleport(wantsVisits.get(i).host);

            // inform
            Tools.tellPlayer(wantsVisits.get(i).host, wantsVisits.get(i).visitor.getDisplayName() + LangDict.getString("visit.isNowVisiting") + StaticValues.VISIT_PRICE + LangDict.getString("visit.asAVisitFee"), ChatColor.GREEN);
            Tools.tellPlayer(wantsVisits.get(i).visitor,LangDict.getString("visit.youAreNowVisiting") + wantsVisits.get(i).host.getDisplayName() + LangDict.getString("visit.youPaidTheHost") + StaticValues.VISIT_PRICE + LangDict.getString("visit.asAVisitFee"), ChatColor.GREEN);

            // remove request
            wantsVisits.remove(i);
            return;
        }
        Tools.tellPlayer(player,LangDict.getString("visit.noVisitRequests"), ChatColor.RED);
    }

    private void rejectVisit(Player player) {
        for (int i = 0; i < wantsVisits.size(); i++) {
            if (wantsVisits.get(i).host.getUniqueId().toString().equals(player.getUniqueId().toString())) {
                Tools.tellPlayer(wantsVisits.get(i).host, wantsVisits.get(i).visitor.getDisplayName() + LangDict.getString("visit.willNotBeVisiting"), ChatColor.RED);
                Tools.tellPlayer(wantsVisits.get(i).visitor,LangDict.getString("visit.notAllowedToVisit") + wantsVisits.get(i).host.getDisplayName(), ChatColor.RED);
                return;
            }
        }
        Tools.tellPlayer(player,LangDict.getString("visit.noVisitRequests"), ChatColor.RED);
    }

    private class WantsVisit {
        public Player visitor;
        public Player host;
    }
}
