package com.oblivionburn.nlp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

class Data
{
    //Config Data
    static void initConfig()
    {
        BufferedWriter writer;
        try
        {
            File file = new File(MainActivity.Brain_dir, "Config.ini");
            writer = new BufferedWriter(new FileWriter(file));
            writer.write("Delay:10 seconds");
            writer.newLine();
            writer.write("Advanced:Off");
            writer.close();
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
    }

    static void setConfig(String delay, String advanced)
    {
        BufferedWriter writer;
        try
        {
            File file = new File(MainActivity.Brain_dir, "Config.ini");
            if (!file.exists())
            {
                file.createNewFile();
            }
            writer = new BufferedWriter(new FileWriter(file));
            writer.write("Delay:" + delay);
            writer.newLine();
            writer.write("Advanced:" + advanced);
            writer.close();
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
    }

    static String getDelay()
    {
        String Config[];
        String result = "";

        File file = new File(MainActivity.Brain_dir, "Config.ini");

        try
        {
            BufferedReader br = new BufferedReader(new FileReader(file));

            String line;
            while ((line = br.readLine()) != null)
            {
                if (line.contains("Delay:"))
                {
                    Config = line.split(":");
                    result = Config[1];
                }
            }
            br.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return result;
    }

    static String getAdvanced()
    {
        String Config[];
        String result = "";

        File file = new File(MainActivity.Brain_dir, "Config.ini");

        try
        {
            BufferedReader br = new BufferedReader(new FileReader(file));

            String line;
            while ((line = br.readLine()) != null)
            {
                if (line.contains("Advanced:"))
                {
                    Config = line.split(":");
                    result = Config[1];
                }
            }
            br.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return result;
    }

    //Words Data
    static void saveWords(List<WordData> data)
    {
        BufferedWriter writer;
        try
        {
            File file = new File(MainActivity.Brain_dir, "Words.txt");
            if (!file.exists())
            {
                file.createNewFile();
            }
            writer = new BufferedWriter(new FileWriter(file));
            String WordsLine;

            for (int i = 0; i < data.size(); i++)
            {
                WordsLine = data.get(i).getWord() + "~" + data.get(i).getFrequency().toString() + "\n";
                writer.write(WordsLine);
                writer.newLine();
            }
            writer.close();
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
    }

    static List<WordData> getWords()
    {
        List<String> words = new ArrayList<>();
        List<Integer> frequencies = new ArrayList<>();
        List<WordData> data = new ArrayList<>();

        String WordSet[];

        File file = new File(MainActivity.Brain_dir, "Words.txt");

        try
        {
            BufferedReader br = new BufferedReader(new FileReader(file));

            String line;
            while ((line = br.readLine()) != null)
            {
                if (line.contains("~"))
                {
                    WordSet = line.split("~");
                    if (!WordSet[1].equals("") && Util.tryParseInt(WordSet[1]))
                    {
                        int frequency = Integer.parseInt(WordSet[1]);
                        words.add(WordSet[0]);
                        frequencies.add(frequency);
                    }
                }
            }
            for (int i = 0; i < words.size(); i++)
            {
                WordData newset = new WordData();
                newset.setWord(words.get(i));
                newset.setFrequency(frequencies.get(i));
                data.add(newset);
            }
            br.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return data;
    }

    //PreWords Data
    static void savePreWords(List<WordData> data, String word)
    {
        BufferedWriter writer;
        try
        {
            File file = new File(MainActivity.Brain_dir, "Pre-" + word + ".txt");
            if (!file.exists())
            {
                file.createNewFile();
            }
            writer = new BufferedWriter(new FileWriter(file));
            String WordsLine;

            for (int i = 0; i < data.size(); i++)
            {
                WordsLine = data.get(i).getWord() + "~" + data.get(i).getFrequency().toString();
                writer.write(WordsLine);
                writer.newLine();
            }
            writer.close();
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
    }

    static List<WordData> getPreWords(String word)
    {
        List<String> words = new ArrayList<>();
        List<Integer> frequencies = new ArrayList<>();
        List<WordData> data = new ArrayList<>();

        String WordSet[];
        File file = new File(MainActivity.Brain_dir, "Pre-" + word + ".txt");

        if (file.isFile())
        {
            try
            {
                BufferedReader br = new BufferedReader(new FileReader(file));

                String line;
                while ((line = br.readLine()) != null)
                {
                    if (line.contains("~"))
                    {
                        WordSet = line.split("~");
                        if (!WordSet[1].equals("") && Util.tryParseInt(WordSet[1]))
                        {
                            int frequency = Integer.parseInt(WordSet[1]);
                            words.add(WordSet[0]);
                            frequencies.add(frequency);
                        }
                    }
                }

                for (int i = 0; i < words.size(); i++)
                {
                    WordData newset = new WordData();
                    newset.setWord(words.get(i));
                    newset.setFrequency(frequencies.get(i));
                    data.add(newset);
                }

                br.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        return data;
    }

    //ProWords Data
    static void saveProWords(List<WordData> data, String word)
    {
        BufferedWriter writer;
        try
        {
            File file = new File(MainActivity.Brain_dir, "Pro-" + word + ".txt");
            if (!file.exists())
            {
                file.createNewFile();
            }
            writer = new BufferedWriter(new FileWriter(file));
            String WordsLine;

            for (int i = 0; i < data.size(); i++)
            {
                WordsLine = data.get(i).getWord() + "~" + data.get(i).getFrequency().toString();
                writer.write(WordsLine);
                writer.newLine();
            }
            writer.close();
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
    }

    static List<WordData> getProWords(String word)
    {
        List<String> words = new ArrayList<>();
        List<Integer> frequencies = new ArrayList<>();
        List<WordData> data = new ArrayList<>();

        String WordSet[];
        File file = new File(MainActivity.Brain_dir, "Pro-" + word + ".txt");

        if (file.isFile())
        {
            try
            {
                BufferedReader br = new BufferedReader(new FileReader(file));

                String line;
                while ((line = br.readLine()) != null)
                {
                    if (line.contains("~"))
                    {
                        WordSet = line.split("~");
                        if (!WordSet[1].equals("") && Util.tryParseInt(WordSet[1]))
                        {
                            int frequency = Integer.parseInt(WordSet[1]);
                            words.add(WordSet[0]);
                            frequencies.add(frequency);
                        }
                    }
                }
                for (int i = 0; i < words.size(); i++)
                {
                    WordData newset = new WordData();
                    newset.setWord(words.get(i));
                    newset.setFrequency(frequencies.get(i));
                    data.add(newset);
                }
                br.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        return data;
    }

    //Input Data
    static void saveInputList(List<String> input)
    {
        BufferedWriter writer;
        try
        {
            File file = new File(MainActivity.Brain_dir, "InputList.txt");
            if (!file.exists())
            {
                file.createNewFile();
            }
            writer = new BufferedWriter(new FileWriter(file));

            for (int i = 0; i < input.size(); i++)
            {
                writer.write(input.get(i));
                writer.newLine();
            }
            writer.close();
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
    }

    static List<String> getInputList()
    {
        List<String> input = new ArrayList<>();

        try
        {
            File file = new File(MainActivity.Brain_dir, "InputList.txt");
            BufferedReader br = new BufferedReader(new FileReader(file));

            String line;
            while ((line = br.readLine()) != null)
            {
                if (!line.equals(""))
                {
                    input.add(line);
                }
            }
            br.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return input;
    }

    //Output Data
    static void saveOutput(List<String> output, String input)
    {
        BufferedWriter writer;
        try
        {
            File file = new File(MainActivity.Brain_dir, input + ".txt");
            if (!file.exists())
            {
                file.createNewFile();
            }
            writer = new BufferedWriter(new FileWriter(file));

            for (int i = 0; i < output.size(); i++)
            {
                writer.write(output.get(i));
                writer.newLine();
            }
            writer.close();
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
    }

    static List<String> getOutputList(String input)
    {
        List<String> output = new ArrayList<>();

        File file = new File(MainActivity.Brain_dir, input + ".txt");

        if (file.isFile())
        {
            try
            {
                BufferedReader br = new BufferedReader(new FileReader(file));

                String line;
                while ((line = br.readLine()) != null)
                {
                    if (!line.equals(""))
                    {
                        output.add(line);
                    }
                }
                br.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            BufferedWriter writer;
            try
            {
                writer = new BufferedWriter(new FileWriter(file));
                String WordsLine = "";
                writer.write(WordsLine);
                writer.newLine();
                writer.close();
            }
            catch(IOException ex)
            {
                ex.printStackTrace();
            }
        }

        return output;
    }

    static List<String> getOutputList_NoTopics(String input)
    {
        List<String> output = new ArrayList<>();

        File file = new File(MainActivity.Brain_dir, input + ".txt");

        if (file.isFile())
        {
            try
            {
                BufferedReader br = new BufferedReader(new FileReader(file));

                String line;
                while ((line = br.readLine()) != null)
                {
                    if (!line.equals("") && !line.contains("#"))
                    {
                        output.add(line);
                    }
                }
                br.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        return output;
    }

    static List<String> getOutputList_OnlyTopics(String input)
    {
        List<String> output = new ArrayList<>();

        File file = new File(MainActivity.Brain_dir, input + ".txt");

        if (file.isFile())
        {
            try
            {
                BufferedReader br = new BufferedReader(new FileReader(file));

                String line;
                while ((line = br.readLine()) != null)
                {
                    if (line.contains("#"))
                    {
                        output.add(line);
                    }
                }
                br.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        return output;
    }

    static List<String> getTopics(String input)
    {
        List<String> result = new ArrayList<>();

        File file = new File(MainActivity.Brain_dir, input + ".txt");

        if (file.isFile())
        {
            try
            {
                BufferedReader br = new BufferedReader(new FileReader(file));

                String line;
                while ((line = br.readLine()) != null)
                {
                    if (line.contains("#"))
                    {
                        int index = line.indexOf("~");
                        result.add(line.substring(1, index));
                    }
                }
                br.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        return result;
    }

    //History Data
    static void saveHistory(List<String> history)
    {
        BufferedWriter writer;
        DateFormat f = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
        String currentDate = f.format(new Date());

        try
        {
            File file = new File(MainActivity.History_dir, currentDate + ".txt");
            if (!file.exists())
            {
                file.createNewFile();
            }
            writer = new BufferedWriter(new FileWriter(file));

            for (int i = 0; i < history.size(); i++)
            {
                writer.write(history.get(i));
                writer.newLine();
            }
            writer.close();
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
    }

    static List<String> getHistory()
    {
        List<String> history = new ArrayList<>();

        DateFormat f = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
        String currentDate = f.format(new Date());
        File file = new File(MainActivity.History_dir, currentDate + ".txt");

        if (file.isFile())
        {
            try
            {
                BufferedReader br = new BufferedReader(new FileReader(file));

                String line;
                while ((line = br.readLine()) != null)
                {
                    if (!line.equals(""))
                    {
                        history.add(line);
                    }
                }
                br.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            BufferedWriter writer;
            try
            {
                writer = new BufferedWriter(new FileWriter(file));
                String WordsLine = "";
                writer.write(WordsLine);
                writer.newLine();
                writer.close();
            }
            catch(IOException ex)
            {
                ex.printStackTrace();
            }
        }

        return history;
    }

    //Thought Data
    static void saveThoughts(List<String> thoughts)
    {
        BufferedWriter writer;
        DateFormat f = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
        String currentDate = f.format(new Date());

        try
        {
            File file = new File(MainActivity.Thought_dir, currentDate + ".txt");
            if (!file.exists())
            {
                file.createNewFile();
            }
            writer = new BufferedWriter(new FileWriter(file));

            for (int i = 0; i < thoughts.size(); i++)
            {
                writer.write(thoughts.get(i));
                writer.newLine();
            }
            writer.close();
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
    }

    static List<String> getThoughts()
    {
        List<String> thoughts = new ArrayList<>();

        DateFormat f = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
        String currentDate = f.format(new Date());
        File file = new File(MainActivity.Thought_dir, currentDate + ".txt");

        if (file.isFile())
        {
            try
            {
                BufferedReader br = new BufferedReader(new FileReader(file));

                String line;
                while ((line = br.readLine()) != null)
                {
                    if (!line.equals(""))
                    {
                        thoughts.add(line);
                    }
                }
                br.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            BufferedWriter writer;
            try
            {
                writer = new BufferedWriter(new FileWriter(file));
                String WordsLine = "";
                writer.write(WordsLine);
                writer.newLine();
                writer.close();
            }
            catch(IOException ex)
            {
                ex.printStackTrace();
            }
        }

        return thoughts;
    }

}
