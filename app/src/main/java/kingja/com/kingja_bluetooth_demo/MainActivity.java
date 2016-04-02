package kingja.com.kingja_bluetooth_demo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    /**
     * UUID相当于Socket的端口，Mac相当于Socket的IP地址
     */

    private Button btn_bluetooth;
    private ListView lv;
    private BluetoothAdapter defaultAdapter;
    private List<String> devices = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;
    private BluetoothDevice bluetoothDevice;
    private final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";//蓝牙模式串口服务
    private final String NAME = "TDR_2.0_08EA";
//    private static final String SPP_UUID = "00001104-0000-1000-8000-00805F9B34FB";//信息同步服务
//    private static final String SPP_UUID = "00001106-0000-1000-8000-00805F9B34FB";//文件传输服务

    
    private android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 100:
                    String result = (String) msg.obj;
                    Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
                    startConnect();
                    Log.i(TAG, this.hashCode() + "开启新的线程");
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 开启连接线程
     */
    private void startConnect() {
        // Cancel the thread that completed the connection
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }
        connectThread = new ConnectThread(bluetoothDevice, defaultAdapter, SPP_UUID, handler);
        connectThread.start();
    }

    private ConnectThread connectThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv = (ListView) findViewById(R.id.lv);
        btn_bluetooth = (Button) findViewById(R.id.btn_bluetooth);
        /**
         * 获取并打开蓝牙设备
         */
        defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter == null) {
            Toast.makeText(MainActivity.this, "没有蓝牙设备", Toast.LENGTH_SHORT).show();
        } else if (!defaultAdapter.enable()) {
            defaultAdapter.enable();
        }
        /**
         * 将已经配对的设备存入列表
         */
        Set<BluetoothDevice> bondedDevices = defaultAdapter.getBondedDevices();
        if (bondedDevices.size() > 0) {
            for (BluetoothDevice device : bondedDevices) {
                devices.add(device.getAddress() + "#" + device.getName());
            }
        }
/**
 * 接收蓝牙搜索 找到和完成广播
 */
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(receiver, intentFilter);
        intentFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(receiver, intentFilter);


        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, devices);
        lv.setAdapter(arrayAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = arrayAdapter.getItem(position);
                String macAddress = item.split("#")[0];
                Log.i(TAG, "macAddress: " + macAddress);
                if (defaultAdapter.isDiscovering()) {
                    defaultAdapter.cancelDiscovery();
                }
                if (bluetoothDevice == null) {
                    bluetoothDevice = defaultAdapter.getRemoteDevice(macAddress);
                    startConnect();
                }
            }
        });


        btn_bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (defaultAdapter.isDiscovering()) {
                    defaultAdapter.cancelDiscovery();
                }
                defaultAdapter.startDiscovery();
                Toast.makeText(MainActivity.this, "Searching!", Toast.LENGTH_SHORT).show();

            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (defaultAdapter != null && defaultAdapter.isDiscovering()) {
            defaultAdapter.cancelDiscovery();
        }
        unregisterReceiver(receiver);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    devices.add(device.getAddress() + "#" + device.getName());
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Toast.makeText(MainActivity.this, "finished", Toast.LENGTH_SHORT).show();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        arrayAdapter.notifyDataSetChanged();
                    }
                });

            }

        }
    };
}
