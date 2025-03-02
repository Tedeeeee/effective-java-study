package codeTest;

import java.util.Arrays;

public class Programmers_H_Index {
    public static void main(String[] args) {
        int[] arr = {3, 0, 6, 1, 5};

        System.out.println(solution(arr));
    }

    public static int solution(int[] citations) {
        int answer = 0;

        Arrays.sort(citations);

        for (int i = 0; i < citations.length; i++) {
            int h = citations.length - i;

            if (citations[i] >= h) {
                answer = h;
                break;
            }
        }

        return answer;
    }
}
