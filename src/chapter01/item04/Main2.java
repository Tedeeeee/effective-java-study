package chapter01.item04;

class StringUtils {

    private StringUtils() {};

    public static String removeCommas(String str) {
        return str.replaceAll(",", "");
    }
}

public class Main2 {
    public static void main(String[] args) {
        //StringUtils stringUtils = new StringUtils();

        StringUtils.removeCommas("1,2,3,4,5");
    }
}


