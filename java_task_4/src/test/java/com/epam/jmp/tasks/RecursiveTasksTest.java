package com.epam.jmp.tasks;

import org.testng.annotations.Test;
import java.util.concurrent.ForkJoinPool;

import static org.testng.Assert.assertEquals;

@Test()
public class RecursiveTasksTest {

    @Test(groups = "recursive")
    public void fibonacciTaskTest() {
        try (ForkJoinPool pool = new ForkJoinPool()) {
            assertEquals(1134903170L, pool.invoke(new FibonacciTask(45L)));
        }
    }
}
