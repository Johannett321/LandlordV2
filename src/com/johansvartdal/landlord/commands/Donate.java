package com.johansvartdal.landlord.commands;

import com.johansvartdal.landlord.LangDict;
import com.johansvartdal.landlord.Main;
import com.johansvartdal.landlord.Tools;
import com.johansvartdal.landlord.levels.LevelManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Donate implements CommandExecutor {

private Main plugin;

	public Donate(Main plugin) {
		this.plugin = plugin;
		plugin.getCommand("donate").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (Tools.stateNotNormal(sender)) {
			Tools.tellPlayer(sender, LangDict.getString(LangDict.CMD_NOT_NOW), ChatColor.RED);
			return true;
		}

		Player player = (Player) sender;
		ItemStack itemInMainHand = player.getInventory().getItemInMainHand();

		if (!Main.levelManager.itemRequiredForUpgrade(itemInMainHand)) {
			Tools.tellPlayer(player, "This item is not required for upgrade", ChatColor.RED);
			return true;
		}

		Main.levelManager.donateItem(player, itemInMainHand);
		return true;
	}
}
