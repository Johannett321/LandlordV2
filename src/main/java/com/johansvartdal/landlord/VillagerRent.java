package com.johansvartdal.landlord;

import com.johansvartdal.landlord.levels.LevelManager;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

@RequiredArgsConstructor
public class VillagerRent {

    private final Main plugin;

    public void startScheduler() {
        long intervalTime = Tools.secToTicks(60 * 60 * 3); // 3 hours

        if (Properties.DEV_CHEAT_MODE) {
            intervalTime = Tools.secToTicks(30);
        }

        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (Properties.DEBUG_LOGGING) {
                System.out.println("Collecting rent rom villagers");
            }

            // make sure feature is unlocked
            if (!LevelManager.featureUnlocked("villager_rent")) {
                return;
            }

            // loop over playerData, for each: loop over their chunks
            for (Player player : Bukkit.getOnlinePlayers()) {
                collectRent(player);
            }
        }, intervalTime, intervalTime);
    }

    private void collectRent(Player player) {
        if (!Main.playerDataManager.playerExists(player)) {
            return;
        }

        int collectedRentForPlayer = 0;
        int rentCost = 0;

        PlayerData playerData = Main.playerDataManager.getPlayerData(player);
        for (int[] chunkCords : playerData.getOwnedChunks()) {
            Chunk chunk = Bukkit.getWorlds().get(0).getChunkAt(chunkCords[0], chunkCords[1]);

            int numberOfVillagersInChunk = getNumberOfVillagersInChunk(chunk);
            int villagerPoints = getVillagerPoints(chunk);

            if (numberOfVillagersInChunk <= 0) {
                continue;
            }

            collectedRentForPlayer += villagerPoints * StaticValues.VILLAGER_RENT_AMOUNT_PER_LEVEL;
            rentCost += numberOfVillagersInChunk * StaticValues.VILLAGER_COST_AMOUNT;
        }

        int profit = collectedRentForPlayer - rentCost;

        if (collectedRentForPlayer > 0) {
            Tools.tellPlayer(player, LangDict.getString("villagerRent.preMessage") + Tools.formatCurrency(collectedRentForPlayer) + LangDict.getString("villagerRent.midMessage1") + Tools.formatCurrency(rentCost) + LangDict.getString("villagerRent.midMessage2") + Tools.formatCurrency(profit), ChatColor.GRAY);
            Bank.depositPlayer(player, profit);
        } else if (collectedRentForPlayer < 0) {
            Tools.tellPlayer(player, LangDict.getString("villagerRent.preMessage") + Tools.formatCurrency(collectedRentForPlayer) + LangDict.getString("villagerRent.midMessage1") + Tools.formatCurrency(rentCost) + LangDict.getString("villagerRent.midMessage2") + Tools.formatCurrency(profit), ChatColor.RED);

            // send to jail if cannot afford
            if (!Bank.playerCanAfford(player, profit)) {
                JailManager.sendToJail(plugin, player, LangDict.getString("playerEvents.jail.jailReasonRent"), 60*5);
                Bank.bankruptPlayer(player);
                return;
            }

            // withdraw player
            Bank.withdrawPlayer("villager expenses", player, profit);
        }
    }

    public int getVillagerPoints(Chunk chunk) {
        int villagerPoints = 0;
        for (Entity entity : chunk.getEntities()) {
            if (entity instanceof Villager villager) {
                villagerPoints += villager.getVillagerLevel();
            }
        }
        return villagerPoints;
    }

    public int getNumberOfVillagersInChunk(Chunk chunk) {
        int numberOfVillagers = 0;
        for (Entity entity : chunk.getEntities()) {
            if (entity instanceof Villager) {
                numberOfVillagers++;
            }
        }
        return numberOfVillagers;
    }

}
