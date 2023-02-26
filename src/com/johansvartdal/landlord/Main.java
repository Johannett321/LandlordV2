package com.johansvartdal.landlord;

import com.johansvartdal.landlord.commands.*;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {

	public static Properties properties;
	public static Configurator configurator;
	public static LangDict langDict;
	public static TradeCenter tradeCenter;
	public static PlayerDataManager playerDataManager;
	
	@Override
	public void onEnable() {
		Tools.init(this);
		properties = new Properties();
		properties.load();
		configurator = new Configurator(this);
		configurator.configure();
		langDict = new LangDict();
		tradeCenter = new TradeCenter(Bukkit.getWorlds().get(0));
		playerDataManager = new PlayerDataManager(Bukkit.getWorlds().get(0), this);
		playerDataManager.loadData();

		new Landlord(this);
		new BuyChunk(this);
		new Day(this);
		new DailyBonus(this);
		new Bal(this);
		new Home(this);
		new Trade(this);

		getServer().getPluginManager().registerEvents(this, this);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (!playerDataManager.playerExists(event.getPlayer())) {
			if (properties.gameHasStarted()) {
				event.getPlayer().kickPlayer("You are not allowed to join");
				return;
			}

			PlayerData playerData = new PlayerData(event.getPlayer().getWorld(), event.getPlayer());
			playerDataManager.addNewPlayer(playerData);
		}
	}

}
