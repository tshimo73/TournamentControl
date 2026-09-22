
package utils;

/**
 * Generates random numbers
 * @author tshim
 */
public class Random {
    
    /**
     * Generates a random integer
     * @param start - minimum
     * @param end - maximum
     * @return the random integer
     */
    public static int getRandomInt(int start, int end){
        return (int)(Math.random() * (end - start) + start);
    }
    
    /**
     * Generates a random double
     * @param start - minimum
     * @param end - maximum
     * @return the generated double
     */
    public static double getRandomDouble(double start, double end){
        double r = Double.parseDouble(String.format("%.2f", (Math.random() * (end - start) + start)));
        
        return r;
    }
}
