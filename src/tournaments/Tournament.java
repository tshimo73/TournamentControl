
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
    protected boolean isImported = false, hasEnded = false;


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

    
    public boolean hasEnded() {
        return hasEnded;
    }

    public void setHasEnded(boolean hasEnded) {
        this.hasEnded = hasEnded;
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
