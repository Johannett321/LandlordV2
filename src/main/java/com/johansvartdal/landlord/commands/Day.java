package com.johansvartdal.landlord.commands;

import com.johansvartdal.landlord.*;
import com.johansvartdal.landlord.levels.LevelManager;
import com.johansvartdal.landlord.chatentities.ErrorChat;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Day implements CommandExecutor {

	private int commandPrice = 14990;
	
	public Day(Main plugin) {
		plugin.getCommand("day").setExecutor(this);;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (Tools.stateNotNormal(sender)) {
			Tools.tellPlayer(sender, LangDict.getString(LangDict.CMD_NOT_NOW), ChatColor.RED);
			return true;
		}

		Player player = (Player) sender;

		if (!Main.playerDataManager.playerExists(player)) {
			Tools.tellPlayer(player, LangDict.getString(LangDict.CMD_NOT_NOW), ChatColor.RED);
			return true;
		}

		// Has feature been unlocked yet?
		if (!LevelManager.featureUnlocked("day")) {
			Tools.tellPlayer(new ErrorChat(), player, LangDict.getString(LangDict.CMD_NOT_UNLOCKED), ChatColor.RED);
			return true;
		}

		// Can player afford
		if (!Bank.playerCanAfford(player, commandPrice)) {
			Bank.tellPlayerTheyNeed(player, commandPrice, LangDict.getString("banking.forThisCommand"));
			return true;
		}

		// Withdraw the player
		Bank.withdrawPlayer(LangDict.getString("playerEvents.day.aMagicSpell"), player, commandPrice);

		World world = player.getWorld();
		world.setTime(0);
		world.setStorm(false);

		God.speak(player.getDisplayName() + LangDict.getString("playerEvents.day.playerPaidForSpell"));
		Tools.playSoundForEveryone(Sound.ENTITY_LIGHTNING_BOLT_THUNDER);
		return false;
	}
}