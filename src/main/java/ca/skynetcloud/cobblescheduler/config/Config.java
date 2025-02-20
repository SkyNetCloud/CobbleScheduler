package ca.skynetcloud.cobblescheduler.config;

import ca.skynetcloud.cobblescheduler.utils.DateUtils;
import ca.skynetcloud.cobblescheduler.utils.PokemonData;

import java.util.*;

public class Config {

    private List<DateUtils> holidays;
    public long cooldown = 7200000;
    private boolean sendMessagesEnabled = false;
    private long messageCooldown = 7200000;

    public boolean isSendMessagesEnabled() {
        return sendMessagesEnabled;
    }

    public void setSendMessagesEnabled(boolean sendMessagesEnabled) {
        this.sendMessagesEnabled = sendMessagesEnabled;
    }

    public long getMessageCooldown() {
        return messageCooldown;
    }

    public void setMessageCooldown(long messageCooldown) {
        this.messageCooldown = messageCooldown;
    }
    public void setCooldown(long cooldown) {
        this.cooldown = cooldown;
    }

    public List<DateUtils> getHolidays() {
        return holidays != null ? holidays : new ArrayList<>();
    }

    public long getCooldown() {
        return cooldown;
    }

    public void setHolidays(List<DateUtils> holidays) {
        this.holidays = holidays;
    }

    public static Config Configs() {
        Config config = new Config();

        config.setSendMessagesEnabled(false);
        config.setMessageCooldown(7200000);
        config.setCooldown(7200000);

        config.setHolidays(Arrays.asList(
                createNewYearsDay(),
                createMarchBreak(),
                createIndependenceDay(),
                createThanksgiving(),
                createHalloween(),
                createChristmas()
        ));
        return config;
    }

    private static DateUtils createNewYearsDay() {
        DateUtils newYearsDay = new DateUtils();
        newYearsDay.setHoliday("New Year's Day");
        newYearsDay.setStartDate("01-01");
        newYearsDay.setHolidayMessage("Happy New Year! Celebrate with a special Pichu!");

        PokemonData pichu = createPokemon("pichu", 10, 0.6);
        PokemonData clefairy = createPokemon("clefairy", 15, 0.4);

        newYearsDay.setPokemonEntityList(Arrays.asList(pichu, clefairy));
        return newYearsDay;
    }

    private static DateUtils createMarchBreak() {
        DateUtils marchBreak = new DateUtils();
        marchBreak.setHoliday("March Break");
        marchBreak.setStartDate("03-10");
        marchBreak.setEndDate("03-14");
        marchBreak.setHolidayMessage("Enjoy your March Break Pokemon Spawns!");

        PokemonData togekiss = createPokemon("togekiss", 15, 0.7);
        PokemonData leafeon = createPokemon("leafeon", 40, 0.3);

        marchBreak.setPokemonEntityList(Arrays.asList(togekiss, leafeon));
        return marchBreak;
    }

    private static DateUtils createIndependenceDay() {
        DateUtils independenceDay = new DateUtils();
        independenceDay.setHoliday("Independence Day");
        independenceDay.setStartDate("07-04");
        independenceDay.setHolidayMessage("Celebrate freedom with a Pikachu and Charizard!");

        PokemonData pikachu = createPokemon("pikachu", 25, 0.7);
        PokemonData charizard = createPokemon("charizard", 40, 0.3);

        independenceDay.setPokemonEntityList(Arrays.asList(pikachu, charizard));
        return independenceDay;
    }

    private static DateUtils createThanksgiving() {
        DateUtils thanksgiving = new DateUtils();
        thanksgiving.setHoliday("Thanksgiving");
        thanksgiving.setStartDate("11-25");
        thanksgiving.setHolidayMessage("Give thanks with Farfetch'd and Squirtle!");

        PokemonData farfetchd = createPokemon("farfetchd", 20, 0.5);
        PokemonData squirtle = createPokemon("squirtle", 10, 0.5);

        thanksgiving.setPokemonEntityList(Arrays.asList(farfetchd, squirtle));
        return thanksgiving;
    }

    private static DateUtils createHalloween() {
        DateUtils halloween = new DateUtils();
        halloween.setHoliday("Halloween");
        halloween.setStartDate("10-31");
        halloween.setHolidayMessage("Spooky season with Gastly and Pumpkaboo!");

        PokemonData gastly = createPokemon("gastly", 25, 0.6);
        PokemonData pumpkaboo = createPokemon("pumpkaboo", 20, 0.4);

        halloween.setPokemonEntityList(Arrays.asList(gastly, pumpkaboo));
        return halloween;
    }

    private static DateUtils createChristmas() {
        DateUtils christmas = new DateUtils();
        christmas.setHoliday("Christmas");
        christmas.setStartDate("12-25");
        christmas.setHolidayMessage("Merry Christmas with Delibird and Snorlax!");

        PokemonData delibird = createPokemon("delibird", 15, 0.7, "minecraft:plains", "minecraft:forest");
        PokemonData snorlax = createPokemon("snorlax", 35, 0.3, "minecraft:plains", "minecraft:forest");

        christmas.setPokemonEntityList(Arrays.asList(delibird, snorlax));
        return christmas;
    }

    private static PokemonData createPokemon(String name, int level, double spawnRate, String... biomes) {
        PokemonData pokemon = new PokemonData();
        pokemon.setName(name);
        pokemon.setLevel(level);
        pokemon.setSpawn_rate(spawnRate);

        Set<String> allowedBiomes = (biomes.length > 0) ? new HashSet<>(Arrays.asList(biomes)) : Collections.emptySet();
        pokemon.setAllowedBiomes(allowedBiomes);

        return pokemon;
    }
}
