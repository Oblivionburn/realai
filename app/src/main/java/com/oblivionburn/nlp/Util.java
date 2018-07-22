package com.oblivionburn.nlp;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MenuItem;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class Util
{
    static void CleanMemory()
    {
        Thread t = new Thread()
        {
            public void run()
            {
                List<String> input = Data.getInputList();
                if (input.size() > 0)
                {
                    for (int i = 0; i < input.size(); i++)
                    {
                        String MemoryCheck = input.get(i);
                        File file = new File(MainActivity.Brain_dir, MemoryCheck + ".txt");

                        if (file.exists())
                        {
                            List<String> output = Data.getOutputList(MemoryCheck);
                            if (output.size() == 0)
                            {
                                file.delete();
                                input.remove(i);
                                if (i > 0)
                                {
                                    i--;
                                }
                            }
                            else if (output.size() == 1)
                            {
                                if (output.get(0).contains("~"))
                                {
                                    file.delete();
                                    input.remove(i);
                                    if (i > 0)
                                    {
                                        i--;
                                    }
                                }
                            }
                        }
                        else
                        {
                            input.remove(i);
                            if (i > 0)
                            {
                                i--;
                            }
                        }
                    }

                    Data.saveInputList(input);
                }

                File[] files = MainActivity.Brain_dir.listFiles();
                if (files != null)
                {
                    for (File file : files)
                    {
                        String MemoryCheck = file.getName();
                        int index = MemoryCheck.lastIndexOf('.');
                        if (index > 0)
                        {
                            MemoryCheck = MemoryCheck.substring(0, index);

                            List<String> output = Data.getOutputList(MemoryCheck);
                            if (output.size() == 0)
                            {
                                file.delete();
                            }
                        }
                    }
                }
            }
        };
        t.start();
    }

    static void ClearLeftovers()
    {
        File file = new File(MainActivity.Brain_dir, ".txt");
        if (file.exists())
        {
            file.delete();
        }

        file = new File(MainActivity.Brain_dir, ",.txt");
        if (file.exists())
        {
            file.delete();
        }

        file = new File(MainActivity.Brain_dir, "..txt");
        if (file.exists())
        {
            file.delete();
        }
    }

    static void Encourage()
    {
        if (!Logic.last_response.equals(""))
        {
            if (Logic.last_response.contains("."))
            {
                String str = Logic.last_response;
                StringBuilder sb = new StringBuilder(str).replace(Logic.last_response.indexOf("."), Logic.last_response.indexOf(".") + 1, " .");
                Logic.last_response = sb.toString();
            }
            else if (Logic.last_response.contains("?"))
            {
                String str = Logic.last_response;
                StringBuilder sb = new StringBuilder(str).replace(Logic.last_response.indexOf("?"), Logic.last_response.indexOf("?") + 1, " $");
                Logic.last_response = sb.toString();
            }
            else if (Logic.last_response.contains("!"))
            {
                String str = Logic.last_response;
                StringBuilder sb = new StringBuilder(str).replace(Logic.last_response.indexOf("!"), Logic.last_response.indexOf("!") + 1, " !");
                Logic.last_response = sb.toString();
            }

            String[] WordArray = Logic.last_response.split(" ");
            for (int i = 0; i < WordArray.length; i++)
            {
                switch (WordArray[i])
                {
                    case ",":
                        WordArray[i] = " ,";
                        break;
                    case ";":
                        WordArray[i] = " ;";
                        break;
                    case ":":
                        WordArray[i] = "";
                        break;
                    case "?":
                        WordArray[i] = " $";
                        break;
                    case "$":
                        WordArray[i] = " $";
                        break;
                    case "!":
                        WordArray[i] = " !";
                        break;
                    case ".":
                        WordArray[i] = " .";
                        break;
                }
            }

            List<WordData> data;
            List<String> words = new ArrayList<>();
            List<Integer> frequencies = new ArrayList<>();

            for (int pro = 0; pro < WordArray.length - 1; pro++)
            {
                data = Data.getProWords(WordArray[pro]);
                words.clear();
                frequencies.clear();

                for (int i = 0; i < data.size(); i++)
                {
                    words.add(data.get(i).getWord());
                    frequencies.add(data.get(i).getFrequency());
                }

                if (words.contains(WordArray[pro + 1]))
                {
                    int index = words.indexOf(WordArray[pro + 1]);
                    frequencies.set(index, frequencies.get(index) + 1);
                }

                data.clear();
                for (int i = 0; i < words.size(); i++)
                {
                    WordData new_data = new WordData();
                    new_data.setWord(words.get(i));
                    new_data.setFrequency(frequencies.get(i));
                    data.add(new_data);
                }

                Data.saveProWords(data, WordArray[pro]);
            }

            for (int pre = 1; pre < WordArray.length; pre++)
            {
                data = Data.getPreWords(WordArray[pre]);
                words.clear();
                frequencies.clear();

                for (int i = 0; i < data.size(); i++)
                {
                    words.add(data.get(i).getWord());
                    frequencies.add(data.get(i).getFrequency());
                }

                if (words.contains(WordArray[pre - 1]))
                {
                    int index = words.indexOf(WordArray[pre - 1]);
                    frequencies.set(index, frequencies.get(index) + 1);
                }

                data.clear();
                for (int i = 0; i < words.size(); i++)
                {
                    WordData new_data = new WordData();
                    new_data.setWord(words.get(i));
                    new_data.setFrequency(frequencies.get(i));
                    data.add(new_data);
                }

                Data.savePreWords(data, WordArray[pre]);
            }
        }
    }

    static void Discourage()
    {
        if (!Logic.last_response.equals(""))
        {
            if (Logic.last_response.contains("."))
            {
                String str = Logic.last_response;
                StringBuilder sb = new StringBuilder(str).replace(Logic.last_response.indexOf("."), Logic.last_response.indexOf(".") + 1, " .");
                Logic.last_response = sb.toString();
            }
            else if (Logic.last_response.contains("?"))
            {
                String str = Logic.last_response;
                StringBuilder sb = new StringBuilder(str).replace(Logic.last_response.indexOf("?"), Logic.last_response.indexOf("?") + 1, " $");
                Logic.last_response = sb.toString();
            }
            else if (Logic.last_response.contains("!"))
            {
                String str = Logic.last_response;
                StringBuilder sb = new StringBuilder(str).replace(Logic.last_response.indexOf("!"), Logic.last_response.indexOf("!") + 1, " !");
                Logic.last_response = sb.toString();
            }

            String[] WordArray = Logic.last_response.split(" ");
            for (int i = 0; i < WordArray.length; i++)
            {
                switch (WordArray[i])
                {
                    case ",":
                        WordArray[i] = " ,";
                        break;
                    case ";":
                        WordArray[i] = " ;";
                        break;
                    case ":":
                        WordArray[i] = "";
                        break;
                    case "?":
                        WordArray[i] = " $";
                        break;
                    case "$":
                        WordArray[i] = " $";
                        break;
                    case "!":
                        WordArray[i] = " !";
                        break;
                    case ".":
                        WordArray[i] = " .";
                        break;
                }
            }

            List<WordData> data;
            List<String> words = new ArrayList<>();
            List<Integer> frequencies = new ArrayList<>();

            for (int pro = 0; pro < WordArray.length - 1; pro++)
            {
                data = Data.getProWords(WordArray[pro]);
                words.clear();
                frequencies.clear();

                for (int i = 0; i < data.size(); i++)
                {
                    words.add(data.get(i).getWord());
                    frequencies.add(data.get(i).getFrequency());
                }

                if (words.contains(WordArray[pro + 1]))
                {
                    int index = words.indexOf(WordArray[pro + 1]);
                    if (frequencies.get(index) > 0)
                    {
                        frequencies.set(index, frequencies.get(index) - 1);
                    }
                }

                data.clear();
                for (int i = 0; i < words.size(); i++)
                {
                    WordData new_data = new WordData();
                    new_data.setWord(words.get(i));
                    new_data.setFrequency(frequencies.get(i));
                    data.add(new_data);
                }

                Data.saveProWords(data, WordArray[pro]);
            }

            for (int pre = 1; pre < WordArray.length; pre++)
            {
                data = Data.getPreWords(WordArray[pre]);
                words.clear();
                frequencies.clear();

                for (int i = 0; i < data.size(); i++)
                {
                    words.add(data.get(i).getWord());
                    frequencies.add(data.get(i).getFrequency());
                }

                if (words.contains(WordArray[pre - 1]))
                {
                    int index = words.indexOf(WordArray[pre - 1]);
                    if (frequencies.get(index) > 0)
                    {
                        frequencies.set(index, frequencies.get(index) - 1);
                    }
                }

                data.clear();
                for (int i = 0; i < words.size(); i++)
                {
                    WordData new_data = new WordData();
                    new_data.setWord(words.get(i));
                    new_data.setFrequency(frequencies.get(i));
                    data.add(new_data);
                }

                Data.savePreWords(data, WordArray[pre]);
            }
        }
    }

    static void EraseMemory(File fileOrDirectory)
    {
        if (fileOrDirectory.isDirectory())
        {
            for (File child : fileOrDirectory.listFiles())
            {
                EraseMemory(child);
            }
        }

        if (!fileOrDirectory.getName().contains("Config"))
        {
            fileOrDirectory.delete();
        }
    }

    static void ToggleAdvanced(MenuItem item)
    {
        if (Logic.Advanced)
        {
            Logic.Advanced = false;
            item.setTitle("Advanced Mode: false");

            if (MainActivity.bl_DelayForever)
            {
                Data.setConfig("Infinite", "false", Logic.TopicBased.toString(), Logic.ConditionBased.toString(),
                        Logic.ProceduralBased.toString());
            }
            else
            {
                Data.setConfig((MainActivity.int_Time / 1000) + " seconds", "false", Logic.TopicBased.toString(), Logic.ConditionBased.toString(),
                        Logic.ProceduralBased.toString());
            }
        }
        else
        {
            Logic.Advanced = true;
            item.setTitle("Advanced Mode: true");

            if (MainActivity.bl_DelayForever)
            {
                Data.setConfig("Infinite", "true", Logic.TopicBased.toString(), Logic.ConditionBased.toString(),
                        Logic.ProceduralBased.toString());
            }
            else
            {
                Data.setConfig((MainActivity.int_Time / 1000) + " seconds", "true", Logic.TopicBased.toString(), Logic.ConditionBased.toString(),
                        Logic.ProceduralBased.toString());
            }
        }
    }

    static float dpToPx(Context context)
    {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) 200, metrics);
    }

    private static int GetMin(List<Integer> Integer_List)
    {
        int lowest_number = Integer.MAX_VALUE;

        if (Integer_List.size() > 0)
        {
            for (int i : Integer_List)
            {
                if (i <= lowest_number)
                {
                    lowest_number = i;
                }
            }
        }

        return lowest_number;
    }

    static int GetMax(List<Integer> Integer_List)
    {
        int highest_number = 0;

        if (Integer_List.size() > 0)
        {
            for (int i : Integer_List)
            {
                if (i >= highest_number)
                {
                    highest_number = i;
                }
            }
        }

        return highest_number;
    }

    static int Choose(List<Integer> Integer_List)
    {
        Random random;
        int max = GetMax(Integer_List);
        int result = 0;

        if (Integer_List.size() > 0)
        {
            for (int i : Integer_List)
            {
                random = new Random();
                int choice = random.nextInt(max);

                if (i >= choice)
                {
                    result = i;
                    break;
                }
            }
        }

        return result;
    }

    static boolean tryParseInt(String value)
    {
        try
        {
            Integer.parseInt(value);
            return true;
        }
        catch (NumberFormatException e)
        {
            return false;
        }
    }

    static String PunctuationFix_ForInput(String old_word)
    {
        String word = old_word;

        if (word.contains("$") && word.indexOf('$') > 0)
        {
            if (word.charAt(word.indexOf("$") - 1) != ' ')
            {
                String str = word;
                StringBuilder sb = new StringBuilder(str);
                sb.replace(str.indexOf('$'), str.indexOf('$') + 1, " $");
                word = sb.toString();
            }
        }
        else if (word.contains("?") && word.indexOf('?') > 0)
        {
            if (word.charAt(word.indexOf("?") - 1) != ' ')
            {
                String str = word;
                StringBuilder sb = new StringBuilder(str);
                sb.replace(str.indexOf('?'), str.indexOf('?') + 1, " $");
                word = sb.toString();
            }
        }
        else if (word.contains(".") && word.indexOf('.') > 0)
        {
            if (word.charAt(word.indexOf(".") - 1) != ' ')
            {
                String str = word;
                StringBuilder sb = new StringBuilder(str);
                sb.replace(str.indexOf('.'), str.indexOf('.') + 1, " .");
                word = sb.toString();
            }
        }
        else if (word.contains("!") && word.indexOf('!') > 0)
        {
            if (word.charAt(word.indexOf("!") - 1) != ' ')
            {
                String str = word;
                StringBuilder sb = new StringBuilder(str);
                sb.replace(str.indexOf('!'), str.indexOf('!') + 1, " !");
                word = sb.toString();
            }
        }
        else if (word.contains(",") && word.indexOf(',') > 0)
        {
            if (word.charAt(word.indexOf(",") - 1) != ' ')
            {
                String str = word;
                StringBuilder sb = new StringBuilder(str);
                sb.replace(str.indexOf(','), str.indexOf(',') + 1, " ,");
                word = sb.toString();
            }
        }
        else if (word.contains(";") && word.indexOf(';') > 0)
        {
            if (word.charAt(word.indexOf(";") - 1) != ' ')
            {
                String str = word;
                StringBuilder sb = new StringBuilder(str);
                sb.replace(str.indexOf(';'), str.indexOf(';') + 1, " ;");
                word = sb.toString();
            }
        }
        else if (word.equals("$"))
        {
            word = " $";
        }
        else if (word.equals("?"))
        {
            word = " $";
        }
        else if (word.equals("."))
        {
            word = " .";
        }
        else if (word.equals("!"))
        {
            word = " !";
        }
        else if (word.equals(","))
        {
            word = " ,";
        }
        else if (word.equals(";"))
        {
            word = " ;";
        }

        return word;
    }

    static List<String> Get_Related(List<String> topics)
    {
        List<String> OutputList = new ArrayList<>();
        List<String> InputList = new ArrayList<>();

        List<String> input = Data.getInputList();
        if (input.size() > 0)
        {
            //Get everything with matching topics
            for (int a = 0; a < input.size(); a++)
            {
                int count = 0;

                List<String> list = Data.getTopics(input.get(a));
                for (String result : list)
                {
                    for (int i = 0; i < topics.size(); i++)
                    {
                        if (result.equals(topics.get(i)))
                        {
                            count++;
                        }
                    }
                }

                if (count >= topics.size())
                {
                    InputList.add(input.get(a));
                }
            }

            if (InputList.size() > 0)
            {
                //Get highest matching topic frequency
                List<Integer> frequencies = new ArrayList<>();
                for (String result : InputList)
                {
                    List<String> output_topics = Data.getOutputList_OnlyTopics(result);
                    for (int i = 0; i < output_topics.size(); i++)
                    {
                        String[] topic = output_topics.get(i).split("~");
                        for (int t = 0; t < topics.size(); t++)
                        {
                            if (topic[0].equals("#" + topics.get(t)))
                            {
                                frequencies.add(Integer.parseInt(topic[1]));
                            }
                        }
                    }
                }

                int max = Util.GetMax(frequencies);
                if (max > 0)
                {
                    //Return whatever has a matching topic with the max frequency
                    for (String result : InputList)
                    {
                        List<String> output_topics = Data.getOutputList_OnlyTopics(result);
                        for (int i = 0; i < output_topics.size(); i++)
                        {
                            boolean found = false;

                            String[] topic = output_topics.get(i).split("~");
                            int num = Integer.parseInt(topic[1]);

                            for (int t = 0; t < topics.size(); t++)
                            {
                                if (topic[0].equals("#" + topics.get(t)) && num == max)
                                {
                                    found = true;

                                    List<String> output = Data.getOutputList_NoTopics(result);
                                    OutputList.addAll(output);
                                    break;
                                }
                            }

                            if (found)
                            {
                                break;
                            }
                        }
                    }
                }
            }
        }

        return OutputList;
    }

    private static List<String> Get_LowestFrequencies(String[] wordArray)
    {
        int int_lowest_f;
        List<String> lowest_words = new ArrayList<>();

        List<String> words = new ArrayList<>();
        List<Integer> frequencies = new ArrayList<>();

        List<WordData> data = Data.getWords();

        if (wordArray != null)
        {
            for (String word : wordArray)
            {
                for (int a2 = 0; a2 < data.size(); a2++)
                {
                    if (data.get(a2).getWord().equals(word))
                    {
                        words.add(data.get(a2).getWord());
                        frequencies.add(data.get(a2).getFrequency());
                    }
                }
            }
        }

        if (frequencies.size() > 0)
        {
            int_lowest_f = Util.GetMin(frequencies);
            List<Integer> RandomOnes = new ArrayList<>();
            for (int b = 0; b < frequencies.size(); b++)
            {
                if (frequencies.get(b) == int_lowest_f)
                {
                    RandomOnes.add(b);
                }
            }

            Boolean bl_accepted;
            for (int i = 0; i < RandomOnes.size(); i++)
            {
                String word = words.get(RandomOnes.get(i)).toLowerCase();

                bl_accepted = !(word.equals(" .") || word.equals(" $") || word.equals(" !") || word.equals(" ,"));

                if (bl_accepted && !lowest_words.contains(word))
                {
                    lowest_words.add(word);
                }
            }
        }

        return lowest_words;
    }

    static String Get_RandomWord()
    {
        List<String> words = new ArrayList<>();
        String lowest_word = "";

        List<WordData> data = Data.getWords();

        for (int a = 0; a < data.size(); a++)
        {
            words.add(data.get(a).getWord());
        }

        if (words.size() > 0)
        {
            Boolean bl_accepted;
            for (int i = 0; i < words.size(); i++)
            {
                Random random = new Random();
                int int_choice = random.nextInt(words.size());
                lowest_word = words.get(int_choice);

                bl_accepted = !(lowest_word.equals(" .") || lowest_word.equals(" $") || lowest_word.equals(" !") || lowest_word.equals(" ,"));

                if (bl_accepted)
                {
                    lowest_word = lowest_word.toLowerCase();
                    break;
                }
            }
        }

        return lowest_word;
    }

    static void UpdateInputList(String input)
    {
        String new_input = input;
        List<String> inputList = Data.getInputList();

        if (input.length() > 1)
        {
            new_input = Util.PunctuationFix_ForInput(new_input);
        }

        if (!inputList.contains(new_input))
        {
            inputList.add(new_input);
            Data.saveInputList(inputList);
        }
    }

    static void UpdateOutputList(String input)
    {
        String temp_input = input;
        String temp_last_response = Logic.last_response;

        if (temp_input.length() > 1)
        {
            temp_input = Util.PunctuationFix_ForInput(temp_input);
        }

        if (temp_last_response.length() > 1)
        {
            temp_last_response = Util.PunctuationFix_ForInput(temp_last_response);
        }

        //Add new input to previous response output list
        List<String> output = Data.getOutputList(temp_last_response);

        if (!output.contains(temp_input) && !temp_last_response.equals(temp_input))
        {
            output.add(temp_input);
            Data.saveOutput(output, temp_last_response);
        }
    }

    static String RulesCheck(String input)
    {
        String response = input;

        if (response.length() > 1)
        {
            //Learn which words should be capitalized by example
            String[] str_response_check = response.split(" ");
            for (int i = 1; i < str_response_check.length; i++)
            {
                String str_checked_word = str_response_check[i];
                if (!str_checked_word.equals(""))
                {
                    char capital_letter = str_checked_word.charAt(0);
                    if (Character.isUpperCase(capital_letter) && !str_checked_word.equals("I"))
                    {
                        List<String> words = new ArrayList<>();
                        List<Integer> frequencies = new ArrayList<>();

                        List<WordData> data = Data.getWords();
                        for (int a = 0; a < data.size(); a++)
                        {
                            words.add(data.get(a).getWord());
                            frequencies.add(data.get(a).getFrequency());
                        }

                        String str_lower_word = str_checked_word;
                        String str_capital_letter = Character.toString(capital_letter);
                        int int_high_frequency = 0;
                        int int_low_frequency = 0;
                        List<Integer> Frequency_List = new ArrayList<>();
                        str_capital_letter = str_capital_letter.toLowerCase();
                        String str = str_lower_word;
                        StringBuilder sb = new StringBuilder(str).replace(0, 0, "");
                        sb.insert(0, str_capital_letter);
                        str_lower_word = sb.toString();

                        for (int b = 0; b < words.size(); b++)
                        {
                            if (words.get(b).equals(str_lower_word))
                            {
                                int_low_frequency = frequencies.get(b);
                                Frequency_List.add(int_low_frequency);
                            }
                            else if (words.get(b).equals(str_checked_word))
                            {
                                int_high_frequency = frequencies.get(b);
                                Frequency_List.add(int_high_frequency);
                            }
                        }

                        if (Frequency_List.size() > 0)
                        {
                            if (Util.GetMax(Frequency_List) == int_low_frequency)
                            {
                                str_response_check[i] = str_lower_word;
                            }
                            else if (Util.GetMax(Frequency_List) == int_high_frequency)
                            {
                                str_response_check[i] = str_checked_word;
                            }
                        }
                    }
                }
            }

            String str_new_response = "";
            for (String word : str_response_check)
            {
                str_new_response = str_new_response.concat(word).concat(" ");
            }
            response = str_new_response;

            //Remove any spaces before commas
            while (response.contains(" ,"))
            {
                String str3 = response;
                StringBuilder sb3 = new StringBuilder(str3).replace(response.indexOf(" ,"), response.indexOf(" ,") + 2, ",");
                response = sb3.toString();
            }

            //Remove any spaces before colons
            while (response.contains(" :"))
            {
                String str3 = response;
                StringBuilder sb3 = new StringBuilder(str3).replace(response.indexOf(" :"), response.indexOf(" :") + 2, ":");
                response = sb3.toString();
            }

            //Remove any spaces before semicolons
            while (response.contains(" ;"))
            {
                String str3 = response;
                StringBuilder sb3 = new StringBuilder(str3).replace(response.indexOf(" ;"), response.indexOf(" ;") + 2, ";");
                response = sb3.toString();
            }

            if (response.length() > 0)
            {
                //Make sure the first word is capitalized
                char first_letter = response.charAt(0);
                if (!Character.isUpperCase(first_letter))
                {
                    String str_capital_letter = Character.toString(first_letter);
                    str_capital_letter = str_capital_letter.toUpperCase();
                    String str2 = response;
                    StringBuilder sb2 = new StringBuilder(str2).delete(0, 1);
                    sb2.insert(0, str_capital_letter);
                    response = sb2.toString();
                }

                //Remove any empty spaces at the end
                String str2 = response;
                StringBuilder sb2 = new StringBuilder(str2).delete(0, response.length() - 1);
                char last_letter = sb2.charAt(0);
                String str_last_letter = Character.toString(last_letter);

                while (str_last_letter.equals(" "))
                {
                    String str3 = response;
                    StringBuilder sb3 = new StringBuilder(str3).delete(response.length() - 1, response.length());
                    response = sb3.toString();

                    String str4 = response;
                    StringBuilder sb4 = new StringBuilder(str4).delete(0, response.length() - 1);
                    last_letter = sb4.charAt(0);

                    str_last_letter = Character.toString(last_letter);
                }

                //Set an ending punctuation if one does not exist
                if (!str_last_letter.equals(".") && !str_last_letter.equals("$") && !str_last_letter.equals("!"))
                {
                    String str3 = response;
                    StringBuilder sb3 = new StringBuilder(str3).insert(response.length(), ".");
                    response = sb3.toString();
                }
            }

            //Learn the best ending punctuation from example
            if (response.endsWith("$") || response.endsWith(".") || response.endsWith("!"))
            {
                List<String> inputList = Data.getInputList();
                if (inputList.size() > 0)
                {
                    int q_count = 0;
                    int p_count = 0;
                    int e_count = 0;

                    for (int i = 0; i < inputList.size(); i++)
                    {
                        String CurrentSentence = inputList.get(i);
                        String[] str_currentwords_check = CurrentSentence.split(" ");
                        String[] str_response_check2 = response.split(" ");

                        if (str_currentwords_check.length > 0 && str_response_check2.length > 0)
                        {
                            if (str_currentwords_check[0].equals(str_response_check2[0]))
                            {
                                switch (str_currentwords_check[str_currentwords_check.length - 1])
                                {
                                    case "$":
                                        q_count++;
                                        break;
                                    case ".":
                                        p_count++;
                                        break;
                                    case "!":
                                        e_count++;
                                        break;
                                }
                            }
                        }
                    }

                    if (q_count > p_count && q_count > e_count)
                    {
                        String str3 = response;
                        StringBuilder sb3 = new StringBuilder(str3).replace(response.length() - 1, response.length(), "$");
                        response = sb3.toString();
                    }
                    else if (p_count > q_count && p_count > e_count)
                    {
                        String str3 = response;
                        StringBuilder sb3 = new StringBuilder(str3).replace(response.length() - 1, response.length(), ".");
                        response = sb3.toString();
                    }
                    else if (e_count > q_count && e_count > p_count)
                    {
                        String str3 = response;
                        StringBuilder sb3 = new StringBuilder(str3).replace(response.length() - 1, response.length(), "!");
                        response = sb3.toString();
                    }
                }
            }

            //Replace any dollar signs with question marks
            while (response.contains("$"))
            {
                String str3 = response;
                StringBuilder sb3 = new StringBuilder(str3).replace(response.indexOf("$"), response.indexOf("$") + 1, "?");
                response = sb3.toString();
            }

            //Remove any spaces before ending punctuation
            while (response.contains(" ."))
            {
                String str3 = response;
                StringBuilder sb3 = new StringBuilder(str3).replace(response.indexOf(" ."), response.indexOf(" .") + 2, ".");
                response = sb3.toString();
            }
            while (response.contains(" ?"))
            {
                String str3 = response;
                StringBuilder sb3 = new StringBuilder(str3).replace(response.indexOf(" ?"), response.indexOf(" ?") + 2, "?");
                response = sb3.toString();
            }
            while (response.contains(" !"))
            {
                String str3 = response;
                StringBuilder sb3 = new StringBuilder(str3).replace(response.indexOf(" !"), response.indexOf(" !") + 2, "!");
                response = sb3.toString();
            }
        }

        return response;
    }

    static String HistoryRules(String old_string)
    {
        String new_string = old_string;

        if (new_string.length() > 1 && !new_string.equals(""))
        {
            //Remove any spaces before commas
            while (new_string.contains(" ,"))
            {
                String str3 = new_string;
                StringBuilder sb3 = new StringBuilder(str3).replace(new_string.indexOf(" ,"), new_string.indexOf(" ,") + 2, ",");
                new_string = sb3.toString();
            }

            //Remove any spaces before colons
            while (new_string.contains(" :"))
            {
                String str3 = new_string;
                StringBuilder sb3 = new StringBuilder(str3).replace(new_string.indexOf(" :"), new_string.indexOf(" :") + 2, ":");
                new_string = sb3.toString();
            }

            //Remove any spaces before semicolons
            while (new_string.contains(" ;"))
            {
                String str3 = new_string;
                StringBuilder sb3 = new StringBuilder(str3).replace(new_string.indexOf(" ;"), new_string.indexOf(" ;") + 2, ";");
                new_string = sb3.toString();
            }

            //Make sure the first word is capitalized
            char first_letter = new_string.charAt(0);
            if (!Character.isUpperCase(first_letter))
            {
                String str_capital_letter = Character.toString(first_letter);
                str_capital_letter = str_capital_letter.toUpperCase();
                String str2 = new_string;
                StringBuilder sb2 = new StringBuilder(str2).delete(0, 1);
                sb2.insert(0, str_capital_letter);
                new_string = sb2.toString();
            }

            //Remove any empty spaces at the end
            String str2 = new_string;
            StringBuilder sb2 = new StringBuilder(str2).delete(0, new_string.length() - 1);
            char last_letter = sb2.charAt(0);
            String str_last_letter = Character.toString(last_letter);

            while (str_last_letter.equals(" "))
            {
                String str3 = new_string;
                StringBuilder sb3 = new StringBuilder(str3).delete(new_string.length() - 1, new_string.length());
                new_string = sb3.toString();

                String str4 = new_string;
                StringBuilder sb4 = new StringBuilder(str4).delete(0, new_string.length() - 1);
                last_letter = sb4.charAt(0);

                str_last_letter = Character.toString(last_letter);
            }

            //Set an ending punctuation if one does not exist
            if (!str_last_letter.equals(".") && !str_last_letter.equals("$") && !str_last_letter.equals("!") && !str_last_letter.equals("?"))
            {
                String str3 = new_string;
                StringBuilder sb3 = new StringBuilder(str3).insert(new_string.length(), ".");
                new_string = sb3.toString();
            }

            //Replace any dollar signs with question marks
            while (new_string.contains("$"))
            {
                String str3 = new_string;
                StringBuilder sb3 = new StringBuilder(str3).replace(new_string.indexOf("$"), new_string.indexOf("$") + 1, "?");
                new_string = sb3.toString();
            }

            //Remove any spaces before ending punctuation
            while (new_string.contains(" ."))
            {
                String str3 = new_string;
                StringBuilder sb3 = new StringBuilder(str3).replace(new_string.indexOf(" ."), new_string.indexOf(" .") + 2, ".");
                new_string = sb3.toString();
            }
            while (new_string.contains(" ?"))
            {
                String str3 = new_string;
                StringBuilder sb3 = new StringBuilder(str3).replace(new_string.indexOf(" ?"), new_string.indexOf(" ?") + 2, "?");
                new_string = sb3.toString();
            }
            while (new_string.contains(" !"))
            {
                String str3 = new_string;
                StringBuilder sb3 = new StringBuilder(str3).replace(new_string.indexOf(" !"), new_string.indexOf(" !") + 2, "!");
                new_string = sb3.toString();
            }
        }

        return new_string;
    }

    static void GenTopics(String[] wordArray)
    {
        List<String> lowest_words = Util.Get_LowestFrequencies(wordArray);

        //Get new topics, but keep existing ones if found in input
        List<String> old_topics = new ArrayList<>();
        old_topics.addAll(Logic.topics);

        Logic.topics.clear();
        Logic.topics.addAll(lowest_words);

        for (String topic : old_topics)
        {
            for (String word : wordArray)
            {
                if (word.equals(topic))
                {
                    Logic.topics.add(topic);
                    break;
                }
            }
        }
    }

    static void GenTopics_ForThinking(String[] wordArray)
    {
        if (wordArray != null)
        {
            List<String> lowest_words = Util.Get_LowestFrequencies(wordArray);

            //Get new topics, but keep existing ones if found in input
            List<String> old_topics = new ArrayList<>();
            old_topics.addAll(Logic.topics_thinking);

            Logic.topics_thinking.clear();
            Logic.topics_thinking.addAll(lowest_words);

            for (String topic : old_topics)
            {
                for (String word : wordArray)
                {
                    if (word.equals(topic))
                    {
                        Logic.topics_thinking.add(topic);
                        break;
                    }
                }
            }
        }
    }

    static void AddTopics(String input)
    {
        String temp_input = input;

        if (temp_input.length() > 1)
        {
            temp_input = Util.PunctuationFix_ForInput(temp_input);
        }

        //Add lowest frequency word to current input's output list
        List<String> output = Data.getOutputList(temp_input);

        if (output.size() > 0)
        {
            for (int i = 0; i < output.size(); i++)
            {
                if (output.get(i).contains("#"))
                {
                    String[] topic = output.get(i).split("~");

                    boolean match = false;
                    for (String word : Logic.topics)
                    {
                        if (topic[0].equals("#" + word.toLowerCase()))
                        {
                            match = true;
                            int num = Integer.parseInt(topic[1]) + 1;
                            topic[1] = Integer.toString(num);
                        }
                    }

                    if (!match)
                    {
                        try
                        {
                            if (topic.length > 1)
                            {
                                if (topic[1] != null)
                                {
                                    int num = Integer.parseInt(topic[1]);
                                    if (num - 1 > 0)
                                    {
                                        num--;
                                        topic[1] = Integer.toString(num);
                                        output.set(i, topic[0] + "~" + topic[1]);
                                    }
                                    else
                                    {
                                        output.remove(i);
                                        i--;
                                    }
                                }
                            }
                        }
                        catch (NumberFormatException e)
                        {
                            //ignore since it's probably the user entering a #
                        }
                    }
                }
                else if (output.get(i).contains("~"))
                {
                    output.remove(i);
                    i--;
                }
            }

            Data.saveOutput(output, temp_input);
        }

        output = Data.getOutputList(temp_input);
        for (String word : Logic.topics)
        {
            boolean found = false;

            for (int i = 0; i < output.size(); i++)
            {
                if (output.get(i).contains("#"))
                {
                    String[] topic = output.get(i).split("~");
                    if (topic[0].equals("#" + word.toLowerCase()))
                    {
                        found = true;
                        break;
                    }
                }
            }

            if (!found)
            {
                if (!(word.equals(" .") || word.equals(" $") || word.equals(" !") || word.equals(" ,") || word.equals("")))
                {
                    output.add(0, "#" + word + "~7");
                }
            }
        }
        Data.saveOutput(output, temp_input);
    }
}
