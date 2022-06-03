package org.oreoprojekt.cube.util;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

import java.util.Random;

public class cubeRandomPicker {
    public static int random(int[] percentages) { //0번칸부터 n-1번칸까지 n개 각각 칸에 확률 리턴 : 칸번호 -1: error
        Random random = new Random();

        int size = 1000;

        int c = random.nextInt(size);
        int sum = 0;
        int cnt = 0;
        int sum2 = 0;
        int ret = -1;
        int realLeng = 0;

        for (int i = 0; i < percentages.length; i++) {
            if (percentages[i] != 0) realLeng++;
            sum = sum + percentages[i];
            if (sum > size) return ret;
        }

        for (int s = 0; s < size; s++) {
            if (cnt >= percentages.length) return realLeng + 1;
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
