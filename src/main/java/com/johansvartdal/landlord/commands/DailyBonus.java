package com.johansvartdal.landlord.commands;

import java.io.File;
import java.util.Calendar;

import com.johansvartdal.landlord.Bank;
import com.johansvartdal.landlord.LangDict;
import com.johansvartdal.landlord.Main;
import com.johansvartdal.landlord.Tools;
import com.johansvartdal.landlord.chatentities.ErrorChat;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DailyBonus implements CommandExecutor {
	
private Main plugin;

	private int dailyBonus = 650;
	
	public DailyBonus(Main plugin) {
		this.plugin = plugin;
		plugin.getCommand("claimbonus").setExecutor(this);;
	}
	
	// claiming daily bonus will not work when the year changes
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("This command can only be executed by players");
			return true;
		}

		Player player = (Player) sender;

		// Load info about streaks
		long currentTime = System.currentTimeMillis();
		int multiplier = Main.playerDataManager.getPlayerData(player).getStreakMultiplier();
		long streakCollectOpens = Main.playerDataManager.getPlayerData(player).getStreakCollectOpens();
		long deadline = Main.playerDataManager.getPlayerData(player).getStreakCollectDeadline();

		// Check if player has  already collected today
		if (currentTime < streakCollectOpens) {
			Tools.tellPlayer(new ErrorChat(), player, LangDict.getString("bonus.bonusCollectedAlready"), ChatColor.RED);
			return true;
		}

		// Check if player was too late
		if (currentTime > deadline && deadline != 0) {
			Tools.tellPlayer(player, LangDict.getString("bonus.lostBonus") + multiplier + "X!", ChatColor.RED);
		}

		// start new streak if we should
		if (deadline == 0 || currentTime > deadline) {
			startNewStreak(player);
			return true;
		}

		updateStreak(player, multiplier);
		return false;
	}

	private void startNewStreak(Player player) {
		updateStreak(player, 0);
	}

	private void updateStreak (Player player, int yesterdaysMultiplier) {
		long currentTime = System.currentTimeMillis();

		// increase multiplayer for each day before we deposit
		yesterdaysMultiplier++;

		// Get dates of deadline and open
		long deadline = currentTime + (1000*60*60*24)*2; // two days
		long streakCollectOpens = currentTime + (1000 * 60 * 60 * 24); // one day

		// Calculate bonus
		int todaysBonus = dailyBonus*yesterdaysMultiplier;

		// Deposit and tell player about it
		Bank.depositPlayer(player, dailyBonus*yesterdaysMultiplier);
		Tools.tellPlayer(player, LangDict.getString("bonus.justCollectedBonus") + todaysBonus + LangDict.getString(LangDict.CURRENCY) + " (" + yesterdaysMultiplier + LangDict.getString("generalSentenceParts.days") + ")");
		Main.playerDataManager.getPlayerData(player).updateStreak(streakCollectOpens, deadline, yesterdaysMultiplier);
	}
}
