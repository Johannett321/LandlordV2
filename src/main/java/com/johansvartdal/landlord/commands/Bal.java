package com.johansvartdal.landlord.commands;

import java.util.ArrayList;

import com.johansvartdal.landlord.*;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Bal implements CommandExecutor {
	
	private Main plugin;
	
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
		Tools.printMenuHeader(player, LangDict.getString("banking.balance"));
		Tools.printMenuOption(player, LangDict.getString("banking.currentIncomeTax"), Bank.getDepositTaxPercentDisplayForPlayer(player) + "%");
		Tools.printMenuOption(player, LangDict.getString("banking.currentVAT"), Bank.getWithdrawTaxPercentDisplay() + "%");
		Tools.printMenuOption(player, LangDict.getString("banking.currentWealthTax"), Bank.getWealthTaxPercentDisplayForPlayer(player) + "%");
		Tools.printMenuOption(player, LangDict.getString("banking.currentBalance"), Main.playerDataManager.getPlayerData(player).getBalance() + LangDict.getString(LangDict.CURRENCY));
		return true;
	}
}
