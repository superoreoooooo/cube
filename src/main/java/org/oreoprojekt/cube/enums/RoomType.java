package org.oreoprojekt.cube.enums;

public enum RoomType {
    NORMAL("일반", 0),
    BOSS("보스", 1),
    ERROR("에러", -1);

    private final String name;
    private final int num;

    RoomType(String name, int num) {
        this.name = name;
        this.num = num;
    }

    public int getNum() {
        return num;
    }

    public String getName() {
        return name;
    }
}
