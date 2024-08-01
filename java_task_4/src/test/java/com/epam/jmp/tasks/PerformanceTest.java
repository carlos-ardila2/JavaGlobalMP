package com.epam.jmp.tasks;

import com.epam.jmp.akka.service.PCAssemblyService;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Test()
public class PerformanceTest {

    @Test()
    public void runPerformanceTests() {
        Options opt = new OptionsBuilder()
                .include(PerformanceTest.class.getSimpleName())
                .build();
        try {
            new Runner(opt).run();
            Assert.assertTrue(true);
        } catch (Exception e) {
            Assert.fail("Exception was thrown!");
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void payrollServicePerformance() {
        PCAssemblyService service = new PCAssemblyService();

        var results = new CompletableFuture[100];

        for (int i = 0; i < 100; i++) {
            results[i] = service.assemblePC();
        }

        Stream.of(results)
                .map(CompletableFuture::join)
                .forEach(pc -> System.out.println("PC Assembled: " + pc));

        service.shutdown();
    }
}
