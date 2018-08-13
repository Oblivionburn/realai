package com.oblivionburn.nlp;

import android.content.Context;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

class Data
{
    private static String dir;

    static void initData(Context context)
    {
        dir = context.getFilesDir().getAbsolutePath();
    }

    //Config Data
    static void initConfig()
    {
        StringBuilder fileContents = new StringBuilder();
        fileContents.append("Delay:10 seconds").append("\n");
        fileContents.append("Advanced:false").append("\n");
        fileContents.append("Topic Response Method:true").append("\n");
        fileContents.append("Condition Response Method:true").append("\n");
        fileContents.append("Procedural Response Method:true").append("\n");

        String fileName = dir + "/Brain/Config.ini";
        File file = new File(fileName);

        try
        {
            if (!file.exists())
            {
                file.createNewFile();
            }

            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(fileContents.toString().getBytes());
            outputStream.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    static void setConfig(String delay, String advanced, String topic, String condition, String procedural)
    {
        StringBuilder fileContents = new StringBuilder();
        fileContents.append("Delay:").append(delay).append("\n");
        fileContents.append("Advanced:").append(advanced).append("\n");
        fileContents.append("Topic Response Method:").append(topic).append("\n");
        fileContents.append("Condition Response Method:").append(condition).append("\n");
        fileContents.append("Procedural Response Method:").append(procedural).append("\n");

        String fileName = dir + "/Brain/Config.ini";
        File file = new File(fileName);

        try
        {
            if (!file.exists())
            {
                file.createNewFile();
            }

            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(fileContents.toString().getBytes());
            outputStream.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    static String getDelay()
    {
        String result = "";
        String fileName = dir + "/Brain/Config.ini";
        File file = new File(fileName);

        if (file.exists())
        {
            try
            {
                FileInputStream inputStream = new FileInputStream(file);
                DataInputStream dataStream = new DataInputStream(inputStream);
                BufferedReader reader = new BufferedReader(new InputStreamReader(dataStream));

                String line;
                while ((line = reader.readLine()) != null)
                {
                    if (line.contains("Delay:"))
                    {
                        String Config[] = line.split(":");
                        result = Config[1];
                        break;
                    }
                }
                dataStream.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        return result;
    }

    static String getAdvanced()
    {
        String result = "";
        String fileName = dir + "/Brain/Config.ini";
        File file = new File(fileName);

        if (file.exists())
        {
            try
            {
                FileInputStream inputStream = new FileInputStream(file);
                DataInputStream dataStream = new DataInputStream(inputStream);
                BufferedReader reader = new BufferedReader(new InputStreamReader(dataStream));

                String line;
                while ((line = reader.readLine()) != null)
                {
                    if (line.contains("Advanced:"))
                    {
                        String Config[] = line.split(":");
                        result = Config[1];
                        break;
                    }
                }
                dataStream.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        return result;
    }

    static String getTopicBased()
    {
        String result = "";
        String fileName = dir + "/Brain/Config.ini";
        File file = new File(fileName);

        if (file.exists())
        {
            try
            {
                FileInputStream inputStream = new FileInputStream(file);
                DataInputStream dataStream = new DataInputStream(inputStream);
                BufferedReader reader = new BufferedReader(new InputStreamReader(dataStream));

                String line;
                while ((line = reader.readLine()) != null)
                {
                    if (line.contains("Topic Response Method:"))
                    {
                        String Config[] = line.split(":");
                        result = Config[1];
                        break;
                    }
                }
                dataStream.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        return result;
    }

    static String getConditionBased()
    {
        String result = "";
        String fileName = dir + "/Brain/Config.ini";
        File file = new File(fileName);

        if (file.exists())
        {
            try
            {
                FileInputStream inputStream = new FileInputStream(file);
                DataInputStream dataStream = new DataInputStream(inputStream);
                BufferedReader reader = new BufferedReader(new InputStreamReader(dataStream));

                String line;
                while ((line = reader.readLine()) != null)
                {
                    if (line.contains("Condition Response Method:"))
                    {
                        String Config[] = line.split(":");
                        result = Config[1];
                        break;
                    }
                }
                dataStream.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        return result;
    }

    static String getProceduralBased()
    {
        String result = "";
        String fileName = dir + "/Brain/Config.ini";
        File file = new File(fileName);

        if (file.exists())
        {
            try
            {
                FileInputStream inputStream = new FileInputStream(file);
                DataInputStream dataStream = new DataInputStream(inputStream);
                BufferedReader reader = new BufferedReader(new InputStreamReader(dataStream));

                String line;
                while ((line = reader.readLine()) != null)
                {
                    if (line.contains("Procedural Response Method:"))
                    {
                        String Config[] = line.split(":");
                        result = Config[1];
                        break;
                    }
                }
                dataStream.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        return result;
    }

    //Words Data
    static void saveWords(List<WordData> data)
    {
        String fileName = dir + "/Brain/Words.txt";
        File file = new File(fileName);

        BufferedWriter writer;
        try
        {
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
        List<WordData> data = new ArrayList<>();
        List<String> words = new ArrayList<>();
        List<Integer> frequencies = new ArrayList<>();

        String fileName = dir + "/Brain/Words.txt";
        File file = new File(fileName);

        try
        {
            BufferedReader br = new BufferedReader(new FileReader(file));

            String line;
            while ((line = br.readLine()) != null)
            {
                if (line.contains("~"))
                {
                    String WordSet[] = line.split("~");
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
        String fileName = dir + "/Brain/Pre-" + word + ".txt";
        File file = new File(fileName);

        BufferedWriter writer;
        try
        {
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

        String fileName = dir + "/Brain/Pre-" + word + ".txt";
        File file = new File(fileName);

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
                        String WordSet[] = line.split("~");
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
        String fileName = dir + "/Brain/Pro-" + word + ".txt";
        File file = new File(fileName);

        BufferedWriter writer;
        try
        {
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

        String fileName = dir + "/Brain/Pro-" + word + ".txt";
        File file = new File(fileName);

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
                        String WordSet[] = line.split("~");
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
        String fileName = dir + "/Brain/InputList.txt";
        File file = new File(fileName);

        BufferedWriter writer;
        try
        {
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
        String fileName = dir + "/Brain/InputList.txt";
        File file = new File(fileName);

        try
        {
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
        String fileName = dir + "/Brain/" + input + ".txt";
        File file = new File(fileName);

        BufferedWriter writer;
        try
        {
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

    static List<String> getAllOutputs(String input)
    {
        List<String> output = new ArrayList<>();
        String fileName = dir + "/Brain/" + input + ".txt";
        File file = new File(fileName);

        if (file.exists())
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

        return output;
    }

    static List<String> getOutputList_NoRelated(String input)
    {
        List<String> output = new ArrayList<>();
        String fileName = dir + "/Brain/" + input + ".txt";
        File file = new File(fileName);

        if (file.exists())
        {
            try
            {
                BufferedReader br = new BufferedReader(new FileReader(file));

                String line;
                while ((line = br.readLine()) != null)
                {
                    if (!line.equals("") &&
                        !line.contains("#"))
                    {
                        if (line.contains("^"))
                        {
                            int index = line.indexOf("^");
                            output.add(line.substring(0, index));
                        }
                        else
                        {
                            output.add(line);
                        }
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
        String fileName = dir + "/Brain/" + input + ".txt";
        File file = new File(fileName);

        if (file.exists())
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

    static List<String> getRelatedOutputs(String input, String phrase)
    {
        List<String> output = new ArrayList<>();
        String fileName = dir + "/Brain/" + input + ".txt";
        File file = new File(fileName);

        if (file.exists())
        {
            try
            {
                BufferedReader br = new BufferedReader(new FileReader(file));

                String line;
                while ((line = br.readLine()) != null)
                {
                    if (!line.equals(""))
                    {
                        if (line.contains(phrase) &&
                                line.contains("^"))
                        {
                            String[] outputs = line.split("\\^");
                            for (String new_output : outputs)
                            {
                                output.add(new_output);
                            }
                        }
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
        String fileName = dir + "/Brain/" + input + ".txt";
        File file = new File(fileName);

        if (file.exists())
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
                        if (index > 1)
                        {
                            result.add(line.substring(1, index));
                        }
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

        String history_dir = dir + "/Brain/History/";
        String fileName = history_dir + currentDate + ".txt";
        File file = new File(fileName);

        try
        {
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

        String history_dir = dir + "/Brain/History/";
        String fileName = history_dir + currentDate + ".txt";
        File file = new File(fileName);

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

        return history;
    }

    //Thought Data
    static void saveThoughts(List<String> thoughts)
    {
        BufferedWriter writer;
        DateFormat f = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
        String currentDate = f.format(new Date());

        String thoughts_dir = dir + "/Brain/Thoughts/";
        String fileName = thoughts_dir + currentDate + ".txt";
        File file = new File(fileName);

        try
        {
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

        String thoughts_dir = dir + "/Brain/Thoughts/";
        String fileName = thoughts_dir + currentDate + ".txt";
        File file = new File(fileName);

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

        return thoughts;
    }

}
