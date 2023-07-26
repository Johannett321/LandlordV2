package com.johansvartdal.landlord;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;

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

        if (LevelManager.getCurrentDisplayLevelNum() == 0) {
            setTitle(LangDict.getString("events.preparations.preparations"));
            objective.setDisplayName(LangDict.getString("events.preparations.preparations"));
        }else {
            setTitle(LangDict.getString("upgrade.scoreboardSeason") + LevelManager.getCurrentDisplaySeasonNum() + " " + LangDict.getString("upgrade.scoreboardLevel") + (LevelManager.getCurrentDisplayLevelNum()));
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

                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.setScoreboard(board);
                }
            }
        }, 20*5);
    }

    public void refreshMoney(Objective objective) {
        Score bankBalance = objective.getScore(ChatColor.AQUA + LangDict.getString("banking.governmentBank"));
        bankBalance.setScore(Bank.getBankBalance());

        for (Player player : Bukkit.getOnlinePlayers()) {
            Score score = objective.getScore(ChatColor.YELLOW + player.getDisplayName());
            score.setScore(Bank.getPlayerBalance(player)); //Integer only!
        }
    }

    public void getCurrentReqScores(Objective objective) {
        // Make sure we actually have a level
        if (LevelManager.getCurrentLevel() == null) {
            return;
        }

        ArrayList<ItemStack> requiredItems = LevelManager.getRequiredItemsForNextLevel();

        for (int i = 0; i < requiredItems.size(); i++) {
            ItemStack remaining = LevelManager.getRemainingItem(requiredItems.get(i).getType());

            Score score;

            // item is fulfilled
            if (remaining == null) {
                board.resetScores(ChatColor.RED + requiredItems.get(i).getType().name().toLowerCase());
                continue;
            }

            score = objective.getScore(ChatColor.RED + remaining.getType().name().toLowerCase());
            score.setScore(remaining.getAmount()); //Integer only!
        }
    }

    public void warnNewLevel(int newSeason, int newLevel) {
        setTitle(LangDict.getString("upgrade.scoreboardSeason") + newSeason + " " + LangDict.getString("upgrade.scoreboardLevel") + newLevel);
        ArrayList<ItemStack> requiredItems = LevelManager.getRequiredItemsForNextLevel();

        for (int i = 0; i < requiredItems.size(); i++) {
            board.resetScores(ChatColor.RED + requiredItems.get(i).getType().name().toLowerCase());
        }
    }

    public void setTitle(String title) {
        objective.setDisplayName(title);
    }
}
