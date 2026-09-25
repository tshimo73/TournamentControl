
package tournaments;

import enums.TournamentType;
import java.time.LocalDateTime;
import java.util.Map;
import utils.Random;

public class TournamentManager {

    // made with claude
    private static final int MAX_DAYS_UNTIL_START = 180;

    // made with claude
    private static final Map<TournamentType, int[]> DURATION_RANGES = Map.of(
            TournamentType.RAPID, new int[]{1, 3},
            TournamentType.BLITZ, new int[]{1, 2},
            TournamentType.CLASSICAL, new int[]{7, 14},
            TournamentType.BULLET, new int[]{1, 1},
            TournamentType.HYPERBULLET, new int[]{1, 1}
    );

    // made with claude and me
    /**
     * Generates a start time
     * @return 
     */
    public static LocalDateTime generateStartDate() {
        int daysFromNow = Random.getRandomInt(0, MAX_DAYS_UNTIL_START) + 1;
        int hour = 9 + Random.getRandomInt(0, 4); // start between 9am and 12pm
        return LocalDateTime.now()
                .plusDays(daysFromNow)
                .withHour(hour)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
    }

    // made with claude and me
    /**
     * Generates an end date time based on the start datetime and the tournament type
     * @param start the start datetime
     * @param type the tournament type
     * @return 
     */
    public static LocalDateTime generateEndDate(LocalDateTime start, TournamentType type) {
        int[] range = DURATION_RANGES.getOrDefault(type, new int[]{3, 7});
        int duration = range[0] + Random.getRandomInt(0, range[1] - range[0] + 1);
        return start.plusDays(duration).withHour(18); // ends at 6pm on final day
    }

}
