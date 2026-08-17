package com.johansvartdal.landlord.playerevents;

import com.johansvartdal.landlord.*;
import com.johansvartdal.landlord.levels.LevelManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class NetherWildernessEvent extends PlayerEvent{

    public NetherWildernessEvent(Main plugin, Player player) {
        super(plugin, player);
    }

    @Override
    public void start() {
        Location location = prepareSpawnLocation();
        player.teleport(location);

        // update status
        PlayerDataManager.updatePlayerStatus(player, LangDict.getString("playerStatus.inNether"));

        scheduleAutoEnd();
    }

    private Location prepareSpawnLocation() {
        int xz = getXZCordsForSeason();

        Location spawnLocation = new Location(Bukkit.getWorld("world_nether"), xz, 81, xz);
        buildRoomAroundPlayer(spawnLocation);

        return spawnLocation;
    }

    private void buildRoomAroundPlayer(Location spawnLocation) {
        int roomSize = 5;
        int floorLevel = (int) spawnLocation.getY()-1;
        int roofLevel = (int) spawnLocation.getY()+3;
        for (double x = spawnLocation.getX()-roomSize; x < spawnLocation.getX()+roomSize+1; x++) {
            for (int y = floorLevel; y <= roofLevel; y++) {
                for (double z = spawnLocation.getZ()-roomSize; z < spawnLocation.getZ()+roomSize+1; z++) {
                    Location clearBlock = new Location(spawnLocation.getWorld(), x, y, z);

                    if (x == spawnLocation.getX() && z == spawnLocation.getZ()+roomSize && (y == floorLevel + 1 || y == floorLevel + 2)) {
                        clearBlock.getBlock().setType(Material.NETHER_BRICKS);
                        continue;
                    }

                    // make sure walls are not lava
                    if (x == spawnLocation.getX()-roomSize || x == spawnLocation.getX()+roomSize || z == spawnLocation.getZ()-roomSize || z == spawnLocation.getZ()+roomSize) {
                        clearBlock.getBlock().setType(Material.OBSIDIAN);
                        continue;
                    }

                    if (y == floorLevel || y == roofLevel) {
                        clearBlock.getBlock().setType(Material.OBSIDIAN);
                    }else {
                        clearBlock.getBlock().setType(Material.AIR);
                    }
                }
            }
        }
        spawnLocation.getBlock().setType(Material.TORCH);
    }

    private int getXZCordsForSeason() {
        return switch (LevelManager.getCurrentDisplaySeasonNum()) {
            case 1 -> 15000;
            case 2 -> 30000;
            default -> 45000;
        };
    }

    @Override
    public void endEvent() {
        super.endEvent();
        player.teleport(locationBeforeEvent);

        // update status
        PlayerDataManager.updatePlayerStatus(player, LangDict.getString("playerStatus.home"));
    }

    @Override
    public Integer getLengthOfEventInSeconds() {
        if (Properties.DEV_CHEAT_MODE) {
            return 20;
        }

        return 60*6;
    }

    @Override
    public boolean playerTPAwayAllowed() {
        return true;
    }

    @Override
    public void onWarningEventShouldCancel() {
        Tools.performTaskAfterCountdown(() -> {
            Tools.tellPlayer(player, LangDict.getString(LangDict.WELCOME_HOME), ChatColor.GREEN);
            endEvent();
        }, LangDict.getString("playerEvents.wilderness.wildEndingIn"), 60);
    }

    @Override
    public int getExtensionPrice() {
        return LevelManager.getNetherWildernessPrice();
    }

    @Override
    public String getTitle() {
        return LangDict.getString("playerEvents.wilderness.wildernessNether");
    }
}
