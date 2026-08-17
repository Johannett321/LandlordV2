package com.johansvartdal.landlord;

import com.johansvartdal.landlord.business.BusinessManager;
import com.johansvartdal.landlord.chatentities.InfoChat;
import com.johansvartdal.landlord.chatentities.WarningChat;
import com.johansvartdal.landlord.commands.*;
import com.johansvartdal.landlord.events.LandlordEventManager;
import com.johansvartdal.landlord.lan.LanController;
import com.johansvartdal.landlord.levels.LevelManager;
import com.johansvartdal.landlord.playerevents.JailEvent;
import com.johansvartdal.landlord.playerevents.PlayerEvent;
import com.johansvartdal.landlord.playerevents.PlayerEventManager;
import com.johansvartdal.landlord.renting.RentManager;
import com.johansvartdal.landlord.webserver.WebServerManager;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import static com.johansvartdal.landlord.Tools.debugLog;
import static com.johansvartdal.landlord.Tools.plugin;

@Slf4j
public class Main extends JavaPlugin implements Listener {

	public static BusinessManager businessManager;
	public static ScoreboardHelper scoreboardHelper;
	public static Properties properties;
	public static Configurator configurator;
	public static TradeCenter tradeCenter;
	public static PlayerDataManager playerDataManager;
	public static WebServerManager webServerManager;
	public static ChunkGuardManager chunkGuardManager;
	public static ChestManager chestManager;
	private static Landlord landlord;

	/** How long to wait for the lladv world to be loaded before giving up. */
	private static final int LLADV_WAIT_SECONDS = 120;

	@Override
	public void onEnable() {
		debugLog("Landlord is loading!");
		configurator = new Configurator(this);
		configurator.configure();
		Tools.init(this);
		CustomConfig.load();

		// setup economy
		Bank.setupEconomy();

		SpecialEffects.setPlugin(this);

		// initialize managers
		properties = new Properties();
		properties.load();
		new CheatProtection(this);
		new SleepPercentage(this);
		new AFKDetector(this);
		LangDict.loadLanguage();
		Bank.load();
		StockManager.loadStocks();
		ChunkBuilder.load();
		LevelManager.init(this);
		new EmissionTax(this);
		RentManager.registerListeners(this);
		new RandomHint(this);
		new RouletteGame(this);


		tradeCenter = new TradeCenter();
		playerDataManager = new PlayerDataManager(Bukkit.getWorlds().get(0), this);
		playerDataManager.loadData();
		scoreboardHelper = new ScoreboardHelper(this);

		// COMMANDS
		new Sell(this);
		landlord = new Landlord(this);
		new BuyChunk(this);
		new Day(this);
		new Night(this);
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
		new Pay(this);
		new ChunkGuard(this);
		new BusinessCommand(this);
		new Millionaire(this);
		new Cabin(this);
		new Shop(this);

		new NoEndermen(this).startScheduler();

		new VillagerRent(this).startScheduler();

		chunkGuardManager = new ChunkGuardManager(this);
		businessManager = new BusinessManager(this);
		chestManager = new ChestManager(this);

		SpectatorManager.lockSpectatorsInSpectatorMode(this);

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

		waitForLladvWorld();
	}

	/**
	 * Landlord cannot run without the hand-built `lladv` world, so give up and stop the
	 * server if it never turns up.
	 * <p>
	 * This polls instead of checking once. Multiverse only imports a world the first time
	 * after the server is fully started, which on a cold boot happens well over ten seconds
	 * after we enable — a single early check would mistake a slow import for a missing
	 * world and kill the server on the user's very first run.
	 */
	private void waitForLladvWorld() {
		new BukkitRunnable() {
			private int secondsWaited = 0;

			@Override
			public void run() {
				World lladv = Bukkit.getWorld("lladv");
				if (lladv != null) {
					cancel();
					return;
				}

				if (++secondsWaited < LLADV_WAIT_SECONDS) {
					return;
				}

				cancel();
				log.error("------------------------------------------------------------------");
				log.error("[LANDLORD] ERROR: COULD NOT FIND LLADV WORLD. SHUTTING DOWN SERVER");
				log.error("[LANDLORD] Put the world in your server folder and load it, e.g.");
				log.error("[LANDLORD]   /mv import lladv normal");
				log.error("------------------------------------------------------------------");
				Bukkit.shutdown();
			}
		}.runTaskTimer(this, Tools.secToTicks(10), Tools.secToTicks(1));
	}

	@Override
	public void onDisable() {
		PlayerEventManager.forceEndAllEvents();
		RentManager.forceEndAllRents();

		// May be null if onEnable failed before the web server was started. Guarding here
		// keeps the original startup error visible instead of burying it under an NPE.
		if (webServerManager != null) {
			webServerManager.stopServer();
		}
	}

	@EventHandler
	public void onPlayerChat(PlayerChatEvent event) {
		// Get the player
		Player chattingPlayer = event.getPlayer();
		PlayerData chattingPlayerData = playerDataManager.getPlayerData(chattingPlayer);

		// Theme the players message
		if (!Main.playerDataManager.playerExists(chattingPlayer)) {
			event.setCancelled(true);
			for(Player onlinePlayer: Bukkit.getOnlinePlayers()) {
				onlinePlayer.sendMessage(ChatColor.AQUA + "[SPECTATOR]" + ChatColor.WHITE + " <" + chattingPlayer.getDisplayName() + "> " + event.getMessage());
			}
		}else if (Bank.playerIsTreasuryChancellor(chattingPlayer)) { // treasury chancellor
			event.setCancelled(true);
			for(Player onlinePlayer: Bukkit.getOnlinePlayers()) {
				onlinePlayer.sendMessage(ChatColor.DARK_GREEN + "[" + LangDict.getString(LangDict.TREASURY_SENTINEL) + "] " + ChatColor.WHITE + event.getMessage());
			}
		}else if (chattingPlayer.getDisplayName().equalsIgnoreCase("johannett321")) { // Creator
			event.setCancelled(true);
			for(Player onlinePlayer: Bukkit.getOnlinePlayers()) {
				if (Properties.DEV_CHEAT_MODE) {
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
		}else if (chattingPlayer.getDisplayName().equalsIgnoreCase("southless")) { // Builder
			event.setCancelled(true);
			for(Player onlinePlayer: Bukkit.getOnlinePlayers()) {
				onlinePlayer.sendMessage(ChatColor.GREEN + "[BUILDER]" + ChatColor.WHITE + " <Southless> " + event.getMessage());
			}
		}else if (chattingPlayer.getDisplayName().equalsIgnoreCase("jorgenmyr")) { // Builder
			event.setCancelled(true);
			for(Player onlinePlayer: Bukkit.getOnlinePlayers()) {
				onlinePlayer.sendMessage(ChatColor.LIGHT_PURPLE + "[OG Pioneer]" + ChatColor.WHITE + " <jorgenmyr> " + event.getMessage());
			}
		}else if (chattingPlayer.getDisplayName().equalsIgnoreCase("shadyyduck")) { // Builder
			event.setCancelled(true);
			for(Player onlinePlayer: Bukkit.getOnlinePlayers()) {
				onlinePlayer.sendMessage(ChatColor.LIGHT_PURPLE + "[OG Pioneer]" + ChatColor.WHITE + " <ShadyyDuck> " + event.getMessage());
			}
		}else if (Bank.isMillionaire(chattingPlayer)) {
			event.setCancelled(true);
			for(Player onlinePlayer: Bukkit.getOnlinePlayers()) {
				onlinePlayer.sendMessage(ChatColor.GOLD + "[MILLIONAIRE]" + ChatColor.WHITE + " <" + chattingPlayer.getDisplayName() + "> " + event.getMessage());
			}
		}else if (Bank.isHighEnd(chattingPlayer)) {
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
		if (!Properties.DEV_CHEAT_MODE) {
			Tools.tellPlayer(new InfoChat(), joinedPlayer, LangDict.getString("info.alphaBuild"), ChatColor.RED);
		}

		// inform player about debug mode
		if (Properties.DEV_CHEAT_MODE) {
			Tools.tellPlayer(new WarningChat(), joinedPlayer, LangDict.getString("info.devCheatWarning"), ChatColor.RED);
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
		if (properties.gameHasStarted() && !landlord.playerHasBeenAllowed(joinedPlayer)) {
			Bukkit.getScheduler().runTaskLater(plugin, () -> {
				joinedPlayer.setGameMode(GameMode.SPECTATOR);
				Tools.tellPlayer(joinedPlayer, "The game is full, but you can still spectate :)");
			}, Tools.secToTicks(1));
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

		if (!properties.gameHasStarted()) {
			// teleport to start location
			Bukkit.getScheduler().runTaskLater(this, ()-> {
				event.getPlayer().teleport(StaticValues.GAME_START_LOCATION);
			}, Tools.secToTicks(5));

			// inform OP about commands
			if (event.getPlayer().isOp()) {
				Tools.tellPlayer(event.getPlayer(), LangDict.getString("events.preparations.runLandlordConfigCmd"));
			}
		}

		// give player playguide
		givePlayGuide(event.getPlayer());

		// if the game has started and it is a new player, create it's chunk and things...
		if (properties.gameHasStarted()) {
			Bukkit.getScheduler().runTaskLater(this, ()-> {
				Tools.tellPlayer(new WarningChat(), joinedPlayer, "Please stand by!");
				new GameJustStarted(this, Bukkit.getWorlds().get(0)).setupForPlayer(joinedPlayer, properties.getNumberOfPlayers(), properties.getNumberOfPlayers() + 1);
			}, Tools.secToTicks(3));
		}
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event){
		Player leavingPlayer = event.getPlayer();
		if (!Main.playerDataManager.playerExists(leavingPlayer)) {
			return; // don't do anything with spectators
		}

		event.setQuitMessage(ChatColor.GREEN + "[INFO] " + ChatColor.GOLD + LangDict.getString("joinMessages.citizenLeaveStart") + ChatColor.DARK_AQUA + leavingPlayer.getDisplayName() + ChatColor.GOLD + LangDict.getString("joinMessages.citizenLeaveEnd"));

		// update status of player
		PlayerDataManager.updatePlayerStatus(leavingPlayer, LangDict.getString("playerStatus.offline"));

		// Player is in jail
		if (PlayerEventManager.getEventForPlayer(leavingPlayer) instanceof JailEvent) {
			debugLog("A player in jail just left!");
			return;
		}

		/* ---------- EVERYTHING BELOW DOES NOT RUN IF PLAYER IS IN JAIL ---------- */

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

		playguide.addPage(LangDict.getString("playguide.page1"));
		playguide.addPage(LangDict.getString("playguide.page2"));
		playguide.addPage(LangDict.getString("playguide.page3"));
		playguide.addPage(LangDict.getString("playguide.page4"));
		playguide.addPage(LangDict.getString("playguide.page5"));
		playguide.addPage(LangDict.getString("playguide.page6"));
		playguide.addPage(LangDict.getString("playguide.page7"));
		playguide.addPage(LangDict.getString("playguide.page8"));
		playguide.addPage(LangDict.getString("playguide.page9"));
		playguide.addPage(LangDict.getString("playguide.page10"));
		playguide.addPage(LangDict.getString("playguide.page11"));
		playguide.addPage(LangDict.getString("levelBooks.endSignature"));

		Tools.givePlayerItemOrDrop(player, playguide.produceAndGetBook(), true);
	}
}
