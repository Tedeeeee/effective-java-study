package codeTest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        String[] arr = {"may", "kein", "kain", "radi"};
        int[] arr2 = {5, 10, 1, 3};
        String[][] arr3 = {{"may", "kein", "kain", "radi"}
                , {"may", "kein", "brin", "deny"}
                , {"kon", "kain", "may", "coni"}};

        int[] solution = solution(arr, arr2, arr3);

        System.out.println(Arrays.toString(solution));
    }

    public static int[] solution(String[] name, int[] yearning, String[][] photo) {
        int[] answer = new int[photo.length];

        Map<String, Integer> mapArr = new HashMap<>();

        int size = name.length;
        for (int i = 0; i < size; i++) {
            mapArr.put(name[i], yearning[i]);
        }

        int photoSize = photo.length;
        for (int i = 0; i < photoSize ; i++) {
            int photoListSize = photo[i].length;
            int sum = 0;
            for (int j = 0; j < photoListSize; j++) {
                if (mapArr.containsKey(photo[i][j])){
                    sum += mapArr.get(photo[i][j]);
                }
            }
            answer[i] = sum;
        }

        return answer;
    }
}
