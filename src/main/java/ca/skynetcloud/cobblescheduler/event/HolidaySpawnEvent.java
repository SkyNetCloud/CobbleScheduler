package ca.skynetcloud.cobblescheduler.event;

import ca.skynetcloud.cobblescheduler.CobbleScheduler;
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

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

import static ca.skynetcloud.cobblescheduler.CobbleScheduler.config;
import static ca.skynetcloud.cobblescheduler.CobbleScheduler.lastSpawnTime;

public class HolidaySpawnEvent {

    private static long lastMessageTime = 0;
    private static final Random random = new Random();
    private static List<DateUtils> activeHolidays = null;
    private static long lastHolidayCheck = 0;
    private static final long HOLIDAY_CHECK_INTERVAL = 60000; // Check every minute

    public static void SpawnInit() {
        CobblemonEvents.POKEMON_ENTITY_SPAWN.subscribe(Priority.HIGHEST, pokemonEntitySpawnEvent -> {
            var pokemonEntity = pokemonEntitySpawnEvent.getEntity();
            var pokemon = pokemonEntity.getPokemon();

            if (isSpecial(pokemon)) {
                return Unit.INSTANCE;
            }

            if (System.currentTimeMillis() - lastSpawnTime < config.getCooldown()) {
                return Unit.INSTANCE;
            }

            List<DateUtils> holidays = getActiveHolidays();
            if (holidays.isEmpty()) {
                return Unit.INSTANCE;
            }

            if (random.nextDouble() > 0.01) {
                return Unit.INSTANCE;
            }


            for (DateUtils holiday : holidays) {
                for (PokemonData pokemonData : holiday.getPokemonEntityList()) {
                    double spawnChance = pokemonData.getSpawn_rate() / 100.0;
                    if (random.nextDouble() < spawnChance) {
                        if (spawnHolidayPokemon(pokemonData, pokemonEntity)) {
                            pokemonEntitySpawnEvent.cancel();
                            lastSpawnTime = System.currentTimeMillis();

                            if (config.isSendMessagesEnabled() && System.currentTimeMillis() - lastMessageTime > config.getMessageCooldown()) {
                                for (ServerPlayerEntity player : Objects.requireNonNull(pokemonEntity.getServer()).getPlayerManager().getPlayerList()) {
                                    MessageUtils.sendMessage(player, config);
                                }
                                lastMessageTime = System.currentTimeMillis();
                            }
                            return Unit.INSTANCE;
                        }
                    }
                }
            }
            return Unit.INSTANCE;
        });
    }

    private static List<DateUtils> getActiveHolidays() {
        long currentTime = System.currentTimeMillis();
        if (activeHolidays == null || currentTime - lastHolidayCheck > HOLIDAY_CHECK_INTERVAL) {
            activeHolidays = config.getHolidays().stream()
                    .filter(holiday -> DateUtils.isDateMatch(holiday.getHoliday(), holiday.getStartDate(), holiday.getEndDate()))
                    .collect(java.util.stream.Collectors.toList());
            lastHolidayCheck = currentTime;
        }
        return activeHolidays;
    }

    public static boolean isSpecial(Pokemon pokemon) {
        String speciesName = pokemon.getSpecies().getName();
        List<DateUtils> holidays = getActiveHolidays();

        for (DateUtils holiday : holidays) {
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
            return false;
        }

        World world = pokemonEntity.getServer().getOverworld();
        BlockPos playerPos = closestPlayer.getBlockPos();

        Set<String> allowedBiomes = pokemonData.getAllowedBiomes();
        BlockPos spawnPos = findValidSpawnPosition(playerPos, world, allowedBiomes);

        if (spawnPos == null) {
            return false;
        }

        try {
            PokemonProperties properties = PokemonProperties.Companion.parse(pokemonData.getName());
            PokemonEntity holidayPokemon = properties.createEntity(world);

            holidayPokemon.getPokemon().setLevel(pokemonData.getLevel());
            holidayPokemon.setPos(spawnPos.getX(), spawnPos.getY() + 1, spawnPos.getZ());

            world.spawnEntity(holidayPokemon);
            return true;

        } catch (Exception e) {
            CobbleScheduler.LOGGER.error("Error spawning holiday Pokémon: {}", pokemonData.getName(), e);
            return false;
        }
    }

    private static BlockPos findValidSpawnPosition(BlockPos playerPos, World world, Set<String> allowedBiomes) {
        if (allowedBiomes == null || allowedBiomes.isEmpty()) {
            return getRandomNearbyPosition(playerPos);
        }

        for (int i = 0; i < 10; i++) {
            BlockPos potentialPos = getRandomNearbyPosition(playerPos);
            Biome biome = world.getBiome(potentialPos).value();

            if (allowedBiomes.contains(biome.toString())) {
                return potentialPos;
            }
        }

        return null;
    }

    private static BlockPos getRandomNearbyPosition(BlockPos playerPos) {
        int distance = random.nextInt(16) + 8; // 8 to 24 blocks away
        Direction direction = Direction.fromHorizontal(random.nextInt(4));

        return playerPos.offset(direction, distance);
    }
}