package cz.mendelu.dnaAnalyser.sequence;

import cz.mendelu.dnaAnalyser.sequence.stream.Window;
import cz.mendelu.dnaAnalyser.sequence.stream.WindowStream;
import cz.mendelu.dnaAnalyser.sequence.stream.WindowStreamService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalTime;
import java.util.concurrent.*;

@SpringBootApplication
public class SequenceApplicationParallel {

    private static String CSV_FILE_NAME = "iMotiveResults";
    private static String LONG_FILE = "cdeb6610-9521-11ea-bb37-0242ac130002";
    private static String SHORT_FILE = "eb50a995-a044-4f33-8ffa-5b184750b120";

    private static BlockingDeque<Future<Result>> futures = new LinkedBlockingDeque<>(500);

    public static void main(String[] args) {
        SpringApplication.run(SequenceApplication.class, args);
    }

    @Bean
    public CommandLineRunner work_parallel(WindowStreamService windowStreamService) {
        return args -> {

            ExecutorService executor = Executors.newFixedThreadPool(8);

            Sequence sequence = new Sequence(SHORT_FILE, SequenceType.DNA, false);

           new Thread(() -> {
                for (int i = 11; i < 50; i++){
                    WindowStream windowStream = windowStreamService.open(sequence, i).get();
                    windowStream.forEach(window -> {
                        final Window w = window;
                        try {
                            futures.put(executor.submit(() -> new Result(w)));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    });
                }
           }, "producer").start();

            Thread writing = new Thread(() -> {
                String filename = LocalTime.now().toString().replace(":","-");
                final FileWriter writer;
                try {
                    writer = new FileWriter(CSV_FILE_NAME + filename + ".csv");
                    while (true){
                        try {
                            Result result = futures.take().get();
                            if (result.isIMotif())
                                CSVUtils.writeLine(writer, result.resultToStringList());
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            },"consumer");

            writing.start();
            writing.join();

            executor.shutdown();

        };
    }


}
