package aalto.comnet.thepreciousfoodintake;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

public class BluetoothManager extends Service{
	private String TAG = "BluetoothManager";
	private String WristbandBTName = "aaa";
	
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker;

    @Override
    public void onCreate(){

    }
    
    public int onStartCommand(Intent intenc, int flags, int idArranque) {
        findBT();
        openBT();
		return START_STICKY;
	}
    
	 public void onDestroy() {
		 closeBT();
	 }
	 
	 @Override
    public IBinder onBind(Intent intencion) {
          return null;
    }

    void findBT()
    {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null)
        {
            Log.e(TAG,"No bluetooth adapter available");
        }

        if(!mBluetoothAdapter.isEnabled())
        {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBluetooth.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(enableBluetooth);
            //aalto.comnet.thepreciousfoodintake.MainActivity.startActivity(enableBluetooth);
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0)
        {
            for(BluetoothDevice device : pairedDevices)
            {
                if(device.getName().equals(WristbandBTName))
                {
                    mmDevice = device;
                    break;
                }
            }
        }
        Log.i(TAG,"Bluetooth Device Found");
    }

    void openBT()
    {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
        try{
        	mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
        	mmSocket.connect();
	        mmOutputStream = mmSocket.getOutputStream();
	        mmInputStream = mmSocket.getInputStream();
        }catch (Exception e){
        	Log.e(TAG,"openBT error",e);
        }

        beginListenForData();
        Log.i(TAG,"Bluetooth Opened");
    }

    void beginListenForData()
    {
//        final Handler handler = new Handler();
//        final byte delimiter = 10; //This is the ASCII code for a newline character

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable()
        {
            public void run()
            {
                while(!Thread.currentThread().isInterrupted() && !stopWorker)
                {
                    try
                    {
                        int bytesAvailable = mmInputStream.available();
                        if(bytesAvailable > 0)
                        {
                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            writeByteArrayInExternalFile(packetBytes, "BTdata2.txt");
                        }
                    }
                    catch (IOException ex)
                    {
                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
    }

    void sendData(String msg) throws IOException
    {	        
        msg += "\n";
        mmOutputStream.write(msg.getBytes());

    }

    void closeBT()
    {
        stopWorker = true;
        try{
	        mmOutputStream.close();
	        mmInputStream.close();
	        mmSocket.close();
        }catch (Exception e){
        	Log.e(TAG,"closeBT error",e);
        }
        Log.i(TAG,"Bluetooth Closed");
    }
    
    
    
    /**
     *  Writes a string line in a file in external memory
     * filename specifies both location and name of the file
     * example: filename="/folder1/myfile.txt"
     */
    public void writeStingInExternalFile(String data, String fileName){
        try {
            if(isExternalStorageWritable()){
                File ext_storage = Environment.getExternalStorageDirectory();
                String extPath = ext_storage.getPath();
                File folder = new File(extPath+"/precious");
                boolean success = false;
                if(!folder.exists())
                    success = folder.mkdir();
                if(folder.exists() || success){
                    File file = new File (folder, fileName);
                    if(!file.exists())
                        file.createNewFile();
                    FileOutputStream f = new FileOutputStream(file, true);
                    String texto = data + "\n";
                    f.write(texto.getBytes());
                    f.close();
                    Log.i("File " + fileName, "Stored " + data);
                }
                else Log.e("ActivityRecognition","unable to create folder or file");
            }
        } catch (Exception e) {
            Log.e("Error opening file", e.getMessage(), e);
        }
    }
    /**
     *  Writes a byte array line in a file in external memory
     * filename specifies both location and name of the file
     * example: filename="/folder1/myfile.txt"
     */
    public void writeByteArrayInExternalFile(byte[] data, String fileName){
        try {
            if(isExternalStorageWritable()){
                File ext_storage = Environment.getExternalStorageDirectory();
                String extPath = ext_storage.getPath();
                File folder = new File(extPath+"/precious");
                boolean success = false;
                if(!folder.exists())
                    success = folder.mkdir();
                if(folder.exists() || success){
                    File file = new File (folder, fileName);
                    if(!file.exists())
                        file.createNewFile();
                    FileOutputStream f = new FileOutputStream(file, true);
                    f.write(data);
                    f.close();
                    Log.i("File " + fileName, "Stored " + data.toString());
                }
                else Log.e("ActivityRecognition","unable to create folder or file");
            }
        } catch (Exception e) {
            Log.e("Error opening file", e.getMessage(), e);
        }
    }
    /**
     *  Checks if external storage is available for read and write
     */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state));
    }
}
