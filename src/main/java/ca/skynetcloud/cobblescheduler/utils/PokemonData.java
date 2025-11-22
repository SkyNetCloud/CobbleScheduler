package ca.skynetcloud.cobblescheduler.utils;

import java.util.Set;

public class PokemonData {

    private String name;
    private int level;
    private double spawnRate;
    private Set<String> allowedBiomes;


    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public double getSpawnRate() {
        return spawnRate;
    }

    public Set<String> getAllowedBiomes() {
        return allowedBiomes;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setSpawnRate(double spawnRate) {
        this.spawnRate = spawnRate;
    }

    public void setAllowedBiomes(Set<String> allowedBiomes) {
        this.allowedBiomes = allowedBiomes;
    }

    public double getSpawn_rate() {
        return spawnRate;
    }

    public void setSpawn_rate(double spawn_rate) {
        this.spawnRate = spawn_rate;
    }
}