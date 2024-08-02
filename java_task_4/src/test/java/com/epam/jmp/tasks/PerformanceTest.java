package com.epam.jmp.tasks;

import com.epam.jmp.akka.service.PCAssemblyService;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.util.concurrent.atomic.AtomicLong;

@Test()
public class PerformanceTest {

    @Test(groups = "performance")
    public void payrollServicePerformance() {
        PCAssemblyService service = new PCAssemblyService();

        int goal = 100_000;

        long startTime = System.currentTimeMillis();

        AtomicLong counter = new AtomicLong(0);

        for (int i = 0; i < goal; i++) {
            service.assemblePC().thenApply(pc -> {
                System.out.println("PC Assembled: " + pc);
                counter.incrementAndGet();
                return pc;
            });
        }

        while (true) {
            if (counter.get() == goal) break;
        }

        service.shutdown();

        long duration = System.currentTimeMillis() - startTime;
        System.out.println("Process took " + duration + " milliseconds.");
        Assert.assertTrue(duration < 4000);
    }
}
