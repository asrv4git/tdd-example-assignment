package co.incubyte.tddexample;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StringCalculator {
    public int add(String numbers){
        if(numbers.equals(""))
            return 0;
        else if(numbers.matches("^\\d+$"))
            return Integer.parseInt(numbers);
        else if(numbers.matches("^((\\d+(,|\n))+\\d+)$")){
            int result = Arrays.stream(numbers.split(","))
                    .map(str->str.split("\n"))
                    .flatMap(strArray->Arrays.stream(strArray))
                    .map(str->Integer.parseInt(str))
                    .reduce(0,(a,b)->a+b);
            return result;
        }
        return 0;
    }

    public static void main(String[] args) {
        System.out.println(Pattern.compile("\n").matcher("pogo\njojo").start());
    }
}
