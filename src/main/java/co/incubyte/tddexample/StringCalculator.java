package co.incubyte.tddexample;

public class StringCalculator {
    public int add(String numbers){
        if(numbers.equals(""))
            return 0;
        else if(numbers.matches("^\\d+$"))
            return Integer.parseInt(numbers);
        else if(numbers.matches("^((\\d+,)+\\d+)$")){
            String[] nums = numbers.split(",");
            return Integer.parseInt(nums[0])+Integer.parseInt(nums[1]);
        }
        return 0;
    }
}
