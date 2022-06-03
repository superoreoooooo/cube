package org.oreoprojekt.cube.system;

public class cubeInitial {
    public static int[] effectList = new int[9];
    public static int[] roomType = new int[5];
    public static void initialize() {
        for (int i = 0; i < 6; i++) {
            effectList[i] = 80; //1 ~ 6 Gravity ~ Tired
        }
        effectList[6] = 10;     //7 Misfortune
        effectList[7] = 10;     //8 Fortune
        effectList[8] = 500;    //9 Normal

        roomType[0] = 1;         //up
        roomType[1] = 9;         //boss
        roomType[2] = 90;        //Item(trap)
        roomType[3] = 100;       //Normal
        roomType[4] = 800;       //Enemy
    }
}
