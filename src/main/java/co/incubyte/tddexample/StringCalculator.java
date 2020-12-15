package co.incubyte.tddexample;

import java.util.Arrays;

public class StringCalculator {
    public int add(String numbers){
        if(numbers.equals(""))
            return 0;
        else if(numbers.matches("^\\d+$"))
            return Integer.parseInt(numbers);
        else if(numbers.matches("^((\\d+,)+\\d+)$")){
            int result = Arrays.stream(numbers.split(","))
                    .map(str->Integer.parseInt(str))
                    .reduce(0,(a,b)->a+b);
            return result;
        }
        return 0;
    }
}
