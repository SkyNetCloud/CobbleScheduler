package ca.skynetcloud.cobblescheduler.utils;

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.Species;

import java.util.List;
import java.util.Set;

public class PokemonData {
    private String name;
    private int level;
    private double spawn_rate;
    private Set<String> allowedBiomes; // Missing field added

    public String getName() {
        return name;
    }

    public Set<String> getAllowedBiomes() {
        return allowedBiomes;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public double getSpawn_rate() {
        return spawn_rate;
    }

    public void setSpawn_rate(double spawn_rate) {
        this.spawn_rate = spawn_rate;
    }

    public void setAllowedBiomes(Set<String> allowedBiomes) {
        this.allowedBiomes = allowedBiomes;
    }
}
