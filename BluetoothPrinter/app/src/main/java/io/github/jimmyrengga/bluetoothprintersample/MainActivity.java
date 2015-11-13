package io.github.jimmyrengga.bluetoothprintersample;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    Button btnPrint;
    ImageButton btnConnect, btnSearch;
    EditText txtPrint;
    Spinner spDevice;
    BluetoothAdapter bluetoothAdapter;
    ProgressDialog scanningProgress;
    private ArrayList<BluetoothDevice> deviceList = new ArrayList<BluetoothDevice>();
    private static final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
    private BluetoothSocket mSocket;
    private OutputStream mOutputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPrint    = (Button) findViewById(R.id.button);
        txtPrint    = (EditText) findViewById(R.id.editText);
        spDevice    = (Spinner) findViewById(R.id.sp_device);
        btnConnect  = (ImageButton) findViewById(R.id.btn_connect);
        btnSearch   = (ImageButton) findViewById(R.id.btn_enable);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        scanningProgress = new ProgressDialog(this);
        scanningProgress.setMessage("Scanning...");
        scanningProgress.setCancelable(false);

        if(bluetoothAdapter == null) {
            showToast("Bluetooth isn't support in this device");
        } else {
            if(!bluetoothAdapter.isEnabled()) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

                startActivityForResult(intent, 1000);
            } else {

            }
        }

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connect();
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetoothAdapter.startDiscovery();
            }
        });

        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    sendData(charArrToBytesASCII(EpsonT5Command.RESET));
                    sendData(charArrToBytesASCII(EpsonT5Command.BOLD_ON));
                    sendData(stringToBytesASCII("Hello World !"));
//                    sendData(charArrToBytesASCII(EpsonT5Command.RESET));
//                    sendData(charToBytesASCII(EpsonT5Command.LF));
                    sendData(stringToBytesASCII("++++++++++"));
//                    sendData(charToBytesASCII(EpsonT5Command.LF));
                    sendData(charArrToBytesASCII(EpsonT5Command.BOLD_OFF));
                    sendData(stringToBytesASCII("Hello World !"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        IntentFilter filter = new IntentFilter();

        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);

        registerReceiver(receiver, filter);
    }

    public static byte[] charArrToBytesASCII(char[] ch) {
        return new String(ch).getBytes(StandardCharsets.UTF_8);
    }

    public static byte[] charToBytesASCII(char ch) {
        return String.valueOf(ch).getBytes(StandardCharsets.UTF_8);
    }

    public static byte[] stringToBytesASCII(String str) {
        char[] buffer = str.toCharArray();
        byte[] b = new byte[buffer.length];
        for (int i = 0; i < b.length; i++) {
            b[i] = (byte) buffer[i];
        }
        return b;
    }

    public void sendData(byte[] msg) throws Exception {
        if (mSocket == null) {
            throw new Exception("Socket is not connected, try to call connect() first");
        }

        try {
            mOutputStream.write(msg);
            mOutputStream.flush();

        } catch(Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    private void createBond(BluetoothDevice device) throws Exception {

        try {
            Class<?> cl 	= Class.forName("android.bluetooth.BluetoothDevice");
            Class<?>[] par 	= {};

            Method method 	= cl.getMethod("createBond", par);

            method.invoke(device);

        } catch (Exception e) {
            e.printStackTrace();

            throw e;
        }
    }

    private void connect() {
        if (deviceList == null || deviceList.size() == 0) {
            return;
        }

        BluetoothDevice device = deviceList.get(spDevice.getSelectedItemPosition());

        if (device.getBondState() == BluetoothDevice.BOND_NONE) {
            try {
                createBond(device);
            } catch (Exception e) {
                showToast("Failed to pair device");

                return;
            }
        }

        connect(device);
    }

    public void connect(BluetoothDevice device) {
        new ConnectTask(device).execute();
    }

    public class ConnectTask extends AsyncTask<URL, Integer, Long> {
        BluetoothDevice device;
        String error = "";

        public ConnectTask(BluetoothDevice device) {
            this.device = device;
        }

        protected void onPreExecute() {
            showToast("Connecting...");
        }

        protected Long doInBackground(URL... urls) {
            long result = 0;

            try {
                mSocket			= device.createRfcommSocketToServiceRecord(UUID.fromString(SPP_UUID));
                mSocket.connect();
                mOutputStream	= mSocket.getOutputStream();

                result = 1;
            } catch (IOException e) {
                e.printStackTrace();

                error = e.getMessage();
            }

            return result;
        }

        protected void onPostExecute(Long result) {
            showToast("Connected to printer");
        }
    }

    private void enabledView(Boolean isEnabled) {
        btnSearch.setEnabled(isEnabled);
        btnConnect.setEnabled(isEnabled);
        spDevice.setEnabled(isEnabled);
        btnPrint.setEnabled(isEnabled);
        txtPrint.setEnabled(isEnabled);
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                final int state 	= intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                if (state == BluetoothAdapter.STATE_ON) {
                    enabledView(true);
                } else if (state == BluetoothAdapter.STATE_OFF) {
                    enabledView(false);
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                deviceList = new ArrayList<BluetoothDevice>();

                scanningProgress.show();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                scanningProgress.dismiss();

                updateDeviceList();
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                deviceList.add(device);
            }
        }
    };

    private String[] getArray(ArrayList<BluetoothDevice> data) {
        String[] list = new String[0];

        if (data == null) return list;

        int size	= data.size();
        list		= new String[size];

        for (int i = 0; i < size; i++) {
            list[i] = data.get(i).getName();
        }

        return list;
    }

    private void updateDeviceList() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item, getArray(deviceList));

        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

        spDevice.setAdapter(adapter);
        spDevice.setSelection(0);
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

}
