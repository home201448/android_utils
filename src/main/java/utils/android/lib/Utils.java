package utils.android.lib;

/**
 */
public class Utils {
    private static final String TAG = "Utils";

    // Linux or Windows
    public static boolean isLinux() {
        String separator = System.getProperty("file.separator");
        if (separator.equals("/")) {
            return true;
        }
        return false;
    }
}
