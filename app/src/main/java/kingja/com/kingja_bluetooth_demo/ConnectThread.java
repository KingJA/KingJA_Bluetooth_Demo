package kingja.com.kingja_bluetooth_demo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

/**
 * 项目名称：物联网城市防控(警用版)
 * 类描述：TODO
 * 创建人：KingJA
 * 创建时间：2016/4/2 15:23
 * 修改备注：
 */
public class ConnectThread extends Thread {
    private static final String TAG = "ConnectThread";
    private BluetoothDevice device;
    private BluetoothAdapter adapterr;
    private final String uuid;
    private final Handler handler;
    private BluetoothSocket stock;

    public ConnectThread(BluetoothDevice device, BluetoothAdapter adapterr, String uuid, Handler handler) {
        this.device = device;
        this.adapterr = adapterr;
        this.uuid = uuid;
        this.handler = handler;
        try {
            stock = device.createRfcommSocketToServiceRecord(UUID.fromString(uuid));


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        if (adapterr.isDiscovering()) {
            adapterr.cancelDiscovery();
        }
        try {
            stock.connect();
            ReceiveThread receiveThread = new ReceiveThread(stock, handler);
            receiveThread.start();
//            stock.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i(TAG, this.hashCode() + "ConnectThread run: 结束 ");
    }

    public void cancel() {
        try {
            stock.close();
        } catch (IOException e) {
            Log.e(TAG, "close() of connect " + stock + " socket failed", e);
        }
    }
}
