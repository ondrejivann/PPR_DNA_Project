package cz.mendelu.dnaAnalyser.sequence;

import cz.mendelu.dnaAnalyser.sequence.stream.WindowStream;
import cz.mendelu.dnaAnalyser.sequence.stream.WindowStreamService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class SequenceApplication {

    private static String CSV_FILE_NAME = "iMotiveResults";
    private static String LONG_FILE = "cdeb6610-9521-11ea-bb37-0242ac130002";
    private static String SHORT_FILE = "eb50a995-a044-4f33-8ffa-5b184750b120";

    private  static List<Result> results;
    private  static List<Long> timeStamps = new ArrayList<>();

    public static void main(String[] args) {
        SpringApplication.run(SequenceApplication.class, args);
    }

    @Bean
    public CommandLineRunner work(WindowStreamService windowStreamService) {
        return args -> {

            timeStamps.add(System.currentTimeMillis());

            // Prepare section
            Sequence sequence = new Sequence(LONG_FILE, SequenceType.DNA, false);

            timeStamps.add(System.currentTimeMillis());

            // Process section
            results = new ArrayList<>();

            for (int i = 20; i < 23; i++){
                WindowStream windowStream = windowStreamService.open(sequence, i).get();

                windowStream.forEach(window -> {
                    Result result = new Result(window);
                    if (result.isIMotif()){
                        results.add(result);
                    }
                });
            }

            timeStamps.add(System.currentTimeMillis());

            // Writing section
            writeToCSVFile();

            timeStamps.add(System.currentTimeMillis());

            printTimes();
        };
    }

    private static void writeToCSVFile() {
        String filename = LocalTime.now().toString().replace(":","-");
        final FileWriter writer;
        try {
            writer = new FileWriter(CSV_FILE_NAME + filename + ".csv");
            results.forEach(result -> {
                try {
                    CSVUtils.writeLine(writer, result.resultToStringList());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void printTimes() {
        long preparingWindowStreamTime = timeStamps.get(1) - timeStamps.get(0);
        long processingTime = timeStamps.get(2) - timeStamps.get(1);
        long writingTime = timeStamps.get(3) - timeStamps.get(2);
        long totalTime = timeStamps.get(3) - timeStamps.get(0);

        float preparingWindowStreamTimePercentage = (((float) preparingWindowStreamTime)/totalTime)*100;
        float processingTimePercentage = (((float) processingTime)/totalTime)*100;
        float writingTimePercentage = (((float) writingTime)/totalTime)*100;

        System.out.println("Preparing window stream: " + preparingWindowStreamTime + " ms" + " In percentage: " + preparingWindowStreamTimePercentage + " %");
        System.out.println("Processing time: " + processingTime + " ms" + " In percentage: " + processingTimePercentage + " %");
        System.out.println("Writing time: " + writingTime + " ms" + " In percentage: " + writingTimePercentage + " %");
        System.out.println("Total time: " + totalTime + " ms");
    }
}
