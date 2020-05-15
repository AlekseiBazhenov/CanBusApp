package ru.test.testjni.canbus;

import java.io.IOException;
import java.util.Arrays;

import ru.test.testjni.CanSocket;

public class CanUtils {

    private static CanSocket socket;
    private static CanSocket.CanInterface canif;

    public static void initCan0() throws IOException {
        socket = new CanSocket(CanSocket.Mode.RAW);
        canif = new CanSocket.CanInterface(socket, "can0");
        socket.bind(canif);
    }

    public static CanSocket.CanFrame revCan0Data() throws IOException {
        return socket.recv();
    }

    public static void sendCan0Data(int address, byte[] data) {
        try {
            CanSocket.CanId id = new CanSocket.CanId(address);
            int i = 0;
            byte[] currentData;
            for (; i * 8 < data.length - 8; i++) {
                currentData = Arrays.copyOfRange(data, i * 8, (i + 1) * 8);
                socket.send(new CanSocket.CanFrame(canif, id, currentData));
            }
            currentData = Arrays.copyOfRange(data, i * 8, data.length);
            socket.send(new CanSocket.CanFrame(canif, id, currentData));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
