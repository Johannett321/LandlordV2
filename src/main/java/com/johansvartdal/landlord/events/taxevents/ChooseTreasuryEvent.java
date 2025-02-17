package com.johansvartdal.landlord.events.taxevents;

import com.johansvartdal.landlord.*;
import com.johansvartdal.landlord.commands.TreasuryCommand;
import com.johansvartdal.landlord.events.LandlordEvent;
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
        updatePlayerStatuses();
        resumeFromHere();
    }

    private void updatePlayerStatuses() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerDataManager.updatePlayerStatus(player, LangDict.getString("playerStatus.treasuryVoting"));
        }
    }

    private void resumeFromHere() {
        informPlayers();
        int decisionTime = 120;
        if (Properties.DEV_CHEAT_MODE) {
            decisionTime = 10;
        }
        Tools.performTaskAfterCountdown(this::getMostVoted, LangDict.getString("treasury.treasuryDecidingIn"), decisionTime);
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
        God.speak(LangDict.getString("treasury.treasuryIsChosen"));
        PotionEffect levitationFast = new PotionEffect(PotionEffectType.LEVITATION, (int) Tools.secToTicks(10), 1);
        player.addPotionEffect(levitationFast);

        // after three seconds
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            // remove levitation
            player.removePotionEffect(PotionEffectType.LEVITATION);

            // teleport player
            player.teleport(treasuryGoldBoxLocation);

            // effects
            player.getWorld().spawnParticle(Particle.WITCH, treasuryGoldBoxLocation,100, 2F, 2F, 2F);
            player.getWorld().spawnParticle(Particle.SMOKE, treasuryGoldBoxLocation,100, 2F, 2F, 2F);
            Tools.playSoundForEveryone(Sound.ITEM_TOTEM_USE);
            Tools.playSoundForEveryone(Sound.ENTITY_PLAYER_LEVELUP);

            // promote player
            Bank.promotePlayerToTreasuryChancellor(player);

            // inform others
            Tools.broadcastMessage(player.getDisplayName() + LangDict.getString("treasury.playerHasBeenChosen"), ChatColor.GOLD);

            // schedule end
            scheduleEndEvent();
        }, Tools.secToTicks(3));
    }

    private void scheduleEndEvent() {
        Bukkit.getScheduler().runTaskLater(plugin, () -> endEvent(false), Tools.secToTicks(10));
    }

    private void informPlayers() {
        Tools.broadcastTitle(LangDict.getString("treasury.treasuryVotingHall"), null);
        God.speak(LangDict.getString("treasury.treasuryWelcome"));
        Tools.broadcastMessage(LangDict.getString("treasury.treasuryApplyInfo"));
        Tools.broadcastMessage(LangDict.getString("treasury.treasuryVoteInfo"));
        Tools.broadcastMessage(LangDict.getString("treasury.roleDecidingSoon"));
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

    @Override
    protected int getPreparationTimeSeconds() {
        return 30;
    }
}
