package co.incubyte.tddexample;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {
    public int add(String numbers) {
        if (numbers.equals(""))
            return 0;
        else if (numbers.matches("-?\\d+")) {
            int number = Integer.parseInt(numbers);
            if(number<0)
                throw new RuntimeException("negatives not allowed");
            return number;
        }
        else if (numbers.matches("^((\\d+(,|\n))+\\d+)$")) {
            int result = Arrays.stream(numbers.split(","))
                    .map(str -> str.split("\n"))
                    .flatMap(strArray -> Arrays.stream(strArray))
                    .map(str -> Integer.parseInt(str))
                    .reduce(0, (a, b) -> a + b);
            return result;
        } else if (numbers.matches("^\\/\\/(.)\\n(.*)$")) {
            Pattern p = Pattern.compile("^\\/\\/(.)\\n(.*)$");
            Matcher m = p.matcher(numbers);
            if (m.find()) {
                String delimiter = m.group(1);
                numbers = m.group(2);
                int result = Arrays.stream(numbers.split(delimiter.equals(".")?"\\.":delimiter))
                            .map(str -> Integer.parseInt(str))
                            .reduce(0, (a, b) -> a + b);
                    return result;
            }
            return 0;
        }
        return 0;
    }
}
