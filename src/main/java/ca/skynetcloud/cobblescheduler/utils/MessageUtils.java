package ca.skynetcloud.cobblescheduler.utils;

import ca.skynetcloud.cobblescheduler.config.Config;
import net.kyori.adventure.text.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static ca.skynetcloud.cobblescheduler.CobbleScheduler.*;

public class MessageUtils {


    private static Instant lastMessageSent = Instant.MIN; // Store the last time a message was sent


    public static void sendMessage(ServerPlayer player, Config config) {
        if (!config.isSendMessagesEnabled()) {
            return; // Do nothing if the feature is disabled
        }

        Instant currentTime = Instant.now();
        long timeDifference = Duration.between(lastMessageSent, currentTime).getSeconds();


        if (timeDifference < config.getCooldown()) {
            return;
        }

        LocalDate currentDate = LocalDate.now();
        String currentDateFormatted = currentDate.format(DateTimeFormatter.ofPattern("MM-dd"));

        for (DateUtils holiday : config.getHolidays()) {
            if (holiday.getStartDate().equals(currentDateFormatted)) {

                String holidayMsg = holiday.getHolidayMessage();
                // Send the holiday message
                Component messageComponent = miniMessage.deserialize(holidayMsg);
                player.sendMessage(messageComponent);

                // Update the last message sent time
                lastMessageSent = Instant.now();

                return; // Exit after sending the message
            }
        }
    }

}
