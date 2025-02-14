package com.johansvartdal.landlord.business;

import com.johansvartdal.landlord.*;
import com.johansvartdal.landlord.Properties;
import com.johansvartdal.landlord.chatentities.ErrorChat;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
        if (args.length == 0) {
            Tools.printMenuOption(player, "/business", "insurance create <NAME> <MONTHLY_COST>");
            Tools.printMenuOption(player, "/business", "insurance delete <NAME>");
            Tools.printMenuOption(player, "/business", "insurance list");
            return true;
        }

        if (!args[0].equals("insurance") || args.length == 1) {
            return false;
        }

        switch (args[1]) {
            case "create":
                if (args.length < 4) {
                    return false;
                }
                createInsurance(player, args[2], Integer.parseInt(args[3]));
                return true;
            case "delete":
                if (args.length < 3) {
                    return false;
                }
                deleteInsurance(player, args[2]);
                return true;
            case "list":
                if (args.length != 2) {
                    return false;
                }
                viewInsurances(player);
                return true;
            default:
        }
        return false;
    }

    private void deleteInsurance(Player player, String insuranceName) {
        InsurancePlan insurancePlan = insurancePlans.stream().filter(ip -> ip.getName().equals(insuranceName)).findFirst().orElse(null);
        if (insurancePlan == null) {
            Tools.tellPlayer(new ErrorChat(), player, LangDict.getString("business.insurance.invalidPlan") + insuranceName);
            return;
        }

        insurancePlans.remove(insurancePlan);
        Main.businessManager.saveBusinesses();
        Tools.tellPlayer(getBusinessChatEntity(), player, LangDict.getString("business.insurance.deleteSuccess") + insuranceName);
    }

    @Override
    protected BusinessType getBusinessType() {
        return BusinessType.INSURANCE;
    }

    private void createInsurance(Player player, String name, int monthlyPrice) {
        if (monthlyPrice < 100 || monthlyPrice > 600) {
            Tools.tellPlayer(new ErrorChat(), player, LangDict.getString("business.insurance.invalidPrice"));
            return;
        }

        if (!canAfford(StaticValues.BUSINESS_INSURANCE_NEW_INSURANCE_PRICE)) {
            tellCannotAfford(LangDict.getString("business.insurance.toCreateANewInsurance"), StaticValues.BUSINESS_INSURANCE_NEW_INSURANCE_PRICE);
            return;
        }

        withdrawBank(StaticValues.BUSINESS_INSURANCE_NEW_INSURANCE_PRICE);

        // Calculate max customers based on price using a logarithmic function
        int maxCustomers = (int) (2500 / Math.log(monthlyPrice + 10)); // Adjust scaling factor

        insurancePlans.add(new InsurancePlan(name, monthlyPrice, maxCustomers));
        Tools.tellPlayer(getBusinessChatEntity(), player, LangDict.getString("business.insurance.created") + name + LangDict.getString("business.insurance.andPaid") + Tools.formatCurrency(StaticValues.BUSINESS_INSURANCE_NEW_INSURANCE_PRICE));
    }

    private void viewInsurances(Player player) {
        if (insurancePlans.isEmpty()) {
            Tools.tellPlayer(new ErrorChat(), player, LangDict.getString("business.insurance.noActivePlans"));
            return;
        }

        Tools.printMenuHeader(player, LangDict.getString("business.insurance.activePlansHeader"));
        for (InsurancePlan plan : insurancePlans) {
            Tools.printMenuOption(player, plan.getName(), "Monthly Price: " + Tools.formatCurrency(plan.getMonthlyPrice()) +
            " | " + LangDict.getString("business.insurance.customers") + plan.getCurrentCustomers());
        }
    }

    private void startRevenueScheduler() {
        if (revenueSchedulerTaskId != -1) {
            Bukkit.getScheduler().cancelTask(revenueSchedulerTaskId);
        }

        int interval = (int) Tools.secToTicks(3600);

        revenueSchedulerTaskId = Bukkit.getScheduler().runTaskTimer(this.getPlugin(), () -> {
            int totalRevenue = 0;
            for (InsurancePlan plan : insurancePlans) {
                int revenue = plan.getCurrentCustomers() * plan.getMonthlyPrice();
                depositBank(revenue);
                totalRevenue += revenue;
                plan.increaseCustomers();

                if (revenue > 0) {
                    Player player = Bukkit.getPlayer(UUID.fromString(getOwnerUUID()));
                    if (player != null) {
                        Tools.tellPlayer(getBusinessChatEntity(), player, plan.getName() + LangDict.getString("business.insurance.summary") + Tools.formatCurrency(revenue), ChatColor.GRAY);
                    }
                }
            }
            if (totalRevenue > 0) {
                Main.businessManager.saveBusinesses();
            }
        }, interval, interval).getTaskId(); // Runs every hour
    }

    private void startAccidentScheduler() {
        if (accidentSchedulerTaskId != -1) {
            Bukkit.getScheduler().cancelTask(accidentSchedulerTaskId);
        }

        int interval = (int) Tools.secToTicks(6900); // Runs every 2 hours

        accidentSchedulerTaskId = Bukkit.getScheduler().runTaskTimer(this.getPlugin(), () -> {
            if (insurancePlans.isEmpty()) return;

            InsurancePlan randomPlan = insurancePlans.get(random.nextInt(insurancePlans.size()));
            int payout = (int) (randomPlan.getMonthlyPrice() * (random.nextDouble()*10*10*5 + 500));

            if (getBankAccount() >= payout) {
                withdrawBank(payout);
                Player player = Bukkit.getPlayer(UUID.fromString(getOwnerUUID()));
                if (player != null) {
                    Tools.tellPlayer(getBusinessChatEntity(), player, LangDict.getString("business.insurance.accidentOccurred")
                            + randomPlan.getName() + " | " + LangDict.getString("business.insurance.paidOut") + Tools.formatCurrency(payout), ChatColor.RED);
                }
                Main.businessManager.saveBusinesses();
            } else {
                Main.businessManager.unregisterBusiness(this);
            }

        }, interval, interval).getTaskId();
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

