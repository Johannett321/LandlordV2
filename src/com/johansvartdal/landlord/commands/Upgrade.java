package com.johansvartdal.landlord.commands;

import com.johansvartdal.landlord.LangDict;
import com.johansvartdal.landlord.Main;
import com.johansvartdal.landlord.Tools;
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
			return true;
		}

		if (args[0].equals("info")) {
			Tools.printMenuHeader(player, "UPGRADE INFO");
			Tools.printMenuOption(player, "Current level: ", String.valueOf(Main.levelManager.getCurrentDisplayLevelNum()));
			Tools.printMenuOption(player, "Donations remaining: ", Main.levelManager.getRemainingItemsText());
			Tools.printMenuOption(player, "Players accepted: ", Main.levelManager.getAcceptedPlayersText());
		}else if (args[0].equals("accept")) {
			if (Main.levelManager.playerHasAccepted(player)) {
				Tools.tellPlayer(player, "You have already accepted");
				return true;
			}
			Main.levelManager.playerAcceptsUpgrade(player);
		}else {
			return false;
		}
		return true;
	}
}
