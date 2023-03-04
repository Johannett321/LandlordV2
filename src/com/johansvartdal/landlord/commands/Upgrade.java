package com.johansvartdal.landlord.commands;

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
			return true;
		}

		Player player = (Player) sender;

		if (args.length == 0) {
			sender.sendMessage("--- Commands ---");
			sender.sendMessage("/upgrade info");
			sender.sendMessage("/upgrade accept");
			return true;
		}

		if (args[0].equals("info")) {
			sender.sendMessage(ChatColor.DARK_GRAY + "UPGRADE INFO:");
			sender.sendMessage(ChatColor.DARK_GRAY + "Current level: " + ChatColor.GRAY + Main.levelManager.getCurrentDisplayLevelNum());
			sender.sendMessage(ChatColor.DARK_GRAY + "Donations remaining: " + ChatColor.GRAY + Main.levelManager.getRemainingItemsText());
			sender.sendMessage(ChatColor.DARK_GRAY + "Players accepted: " + ChatColor.GRAY + Main.levelManager.getAcceptedPlayersText());
		}else if (args[0].equals("accept")) {
			if (Main.levelManager.playerHasAccepted(player)) {
				sender.sendMessage("You have already accepted");
				return true;
			}
			Main.levelManager.playerAcceptsUpgrade(player);
		}else {
			return false;
		}
		return true;
	}
}
