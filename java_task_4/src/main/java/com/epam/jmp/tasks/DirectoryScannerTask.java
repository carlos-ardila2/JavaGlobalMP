package com.epam.jmp.tasks;

import com.epam.jmp.tasks.model.DirectoryStatistics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ForkJoinTask;

public class DirectoryScannerTask extends RecursiveTask<DirectoryStatistics> {

    private final Path path;

    public DirectoryScannerTask(Path path) {
        this.path = path;
    }

    @Override
    protected DirectoryStatistics compute() {

        DirectoryStatistics statistics = new DirectoryStatistics();

        List<DirectoryScannerTask> tasks = new ArrayList<>();

        try (Stream<Path> stream = Files.list(path)) {
            stream.forEach(p -> {
                if (Files.isDirectory(p)) {
                    statistics.incrementDirectoryCount();
                    tasks.add(new DirectoryScannerTask(p));
                } else {
                    statistics.incrementFileCount();
                    statistics.addTotalSize(p.toFile().length());
                }
            });

            invokeAll(tasks);

            tasks.stream()
                    .map(ForkJoinTask::join)
                    .forEach(statistics::incrementDirectoryStatistics);

            return statistics;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}