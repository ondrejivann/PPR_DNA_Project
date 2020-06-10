package cz.mendelu.dnaAnalyser.sequence;

import cz.mendelu.dnaAnalyser.sequence.stream.WindowStreamService;
import cz.mendelu.dnaAnalyser.sequence.stream.WindowStream;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class SequenceApplication {
    private void writeResultsToFile(List<Result> resultList, String resultPath) throws IOException {

        File resultFile = new File(resultPath);

        if (resultFile.exists()) {
            resultFile.delete();
        }

        FileWriter fileWriter = new FileWriter(resultPath);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        try {
            for (Result result: resultList) {
                bufferedWriter.append(result.toString() + "\n");
            }
            fileWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(SequenceApplication.class, args);
    }

    @Bean
    public CommandLineRunner demo_open_and_print(WindowStreamService windowStreamService) {

        ExecutorService executor = Executors.newFixedThreadPool(4);
//        ExecutorService executor = Executors.newSingleThreadExecutor();
        return args -> {
            long startTime = System.nanoTime();
            List<Result> resultList = new ArrayList<>();

            // Create Sequence file:
            Sequence sequence = new Sequence("c116daa3-b59f-4085-955c-ba99194cc235", SequenceType.DNA, false);
            WindowStream windowStream = windowStreamService.open(sequence, 31).get();

            // Print each triple nuclide to std out.
//            System.out.println("Print windows stream");

            long startTimeForEach = System.nanoTime();

            windowStream.forEach(

                    window -> {
                        Result result = new Result(window);
                        result.setScore();
                        executor.submit(()-> {
                            if(result.isIMotif()) {
                                System.out.println(result.toString());
                                resultList.add(result);
                            }
                        });


                    });
//            executor.shutdown();


            try {
                System.out.println("attempt to shutdown executor");
                executor.shutdown();
                executor.awaitTermination(5, TimeUnit.SECONDS);
            }
            catch (InterruptedException e) {
                System.err.println("tasks interrupted");
            }
            finally {
                if (!executor.isTerminated()) {
                    System.err.println("cancel non-finished tasks");
                }
                executor.shutdownNow();
                System.out.println("shutdown finished");

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
                System.out.println("Found iMotifs: " + resultList.size());

                writeResultsToFile(resultList, "data/results.txt");

            }

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
