import java.util.*;
import java.util.concurrent.*;

public class Main {

  public static void main(String[] args) throws InterruptedException, ExecutionException {
    List<Future> futureList = new ArrayList<>();
    ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    try {
      String[] texts = new String[25];
      for (int i = 0; i < texts.length; i++) {
        texts[i] = generateText("aab", 30_000);
      }
      long startTs = System.currentTimeMillis(); // start time
      for (String text : texts) {
        Callable logic = () -> {
          int maxSize = 0;
          for (int i = 0; i < text.length(); i++) {
            for (int j = 0; j < text.length(); j++) {
              if (i >= j) {
                continue;
              }
              boolean bFound = false;
              for (int k = i; k < j; k++) {
                if (text.charAt(k) == 'b') {
                  bFound = true;
                  break;
                }
              }
              if (!bFound && maxSize < j - i) {
                maxSize = j - i;
              }
            }
          }
          System.out.println(text.substring(0, 100) + " -> " + maxSize);
          return maxSize;
        };
        futureList.add(executor.submit(logic));
      }

      int maxSizeValue = 0;
      for (Future f : futureList) {
        maxSizeValue = (Integer) f.get() > maxSizeValue ? (Integer) f.get() : maxSizeValue;
      }

      System.out.println("Maximal size: " + maxSizeValue);
      long endTs = System.currentTimeMillis(); // end time
      System.out.println("Time: " + (endTs - startTs) + "ms");
    } finally {
      executor.shutdown();
    }
  }

  public static String generateText(String letters, int length) {
    Random random = new Random();
    StringBuilder text = new StringBuilder();
    for (int i = 0; i < length; i++) {
      text.append(letters.charAt(random.nextInt(letters.length())));
    }
    return text.toString();
  }
}