package com.johansvartdal.landlord.business;

import com.johansvartdal.landlord.LangDict;
import com.johansvartdal.landlord.Main;
import com.johansvartdal.landlord.Tools;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import java.util.*;

@Getter
public class InsuranceBusiness extends Business {

    private final List<InsurancePlan> insurancePlans = new ArrayList<>();
    private final Random random = new Random();
    private int revenueSchedulerTaskId = -1;
    private int accidentSchedulerTaskId = -1;

    public InsuranceBusiness(Main plugin, Player player, String name) {
        super(plugin, player, name);
    }

    public InsuranceBusiness(Main plugin, JSONObject businessJson) {
        super(plugin, businessJson);
        if (businessJson.containsKey("insurancePlans")) {
            List<?> plansJson = (List<?>) businessJson.get("insurancePlans");
            for (Object obj : plansJson) {
                insurancePlans.add(new InsurancePlan((JSONObject) obj));
            }
        }
        startRevenueScheduler();
        startAccidentScheduler();
    }

    @Override
    protected int getRegistrationFee() {
        return 250000; // Medium registration fee for insurance businesses
    }

    @Override
    protected BusinessType getType() {
        return BusinessType.INSURANCE;
    }

    @Override
    protected void onBusinessRegistered() {
        startRevenueScheduler();
        startAccidentScheduler();
    }

    @Override
    protected void onBusinessUnregistered() {
        stopRevenueScheduler();
        stopAccidentScheduler();
    }

    @Override
    protected boolean handleCommand(Player player, String[] args) {
        if (args.length < 1) {
            Tools.tellPlayer(player, LangDict.getString("insurance.usage"));
            return false;
        }

        switch (args[0]) {
            case "create":
                if (args.length < 3) {
                    Tools.tellPlayer(player, LangDict.getString("insurance.createUsage"));
                    return false;
                }
                createInsurance(player, args[1], Integer.parseInt(args[2]));
                break;
            case "view":
                viewInsurances(player);
                break;
            default:
                Tools.tellPlayer(player, LangDict.getString("insurance.invalidCommand"));
        }
        return false;
    }

    private void createInsurance(Player player, String name, int monthlyPrice) {
        if (monthlyPrice < 50 || monthlyPrice > 10000) {
            Tools.tellPlayer(player, LangDict.getString("insurance.invalidPrice"));
            return;
        }

        // Calculate max customers based on price using a logarithmic function
        int maxCustomers = (int) (5000 / Math.log(monthlyPrice + 10)); // Adjust scaling factor

        insurancePlans.add(new InsurancePlan(name, monthlyPrice, maxCustomers));
        Tools.tellPlayer(player, LangDict.getString("insurance.created") + name);
    }

    private void viewInsurances(Player player) {
        if (insurancePlans.isEmpty()) {
            Tools.tellPlayer(player, LangDict.getString("insurance.noActivePlans"));
            return;
        }

        Tools.tellPlayer(player, LangDict.getString("insurance.activePlansHeader"));
        for (InsurancePlan plan : insurancePlans) {
            Tools.tellPlayer(player, plan.getName() +
                    " | Monthly Price: " + Tools.formatCurrency(plan.getMonthlyPrice()) +
                    " | Customers: " + plan.getCurrentCustomers() + "/" + plan.getMaxCustomers());
        }
    }

    private void startRevenueScheduler() {
        if (revenueSchedulerTaskId != -1) {
            Bukkit.getScheduler().cancelTask(revenueSchedulerTaskId);
        }

        revenueSchedulerTaskId = Bukkit.getScheduler().runTaskTimer(this.getPlugin(), () -> {
            for (InsurancePlan plan : insurancePlans) {
                int revenue = plan.getCurrentCustomers() * plan.getMonthlyPrice();
                depositBank(revenue);
                plan.increaseCustomers();
            }
        }, Tools.secToTicks(3600), Tools.secToTicks(3600)).getTaskId(); // Runs every hour
    }

    private void startAccidentScheduler() {
        if (accidentSchedulerTaskId != -1) {
            Bukkit.getScheduler().cancelTask(accidentSchedulerTaskId);
        }

        accidentSchedulerTaskId = Bukkit.getScheduler().runTaskTimer(this.getPlugin(), () -> {
            if (insurancePlans.isEmpty()) return;

            InsurancePlan randomPlan = insurancePlans.get(random.nextInt(insurancePlans.size()));
            int payout = (int) (randomPlan.getMonthlyPrice() * (random.nextDouble() * 10 + 5)); // 5x to 15x payout

            if (getBankAccount() >= payout) {
                withdrawBank(payout);
                Tools.tellPlayer(Bukkit.getPlayer(UUID.fromString(getOwnerUUID())), LangDict.getString("insurance.accidentOccurred")
                        + randomPlan.getName() + " | Paid Out: " + Tools.formatCurrency(payout));
            } else {
                Tools.tellPlayer(Bukkit.getPlayer(UUID.fromString(getOwnerUUID())), LangDict.getString("insurance.bankruptWarning"));
            }

        }, Tools.secToTicks(7200), Tools.secToTicks(7200)).getTaskId(); // Runs every 2 hours
    }

    private void stopRevenueScheduler() {
        if (revenueSchedulerTaskId != -1) {
            Bukkit.getScheduler().cancelTask(revenueSchedulerTaskId);
            revenueSchedulerTaskId = -1;
        }
    }

    private void stopAccidentScheduler() {
        if (accidentSchedulerTaskId != -1) {
            Bukkit.getScheduler().cancelTask(accidentSchedulerTaskId);
            accidentSchedulerTaskId = -1;
        }
    }

    @Override
    public JSONObject getJson() {
        JSONObject json = super.getJson();
        List<JSONObject> plansJson = new ArrayList<>();
        for (InsurancePlan plan : insurancePlans) {
            plansJson.add(plan.toJson());
        }
        json.put("insurancePlans", plansJson);
        return json;
    }

    @Getter
    private static class InsurancePlan {
        private final String name;
        private final int monthlyPrice;
        private final int maxCustomers;
        private int currentCustomers = 0;

        public InsurancePlan(String name, int monthlyPrice, int maxCustomers) {
            this.name = name;
            this.monthlyPrice = monthlyPrice;
            this.maxCustomers = maxCustomers;
        }

        public InsurancePlan(JSONObject json) {
            this.name = json.get("name").toString();
            this.monthlyPrice = ((Long) json.get("monthlyPrice")).intValue();
            this.maxCustomers = ((Long) json.get("maxCustomers")).intValue();
            this.currentCustomers = ((Long) json.get("currentCustomers")).intValue();
        }

        public void increaseCustomers() {
            if (currentCustomers < maxCustomers) {
                currentCustomers += Math.max(1, (maxCustomers - currentCustomers) / 10);
            }
        }

        public JSONObject toJson() {
            JSONObject json = new JSONObject();
            json.put("name", name);
            json.put("monthlyPrice", monthlyPrice);
            json.put("maxCustomers", maxCustomers);
            json.put("currentCustomers", currentCustomers);
            return json;
        }
    }
}

