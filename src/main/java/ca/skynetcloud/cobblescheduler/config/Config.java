package ca.skynetcloud.cobblescheduler.config;

import ca.skynetcloud.cobblescheduler.utils.DateUtils;
import ca.skynetcloud.cobblescheduler.utils.PokemonData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Config {

    public long SpawnCoolDown = 1500000;  // default cooldown time (in milliseconds)
    private List<DateUtils> holidays;

    public List<DateUtils> getHolidays() {
        return holidays != null ? holidays : new ArrayList<>();
    }

    public void setHolidays(List<DateUtils> holidays) {
        this.holidays = holidays;
    }

    public long getSpawnCoolDown() {
        return SpawnCoolDown;
    }

    public void setSpawnCoolDown(long spawnCoolDown) {
        this.SpawnCoolDown = spawnCoolDown;
    }

    // Method to create a default config
    public static Config Configs() {

        Config config = new Config();
        DateUtils dateUtils = new DateUtils();
        dateUtils.setHoliday("New Year");
        dateUtils.setDate("01-01");

        PokemonData pokemonData = new PokemonData();
        pokemonData.setName("Ralts");
        pokemonData.setLevel(5);
        pokemonData.setSpawn_rate(0.5);
        pokemonData.setSkin(""); // Empty string for skin or set a default skin

        dateUtils.setPokemonEntityList(Collections.singletonList(pokemonData));
        config.setHolidays(Collections.singletonList(dateUtils));

        // Set the spawn cooldown (for example, 1500000 milliseconds)
        config.setSpawnCoolDown(1500000);

        return config;
    }
}
