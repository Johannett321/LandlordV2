package com.johansvartdal.landlord.commands;

import com.johansvartdal.landlord.*;
import com.johansvartdal.landlord.chatentities.ErrorChat;
import com.johansvartdal.landlord.levels.LevelManager;
import com.johansvartdal.landlord.renting.*;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Rent implements CommandExecutor {

	private final Main plugin;

	public Rent(Main plugin) {
		this.plugin = plugin;
		plugin.getCommand("rent").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (Tools.stateNotNormal(sender)) {
			return true;
		}

		Player player = (Player) sender;

		if (!Main.playerDataManager.playerExists(player)) {
			Tools.tellPlayer(player, LangDict.getString(LangDict.CMD_NOT_NOW), ChatColor.RED);
			return true;
		}

		if (!LevelManager.featureUnlocked("rent_basic_tool")) {
			Tools.tellPlayer(new ErrorChat(), player, LangDict.getString(LangDict.CMD_NOT_UNLOCKED));
			return true;
		}

		// command info
		if (args.length == 0) {
			Tools.printMenuHeader(player, "RENT");
			Tools.printMenuOption(player, "/rent", "[TOOL]");
			Tools.printMenuOption(player, "/rent", "end");
			Tools.printMenuOption(player, "/rent", "autoend");
			Tools.printMenuOption(player, "/rent", "info");
			return true;
		}

		// info
		if (args[0].equalsIgnoreCase("end")) {
			attemptEndRent(player);
			return true;
		}else if (args[0].equalsIgnoreCase("autoend")) {
			addPlayerToAutoEndList(player);
			return true;
		}else if (args[0].equalsIgnoreCase("info")) {
			Tools.printMenuHeader(player, LangDict.getString("itemRent.rentableItems"));
			printRentableItem(player, new RentableSword(plugin), "rent_basic_tool");
			printRentableItem(player, new RentablePickaxe(plugin), "rent_basic_tool");
			printRentableItem(player, new RentableAxe(plugin), "rent_basic_tool");
			printRentableItem(player, new RentableShovel(plugin), "rent_basic_tool");
			printRentableItem(player, new RentableElytra(plugin), "rent_elytra");
			printRentableItem(player, new RentableTurtleShell(plugin), "rent_turtle_shell");
			return true;
		}else if (args[0].equalsIgnoreCase("sword")) {
			attemptRentSword(player);
			return true;
		}else if (args[0].equalsIgnoreCase("pickaxe")) {
			attemptRentPickaxe(player);
			return true;
		}else if (args[0].equalsIgnoreCase("axe")) {
			attemptRentAxe(player);
			return true;
		}else if (args[0].equalsIgnoreCase("shovel")) {
			attemptRentShovel(player);
			return true;
		}else if (args[0].equalsIgnoreCase("elytra")) {
			attemptRentElytra(player);
			return true;
		}else if (args[0].equalsIgnoreCase("turtle_shell")) {
			attemptRentTurtleShell(player);
			return true;
		}
		return false;
	}

	private void addPlayerToAutoEndList(Player player) {
		if (!RentManager.playerCurrentlyRentingItems(player)) {
			Tools.tellPlayer(new ErrorChat(), player, LangDict.getString("commandResponses.errorMessages.commandOnlyWhileRenting"), ChatColor.RED);
			return;
		}

		RentManager.toggleAutoEndForPlayer(player);
	}

	private void printRentableItem(Player player, RentableItem rentableItem, String featureName) {
		if (featureName != null) {
			if (!LevelManager.featureUnlocked(featureName)) {
				return;
			}
		}
		Tools.printMenuOption(player, "/rent " + rentableItem.getItemName() + ":", Tools.formatCurrency(rentableItem.getItemRentPrice()) + LangDict.getString("itemRent.everyTwoMinutes"));
	}

	private void attemptEndRent(Player player) {
		if (!RentManager.itemIsRented(player.getInventory().getItemInMainHand())) {
			Tools.tellPlayer(new ErrorChat(), player, LangDict.getString("itemRent.pleaseHoldMainHand"));
			return;
		}
		RentManager.cancelRentOfItem(player);
	}

	private void attemptRentSword(Player player) {
		RentableSword rentedItem = new RentableSword(plugin);
		rentItem(player, rentedItem, "rent_basic_tool");
	}

	private void attemptRentPickaxe(Player player) {
		RentablePickaxe rentedItem = new RentablePickaxe(plugin);
		rentItem(player, rentedItem, "rent_basic_tool");
	}

	private void attemptRentAxe(Player player) {
		RentableAxe rentedItem = new RentableAxe(plugin);
		rentItem(player, rentedItem, "rent_basic_tool");
	}

	private void attemptRentShovel(Player player) {
		RentableShovel rentedItem = new RentableShovel(plugin);
		rentItem(player, rentedItem, "rent_basic_tool");
	}

	private void attemptRentElytra(Player player) {
		RentableElytra rentedItem = new RentableElytra(plugin);
		rentItem(player, rentedItem, "rent_elytra");
	}

	private void attemptRentTurtleShell(Player player) {
		RentableTurtleShell rentedItem = new RentableTurtleShell(plugin);
		rentItem(player, rentedItem, "rent_turtle_shell");
	}

	private void rentItem(Player player, RentableItem rentableItem, String featureName) {
		// unlocked yet
		if (!LevelManager.featureUnlocked(featureName)) {
			Tools.tellPlayer(new ErrorChat(), player, LangDict.getString(LangDict.CMD_NOT_UNLOCKED));
			return;
		}

		// make sure player is not already renting this
		if (RentManager.playerCurrentlyRentingItem(player, rentableItem)) {
			Tools.tellPlayer(new ErrorChat(), player, LangDict.getString("itemRent.cannotRentTwice"));
			return;
		}

		// can afford
		if (!Bank.playerCanAfford(player, rentableItem.getItemRentPrice())) {
			Bank.tellPlayerCannotAfford(player, LangDict.getString("itemRent.rentingA") + rentableItem.getItemName(), rentableItem.getItemRentPrice());
			return;
		}

		// rent and give player pickaxe
		Bank.withdrawPlayer(LangDict.getString("itemRent.rentingA") + rentableItem.getItemName(), player, rentableItem.getItemRentPrice());
		RentManager.rentItem(player, rentableItem);
	}
}
