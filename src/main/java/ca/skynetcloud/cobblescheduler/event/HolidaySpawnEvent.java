package ca.skynetcloud.cobblescheduler.event;

import ca.skynetcloud.cobblescheduler.CobbleScheduler;
import ca.skynetcloud.cobblescheduler.config.Config;
import ca.skynetcloud.cobblescheduler.utils.DateUtils;
import ca.skynetcloud.cobblescheduler.utils.MessageUtils;
import ca.skynetcloud.cobblescheduler.utils.PokemonData;
import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import kotlin.Unit;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.Objects;
import java.util.Random;
import java.util.Set;

import static ca.skynetcloud.cobblescheduler.CobbleScheduler.config;
import static ca.skynetcloud.cobblescheduler.CobbleScheduler.lastSpawnTime;

public class HolidaySpawnEvent {

    private static long lastMessageTime = 0;
    private static final Random random = new Random();

    public static void SpawnInit() {
        CobblemonEvents.POKEMON_ENTITY_SPAWN.subscribe(Priority.HIGHEST, pokemonEntitySpawnEvent -> {
            var pokemonEntity = pokemonEntitySpawnEvent.getEntity();
            var pokemon = pokemonEntity.getPokemon();

            // Check if this is already a holiday Pokémon FIRST
            if (isSpecial(pokemon)) {
                return Unit.INSTANCE;
            }

            // Check cooldown at the start to avoid unnecessary processing
            if (System.currentTimeMillis() - lastSpawnTime < config.getCooldown()) {
                return Unit.INSTANCE;
            }

            Config config = CobbleScheduler.config;
            //String today = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("MM-dd"));

            for (DateUtils holiday : config.getHolidays()) {
                if (DateUtils.isDateMatch(holiday.getHoliday(), holiday.getStartDate(), holiday.getEndDate())) {
                    for (PokemonData pokemonData : holiday.getPokemonEntityList()) {
                        if (random.nextInt(100) < (pokemonData.getSpawn_rate() * 50)) {
                            if (spawnHolidayPokemon(pokemonData, pokemonEntity)) {
                                // CRITICAL: Cancel the original spawn
                                pokemonEntitySpawnEvent.cancel();
                                lastSpawnTime = System.currentTimeMillis();

                                // Send message if enabled
                                if (config.isSendMessagesEnabled() && System.currentTimeMillis() - lastMessageTime > config.getMessageCooldown()) {
                                    for (ServerPlayerEntity player : Objects.requireNonNull(pokemonEntity.getServer()).getPlayerManager().getPlayerList()) {
                                        MessageUtils.sendMessage(player, config);
                                    }
                                    lastMessageTime = System.currentTimeMillis();
                                }
                            }
                            return Unit.INSTANCE;
                        }
                    }
                }
            }
            return Unit.INSTANCE;
        });
    }

    public static boolean isSpecial(Pokemon pokemon) {
        String speciesName = pokemon.getSpecies().getName();
        for (DateUtils holiday : config.getHolidays()) {
            for (PokemonData specialPokemon : holiday.getPokemonEntityList()) {
                if (specialPokemon.getName().equalsIgnoreCase(speciesName)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean spawnHolidayPokemon(PokemonData pokemonData, PokemonEntity pokemonEntity) {
        ServerPlayerEntity closestPlayer = Objects.requireNonNull(pokemonEntity.getServer()).getOverworld().getRandomAlivePlayer();
        if (closestPlayer == null) {
            // LOGGER.debug("No players found nearby for holiday spawn.");
            return false;
        }

        World world = pokemonEntity.getServer().getOverworld();
        BlockPos playerPos = closestPlayer.getBlockPos();

        Set<String> allowedBiomes = pokemonData.getAllowedBiomes();
        BlockPos spawnPos = findValidSpawnPosition(playerPos, world, allowedBiomes);

        if (spawnPos == null) {
            // LOGGER.debug("No valid spawn position found for {}", pokemonData.getName());
            return false;
        }

        try {
            PokemonProperties properties = PokemonProperties.Companion.parse(pokemonData.getName());
            PokemonEntity holidayPokemon = properties.createEntity(world);

            holidayPokemon.getPokemon().setLevel(pokemonData.getLevel());
            holidayPokemon.setPos(spawnPos.getX(), spawnPos.getY() + 1, spawnPos.getZ());

            world.spawnEntity(holidayPokemon);
            // LOGGER.info("Spawned holiday Pokémon: {} at {}, {}, {}", pokemonData.getName(), spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());

            return true;

        } catch (Exception e) {
            // LOGGER.error("Error spawning holiday Pokémon: {}", pokemonData.getName(), e);
            return false;
        }
    }

    private static BlockPos findValidSpawnPosition(BlockPos playerPos, World world, Set<String> allowedBiomes) {
        // If no biomes specified, use simple random position
        if (allowedBiomes == null || allowedBiomes.isEmpty()) {
            return getRandomNearbyPosition(playerPos);
        }

        // Check a few positions for valid biomes
        for (int i = 0; i < 5; i++) {
            BlockPos potentialPos = getRandomNearbyPosition(playerPos);
            Biome biome = world.getBiome(potentialPos).value();

            if (allowedBiomes.contains(biome.toString())) {
                return potentialPos;
            }
        }

        return null;
    }

    private static BlockPos getRandomNearbyPosition(BlockPos playerPos) {
        Random random = new Random();
        int distance = random.nextInt(11); // 0 to 10 blocks
        Direction direction = Direction.fromHorizontal(random.nextInt(4)); // N, E, S, W

        return playerPos.offset(direction, distance);
    }
}