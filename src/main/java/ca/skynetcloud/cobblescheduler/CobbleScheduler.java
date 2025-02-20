package ca.skynetcloud.cobblescheduler;


import ca.skynetcloud.cobblescheduler.config.Config;
import ca.skynetcloud.cobblescheduler.utils.DateUtils;
import ca.skynetcloud.cobblescheduler.utils.PokemonData;
import com.cobblemon.mod.common.CobblemonEntities;
import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import kotlin.Unit;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class CobbleScheduler implements ModInitializer {

    public static Logger logger = LoggerFactory.getLogger("CobbleScheduler");


    private static final String NAME = "CobbleScheduler";
    private static final String VERSION = "";
    private static final File CONFIG_FILE = new File("config/CobbleHolidays/holidays.json");
    private static Config config;
    private static final Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
    private long lastSpawnTime = 0;
    private final long SPAWN_COOLDOWN = config.SpawnCoolDown;

    @Override
    public void onInitialize() {
        loadConfig();

        CobblemonEvents.POKEMON_ENTITY_SPAWN.subscribe(Priority.HIGHEST, pokemonEntitySpawnEvent -> {
            var pokemonEntity = pokemonEntitySpawnEvent.getEntity();
            var pokemon = pokemonEntity.getPokemon();

            if (isSpecial(pokemon)) {
                return Unit.INSTANCE;  // If it's special, don't spawn holiday Pokémon
            }



            String today = getTodayDate();
            Config config = CobbleScheduler.config;
            for (DateUtils holiday : config.getHolidays()) {
                if (holiday.getDate().equals(today)) {
                    Random random = new Random();
                    // Check for each Pokémon associated with the holiday
                    for (PokemonData pokemonData : holiday.getPokemonEntityList()) {
                        // Compare random number with spawn rate
                        if (System.currentTimeMillis() - lastSpawnTime > SPAWN_COOLDOWN) {
                            if (random.nextInt(100) < (pokemonData.getSpawn_rate() * 100)) {
                                // Spawn the holiday Pokémon
                                spawnHolidayPokemon(pokemonData, pokemonEntity);
                                pokemonEntitySpawnEvent.cancel(); // Cancel the normal Pokémon spawn
                                return Unit.INSTANCE;
                            }
                        }
                    }
                }
            }


            return Unit.INSTANCE;
        });

    }

    private String getTodayDate() {
        return java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("MM-dd"));
    }

    public boolean isSpecial(Pokemon pokemon) {
        for (DateUtils holiday : CobbleScheduler.config.getHolidays()) {
            for (PokemonData specialPokemon : holiday.getPokemonEntityList()) {
                if (specialPokemon.getName().equalsIgnoreCase(pokemon.getDisplayName().toString())) {
                    return true;
                }
            }
        }
        return false;
    }


    private void spawnHolidayPokemon(PokemonData pokemonData, PokemonEntity pokemonEntity) {
        Entity closestPlayer = pokemonEntity.getServer().overworld().getRandomPlayer();
        if (closestPlayer == null) {
            System.out.println("No players found nearby.");
            return;
        }

        BlockPos playerPos = closestPlayer.getOnPos();
        BlockPos spawnPos = getRandomNearbyPosition(playerPos);

        // Create the Pokémon using the specified data
        Species species = PokemonSpecies.INSTANCE.getByName(pokemonData.getName().toLowerCase());
        if (species == null) {
            System.out.println("Could not find Pokémon species: " + pokemonData.getName());
            return;
        }

        PokemonEntity holidayPokemon = new PokemonEntity(pokemonEntity.getServer().overworld(), species.create(pokemonData.getLevel()), CobblemonEntities.POKEMON);
        holidayPokemon.setPos(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());


        pokemonEntity.getServer().overworld().addFreshEntity(holidayPokemon);
        System.out.println("Spawned holiday Pokémon: " + pokemonData.getName());
    }

    private BlockPos getRandomNearbyPosition(BlockPos playerPos) {
        Random random = new Random();
        int offsetX = random.nextInt(10) - 5;
        int offsetZ = random.nextInt(10) - 5;

        return playerPos.offset(offsetX, 0, offsetZ);
    }


    public static void loadConfig() {
        CONFIG_FILE.getParentFile().mkdirs(); // Ensure directory exists

        if (CONFIG_FILE.exists()) {
            logger.info(NAME + ": Config file exists. Loading it.");
            try (FileReader fileReader = new FileReader(CONFIG_FILE)) {
                config = gson.fromJson(fileReader, Config.class);

                if (config == null) {
                    logger.warn("Loaded config is null. Resetting to default config.");
                    config = Config.Configs(); // Fallback to default
                }
            } catch (JsonSyntaxException e) {
                logger.warn("Error reading config file. Using default config.");
                e.printStackTrace();
                config = Config.Configs();  // Fallback to default config
            } catch (IOException e) {
                logger.warn("Error reading config file.");
                e.printStackTrace();
                config = Config.Configs(); // Fallback to default config
            }
        } else {
            logger.info(NAME + ": Config file not found. Creating a new one with default settings.");
            config = Config.Configs(); // Create a new default config
        }

        saveConfig(); // Save the loaded or default config
    }

    public static void saveConfig() {
        try (FileWriter fileWriter = new FileWriter(CONFIG_FILE)) {
            gson.toJson(config, fileWriter);
            fileWriter.flush();
            logger.info(NAME + ": Config saved successfully.");
        } catch (IOException e) {
            logger.warn("Failed to save the config!");
            e.printStackTrace();
        }
    }

    public static void reloadConfig() {
        logger.info("Reloading config");
        loadConfig();
    }
}