import java.time.Duration;
import java.time.Instant;

public class Main {
    public static void main(String[] args) {
        String[] Files = Ex2_1.createTextFiles(1000,42,100);
        Instant beforeNonThread = Instant.now();
        System.out.println(Ex2_1.getNumOfLines(Files));
        Instant afterNonThread = Instant.now();
        System.out.println(Duration.between(beforeNonThread, afterNonThread).toMillis()+"milliseconds");
        Instant beforeThread = Instant.now();
        System.out.println(Ex2_1.getNumOfLinesThreads(Files));
        Instant afterThread = Instant.now();
        System.out.println(Duration.between(beforeThread, afterThread).toMillis()+"milliseconds");
        Instant beforeThreadpool = Instant.now();
        System.out.println(Ex2_1.getNumOfLinesThreadpool(Files));
        Instant afterThreadpool = Instant.now();
        System.out.println(Duration.between(beforeThreadpool, afterThreadpool).toMillis()+"milliseconds");

    }
}
