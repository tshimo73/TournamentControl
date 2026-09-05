/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tournaments;

import entities.TournamentEntity;
import java.time.LocalDateTime;
import players.Player;
import games.Game;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import matchmaking.MatchMaker;

public class Tournament {

    protected final TournamentEntity te = new TournamentEntity();
    protected String name, federation, director, chiefArbiter, deputyChiefArbiter, id;
    protected TournamentType tournamentType;
    protected LocalDateTime startDate, endDate;
    protected int rounds;
    protected List<Player> players = new ArrayList<>();
    protected List<Game> games = new ArrayList<>();
    protected boolean isImported = false;

    /**
     * Tournament with players and games set
     *
     * @param name
     * @param federation
     * @param director
     * @param chiefArbiter
     * @param deputyChiefArbiter
     * @param tournamentType
     * @param startDate
     * @param endDate
     * @param players
     * @param games
     */
    public Tournament(String name, String federation, String director, String chiefArbiter, String deputyChiefArbiter, TournamentType tournamentType, LocalDateTime startDate, LocalDateTime endDate, List<Player> players, List<Game> games) {
        this.name = name;
        this.federation = federation;
        this.director = director;
        this.chiefArbiter = chiefArbiter;
        this.deputyChiefArbiter = deputyChiefArbiter;
        this.tournamentType = tournamentType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.players = players;
        this.games = games;
    }

    /**
     * Tournament without players or games set
     *
     * @param name
     * @param federation
     * @param director
     * @param chiefArbiter
     * @param deputyChiefArbiter
     * @param tournamentType
     * @param startDate
     * @param endDate
     */
    public Tournament(String name, String federation, String director, String chiefArbiter, String deputyChiefArbiter, TournamentType tournamentType, LocalDateTime startDate, LocalDateTime endDate) {
        this.name = name;
        this.federation = federation;
        this.director = director;
        this.chiefArbiter = chiefArbiter;
        this.deputyChiefArbiter = deputyChiefArbiter;
        this.tournamentType = tournamentType;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Tournament() {
    }

    public void simulateGames(int rounds) {
        if (players.isEmpty()) {
            System.exit(0);
        }

        MatchMaker mm = new MatchMaker(players, 1);

        for (int i = 0; i < rounds; i++) {

            mm.setRound(i + 1);
            List<Game> matches = mm.generateRound(this);
            games.addAll(matches);
        }
    }

    /**
     * Returns the current standing of the players according to their scores,
     * and then their points
     *
     * @return
     */
    public List<Player> getLeaderBoard() {
        // My code copied and pasted from the MatchMaker class
        // sort by score decending, rating desc as tiebreaker
        Player[] sorted = (Player[]) players.toArray();

        // manually sorting them (for marks and then converting back to array
        // lists because i find them easier to use in this project)
        for (int i = 0; i < sorted.length - 1; i++) {
            for (int j = i + 1; j < sorted.length; j++) {

                if (sorted[i].getScore() == sorted[j].getScore()) {
                    // If their scores are even then sort them by tiebreaks
                    if (sorted[i].getTieBreak() < sorted[j].getTieBreak()) {
                        Player temp = sorted[i];
                        sorted[i] = sorted[j];
                        sorted[j] = temp;
                    }

                } else if (sorted[i].getScore() < sorted[j].getScore()) {
                    // if J's score is larger than I's then swap them
                    Player temp = sorted[i];
                    sorted[i] = sorted[j];
                    sorted[j] = temp;
                }
            }
        }

        return List.of(sorted);
    }

    public void setGamesFromDB() {
        if (getIsImported()) {
            return;
        }

        Map<Integer, Player> ps = new HashMap<>();
        System.out.println("Seeding tournament with games from Database...");
        List<Game> gs = te.getGames(id);

        if (!gs.isEmpty()) {
            setGames(gs);
            System.out.printf("%d games found and added!", gs.size());
        } else {
            System.out.println("No games were found for this tournament");
            return;
        }

        int count = 1;
        for (Game g : gs) {
            System.out.println(String.format("Game %d: %s", count, g));
            Player black = g.getBlack(), white = g.getWhite();
            if (!ps.containsKey(black.getId())) {
                ps.put(black.getId(), black);
            }
            if (!ps.containsKey(white.getId())) {
                ps.put(white.getId(), white);
            }
            count++;
        }

        setPlayers(new ArrayList<>(ps.values()));

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFederation() {
        return federation.toUpperCase();
    }

    public void setFederation(String federation) {
        this.federation = federation;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getChiefArbiter() {
        return chiefArbiter;
    }

    public void setChiefArbiter(String chiefArbiter) {
        this.chiefArbiter = chiefArbiter;
    }

    public String getDeputyChiefArbiter() {
        return deputyChiefArbiter;
    }

    public void setDeputyChiefArbiter(String deputyChiefArbiter) {
        this.deputyChiefArbiter = deputyChiefArbiter;
    }

    public TournamentType getTournamentType() {
        return tournamentType;
    }

    public void setTournamentType(TournamentType tournamentType) {
        this.tournamentType = tournamentType;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public int getRounds() {
        return rounds;
    }

    public void setRounds(int rounds) {
        this.rounds = rounds;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public List<Game> getGames() {
        return games;
    }

    public void setGames(List<Game> games) {
        this.games = games;
    }

    public boolean getIsImported() {
        return isImported;
    }

    public void setIsImported(boolean isImported) {
        this.isImported = isImported;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TOURNAMENT").append("#");
        sb.append(id).append("#");
        sb.append(name).append("#");
        sb.append(federation).append("#");
        sb.append(director).append("#");
        sb.append(chiefArbiter).append("#");
        sb.append(deputyChiefArbiter).append("#");
        sb.append(tournamentType).append("#");
        sb.append(startDate).append("#");
        sb.append(endDate).append("#");
        sb.append(rounds);

        return sb.toString();
    }

}
