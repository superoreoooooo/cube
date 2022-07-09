package org.oreoprojekt.cube.system;

public class cubeInitial { // 1 : 0.1%
    public static int[] effectList = new int[9];
    public static int[] cubeType = new int[5];
    public static void initialize() {
        for (int i = 0; i < 6; i++) {
            effectList[i] = 80; //1 ~ 6 Gravity ~ Tired
        }
        effectList[6] = 10;     //7 Misfortune
        effectList[7] = 10;     //8 Fortune
        effectList[8] = 500;    //9 Normal

        /*
        cubeType[0] = 1;         //up
        cubeType[1] = 9;         //boss
        cubeType[2] = 90;        //Item(trap)
        cubeType[3] = 100;       //Normal
        cubeType[4] = 800;       //Enemy
         */

        cubeType[0] = 990;      //normal
        cubeType[1] = 10;      //boss
    }
}
