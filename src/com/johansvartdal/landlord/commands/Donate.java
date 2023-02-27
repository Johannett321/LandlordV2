package com.johansvartdal.landlord.commands;

import com.johansvartdal.landlord.Main;
import com.johansvartdal.landlord.Tools;
import com.johansvartdal.landlord.levels.LevelManager;
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
			return true;
		}

		Player player = (Player) sender;
		ItemStack itemInMainHand = player.getInventory().getItemInMainHand();

		if (!Main.levelManager.itemRequiredForUpgrade(itemInMainHand)) {
			sender.sendMessage("This item is not required for upgrade");
			return true;
		}

		Main.levelManager.donateItem(player, itemInMainHand);
		return true;
	}
}
