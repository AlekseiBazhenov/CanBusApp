package ru.test.testjni;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import ru.test.testjni.callback.CanBusDataReceiverCallback;
import ru.test.testjni.callback.ResultReceiverCallback;
import ru.test.testjni.canbus.response.CanBusResponse;
import ru.test.testjni.canbus.response.IndicatorStateId;
import ru.test.testjni.service.CanService;
import ru.test.testjni.speedviews.SpeedView;

public class MainActivity extends Activity {

    static {
        System.loadLibrary("CanSocket");
    }

    private SpeedView speedView;
    private SpeedView tachometerView;
    private ImageView parkingBrake;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        speedView = findViewById(R.id.speedView);
        tachometerView = findViewById(R.id.tachometerView);
        parkingBrake = findViewById(R.id.parking_brake);


        ResultReceiverCallback<CanBusResponse> receiverCallback = new CanBusDataReceiverCallback(this);
        CanService.listenCanInterface(this, receiverCallback);
    }

    public void updateView(CanBusResponse response) {

        switch (response.getFrameId()) {
            case CAR_SPEED:
                if (response.getFrameData() < 150) {
                    Log.d("Скорость авто - ", String.valueOf(response.getFrameData()));
                    speedView.setSpeedAt(response.getFrameData());
                }
                break;
            case ENGINE_SPEED:
                if (response.getFrameData() < 6000) {
                    Log.d("Обороты двиг. - ", String.valueOf(response.getFrameData()));
                    tachometerView.setSpeedAt(response.getFrameData());
                }
                break;
            case FUEL_LEVEL:
                Log.d("Уровень топлива - ", String.valueOf(response.getFrameData()));
                break;
            case ENGINE_TEMPERATURE:
                Log.d("Температура двиг. - ", String.valueOf(response.getFrameData()));
                break;
            case OTHER:
                Log.d("Скорость тарелок - ", String.valueOf(response.getFrameData()));
                Log.d("Индикаторы - ", response.getIndicators().toString());

                parkingBrake.setBackgroundColor(response.getIndicators().get(IndicatorStateId.GATE)
                        ? ContextCompat.getColor(this, R.color.colorAccent)
                        : ContextCompat.getColor(this, R.color.transparent));
                break;
        }
    }

    public void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}
