package com.johansvartdal.landlord.business;

import com.johansvartdal.landlord.Bank;
import com.johansvartdal.landlord.LangDict;
import com.johansvartdal.landlord.Main;
import com.johansvartdal.landlord.Tools;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import java.util.*;

@Getter
public class BankBusiness extends Business {

    private final List<Loan> activeLoans = new ArrayList<>();
    private final List<LoanApplication> pendingApplications = new ArrayList<>();
    private final Random random = new Random();
    private int loanRequestSchedulerTaskId = -1;
    private int loanRepaymentSchedulerTaskId = -1;

    private static final String[] BUSINESS_NAMES = {
            "SteelCorp", "Quantum Solutions", "Blue Horizon Ventures",
            "Evergreen Industries", "Titan Enterprises", "NovaTech",
            "Skyline Investments", "Omega Holdings", "Solaris Tech"
    };

    public BankBusiness(Main plugin, Player player, String name) {
        super(plugin, player, name);
    }

    public BankBusiness(Main plugin, JSONObject businessJson) {
        super(plugin, businessJson);
        if (businessJson.containsKey("activeLoans")) {
            List<?> loansJson = (List<?>) businessJson.get("activeLoans");
            for (Object obj : loansJson) {
                activeLoans.add(new Loan((JSONObject) obj));
            }
        }
        if (businessJson.containsKey("pendingApplications")) {
            List<?> applicationsJson = (List<?>) businessJson.get("pendingApplications");
            for (Object obj : applicationsJson) {
                pendingApplications.add(new LoanApplication((JSONObject) obj));
            }
        }
        startLoanRequestScheduler();
        startLoanRepaymentScheduler();
    }

    @Override
    protected int getRegistrationFee() {
        return 500000; // Higher fee for banks
    }

    @Override
    protected BusinessType getType() {
        return BusinessType.BANK;
    }

    @Override
    protected void onBusinessRegistered() {
        startLoanRequestScheduler();
        startLoanRepaymentScheduler();
    }

    @Override
    protected void onBusinessUnregistered() {
        stopLoanRequestScheduler();
        stopLoanRepaymentScheduler();
    }

    @Override
    protected boolean handleCommand(Player player, String[] args) {
        if (args.length < 1) {
            Tools.tellPlayer(player, LangDict.getString("bank.usage"));
            return false;
        }

        switch (args[0]) {
            case "viewrequests":
                viewLoanRequests(player);
                break;
            case "approveloan":
                if (args.length < 2) {
                    Tools.tellPlayer(player, LangDict.getString("bank.specifyLoanID"));
                    return false;
                }
                approveLoan(player, Integer.parseInt(args[1]));
                break;
            case "rejectloan":
                if (args.length < 2) {
                    Tools.tellPlayer(player, LangDict.getString("bank.specifyLoanID"));
                    return false;
                }
                rejectLoan(player, Integer.parseInt(args[1]));
                break;
            case "viewloans":
                viewLoans(player);
                break;
            default:
                Tools.tellPlayer(player, LangDict.getString("bank.invalidCommand"));
        }
        return false;
    }

    private void viewLoanRequests(Player player) {
        if (pendingApplications.isEmpty()) {
            Tools.tellPlayer(player, LangDict.getString("bank.noLoanRequests"));
            return;
        }

        Tools.tellPlayer(player, LangDict.getString("bank.loanRequestsHeader"));
        for (int i = 0; i < pendingApplications.size(); i++) {
            LoanApplication app = pendingApplications.get(i);
            Tools.tellPlayer(player, i + ": " + app.getBusinessName() +
                    " | Amount: " + Tools.formatCurrency(app.getAmount()) +
                    " | Interest: " + app.getInterestRate() + "%" +
                    " | Duration: " + app.getDurationHours() + "h");
        }
    }

    private void viewLoans(Player player) {
        if (activeLoans.isEmpty()) {
            Tools.tellPlayer(player, LangDict.getString("bank.noActiveLoans"));
            return;
        }

        Tools.tellPlayer(player, LangDict.getString("bank.activeLoansHeader"));
        for (int i = 0; i < activeLoans.size(); i++) {
            Loan loan = activeLoans.get(i);
            Tools.tellPlayer(player, i + ": " + loan.getBusinessName() +
                    " | Principal: " + Tools.formatCurrency(loan.getPrincipal()) +
                    " | Interest: " + loan.getInterestRate() + "%" +
                    " | Total Due: " + Tools.formatCurrency(loan.getTotalDue()));
        }
    }

    private void approveLoan(Player player, int loanIndex) {
        if (loanIndex < 0 || loanIndex >= pendingApplications.size()) {
            Tools.tellPlayer(player, LangDict.getString("bank.invalidLoanID"));
            return;
        }

        LoanApplication app = pendingApplications.remove(loanIndex);

        if (!Bank.playerCanAfford(player, (int) app.getAmount())) {
            Tools.tellPlayer(player, LangDict.getString("bank.notEnoughFunds"));
            return;
        }

        withdrawBank(app.getAmount());

        activeLoans.add(new Loan(app.getBusinessName(), app.getAmount(), app.getInterestRate()));

        Tools.tellPlayer(player, LangDict.getString("bank.loanApproved") + app.getBusinessName());
    }

    private void rejectLoan(Player player, int loanIndex) {
        if (loanIndex < 0 || loanIndex >= pendingApplications.size()) {
            Tools.tellPlayer(player, LangDict.getString("bank.invalidLoanID"));
            return;
        }

        pendingApplications.remove(loanIndex);
        Tools.tellPlayer(player, LangDict.getString("bank.loanRejected"));
    }

    private void startLoanRequestScheduler() {
        if (loanRequestSchedulerTaskId != -1) {
            Bukkit.getScheduler().cancelTask(loanRequestSchedulerTaskId);
        }

        scheduleNextLoanRequest();
    }

    private void stopLoanRequestScheduler() {
        if (loanRequestSchedulerTaskId != -1) {
            Bukkit.getScheduler().cancelTask(loanRequestSchedulerTaskId);
            loanRequestSchedulerTaskId = -1;
        }
    }

    private void stopLoanRepaymentScheduler() {
        if (loanRepaymentSchedulerTaskId != -1) {
            Bukkit.getScheduler().cancelTask(loanRepaymentSchedulerTaskId);
            loanRepaymentSchedulerTaskId = -1;
        }
    }


    private void scheduleNextLoanRequest() {
        int delayMinutes = random.nextInt(60) + 90; // Between 1.5 to 2.5 hours
        long delayTicks = Tools.secToTicks(60 * delayMinutes);

        loanRequestSchedulerTaskId = Bukkit.getScheduler().runTaskLater(this.getPlugin(), () -> {
            String businessName = BUSINESS_NAMES[random.nextInt(BUSINESS_NAMES.length)];
            int amount = random.nextInt() * 50000 + 50000; // 50,000 - 100,000
            double interestRate = random.nextDouble() * 5 + 5; // 5% - 10%
            int durationHours = 24; // Always 24 hours

            pendingApplications.add(new LoanApplication(businessName, amount, interestRate, durationHours));

            Player owner = Bukkit.getPlayer(UUID.fromString(getOwnerUUID()));
            if (owner != null) {
                Tools.tellPlayer(owner, LangDict.getString("bank.newLoanRequest") + businessName);
            }

            scheduleNextLoanRequest();
        }, delayTicks).getTaskId();
    }

    private void startLoanRepaymentScheduler() {
        if (loanRepaymentSchedulerTaskId != -1) {
            Bukkit.getScheduler().cancelTask(loanRepaymentSchedulerTaskId);
        }

        loanRepaymentSchedulerTaskId = Bukkit.getScheduler().runTaskLater(this.getPlugin(), () -> {
            for (Iterator<Loan> iterator = activeLoans.iterator(); iterator.hasNext(); ) {
                Loan loan = iterator.next();
                double totalDue = loan.getTotalDue();

                if (random.nextDouble() > 0.1) { // 90% chance of repayment
                    depositBank((int) totalDue);
                    Tools.tellPlayer(Bukkit.getPlayer(UUID.fromString(getOwnerUUID())), LangDict.getString("bank.loanRepaid") + loan.getBusinessName());
                } else {
                    Tools.tellPlayer(Bukkit.getPlayer(UUID.fromString(getOwnerUUID())), LangDict.getString("bank.loanDefaulted") + loan.getBusinessName());
                }

                iterator.remove();
            }

            startLoanRepaymentScheduler();
        }, Tools.secToTicks(86400)).getTaskId(); // Runs every 24 real-life hours
    }



    @Getter
    public class Loan {
        private final String businessName;
        private final int principal;
        private final double interestRate;

        public Loan(String businessName, int principal, double interestRate) {
            this.businessName = businessName;
            this.principal = principal;
            this.interestRate = interestRate;
        }

        public Loan(JSONObject json) {
            this.businessName = json.get("businessName").toString();
            this.principal = (int) json.get("principal");
            this.interestRate = (double) json.get("interestRate");
        }

        /**
         * Calculates the total repayment amount.
         */
        public double getTotalDue() {
            return principal + (principal * (interestRate / 100));
        }

        public JSONObject toJson() {
            JSONObject json = new JSONObject();
            json.put("businessName", businessName);
            json.put("principal", principal);
            json.put("interestRate", interestRate);
            return json;
        }
    }

    @Getter
    public class LoanApplication {
        private final String businessName;
        private final int amount;
        private final double interestRate;
        private final int durationHours;

        public LoanApplication(String businessName, int amount, double interestRate, int durationHours) {
            this.businessName = businessName;
            this.amount = amount;
            this.interestRate = interestRate;
            this.durationHours = durationHours;
        }

        public LoanApplication(JSONObject json) {
            this.businessName = json.get("businessName").toString();
            this.amount = (int) json.get("amount");
            this.interestRate = (double) json.get("interestRate");
            this.durationHours = ((Long) json.get("durationHours")).intValue();
        }

        public JSONObject toJson() {
            JSONObject json = new JSONObject();
            json.put("businessName", businessName);
            json.put("amount", amount);
            json.put("interestRate", interestRate);
            json.put("durationHours", durationHours);
            return json;
        }
    }
}

