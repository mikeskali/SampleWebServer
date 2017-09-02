package samle.webserver.math;

import org.junit.Test;
import sample.webserver.math.MathOperations;

import static org.junit.Assert.assertEquals;

public class TestMathOperations {
  @Test
  public void testFibonacci(){
    assertEquals("Fibonacci for 1 should be 1", 1, MathOperations.calculateFibonacci(1));
    assertEquals("Fibonacci for 2 should be 1", 1, MathOperations.calculateFibonacci(2));
    assertEquals("Fibonacci for 10 should be 55", 55, MathOperations.calculateFibonacci(10));
  }
}
