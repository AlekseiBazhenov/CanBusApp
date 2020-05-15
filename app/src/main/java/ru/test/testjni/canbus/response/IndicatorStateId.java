package ru.test.testjni.canbus.response;

public enum IndicatorStateId {

    LOW_VOLTAGE_ON_BOARD(0),
    HIGH_ENGINE_TEMP(1),
    LOW_OIL_PRESSURE(2),
    LOW_BREAK_FLUID_LEVEL(3),
    LOW_FUEL_LEVEL(4),
    MANUAL_BRAKE_REMOVED(8),
    LEFT_TURN_SIGNAL(9),
    RIGHT_TURN_SIGNAL(10),
    HEAD_LIGHT(11),
    PLATES(16),
    GATE(17);

    private int value;

    IndicatorStateId(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
