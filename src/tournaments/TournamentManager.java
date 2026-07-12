/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tournaments;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.datafaker.Faker;
import net.datafaker.providers.sport.Chess;
import players.Player;

public class TournamentManager {

    private static final Faker F = new Faker();
    private static final Chess C = F.chess();
    private static final java.util.Random RAND = new java.util.Random();

    // made with claude
    private static final int MAX_DAYS_UNTIL_START = 180;

    // made with claude
    private static final Map<TournamentType, int[]> DURATION_RANGES = Map.of(
            TournamentType.RAPID, new int[]{1, 3},
            TournamentType.BLITZ, new int[]{1, 2},
            TournamentType.CLASSICAL, new int[]{7, 14},
            TournamentType.BULLET, new int[]{1, 1}
    );

    // made with claude
    private static LocalDateTime generateStartDate() {
        int daysFromNow = RAND.nextInt(MAX_DAYS_UNTIL_START) + 1;
        int hour = 9 + RAND.nextInt(4); // start between 9am and 12pm
        return LocalDateTime.now()
                .plusDays(daysFromNow)
                .withHour(hour)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
    }

    // made with claude
    private static LocalDateTime generateEndDate(LocalDateTime start, TournamentType type) {
        int[] range = DURATION_RANGES.getOrDefault(type, new int[]{3, 7});
        int duration = range[0] + RAND.nextInt(range[1] - range[0] + 1);
        return start.plusDays(duration).withHour(18); // ends at 6pm on final day
    }

    public static Tournament generateTournament(int numPlayers) {
        String name = C.tournament(),
                fed = F.country().countryCode3().toUpperCase(),
                dir = F.name().fullName(),
                cArb = F.name().fullName(),
                depArb = F.name().fullName();

        TournamentType type = TournamentType.getRandomType();
        LocalDateTime start = generateStartDate();
        LocalDateTime end = generateEndDate(start, type);
        int rounds = type.getRounds(numPlayers);

        Tournament t = new Tournament();
        t.setName(name);
        t.setFederation(fed);
        t.setDirector(dir);
        t.setChiefArbiter(cArb);
        t.setDeputyChiefArbiter(depArb);
        t.setTournamentType(type);
        t.setStartDate(start);
        t.setEndDate(end);
        t.setRounds(rounds);

        return t;

    }

    public static Tournament generateTournament() {
        String name = C.tournament(),
                fed = F.country().countryCode3(),
                dir = F.name().fullName(),
                cArb = F.name().fullName(),
                depArb = F.name().fullName();

        TournamentType type = TournamentType.getRandomType();
        LocalDateTime start = generateStartDate();
        LocalDateTime end = generateEndDate(start, type);
        int rounds = type.getRounds();

        Tournament t = new Tournament();
        t.setName(name);
        t.setFederation(fed);
        t.setDirector(dir);
        t.setChiefArbiter(cArb);
        t.setDeputyChiefArbiter(depArb);
        t.setTournamentType(type);
        t.setStartDate(start);
        t.setEndDate(end);
        t.setRounds(rounds);

        return t;

    }

    public static Tournament generateTournamentPlayers(int numPlayers) {
        List<Player> players = new ArrayList<>();

        for (int i = 0; i < numPlayers; i++) {
            players.add(Player.generatePlayer());
        }

        Tournament t = generateTournament(numPlayers);
        t.setPlayers(players);
        
        return t;
    }
}
