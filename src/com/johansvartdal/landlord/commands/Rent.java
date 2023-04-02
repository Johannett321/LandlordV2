package com.johansvartdal.landlord.commands;

import com.johansvartdal.landlord.*;
import com.johansvartdal.landlord.chatentities.ErrorChat;
import com.johansvartdal.landlord.renting.RentableItem;
import com.johansvartdal.landlord.renting.RentedElytra;
import com.johansvartdal.landlord.renting.RentedPickaxe;
import com.johansvartdal.landlord.renting.RentManager;
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

		// command info
		if (args.length == 0) {
			Tools.printMenuHeader(player, "RENT");
			Tools.printMenuOption(player, "/rent", "[TOOL]");
			Tools.printMenuOption(player, "/rent", "end");
			Tools.printMenuOption(player, "/rent", "info");
			return true;
		}

		// info
		if (args[0].equalsIgnoreCase("end")) {
			attemptEndRent(player);
			return true;
		}else if (args[0].equalsIgnoreCase("info")) {
			Tools.printMenuHeader(player, "RENTABLE ITEMS");
			Tools.printMenuOption(player, "Pickaxe:", "15kr");
			Tools.printMenuOption(player, "Elytra:", "15kr");
			return true;
		}else if (args[0].equalsIgnoreCase("pickaxe")) {
			attemptRentPickaxe(player);
			return true;
		}else if (args[0].equalsIgnoreCase("elytra")) {
			attemptRentElytra(player);
			return true;
		}
		return false;
	}

	private void attemptEndRent(Player player) {
		if (!RentManager.itemIsRented(player.getInventory().getItemInMainHand())) {
			Tools.tellPlayer(new ErrorChat(), player, "Please hold the rented item in your main hand");
			return;
		}
		RentManager.cancelRentOfItem(player);
	}

	private void attemptRentPickaxe(Player player) {
		RentedPickaxe rentablePickaxe = new RentedPickaxe(plugin);
		rentItem(player, rentablePickaxe, "rentPickaxe");
	}

	private void attemptRentElytra(Player player) {
		RentedElytra rentableElytra = new RentedElytra(plugin);
		rentItem(player, rentableElytra, "rentElytra");
	}

	private void rentItem(Player player, RentableItem rentableItem, String featureName) {
		// unlocked yet
		if (!LevelManager.featureUnlocked(featureName)) {
			Tools.tellPlayer(new ErrorChat(), player, LangDict.getString(LangDict.CMD_NOT_UNLOCKED));
			return;
		}

		// make sure player is not already renting this
		if (RentManager.playerCurrentlyRentingItem(rentableItem)) {
			Tools.tellPlayer(new ErrorChat(), player, "You cannot rent a " + rentableItem.getItemName() + " twice");
			return;
		}

		// can afford
		if (!Bank.playerCanAfford(player, rentableItem.getItemRentPrice())) {
			Tools.tellPlayer(new ErrorChat(), player, LangDict.getString(LangDict.YOU_CANNOT_AFFORD_ + "renting a " + rentableItem.getItemName()));
			return;
		}

		// rent and give player pickaxe
		Bank.withdrawPlayer("renting a " + rentableItem.getItemName(), player, rentableItem.getItemRentPrice());
		RentManager.rentItem(player, rentableItem);
	}
}
