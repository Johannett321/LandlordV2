package com.johansvartdal.landlord.commands;

import com.johansvartdal.landlord.*;
import com.johansvartdal.landlord.chatentities.ErrorChat;
import com.johansvartdal.landlord.events.taxevents.ChooseTreasuryEvent;
import com.johansvartdal.landlord.levels.LevelManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class TreasuryCommand implements CommandExecutor {

    private Main plugin;
    TreasuryChancellorCommandHandler treasuryChancellorCommandHandler;

    @RequiredArgsConstructor
    @Getter
    public static class VoteHolder {
        private final Player player;
        private ArrayList<Player> votes = new ArrayList<>();

        public void addVote(Player player) {
            votes.add(player);
        }
    }

    public static ArrayList<VoteHolder> treasuryPlayers = new ArrayList<>();
    ArrayList<Player> playersVotedForResign = new ArrayList<>();

    public TreasuryCommand(Main plugin) {
        this.plugin = plugin;
        this.treasuryChancellorCommandHandler = new TreasuryChancellorCommandHandler(plugin);
        plugin.getCommand("treasury").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;

        // check if unlocked
        if (!LevelManager.featureUnlocked("treasury") && !Properties.DEV_CHEAT_MODE) {
            Tools.tellPlayer(new ErrorChat(), player, LangDict.getString(LangDict.CMD_NOT_UNLOCKED));
            return true;
        }

        // show menu
        if (args.length == 0) {
            // thise player is chancellor
            if (Bank.playerIsTreasuryChancellor(player)) {
                int donationsPrice = StaticValues.TREASURY_DONATIONS_BASE_PRICE + LevelManager.getNumberOfRemainingItemsTotal() * StaticValues.TREASURY_DONATIONS_PRICE_PER_UNIT;
                Tools.printMenuHeader(player, LangDict.getString("generalSentenceParts.commands"));
                Tools.printMenuOption(player, "/treasury", "buy haste " + ChatColor.GOLD + "(" + Tools.formatCurrency(StaticValues.TREASURY_HASTE_PRICE) + ")");
                Tools.printMenuOption(player, "/treasury", "buy chunkdiscount " + ChatColor.GOLD + "(" + Tools.formatCurrency(StaticValues.TREASURY_CHUNK_DISCOUNT_PRICE) + ")");
                Tools.printMenuOption(player, "/treasury", "buy donations " + ChatColor.GOLD + "(" + Tools.formatCurrency(donationsPrice) + ")");
                Tools.printMenuOption(player, "/treasury", "withdraw "+ ChatColor.GOLD + "(" + Tools.formatCurrency(StaticValues.TREASURY_WITHDRAW_PRICE) + ")");
                return true;
            }

            Tools.printMenuHeader(player, LangDict.getString("generalSentenceParts.commands"));

            // chancellor is chosen
            if (Bank.aTreasuryChancellorIsChosen()) {
                Tools.printMenuOption(player, "/treasury", "voteresign");
                return true;
            }

            // chancellor has NOT been chosen yet
            Tools.printMenuOption(player, "/treasury", "apply");
            Tools.printMenuOption(player, "/treasury", "vote [PLAYER]");
            return true;
        }

        if (args[0].equals("voteresign")) {
            if (!Bank.aTreasuryChancellorIsChosen() || !Main.properties.gameStateIsNormal()) {
                Tools.tellPlayer(new ErrorChat(), player, LangDict.getString(LangDict.CMD_NOT_NOW), ChatColor.RED);
                return true;
            }
            if (args.length != 1) {
                return false;
            }

            voteForResignation(player);
            return true;
        }

        if (Bank.playerIsTreasuryChancellor(player)) {
            return treasuryChancellorCommandHandler.executeChancellorCommand(player, args);
        }else {
            return normalCommand(player, args);
        }
    }

    private Boolean normalCommand(Player player, String[] args) {
        if (args[0].equals("apply")) {
            if (!(LandlordEventManager.getCurrentEvent() instanceof ChooseTreasuryEvent)) {
                Tools.tellPlayer(new ErrorChat(), player, LangDict.getString(LangDict.CMD_NOT_NOW), ChatColor.RED);
                return true;
            }

            applyForTreasury(player);
        }else if (args[0].equals("vote")) {
            if (!(LandlordEventManager.getCurrentEvent() instanceof ChooseTreasuryEvent)) {
                Tools.tellPlayer(new ErrorChat(), player, LangDict.getString(LangDict.CMD_NOT_NOW), ChatColor.RED);
                return true;
            }
            if (args.length != 2) {
                return false;
            }

            voteForPlayer(player, args[1]);
        }else {
            return false;  // command not typed in correctly
        }
        return true;
    }

    private void voteForResignation(Player player) {
        if (playersVotedForResign.contains(player)) {
            Tools.tellPlayer(new ErrorChat(), player, LangDict.getString("treasury.cannotVoteTwice"), ChatColor.RED);
        }
        playersVotedForResign.add(player);
        if (Main.properties.getNumberOfPlayers() <= 2 || (playersVotedForResign.size() > Main.properties.getNumberOfPlayers() / 2)) {
            Tools.broadcastMessage(LangDict.getString("treasury.treasuryResigned"), ChatColor.RED);
            Bank.resignChancellor(plugin);
        }else {
            Tools.broadcastMessage(LangDict.getString("treasury.someoneVotedResignation"));
        }
    }

    private void applyForTreasury(Player player) {
        for (VoteHolder voteHolder : treasuryPlayers) {
            if (voteHolder.getPlayer().equals(player)) {
                Tools.tellPlayer(new ErrorChat(), player, LangDict.getString("treasury.alreadyApplied"), ChatColor.RED);
                return;
            }
        }

        VoteHolder voteHolder = new VoteHolder(player);
        treasuryPlayers.add(voteHolder);
        Tools.tellPlayer(player, LangDict.getString("treasury.appliedForTreasury"), ChatColor.GREEN);
    }

    private void voteForPlayer(Player player, String voteUsername) {
        if (voteUsername.equalsIgnoreCase(player.getDisplayName()) && !Properties.DEV_CHEAT_MODE) {
            Tools.tellPlayer(new ErrorChat(), player, LangDict.getString(LangDict.CANNOT_USE_ON_YOURSELF), ChatColor.RED);
            return;
        }
        for (VoteHolder voteHolder : treasuryPlayers) {
            for (Player playerVote : voteHolder.getVotes()) {
                if (playerVote.equals(player)) {
                    Tools.tellPlayer(new ErrorChat(), player, LangDict.getString("treasury.alreadyVoted"), ChatColor.RED);
                    return;
                }
            }
        }

        // add the current players vote
        for (VoteHolder voteHolder : treasuryPlayers) {
            if (voteHolder.getPlayer().getDisplayName().equalsIgnoreCase(voteUsername)) {
                Tools.tellPlayer(player, LangDict.getString("treasury.votedFor") + voteUsername + "!", ChatColor.GREEN);
                voteHolder.addVote(player);
                return;
            }
        }

        Tools.tellPlayer(new ErrorChat(), player, LangDict.getString("treasury.cannotFindVoluntary") + voteUsername, ChatColor.RED);
    }
}
