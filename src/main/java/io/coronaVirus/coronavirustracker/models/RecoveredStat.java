package io.coronaVirus.coronavirustracker.models;

public class RecoveredStat {
    private String state;
    private int recoveredCases;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getRecoveredCases() {
        return recoveredCases;
    }

    public void setRecoveredCases(int recoveredCases) {
        this.recoveredCases = recoveredCases;
    }
}
