package kingja.com.kingja_bluetooth_demo;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

/**
 * 项目名称：物联网城市防控(警用版)
 * 类描述：TODO
 * 创建人：KingJA
 * 创建时间：2016/4/2 15:26
 * 修改备注：
 */
public class ReceiveThread extends Thread {
    private static final String TAG = "ReceiveThread";
    private BluetoothSocket stock;
    private Handler handler;

    public ReceiveThread(BluetoothSocket stock, Handler handler) {

        this.stock = stock;
        this.handler = handler;
    }

    @Override
    public void run() {
        InputStream inputStream = null;
        try {
            inputStream = stock.getInputStream();
            int count = 0;
            byte[] totleByte = null;
            while (true) {
                if (inputStream.available() != 0) {
                    count = inputStream.available();
                    Log.i(TAG, "count: " + count);
                    byte[] bytes = new byte[count];
                    inputStream.read(bytes);
                    totleByte = BluetoothUtil.ByteArrayCopy(totleByte, bytes);
                    if (totleByte[totleByte.length - 1] == (byte) (0x7e)) {
                        String result = BluetoothUtil.bytesToHexString(totleByte);
                        Log.i(TAG, "result " + result);

                        Message msg = handler.obtainMessage();
                        msg.obj = result;
                        msg.what = 100;
                        handler.sendMessage(msg);
                        break;
                    }
                } else {
                    continue;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i(TAG, this.hashCode() + "ReceiveThread run: 结束 ");
    }
}
