package ca.skynetcloud.cobblescheduler.utils;

import ca.skynetcloud.cobblescheduler.CobbleScheduler;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DateUtils {

    private String holiday;
    private String startDate;
    private String endDate;
    private List<PokemonData> pokemonEntityList;
    private String holidayMessage;
    private static Boolean Debug = false;

    public String getHoliday() {
        return holiday;
    }

    public void setHoliday(String holiday) {
        this.holiday = holiday;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public List<PokemonData> getPokemonEntityList() {
        return pokemonEntityList;
    }

    public void setPokemonEntityList(List<PokemonData> pokemonEntityList) {
        this.pokemonEntityList = pokemonEntityList;
    }

    public String getHolidayMessage() {
        if (holidayMessage == null || holidayMessage.isEmpty()) {
            return "Happy " + holiday + "!"; // Default message if missing
        }
        return holidayMessage;
    }

    public void setHolidayMessage(String holidayMessage) {
        this.holidayMessage = holidayMessage;
    }

    public static String getTodayDate() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("MM-dd"));
    }

    public static boolean isDateInRange(String startDate, String endDate) {
        String todayDate = getTodayDate();

        if (endDate == null) {
            if (Debug) {
                CobbleScheduler.logger.info("Info: End date is null!");
            }
            return false;
        }

        return (todayDate.compareTo(startDate) >= 0) && (todayDate.compareTo(endDate) <= 0);
    }

    public static boolean isDateMatch(String holidayDate, String holidayStartDate, String holidayEndDate) {
        String todayDate = getTodayDate();

        if (holidayDate != null && !holidayDate.isEmpty()) {
            return todayDate.equals(holidayDate);
        }

        return isDateInRange(holidayStartDate, holidayEndDate);
    }
}
