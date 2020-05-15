package ru.test.testjni.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;

import ru.test.testjni.CanSocket;
import ru.test.testjni.Consts;
import ru.test.testjni.MainActivity;
import ru.test.testjni.R;
import ru.test.testjni.callback.ResultReceiverCallback;
import ru.test.testjni.canbus.BitUtils;
import ru.test.testjni.canbus.CanUtils;
import ru.test.testjni.canbus.response.CanBusResponse;
import ru.test.testjni.canbus.response.FrameId;
import ru.test.testjni.canbus.response.IndicatorStateId;

// TODO: intent service
public class CanService extends Service {

    private static final String ACTION_LISTEN_CAN_INTERFACE = "ru.test.testjni.action.LISTEN_CAN_INTERFACE";
    private static final String EXTRA_RESULT_RECEIVER = "ru.test.testjni.extra.RESULT_RECEIVER";

    private ResultReceiver resultReceiver;

    public CanService() {
    }

    public static void listenCanInterface(Context context, ResultReceiverCallback<CanBusResponse> callback) {
        CanDataResultReceiver<CanBusResponse> resultReceiver = new CanDataResultReceiver<>(new Handler(context.getMainLooper()));
        resultReceiver.setReceiverCallback(callback);

        Intent intent = new Intent(context, CanService.class);
        intent.setAction(ACTION_LISTEN_CAN_INTERFACE);
        intent.putExtra(EXTRA_RESULT_RECEIVER, resultReceiver);
        context.startService(intent);
    }

    private Runnable listeningCanBusRunnable = new Runnable() {
        @Override
        public void run() {
            startCanBusListening();
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        resultReceiver = intent.getParcelableExtra(EXTRA_RESULT_RECEIVER);

        final String action = intent.getAction();
        if (ACTION_LISTEN_CAN_INTERFACE.equals(action)) {
            try {
                CanUtils.initCan0();
            } catch (IOException e) {
                e.printStackTrace();
            }
            new Thread(listeningCanBusRunnable).start();
        }

        return Service.START_STICKY;
    }

    private void startCanBusListening() {
        while (true) {
            try {
                CanSocket.CanFrame frame = CanUtils.revCan0Data();
                parseDataFrame(frame);
            } catch (IOException e) {
                int code = CanDataResultReceiver.RESULT_CODE_ERROR;
                sendDataToUI(code, "0", 0, null, e.getLocalizedMessage());
            }
        }
    }

    private void parseDataFrame(CanSocket.CanFrame frame) {
        final String frameId = Integer.toHexString(frame.getCanId().getCanId_EFF());
        final byte[] bytes = frame.getData();
        if (frameId.equals(FrameId.OTHER.getValue())) {
            // для 105-го frameData является типом long (по протоколу)
            long frameData = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getLong();
            // 0-2 байты - индикаторы, 3 и 4 байт - скорость вращения тарелок
            long speed = frameData >> 24 & 0xFFFF;

            int resultCode = CanDataResultReceiver.RESULT_CODE_OK;
            HashMap<IndicatorStateId, Boolean> indicators = parseIndicators(frameData);
            sendDataToUI(resultCode, frameId, speed, indicators, "");

//            пробное изменение параметров гидропривода (отправка данных в шину)
//            int state = Consts.GATE | Consts.PLATES;
//            int sp = 100; // 2 первых байта, 3 - флаги состояний state
//            byte[] send = new byte[]{(byte) ((sp) & 0xff), (byte) (sp >> 8 & 0xff), (byte) 0x03};
//            CanUtils.sendCan0Data(0x201, send);
        } else {
            short frameData = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getShort();
            int resultCode = CanDataResultReceiver.RESULT_CODE_OK;
            sendDataToUI(resultCode, frameId, frameData, null, "");
        }
    }

    @NonNull
    private HashMap<IndicatorStateId, Boolean> parseIndicators(long frameData) {
        long indicators = frameData & 0xFFFFFF;
        HashMap<IndicatorStateId, Boolean> indicatorsMap = new HashMap<>();
        for (IndicatorStateId stateId : IndicatorStateId.values()) {
            indicatorsMap.put(stateId, BitUtils.isBitSet(indicators, stateId.getValue()));
        }
        return indicatorsMap;
    }

    private void sendDataToUI(
            int code,
            String frameId,
            long frameData,
            HashMap<IndicatorStateId, Boolean> indicators,
            String errorMessage
    ) {
        if (resultReceiver != null) {
            CanBusResponse response = new CanBusResponse(
                    FrameId.fromString(frameId),
                    frameData, indicators,
                    errorMessage);

            if (response.getFrameId() == null) {
                Log.d("CanService", "sendDataToUI() frameId null, unsupported frameId");
            } else {
                resultReceiver.send(code, getBundle(response));
            }
        }
    }

    @NonNull
    private Bundle getBundle(CanBusResponse response) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(CanDataResultReceiver.PARAM_RESULT, response);
        bundle.putSerializable(CanDataResultReceiver.PARAM_EXCEPTION, response.getError());
        return bundle;
    }


    private void createNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        PendingIntent contentIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default_chanel");
        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("title")
                .setContentText("text");
        Notification notification = builder.build();

        startForeground(101, notification);
    }
}
