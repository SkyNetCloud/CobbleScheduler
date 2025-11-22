package ca.skynetcloud.cobblescheduler.config;

import ca.skynetcloud.cobblescheduler.utils.DateUtils;
import ca.skynetcloud.cobblescheduler.utils.PokemonData;

import java.util.*;

public class Config {
    private static final long DEFAULT_COOLDOWN = 7200000L; // 2 hours in milliseconds

    private List<DateUtils> holidays;
    private long cooldown = DEFAULT_COOLDOWN;
    private boolean sendMessagesEnabled = false;
    private long messageCooldown = DEFAULT_COOLDOWN;

    // Getters and Setters
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

    public long getCooldown() {
        return cooldown;
    }

    public List<DateUtils> getHolidays() {
        return holidays != null ? holidays : new ArrayList<>();
    }

    public void setHolidays(List<DateUtils> holidays) {
        this.holidays = holidays;
    }

    // Factory method for default configuration
    public static Config createDefault() {
        Config config = new Config();

        config.setSendMessagesEnabled(false);
        config.setMessageCooldown(DEFAULT_COOLDOWN);
        config.setCooldown(DEFAULT_COOLDOWN);
        config.setHolidays(createDefaultHolidays());

        return config;
    }

    private static List<DateUtils> createDefaultHolidays() {
        return Arrays.asList(
                createNewYearsDay(),
                createMarchBreak(),
                createIndependenceDay(),
                createThanksgiving(),
                createHalloween(),
                createChristmas()
        );
    }

    private static DateUtils createNewYearsDay() {
        return createHoliday(
                "New Year's Day",
                "01-01",
                null,
                "Happy New Year! Celebrate with a special Pichu!",
                createPokemon("pichu", 10, 0.6),
                createPokemon("clefairy", 15, 0.4)
        );
    }

    private static DateUtils createMarchBreak() {
        return createHoliday(
                "March Break",
                "03-10",
                "03-14",
                "Enjoy your March Break Pokemon Spawns!",
                createPokemon("togekiss", 15, 0.7),
                createPokemon("leafeon", 40, 0.3)
        );
    }

    private static DateUtils createIndependenceDay() {
        return createHoliday(
                "Independence Day",
                "07-04",
                null,
                "Celebrate freedom with a Pikachu and Charizard!",
                createPokemon("pikachu", 25, 0.7),
                createPokemon("charizard", 40, 0.3)
        );
    }

    private static DateUtils createThanksgiving() {
        return createHoliday(
                "Thanksgiving",
                "11-25",
                null,
                "Give thanks with Farfetch'd and Squirtle!",
                createPokemon("farfetchd", 20, 0.5),
                createPokemon("squirtle", 10, 0.5)
        );
    }

    private static DateUtils createHalloween() {
        return createHoliday(
                "Halloween",
                "10-31",
                null,
                "Spooky season with Gastly and Pumpkaboo!",
                createPokemon("gastly", 25, 0.6),
                createPokemon("pumpkaboo", 20, 0.4)
        );
    }

    private static DateUtils createChristmas() {
        return createHoliday(
                "Christmas",
                "12-25",
                null,
                "Merry Christmas with Delibird and Snorlax!",
                createPokemon("delibird", 15, 0.7, "minecraft:plains", "minecraft:forest"),
                createPokemon("snorlax", 35, 0.3, "minecraft:plains", "minecraft:forest")
        );
    }

    private static DateUtils createHoliday(String name, String startDate, String endDate,
                                           String message, PokemonData... pokemonData) {
        DateUtils holiday = new DateUtils();
        holiday.setHoliday(name);
        holiday.setStartDate(startDate);
        holiday.setEndDate(endDate);
        holiday.setHolidayMessage(message);
        holiday.setPokemonEntityList(Arrays.asList(pokemonData));
        return holiday;
    }

    private static PokemonData createPokemon(String name, int level, double spawnRate, String... biomes) {
        PokemonData pokemon = new PokemonData();
        pokemon.setName(name);
        pokemon.setLevel(level);
        pokemon.setSpawnRate(spawnRate);

        Set<String> allowedBiomes = (biomes.length > 0) ?
                new HashSet<>(Arrays.asList(biomes)) :
                Collections.emptySet();
        pokemon.setAllowedBiomes(allowedBiomes);

        return pokemon;
    }
}