
package utils;

import java.time.LocalDateTime;
import java.util.HashMap;


public class Validation {
    //not needed
    private Validation(){}
    
    public static String isAnyBlank(HashMap<String, String> fields){
        StringBuilder errors = new StringBuilder("");
        
        fields.forEach((key, value) -> {
            if(value.isBlank()){
                errors.append(key).append(" is blank.\n");
            }
        });
        
        return errors.toString();
    }
    
public static String isDateGood(LocalDateTime start, LocalDateTime end) {
    StringBuilder errors = new StringBuilder();

    if (start == null) {
        errors.append("Start time cannot be empty.\n");
    }
    if (end == null) {
        errors.append("End time cannot be empty.\n");
    }
    
    if (start.isAfter(end)) {
        errors.append("The start date cannot be after the end date.\n");
    } else if (start.isEqual(end)) { 
        errors.append("The start and end date cannot be the exact same time.\n");
    }

    return errors.toString();
}
}
