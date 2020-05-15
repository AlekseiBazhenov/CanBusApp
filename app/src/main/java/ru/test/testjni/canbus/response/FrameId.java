package ru.test.testjni.canbus.response;

public enum FrameId {

    CAR_SPEED("101"),
    ENGINE_SPEED("102"),
    FUEL_LEVEL("103"),
    ENGINE_TEMPERATURE("104"),
    OTHER("105"); // гидропривод прицепа

    private String value;

    FrameId(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static FrameId fromString(String text) {
        for (FrameId frameId : FrameId.values()) {
            if (frameId.value.equalsIgnoreCase(text)) {
                return frameId;
            }
        }
        return null;
    }
}
