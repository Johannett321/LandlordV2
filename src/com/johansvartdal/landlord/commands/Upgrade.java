package com.johansvartdal.landlord.commands;

import com.johansvartdal.landlord.*;
import com.johansvartdal.landlord.chatentities.ErrorChat;
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
			Tools.printMenuHeader(player, LangDict.getString("commands"));
			Tools.printMenuOption(player, "/upgrade", "info");
			Tools.printMenuOption(player, "/upgrade", "accept");
			if (player.isOp()) {
				Tools.printMenuOption(player, "/upgrade", "force");
			}
			return true;
		}

		if (args[0].equals("info")) {
			Tools.printMenuHeader(player, LangDict.getString("upgradeInfo"));
			Tools.printMenuOption(player, LangDict.getString("currentLevel"), String.valueOf(LevelManager.getCurrentDisplayLevelNum()));
			Tools.printMenuOption(player, LangDict.getString("donationsRemaining"), LevelManager.getRemainingItemsText());
			Tools.printMenuOption(player, LangDict.getString("playersAccepted"), LevelManager.getAcceptedPlayersText());
		}else if (args[0].equals("accept")) {
			if (LevelManager.playerHasAccepted(player)) {
				Tools.tellPlayer(new ErrorChat(), player, LangDict.getString("alreadyAccepted"));
				return true;
			}

			// check if player is flying
			if (PlayerEventManager.playerIsInFlyingEvent(player)) {
				Tools.tellPlayer(new ErrorChat(), player, LangDict.getString(LangDict.END_FLIGHT_FIRST));
				return true;
			}

			// actually accept
			LevelManager.playerAcceptsUpgrade(player);
		}else if (args[0].equals("force")) {
			if (!player.isOp()) {
				Tools.tellPlayer(new ErrorChat(), player, LangDict.getString(LangDict.YOU_ARE_NOT_ALLOWED), ChatColor.RED);
				return true;
			}
			LevelManager.forceUpgrade(player);
		} else {
			return false;
		}
		return true;
	}
}
