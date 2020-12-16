package co.incubyte.tddexample;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class StringCalculatorTest {

    private static StringCalculator stringCalculator;

    @BeforeAll
    public static void setup(){
        stringCalculator = new StringCalculator();
    }

    @Test
    public void addShouldReturnZeroForEmptyStringOfNumbers(){
        int actual = stringCalculator.add("");
        int expected = 0;
        Assertions.assertEquals(expected,actual);
    }

    @Test
    public void addReturnsTheSameNumberForInputStringWithOnlyOneNUmber(){
        Assertions.assertEquals(1,stringCalculator.add("1"));
        Assertions.assertEquals(124,stringCalculator.add("124"));
        Assertions.assertEquals(0,stringCalculator.add("0"));
    }

    @Test
    public void addPerformsValidAdditionForTwoNumbers(){
        Assertions.assertEquals(3,stringCalculator.add("1,2"));
        Assertions.assertEquals(34,stringCalculator.add("12,22"));
        Assertions.assertEquals(2,stringCalculator.add("0,2"));
    }

    @Test
    public void addPerformsValidAdditionForUnknownAmountOfNumbers(){
        Assertions.assertEquals(15,stringCalculator.add("1,2,3,4,5"));
        Assertions.assertEquals(100,stringCalculator.add("100"));
        Assertions.assertEquals(0,stringCalculator.add("0"));
        Assertions.assertEquals(0,stringCalculator.add(""));
        Assertions.assertEquals(200,stringCalculator.add("100,100"));
    }

    @Test
    public void addShouldAlsoHandleNewLineBetweenNumbers(){
        Assertions.assertEquals(6,stringCalculator.add("1\n2,3"));
        Assertions.assertEquals(15,stringCalculator.add("1,2,3\n4,5"));
        //invalid case
        Assertions.assertEquals(0,stringCalculator.add("10,\n,20,30\n40,500"));
    }

    @Test
    public void addShouldHandleCustomDelimiterDefinedAtTheStartOFInputString(){
        Assertions.assertEquals(150,stringCalculator.add("//;\n10;20;30;40;50"));
        Assertions.assertEquals(150,stringCalculator.add("// \n10 20 30 40 50"));
        Assertions.assertEquals(150,stringCalculator.add("//.\n10.20.30.40.50"));
    }
}
