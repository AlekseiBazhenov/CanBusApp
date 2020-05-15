package ru.test.testjni.canbus.response;

import java.io.Serializable;
import java.util.HashMap;

public class CanBusResponse implements Serializable {

    private FrameId frameId;
    private long frameData;
    private HashMap<IndicatorStateId, Boolean> indicators;
    private String error;

    public CanBusResponse(FrameId frameId, long frameData, HashMap<IndicatorStateId, Boolean> indicators, String error) {
        this.frameId = frameId;
        this.frameData = frameData;
        this.indicators = indicators;
        this.error = error;
    }

    public FrameId getFrameId() {
        return frameId;
    }

    public long getFrameData() {
        return frameData;
    }

    public HashMap<IndicatorStateId, Boolean> getIndicators() {
        return indicators;
    }

    public String getError() {
        return error;
    }

}
