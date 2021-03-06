package cz.mendelu.dnaAnalyser.sequence;

import cz.mendelu.dnaAnalyser.sequence.stream.WindowStreamService;
import cz.mendelu.dnaAnalyser.sequence.stream.WindowStream;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class SequenceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SequenceApplication.class, args);
    }

    @Bean
    public CommandLineRunner demo_open_and_print(WindowStreamService windowStreamService) {

        ExecutorService executor = Executors.newFixedThreadPool(4);
        return args -> {
            long startTime = System.nanoTime();
            // Create Sequence file:
            Sequence sequence = new Sequence("cdeb6610-9521-11ea-bb37-0242ac130002", SequenceType.DNA, false);
            WindowStream windowStream = windowStreamService.open(sequence, 31).get();

            // Print each triple nuclide to std out.
//            System.out.println("Print windows stream");

            long startTimeForEach = System.nanoTime();

            windowStream.forEach(
                    window -> {
                        executor.submit(()-> {
                            Result result = new Result(window);

                            if(result.isIMotif()) {
                                System.out.println(result.toString());
                            }
                        });


                    });
            executor.shutdown();
            long endTimeForEach = System.nanoTime();

            System.out.println("----------");
            long endTime = System.nanoTime();
            long timeElapsed = endTime - startTime;
            long timeElapsedForEach = endTimeForEach - startTimeForEach;

            double elapsedTimeInSecond = (double) timeElapsed / 1_000_000_000;
            double elapsedTimeInSecondReadFile = (double) timeElapsedForEach / 1_000_000_000;

            System.out.println("Execution ForEach time in milliseconds: " + timeElapsedForEach / 1000000);
            System.out.println("Execution ForEach time in seconds: " + elapsedTimeInSecondReadFile);
            System.out.println("----------");
            System.out.println("Execution time in milliseconds: " + timeElapsed / 1000000);
            System.out.println("Execution time in seconds: " + elapsedTimeInSecond);
        };
    }

    /*
    @Bean
    public CommandLineRunner demo_filter_by_start_Nucleotide(WindowStreamService windowStreamService) {
        return args -> {
            // Create Cequence file:
            Sequence s = new Sequence("5f9a8c63-71da-47c5-9130-31863476de9d", SequenceType.DNA, false);

            // Open windows Stream with windows size 3
            WindowStream windowStream = windowStreamService.open(s, 5).get();

            // Print each triple nuclide to std out.
            System.out.println("Print windows start with A");
            windowStream
                    .filter(w -> w.get(0) == Nucleotide.A)
                    .forEach(System.out::println);
            System.out.println("----------");
        };
    }

     */

}
