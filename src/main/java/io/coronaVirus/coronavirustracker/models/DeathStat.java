package io.coronaVirus.coronavirustracker.models;

public class DeathStat {
    private String state;
    private int deathCases;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getDeathCases() {
        return deathCases;
    }

    public void setDeathCases(int deathCases) {
        this.deathCases = deathCases;
    }
}
