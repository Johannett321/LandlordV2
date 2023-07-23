package com.johansvartdal.landlord.commands;

import com.johansvartdal.landlord.*;
import com.johansvartdal.landlord.LevelManager;
import com.johansvartdal.landlord.chatentities.ErrorChat;
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
			Tools.tellPlayer(new ErrorChat(), player, LangDict.getString(LangDict.CMD_NOT_UNLOCKED), ChatColor.RED);
			return true;
		}

		if (!Bank.playerCanAfford(player, commandPrice)) {
			Tools.tellPlayer(new ErrorChat(), player, LangDict.getString("youNeed") + commandPrice + LangDict.getString("currency") + LangDict.getString("forThisCommand"), ChatColor.RED);
			return true;
		}

		Bank.withdrawPlayer(LangDict.getString("aMagicSpell"), player, commandPrice);

		World world = player.getWorld();
		world.setTime(0);
		world.setStorm(false);

		God.speak(player.getDisplayName() + LangDict.getString("playerPaidForSpell"));
		Tools.playSoundForEveryone(Sound.ENTITY_LIGHTNING_BOLT_THUNDER);
		return false;
	}
}