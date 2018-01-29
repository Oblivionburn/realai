package com.oblivionburn.nlp;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends Activity implements OnItemSelectedListener
{
    public static final File Brain_dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Brain/" );
    public static final File History_dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Brain/History/" );
    public static final File Thought_dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Brain/Thoughts/" );

    static int int_Time = 10000;
    static boolean bl_DelayForever = false;

    private LiteText Output = null;
    private LiteText Input = null;
    private LiteText txt_WordFix = null;
    private Spinner sp_WordFix = null;
    private Button btn_WordFix = null;
    private Button btn_Enter = null;
    private Button btn_Menu = null;
    private Button btn_Encourage = null;
    private Button btn_Discourage = null;
    private ImageView img_Face = null;

    private int int_Delay = 0;
    private int delay_selection = 0;
    private int wordfix_selection = 0;

    private boolean bl_Typing = false;
    private boolean bl_Ready = false;
    private boolean bl_Thought = false;
    private boolean bl_WordFix = false;
    private boolean bl_Delay = false;
    private boolean bl_Tips = false;
    private boolean bl_PermitsMissing = false;
    private boolean bl_Encourage_Pressed = false;
    private boolean bl_Discourage_Pressed = false;

    private Handler handler;
    private boolean KeyboardOpen;
    private View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        Input = findViewById(R.id.txt_Input);

        Output = findViewById(R.id.txt_Output);
        Output.setMaxLines(Integer.MAX_VALUE);

        btn_Enter = findViewById(R.id.btn_Enter);
        btn_Menu = findViewById(R.id.btn_Menu);

        sp_WordFix = findViewById(R.id.sp_WordFix);
        sp_WordFix.setOnItemSelectedListener(this);
        txt_WordFix = findViewById(R.id.txt_WordFix);
        btn_WordFix = findViewById(R.id.btn_WordFix);

        btn_Encourage = findViewById(R.id.btn_Encourage);
        btn_Discourage = findViewById(R.id.btn_Discourage);

        img_Face = findViewById(R.id.img_Face);

        handler = new Handler();

        createBrain();
        createListeners();

        rootView = findViewById(android.R.id.content);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {
                int heightDiff = rootView.getRootView().getHeight() - rootView.getHeight();
                KeyboardOpen = heightDiff > Util.dpToPx(getApplicationContext());
            }
        });

        if (hasPermissions())
        {
            bl_Ready = true;
            DisplayTips();
        }
        else
        {
            DisplayPermissions();
        }
    }

    private void createBrain()
    {
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

        file = new File(Brain_dir, "Config.ini");
        if (!file.exists())
        {
            try
            {
                file.createNewFile();
                Data.initConfig();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
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
                case "Off":
                    Logic.Advanced = false;
                    Disable_AdvancedStuff();
                    break;

                case "On":
                    Logic.Advanced = true;
                    Enabled_AdvancedStuff();
                    break;
            }
        }

        if (!History_dir.exists())
        {
            History_dir.mkdirs();
        }

        if (!Thought_dir.exists())
        {
            Thought_dir.mkdirs();
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
                if (actionId == EditorInfo.IME_ACTION_DONE)
                {
                    onSend(v);
                }
                return true;
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

    private boolean hasPermissions()
    {
        boolean result = true;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (!Settings.canDrawOverlays(this))
            {
                result = false;
            }
            else if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                result = false;
            }
        }

        return result;
    }

    @Override
    public void onDestroy()
    {
        stopTimer();
        stopThinking();
        android.os.Process.killProcess(android.os.Process.myPid());
        super.onDestroy();
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
        else if (bl_PermitsMissing)
        {
            onDestroy();
        }
        else
        {
            Acknowledge_Exit();
        }
    }

    //Timer
    private final Runnable Timer = new Runnable()
    {
        @Override
        public void run()
        {
            if (int_Delay == 0)
            {
                int_Delay++;
            }
            else if (int_Delay == 1 &&
                    !bl_DelayForever)
            {
                AttentionSpan();
                int_Delay = 0;
            }
            handler.postDelayed(Timer, int_Time);
        }
    };

    private void startTimer()
    {
        int_Delay = 0;
        Timer.run();
    }

    private void stopTimer()
    {
        handler.removeCallbacks(Timer);
    }

    //Thinking
    private final Runnable Thought = new Runnable()
    {
        @Override
        public void run()
        {
            Thread t = new Thread()
            {
                public void run()
                {
                    Logic.UserInput = false;

                    List<String> thoughts = Data.getThoughts();
                    String[] wordArray = Logic.prepInput(Logic.last_response_thinking);

                    Logic.last_response_thinking = Logic.Think(wordArray);
                    Logic.last_response_thinking = Util.HistoryRules(Logic.last_response_thinking);

                    if (!Logic.last_response_thinking.equals(""))
                    {
                        thoughts.add("NLP: " + Logic.last_response_thinking);
                        Data.saveThoughts(thoughts);
                    }

                    Util.ClearLeftovers();

                    if (bl_Thought)
                    {
                        ScrollThoughts();
                    }

                    handler.postDelayed(Thought, 1000);
                }
            };
            t.start();
        }
    };

    private void startThinking()
    {
        Thought.run();
    }

    private void stopThinking()
    {
        handler.removeCallbacks(Thought);
    }

    //Try to initiate conversation
    public void AttentionSpan()
    {
        if (!bl_Typing)
        {
            Thread t = new Thread()
            {
                public void run()
                {
                    if (Logic.NewInput)
                    {
                        Util.CleanMemory();
                    }

                    Logic.NewInput = false;
                    Logic.Initiation = true;
                    Logic.UserInput = false;

                    List<String> history = Data.getHistory();
                    String[] wordArray = new String[0];

                    String output = Logic.Respond(wordArray, "");

                    if (!output.equals(""))
                    {
                        history.add("AI: " + output);
                        Data.saveHistory(history);
                    }

                    ScrollHistory();
                    img_Face.setImageResource(R.drawable.face_neutral);
                }
            };
            t.start();
        }
    }

    //After Enter
    public void onSend(View view)
    {
        if (bl_WordFix ||
            bl_Delay)
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
        else if (bl_PermitsMissing)
        {
            onDestroy();
        }
        else if (bl_Ready)
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
                    input = Util.HistoryRules(input);
                    history.add("User: " + input);

                    String output = Logic.Respond(wordArray, input);

                    if (!output.equals(""))
                    {
                        history.add("AI: " + output);
                    }

                    Data.saveHistory(history);

                    ScrollHistory();

                    Util.ClearLeftovers();
                }

                Input.setText("");
                img_Face.setImageResource(R.drawable.face_neutral);
            }
        }
    }

    private void ScrollHistory()
    {
        Output.setText("");
        Output.setMovementMethod(new ScrollingMovementMethod());

        List<String> history = Data.getHistory();
        if (history.size() > 40)
        {
            for (int i = history.size() - 40; i < history.size(); i++)
            {
                Output.append(history.get(i) + "\n");
            }
        }
        else
        {
            for (int i = 0; i < history.size(); i++)
            {
                Output.append(history.get(i) + "\n");
            }
        }

        Output.setSelection(Output.getText().length());
    }

    private void ScrollThoughts()
    {
        Output.setText("");
        Output.setMovementMethod(new ScrollingMovementMethod());

        List<String> thoughts = Data.getThoughts();
        if (thoughts.size() > 40)
        {
            for (int i = thoughts.size() - 40; i < thoughts.size(); i++)
            {
                Output.append(thoughts.get(i) + "\n");
            }
        }
        else
        {
            for (int i = 0; i < thoughts.size(); i++)
            {
                Output.append(thoughts.get(i) + "\n");
            }
        }

        Output.setSelection(Output.getText().length());
    }

    //MessageBox
    private void PopUp()
    {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
        dlgAlert.setMessage("Memory has been erased.");
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
            Disable_AdvancedStuff();
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
                            Enabled_AdvancedStuff();
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            startTimer();
                            break;
                    }
                }
            };
            Alert(dialogClickListener, "Warning: Advanced mode will force the AI to only use procedurally generated responses and allow its thinking to modify data. " +
                    "This mode is not recommended and should only be used for the sake of science. Are you sure you want to enable this?");
        }
    }

    private void Enabled_AdvancedStuff()
    {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ABOVE, R.id.btn_Discourage);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        btn_Enter.setLayoutParams(params);

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
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ABOVE, R.id.btn_Menu);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        btn_Enter.setLayoutParams(params);

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
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        if (Logic.Advanced)
        {
            MenuItem advanced = menu.findItem(R.id.advanced);
            advanced.setTitle("Advanced Mode: On");
        }
        else
        {
            MenuItem advanced = menu.findItem(R.id.advanced);
            advanced.setTitle("Advanced Mode: Off");
        }

        return true;
    }

    public void onMenu(View view)
    {
        this.openOptionsMenu();
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu)
    {
        Output.setVisibility(View.INVISIBLE);
        Input.setVisibility(View.INVISIBLE);
        btn_Enter.setVisibility(View.INVISIBLE);
        btn_Menu.setVisibility(View.INVISIBLE);
        btn_Encourage.setVisibility(View.INVISIBLE);
        btn_Discourage.setVisibility(View.INVISIBLE);
        img_Face.setVisibility(View.INVISIBLE);

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
            if (!bl_WordFix && !bl_Delay)
            {
                Output.setVisibility(View.VISIBLE);
                Input.setVisibility(View.VISIBLE);
                btn_Menu.setVisibility(View.VISIBLE);

                btn_Enter.setText(R.string.enter_button);
                btn_Enter.setVisibility(View.VISIBLE);

                if (Logic.Advanced)
                {
                    btn_Encourage.setVisibility(View.VISIBLE);
                    btn_Discourage.setVisibility(View.VISIBLE);
                    img_Face.setVisibility(View.VISIBLE);
                    img_Face.setImageResource(R.drawable.face_neutral);
                }

                startTimer();
                startThinking();
            }
            else
            {
                btn_Enter.setText(R.string.back_button);
                btn_Enter.setVisibility(View.VISIBLE);
            }
        }
        else
        {
            btn_Enter.setText(R.string.back_button);
            btn_Enter.setVisibility(View.VISIBLE);
            Output.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
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

            case R.id.erase_memory:
                Acknowledge_Erase();
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
                List<String> output = Data.getOutputList(input.get(i));
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
        }
        else if (bl_Delay)
        {
            if (delay_selection == 3)
            {
                if (Logic.Advanced)
                {
                    Data.setConfig("Infinite", "On");
                }
                else
                {
                    Data.setConfig("Infinite", "Off");
                }

                bl_DelayForever = true;
            }
            else
            {
                if (Logic.Advanced)
                {
                    Data.setConfig(((delay_selection * 10) + 10) + " seconds", "On");
                }
                else
                {
                    Data.setConfig(((delay_selection * 10) + 10) + " seconds", "Off");
                }

                int_Time = ((delay_selection * 10) + 10) * 1000;
                bl_DelayForever = false;
            }
        }

        CloseWordFix();
    }

    private void DisplayDelay()
    {
        //Set Spinner
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

        //Set Button
        btn_WordFix.setVisibility(View.VISIBLE);
        btn_WordFix.setClickable(true);
        btn_WordFix.setFocusable(true);

        stopTimer();
        bl_Delay = true;
    }

    private void DisplayTips()
    {
        Input.setVisibility(View.INVISIBLE);
        btn_Menu.setVisibility(View.INVISIBLE);
        btn_Enter.setText(R.string.ok_button);
        btn_Enter.setVisibility(View.VISIBLE);
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

        tips += "3. The AI runs in real-time and will try to initiate conversation on its own if idle for too long. " +
                "To adjust how long it waits before assuming you're idle, or to make it never check for idleness, " +
                "check out the Set Delay option in the Menu. \n\n";

        tips += "4. If it says something that doesn't make sense, you can discourage the AI by pressing the Discourage button. " +
                "This will also reset the session so that whatever you say next WILL NOT be considered a response to what was " +
                "last said. \n\n";

        tips += "5. In contrast to Discouraging the AI, there is a button to Encourage it... pressing said button will let it know " +
                "it has used words properly. If you would like a more technical breakdown of how exactly " +
                "this works, check here: http://realai.freeforums.net/thread/18/expect-ai?page=1&scrollTo=50 \n\n";

        tips += "6. The AI cannot see/hear/taste/smell/feel any 'things' you refer to, so it can never have any contextual " +
                "understanding of what exactly the 'thing' is (the way you understand it). This also means it'll " +
                "never understand you trying to reference it (or yourself) directly, as it can never have a concept of " +
                "anything external being something different from it without spatial recognition gained from sight/touch/sound. \n\n";

        tips += "7. Use complete sentences when responding. Start with a capital letter and end with a punctuation mark. \n\n";

        tips += "8. Limit your responses to single sentences/questions. \n\n";

        tips += "9. Avoid conjunctions (use \"it is\" instead of \"it's\"). \n\n";

        tips += "10. In general... keep it simple. The simpler you speak to it, the better it learns. \n\n";

        Output.setMovementMethod(LinkMovementMethod.getInstance());
        Output.setText(tips);

        stopTimer();
        bl_Tips = true;
    }

    private void DisplayPermissions()
    {
        Input.setVisibility(View.INVISIBLE);
        btn_Menu.setVisibility(View.INVISIBLE);
        btn_Enter.setText(R.string.exit_app);
        btn_Enter.setVisibility(View.VISIBLE);
        Disable_AdvancedStuff();

        String permissions = "";
        permissions += "This app requires the 'Storage' and 'Draw over other apps' permissions to function. \n\n";

        permissions += "To enable the 'Storage' permission: \n";
        permissions += "1. Exit the app \n";
        permissions += "2. Go to Settings \n";
        permissions += "3. Go to Apps \n";
        permissions += "4. Find 'Real AI Text' in the list and tap it. \n";
        permissions += "5. Tap 'Permissions'. \n";
        permissions += "6. Toggle 'Storage' to ON. \n\n";

        permissions += "To enable the 'Draw over other apps' permission: \n";
        permissions += "1. Exit the app \n";
        permissions += "2. Go to Settings \n";
        permissions += "3. Go to Apps \n";
        permissions += "4. On the top right, tap the gear icon. \n";
        permissions += "5. Under Advanced, chose 'Draw over other apps' \n";
        permissions += "6. Find 'Real AI Text' in the list and tap it. \n";
        permissions += "7. Toggle 'Permit drawing over other apps' to ON. \n";

        Output.setMovementMethod(LinkMovementMethod.getInstance());
        Output.setText(permissions);

        stopTimer();
        bl_PermitsMissing = true;
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
        btn_Menu.setVisibility(View.VISIBLE);
        btn_Enter.setText(R.string.enter_button);
        btn_Enter.setVisibility(View.VISIBLE);

        if (Logic.Advanced)
        {
            Enabled_AdvancedStuff();
        }

        ScrollHistory();

        ShowKeyboard();

        bl_WordFix = false;
        bl_Delay = false;

        startTimer();
    }

    private void CloseThought()
    {
        Input.setVisibility(View.VISIBLE);
        btn_Menu.setVisibility(View.VISIBLE);
        btn_Enter.setText(R.string.enter_button);
        btn_Enter.setVisibility(View.VISIBLE);

        if (Logic.Advanced)
        {
            Enabled_AdvancedStuff();
        }

        ScrollHistory();

        ShowKeyboard();

        bl_Thought = false;

        startTimer();
    }

    private void CloseTips()
    {
        Input.setVisibility(View.VISIBLE);
        btn_Menu.setVisibility(View.VISIBLE);
        btn_Enter.setText(R.string.enter_button);
        btn_Enter.setVisibility(View.VISIBLE);

        if (Logic.Advanced)
        {
            Enabled_AdvancedStuff();
        }

        ScrollHistory();

        ShowKeyboard();

        bl_Tips = false;

        startTimer();
        startThinking();
    }

    public void Encourage(View view)
    {
        Util.Encourage();
    }

    public void Discourage(View view)
    {
        Util.CleanMemory();
        Util.Discourage();

        List<String> history = Data.getHistory();
        history.add("---New Session---");
        Data.saveHistory(history);
        ScrollHistory();

        Logic.NewInput = false;
    }

    public void HideKeyboard()
    {
        if (KeyboardOpen)
        {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null)
            {
                imm.hideSoftInputFromWindow(Input.getWindowToken(), 0);
            }
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
