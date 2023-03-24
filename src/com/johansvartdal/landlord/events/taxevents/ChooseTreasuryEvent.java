package com.johansvartdal.landlord.events.taxevents;

import com.johansvartdal.landlord.*;
import com.johansvartdal.landlord.commands.TreasuryCommand;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.Random;

public class ChooseTreasuryEvent extends LandlordEvent {

    private final Location voteSpawnLocation = new Location(Bukkit.getWorld("lladv"),202.5,92,-1081.5);
    private BukkitTask currentTimer = null;
    private final Location treasuryGoldBoxLocation = new Location(Bukkit.getWorld("lladv"), 202.5, 95, -1077.5);

    public ChooseTreasuryEvent(Main plugin) {
        super(plugin);
        treasuryGoldBoxLocation.setYaw(0);
        treasuryGoldBoxLocation.setPitch(-90);

        voteSpawnLocation.setPitch(0);
        voteSpawnLocation.setYaw(0);
    }

    @Override
    public void startEvent() {
        saveAllPrevLocs();
        teleportALlPlayersToEvent();
        resumeFromHere();
    }

    private void resumeFromHere() {
        informPlayers();
        Tools.performTaskAfterCountdown(this::getMostVoted, "The Treasury Chancellor will be decided in", 120);
    }

    @Override
    public void endEvent(Boolean cancelled) {
        super.endEvent(cancelled);
        if (currentTimer != null) {
            currentTimer.cancel();
        }
        teleportAllPlayersBack();
    }

    private void chooseRandom() {
        Random random = new Random();
        int playersOnline = Bukkit.getOnlinePlayers().size();
        int randomNumber = random.nextInt(playersOnline);

        Player chosenPlayer = Bukkit.getOnlinePlayers().toArray(new Player[Bukkit.getOnlinePlayers().size()])[randomNumber];
        makePlayerTheTreasury(chosenPlayer);
    }

    private void getMostVoted() {
        int currentBestSum = 0;
        Player currentBestPlayer = null;

        // calculate most votes
        for (TreasuryCommand.VoteHolder voteHolder : TreasuryCommand.treasuryPlayers) {
            if (voteHolder.getVotes().size() > currentBestSum) {
                currentBestPlayer = voteHolder.getPlayer();
            }
        }

        // no players were voted on, choose random
        if (currentBestPlayer == null) {
            chooseRandom();
            return;
        }

        // promote player to treasury
        makePlayerTheTreasury(currentBestPlayer);

        // clear treasuryPlayersList
        TreasuryCommand.treasuryPlayers.clear();
    }

    private void makePlayerTheTreasury(Player player) {
        playTreasuryAnimation(player);
    }

    private void playTreasuryAnimation(Player player) {
        // levitation effect
        God.speak("A Treasury Chancellor has been chosen");
        PotionEffect levitationFast = new PotionEffect(PotionEffectType.LEVITATION, (int) Tools.secToTicks(10), 1);
        player.addPotionEffect(levitationFast);

        // after three seconds
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            // remove levitation
            player.removePotionEffect(PotionEffectType.LEVITATION);

            // teleport player
            player.teleport(treasuryGoldBoxLocation);

            // effects
            player.getWorld().spawnParticle(Particle.SPELL_WITCH, treasuryGoldBoxLocation,100, 2F, 2F, 2F);
            player.getWorld().spawnParticle(Particle.SMOKE_NORMAL, treasuryGoldBoxLocation,100, 2F, 2F, 2F);
            Tools.playSoundForEveryone(Sound.ITEM_TOTEM_USE);
            Tools.playSoundForEveryone(Sound.ENTITY_PLAYER_LEVELUP);

            // promote player
            Bank.promotePlayerToTreasuryChancellor(player);

            // inform others
            Tools.broadcastMessage(player.getDisplayName() + " has been chosen for The Treasury Chancellor role!", ChatColor.GOLD);

            // schedule end
            scheduleEndEvent();
        }, Tools.secToTicks(3));
    }

    private void scheduleEndEvent() {
        Bukkit.getScheduler().runTaskLater(plugin, () -> endEvent(false), Tools.secToTicks(10));
    }

    private void informPlayers() {
        Tools.broadcastTitle("Treasury Voting Hall", null);
        God.speak("Greetings, and welcome to the Treasury voting hall. We need someone to take care of The Treasury Chancellor role");
        Tools.broadcastMessage("If you want to apply for the role, please type /treasury apply");
        Tools.broadcastMessage("If you want to make a vote, please type /treasury vote [username]");
        Tools.broadcastMessage("The role will be decided soon!");
    }

    private void teleportALlPlayersToEvent() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.teleport(voteSpawnLocation);
        }
        lockPlayersAtLocation(voteSpawnLocation, 20);
    }

    @Override
    public String getEventType() {
        return "VoteForTreasury";
    }

    @Override
    public void resumeEvent() {
        resumeFromHere();
    }
}
