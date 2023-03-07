package com.johansvartdal.landlord.commands;

import java.io.File;
import java.util.Calendar;

import com.johansvartdal.landlord.Bank;
import com.johansvartdal.landlord.Main;
import com.johansvartdal.landlord.Tools;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DailyBonus implements CommandExecutor {
	
private Main plugin;
	
	public DailyBonus(Main plugin) {
		this.plugin = plugin;
		plugin.getCommand("claimbonus").setExecutor(this);;
	}
	
	//claiming daily bonus will not work when the year changes
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("This command can only be executed by players");
			return true;
		}

		//TODO REVIEW THIS FILE

		Player player = (Player) sender;
		
		Calendar nowCal = Calendar.getInstance();
		long nowDay = nowCal.get(Calendar.DAY_OF_YEAR);
		
		long startDayMillis;
		long lastDayMillis;
		
		File startDayFile = new File(player.getUniqueId() + "/startDayStreak.txt");
		if (startDayFile.exists()) {
			startDayMillis = Long.parseLong(Tools.read(player.getUniqueId() + "/startDayStreak.txt"));
			lastDayMillis = Long.parseLong(Tools.read(player.getUniqueId() + "/endDayStreak.txt"));;
		}else {
			Tools.write(player.getUniqueId() + "/startDayStreak.txt", String.valueOf(nowCal.getTimeInMillis()));
			startDayMillis = nowCal.getTimeInMillis();
			lastDayMillis = nowCal.getTimeInMillis();
		}
		
		
		Calendar startCal = Calendar.getInstance();
		startCal.setTimeInMillis(startDayMillis);
		
		Calendar endCal = Calendar.getInstance();
		endCal.setTimeInMillis(lastDayMillis);
		
		int dayStreak;
		
		if (endCal.get(Calendar.DAY_OF_YEAR) < nowDay) {
			dayStreak = nowCal.get(Calendar.DAY_OF_YEAR)-startCal.get(Calendar.DAY_OF_YEAR);	
		}else {
			sender.sendMessage("You have already claimed your daily reward for today!");
			return true;
		}
		
		if (nowDay - endCal.get(Calendar.DAY_OF_YEAR) > 1) {
			sender.sendMessage("You just lost your daily streak of " + String.valueOf(dayStreak) + " days!");
			dayStreak = 0;
			Tools.write(player.getUniqueId() + "/startDayStreak.txt", String.valueOf(startCal.getTimeInMillis()));
		}
		
		
		int dailyBonus = dayStreak*650;
		if (dailyBonus > 7500) {
			dailyBonus = 7500;
		}
		
		sender.sendMessage("You just claimed your daily bonus of $" + String.valueOf(dailyBonus));
		Tools.write(player.getUniqueId() + "/endDayStreak.txt", String.valueOf(nowCal.getTimeInMillis()));
		Bank.depositPlayer(player, dailyBonus);
		return false;
	}
}
