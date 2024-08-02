package com.epam.jmp.tasks;

import com.epam.jmp.akka.service.PCAssemblyService;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

@Test()
public class AssemblyBenchmarkTest {

    @Test(groups = "benchmark")
    public void runAssemblyBenchmarkTests() {
        Options opt = new OptionsBuilder()
                .include(AssemblyBenchmarkTest.class.getSimpleName())
                .forks(1)
                .threads(4)
                .build();
        try {
            new Runner(opt).run();
            Assert.assertTrue(true);
        } catch (Exception e) {
            Assert.fail("Exception was thrown! " + e.getMessage());
        }
    }

    @State(Scope.Benchmark)
    public static class CommonState {
        private PCAssemblyService service;

        @Setup
        public void setup() {
            service = new PCAssemblyService(4.0);
        }

        @TearDown
        public void tearDown() {
            service.shutdown();
        }
    }

    @Benchmark
    @BenchmarkMode({Mode.Throughput})
    @Warmup(iterations = 0)
    @Measurement(iterations = 1, batchSize = 1)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void pcAssemblyBenchmark(CommonState state, Blackhole blackhole) {
        state.service.assemblePC().thenAccept(blackhole::consume);
    }
}
