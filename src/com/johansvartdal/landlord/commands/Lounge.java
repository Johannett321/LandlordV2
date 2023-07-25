package com.johansvartdal.landlord.commands;

import com.johansvartdal.landlord.*;
import com.johansvartdal.landlord.chatentities.ErrorChat;
import com.johansvartdal.landlord.playerevents.FlyingEvent;
import com.johansvartdal.landlord.playerevents.LoungeEvent;
import com.johansvartdal.landlord.playerevents.PlayerEvent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;

public class Lounge implements CommandExecutor {

	private Main plugin;

	ArrayList<LoungeVisit> cooldown;
	private class LoungeVisit {
		Player player;
		long visitTimeMillis;
	}

	public Lounge(Main plugin) {
		this.plugin = plugin;
		plugin.getCommand("lounge").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = (Player) sender;
		PlayerData playerData = Main.playerDataManager.getPlayerData(player);

		// Check if player is high end
		if (!playerData.isHighEnd()) {
			Tools.tellPlayer(new ErrorChat(), player, LangDict.getString("commandResponses.errorMessages.tooPoorForCommand"));
			return true;
		}

		// game state normal
		if (Tools.stateNotNormal(player)) {
			Tools.tellPlayer(new ErrorChat(), player, LangDict.getString(LangDict.CMD_NOT_NOW));
			return true;
		}

		// invalid format
		if (args.length > 1) {
			return false;
		}

		// print lounge menu
		if (args.length == 0) {
			Tools.printMenuHeader(player, LangDict.getString("playerEvents.lounge.loungeCommand"));
			Tools.printMenuOption(player, "/lounge", "now");
			Tools.printMenuOption(player, "/lounge", "info");
			return true;
		}

		// command args
		if (args[0].equalsIgnoreCase("now")) { 	// now
			attemptLounge(player);
			return true;
		}else if (args[0].equalsIgnoreCase("info")) {	// info
			showInfoMenu(player);
			return true;
		}
		return false;
	}

	private void showInfoMenu(Player player) {
		Tools.printMenuHeader(player, "LOUNGE INFO");
		Tools.printMenuOption(player, LangDict.getString("playerEvents.lounge.whatIsIt"), LangDict.getString("playerEvents.lounge.description"));
	}

	private void attemptLounge(Player player) {
		// check if player is in event
		if (PlayerEventManager.playerIsInEvent(player)) {
			Tools.tellPlayer(new ErrorChat(), player, LangDict.getString(LangDict.CMD_NOT_NOW));
			return;
		}

		// check cooldown
		if (isPlayerInCooldown(player)) {
			Tools.tellPlayer(new ErrorChat(), player, LangDict.getString("playerEvents.lounge.cooldown"));
			return;
		}

		// player is not in cooldown. Take him to the lounge
		PlayerEventManager.startPlayerEvent(new LoungeEvent(plugin, player));
	}

	private boolean isPlayerInCooldown(Player player) {
		Iterator<LoungeVisit> iterator = cooldown.iterator();
		while (iterator.hasNext()) {
			LoungeVisit loungeVisit = iterator.next();
			long millisSinceVisit = System.currentTimeMillis()-loungeVisit.visitTimeMillis;

			// check if more than 20 min since last visit
			if (millisSinceVisit > 20*60*1000) {
				iterator.remove();
			}

			// check if this player in cooldown is the player that issued the command
			if (loungeVisit.player.equals(player)) {
				return true;
			}
		}
		return false;
	}
}
