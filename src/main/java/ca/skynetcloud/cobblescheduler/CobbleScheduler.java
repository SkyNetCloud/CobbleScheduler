package ca.skynetcloud.cobblescheduler;

import ca.skynetcloud.cobblescheduler.commands.SchedulerCommands;
import ca.skynetcloud.cobblescheduler.config.Config;
import ca.skynetcloud.cobblescheduler.event.HolidaySpawnEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class CobbleScheduler implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("CobbleScheduler");
    public static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    public static final Gson GSON = new GsonBuilder()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();

    private static final String MOD_NAME = "CobbleScheduler";
    private static final String MOD_AUTHORS = "SkyNetCloud";
    private static final String MOD_VERSION = "0.1.0";
    private static final File CONFIG_FILE = new File("config/CobbleHolidays/holidays.json");

    public static Config config;
    public static long lastSpawnTime = 0;

    @Override
    public void onInitialize() {
        registerCommands();
        loadConfig();
        registerEventListeners();
        logStartupMessage();
    }

    private void registerCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> SchedulerCommands.register(dispatcher));
    }

    private void registerEventListeners() {
        HolidaySpawnEvent.SpawnInit();
    }

    private void logStartupMessage() {
        ServerLifecycleEvents.SERVER_STARTING.register(server -> LOGGER.info("Starting up {} by {} v{}", MOD_NAME, MOD_AUTHORS, MOD_VERSION));
    }

    public static void loadConfig() {
        File parentDir = CONFIG_FILE.getParentFile();

        if (!parentDir.exists()) {
            boolean directoriesCreated = parentDir.mkdirs();
            if (!directoriesCreated) {
                LOGGER.warn("Failed to create config directories: {}", parentDir.getAbsolutePath());
            }
        }

        if (CONFIG_FILE.exists()) {
            try (FileReader fileReader = new FileReader(CONFIG_FILE)) {
                config = GSON.fromJson(fileReader, Config.class);

                if (config == null) {
                    LOGGER.warn("Loaded config is null. Resetting to default config.");
                    config = Config.createDefault();
                }
            } catch (JsonSyntaxException | IOException e) {
               LOGGER.warn("Error reading config file. Using default config.", e);
                config = Config.createDefault();
            }
        } else {
            LOGGER.info("{}: Config file not found. Creating a new one with default settings.", MOD_NAME);
            config = Config.createDefault();
        }

        saveConfig();
    }

    public static void saveConfig() {
        try (FileWriter fileWriter = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(config, fileWriter);
            fileWriter.flush();
            LOGGER.info("{}: Config saved successfully.", MOD_NAME);
        } catch (IOException e) {
            LOGGER.warn("Failed to save the config!", e);
        }
    }

    public static void reloadConfig() {
        LOGGER.info("Reloading config");
        loadConfig();
    }
}