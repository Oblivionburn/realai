package com.oblivionburn.nlp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

public class BluetoothService
{
    private Handler handle_bluetooth; // handler that gets info from Bluetooth service

    private static final String TAG = "REALAI_BLUETOOTH";

    private static final String NAME_SECURE = "RealAI_BluetoothSecure";
    private static final String NAME_INSECURE = "RealAI_BluetoothInsecure";

    private static final UUID MY_UUID_SECURE = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    private static final UUID MY_UUID_INSECURE = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    private final BluetoothAdapter Adapter;

    private AcceptThread SecureAcceptThread;
    private AcceptThread InsecureAcceptThread;
    private ConnectThread ConnectThread;
    private ConnectedThread ConnectedThread;
    private int State;
    private int NewState;

    public static final int STATE_NONE = 0;
    public static final int STATE_LISTEN = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;

    public interface Constants
    {
        int MESSAGE_STATE_CHANGE = 1;
        int MESSAGE_READ = 2;
        int MESSAGE_WRITE = 3;
        int MESSAGE_DEVICE_NAME = 4;
        int MESSAGE_TOAST = 5;

        String DEVICE_NAME = "device_name";
        String TOAST = "toast";
    }

    public BluetoothService(Context context, Handler handler)
    {
        Adapter = BluetoothAdapter.getDefaultAdapter();
        State = STATE_NONE;
        NewState = State;
        handle_bluetooth = handler;
    }

    public synchronized int getState()
    {
        return State;
    }

    public synchronized void start()
    {
        Log.d(TAG, "start");

        // Cancel any thread attempting to make a connection
        if (ConnectThread != null)
        {
            ConnectThread.cancel();
            ConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (ConnectedThread != null)
        {
            ConnectedThread.cancel();
            ConnectedThread = null;
        }

        // Start the thread to listen on a BluetoothServerSocket
        if (SecureAcceptThread == null)
        {
            SecureAcceptThread = new AcceptThread(true);
            SecureAcceptThread.start();
        }

        if (InsecureAcceptThread == null)
        {
            InsecureAcceptThread = new AcceptThread(false);
            InsecureAcceptThread.start();
        }
    }

    public synchronized void connect(BluetoothDevice device, boolean secure)
    {
        Log.d(TAG, "connect to: " + device);

        // Cancel any thread attempting to make a connection
        if (State == STATE_CONNECTING)
        {
            if (ConnectThread != null)
            {
                ConnectThread.cancel();
                ConnectThread = null;
            }
        }

        // Cancel any thread currently running a connection
        if (ConnectedThread != null)
        {
            ConnectedThread.cancel();
            ConnectedThread = null;
        }

        // Start the thread to connect with the given device
        ConnectThread = new ConnectThread(device, secure);
        ConnectThread.start();
    }

    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device, final String socketType)
    {
        Log.d(TAG, "connected, Socket Type:" + socketType);

        // Cancel the thread that completed the connection
        if (ConnectThread != null)
        {
            ConnectThread.cancel();
            ConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (ConnectedThread != null)
        {
            ConnectedThread.cancel();
            ConnectedThread = null;
        }

        // Cancel the accept thread because we only want to connect to one device
        if (SecureAcceptThread != null)
        {
            SecureAcceptThread.cancel();
            SecureAcceptThread = null;
        }

        if (InsecureAcceptThread != null)
        {
            InsecureAcceptThread.cancel();
            InsecureAcceptThread = null;
        }

        // Start the thread to manage the connection and perform transmissions
        ConnectedThread = new ConnectedThread(socket, socketType);
        ConnectedThread.start();

        // Send the name of the connected device back to the UI Activity
        Message msg = handle_bluetooth.obtainMessage(Constants.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.DEVICE_NAME, device.getName());
        msg.setData(bundle);
        handle_bluetooth.sendMessage(msg);
    }

    public synchronized void stop()
    {
        Log.d(TAG, "stop");

        if (ConnectThread != null)
        {
            ConnectThread.cancel();
            ConnectThread = null;
        }

        if (ConnectedThread != null)
        {
            ConnectedThread.cancel();
            ConnectedThread = null;
        }

        if (SecureAcceptThread != null)
        {
            SecureAcceptThread.cancel();
            SecureAcceptThread = null;
        }

        if (InsecureAcceptThread != null)
        {
            InsecureAcceptThread.cancel();
            InsecureAcceptThread = null;
        }

        State = STATE_NONE;
    }

    public void write(byte[] out)
    {
        ConnectedThread r;

        synchronized (this)
        {
            if (State != STATE_CONNECTED) return;
            r = ConnectedThread;
        }

        r.write(out);
    }

    private void connectionFailed()
    {
        // Send a failure message back to the Activity
        Message msg = handle_bluetooth.obtainMessage(Constants.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOAST, "Unable to connect device");
        msg.setData(bundle);
        handle_bluetooth.sendMessage(msg);

        State = STATE_NONE;

        // Start the service over to restart listening mode
        BluetoothService.this.start();
    }

    private void connectionLost()
    {
        // Send a failure message back to the Activity
        Message msg = handle_bluetooth.obtainMessage(Constants.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOAST, "Device connection was lost");
        msg.setData(bundle);
        handle_bluetooth.sendMessage(msg);

        State = STATE_NONE;

        // Start the service over to restart listening mode
        BluetoothService.this.start();
    }

    private class AcceptThread extends Thread
    {
        // The local server socket
        private final BluetoothServerSocket ServerSocket;
        private String SocketType;

        public AcceptThread(boolean secure)
        {
            BluetoothServerSocket tmp = null;
            SocketType = secure ? "Secure" : "Insecure";

            try
            {
                if (secure)
                {
                    tmp = Adapter.listenUsingRfcommWithServiceRecord(NAME_SECURE, MY_UUID_SECURE);
                }
                else
                {
                    tmp = Adapter.listenUsingInsecureRfcommWithServiceRecord(NAME_INSECURE, MY_UUID_INSECURE);
                }
            }
            catch (IOException e)
            {
                Log.e(TAG, "Socket Type: " + SocketType + "listen() failed", e);
            }

            ServerSocket = tmp;
            State = STATE_LISTEN;
        }

        public void run()
        {
            Log.d(TAG, "Socket Type: " + SocketType + " BEGIN AcceptThread" + this);
            setName("AcceptThread" + SocketType);

            BluetoothSocket socket;

            // Listen to the server socket if we're not connected
            while (State != STATE_CONNECTED)
            {
                try
                {
                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    socket = ServerSocket.accept();
                }
                catch (IOException e)
                {
                    Log.e(TAG, "Socket Type: " + SocketType + " accept() failed", e);
                    break;
                }

                // If a connection was accepted
                if (socket != null)
                {
                    synchronized (BluetoothService.this)
                    {
                        switch (State)
                        {
                            case STATE_LISTEN:

                            case STATE_CONNECTING:
                                // Situation normal. Start the connected thread.
                                connected(socket, socket.getRemoteDevice(), SocketType);
                                break;

                            case STATE_NONE:

                            case STATE_CONNECTED:
                                // Either not ready or already connected. Terminate new socket.
                                try
                                {
                                    socket.close();
                                }
                                catch (IOException e)
                                {
                                    Log.e(TAG, "Could not close unwanted socket", e);
                                }
                                break;
                        }
                    }
                }
            }

            Log.i(TAG, "END AcceptThread, socket Type: " + SocketType);
        }

        public void cancel()
        {
            Log.d(TAG, "Socket Type: " + SocketType + " cancel " + this);

            try
            {
                ServerSocket.close();
            }
            catch (IOException e)
            {
                Log.e(TAG, "Socket Type: " + SocketType + " close() of server failed", e);
            }
        }
    }

    private class ConnectThread extends Thread
    {
        private BluetoothSocket Socket;
        private final BluetoothDevice Device;
        private String SocketType;

        public ConnectThread(BluetoothDevice device, boolean secure)
        {
            Device = device;
            BluetoothSocket tmp = null;
            SocketType = secure ? "Secure" : "Insecure";

            try
            {
                if (secure)
                {
                    tmp = device.createRfcommSocketToServiceRecord(MY_UUID_SECURE);
                }
                else
                {
                    tmp = device.createInsecureRfcommSocketToServiceRecord(MY_UUID_INSECURE);
                }
            }
            catch (IOException e)
            {
                Log.e(TAG, "Socket Type: " + SocketType + " create() failed", e);
            }

            Socket = tmp;
            State = STATE_CONNECTING;
        }

        public void run()
        {
            Log.i(TAG, "BEGIN ConnectThread SocketType: " + SocketType);
            setName("ConnectThread" + SocketType);

            // Make a connection to the BluetoothSocket
            try
            {
                Socket.connect();
            }
            catch (IOException e)
            {
                try
                {
                    Log.e(TAG, "Socket connect failed, trying fallback...");

                    Class<?> clazz = Socket.getRemoteDevice().getClass();
                    Class<?>[] paramTypes = new Class<?>[]{Integer.TYPE};
                    Method m = clazz.getMethod("createRfcommSocket", paramTypes);
                    Object[] params = new Object[]{2};
                    Socket = (BluetoothSocket) m.invoke(Socket.getRemoteDevice(), params);

                    Socket.connect();
                }
                catch(NoSuchMethodException nm)
                {
                    Log.e(TAG, nm.getMessage());
                }
                catch(IllegalAccessException ia)
                {
                    Log.e(TAG, ia.getMessage());
                }
                catch(InvocationTargetException it)
                {
                    Log.e(TAG, it.getMessage());
                }
                catch (IOException e1)
                {
                    try
                    {
                        Log.e(TAG, "Socket connect failed.");
                        Socket.close();
                    }
                    catch (IOException e2)
                    {
                        Log.e(TAG, "unable to close() " + SocketType + " socket during connection failure", e2);
                    }
                    connectionFailed();
                }

                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (BluetoothService.this)
            {
                ConnectThread = null;
            }

            connected(Socket, Device, SocketType);
        }

        public void cancel()
        {
            try
            {
                Socket.close();
            }
            catch (IOException e)
            {
                Log.e(TAG, "close() of connect " + SocketType + " socket failed", e);
            }
        }
    }

    private class ConnectedThread extends Thread
    {
        private final BluetoothSocket Socket;
        private final InputStream InStream;
        private final OutputStream OutStream;

        public ConnectedThread(BluetoothSocket socket, String socketType)
        {
            Log.d(TAG, "create ConnectedThread: " + socketType);
            Socket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try
            {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            }
            catch (IOException e)
            {
                Log.e(TAG, "temp sockets not created", e);
            }

            InStream = tmpIn;
            OutStream = tmpOut;
            State = STATE_CONNECTED;
        }

        public void run()
        {
            Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];
            int bytes;

            while (State == STATE_CONNECTED)
            {
                try
                {
                    bytes = InStream.read(buffer);

                    handle_bluetooth.obtainMessage(Constants.MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                }
                catch (IOException e)
                {
                    Log.e(TAG, "disconnected", e);
                    connectionLost();
                    break;
                }
            }
        }

        public void write(byte[] buffer)
        {
            try
            {
                OutStream.write(buffer);

                handle_bluetooth.obtainMessage(Constants.MESSAGE_WRITE, -1, -1, buffer).sendToTarget();
            }
            catch (IOException e)
            {
                Log.e(TAG, "Exception during write", e);
            }
        }

        public void cancel()
        {
            try
            {
                Socket.close();
            }
            catch (IOException e)
            {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }
}
