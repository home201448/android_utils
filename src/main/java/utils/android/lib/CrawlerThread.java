package utils.android.lib;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Thread
 */
public class CrawlerThread implements Runnable {
    private static final String TAG = "CrawlerThread";

    @Override
    public void run() {
        System.err.println("run");
        exeCmd();
        System.err.println("exit");
    }

    final static String COMMAND_WIN = "cmd"; // php cli.php Test test
    final static String COMMAND_LINUX = "sh"; // php cli.php Test test
    final static String CD_WIN = "cd d:\\WorkProject\\cmsv2 \n";
    final static String CD_LINUX = "pushd /www/ltcms/ \n";
    final static String PHP = "php cli.php Collection run debug true";

    public static void exeCmd() {
        BufferedWriter output = null;
        BufferedInputStream input = null;
        BufferedInputStream error = null;
        try {
            Process process = Runtime.getRuntime().exec(Utils.isLinux() ? COMMAND_LINUX : COMMAND_WIN);
            output = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            input = new BufferedInputStream(process.getInputStream());
            error = new BufferedInputStream(process.getErrorStream());
            output.write(Utils.isLinux() ? CD_LINUX : CD_WIN);
            output.flush();
            output.write(PHP);
            output.write("\n");
            output.write("exit");
            output.write("\n");
            output.flush();
            byte[] buff = new byte[1024 * 1000];
            int len = 0;
            System.err.println("INPUT =>");
            while ((len = input.read(buff)) > 0) {
                System.out.println(new String(buff, 0, len, "gbk"));
            }
            System.err.println("ERROR =>");
            while ((len = error.read(buff)) > 0) {
                System.out.println(new String(buff, 0, len, "gbk"));
            }
            process.waitFor();
            int exitValue = process.exitValue();
            System.err.println("exitValue " + exitValue);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                output.close();
                input.close();
                error.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
