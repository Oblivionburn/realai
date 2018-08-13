package com.oblivionburn.nlp;

import android.text.TextUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class Logic
{
    //Variables
    static String last_response = "";
    static Boolean Initiation = false;
    static Boolean NewInput = false;
    static Boolean UserInput = false;
    static Boolean Advanced = false;
    static Boolean TopicBased = true;
    static Boolean ConditionBased = true;
    static Boolean ProceduralBased = true;

    static String last_response_thinking = "";

    static List<String> topics = new ArrayList<>();
    static List<String> topics_thinking = new ArrayList<>();

    static String[] prepInput(String input)
    {
        String[] wordArray = prepInput_CreateWordArray(input);
        List<String> inputs = handle_MultiPhrase(wordArray);

        if (inputs != null)
        {
            if (UserInput || Advanced)
            {
                for (String new_input : inputs)
                {
                    wordArray = prepInput_CreateWordArray(new_input);
                    Util.UpdateInputList(new_input);
                    prepInput_UpdateExistingFrequencies(wordArray);
                    prepInput_AddNewWords(wordArray);
                    prepInput_UpdatePreWords(wordArray);
                    prepInput_UpdateProWords(wordArray);
                }

                if (NewInput)
                {
                    Util.UpdateOutputList_MultiPhrase(inputs);
                }
            }
            else if (inputs.size() > 0)
            {
                String new_input = inputs.get(inputs.size() - 1);
                wordArray = prepInput_CreateWordArray(new_input);
            }
        }
        else if (wordArray != null &&
                (UserInput || Advanced))
        {
            Util.UpdateInputList(input);
            prepInput_UpdateExistingFrequencies(wordArray);
            prepInput_AddNewWords(wordArray);
            prepInput_UpdatePreWords(wordArray);
            prepInput_UpdateProWords(wordArray);

            if (NewInput)
            {
                Util.UpdateOutputList(input);
            }
        }

        return wordArray;
    }

    private static List<String> handle_MultiPhrase(String[] wordArray)
    {
        boolean multi_phrase = Util.IsMultiPhrase(wordArray);
        if (multi_phrase)
        {
            StringBuilder sb = new StringBuilder();

            List<String> phrases = new ArrayList<>();
            for (String word : wordArray)
            {
                if (word.equals(" .") || word.equals(" !") || word.equals(" $"))
                {
                    sb.append(word);
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
                results.add(Util.RulesCheck(phrase));
            }

            return results;
        }

        return null;
    }

    private static String[] prepInput_CreateWordArray(String input)
    {
        String[] wordArray;
        String[] reserved = {"|", "\\", "*", "<", "\"", ":", ">", "#"};
        String result = input;

        List<String> doc_chars = new ArrayList<>();
        for (char c : result.toCharArray())
        {
            String str = Character.toString(c);
            doc_chars.add(str);
        }

        result = "";

        for (int i = 0; i < doc_chars.size(); i++)
        {
            boolean okay = true;
            for (String s : reserved)
            {
                if (doc_chars.get(i).equals(s))
                {
                    okay = false;
                    doc_chars.remove(i);
                    i--;
                    break;
                }
            }

            if (okay)
            {
                if (doc_chars.get(i).equals(","))
                {
                    doc_chars.set(i, " ,");
                }
                else if (doc_chars.get(i).equals(";"))
                {
                    doc_chars.set(i, " ;");
                }
                else if (doc_chars.get(i).equals("?"))
                {
                    doc_chars.set(i, " $");
                }
                else if (doc_chars.get(i).equals("$"))
                {
                    doc_chars.set(i, " $");
                }
                else if (doc_chars.get(i).equals("!"))
                {
                    doc_chars.set(i, " !");
                }
                else if (doc_chars.get(i).equals("."))
                {
                    if (doc_chars.size() >= i + 2)
                    {
                        if (doc_chars.get(i + 1).equals("."))
                        {
                            doc_chars.set(i, " .");
                            i = i + 2;
                        }
                        else
                        {
                            doc_chars.set(i, " .");
                        }
                    }
                    else
                    {
                        doc_chars.set(i, " .");
                    }
                }
            }
        }

        for (int i = 0; i < doc_chars.size(); i++)
        {
            result = result.concat(doc_chars.get(i));
        }

        result = result.trim();
        if (!TextUtils.isEmpty(result))
        {
            wordArray = result.split(" ");

            for (int i = 0; i < wordArray.length; i++)
            {
                wordArray[i] = Util.PunctuationFix_ForInput(wordArray[i]);
            }

            return wordArray;
        }

        return null;
    }

    private static void prepInput_UpdateExistingFrequencies(String[] wordArray)
    {
        List<WordData> data = Data.getWords();

        for (int a = 0; a < data.size(); a++)
        {
            for (String word : wordArray)
            {
                if (data.get(a).getWord().equals(word))
                {
                    data.get(a).setFrequency(data.get(a).getFrequency() + 1);
                }
            }
        }

        Data.saveWords(data);
    }

    private static void prepInput_AddNewWords(String[] wordArray)
    {
        if (wordArray.length > 0)
        {
            List<WordData> data = Data.getWords();

            for (String word : wordArray)
            {
                //noinspection SuspiciousMethodCalls
                if (data.size() > 0)
                {
                    Boolean found = false;
                    for (int i = 0; i < data.size(); i++)
                    {
                        if (data.get(i).getWord().equals(word) && !word.equals(""))
                        {
                            found = true;
                            break;
                        }
                    }

                    if (!found)
                    {
                        WordData new_wordset = new WordData();
                        new_wordset.setWord(word);
                        new_wordset.setFrequency(1);
                        data.add(new_wordset);
                    }
                }
                else
                {
                    WordData new_wordset = new WordData();
                    new_wordset.setWord(word);
                    new_wordset.setFrequency(1);
                    data.add(new_wordset);
                }
            }

            Data.saveWords(data);
        }
    }

    private static void prepInput_UpdatePreWords(String[] wordArray)
    {
        for (int i = 0; i < wordArray.length - 1; i++)
        {
            //Get current pre_words from the database
            List<WordData> data = Data.getPreWords(wordArray[i + 1]);

            List<String> words = new ArrayList<>();
            for (int a = 0; a < data.size(); a++)
            {
                words.add(data.get(a).getWord());
            }

            //Update the frequency of existing words
            if (words.contains(wordArray[i]))
            {
                int index = words.indexOf(wordArray[i]);
                data.get(index).setFrequency(data.get(index).getFrequency() + 1);
                Data.savePreWords(data, wordArray[i + 1]);
            }
            else
            {
                //Or add the word
                if (!wordArray[i].equals(""))
                {
                    WordData new_wordset = new WordData();
                    new_wordset.setWord(wordArray[i]);
                    new_wordset.setFrequency(1);
                    data.add(new_wordset);
                    Data.savePreWords(data, wordArray[i + 1]);
                }
            }
        }
    }

    private static void prepInput_UpdateProWords(String[] wordArray)
    {
        for (int i = 0; i < wordArray.length - 1; i++)
        {
            if (i != wordArray.length)
            {
                //Get current pro_words from the database
                List<WordData> data = Data.getProWords(wordArray[i]);

                List<String> words = new ArrayList<>();
                for (int b = 0; b < data.size(); b++)
                {
                    words.add(data.get(b).getWord());
                }

                //Update the frequency of existing words
                if (words.contains(wordArray[i + 1]))
                {
                    int index = words.indexOf(wordArray[i + 1]);
                    data.get(index).setFrequency(data.get(index).getFrequency() + 1);
                    Data.saveProWords(data, wordArray[i]);
                }
                else
                {
                    //Or add the word
                    if (!wordArray[i + 1].equals(""))
                    {
                        WordData new_wordset = new WordData();
                        new_wordset.setWord(wordArray[i + 1]);
                        new_wordset.setFrequency(1);
                        data.add(new_wordset);
                        Data.saveProWords(data, wordArray[i]);
                    }
                }
            }
        }
    }

    static String Respond(String[] wordArray, String input)
    {
        String output;
        StringBuilder response = new StringBuilder();

        if (UserInput)
        {
            topics = Util.GenTopics(wordArray, topics);
            Util.AddTopics(input, topics);
            last_response_thinking = input;
        }
        else if (Initiation && topics.size() == 0)
        {
            topics.add(Util.Get_RandomWord());
        }

        if (topics.size() > 0)
        {
            Boolean bl_MatchFound = false;

            if (Advanced)
            {
                Random rand = new Random();
                int int_random_choice = rand.nextInt(topics.size());
                response.append(GenerateResponse(topics.get(int_random_choice)));

                //If nothing new could be generated with the topic, change topic
                if (Initiation && response.toString().equals(topics.get(int_random_choice)))
                {
                    topics.clear();
                }
            }
            else
            {
                //Check for existing responses to phrases using the topics
                if (TopicBased)
                {
                    List<String> info = Util.Get_TopicRelated(topics);
                    if (info.size() > 0)
                    {
                        //If some found, pick one at random
                        Random rand = new Random();
                        int int_random_choice = rand.nextInt(info.size());
                        response.append(info.get(int_random_choice));
                        bl_MatchFound = true;

                        //If nothing could be generated with the topic, change topic
                        if (Initiation && Util.RulesCheck(response.toString()).equals(Util.Get_FirstPhrase(last_response)))
                        {
                            topics.clear();
                            bl_MatchFound = false;
                        }
                    }
                }

                //If none found, check for conditioned responses
                if (!bl_MatchFound && ConditionBased)
                {
                    String temp_input = Util.PunctuationFix_ForInput(input);
                    List<String> outputList = Data.getOutputList_NoRelated(temp_input);
                    if (outputList.size() > 0)
                    {
                        //If some found, pick one at random
                        Random rand = new Random();
                        int int_random_choice = rand.nextInt(outputList.size());
                        response.append(outputList.get(int_random_choice));
                        bl_MatchFound = true;

                        //If nothing could be generated with the topic, change topic
                        if (Initiation && Util.RulesCheck(response.toString()).equals(Util.Get_FirstPhrase(last_response)))
                        {
                            topics.clear();
                            bl_MatchFound = false;
                        }
                    }
                }

                //If none found, procedurally generate a response using the topic
                if (!bl_MatchFound && ProceduralBased)
                {
                    if (topics.size() > 0)
                    {
                        Random rand = new Random();
                        int int_random_choice = rand.nextInt(topics.size());
                        response.append(GenerateResponse(topics.get(int_random_choice)));
                    }
                    else
                    {
                        response.append(GenerateResponse(Util.Get_RandomWord()));
                    }

                    //If nothing new could be generated with the topic, change topic
                    if (Initiation && Util.RulesCheck(response.toString()).equals(Util.Get_FirstPhrase(last_response)))
                    {
                        topics.clear();
                    }
                }
            }

            String current_response = response.toString();
            List<String> RelatedPhrases = Util.Get_PhraseRelated(current_response);
            if (RelatedPhrases.size() > 0)
            {
                for (String related : RelatedPhrases)
                {
                    if (!related.equals(current_response))
                    {
                        response.append(" ").append(related);
                    }
                }
            }

            String response_output = Util.RulesCheck(response.toString());

            if (!response_output.isEmpty())
            {
                output = response_output;
                last_response = response_output;

                NewInput = true;
            }
            else
            {
                output = "";
            }
        }
        else
        {
            output = "";
        }

        return output;
    }

    static String Think(String[] wordArray)
    {
        String output;
        StringBuilder response = new StringBuilder();

        Util.GenTopics_ForThinking(wordArray);

        if (topics_thinking.size() > 0)
        {
            Boolean bl_MatchFound = false;

            //Check for existing responses to phrases using the topics
            List<String> info = Util.Get_TopicRelated(topics_thinking);
            if (info.size() > 0)
            {
                //If some found, pick one at random
                Random rand = new Random();
                int int_random_choice = rand.nextInt(info.size());
                response.append(info.get(int_random_choice));
                bl_MatchFound = true;
            }

            //If none found, check for conditioned responses
            if (!bl_MatchFound)
            {
                String temp_input = Util.PunctuationFix_ForInput(last_response_thinking);
                List<String> outputList = Data.getOutputList_NoRelated(temp_input);
                if (outputList.size() > 0)
                {
                    //If some found, pick one at random
                    Random rand = new Random();
                    int int_random_choice = rand.nextInt(outputList.size());
                    response.append(outputList.get(int_random_choice));
                    bl_MatchFound = true;
                }
            }

            //If none found, procedurally generate a response using the topic
            if (!bl_MatchFound)
            {
                Random rand = new Random();
                int int_random_choice = rand.nextInt(topics_thinking.size());
                response.append(GenerateResponse(topics_thinking.get(int_random_choice)));

                //If nothing could be generated with the topic, change topic
                if (Util.RulesCheck(response.toString()).equals(last_response_thinking))
                {
                    response.append(GenerateResponse(Util.Get_RandomWord()));
                }
            }

            String current_response = response.toString();
            List<String> RelatedPhrases = Util.Get_PhraseRelated(current_response);
            if (RelatedPhrases.size() > 0)
            {
                for (String related : RelatedPhrases)
                {
                    if (!related.equals(current_response))
                    {
                        response.append(" ").append(related);
                    }
                }
            }
        }
        else
        {
            response.append(GenerateResponse(Util.Get_RandomWord()));
        }

        String response_output = Util.RulesCheck(response.toString());
        if (!response_output.isEmpty())
        {
            output = response_output;
        }
        else
        {
            output = "";
        }

        return output;
    }

    private static String GenerateResponse(String lowest_word)
    {
        int int_highest_f;
        String current_pre_word = lowest_word;
        String current_pro_word = lowest_word;
        String response = current_pre_word;
        Boolean words_found = true;
        String[] checker;
        String[] checker2;
        String repeater_check = "";
        Random random;

        List<WordData> data;
        List<String> words;
        List<Integer> frequencies;

        while (words_found)
        {
            data = Data.getPreWords(current_pre_word);
            if (data.size() > 0)
            {
                words = new ArrayList<>();
                frequencies = new ArrayList<>();

                for (int c = 0; c < data.size(); c++)
                {
                    int frequency = data.get(c).getFrequency();
                    if (frequency > 0)
                    {
                        words.add(data.get(c).getWord());
                        frequencies.add(frequency);
                    }
                }

                if (frequencies.size() > 0)
                {
                    int_highest_f = Util.Choose(frequencies);
                    List<Integer> RandomOnes = new ArrayList<>();
                    for (int b = 0; b < frequencies.size(); b++)
                    {
                        if (frequencies.get(b) == int_highest_f)
                        {
                            RandomOnes.add(b);
                        }
                    }
                    random = new Random();
                    int int_choice2 = random.nextInt(RandomOnes.size());
                    current_pre_word = words.get(RandomOnes.get(int_choice2));

                    if (current_pre_word.length() > 1)
                    {
                        StringBuilder sb = new StringBuilder(current_pre_word).delete(1, current_pre_word.length() - 1);
                        char first_letter = sb.charAt(0);
                        if (Character.isUpperCase(first_letter))
                        {
                            String str2 = response;
                            StringBuilder sb2 = new StringBuilder(str2).insert(0, current_pre_word + " ");
                            response = sb2.toString();
                            break;
                        }
                    }

                    checker2 = response.split(" ");
                    for (String check2 : checker2)
                    {
                        String check = check2;
                        check = Util.PunctuationFix_ForInput(check);
                        if (check.equals(current_pre_word))
                        {
                            words_found = false;
                            break;
                        }
                    }

                    if (words_found)
                    {
                        String str = response;
                        StringBuilder sb = new StringBuilder(str).insert(0, current_pre_word + " ");
                        response = sb.toString();
                    }
                }
                else
                {
                    words_found = false;
                }
            }
            else
            {
                words_found = false;
            }
        }
        words_found = true;

        while (words_found)
        {
            data = Data.getProWords(current_pro_word);
            if (data.size() > 0)
            {
                words = new ArrayList<>();
                frequencies = new ArrayList<>();

                for (int e = 0; e < data.size(); e++)
                {
                    int frequency = data.get(e).getFrequency();
                    if (frequency > 0)
                    {
                        words.add(data.get(e).getWord());
                        frequencies.add(frequency);
                    }
                }

                if (frequencies.size() > 0)
                {
                    int_highest_f = Util.Choose(frequencies);
                    List<Integer> RandomOnes = new ArrayList<>();
                    for (int b = 0; b < frequencies.size(); b++)
                    {
                        if (frequencies.get(b) == int_highest_f)
                        {
                            RandomOnes.add(b);
                        }
                    }
                    random = new Random();
                    int int_choice2 = random.nextInt(RandomOnes.size());
                    current_pro_word = words.get(RandomOnes.get(int_choice2));

                    if (repeater_check.length() > 0)
                    {
                        checker = repeater_check.split(" ");
                        for (String check1 : checker)
                        {
                            String check = check1;
                            check = Util.PunctuationFix_ForInput(check);
                            if (check.equals(current_pro_word))
                            {
                                words_found = false;
                                break;
                            }
                        }
                    }

                    if (words_found)
                    {
                        String str = response;
                        StringBuilder sb = new StringBuilder(str).insert(response.length(), " " + current_pro_word);
                        response = sb.toString();

                        String str2 = repeater_check;
                        StringBuilder sb2 = new StringBuilder(str2).insert(repeater_check.length(), current_pro_word + " ");
                        repeater_check = sb2.toString();

                        if (current_pro_word.equals(".") || current_pro_word.equals("$") || current_pro_word.equals("!"))
                        {
                            break;
                        }
                    }
                }
                else
                {
                    words_found = false;
                }
            }
            else
            {
                words_found = false;
            }
        }

        return response;
    }

}
