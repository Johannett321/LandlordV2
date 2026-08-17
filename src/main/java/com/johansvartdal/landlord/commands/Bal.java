package com.johansvartdal.landlord.commands;

import com.johansvartdal.landlord.*;
import com.johansvartdal.landlord.chatentities.ErrorChat;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.johansvartdal.landlord.Tools.round;

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
		if (!plugin.properties.gameHasStarted() || !Main.playerDataManager.playerExists(player)) {
			Tools.tellPlayer(new ErrorChat(), player, LangDict.getString("commandResponses.errorMessages.cmdNotNow"), ChatColor.RED);
			return true;
		}

		Tools.printMenuHeader(player, LangDict.getString("banking.balance"));

		// Print high end info if relevant
		if (Bank.isMillionaire(player)) {
			Tools.printMenuOption(player, "Millionaire", LangDict.getString("banking.millionaireEndStatus"));
		}else if (Bank.isHighEnd(player)) {
			Tools.printMenuOption(player, "VIP", LangDict.getString("banking.highEndStatus"));
		}
		Tools.printMenuOption(player, LangDict.getString("banking.currentBalance"), Tools.formatCurrency(round(Bank.getPlayerBalance(player), 2)));
		Tools.printMenuOption(player, LangDict.getString("banking.currentIncomeTax"), round(Bank.getDepositTaxPercentDisplayForPlayer(player), 2) + "%");
		Tools.printMenuOption(player, LangDict.getString("banking.currentVAT"), round(Bank.getWithdrawTaxPercentDisplay(), 2) + "%");
		Tools.printMenuOption(player, LangDict.getString("banking.currentWealthTax"), round(Bank.getWealthTaxPercentDisplayForPlayer(player), 2) + "%");
		Tools.printMenuOption(player, LangDict.getString("banking.currentPropertyTax"), Tools.formatCurrency(round(Bank.getPropertyTaxForPlayer(player), 2)));
		return true;
	}
}
