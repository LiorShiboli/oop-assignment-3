
public class Main {
    public static void main(String[] args) {
        String[] Files = Ex2_1.createTextFiles(10000,42,10);
        System.out.println(Ex2_1.getNumOfLinesThreads(Files));
    }
}
