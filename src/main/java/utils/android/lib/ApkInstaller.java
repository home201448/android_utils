package utils.android.lib;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 4/27/0027 by
 */
public class ApkInstaller {
    private static final String TAG = "ApkInstaller";
    static final int type_apk = 1; //apk
    static final int type_fold = 2; //fold
    static int type = 0; //fold
    static String all = "yes"; //fold

    public static void main(String[] args) {
        try {
            if (args == null || args.length < 1) {
                System.out.println("no file.");
                return;
            }
            if (args[1].contains("apk")) {
                type = type_apk;
            } else {
                type = type_fold;
            }
            all = args[0];
            List<String> files = new ArrayList<>();
            if (type == type_apk) {
                for (String str : args) {
                    if (str.contains(".apk")) {
                        files.add(str);
                    }
                }
            } else {
                File file = new File(args[1]);
                if (file.exists() && file.isDirectory()) {
                    for (File f : file.listFiles()) {
                        if (f.isFile() && f.getName().contains(".apk")) {
                            files.add(f.getAbsolutePath());
                        }
                    }
                }
            }
            install(files);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    final static String COMMAND_WIN = "cmd "; // php cli.php Test test
    final static String COMMAND_LINUX = "sh "; // php cli.php Test test
    final static String ADB = "adb devices \n";
    final static String ADB_INSTLL = "adb -s @ install -r \"#\" \r\n\r\n";

    public static void install(List<String> files) {
        BufferedWriter output = null;
        BufferedInputStream input = null;
        BufferedInputStream error = null;
        boolean listFlag = false;
        StringBuffer buffer = new StringBuffer();
        try {
            Process process = Runtime.getRuntime().exec(Utils.isLinux() ? COMMAND_LINUX : COMMAND_WIN);
            output = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            input = new BufferedInputStream(process.getInputStream());
            error = new BufferedInputStream(process.getErrorStream());
            output.write(ADB);
            output.write(ADB);
            output.write("\n");
            output.write("where cmd \r\n");
            output.write("\r\n");
            output.flush();

            byte[] buff = new byte[1024 * 1000];
            int len = 0;
            int pos = 0;
            while ((len = input.read(buff)) > 0) {
                buffer.append(new String(buff, 0, len, "gbk"));
                pos = buffer.lastIndexOf("List of devices attached");
                if (!listFlag && pos > 0) {
                    listFlag = true;
                }
                if (listFlag && buffer.indexOf("System32") > 0) {
                    break;
                }
            }
            String devices = buffer.substring(pos, buffer.lastIndexOf("System32"));
            List<String> list = getName(devices);

            buffer.delete(0, buffer.length());
            if (all.equals("yes")) {
                for (String name : list) {
                    for (String file : files) {
                        System.out.println(file + "安装到手机" + name);
                        output.write(ADB_INSTLL.replace("@", name).replace("#", file));
                    }
                    output.write("\r\n");
                }
            } else {
                int max = files.size() < list.size() ? files.size() : list.size();
                for (int ii = 0; ii < max; ii++) {
                    System.out.println(files.get(ii) + "安装到手机" + list.get(ii));
                    output.write(ADB_INSTLL.replace("@", list.get(ii)).replace("#", files.get(ii)));
                }
            }
            output.write("where cmd \n\n");
            output.flush();

            while ((len = input.read(buff)) > 0) {
                String sss = new String(buff, 0, len, "gbk");
                if (sss.contains("install") || sss.contains("Success") || sss.contains("Failure")) {
                    System.out.println(sss);
                }
                if (sss.contains("System32")) {
                    break;
                }
            }

            output.write("\r\n exit \r\n");
            output.flush();
            process.waitFor();
            System.err.println(" \r\n\r\n EXIT ");
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

    static List<String> getName(String devices) {
        List<String> names = new ArrayList<>();
        if (devices != null && !devices.isEmpty()) {
            String[] arr = devices.split("\r\n");
            for (int i = 0; i < arr.length; i++) {
                String str = arr[i];
                if (str.trim().endsWith("device")) {
                    str = str.replace("device", "").trim();
                    System.out.println(str);
                    names.add(str);
                }
            }
        }
        return names;
    }

}
