package com.johansvartdal.landlord.commands;

import com.johansvartdal.landlord.*;
import com.johansvartdal.landlord.levels.LevelManager;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Day implements CommandExecutor {

	private Main plugin;
	private int commandPrice = 100;
	
	public Day(Main plugin) {
		this.plugin = plugin;
		plugin.getCommand("day").setExecutor(this);;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (Tools.stateNotNormal(sender)) {
			Tools.tellPlayer(sender, LangDict.getString(LangDict.CMD_NOT_NOW), ChatColor.RED);
			return true;
		}

		Player player = (Player) sender;

		if (!LevelManager.featureUnlocked("day")) {
			Tools.tellPlayer(player, LangDict.getString(LangDict.CMD_NOT_UNLOCKED), ChatColor.RED);
			return true;
		}

		if (!Bank.playerCanAfford(player, commandPrice)) {
			Tools.tellPlayer(player, "You cannot afford this command (" + commandPrice + LangDict.getString("currency") + " + tax)", ChatColor.RED);
			return true;
		}

		Bank.withdrawPlayer(player, commandPrice);

		World world = player.getWorld();
		world.setTime(0);
		world.setStorm(false);

		Tools.tellPlayer(player, "You paid " + commandPrice + LangDict.getString("currency") + " for this magic spell");
		God.speak(player.getDisplayName() + " has paid for a magic spell!");
		Tools.playSoundForEveryone(Sound.ENTITY_LIGHTNING_BOLT_THUNDER);
		return false;
	}

}