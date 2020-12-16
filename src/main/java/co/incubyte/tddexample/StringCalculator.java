package co.incubyte.tddexample;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
        else if (numbers.matches("^((-?\\d+(,|\n))+-?\\d+)$")) {
            List<Integer> nums = Arrays.stream(numbers.split(","))
                    .map(str -> str.split("\n"))
                    .flatMap(strArray -> Arrays.stream(strArray))
                    .map(str -> Integer.parseInt(str))
                    .collect(Collectors.toList());
            return checkForNegativeNumsAndAdd(nums);
        } else if (numbers.matches("^\\/\\/(.)\\n(.*)$")) {
            Pattern p = Pattern.compile("^\\/\\/(.)\\n(.*)$");
            Matcher m = p.matcher(numbers);
            if (m.find()) {
                String delimiter = m.group(1);
                numbers = m.group(2);
                List<Integer> nums = Arrays.stream(numbers.split(delimiter.equals(".")?"\\.":delimiter))
                            .map(str -> Integer.parseInt(str))
                            .collect(Collectors.toList());
                return checkForNegativeNumsAndAdd(nums);
            }
            return 0;
        }
        return 0;
    }

    private int checkForNegativeNumsAndAdd(List<Integer> nums){
        List<Integer> negativeNums = new ArrayList<>();
        for(Integer n:nums){
            if(n<0)
                negativeNums.add(n);
        }
        if(negativeNums.size()==1)
            throw  new RuntimeException("negatives not allowed");
        else if(negativeNums.size()>1) {
            String message = String.join(",", negativeNums.stream()
                    .map(i->String.valueOf(i))
                    .collect(Collectors.toList()));
            throw new RuntimeException(message);
        }
        else
            return nums.stream().reduce(0, (a, b) -> a + b);
    }
}
