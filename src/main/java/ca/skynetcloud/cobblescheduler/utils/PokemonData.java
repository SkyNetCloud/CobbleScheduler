package ca.skynetcloud.cobblescheduler.utils;

public class PokemonData {
    private String name;
    private int level;
    private double spawn_rate;
    private String skin;

    public String getName() {
        return name;
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

    public String getSkin() {
        return skin;
    }

    public void setSkin(String skin) {
        this.skin = skin;
    }
}
