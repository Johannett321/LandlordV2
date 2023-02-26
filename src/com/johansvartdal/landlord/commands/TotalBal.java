package com.johansvartdal.landlord.commands;

import java.util.ArrayList;

import com.johansvartdal.landlord.Main;
import com.johansvartdal.landlord.PlayerMoneyClass;
import com.johansvartdal.landlord.Tools;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TotalBal implements CommandExecutor {
	
private Main plugin;
	
	public TotalBal(Main plugin) {
		this.plugin = plugin;
		plugin.getCommand("bal").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("This command can only be executed by players");
			return true;
		}
		return false;
	}
}
