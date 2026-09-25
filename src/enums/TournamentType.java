package enums;

import utils.Random;

/**
 * The enum of the types of tournaments
 *
 * @author tshim
 */
public enum TournamentType {
    CLASSICAL, RAPID, BLITZ, BULLET, HYPERBULLET, INVALID;

    /**
     * Gets the time control of the tournament
     *
     * @return
     */
    public String getTimeControl() {
        switch (this) {
            case CLASSICAL:
                return "90+30"; // 90 Minutes, add 30 seconds per move
            case RAPID:
                return "15+10"; // 15 Minutes, add 10 seconds per move
            case BLITZ:
                return "3+2"; // 3 Minutes, add 2 seconds per move
            case BULLET:
                return "2+1"; // 2 Minutes, add 1 second per move
            case HYPERBULLET:
                return "0.5"; // Half a minute
            default:
                return "Invalid Tournament Type";
        }
    }

    /*
        Only found out that you could do enums like
            ENUM("description") a while afterwards.
     */
    /**
     * Gets the name
     *
     * @return the name
     */
    public String getName() {
        switch (this) {
            case CLASSICAL:
                return "Classical";
            case RAPID:
                return "Rapid";
            case BLITZ:
                return "Blitz";
            case BULLET:
                return "Bullet";
            case HYPERBULLET:
                return "HyperBullet";
            default:
                return "Invalid Tournament Type";
        }
    }

    /**
     * Gets the DB ID of the enum
     *
     * @return
     */
    public int getID() {
        switch (this) {
            case CLASSICAL:
                return 1;
            case RAPID:
                return 2;
            case BLITZ:
                return 3;
            case BULLET:
                return 4;
            case HYPERBULLET:
                return 5;
            default:
                return -1;
        }
    }

    /**
     * Get number of rounds including players based on time control
     *
     * @param numPlayers
     * @return
     */
    public int getRounds(int numPlayers) {
        if (numPlayers <= 0) {
            throw new IllegalArgumentException("Player count must be positive");
        }

        // Swiss system: rounds = ceil(log2(numPlayers))
        int swissRounds = (int) Math.ceil(Math.log(numPlayers) / Math.log(2));

        // Cap it against the format's fixed maximum
        return Math.min(swissRounds, getRounds());
    }

    /**
     * Get number of rounds based on time control
     *
     * @return
     */
    public int getRounds() {
        switch (this) {
            case CLASSICAL:
                return 9;
            case RAPID:
                return 13;
            case BLITZ:
                return 15;
            case BULLET:
                return 20;
            case HYPERBULLET:
                return 30;

            default:
                return 5;
        }
    }

    /**
     * Generates a tournament random type
     *
     * @return the generated type
     */
    public static TournamentType getRandomType() { // Returns a random tournament type
        TournamentType[] types = TournamentType.values();
        int length = types.length;
        int randomNumber = Random.getRandomInt(0, length - 1);

        return types[randomNumber];
    }

    /**
     * Gets the type by ID
     *
     * @param id
     * @return
     */
    public static TournamentType getTypeByID(int id) {
        switch (id) {
            case 1:
                return CLASSICAL;
            case 2:
                return RAPID;
            case 3:
                return BLITZ;
            case 4:
                return BULLET;
            case 5:
                return HYPERBULLET;
            default:
                return INVALID;
        }
    }

    /**
     * Gets the tournament type from the name
     * @param name
     * @return 
     */
    public static TournamentType getValueFromName(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }

        for (TournamentType type : values()) {
            if (type.getName().equalsIgnoreCase(name.trim())) {
                return type;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
