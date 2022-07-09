package org.oreoprojekt.cube.enums;

public enum Effect {
    SPAWN("스폰", -2),
    GRAVITY("중력", 0),
    WEIGHTLESS("무중력", 1),
    SMOKE("연막", 2),
    DIZZY("어지러움", 3),
    HUNGRY("배고픔", 4),
    TIRED("피곤", 5),
    MISFORTUNE("불운", 6),
    FORTUNE("행운", 7),
    NORMAL("일반", 8),
    ERROR("에러", -1);

    private final String effect;
    private final int num;

    Effect(String effect, int num) {
        this.effect = effect;
        this.num = num;
    }

    public String getEfName() {
        return this.effect;
    }

    public int getEfNum() {
        return this.num;
    }
}