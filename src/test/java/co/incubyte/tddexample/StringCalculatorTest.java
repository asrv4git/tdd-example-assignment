package co.incubyte.tddexample;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class StringCalculatorTest {

    private StringCalculator stringCalculator;

    @BeforeAll
    public void setup(){
        stringCalculator = new StringCalculator();
    }

    @Test
    public void addShouldReturnZeroForEmptyStringOfNumbers(){
        int actual = stringCalculator.add("");
        int expected = 0;
        Assertions.assertEquals(actual,expected);
    }
}
