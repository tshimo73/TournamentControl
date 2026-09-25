package utils;

import java.time.LocalDateTime;
import java.util.HashMap;

/**
 * Validates user inputs
 *
 * @author tshim
 */
public class Validation {

    //not needed to instantiate - all methods are static
    private Validation() {
    }

    /**
     * Checks if any of he fields are blank
     * @param fields - hashmap, key: field name, value: the value to be checked
     * @return a string of errors
     */
    public static String isValidStrings(HashMap<String, String> fields) {
        StringBuilder errors = new StringBuilder("");

        fields.forEach((key, value) -> {
            if (value.isBlank()) {
                errors.append(key).append(" is blank.\n");
            }else if(value.length() > 35){
                errors.append(key).append(" is more than 35 characters.\n");
            }else if(value.length() < 5){
                errors.append(key).append(" cannot be less than five characters.\n");
            }
        });

        return errors.toString();
    }
    
    /**
     * 
     * @param fields - the names and values of each input
     * @param datatype - the datatype of the values
     * 
     * - ENSURE ALL VALUES ARE OF THE SAME EXPECTED DATATYPE
     * - There cannot be a double in one field and an int in the other
     * - Either all int or all double
     * 
     * @return the errors (if any)
     */
    public static String isValidNumbers(HashMap<String, String> fields, String datatype){
        StringBuilder errors = new StringBuilder("");
        
        fields.forEach((key, value) -> {
            
            if(value.isBlank()){
                errors.append(key).append(" cannot be blank");
            }else switch(datatype.toLowerCase().trim()){
                case "double": 
                    try {
                        double d = Double.parseDouble(value.trim());
                        
                        if(isNegative(d)){
                            errors.append(key).append(" cannot be negative (less than 0).\n");
                        }
                        
                        if(d > 2900){
                            errors.append(key).append(" cannot be higher than 2900.\n");
                        }
                    } catch (NumberFormatException ex) {
                        errors.append(key).append(" is not a valid double\n");
                    }
                    break;
                case "int":
                    try {
                        int i = Integer.parseInt(value.trim());
                        
                        if(isNegative(i)){
                            errors.append(key).append(" cannot be negative (less than 0).\n");
                        }
                        
                        if(i > 2900){
                            errors.append(key).append(" cannot be higher than 2900.\n");
                        }
                    } catch (NumberFormatException ex) {
                        errors.append(key).append(" is not a valid integer.\n");
                    }
                    break;
            }
        });
        
        return errors.toString();
        
    }
    
    private static boolean isNegative(int i){
        return i < 0;
    }
    
    private static boolean isNegative(double d){
        return d < 0;
    }

    /**
     * Validates tournament start and end datetimes
     * @param start 
     * @param end
     * @return a string of errors
     */
    public static String isDateGood(LocalDateTime start, LocalDateTime end) {
        StringBuilder errors = new StringBuilder();

        if (start == null) {
            errors.append("Start time cannot be empty.\n");
        }
        if (end == null) {
            errors.append("End time cannot be empty.\n");
        }

        if (start == null || end == null) {
            return errors.toString();
        }

        if (start.isAfter(end)) {
            errors.append("The start date cannot be after the end date.\n");
        } else if (start.isEqual(end)) {
            errors.append("The start and end date cannot be the exact same time.\n");
        }

        return errors.toString();
    }
}
