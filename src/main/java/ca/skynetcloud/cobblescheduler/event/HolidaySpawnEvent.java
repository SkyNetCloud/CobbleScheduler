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
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

import java.util.Objects;
import java.util.Random;
import java.util.Set;

import static ca.skynetcloud.cobblescheduler.CobbleScheduler.config;
import static ca.skynetcloud.cobblescheduler.CobbleScheduler.lastSpawnTime;

public class HolidaySpawnEvent {

    private static long lastMessageTime = 0;

    public static void SpawnInit() {
        CobblemonEvents.POKEMON_ENTITY_SPAWN.subscribe(Priority.HIGHEST, pokemonEntitySpawnEvent -> {
            var pokemonEntity = pokemonEntitySpawnEvent.getEntity();
            var pokemon = pokemonEntity.getPokemon();

            if (isSpecial(pokemon)) {
                return Unit.INSTANCE; // If it's special, don't spawn holiday Pokémon
            }

            Config config = CobbleScheduler.config;
            for (DateUtils holiday : config.getHolidays()) {
                if (DateUtils.isDateMatch(holiday.getHoliday(), holiday.getStartDate(), holiday.getEndDate())) { // Updated check
                    Random random = new Random();
                    for (PokemonData pokemonData : holiday.getPokemonEntityList()) {
                        if (System.currentTimeMillis() - lastSpawnTime > config.getCooldown()) {
                            if (random.nextInt(100) < (pokemonData.getSpawn_rate() * 50)) {
                                spawnHolidayPokemon(pokemonData, pokemonEntity);
                                lastSpawnTime = System.currentTimeMillis();
                                if (config.isSendMessagesEnabled() && System.currentTimeMillis() - lastMessageTime > config.getMessageCooldown()) {
                                    for (ServerPlayer player : Objects.requireNonNull(pokemonEntity.getServer()).getPlayerList().getPlayers()) {
                                        MessageUtils.sendMessage(player, config);
                                    }
                                    lastMessageTime = System.currentTimeMillis(); // Update last message time
                                }

                                return Unit.INSTANCE;
                            }
                        } else {
                            System.out.println("Cooldown active, skipping spawn.");
                        }
                    }
                }
            }
            return Unit.INSTANCE;
        });
    }

    public static boolean isSpecial(Pokemon pokemon) {
        for (DateUtils holiday : config.getHolidays()) {
            for (PokemonData specialPokemon : holiday.getPokemonEntityList()) {
                if (specialPokemon.getName().equals(pokemon.getDisplayName().toString())) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void spawnHolidayPokemon(PokemonData pokemonData, PokemonEntity pokemonEntity) {
        ServerPlayer closestPlayer = Objects.requireNonNull(pokemonEntity.getServer()).overworld().getRandomPlayer();
        if (closestPlayer == null) {
            System.out.println("No players found nearby.");
            return;
        }

        Level world = pokemonEntity.getServer().overworld();
        BlockPos playerPos = closestPlayer.getOnPos();

        Set<String> allowedBiomes = pokemonData.getAllowedBiomes();

        int attempts = 10;
        BlockPos spawnPos = null;

        for (int i = 0; i < attempts; i++) {
            BlockPos potentialPos = getRandomNearbyPosition(playerPos);
            Biome biome = world.getBiome(potentialPos).value();

            // If no biomes are specified, allow spawning anywhere
            if (allowedBiomes == null || allowedBiomes.isEmpty() || allowedBiomes.contains(biome.toString())) {
                spawnPos = potentialPos;
                break;
            }
        }

        if (spawnPos == null) {
            System.out.println("No valid biome found for spawning " + pokemonData.getName());
            return;
        }

        PokemonProperties argPro = PokemonProperties.Companion.parse(pokemonData.getName());

        BlockPos pos = spawnPos;
        while (pos.getY() > 0 && world.isEmptyBlock(pos)) {
            pos = pos.below(); // Move down until a solid block is found
        }

        PokemonEntity pokemon = argPro.createEntity(world);
        pokemon.getPokemon().setLevel(pokemonData.getLevel());
        pokemon.setPos(spawnPos.getX(), pos.getY(), spawnPos.getZ());

        world.addFreshEntity(pokemon);
        System.out.println("Spawned holiday Pokémon: " + pokemon.getName().getString() + " with form: " + pokemon.getForm().getName());
    }

    private static BlockPos getRandomNearbyPosition(BlockPos playerPos) {
        Random random = new Random();
        int offsetX = random.nextInt(10) - 5;
        int offsetZ = random.nextInt(10) - 5;

        return playerPos.offset(offsetX, 0, offsetZ);
    }
}