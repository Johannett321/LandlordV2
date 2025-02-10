package com.johansvartdal.landlord.commands;

import java.util.ArrayList;

import com.johansvartdal.landlord.*;
import com.johansvartdal.landlord.chatentities.ErrorChat;
import com.johansvartdal.landlord.chatentities.InfoChat;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Bal implements CommandExecutor {
	
	private final Main plugin;
	
	public Bal(Main plugin) {
		this.plugin = plugin;
		plugin.getCommand("bal").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("This command can only be executed by players");
			return true;
		}

		Player player = (Player) sender;
		if (!plugin.properties.gameHasStarted()) {
			Tools.tellPlayer(new ErrorChat(), player, LangDict.getString("commandResponses.errorMessages.cmdNotNow"), ChatColor.RED);
			return true;
		}

		Tools.printMenuHeader(player, LangDict.getString("banking.balance"));

		// Print high end info if relevant
		if (Main.playerDataManager.getPlayerData(player).isHighEnd()) {
			Tools.printMenuOption(player, "VIP", LangDict.getString("banking.highEndStatus"));
		}
		Tools.printMenuOption(player, LangDict.getString("banking.currentBalance"), Tools.formatCurrency(round(Main.playerDataManager.getPlayerData(player).getBalance(), 2)));
		Tools.printMenuOption(player, LangDict.getString("banking.currentIncomeTax"), round(Bank.getDepositTaxPercentDisplayForPlayer(player), 2) + "%");
		Tools.printMenuOption(player, LangDict.getString("banking.currentVAT"), round(Bank.getWithdrawTaxPercentDisplay(), 2) + "%");
		Tools.printMenuOption(player, LangDict.getString("banking.currentWealthTax"), round(Bank.getWealthTaxPercentDisplayForPlayer(player), 2) + "%");
		Tools.printMenuOption(player, LangDict.getString("banking.currentPropertyTax"), Tools.formatCurrency(round(Bank.getPropertyTaxForPlayer(player), 2)));
		return true;
	}

	double round(double value, int places) {
		if (places < 0) throw new IllegalArgumentException();

		return (double) Math.round(value * places) / places;
	}
}
