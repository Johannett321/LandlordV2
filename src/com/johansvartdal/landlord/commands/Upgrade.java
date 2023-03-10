package com.johansvartdal.landlord.commands;

import com.johansvartdal.landlord.LangDict;
import com.johansvartdal.landlord.Main;
import com.johansvartdal.landlord.Tools;
import com.johansvartdal.landlord.LevelManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Upgrade implements CommandExecutor {

	private Main plugin;

	public Upgrade(Main plugin) {
		this.plugin = plugin;
		plugin.getCommand("upgrade").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (Tools.stateNotNormal(sender)) {
			Tools.tellPlayer(sender, LangDict.getString(LangDict.CMD_NOT_NOW), ChatColor.RED);
			return true;
		}

		Player player = (Player) sender;

		if (args.length == 0) {
			Tools.printMenuHeader(player, "COMMANDS");
			Tools.printMenuOption(player, "/upgrade", "info");
			Tools.printMenuOption(player, "/upgrade", "accept");
			if (player.isOp()) {
				Tools.printMenuOption(player, "/upgrade", "force");
			}
			return true;
		}

		if (args[0].equals("info")) {
			Tools.printMenuHeader(player, "UPGRADE INFO");
			Tools.printMenuOption(player, "Current level: ", String.valueOf(LevelManager.getCurrentDisplayLevelNum()));
			Tools.printMenuOption(player, "Donations remaining: ", LevelManager.getRemainingItemsText());
			Tools.printMenuOption(player, "Players accepted: ", LevelManager.getAcceptedPlayersText());
		}else if (args[0].equals("accept")) {
			if (LevelManager.playerHasAccepted(player)) {
				Tools.tellPlayer(player, "You have already accepted");
				return true;
			}
			LevelManager.playerAcceptsUpgrade(player);
		}else if (args[0].equals("force")) {
			if (!player.isOp()) {
				Tools.tellPlayer(player, "You don't have access to this command", ChatColor.RED);
				return true;
			}
			LevelManager.forceUpgrade(player);
		} else {
			return false;
		}
		return true;
	}
}
