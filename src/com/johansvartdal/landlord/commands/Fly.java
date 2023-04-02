package com.johansvartdal.landlord.commands;

import com.johansvartdal.landlord.*;
import com.johansvartdal.landlord.chatentities.ErrorChat;
import com.johansvartdal.landlord.playerevents.FlyingEvent;
import com.johansvartdal.landlord.playerevents.PlayerEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Fly implements CommandExecutor {

	private Main plugin;

	public Fly(Main plugin) {
		this.plugin = plugin;
		plugin.getCommand("fly").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = (Player) sender;

		// check unlocked
		if (!LevelManager.featureUnlocked("fly")) {
			Tools.tellPlayer(player,LangDict.getString(LangDict.CMD_NOT_UNLOCKED));
			return true;
		}

		// game state normal
		if (Tools.stateNotNormal(player)) {
			Tools.tellPlayer(player, LangDict.getString(LangDict.CMD_NOT_NOW));
			return true;
		}

		// invalid format
		if (args.length > 1) {
			return false;
		}

		// print fly menu
		if (args.length == 0) {
			Tools.printMenuHeader(player, LangDict.getString("flyCommand"));
			Tools.printMenuOption(player, "/fly", "now");
			Tools.printMenuOption(player, "/fly", "end");
			Tools.printMenuOption(player, "/fly", "info");
			return true;
		}

		// command args
		if (args[0].equalsIgnoreCase("now")) { 	// now
			attemptFlying(player);
			return true;
		}else if (args[0].equalsIgnoreCase("info")) {	// info
			showInfoMenu(player);
			return true;
		}else if (args[0].equalsIgnoreCase("end")) {	// end
			attemptEndFlight(player);
			return true;
		}
		return false;
	}

	private void showInfoMenu(Player player) {
		Tools.printMenuHeader(player, "FLY INFO");
		Tools.printMenuOption(player, LangDict.getString("pricePerMin"), StaticValues.FLYING_PRICE_PER_MINUTE + LangDict.getString(LangDict.CURRENCY));
	}

	private void attemptFlying(Player player) {
		// check if player is in event
		if (PlayerEventManager.playerIsInEvent(player)) {
			Tools.tellPlayer(player, LangDict.getString(LangDict.CMD_NOT_NOW));
			return;
		}

		// can player afford
		if (!Bank.playerCanAfford(player, StaticValues.FLYING_PRICE_PER_MINUTE)) {
			Tools.tellPlayer(player, LangDict.getString(LangDict.YOU_CANNOT_AFFORD_) + LangDict.getString("flying")
					+ LangDict.getString("for") + StaticValues.FLYING_PRICE_PER_MINUTE + LangDict.getString("currency"));
			return;
		}

		// start flying event
		Bank.withdrawPlayer(LangDict.getString("paidForFlying"), player, StaticValues.FLYING_PRICE_PER_MINUTE);
		PlayerEventManager.startPlayerEvent(new FlyingEvent(plugin, player));
	}

	private void attemptEndFlight(Player player) {
		// make sure player is flying
		PlayerEvent playerEvent = PlayerEventManager.getEventForPlayer(player);
		if (!(playerEvent instanceof FlyingEvent flyingEvent)) {
			Tools.tellPlayer(new ErrorChat(), player, LangDict.getString(LangDict.CMD_NOT_NOW));
			return;
		}

		if (player.isFlying()) {
			Tools.tellPlayer(new ErrorChat(), player, LangDict.getString("landBeforeEndFlight"));
		}

		// end flight and inform player
		flyingEvent.endEvent();
		Tools.tellPlayer(player, LangDict.getString("flightTurnedOff"), ChatColor.YELLOW);
	}
}
