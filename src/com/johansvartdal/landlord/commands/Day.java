package com.johansvartdal.landlord.commands;

import com.johansvartdal.landlord.Main;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Day implements CommandExecutor {

	private Main plugin;
	
	public Day(Main plugin) {
		this.plugin = plugin;
		plugin.getCommand("day").setExecutor(this);;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("This command can only be executed by players");
			return true;
		}else {
			Player player = (Player) sender;
			World world = player.getWorld();
			world.setTime(0);
			world.setStorm(false);
			sender.sendMessage("Everything clear :)");
		}
		return false;
	}

}