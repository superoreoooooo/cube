package org.oreoprojekt.cube.util.utils;

import java.util.Random;

public class util_Randomizer {
    public static int random(int[] percentages) { //0번칸부터 n-1번칸까지 n개 각각 칸에 확률 리턴 : 칸번호 -1: error
        Random random = new Random();

        int size = 1000;

        int c = random.nextInt(size);
        int sum = 0;
        int sum2 = 0;
        int cnt = 0;
        int ret = -1;
        int realLength = 0;

        for (int percentage : percentages) {
            if (percentage != 0) realLength++;
            sum = sum + percentage;
            if (sum > size) return ret;
        }

        for (int s = 0; s < size; s++) {
            if (cnt >= percentages.length) return realLength + 1;
            if (percentages[cnt] + sum2 > c) {
                ret = cnt;
                break;
            }
            else {
                sum2 = sum2 + percentages[cnt];
                cnt++;
            }
        }
        return ret;
    }
}
