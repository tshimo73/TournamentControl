package enums;

import utils.Random;

public enum PlayerTitle {

    NoTitle, NM, CM, FM, IM, GM;

    public String getFullTitle() {
        switch (this) {
            case NoTitle:
                return "Regular Player";
            case NM:
                return "National Master";
            case CM:
                return "Candidate Master";
            case FM:
                return "FIDE Master";
            case IM:
                return "International Master";
            case GM:
                return "Grandmaster";
            default:
                return "N/A";
        }
    }

    public static PlayerTitle getRandomTitle() {
        PlayerTitle[] titles = PlayerTitle.values();
        int length = titles.length;
        int randomNumber = Random.getRandomInt(0, length - 1);

        return titles[randomNumber];
    }
    

    public static PlayerTitle getTitleFromRating(double rating) {
        if (rating < 2100) {
            return PlayerTitle.NoTitle;
        } else if (rating < 2200) {
            return PlayerTitle.NM;
        } else if (rating < 2300) {
            return PlayerTitle.CM;
        } else if (rating < 2400) {
            return PlayerTitle.FM;
        } else if (rating < 2500) {
            return PlayerTitle.IM;
        } else {
            return PlayerTitle.GM;
        }
    }

    public double getRating() {
        switch (this) {
            case NoTitle:
                return Random.getRandomDouble(1000, 2099);
            case NM:
                return Random.getRandomDouble(2100, 2199);
            case CM:
                return Random.getRandomDouble(2200, 2299);
            case FM:
                return Random.getRandomDouble(2300, 2399);
            case IM:
                return Random.getRandomDouble(2400, 2499);
            case GM:
                return Random.getRandomDouble(2500, 2900);
            default:
                return Random.getRandomDouble(100, 999);
        }
    }
}
