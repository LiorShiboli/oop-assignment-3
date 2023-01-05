import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.*;

public class Ex2_1 {
    public static String[] createTextFiles(int n, int seed, int bound){
        String[] fileNames = new String[n];
        Random rand = new Random(seed);
        for (int i = 0; i < n; i++) {
            String fileName = "file"+i;
            fileNames[i] = fileName;
            int lineCount = rand.nextInt(bound);
            try {
                File newFile = new File("part-a\\Files\\"+fileName);
                if (newFile.exists()){
                    newFile.delete();
                }
                newFile.createNewFile();
                FileWriter fileWriter = new FileWriter(newFile);
                PrintWriter printWriter = new PrintWriter(fileWriter);
                for (int j = 0; j <lineCount ; j++) {
                    printWriter.println("this is certainly a line");
                }
                printWriter.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
        return fileNames;

    }
    public static int getNumOfLines(String[] fileNames){
        int lineCount = 0;
        for (String filename:fileNames) {
            lineCount += getNumOfLines(filename);
        }
        return lineCount;
    }

    private static int getNumOfLines(String fileName) {
        int lineCount = 0;
        try {
            File myObj = new File("part-a\\Files\\"+fileName);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                myReader.nextLine();
                lineCount++;
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return lineCount;
    }
    public static int getNumOfLinesThreads(String[] fileNames){
        sharedNumber sumLineCount = new sharedNumber(0);
        threadCalculateLinescount[] threadarr = new threadCalculateLinescount[fileNames.length];
        for (int i = 0; i <threadarr.length ; i++) {
            threadarr[i] = new threadCalculateLinescount(fileNames[i],sumLineCount);
            threadarr[i].start();
        }
        try {
            for (int i = 0; i < threadarr.length; i++) {
                threadarr[i].join();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return sumLineCount.getNum();
    }
    public static class threadCalculateLinescount extends Thread {
        private String fileName;
        private sharedNumber sumLineCount;
        threadCalculateLinescount(String fileName,sharedNumber sumLineCount){
            this.fileName = fileName;
            this.sumLineCount = sumLineCount;
        }
        public void run() {
            int lineCount = 0;
            try {
                File myObj = new File("part-a\\Files\\"+fileName);
                Scanner myReader = new Scanner(myObj);
                while (myReader.hasNextLine()) {
                    myReader.nextLine();
                    lineCount++;
                }
                myReader.close();
            } catch (FileNotFoundException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
            synchronized (sumLineCount){
                sumLineCount.setNum(lineCount + sumLineCount.getNum());
            }
        }
    }
    public static int getNumOfLinesThreadpool(String[] fileNames){
        int lineCount = 0 ;
        ExecutorService executor = Executors.newFixedThreadPool(10);
        //create a list to hold the Future object associated with Callable
        List<Future<Integer>> list = new ArrayList<Future<Integer>>();
        //Create MyCallable instance

        for(String filename:fileNames){
            Callable<Integer> callable = new calculateLinescount(filename);
            //submit Callable tasks to be executed by thread pool
            Future<Integer> future = executor.submit(callable);
            //add Future to the list, we can get return value using Future
            list.add(future);
        }
        for(Future<Integer> future : list){
            try {

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        //shut down the executor service now
        executor.shutdown();
        return lineCount;
    }
    public static class calculateLinescount implements Callable<Integer> {
        private String fileName;
        calculateLinescount(String fileName){
            this.fileName = fileName;
        }
        @Override
        public Integer call() throws Exception {
            int lineCount = 0;
            try {
                File myObj = new File("part-a\\Files\\"+fileName);
                Scanner myReader = new Scanner(myObj);
                while (myReader.hasNextLine()) {
                    myReader.nextLine();
                    lineCount++;
                }
                myReader.close();
            } catch (FileNotFoundException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
            return lineCount;
        }


    }
}


