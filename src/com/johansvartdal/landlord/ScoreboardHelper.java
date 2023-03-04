package com.johansvartdal.landlord;

import com.johansvartdal.landlord.levels.LevelManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.*;

public class ScoreboardHelper {

    private Plugin plugin;
    ScoreboardManager manager;
    Scoreboard board;
    Objective objective;

    public ScoreboardHelper(Plugin plugin) {
        this.plugin = plugin;
        manager = Bukkit.getScoreboardManager();
        board = manager.getNewScoreboard();

        showScoreboard();
    }

    private void showScoreboard() {
        Team team = board.registerNewTeam("teamname");

        for (Player player : Bukkit.getOnlinePlayers()) {
            team.addPlayer(player);
        }

        team.setDisplayName("display name");

        objective = board.registerNewObjective("test", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        if (Main.levelManager.getCurrentDisplayLevelNum() == null) {
            objective.setDisplayName("Preparations");
        }else {
            objective.setDisplayName("Season " + Main.levelManager.getCurrentDisplaySeasonNum() + " level " + (Main.levelManager.getCurrentDisplayLevelNum()));
        }

        scheduleNewRefresh(objective);
    }

    private void scheduleNewRefresh(Objective objective) {
        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                refreshMoney(objective);
                getCurrentReqScores(objective);
                scheduleNewRefresh(objective);
            }
        }, 20*5);
    }

    public void refreshMoney(Objective objective) {
        Score bankBalance = objective.getScore(ChatColor.AQUA + LangDict.getString("governmentBank"));
        bankBalance.setScore(Bank.getBankBalance());

        for (Player player : Bukkit.getOnlinePlayers()) {
            Score score = objective.getScore(ChatColor.YELLOW + player.getDisplayName());
            score.setScore(Bank.getPlayerBalance(player)); //Integer only!
        }
    }

    public void getCurrentReqScores(Objective objective) {
        /*
        UpgradeRequirement[] currentReq = Upgrade.requirements;

        for (int i = 0; i < currentReq.length; i++) {
            Score score;
            if (currentReq[i].getAmountNeeded() == currentReq[i].getCurrentAmount()) {
                score = objective.getScore(ChatColor.GREEN + currentReq[i].getMaterial().name().toLowerCase() + "(" + currentReq[i].getAmountNeeded() + ")");
                objective.getScoreboard().resetScores(ChatColor.RED + currentReq[i].getMaterial().name().toLowerCase() + "(" + currentReq[i].getAmountNeeded() + ")");
            }else {
                score = objective.getScore(ChatColor.RED + currentReq[i].getMaterial().name().toLowerCase() + "(" + currentReq[i].getAmountNeeded() + ")");
            }

            score.setScore(currentReq[i].getCurrentAmount()); //Integer only!
        }
         */

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setScoreboard(board);
        }
    }

    /*
    public void warnNewUpgrade(int newSeason, int newLevel, UpgradeRequirement[] oldUpgrades) {
        objective.setDisplayName("Season " + newSeason + " level " + (newLevel - Upgrade.getCurrentSeasonSubtraction()));
        for (int i = 0; i < oldUpgrades.length; i++) {
            board.resetScores(ChatColor.GREEN + oldUpgrades[i].getMaterial().name().toLowerCase() + "(" + oldUpgrades[i].getAmountNeeded() + ")");
        }
    }
     */
}
