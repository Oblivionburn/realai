package com.oblivionburn.nlp;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
//import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnItemSelectedListener
{
    private static final String TAG = "REALAI";

    private Context context;

    private DataOutputStream os;
    private BluetoothService blueService;
    private BluetoothAdapter blueAdapter;

    private TextToSpeech speech;
    //private static AudioManager audioManager;

    private File Brain_dir;
    private File History_dir;
    private File Thought_dir;

    static int int_Time = 10000;
    static boolean bl_DelayForever = false;

    private LiteText Output = null;
    private LiteText Input = null;
    private LiteText txt_WordFix = null;
    private Spinner sp_WordFix = null;
    private Button btn_WordFix = null;
    private Button btn_Menu = null;
    private Button btn_Encourage = null;
    private Button btn_Discourage = null;
    private ImageView img_Face = null;

    private MenuItem mi_Connect = null;
    private MenuItem mi_Disconnect = null;
    private MenuItem mi_NewSession = null;
    private MenuItem mi_Thoughts = null;
    private MenuItem mi_Tips = null;
    private MenuItem mi_WordFix = null;
    private MenuItem mi_SetDelay = null;
    private MenuItem mi_SetResponse = null;
    private MenuItem mi_EraseBrain = null;
    private MenuItem mi_Advanced = null;
    private MenuItem mi_Exit = null;

    private int int_Delay = 0;
    private int delay_selection = 0;
    private int response_selection = 0;
    private int wordfix_selection = 0;
    private int delay_respond = 0;

    private boolean bl_Typing = false;
    private boolean bl_Thought = false;
    private boolean bl_WordFix = false;
    private boolean bl_Delay = false;
    private boolean bl_Responses = false;
    private boolean bl_Tips = false;
    private boolean bl_Encourage_Pressed = false;
    private boolean bl_Discourage_Pressed = false;
    private boolean bl_Bored = false;
    private boolean bl_Thinking = false;
    private boolean bl_Bluetooth = false;
    private boolean bl_Connected = false;

    private static Handler handle_thinking;
    private static Handler handle_timer;
    private static Handler handle_responding;

    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    private ListView ConversationView;
    private String ConnectedDeviceName = null;
    private StringBuffer OutStringBuffer;
    private String received;

    private View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        handle_thinking = new Handler();
        handle_timer = new Handler();
        handle_responding = new Handler();

        context = getApplicationContext();
        Output = new LiteText(context);
        Input = new LiteText(context);
        txt_WordFix = new LiteText(context);
        sp_WordFix = new Spinner(context);
        btn_WordFix = new Button(context);
        btn_Menu = new Button(context);
        btn_Encourage = new Button(context);
        btn_Discourage = new Button(context);
        img_Face = new ImageView(context);

        Input = findViewById(R.id.txt_Input);

        Output = findViewById(R.id.txt_Output);
        Output.setMaxLines(Integer.MAX_VALUE);

        btn_Menu = findViewById(R.id.btn_Menu);
        btn_Encourage = findViewById(R.id.btn_Encourage);
        btn_Discourage = findViewById(R.id.btn_Discourage);
        btn_WordFix = findViewById(R.id.btn_WordFix);

        sp_WordFix = findViewById(R.id.sp_WordFix);
        sp_WordFix.setOnItemSelectedListener(this);
        txt_WordFix = findViewById(R.id.txt_WordFix);

        img_Face = findViewById(R.id.img_Face);

        Brain_dir = new File(getExternalFilesDir(null).getAbsolutePath() + "/Brain/" );
        History_dir = new File(getExternalFilesDir(null).getAbsolutePath() + "/Brain/History/" );
        Thought_dir = new File(getExternalFilesDir(null).getAbsolutePath() + "/Brain/Thoughts/" );

        speech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener()
        {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    speech.setLanguage(Locale.US);
                }
            }
        });

        //audioManager = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);

        blueAdapter = BluetoothAdapter.getDefaultAdapter();
        if (blueAdapter != null)
        {
            if (blueAdapter.isEnabled())
            {
                Log.d(TAG, "Bluetooth enabled");

                try
                {
                    OutStringBuffer = new StringBuffer();

                    blueService = new BluetoothService(context, handle_bluetooth);
                    blueService.start();
                    connectDevice();

                    bl_Bluetooth = true;
                    Toast.makeText(context, "Found bluetooth connection.", Toast.LENGTH_SHORT).show();
                }
                catch (Exception e)
                {
                    Toast.makeText(context, "Failed to establish bluetooth connection.", Toast.LENGTH_SHORT).show();
                }
            }
        }

        createBrain(context);
        createListeners();

        DisplayTips();
    }

    private void createBrain(Context context)
    {
        Data.initData(context);

        if (!Brain_dir.exists())
        {
            Brain_dir.mkdirs();
        }

        File file = new File(Brain_dir, "Config.ini");
        if (!file.exists())
        {
            Data.initConfig();
        }
        else
        {
            String config = Data.getDelay();
            switch (config)
            {
                case "10 seconds":
                    int_Time = 10000;
                    delay_selection = 0;
                    break;

                case "20 seconds":
                    int_Time = 20000;
                    delay_selection = 1;
                    break;

                case "30 seconds":
                    int_Time = 30000;
                    delay_selection = 2;
                    break;

                case "Infinite":
                    delay_selection = 3;
                    bl_DelayForever = true;
                    break;
            }

            String advanced = Data.getAdvanced();
            switch (advanced)
            {
                case "false":
                    Logic.Advanced = false;
                    Disable_AdvancedStuff();
                    break;

                case "true":
                    Logic.Advanced = true;
                    Enabled_AdvancedStuff();
                    break;
            }

            String topic = Data.getTopicBased();
            switch (topic)
            {
                case "true":
                    Logic.TopicBased = true;
                    break;

                case "false":
                    Logic.TopicBased = false;
                    break;
            }

            String condition = Data.getConditionBased();
            switch (condition)
            {
                case "true":
                    Logic.ConditionBased = true;
                    break;

                case "false":
                    Logic.ConditionBased = false;
                    break;
            }

            String procedural = Data.getProceduralBased();
            switch (procedural)
            {
                case "true":
                    Logic.ProceduralBased = true;
                    break;

                case "false":
                    Logic.ProceduralBased = false;
                    break;
            }

            String speech = Data.getSpeech();
            switch (speech)
            {
                case "true":
                    Logic.Speech = true;
                    break;

                case "false":
                    Logic.Speech = false;
                    break;
            }
        }

        file = new File(Brain_dir, "Words.txt");
        if (!file.exists())
        {
            try
            {
                file.createNewFile();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        file = new File(Brain_dir, "InputList.txt");
        if (!file.exists())
        {
            try
            {
                file.createNewFile();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        DateFormat f = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
        String currentDate = f.format(new Date());

        file = new File(History_dir, currentDate + ".txt");
        if (!History_dir.exists())
        {
            History_dir.mkdirs();
            if (!file.exists())
            {
                try
                {
                    file.createNewFile();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }

        file = new File(Thought_dir, currentDate + ".txt");
        if (!Thought_dir.exists())
        {
            Thought_dir.mkdirs();
            if (!file.exists())
            {
                try
                {
                    file.createNewFile();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    private void createListeners()
    {
        Input.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void afterTextChanged(Editable s)
            {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                img_Face.setImageResource(R.drawable.face_neutral);

                if (Input.getText().toString().equals(""))
                {
                    bl_Typing = false;
                    startTimer();
                    startThinking();
                }
                else
                {
                    bl_Typing = true;
                    Logic.Initiation = false;
                    stopTimer();
                    stopThinking();
                }
            }
        });

        Input.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (actionId == EditorInfo.IME_ACTION_DONE ||
                    actionId == EditorInfo.IME_ACTION_NEXT)
                {
                    onSend(v);
                    return true;
                }
                return false;
            }
        });

        btn_Encourage.setOnTouchListener(new OnTouchListener()
        {
            private final Handler handler = new Handler();
            private final Runnable runnable = new Runnable()
            {
                public void run()
                {
                    if(!bl_Encourage_Pressed)
                    {
                        handler.removeCallbacks(runnable);
                        img_Face.setImageResource(R.drawable.face_neutral);
                    }
                    else
                    {
                        bl_Encourage_Pressed = false;
                        handler.postDelayed(runnable, 250);
                    }
                }
            };

            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        if(event.getAction() == MotionEvent.ACTION_DOWN)
                        {
                            img_Face.setImageResource(R.drawable.face_encourage);
                            bl_Encourage_Pressed = true;
                        }

                        if(event.getAction() == MotionEvent.ACTION_UP)
                        {
                            handler.postDelayed(runnable, 250);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        v.performClick();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        btn_Discourage.setOnTouchListener(new OnTouchListener()
        {
            private final Handler handler = new Handler();
            private final Runnable runnable = new Runnable()
            {
                public void run()
                {
                    if(!bl_Discourage_Pressed)
                    {
                        handler.removeCallbacks(runnable);
                        img_Face.setImageResource(R.drawable.face_neutral);
                    }
                    else
                    {
                        bl_Discourage_Pressed = false;
                        handler.postDelayed(runnable, 250);
                    }
                }
            };

            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        if(event.getAction() == MotionEvent.ACTION_DOWN)
                        {
                            img_Face.setImageResource(R.drawable.face_discourage);
                            bl_Discourage_Pressed = true;
                        }

                        if(event.getAction() == MotionEvent.ACTION_UP)
                        {
                            handler.postDelayed(runnable, 250);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        v.performClick();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public void onDestroy()
    {
        stopTimer();

        handle_responding.removeCallbacks(Respond);

        stopThinking();

        if (blueService != null)
        {
            blueService.stop();
        }

        if (speech != null)
        {
            speech.stop();
            speech.shutdown();
        }
        //audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);

        android.os.Process.killProcess(android.os.Process.myPid());

        super.onDestroy();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (blueService != null)
        {
            if (blueService.getState() == BluetoothService.STATE_NONE)
            {
                blueService.start();
            }
        }
    }

    //Back Button
    @Override
    public void onBackPressed()
    {
        if (bl_Thought)
        {
            CloseThought();
        }
        else if (bl_WordFix ||
                 bl_Delay)
        {
            CloseWordFix();
        }
        else if (bl_Tips)
        {
            CloseTips();
        }
        else
        {
            Acknowledge_Exit();
        }
    }

    private void SendMessage(String message)
    {
        if (blueService.getState() != BluetoothService.STATE_CONNECTED)
        {
            return;
        }

        if (message.length() > 0)
        {
            Log.d(TAG, "Bluetooth sent: " + message);

            byte[] send = message.getBytes();
            blueService.write(send);
            OutStringBuffer.setLength(0);
        }
    }

    private void ReceiveMessage(String message)
    {
        if (message.length() > 0)
        {
            Log.d(TAG, "Bluetooth received: " + message);

            if (message.equals("RealAI Connect"))
            {
                if (!bl_Connected)
                {
                    Toast.makeText(context, "AIs connected.", Toast.LENGTH_SHORT).show();
                    SendMessage("RealAI Connect");
                    bl_Connected = true;

                    Input.setEnabled(false);
                    btn_Encourage.setEnabled(false);
                    btn_Discourage.setEnabled(false);
                    Input.setText("");
                }
            }
            else if (message.equals("RealAI Disconnect"))
            {
                if (bl_Connected)
                {
                    Toast.makeText(context, "AIs disconnected.", Toast.LENGTH_SHORT).show();
                    SendMessage("RealAI Disconnect");
                    bl_Connected = false;

                    Input.setEnabled(true);
                    btn_Encourage.setEnabled(true);
                    btn_Discourage.setEnabled(true);

                    handle_responding.removeCallbacks(Respond);
                    startTimer();
                }
            }
            else
            {
                received = message;

                List<String> history = Data.getHistory();
                history.add("Other AI: " + received);

                Data.saveHistory(history);
                Util.ClearLeftovers(context);

                Output.post(ScrollHistory);

                delay_respond = 0;
                handle_responding.post(Respond);
                stopTimer();
            }
        }
    }

    private final Handler handle_bluetooth = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case BluetoothService.Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1)
                    {
                        case BluetoothService.STATE_CONNECTED:
                            Toast.makeText(context, "Bluetooth connection established.", Toast.LENGTH_SHORT).show();
                            break;

                        case BluetoothService.STATE_CONNECTING:
                            Toast.makeText(context, "Bluetooth connecting...", Toast.LENGTH_SHORT).show();
                            break;

                        case BluetoothService.STATE_LISTEN:
                            Toast.makeText(context, "Bluetooth listening for a connection...", Toast.LENGTH_SHORT).show();
                            break;

                        case BluetoothService.STATE_NONE:
                            Toast.makeText(context, "Bluetooth not connected.", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    break;

                case BluetoothService.Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    String message_received = new String(readBuf, 0, msg.arg1);
                    ReceiveMessage(message_received);
                    break;

                case BluetoothService.Constants.MESSAGE_DEVICE_NAME:
                    ConnectedDeviceName = msg.getData().getString(BluetoothService.Constants.DEVICE_NAME);
                    if (context != null)
                    {
                        Toast.makeText(context, "Connected to " + ConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    }
                    break;

                case BluetoothService.Constants.MESSAGE_TOAST:
                    if (context != null)
                    {
                        Toast.makeText(context, msg.getData().getString(BluetoothService.Constants.TOAST), Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    private void connectDevice()
    {
        Set<BluetoothDevice> pairedDevices = blueAdapter.getBondedDevices();

        if (pairedDevices.size() > 0)
        {
            for (BluetoothDevice device : pairedDevices)
            {
                if (device.getBluetoothClass().getDeviceClass() == BluetoothClass.Device.PHONE_SMART)
                {
                    blueService.connect(device, false);
                    break;
                }
            }
        }
    }

    //Timer
    private final Runnable Timer = new Runnable()
    {
        @Override
        public void run()
        {
            if (!bl_Bored)
            {
                if (int_Delay == 0)
                {
                    int_Delay++;
                }
                else if (int_Delay == 1 &&
                        !bl_DelayForever)
                {
                    bl_Bored = true;
                    AttentionSpan();
                    int_Delay = 0;
                }
            }

            handle_timer.postDelayed(Timer, int_Time);
        }
    };

    private void startTimer()
    {
        if (bl_Connected)
        {
            int_Delay = 1;

            Random random = new Random();
            int_Time = random.nextInt(10000);
            handle_timer.postDelayed(Timer, int_Time);
        }
        else
        {
            int_Delay = 0;
            handle_timer.post(Timer);
        }
    }

    private void stopTimer()
    {
        handle_timer.removeCallbacks(Timer);
    }

    //Thinking
    private final Runnable Thought = new Runnable()
    {
        @Override
        public void run()
        {
            Logic.UserInput = false;

            List<String> thoughts = Data.getThoughts();
            String[] wordArray = Logic.prepInput(Logic.last_response_thinking);

            Logic.last_response_thinking = Logic.Think(wordArray);
            Logic.last_response_thinking = Util.RulesCheck(Logic.last_response_thinking);

            if (Logic.last_response_thinking != null)
            {
                if (!Logic.last_response_thinking.equals(""))
                {
                    thoughts.add("NLP: " + Logic.last_response_thinking);

                    Data.saveThoughts(thoughts);
                    Util.ClearLeftovers(context);
                }
            }

            if (bl_Thought)
            {
                Output.post(ScrollThoughts);
            }

            handle_thinking.postDelayed(Thought, 2000);
        }
    };

    private void startThinking()
    {
        handle_thinking.post(Thought);
    }

    private void stopThinking()
    {
        handle_thinking.removeCallbacks(Thought);
    }

    //Try to initiate conversation
    public void AttentionSpan()
    {
        if (!bl_Typing)
        {
            Logic.NewInput = false;
            Logic.Initiation = true;
            Logic.UserInput = false;

            String[] wordArray = new String[0];
            String output = Logic.Respond(wordArray, "");

            if (output != null)
            {
                if (!output.equals(""))
                {
                    List<String> history = Data.getHistory();
                    history.add("AI: " + output);

                    Data.saveHistory(history);
                    Util.CleanMemory(context);

                    Output.post(ScrollHistory);

                    if (bl_Connected)
                    {
                        SendMessage(output);
                        stopTimer();
                        bl_DelayForever = true;
                    }
                }
            }
        }
    }

    //After Enter
    public void onSend(View view)
    {
        handle_responding.post(Respond);
    }

    private final Runnable Respond = new Runnable()
    {
        @Override
        public void run()
        {
            String input = Input.getText().toString();
            if (input.length() > 0)
            {
                Logic.Initiation = false;
                Logic.UserInput = true;

                String[] wordArray = Logic.prepInput(input);

                if (wordArray != null)
                {
                    List<String> history = Data.getHistory();
                    input = Util.RulesCheck(input);
                    history.add("User: " + input);

                    String output = Logic.Respond(wordArray, input);

                    if (output != null)
                    {
                        if (!output.equals(""))
                        {
                            history.add("AI: " + output);
                        }
                    }

                    if (Logic.Speech)
                    {
                        speech.speak(output, TextToSpeech.QUEUE_FLUSH, null);
                    }

                    Data.saveHistory(history);
                    Util.ClearLeftovers(context);

                    Output.post(ScrollHistory);

                    Input.setText("");
                }
            }
            else if (bl_Connected)
            {
                if (delay_respond == 0)
                {
                    delay_respond++;
                    handle_responding.postDelayed(Respond, 2000);
                }
                else if (delay_respond == 1)
                {
                    Log.d(TAG, "Responding to: " + received);

                    if (received.length() > 0)
                    {
                        Log.d(TAG, "Prepping input...");
                        String[] wordArray = Logic.prepInput(received);

                        if (wordArray != null)
                        {
                            Log.d(TAG, "Checking rules...");
                            List<String> history = Data.getHistory();
                            received = Util.RulesCheck(received);

                            Log.d(TAG, "Responding...");
                            String output = Logic.Respond(wordArray, received);

                            if (output != null)
                            {
                                if (!output.equals(""))
                                {
                                    history.add("AI: " + output);
                                }
                            }

                            if (Logic.Speech)
                            {
                                speech.speak(output, TextToSpeech.QUEUE_FLUSH, null);
                            }

                            Data.saveHistory(history);
                            Util.ClearLeftovers(context);

                            Output.post(ScrollHistory);

                            SendMessage(output);
                        }
                    }
                }
            }
        }
    };

    private final Runnable ScrollHistory = new Runnable()
    {
        @Override
        public void run()
        {
            Output.setText("");
            Output.setMovementMethod(new ScrollingMovementMethod());

            List<String> history = Data.getHistory();
            for (int i = 0; i < history.size(); i++)
            {
                Output.append(history.get(i));
            }

            Output.setSelection(Output.getText().length());

            img_Face.setImageResource(R.drawable.face_neutral);

            if (bl_Bored)
            {
                bl_Bored = false;
            }
        }
    };

    private final Runnable ScrollThoughts = new Runnable()
    {
        @Override
        public void run()
        {
            Output.setText("");
            Output.setMovementMethod(new ScrollingMovementMethod());

            List<String> thoughts = Data.getThoughts();
            for (int i = 0; i < thoughts.size(); i++)
            {
                Output.append(thoughts.get(i));
            }

            Output.setSelection(Output.getText().length());
        }
    };

    //MessageBox
    private void PopUp()
    {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
        dlgAlert.setMessage("Brain has been erased.");
        dlgAlert.setTitle("System Message");
        dlgAlert.setPositiveButton("Ok", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {

            }
        });
        dlgAlert.setCancelable(false);
        dlgAlert.create().show();
    }

    //Yes/No Box for Exit
    private void Acknowledge_Exit()
    {
        stopTimer();
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        android.os.Process.killProcess(android.os.Process.myPid());
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        startTimer();
                        break;
                }
            }
        };
        Alert(dialogClickListener, "Exit the NLP Program?");
    }

    //Yes/No Box for Erase
    private void Acknowledge_Erase()
    {
        stopTimer();
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                switch (which)
                {
                    case DialogInterface.BUTTON_POSITIVE:
                        Util.EraseMemory(Brain_dir);

                        Output.setText("");
                        Input.setText("");

                        Logic.last_response = "";
                        Logic.last_response_thinking = "";
                        Logic.topics.clear();

                        if (!Brain_dir.exists())
                        {
                            Brain_dir.mkdirs();
                        }

                        File file = new File(Brain_dir, "Words.txt");
                        if (!file.exists())
                        {
                            try
                            {
                                file.createNewFile();
                            }
                            catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                        }

                        file = new File(Brain_dir, "InputList.txt");
                        if (!file.exists())
                        {
                            try
                            {
                                file.createNewFile();
                            }
                            catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                        }

                        DateFormat f = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
                        String currentDate = f.format(new Date());

                        file = new File(History_dir, currentDate + ".txt");
                        if (!History_dir.exists())
                        {
                            History_dir.mkdirs();
                            if (!file.exists())
                            {
                                try
                                {
                                    file.createNewFile();
                                }
                                catch (IOException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        }

                        file = new File(Thought_dir, currentDate + ".txt");
                        if (!Thought_dir.exists())
                        {
                            Thought_dir.mkdirs();
                            if (!file.exists())
                            {
                                try
                                {
                                    file.createNewFile();
                                }
                                catch (IOException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        }

                        PopUp();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        startTimer();
                        break;
                }
            }
        };
        Alert(dialogClickListener, "Erase all memory?");
    }

    //Yes/No Box for Advanced
    private void Acknowledge_Advanced(MenuItem item)
    {
        final MenuItem item_Advanced = item;

        if (Logic.Advanced)
        {
            Util.ToggleAdvanced(item_Advanced);
        }
        else
        {
            stopTimer();
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    switch (which)
                    {
                        case DialogInterface.BUTTON_POSITIVE:
                            Util.ToggleAdvanced(item_Advanced);
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            startTimer();
                            break;
                    }
                }
            };
            Alert(dialogClickListener, "Warning: Advanced mode will force the AI to only use procedurally generated responses and allow its thinking to modify its brain. " +
                    "This mode is not recommended and should only be used for the sake of science. Are you sure you want to enable this?");
        }
    }

    private void Enabled_AdvancedStuff()
    {
        btn_Encourage.setVisibility(View.VISIBLE);
        btn_Encourage.setClickable(true);
        btn_Encourage.setFocusableInTouchMode(true);
        btn_Encourage.setFocusable(true);

        btn_Discourage.setVisibility(View.VISIBLE);
        btn_Discourage.setClickable(true);
        btn_Discourage.setFocusableInTouchMode(true);
        btn_Discourage.setFocusable(true);

        img_Face.setVisibility(View.VISIBLE);
        img_Face.setImageResource(R.drawable.face_neutral);
    }

    private void Disable_AdvancedStuff()
    {
        btn_Encourage.setVisibility(View.INVISIBLE);
        btn_Encourage.setClickable(false);
        btn_Encourage.setFocusableInTouchMode(false);
        btn_Encourage.setFocusable(false);

        btn_Discourage.setVisibility(View.INVISIBLE);
        btn_Discourage.setClickable(false);
        btn_Discourage.setFocusableInTouchMode(false);
        btn_Discourage.setFocusable(false);

        img_Face.setVisibility(View.INVISIBLE);
    }

    private void Alert(DialogInterface.OnClickListener dialogClickListener, String message)
    {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
        dlgAlert.setMessage(message);
        dlgAlert.setTitle("System Message");
        dlgAlert.setPositiveButton("Yes", dialogClickListener);
        dlgAlert.setNegativeButton("No", dialogClickListener);
        dlgAlert.setCancelable(false);
        dlgAlert.create().show();
    }

    //Menu
    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        mi_Connect = menu.findItem(R.id.connect);
        mi_Disconnect = menu.findItem(R.id.disconnect);
        mi_NewSession = menu.findItem(R.id.new_session);
        mi_Thoughts = menu.findItem(R.id.thought_log);
        mi_Tips = menu.findItem(R.id.tips);
        mi_WordFix = menu.findItem(R.id.word_fix);
        mi_SetDelay = menu.findItem(R.id.setdelay);
        mi_SetResponse = menu.findItem(R.id.response_types);
        mi_EraseBrain = menu.findItem(R.id.erase_brain);
        mi_Advanced = menu.findItem(R.id.advanced);
        mi_Exit = menu.findItem(R.id.exit_app);

        mi_Connect.setVisible(false);
        mi_Disconnect.setVisible(false);

        if (bl_Bluetooth)
        {
            if (bl_Connected)
            {
                mi_Connect.setVisible(false);
                mi_Disconnect.setVisible(true);

                mi_NewSession.setVisible(false);
                mi_Thoughts.setVisible(false);
                mi_Tips.setVisible(false);
                mi_WordFix.setVisible(false);
                mi_SetDelay.setVisible(false);
                mi_SetResponse.setVisible(false);
                mi_EraseBrain.setVisible(false);
                mi_Advanced.setVisible(false);
                mi_Exit.setVisible(false);
            }
            else
            {
                mi_Connect.setVisible(true);
                mi_Disconnect.setVisible(false);

                mi_NewSession.setVisible(true);
                mi_Thoughts.setVisible(true);
                mi_Tips.setVisible(true);
                mi_WordFix.setVisible(true);
                mi_SetDelay.setVisible(true);
                mi_SetResponse.setVisible(true);
                mi_EraseBrain.setVisible(true);
                mi_Advanced.setVisible(true);
                mi_Exit.setVisible(true);
            }
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        if (Logic.Advanced)
        {
            MenuItem advanced = menu.findItem(R.id.advanced);
            advanced.setTitle("Advanced Mode: true");
        }
        else
        {
            MenuItem advanced = menu.findItem(R.id.advanced);
            advanced.setTitle("Advanced Mode: false");
        }

        if (Logic.Speech)
        {
            MenuItem advanced = menu.findItem(R.id.speech);
            advanced.setTitle("Speech: true");
        }
        else
        {
            MenuItem advanced = menu.findItem(R.id.speech);
            advanced.setTitle("Speech: false");
        }

        return true;
    }

    public void onMenu(View view)
    {
        if (bl_WordFix || bl_Delay || bl_Responses)
        {
            CloseWordFix();
        }
        else if (bl_Thought)
        {
            CloseThought();
        }
        else if (bl_Tips)
        {
            CloseTips();
        }
        else
        {
            this.openOptionsMenu();
        }
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu)
    {
        Output.setVisibility(View.INVISIBLE);
        Input.setVisibility(View.INVISIBLE);
        btn_Menu.setVisibility(View.INVISIBLE);
        Disable_AdvancedStuff();

        HideKeyboard();

        stopTimer();
        stopThinking();

        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public void onPanelClosed(int featureId, Menu menu)
    {
        if (!bl_Thought && !bl_Tips)
        {
            if (!bl_WordFix && !bl_Delay && !bl_Responses)
            {
                Output.setVisibility(View.VISIBLE);
                Input.setVisibility(View.VISIBLE);

                btn_Menu.setText(R.string.menu_button);
                btn_Menu.setVisibility(View.VISIBLE);

                Enabled_AdvancedStuff();

                startTimer();
                startThinking();
            }
            else
            {
                btn_Menu.setText(R.string.back_button);
                btn_Menu.setVisibility(View.VISIBLE);
            }
        }
        else
        {
            btn_Menu.setText(R.string.ok_button);
            btn_Menu.setVisibility(View.VISIBLE);
            Output.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.connect:
                SendMessage("RealAI Connect");
                return true;

            case R.id.disconnect:
                SendMessage("RealAI Disconnect");
                bl_Connected = false;

                Input.setEnabled(true);
                btn_Encourage.setEnabled(true);
                btn_Discourage.setEnabled(true);

                handle_responding.removeCallbacks(Respond);
                startTimer();
                return true;

            case R.id.new_session:
                NewSession();
                return true;

            case R.id.tips:
                DisplayTips();
                return true;

            case R.id.thought_log:
                stopTimer();
                startThinking();
                bl_Thought = true;
                return true;

            case R.id.word_fix:
                DisplayWordFix();
                return true;

            case R.id.setdelay:
                DisplayDelay();
                return true;

            case R.id.response_types:
                DisplayResponses();
                return true;

            case R.id.erase_brain:
                Acknowledge_Erase();
                return true;

            case R.id.speech:
                Util.ToggleSpeech(item);
                return true;

            case R.id.advanced:
                Acknowledge_Advanced(item);
                return true;

            case R.id.exit_app:
                Acknowledge_Exit();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
    {
        if (bl_WordFix)
        {
            List<WordData> data = Data.getWords();
            List<String> words = new ArrayList<>();
            for (int i = 0; i < data.size(); i++)
            {
                words.add(data.get(i).getWord());
            }

            wordfix_selection = parent.getSelectedItemPosition();
            txt_WordFix.setText(words.get(wordfix_selection));
        }
        else if (bl_Delay)
        {
            delay_selection = parent.getSelectedItemPosition();
        }
        else if (bl_Responses)
        {
            response_selection = parent.getSelectedItemPosition();
            if (response_selection == 0)
            {
                btn_WordFix.setText(Logic.TopicBased.toString());
            }
            else if (response_selection == 1)
            {
                btn_WordFix.setText(Logic.ConditionBased.toString());
            }
            else if (response_selection == 2)
            {
                btn_WordFix.setText(Logic.ProceduralBased.toString());
            }
        }
    }

    public void onNothingSelected(AdapterView<?> parent)
    {

    }

    private void DisplayWordFix()
    {
        //Set Spinner
        List<WordData> data = Data.getWords();
        List<String> words = new ArrayList<>();
        for (int i = 0; i < data.size(); i++)
        {
            words.add(data.get(i).getWord());
        }

        if (words.size() > 0)
        {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, words);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sp_WordFix.setAdapter(adapter);
            sp_WordFix.setSelection(0);
            sp_WordFix.setVisibility(View.VISIBLE);
            sp_WordFix.setClickable(true);
            sp_WordFix.setFocusable(true);

            //Set Input
            if (wordfix_selection > words.size() - 1)
            {
                wordfix_selection = words.size() - 1;
            }

            txt_WordFix.setText(words.get(wordfix_selection));
            txt_WordFix.setVisibility(View.VISIBLE);
            txt_WordFix.setClickable(true);
            txt_WordFix.setFocusableInTouchMode(true);
            txt_WordFix.setFocusable(true);
            txt_WordFix.requestFocus();

            //Set Button
            btn_WordFix.setText(R.string.btn_accept);
            btn_WordFix.setVisibility(View.VISIBLE);
            btn_WordFix.setClickable(true);
            btn_WordFix.setFocusable(true);

            stopTimer();
            bl_WordFix = true;
        }
    }

    public void WordFix(View view)
    {
        if (bl_WordFix)
        {
            List<WordData> data = Data.getWords();

            String oldWord = data.get(wordfix_selection).getWord();
            String newWord = txt_WordFix.getText().toString();

            List<String> input = Data.getInputList();
            for (int i = 0; i < input.size(); i++)
            {
                List<String> output = Data.getAllOutputs(input.get(i));
                for (int j = 0; j < output.size(); j++)
                {
                    if (output.get(j).contains(oldWord))
                    {
                        String newOutput = output.get(j).replace(oldWord, newWord);
                        output.set(j, newOutput);
                    }
                }
                Data.saveOutput(output, input.get(i));

                if (input.get(i).contains(oldWord))
                {
                    String oldPath = input.get(i) + ".txt";
                    String newInput = input.get(i).replace(oldWord, newWord);
                    input.set(i, newInput);
                    File oldFile = new File(Brain_dir, oldPath);
                    File newFile = new File(Brain_dir, input.get(i) + ".txt");
                    oldFile.renameTo(newFile);
                }
            }
            Data.saveInputList(input);

            List<String> words = new ArrayList<>();
            for (int i = 0; i < data.size(); i++)
            {
                words.add(data.get(i).getWord());
            }

            for (int i = 0; i < words.size(); i++)
            {
                data = Data.getPreWords(words.get(i));
                for (int j = 0; j < data.size(); j++)
                {
                    if (data.get(j).getWord().equals(oldWord))
                    {
                        String oldPath = "Pre-" + data.get(j).getWord() + ".txt";
                        data.get(j).setWord(newWord);
                        String newPath = "Pre-" + data.get(j).getWord() + ".txt";

                        File oldFile = new File(Brain_dir, oldPath);
                        File newFile = new File(Brain_dir, newPath);
                        oldFile.renameTo(newFile);
                    }
                }
                Data.savePreWords(data, words.get(i));

                data = Data.getProWords(words.get(i));
                for (int j = 0; j < data.size(); j++)
                {
                    if (data.get(j).getWord().equals(oldWord))
                    {
                        String oldPath = "Pro-" + data.get(j).getWord() + ".txt";
                        data.get(j).setWord(newWord);
                        String newPath = "Pro-" + data.get(j).getWord() + ".txt";

                        File oldFile = new File(Brain_dir, oldPath);
                        File newFile = new File(Brain_dir, newPath);
                        oldFile.renameTo(newFile);
                    }
                }
                Data.saveProWords(data, words.get(i));
            }

            data = Data.getWords();
            data.get(wordfix_selection).setWord(newWord);
            Data.saveWords(data);

            CloseWordFix();
        }
        else if (bl_Delay)
        {
            if (delay_selection == 3)
            {
                Data.setConfig("Infinite", Logic.Advanced.toString(), Logic.TopicBased.toString(), Logic.ConditionBased.toString(),
                        Logic.ProceduralBased.toString(), Logic.Speech.toString());
                bl_DelayForever = true;
            }
            else
            {
                Data.setConfig(((delay_selection * 10) + 10) + " seconds", Logic.Advanced.toString(), Logic.TopicBased.toString(), Logic.ConditionBased.toString(),
                        Logic.ProceduralBased.toString(), Logic.Speech.toString());
                int_Time = ((delay_selection * 10) + 10) * 1000;
                bl_DelayForever = false;
            }

            CloseWordFix();
        }
        else if (bl_Responses)
        {
            if (response_selection == 0)
            {
                Logic.TopicBased = !Logic.TopicBased;
                btn_WordFix.setText(Logic.TopicBased.toString());
            }
            else if (response_selection == 1)
            {
                Logic.ConditionBased = !Logic.ConditionBased;
                btn_WordFix.setText(Logic.ConditionBased.toString());
            }
            else if (response_selection == 2)
            {
                Logic.ProceduralBased = !Logic.ProceduralBased;
                btn_WordFix.setText(Logic.ProceduralBased.toString());
            }

            if (delay_selection == 3)
            {
                Data.setConfig("Infinite", Logic.Advanced.toString(), Logic.TopicBased.toString(), Logic.ConditionBased.toString(),
                        Logic.ProceduralBased.toString(), Logic.Speech.toString());
            }
            else
            {
                Data.setConfig(((delay_selection * 10) + 10) + " seconds", Logic.Advanced.toString(), Logic.TopicBased.toString(), Logic.ConditionBased.toString(),
                        Logic.ProceduralBased.toString(), Logic.Speech.toString());
            }
        }
    }

    private void DisplayDelay()
    {
        List<String> delays = new ArrayList<>();
        delays.add("10 seconds");
        delays.add("20 seconds");
        delays.add("30 seconds");
        delays.add("Infinite");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, delays);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_WordFix.setAdapter(adapter);
        sp_WordFix.setSelection(delay_selection);
        sp_WordFix.setVisibility(View.VISIBLE);
        sp_WordFix.setClickable(true);
        sp_WordFix.setFocusable(true);

        btn_WordFix.setText(R.string.btn_accept);
        btn_WordFix.setVisibility(View.VISIBLE);
        btn_WordFix.setClickable(true);
        btn_WordFix.setFocusable(true);

        stopTimer();
        bl_Delay = true;
    }

    private void DisplayResponses()
    {
        btn_Menu.setText(R.string.ok_button);
        btn_Menu.setVisibility(View.VISIBLE);

        //Set Spinner
        List<String> methods = new ArrayList<>();
        methods.add("Topic Response Method");
        methods.add("Condition Response Method");
        methods.add("Procedural Response Method");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, methods);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_WordFix.setAdapter(adapter);
        sp_WordFix.setSelection(response_selection);
        sp_WordFix.setVisibility(View.VISIBLE);
        sp_WordFix.setClickable(true);
        sp_WordFix.setFocusable(true);

        //Set Button
        if (response_selection == 0)
        {
            btn_WordFix.setText(Logic.TopicBased.toString());
        }
        else if (response_selection == 1)
        {
            btn_WordFix.setText(Logic.ConditionBased.toString());
        }
        else if (response_selection == 2)
        {
            btn_WordFix.setText(Logic.ProceduralBased.toString());
        }

        btn_WordFix.setVisibility(View.VISIBLE);
        btn_WordFix.setClickable(true);
        btn_WordFix.setFocusable(true);

        stopTimer();
        bl_Responses = true;
    }

    private void DisplayTips()
    {
        Input.setVisibility(View.INVISIBLE);
        btn_Menu.setText(R.string.ok_button);
        Disable_AdvancedStuff();

        String tips = "";
        tips += "Here are some tips for teaching the AI: \n\n";

        tips += "1. The AI learns from observing how you respond to what it says... " +
                "so, if it says \"Hello.\" and you say \"How are you?\" it will learn that \"How are you?\" " +
                "is a possible response to \"Hello.\". If you say something it has never seen before, it will " +
                "repeat it to see how -you- would respond to it. Learning by imitation, like a young child, " +
                "is not the only way it learns as you will soon discover.\n\n";

        tips += "2. It will generate stuff that sounds nonsensical early on... this is part of the learning process, " +
                "similar to the way children phrase things in ways that don't quite make sense early on. \n\n";

        tips += "3. If it says something that doesn't make sense, you can discourage the AI by pressing the Discourage button. " +
                "This will also reset the session so that whatever you say next won't be considered a response to what was " +
                "last said. \n\n";

        tips += "4. In contrast to Discouraging the AI, there is a button to Encourage it and let it know " +
                "it has used words properly. \n\n";

        tips += "5. Limit your response to a single sentence or question. \n\n";

        tips += "6. Use complete sentences when responding. Start with a capital letter and end with a punctuation mark. \n\n";

        tips += "7. Avoid contractions (use \"it is\" instead of \"it's\"). \n\n";

        tips += "8. The AI runs in real-time and will try to initiate conversation on its own if idle for too long. " +
                "To adjust how long it waits before assuming you're idle, or to make it never check for idleness, " +
                "check out the Set Delay option in the Menu. \n\n";

        tips += "9. The AI cannot see/hear/taste/smell/feel any 'things' you refer to, so it can never have any contextual " +
                "understanding of what exactly the 'thing' is (the way you understand it). This also means it'll " +
                "never understand you trying to reference it (or yourself) directly, as it can never have a concept of " +
                "anything external being something different from it without spatial recognition gained from sight/touch/sound. \n\n";

        tips += "10. In general... keep it simple. The simpler you speak to it, the better it learns. \n\n";

        tips += "For help, check Discord: https://discord.gg/3yJ8rce \n\n";

        tips += "For more information and details of how the AI works, check the Forum: http://realai.freeforums.net/#category-3 \n\n";

        Output.setMovementMethod(LinkMovementMethod.getInstance());
        Output.setText(tips);

        stopTimer();
        bl_Tips = true;
    }

    private void CloseWordFix()
    {
        //Set Spinner
        sp_WordFix.setVisibility(View.INVISIBLE);
        sp_WordFix.setClickable(false);
        sp_WordFix.setFocusable(false);

        //Set Input
        txt_WordFix.setVisibility(View.INVISIBLE);
        txt_WordFix.setClickable(false);
        txt_WordFix.setFocusable(false);
        txt_WordFix.setFocusableInTouchMode(false);

        //Set Button
        btn_WordFix.setVisibility(View.INVISIBLE);
        btn_WordFix.setClickable(false);
        btn_WordFix.setFocusable(false);

        Output.setVisibility(View.VISIBLE);
        Input.setVisibility(View.VISIBLE);

        btn_Menu.setText(R.string.menu_button);
        btn_Menu.setVisibility(View.VISIBLE);

        Enabled_AdvancedStuff();

        Output.post(ScrollHistory);

        ShowKeyboard();

        bl_WordFix = false;
        bl_Delay = false;
        bl_Responses = false;

        startTimer();
    }

    private void CloseThought()
    {
        Input.setVisibility(View.VISIBLE);

        btn_Menu.setText(R.string.menu_button);
        btn_Menu.setVisibility(View.VISIBLE);

        Enabled_AdvancedStuff();

        Output.post(ScrollHistory);

        ShowKeyboard();

        bl_Thought = false;

        startTimer();
    }

    private void CloseTips()
    {
        Input.setVisibility(View.VISIBLE);

        btn_Menu.setText(R.string.menu_button);
        btn_Menu.setVisibility(View.VISIBLE);

        Enabled_AdvancedStuff();

        Output.post(ScrollHistory);

        ShowKeyboard();

        bl_Tips = false;

        startTimer();
        startThinking();
    }

    public void Encourage(View view)
    {
        Util.CleanMemory(context);
        Util.Encourage();

        List<String> history = Data.getHistory();
        history.add("---New Session---");
        Data.saveHistory(history);
        Output.post(ScrollHistory);

        Logic.NewInput = false;
    }

    public void Discourage(View view)
    {
        Util.CleanMemory(context);
        Util.Discourage();

        List<String> history = Data.getHistory();
        history.add("---New Session---");
        Data.saveHistory(history);
        Output.post(ScrollHistory);

        Logic.NewInput = false;
    }

    public void NewSession()
    {
        Logic.NewInput = false;

        List<String> history = Data.getHistory();
        history.add("---New Session---");

        Data.saveHistory(history);
        Util.CleanMemory(context);

        Input.setVisibility(View.VISIBLE);

        btn_Menu.setText(R.string.menu_button);
        btn_Menu.setVisibility(View.VISIBLE);

        Enabled_AdvancedStuff();

        Output.post(ScrollHistory);

        ShowKeyboard();
    }

    public void HideKeyboard()
    {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null)
        {
            imm.hideSoftInputFromWindow(Input.getWindowToken(), 0);
        }
    }

    private void ShowKeyboard()
    {
        Input.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null)
        {
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }
}
