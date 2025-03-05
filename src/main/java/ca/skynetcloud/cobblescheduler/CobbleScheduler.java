package ca.skynetcloud.cobblescheduler;


import ca.skynetcloud.cobblescheduler.commands.DebugMode;
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

    public static Logger logger = LoggerFactory.getLogger("CobbleScheduler");


    private static final String NAME = "CobbleScheduler";
    private static final String AUTHORS = "SkyNetCloud";
    private static final String VERSION = "0.0.3";
    private static final File CONFIG_FILE = new File("config/CobbleHolidays/holidays.json");
    public static Config config;
    public static MiniMessage miniMessage = MiniMessage.miniMessage();
    public static final Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
    public static long lastSpawnTime = 0;

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register( (commandDispatcher, commandBuildContext, commandSelection) -> {
                    SchedulerCommands.register(commandDispatcher);
                    DebugMode.register(commandDispatcher);
                });

        loadConfig();
        ServerLifecycleEvents.SERVER_STARTING.register(minecraftServer -> {
            logger.info(
                    "Starting up %n by %authors %v".replace("%n", NAME).replace("%authors", AUTHORS).replace("%v",
                            VERSION
                    ));
        });


        HolidaySpawnEvent.SpawnInit();
    }


    public static void loadConfig() {
        CONFIG_FILE.getParentFile().mkdirs(); // Ensure directory exists

        if (CONFIG_FILE.exists()) {
            logger.info(NAME + ": Config file exists. Loading it.");
            try (FileReader fileReader = new FileReader(CONFIG_FILE)) {
                config = gson.fromJson(fileReader, Config.class);

                if (config == null) {
                    logger.warn("Loaded config is null. Resetting to default config.");
                    config = Config.Configs();
                }
            } catch (JsonSyntaxException e) {
                logger.warn("Error reading config file. Using default config.");
                
                config = Config.Configs();
            } catch (IOException e) {
                logger.warn("Error reading config file.");
                
                config = Config.Configs();
            }
        } else {
            logger.info(NAME + ": Config file not found. Creating a new one with default settings.");
            config = Config.Configs();
        }

        saveConfig();
    }

    public static void saveConfig() {
        try (FileWriter fileWriter = new FileWriter(CONFIG_FILE)) {
            gson.toJson(config, fileWriter);
            fileWriter.flush();
            logger.info(NAME + ": Config saved successfully.");
        } catch (IOException e) {
            logger.warn("Failed to save the config!");
            
        }
    }

    public static void reloadConfig() {
        logger.info("Reloading config");
        loadConfig();
    }
}