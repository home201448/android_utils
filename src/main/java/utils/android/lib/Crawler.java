package utils.android.lib;

import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Crawler {
    public static void main(String[] args) {
        System.out.println(Calendar.getInstance().getTime());
        Utils.isLinux();
       // getInstance().startCrawler();
    }

    private Crawler() {

    }

    public static synchronized Crawler getInstance() {
        if (instance == null) {
            instance = new Crawler();
        }
        return instance;
    }

    private static Crawler instance;
    ExecutorService service = Executors.newFixedThreadPool(Config.THREAD_COUNT);

    public void startCrawler() {
        service.execute(new CrawlerThread());
    }
}
