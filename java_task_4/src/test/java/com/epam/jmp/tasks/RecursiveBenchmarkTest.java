package com.epam.jmp.tasks;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

@Test()
public class RecursiveBenchmarkTest {

    @Test(groups = "benchmark")
    public void runRecursiveBenchmarkTests() {
        Options opt = new OptionsBuilder()
                .include(RecursiveBenchmarkTest.class.getSimpleName())
                .forks(1)
                .threads(4)
                .warmupIterations(1)
                .build();
        try {
            new Runner(opt).run();
            Assert.assertTrue(true);
        } catch (Exception e) {
            Assert.fail("Exception was thrown! " + e.getMessage());
        }
    }

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        private final int TEST_SIZE = 500_000_000;
        private final double[] TEST_ARRAY = new double[TEST_SIZE];

        @Setup
        public void setup() {
            Random random = new Random(1234);
            for (int i = 0; i < TEST_SIZE; i++) {
                TEST_ARRAY[i] = random.nextDouble(10000);
            }
        }
    }

    @State(Scope.Thread)
    public static class ThreadState {
        private final ForkJoinPool pool = new ForkJoinPool();

        @TearDown
        public synchronized void tearDown() {
            pool.shutdown();
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void doubleSquaresRecursiveBenchmark(BenchmarkState benchmarkState, ThreadState threadState, Blackhole blackhole) {
        blackhole.consume(threadState.pool.invoke(new DoubleSquaresTask(benchmarkState.TEST_ARRAY, 0, benchmarkState.TEST_SIZE)));
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void doubleSquaresLinearBenchmark(BenchmarkState benchmarkState, Blackhole blackhole) {
        double sum = 0;
        for (double v : benchmarkState.TEST_ARRAY) {
            sum += v * v;
        }
        blackhole.consume(sum);
    }
}
