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
    static void CleanMemory(final Context context)
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
                        File file = new File(context.getFilesDir().getAbsolutePath() + MemoryCheck + ".txt");

                        if (file.exists())
                        {
                            List<String> output = Data.getAllOutputs(MemoryCheck);
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

                File[] files = context.getFilesDir().listFiles();
                if (files != null)
                {
                    for (File file : files)
                    {
                        String MemoryCheck = file.getName();
                        int index = MemoryCheck.lastIndexOf('.');
                        if (index > 0)
                        {
                            MemoryCheck = MemoryCheck.substring(0, index);

                            List<String> output = Data.getAllOutputs(MemoryCheck);
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

    static void ClearLeftovers(Context context)
    {
        File file = new File(context.getFilesDir(), ".txt");
        if (file.exists())
        {
            file.delete();
        }

        file = new File(context.getFilesDir(), ",.txt");
        if (file.exists())
        {
            file.delete();
        }

        file = new File(context.getFilesDir(), "..txt");
        if (file.exists())
        {
            file.delete();
        }
    }

    static void Encourage()
    {
        if (!Logic.last_response.equals(""))
        {
            Logic.last_response = PunctuationFix_ForInput(Logic.last_response);
            String WordArray[] = Logic.last_response.split(" ");

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
            Logic.last_response = PunctuationFix_ForInput(Logic.last_response);
            String WordArray[] = Logic.last_response.split(" ");

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
        }
        else
        {
            Logic.Advanced = true;
            item.setTitle("Advanced Mode: true");
        }

        if (MainActivity.bl_DelayForever)
        {
            Data.setConfig("Infinite", Logic.Advanced.toString(), Logic.TopicBased.toString(), Logic.ConditionBased.toString(),
                    Logic.ProceduralBased.toString(), Logic.Speech.toString());
        }
        else
        {
            Data.setConfig((MainActivity.int_Time / 1000) + " seconds", Logic.Advanced.toString(), Logic.TopicBased.toString(), Logic.ConditionBased.toString(),
                    Logic.ProceduralBased.toString(), Logic.Speech.toString());
        }
    }

    static void ToggleSpeech(MenuItem item)
    {
        if (Logic.Speech)
        {
            Logic.Speech = false;
            item.setTitle("Speech: false");
        }
        else
        {
            Logic.Speech = true;
            item.setTitle("Speech: true");
        }

        if (MainActivity.bl_DelayForever)
        {
            Data.setConfig("Infinite", Logic.Advanced.toString(), Logic.TopicBased.toString(), Logic.ConditionBased.toString(),
                    Logic.ProceduralBased.toString(), Logic.Speech.toString());
        }
        else
        {
            Data.setConfig((MainActivity.int_Time / 1000) + " seconds", Logic.Advanced.toString(), Logic.TopicBased.toString(), Logic.ConditionBased.toString(),
                    Logic.ProceduralBased.toString(), Logic.Speech.toString());
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
        StringBuilder word = new StringBuilder(old_word);

        for (int i = 0; i < word.length(); i++)
        {
            if (i > 0)
            {
                if (word.charAt(i) == '$' &&
                    word.charAt(i - 1) != ' ')
                {
                    word.insert(i, ' ');
                }
                else if (word.charAt(i) == '?' &&
                         word.charAt(i - 1) != ' ')
                {
                    word.insert(i, ' ');
                }
                else if (word.charAt(i) == '.' &&
                         word.charAt(i - 1) != ' ')
                {
                    word.insert(i, ' ');
                }
                else if (word.charAt(i) == '!' &&
                         word.charAt(i - 1) != ' ')
                {
                    word.insert(i, ' ');
                }
                else if (word.charAt(i) == ',' &&
                         word.charAt(i - 1) != ' ')
                {
                    word.insert(i, ' ');
                }
                else if (word.charAt(i) == ';' &&
                         word.charAt(i - 1) != ' ')
                {
                    word.insert(i, ' ');
                }
            }
            else if (word.charAt(i) == '$' ||
                     word.charAt(i) == '?' ||
                     word.charAt(i) == '.' ||
                     word.charAt(i) == '!' ||
                     word.charAt(i) == ',' ||
                     word.charAt(i) == ';')
            {
                word.insert(i, ' ');
            }
        }

        return word.toString();
    }

    static List<String> Get_TopicRelated(List<String> topics)
    {
        List<String> Related = new ArrayList<>();
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

                                    List<String> output = Data.getOutputList_NoRelated(result);
                                    Related.addAll(output);
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

        return Related;
    }

    static List<String> Get_PhraseRelated(String phrase)
    {
        List<String> Related = new ArrayList<>();
        List<String> InputList = new ArrayList<>();

        List<String> inputList = Data.getInputList();
        for (String input : inputList)
        {
            //Get Outputs
            List<String> list = Data.getOutputList_NoRelated(input);
            for (String output : list)
            {
                if (output.equals(phrase))
                {
                    //Get related phrases
                    List<String> related = Data.getRelatedOutputs(input, phrase);
                    if (related.size() > 0)
                    {
                        Related.addAll(related);
                    }
                }
            }
        }

        return Related;
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

        String temp_last_response;
        if (IsMultiPhrase(Logic.last_response))
        {
            temp_last_response = Get_LastPhrase(Logic.last_response);
        }
        else
        {
            temp_last_response = Logic.last_response;
        }

        if (temp_last_response != null)
        {
            if (temp_input.length() > 1)
            {
                temp_input = Util.PunctuationFix_ForInput(temp_input);
            }

            if (temp_last_response.length() > 1)
            {
                temp_last_response = Util.PunctuationFix_ForInput(temp_last_response);
            }

            //Add new input to previous response output list
            List<String> output = Data.getAllOutputs(temp_last_response);

            if (!output.contains(temp_input) && !temp_last_response.equals(temp_input))
            {
                output.add(temp_input);
                Data.saveOutput(output, temp_last_response);
            }
        }
    }

    static void UpdateOutputList_MultiPhrase(List<String> inputs)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < inputs.size(); i++)
        {
            if (i < inputs.size() - 1)
            {
                sb.append(inputs.get(i)).append("^");
            }
            else
            {
                sb.append(inputs.get(i));
            }
        }

        String temp_input = sb.toString();
        String temp_input_first = inputs.get(0);

        String temp_last_response;
        if (IsMultiPhrase(Logic.last_response))
        {
            temp_last_response = Get_LastPhrase(Logic.last_response);
        }
        else
        {
            temp_last_response = Logic.last_response;
        }

        if (temp_last_response != null)
        {
            if (temp_input.length() > 1)
            {
                temp_input = PunctuationFix_ForInput(temp_input);
            }

            if (temp_input_first.length() > 1)
            {
                temp_input_first = PunctuationFix_ForInput(temp_input_first);
            }

            if (temp_last_response.length() > 1)
            {
                temp_last_response = PunctuationFix_ForInput(temp_last_response);
            }

            //Add new input to previous response
            List<String> output = Data.getAllOutputs(temp_last_response);

            if (!output.contains(temp_input_first) && !temp_last_response.equals(temp_input_first))
            {
                output.add(temp_input);
                Data.saveOutput(output, temp_last_response);
            }
            else if (!temp_last_response.equals(temp_input_first))
            {
                //Append new related outputs for existing input to previous response
                List<String> related_outputs = Data.getRelatedOutputs(temp_last_response, temp_input_first);

                for (int i = 0; i < inputs.size(); i++)
                {
                    temp_input = PunctuationFix_ForInput(inputs.get(i));
                    if (!related_outputs.contains(temp_input))
                    {
                        related_outputs.add(temp_input);
                    }
                }

                sb = new StringBuilder();
                sb.append(temp_input_first);
                for (String related_output : related_outputs)
                {
                    if (!related_output.equals(temp_input_first))
                    {
                        sb.append("^").append(related_output);
                    }
                }

                for (int i = 0; i < output.size(); i++)
                {
                    if (output.get(i).contains(temp_input_first))
                    {
                        output.set(i, sb.toString());
                        break;
                    }
                }

                Data.saveOutput(output, temp_last_response);
            }
        }
    }

    static boolean IsMultiPhrase(String input)
    {
        int count = 0;
        for (int i = 0; i < input.length(); i++)
        {
            if (i < input.length() &&
                (input.charAt(i) == '.' ||
                 input.charAt(i) == '!' ||
                 input.charAt(i) == '$' ||
                 input.charAt(i) == '?'))
            {
                count++;
            }
        }

        if (count > 1)
        {
            return true;
        }

        return false;
    }

    static boolean IsMultiPhrase(String[] wordArray)
    {
        if (wordArray != null)
        {
            int count = 0;
            for (int i = 0; i < wordArray.length; i++)
            {
                if (i < wordArray.length &&
                    (wordArray[i].equals(" .") ||
                     wordArray[i].equals(" !") ||
                     wordArray[i].equals(" $") ||
                     wordArray[i].equals(" ?")))
                {
                    count++;
                }
            }

            if (count > 1)
            {
                return true;
            }
        }

        return false;
    }

    static String Get_LastPhrase(String input)
    {
        if (input.length() > 1)
        {
            int startIndex = input.length() - 1;

            for (int i = input.length() - 1; i > 0; i--)
            {
                if ((input.charAt(i) == '.' ||
                     input.charAt(i) == '!' ||
                     input.charAt(i) == '$' ||
                     input.charAt(i) == '?') &&
                    (input.charAt(i - 1) != '.' &&
                     input.charAt(i - 1) != '!' &&
                     input.charAt(i - 1) != '$' &&
                     input.charAt(i - 1) != '?'))
                {
                    startIndex = i;
                }
            }

            if (input.length() > 3)
            {
                for (int i = startIndex - 1; i > 0; i--)
                {
                    if (input.charAt(i) == '.' ||
                        input.charAt(i) == '!' ||
                        input.charAt(i) == '$' ||
                        input.charAt(i) == '?')
                    {
                        return input.substring(i + 2);
                    }
                }
            }
        }

        return null;
    }

    static String Get_FirstPhrase(String input)
    {
        if (input.length() > 2)
        {
            for (int i = 0; i < input.length(); i++)
            {
                if (input.charAt(i) == '.' ||
                    input.charAt(i) == '!' ||
                    input.charAt(i) == '$' ||
                    input.charAt(i) == '?')
                {
                    return input.substring(0, i + 1);
                }
            }
        }

        return null;
    }

    static String RulesCheck(String input)
    {
        StringBuilder response = new StringBuilder(input);

        if (response.length() > 1 &&
            !response.toString().equals(""))
        {
            //Replace any dollar signs with question marks
            while (response.indexOf("$") > 0)
            {
                response.replace(response.indexOf("$"), response.indexOf("$") + 1, "?");
            }

            //Make sure the first word is capitalized
            if (Util.IsMultiPhrase(response.toString()))
            {
                String wordArray[];
                wordArray = PunctuationFix_ForInput(response.toString()).split(" ");
                StringBuilder sb = new StringBuilder();

                List<String> phrases = new ArrayList<>();
                for (String word : wordArray)
                {
                    if (word.equals(".") || word.equals("!") || word.equals("?"))
                    {
                        sb.append(word).append(" ");
                        phrases.add(sb.toString());
                        sb = new StringBuilder();
                    }
                    else
                    {
                        sb.append(word).append(" ");
                    }
                }

                List<String> results = new ArrayList<>();
                for (String phrase : phrases)
                {
                    String new_phrase = phrase;
                    char first_letter = phrase.charAt(0);
                    if (!Character.isUpperCase(first_letter))
                    {
                        String str_capital_letter = Character.toString(first_letter);
                        str_capital_letter = str_capital_letter.toUpperCase();
                        sb = new StringBuilder(phrase).replace(0, 1, str_capital_letter);
                        new_phrase = sb.toString();
                    }

                    results.add(new_phrase);
                }

                response = new StringBuilder();
                for (String result : results)
                {
                    response.append(result);
                }
            }
            else
            {
                char first_letter = response.charAt(0);
                if (!Character.isUpperCase(first_letter))
                {
                    String str_capital_letter = Character.toString(first_letter);
                    str_capital_letter = str_capital_letter.toUpperCase();
                    response.replace(0, 1, str_capital_letter);
                }
            }

            //Remove any spaces before commas
            while (response.indexOf(" ,") > 0)
            {
                response.replace(response.indexOf(" ,"), response.indexOf(" ,") + 2, ",");
            }

            //Remove any spaces before semicolons
            while (response.indexOf(" ;") > 0)
            {
                response.replace(response.indexOf(" ;"), response.indexOf(" ;") + 2, ";");
            }

            //Remove any spaces before periods
            while (response.indexOf(" .") > 0)
            {
                response.replace(response.indexOf(" ."), response.indexOf(" .") + 2, ".");
            }

            //Remove any spaces before question marks
            while (response.indexOf(" ?") > 0)
            {
                response.replace(response.indexOf(" ?"), response.indexOf(" ?") + 2, "?");
            }

            //Remove any spaces before exclamation marks
            while (response.indexOf(" !") > 0)
            {
                response.replace(response.indexOf(" !"), response.indexOf(" !") + 2, "!");
            }

            //Remove any empty spaces at the end
            if (response.length() > 0)
            {
                char last_letter = response.charAt(response.length() - 1);
                String str_last_letter = Character.toString(last_letter);

                while (str_last_letter.equals(" "))
                {
                    response.delete(response.length() - 1, response.length());

                    if (response.length() > 0)
                    {
                        last_letter = response.charAt(response.length() - 1);
                        str_last_letter = Character.toString(last_letter);
                    }
                }

                //Set an ending punctuation if one does not exist
                if (!str_last_letter.equals(".") && !str_last_letter.equals("?") && !str_last_letter.equals("!"))
                {
                    response.insert(response.length(), ".");
                }
            }
        }

        return response.toString();
    }

    static List<String> GenTopics(String[] wordArray, List<String> topics)
    {
        List<String> lowest_words = Util.Get_LowestFrequencies(wordArray);

        //Get new topics, but keep existing ones if found in input
        List<String> old_topics = new ArrayList<>();

        if (topics.size() > 0)
        {
            old_topics.addAll(topics);
        }

        List<String> new_topics = new ArrayList<>();
        new_topics.addAll(lowest_words);

        for (String topic : old_topics)
        {
            for (String word : wordArray)
            {
                if (word.equals(topic))
                {
                    new_topics.add(topic);
                    break;
                }
            }
        }

        return new_topics;
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

    static void AddTopics(String input, List<String> topics)
    {
        String temp_input = input;

        if (temp_input.length() > 1)
        {
            temp_input = Util.PunctuationFix_ForInput(temp_input);
        }

        //Add lowest frequency word to current input's output list
        List<String> output = Data.getAllOutputs(temp_input);

        if (output.size() > 0)
        {
            for (int i = 0; i < output.size(); i++)
            {
                if (output.get(i).contains("#"))
                {
                    String[] topic = output.get(i).split("~");

                    boolean match = false;
                    for (String word : topics)
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

        //Add current topics if missing
        output = Data.getAllOutputs(temp_input);
        for (String word : topics)
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
