package com.kickbasebot.data.me;

public class Budget {

    private double projectedBudgetAfterAllActions; // pbaa
    private double projectedBudgetAfterSales; // pbas
    private double currentBudget; // b
    private int budgetStatus; // bs

    public Budget(double projectedBudgetAfterAllActions, double projectedBudgetAfterSales, double currentBudget, int budgetStatus) {
        this.projectedBudgetAfterAllActions = projectedBudgetAfterAllActions;
        this.projectedBudgetAfterSales = projectedBudgetAfterSales;
        this.currentBudget = currentBudget;
        this.budgetStatus = budgetStatus;
    }

    public double getProjectedBudgetAfterAllActions() {
        return projectedBudgetAfterAllActions;
    }

    public double getProjectedBudgetAfterSales() {
        return projectedBudgetAfterSales;
    }

    public double getCurrentBudget() {
        return currentBudget;
    }

    public int getBudgetStatus() {
        return budgetStatus;
    }

    @Override
    public String toString() {
        return "Budget{" +
                "projectedBudgetAfterAllActions=" + projectedBudgetAfterAllActions +
                ", projectedBudgetAfterSales=" + projectedBudgetAfterSales +
                ", currentBudget=" + currentBudget +
                ", budgetStatus=" + budgetStatus +
                '}';
    }
}
