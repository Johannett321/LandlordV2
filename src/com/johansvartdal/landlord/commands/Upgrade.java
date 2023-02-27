package com.johansvartdal.landlord.commands;

import com.johansvartdal.landlord.Main;
import com.johansvartdal.landlord.Tools;
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
			return false;
		}

		if (args[0].equals("info")) {
			sender.sendMessage("---- UPGRADE INFO ---");
			sender.sendMessage("Item donations remaining before upgrade: " + Main.levelManager.getRemainingItemsText());
			sender.sendMessage("Players who have accepted the upgrade: " + Main.levelManager.getAcceptedPlayersText());
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
