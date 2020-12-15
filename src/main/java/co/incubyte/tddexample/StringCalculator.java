package co.incubyte.tddexample;

public class StringCalculator {
    public int add(String numbers){
        if(numbers.equals(""))
            return 0;
        else if(numbers.matches("^\\d+$"))
            return Integer.parseInt(numbers);
        else
            return 0;
    }
}
