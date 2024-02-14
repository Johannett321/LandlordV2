package com.johansvartdal.landlord.commands;

import com.johansvartdal.landlord.*;
import com.johansvartdal.landlord.chatentities.BankChat;
import com.johansvartdal.landlord.chatentities.ErrorChat;
import com.johansvartdal.landlord.levels.LevelManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Pay implements CommandExecutor {

	private Main plugin;

	public Pay(Main plugin) {
		this.plugin = plugin;
		plugin.getCommand("pay").setExecutor(this);;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (Tools.stateNotNormal(sender)) {
			Tools.tellPlayer(sender, LangDict.getString(LangDict.CMD_NOT_NOW), ChatColor.RED);
			return true;
		}

		Player player = (Player) sender;

		// Has feature been unlocked yet?
		if (!LevelManager.featureUnlocked("pay")) {
			Tools.tellPlayer(new ErrorChat(), player, LangDict.getString(LangDict.CMD_NOT_UNLOCKED), ChatColor.RED);
			return true;
		}

		// make sure format is correct
		if (args.length != 2) {
			Tools.printMenuHeader(player, "PAY");
			Tools.printMenuOption(player, "/pay", "<username> <amount>");
			return true;
		}

		// attempt to find receiving player
		String username = args[0];
		if (!Main.playerDataManager.playerExists(username)) {
			Tools.tellPlayer(new ErrorChat(), player, LangDict.getString("visit.couldNotFindPlayer" + username));
			return true;
		}

		String amountString = args[1];
		int amount = Integer.parseInt(amountString);

		// make sure amount is higher than 50
		if (amount <= 50) {
			Tools.tellPlayer(new ErrorChat(), player, LangDict.getString("banking.amountTooLow") + amount + LangDict.getString(LangDict.CURRENCY));
			return true;
		}

		// check if player can afford
		if (!Bank.playerCanAfford(player, amount)) {
			Tools.tellPlayer(new ErrorChat(), player, LangDict.getString(LangDict.YOU_CANNOT_AFFORD_) + amount + LangDict.getString(LangDict.CURRENCY) + LangDict.getString(LangDict._PLUS_TAX));
			return true;
		}

		// actually transfer
		Player receivingPlayer = Bukkit.getPlayer(username);
		Bank.withdrawPlayer(LangDict.getString("banking.moneyTransferServices"), player, amount);
		Bank.depositPlayerWithoutTax(receivingPlayer, Main.playerDataManager.getPlayerData(username), amount);
		Tools.tellPlayer(new BankChat(), receivingPlayer, player.getDisplayName() + LangDict.getString("banking.justTransferred") + amount + LangDict.getString(LangDict.CURRENCY) + LangDict.getString("banking.toYourAccount"));
		return true;
	}
}