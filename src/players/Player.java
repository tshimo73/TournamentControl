
package players;

import enums.PlayerTitle;

public class Player {
    private int id;
    private String firstName, lastName, fideID, federation, tournamentID;
    private double rating, score = 0.0, tieBreak = 0.0;
    private PlayerTitle title;
    
    public Player(int id, String fN, String lN, String FID, String fed, double rat, PlayerTitle t){
        this.id = id;
        this.firstName = fN;
        this.lastName = lN;
        this.fideID = FID;
        this.federation = fed;
        this.rating = rat;
        this.title = t;
    }
    /**
     * Needed to create the snapshots for the matchmaker leaderboards
     */
    public Player(Player playerSnap){
        id = playerSnap.getId();
        firstName = playerSnap.getFirstName();
        lastName = playerSnap.getLastName();
        rating = playerSnap.getRating();
        score = playerSnap.getScore();
        tieBreak = playerSnap.getTieBreak();
        federation = playerSnap.getFederation();
        title = playerSnap.getTitle();
    }
    
    public Player(){}
    
    public static Player generatePlayer(){
        return PlayerManager.generatePlayer();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("PLAYER").append("#");
        sb.append(id).append("#");
        sb.append(firstName).append("#");
        sb.append(lastName).append("#");
        sb.append(fideID).append("#");
        sb.append(federation).append("#");
        sb.append(rating).append("#");
        sb.append(title).append("#");
        sb.append(score).append("#");
        sb.append(tieBreak).append("#");    
        return sb.toString();
    }

   

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getFullName(){
        return this.firstName + " " + this.lastName;
    }

    public String getFideID() {
        return fideID;
    }

    public void setFideID(String fideID) {
        this.fideID = fideID;
    }

    public String getFederation() {
        return federation;
    }

    public void setFederation(String federation) {
        this.federation = federation;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public PlayerTitle getTitle() {
        return title;
    }

    public void setTitle(PlayerTitle title) {
        this.title = title;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public double getTieBreak() {
        return tieBreak;
    }

    public void setTieBreak(double tieBreak) {
        this.tieBreak = tieBreak;
    }
    
    public void setTournamentID(String tID){
        this.tournamentID = tID;
    }
    
    public String getTournamentID(){
        return tournamentID;
    }
    
    
    
    
    
}
