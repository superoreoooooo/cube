package org.oreoprojekt.cube.enums;

public enum Effect {
    GRAVITY("중력"),
    WEIGHTLESS("무중력"),
    SMOKE("연막"),
    DIZZY("어지러움"),
    HUNGRY("배고픔"),
    TIRED("피곤"),
    MISFORTUNE("불운"),
    FORTUNE("행운"),
    NORMAL("일반"),
    ERROR("에러");

    private final String effect;

    Effect(String effect) {
        this.effect = effect;
    }

    public String getEfName() {
        return this.effect;
    }
}