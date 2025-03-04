package ca.skynetcloud.cobblescheduler.utils;

import ca.skynetcloud.cobblescheduler.config.Config;
import net.kyori.adventure.text.Component;
import net.minecraft.server.level.ServerPlayer;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static ca.skynetcloud.cobblescheduler.CobbleScheduler.miniMessage;

public class MessageUtils {

    private static Instant lastMessageSent = Instant.MIN;

    public static void sendMessage(ServerPlayer player, Config config) {
        if (!config.isSendMessagesEnabled()) {
            return;
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

                Component messageComponent = miniMessage.deserialize(holidayMsg);
                player.sendMessage(messageComponent);


                lastMessageSent = Instant.now();

                return;
            }
        }
    }

}
