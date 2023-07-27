package com.johansvartdal.landlord;

import com.johansvartdal.landlord.chatentities.InfoChat;
import com.johansvartdal.landlord.chatentities.WarningChat;
import com.johansvartdal.landlord.commands.*;
import com.johansvartdal.landlord.lan.LanController;
import com.johansvartdal.landlord.playerevents.JailEvent;
import com.johansvartdal.landlord.playerevents.PlayerEvent;
import com.johansvartdal.landlord.renting.RentManager;
import com.johansvartdal.landlord.webserver.WebServerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static com.johansvartdal.landlord.Tools.debugLog;

public class Main extends JavaPlugin implements Listener {

	public static ScoreboardHelper scoreboardHelper;
	public static Properties properties;
	public static Configurator configurator;
	public static TradeCenter tradeCenter;
	public static PlayerDataManager playerDataManager;
	public static WebServerManager webServerManager;
	
	@Override
	public void onEnable() {
		debugLog("Landlord is loading!");
		// before licence
		configurator = new Configurator(this);
		configurator.configure();
		Tools.init(this);
		CustomConfig.load();
		LicenceVerifier.verifyLicence();

		SpecialEffects.setPlugin(this);

		// initialize managers
		properties = new Properties();
		properties.load();
		new CheatProtection(this);
		new SleepPercentage(this);
		LangDict.loadLanguage();
		Bank.load();
		StockManager.loadStocks();
		ChunkBuilder.load();
		LevelManager.init(this);
		new EmissionTax(this);
		RentManager.registerListeners(this);
		new RandomHint(this);


		tradeCenter = new TradeCenter();
		playerDataManager = new PlayerDataManager(Bukkit.getWorlds().get(0), this);
		playerDataManager.loadData();
		scoreboardHelper = new ScoreboardHelper(this);

		// COMMANDS
		new Sell(this);
		new Landlord(this);
		new BuyChunk(this);
		new Day(this);
		new DailyBonus(this);
		new Bal(this);
		new Home(this);
		new SetHome(this);
		new Trade(this);
		new Donate(this);
		new Upgrade(this);
		new Stocks(this);
		new JoinRoulette(this);
		new Capture(this);
		new Wilderness(this);
		new Visit(this);
		new SendHome(this);
		new Adm(this);
		new SetTrade(this);
		new TreasuryCommand(this);
		new ChangeLanguage(this);
		new Fly(this);
		new Rent(this);
		new Lounge(this);
		new Status(this);

		Bank.startTaxCollector(this);

		getServer().getPluginManager().registerEvents(this, this);

		// Event
		LandlordEventManager.loadEventIfAny(this);

		// Start webserver
		webServerManager = new WebServerManager();

		// Only for LAN
		LanController.initiate();

		// inform players about server restart
		Tools.broadcastMessage(LangDict.getString("info.serverRestarted"), ChatColor.GREEN);
	}

	@Override
	public void onDisable() {
		PlayerEventManager.forceEndAllEvents();
		RentManager.forceEndAllRents();
		webServerManager.stopServer();
	}

	@EventHandler
	public void onPlayerChat(PlayerChatEvent event) {
		// Get the player
		Player chattingPlayer = event.getPlayer();
		PlayerData chattingPlayerData = playerDataManager.getPlayerData(chattingPlayer);

		// Theme the players message
		if (Bank.playerIsTreasuryChancellor(chattingPlayer)) { // treasury chancellor
			event.setCancelled(true);
			for(Player onlinePlayer: Bukkit.getOnlinePlayers()) {
				onlinePlayer.sendMessage(ChatColor.DARK_GREEN + "[" + LangDict.getString(LangDict.TREASURY_SENTINEL) + "] " + ChatColor.WHITE + event.getMessage());
			}
		}else if (chattingPlayer.getDisplayName().equalsIgnoreCase("johannett321")) { // Creator
			event.setCancelled(true);
			for(Player onlinePlayer: Bukkit.getOnlinePlayers()) {
				if (Properties.DEBUG_MODE) {
					onlinePlayer.sendMessage(ChatColor.DARK_GREEN + "[DEV] " + ChatColor.WHITE + "<Johannett321> " + event.getMessage());
				}else {
					onlinePlayer.sendMessage(ChatColor.DARK_GREEN + "[CREATOR] " + ChatColor.WHITE + "<Johannett321> " + event.getMessage());
				}
			}
		}else if (chattingPlayer.getDisplayName().equalsIgnoreCase("karafo")) { // Builder
			event.setCancelled(true);
			for(Player onlinePlayer: Bukkit.getOnlinePlayers()) {
				onlinePlayer.sendMessage(ChatColor.GREEN + "[BUILDER]" + ChatColor.WHITE + " <Karafo> " + event.getMessage());
			}
		}else if (chattingPlayerData.isHighEnd()) {
			event.setCancelled(true);
			for(Player onlinePlayer: Bukkit.getOnlinePlayers()) {
				onlinePlayer.sendMessage(ChatColor.GOLD + "[VIP]" + ChatColor.WHITE + " <" + chattingPlayer.getDisplayName() + "> " + event.getMessage());
			}
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player joinedPlayer = event.getPlayer();

		// TODO: FJERN DETTE (Info om alpha versjon)
		if (!Properties.DEBUG_MODE) {
			Tools.tellPlayer(new InfoChat(), joinedPlayer, LangDict.getString("info.alphaBuild"), ChatColor.RED);
		}

		// inform player about debug mode
		if (Properties.DEBUG_MODE) {
			Tools.tellPlayer(new WarningChat(), joinedPlayer, LangDict.getString("info.debugWarning"), ChatColor.RED);
		}

		// make sure player is not flying unless allowed to
		if (joinedPlayer.getGameMode() == GameMode.SURVIVAL || joinedPlayer.getGameMode() == GameMode.ADVENTURE) {
			// end current flight
			if (joinedPlayer.isFlying()) {
				joinedPlayer.setFlying(false);

				// add slow falling, so player doesn't hurt
				PotionEffect potionEffect = new PotionEffect(PotionEffectType.SLOW_FALLING, (int) Tools.secToTicks(20), 6);
				joinedPlayer.addPotionEffect(potionEffect);
			}

			// disapprove flight
			if (joinedPlayer.getAllowFlight()) {
				joinedPlayer.setAllowFlight(false);
			}
		}

		// update status of joined player
		PlayerDataManager.updatePlayerStatus(joinedPlayer, LangDict.getString("playerStatus.home"));

		// check if it is a returning player
		if (playerDataManager.playerExists(joinedPlayer)) {
			event.setJoinMessage(ChatColor.GREEN + "[INFO] " + ChatColor.GOLD + LangDict.getString("joinMessages.citizenJoinStart") + ChatColor.DARK_AQUA + event.getPlayer().getDisplayName() + ChatColor.GOLD + LangDict.getString("joinMessages.citizenJoinEnd"));
			return;
		}

		// ------- ONLY RUN IF PLAYER JOINS FOR THE FIRST TIME --------

		// kick player if game already running
		if (properties.gameHasStarted()) {
			event.getPlayer().kickPlayer(LangDict.getString("joinMessages.gameAlreadyRunningJoinMessage"));
			return;
		}

		// make sure number of players never exceeds max number
		if (playerDataManager.getPlayerDataList().size() >= StaticValues.MAX_PLAYERS) {
			event.getPlayer().kickPlayer(LangDict.getString("joinMessages.gameFull"));
			return;
		}

		// create the playerdata file
		PlayerData playerData = new PlayerData(event.getPlayer().getWorld(), event.getPlayer());
		playerDataManager.addNewPlayer(playerData);
		event.setJoinMessage(ChatColor.DARK_PURPLE + LangDict.getString("god") + ChatColor.WHITE + " " + LangDict.getString("joinMessages.newCitizen") + event.getPlayer().getDisplayName());

		// teleport to start location
		Bukkit.getScheduler().runTaskLater(this, ()-> {
			event.getPlayer().teleport(StaticValues.GAME_START_LOCATION);
		}, Tools.secToTicks(1));


		// inform OP about commands
		if (event.getPlayer().isOp()) {
			Tools.tellPlayer(event.getPlayer(), LangDict.getString("events.preparations.runLandlordConfigCmd"));
		}

		// give player playguide
		givePlayGuide(event.getPlayer());
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event){
		Player leavingPlayer = event.getPlayer();
		event.setQuitMessage(ChatColor.GREEN + "[INFO] " + ChatColor.GOLD + LangDict.getString("joinMessages.citizenLeaveStart") + ChatColor.DARK_AQUA + leavingPlayer.getDisplayName() + ChatColor.GOLD + LangDict.getString("joinMessages.citizenLeaveEnd"));

		// update status of player
		PlayerDataManager.updatePlayerStatus(leavingPlayer, LangDict.getString("playerStatus.offline"));

		// Player is in jail
		if (PlayerEventManager.getEventForPlayer(leavingPlayer) instanceof JailEvent) {
			debugLog("A player in jail just left!");
			return;
		}

		if (leavingPlayer.getGameMode() == GameMode.SURVIVAL || leavingPlayer.getGameMode() == GameMode.ADVENTURE) {
			if (leavingPlayer.isFlying()) {

				// add slow falling, so player doesn't hurt when flight is ended
				PotionEffect potionEffect = new PotionEffect(PotionEffectType.SLOW_FALLING, (int) Tools.secToTicks(20), 6);
				leavingPlayer.addPotionEffect(potionEffect);
			}
		}

		// end player event
		PlayerEvent playerEvent = PlayerEventManager.getEventForPlayer(leavingPlayer);
		if (playerEvent != null) {
			playerEvent.endEvent();
		}
	}

	private void givePlayGuide(Player player) {
		Book playguide = new Book("Playguide");

		playguide.addPage("&2Welcome to Landlord!&0 \nType '&3/help landlord&0' for a list of commands.");
		playguide.addPage("&2How to play - preparations&0 \nOnce the admin initiates the game, you will have 5 minutes to collect all the items you want to bring into your chunk. The chat will tell you what items you are required to collect.");
		playguide.addPage("Once five minutes have past, you will be teleported into your 16x16 block chunk. At a later level, you will be able to get out of your chunk for 5 minutes at a time, by using the command '&3/wilderness&0'");
		playguide.addPage("&2How to play - Setting home&0 \nInside your chunk, you can perform the command '&3/sethome&0', to change the home location in your chunk. You can then teleport to that location by typing '&3/home&0'");
		playguide.addPage("&2How to play - Leveling&0 \nDo '&3/upgrade info&0' to see what items you need to collect to level up. When you get an item you want to donate to the vault, do '&3/donte&0'. Every time you level up, you will be rewarded a chunk that you can claim.");
		playguide.addPage("&2How to play - Claiming chunks&0 \nTo claim a chunk, do '&3/buychunk&0' while looking towards the chunk you'd like to claim. A new chunk will be unlocked in the preferred direction. The borders will then fall between you and that chunk");
		playguide.addPage("&2How to play - Capturing animals&0 \nAt at later lever, you will unlock the command '&3/capture&0', which captures the closest animal to you.");
		playguide.addPage("&2How to play - Trade&0 \nIf you want to trade with another player, type the command '&3/trade&0' to get to the middle chunk where you can meet. All players are free to build inside that chunk");
		playguide.addPage("&2How to play - Selling items&0 \nThe command '&3/sell now&0' allow you to sell the current item in your hand. Do '&3/sell info&0' to check the item's current value. The value will change over time, so one may wait selling till the item has a greater value.");
		playguide.addPage("&2How to play - Roulette game&0 \nOnce every hour, a new game of Roulette will start. To join the game, type '&3/joinroulette&0' before the time limit. The roulette is not activated before getting to a certain level.");

		Tools.givePlayerItemOrDrop(player, playguide.produceAndGetBook(), true);
	}
}
