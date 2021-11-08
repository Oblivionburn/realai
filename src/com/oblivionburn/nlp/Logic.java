package com.oblivionburn.nlp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Logic 
{
	//Variables
	public static String str_Input = "";
	public static String str_Output = "";
	public static String str_response = "";
	public static String str_last_response = "";
	public static String str_History = "";
	public static String[] WordArray;
	public static Boolean bl_Initiation = false;
	public static Boolean bl_NewInput = false;
	public static Boolean bl_MatchFound = false;
	private static List<String> Words = new ArrayList<String>();
	private static List<Integer> Frequencies = new ArrayList<Integer>();
	
	public static void prepInput() throws IOException
    {    	
    	if (!str_Input.equals(null) && !str_Input.equals(""))
    	{
    		prepInput_CreateWordArray();
			prepInput_UpdateExistingFrequencies();
			prepInput_AddNewWords();
			prepInput_UpdatePreWords();
			prepInput_UpdateProWords();
    	}
    }
	
	private static void prepInput_CreateWordArray()
	{
		List<String> doc_chars = new ArrayList<String>();
		for (char c : str_Input.toCharArray())
        {
            String str = Character.toString(c);
            doc_chars.add(str);
        }

		str_Input = "";

		for (int i = 0; i < doc_chars.size(); i++)
        {
            if (doc_chars.get(i).equals(","))
            {
                doc_chars.set(i, " ,");
            }
            else if (doc_chars.get(i).equals(";"))
            {
            	doc_chars.set(i, " ;");
            }
            else if (doc_chars.get(i).equals(":"))
            {
            	doc_chars.set(i, null);
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
		
		for (int i = 0; i < doc_chars.size(); i++)
        {
			str_Input += doc_chars.get(i);
        }
		
		WordArray = str_Input.split(" ");
		
		for (int i = 0; i < WordArray.length; i++)
        {
			WordArray[i] = PunctuationFix_ForInput(WordArray[i]);
        }
	}
	
	private static void prepInput_UpdateExistingFrequencies()
	{
		Data.getWords();
		
		for (int a = 0; a < Data.getWordDataSet().size(); a++)
        {
            for (int b = 0; b < WordArray.length; b++)
            {
                if (Data.getWordDataSet().get(a).getWord().equals(WordArray[b]))
                {
                	Data.getWordDataSet().get(a).setFrequency(Data.getWordDataSet().get(a).getFrequency() + 1);
                }
            }
        }
		
		try
		{
			Data.saveWords();
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
	}
	
	private static void prepInput_AddNewWords()
	{
		Data.getWords();
		
		for (int i = 0; i < WordArray.length; i++)
        {
            if (Data.Words.contains(WordArray[i]))
            {

            }
            else
            {
            	if (!WordArray[i].equals(""))
            	{
            		try  		
            		{
                        WordData new_wordset = new WordData();
                        new_wordset.setWord(WordArray[i]);
                        new_wordset.setFrequency(1);
                        Data.getWordDataSet().add(new_wordset);
                        Data.saveWords();

                        Data.getWordDataSet().clear();
                        Data.savePreWords(WordArray[i]);
                        Data.saveProWords(WordArray[i]);
            		}
            		catch(IOException ex)
            		{
            			ex.printStackTrace();
            		}
            	}
            }
        }
	}
	
	private static void prepInput_UpdatePreWords()
	{
		try
		{
			for (int i = 0; i < WordArray.length - 1; i++)
	        {
	            //Get current pre_words from the database
				Data.getPreWords(WordArray[i + 1]);
				Data.Words.clear();

	            for (int a = 0; a < Data.getWordDataSet().size(); a++)
	            {
	            	Data.Words.add(Data.getWordDataSet().get(a).getWord());
	            }

	            //Update the frequency of existing words
	            if (Data.Words.contains(WordArray[i]))
	            {
	                int index = Data.Words.indexOf(WordArray[i]);
	                Data.getWordDataSet().get(index).setFrequency(Data.getWordDataSet().get(index).getFrequency() + 1);
	                Data.savePreWords(WordArray[i + 1]);
	            }
	            else
	            {
	                //Or add the word
	            	if (!WordArray[i].equals(""))
	            	{
	            		WordData new_wordset = new WordData();
	                    new_wordset.setWord(WordArray[i]);
	                    new_wordset.setFrequency(1);
	                    Data.getWordDataSet().add(new_wordset);
	                    Data.savePreWords(WordArray[i + 1]);
	            	}
	            }
	        }
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
	}
	
	private static void prepInput_UpdateProWords()
	{
		try
		{
			for (int i = 0; i < WordArray.length - 1; i++)
	        {
	            if (i == WordArray.length)
	            {

	            }
	            else
	            {
	                //Get current pro_words from the database
	            	Data.getWordDataSet().clear();
	            	Data.getProWords(WordArray[i]);
	            	Data.Words.clear();

	                for (int b = 0; b < Data.getWordDataSet().size(); b++)
	                {
	                	Data.Words.add(Data.getWordDataSet().get(b).getWord());
	                }

	                //Update the frequency of existing words
	                if (Data.Words.contains(WordArray[i + 1]))
	                {
	                    int index = Data.Words.indexOf(WordArray[i + 1]);
	                    Data.getWordDataSet().get(index).setFrequency(Data.getWordDataSet().get(index).getFrequency() + 1);
	                    Data.saveProWords(WordArray[i]);
	                }
	                else
	                {
	                    //Or add the word
	                	if (!WordArray[i + 1].equals(""))
	                	{
	                		WordData new_wordset = new WordData();
	                        new_wordset.setWord(WordArray[i + 1]);
		                    new_wordset.setFrequency(1);
		                    Data.getWordDataSet().add(new_wordset);
		                    Data.saveProWords(WordArray[i]);
	                	}
	                }
	            }
	        }
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
	}

    public static void Respond() throws FileNotFoundException
    {
    	//Get topic
        String str_lowest_word = Get_LowestFrequency();

        if (str_lowest_word.length() > 0)
        {
            if (bl_NewInput)
            {
            	UpdateOutputList();
            }
            
            bl_MatchFound = false;
            
            //Check for existing responses to phrases using the topic
            Data.pullInfo(str_lowest_word);
            if (Data.InformationBank.size() > 0)
            {
            	//If some found, pick one at random
                Random rand = new Random();
                int int_random_choice = rand.nextInt(Data.InformationBank.size());
                str_response = Data.InformationBank.get(int_random_choice);
                bl_MatchFound = true;
            }
            
            //If none found, procedurally generate a response using the topic
            if (bl_MatchFound == false)
            {
            	UpdateInputList(str_Input);
            	GenerateResponse(str_lowest_word);
            }
            
            RulesCheck();
            str_Output = str_response;
            str_last_response = str_response;
            
            if (str_response.equals("") || str_response.equals(null))
            {
            	str_response = "";
            }
            
            if (MainActivity.bl_Feedback = true)
            {
            	str_Input = str_response;
            }
            
            bl_NewInput = true;
        }
        else
        {
            str_Output = "";
        }
    }
    
    private static void GenerateResponse(String str_lowest_word)
    {
    	int int_highest_f = 0;
    	String str_current_pre_word = str_lowest_word;
        String str_current_pro_word = str_lowest_word;
        str_response = str_current_pre_word;
        Boolean bl_words_found = true;
        String[] arr_checker = null;
        String[] arr_checker2 = null;
        String str_repeater_check = "";
        Random random = new Random();
        
    	while (bl_words_found)
        {
        	Data.getPreWords(str_current_pre_word);
            if (Data.getWordDataSet().size() > 0)
            {
            	Data.Words.clear();
            	Data.Frequencies.clear();
                for (int c = 0; c < Data.getWordDataSet().size(); c++)
                {
                	Data.Words.add(Data.getWordDataSet().get(c).getWord());
                	Data.Frequencies.add(Data.getWordDataSet().get(c).getFrequency());
                }

                int_highest_f = GetMax(Data.Frequencies);
                List<Integer> RandomOnes = new ArrayList<Integer>();
                for (int b = 0; b < Data.Frequencies.size(); b++)
                {
                    if (Data.Frequencies.get(b) == int_highest_f)
                    {
                        RandomOnes.add(b);
                    }
                }
                random = new Random();
                int int_choice2 = random.nextInt(RandomOnes.size());
                str_current_pre_word = Data.Words.get(RandomOnes.get(int_choice2));

                if (str_current_pre_word.length() > 1)
                {
                	StringBuilder sb = new StringBuilder(str_current_pre_word).delete(1, str_current_pre_word.length() - 1);
                	char first_letter = sb.charAt(0);
                    if (Character.isUpperCase(first_letter))
                    {
                    	String str2 = str_response;
                    	StringBuilder sb2 = new StringBuilder(str2).insert(0, str_current_pre_word + " ");
                        str_response = sb2.toString();
                        break;
                    }
                }

                arr_checker2 = str_response.split(" ");
                for (int a = 0; a < arr_checker2.length; a++)
                {
                	String check = arr_checker2[a];
                	check = PunctuationFix_ForInput(check);
                    if (check.equals(str_current_pre_word))
                    {
                        bl_words_found = false;
                        break;
                    }
                }

                if (bl_words_found != false)
                {
                	String str = str_response;
                	StringBuilder sb = new StringBuilder(str).insert(0, str_current_pre_word + " ");
                    str_response = sb.toString();
                }
            }
            else
            {
                bl_words_found = false;
            }
        }
        bl_words_found = true;
        
        while (bl_words_found)
        {
        	Data.getProWords(str_current_pro_word);
            if (Data.getWordDataSet().size() > 0)
            {
            	Data.Words.clear();
            	Data.Frequencies.clear();
                for (int e = 0; e < Data.getWordDataSet().size(); e++)
                {
                	Data.Words.add(Data.getWordDataSet().get(e).getWord());
                	Data.Frequencies.add(Data.getWordDataSet().get(e).getFrequency());
                }

                int_highest_f = GetMax(Data.Frequencies);
                List<Integer> RandomOnes = new ArrayList<Integer>();
                for (int b = 0; b < Data.Frequencies.size(); b++)
                {
                    if (Data.Frequencies.get(b) == int_highest_f)
                    {
                        RandomOnes.add(b);
                    }
                }
                random = new Random();
                int int_choice2 = random.nextInt(RandomOnes.size());
                str_current_pro_word = Data.Words.get(RandomOnes.get(int_choice2));
                
                if (str_repeater_check.length() > 0)
                {
                	arr_checker = str_repeater_check.split(" ");
                    for (int a = 0; a < arr_checker.length; a++)
                    {
                    	String check = arr_checker[a];
                    	check = PunctuationFix_ForInput(check);
                        if (check.equals(str_current_pro_word))
                        {
                            bl_words_found = false;
                            break;
                        }
                    }
                }

                if (bl_words_found != false)
                {
                	String str = str_response;
                	StringBuilder sb = new StringBuilder(str).insert(str_response.length(), " " + str_current_pro_word);
                    str_response = sb.toString();

                    String str2 = str_repeater_check;
                	StringBuilder sb2 = new StringBuilder(str2).insert(str_repeater_check.length(), str_current_pro_word + " ");
                	str_repeater_check = sb2.toString();

                    if (str_current_pro_word.equals(".") || str_current_pro_word.equals("$") || str_current_pro_word.equals("!"))
                    {
                        bl_words_found = false;
                        break;
                    }
                }
            }
            else
            {
                bl_words_found = false;
            }
        }
    }
    
    private static void UpdateInputList(String input)
    {
    	Data.getInputList();
    	
    	if (input.length() > 1)
    	{
    		input = PunctuationFix_ForInput(input);
    	}
    	
        if (Data.InputList.contains(input) || input.equals(null))
        {

        }
        else
        {
        	Data.InputList.add(input);
        	
            try 
            {
            	Data.saveInputList();
			}
            catch (IOException e)
			{
            	e.printStackTrace();
			}
        }
    }
    
    private static void UpdateOutputList()
    {
    	if (str_last_response.length() > 1)
    	{
    		str_last_response = PunctuationFix_ForInput(str_last_response);
    	}
        
        Data.getOutputList(str_last_response);

        if (str_Input.length() > 1)
        {
        	str_Input = PunctuationFix_ForInput(str_Input);
        }
        
        if (Data.OutputList.contains(str_Input) || str_last_response.equals(str_Input))
        {

        }
        else
        {
        	Data.OutputList.add(str_Input);
        	Data.saveOutput(str_last_response);
        	UpdateInputList(str_last_response);
        }
    }
    
    private static String Get_LowestFrequency()
    {
    	Frequencies.clear();
    	Words.clear();
    	int int_lowest_f = 0;
    	String lowest_word = "";
    	
    	Data.getWords();
    	
    	if (bl_Initiation == true)
        {
        	for (int a = 0; a < Data.getWordDataSet().size(); a++)
            {
            	Words.add(Data.getWordDataSet().get(a).getWord());
            	Frequencies.add(Data.getWordDataSet().get(a).getFrequency());
            }
            
            if (Words.size() > 0)
            {
            	Boolean bl_accepted = false;
                for (int i = 0; i < Words.size(); i++)
                {
	                Random random = new Random();
	                int int_choice = random.nextInt(Words.size());
	                lowest_word = Words.get(int_choice);
	                
	                if (lowest_word.equals(".") || lowest_word.equals("$") || lowest_word.equals("!") || lowest_word.equals(","))
                    {
                    	bl_accepted = false;
                    }
	                else
                    {
                    	bl_accepted = true;
                    }
                    
                    if (bl_accepted == true)
                    {
                    	break;
                    }
                }
            }
        }
        else
        {
        	if (WordArray != null)
            {
            	for (int a = 0; a < WordArray.length; a++)
                {
                    for (int a2 = 0; a2 < Data.getWordDataSet().size(); a2++)
                    {
                        if (Data.getWordDataSet().get(a2).getWord().equals(WordArray[a]))
                        {
                        	Words.add(Data.getWordDataSet().get(a2).getWord());
                        	Frequencies.add(Data.getWordDataSet().get(a2).getFrequency());
                        }
                    }
                }
            }
            
            if (Frequencies.size() > 0)
            {
            	int_lowest_f = GetMin(Frequencies);
            	List<Integer> RandomOnes = new ArrayList<Integer>();
                for (int b = 0; b < Frequencies.size(); b++)
                {
                    if (Frequencies.get(b) == int_lowest_f)
                    {
                        RandomOnes.add(b);
                    }
                }
                
                Boolean bl_accepted = false;
                for (int i = 0; i < RandomOnes.size(); i++)
                {
                	Random random = new Random();
                    int int_choice = random.nextInt(RandomOnes.size());
                    lowest_word = Words.get(RandomOnes.get(int_choice));
                    
                    if (lowest_word.equals(".") || lowest_word.equals("$") || lowest_word.equals("!") || lowest_word.equals(","))
                    {
                    	bl_accepted = false;
                    }
                    else
                    {
                    	bl_accepted = true;
                    }
                    
                    if (bl_accepted == true)
                    {
                    	break;
                    }
                }
                
            }
        }
    	
    	return lowest_word;
    }
    
    private static int GetMin(List<Integer> Integer_List)
    {
        int lowest_number = 0;
        int current_number = Integer_List.get(0);
        for (int b = 0; b < Integer_List.size(); b++)
        {
            if (current_number <= Integer_List.get(b))
            {
                lowest_number = current_number;
            }
            else
            {
                current_number = Integer_List.get(b);
            }
        }

        return lowest_number;
    }
    
    private static int GetMax(List<Integer> Integer_List)
    {
        int highest_number = 0;
        int current_number = Integer_List.get(0);
        for (int b = 0; b < Integer_List.size(); b++)
        {
            if (current_number >= Integer_List.get(b))
            {
                highest_number = current_number;
            }
            else
            {
                current_number = Integer_List.get(b);
            }
        }

        return highest_number;
    }
    
    private static String PunctuationFix_ForInput(String old_word)
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
    
    public static void FeedbackLoop() throws IOException 
    {
    	if (MainActivity.bl_Feedback == false)
    	{
    		bl_Initiation = true;
        	bl_NewInput = false;
        	Respond();
        	prepInput();
        	bl_Initiation = false;
        	MainActivity.bl_Feedback = true;
    	}
    	
    	Data.getThoughts();
		str_History = Logic.str_Input;
		Logic.HistoryRules();
		Data.ThoughtList.add("NLP: " + Logic.str_History);
    	
    	Respond();
    	
    	Data.ThoughtList.add("NLP: " + Logic.str_Output);
		Data.saveThoughts();
    	
    	ClearLeftovers();
    }
    
    public static void ClearLeftovers()
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
    	
    	file = new File(MainActivity.Brain_dir, "$.txt");
    	if (file.exists())
    	{
    		file.delete();
    	}
    }
    
    public static void RulesCheck()
    {   
    	if (str_response.length() > 1)
    	{
    		//Learn which words should be capitalized by example
    		String[] str_response_check = str_response.split(" ");
            for (int i = 1; i < str_response_check.length; i++)
            {
                String str_checked_word = str_response_check[i];
                if (str_checked_word.equals(null) || str_checked_word.equals(""))
                {
                	
                }
                else
                {
                	char capital_letter = str_checked_word.charAt(0);
                    if (Character.isUpperCase(capital_letter) && !str_checked_word.equals("I"))
                    {
                        Data.Words.clear();
                        Data.Frequencies.clear();
                        Data.getWords();
                        for (int a = 0; a < Data.getWordDataSet().size(); a++)
                        {
                            Data.Words.add(Data.getWordDataSet().get(a).getWord());
                            Data.Frequencies.add(Data.getWordDataSet().get(a).getFrequency());
                        }

                        String str_lower_word = str_checked_word;
                        String str_capital_letter = Character.toString(capital_letter);
                        int int_high_frequency = 0;
                        int int_low_frequency = 0;
                        List<Integer> Frequency_List = new ArrayList<Integer>();
                        str_capital_letter = str_capital_letter.toLowerCase();
                        String str = str_lower_word;
                        StringBuilder sb = new StringBuilder(str).replace(0, 0, "");
                    	sb.insert(0, str_capital_letter);
                    	str_lower_word = sb.toString();

                        for (int b = 0; b < Data.Words.size(); b++)
                        {
                            if (Data.Words.get(b).equals(str_lower_word))
                            {
                                int_low_frequency = Data.Frequencies.get(b);
                                Frequency_List.add(int_low_frequency);
                            }
                            else if (Data.Words.get(b).equals(str_checked_word))
                            {
                                int_high_frequency = Data.Frequencies.get(b);
                                Frequency_List.add(int_high_frequency);
                            }
                        }
                        
                        if (Frequency_List.size() > 0)
                        {
                        	if (GetMax(Frequency_List) == int_low_frequency)
                            {
                                str_response_check[i] = str_lower_word;
                            }
                            else if (GetMax(Frequency_List) == int_high_frequency)
                            {
                                str_response_check[i] = str_checked_word;
                            }
                        }
                    }
                }
            }
            String str_new_response = "";
            
            for (int i = 0; i < str_response_check.length; i++)
            {
                str_new_response += str_response_check[i] + " ";
            }
            str_response = str_new_response;
            
            //Remove any spaces before commas
            while (str_response.contains(" ,"))
            {
            	String str3 = str_response;
                StringBuilder sb3 = new StringBuilder(str3).replace(str_response.indexOf(" ,"), str_response.indexOf(" ,") + 2, ",");
                str_response = sb3.toString();
            }

            //Remove any spaces before colons
            while (str_response.contains(" :"))
            {
            	String str3 = str_response;
                StringBuilder sb3 = new StringBuilder(str3).replace(str_response.indexOf(" :"), str_response.indexOf(" :") + 2, ":");
                str_response = sb3.toString();
            }

            //Remove any spaces before semicolons
            while (str_response.contains(" ;"))
            {
            	String str3 = str_response;
                StringBuilder sb3 = new StringBuilder(str3).replace(str_response.indexOf(" ;"), str_response.indexOf(" ;") + 2, ";");
                str_response = sb3.toString();
            }
            
            //Make sure the first word is capitalized
            char first_letter = str_response.charAt(0);
            if (Character.isUpperCase(first_letter))
            {

            }
            else
            {            
                String str_capital_letter = Character.toString(first_letter);
                str_capital_letter = str_capital_letter.toUpperCase();
                String str2 = str_response;
                StringBuilder sb2 = new StringBuilder(str2).delete(0, 1);
                sb2.insert(0, str_capital_letter);
                str_response = sb2.toString();
            }
            
            //Remove any empty spaces at the end
            String str2 = str_response;
            StringBuilder sb2 = new StringBuilder(str2).delete(0, str_response.length() - 1);
        	char last_letter = sb2.charAt(0);
            String str_last_letter = Character.toString(last_letter);
            
            while (str_last_letter.equals(" "))
            {            
            	String str3 = str_response;
            	StringBuilder sb3 = new StringBuilder(str3).delete(str_response.length() - 1, str_response.length());
                str_response = sb3.toString();
                
                String str4 = str_response;
                StringBuilder sb4 = new StringBuilder(str4).delete(0, str_response.length() - 1);
                last_letter = sb4.charAt(0);
                
                str_last_letter = Character.toString(last_letter);
            }
            
            //Set an ending punctuation if one does not exist
            if (!str_last_letter.equals(".") && !str_last_letter.equals("$") && !str_last_letter.equals("!"))
            {
            	String str3 = str_response;
            	StringBuilder sb3 = new StringBuilder(str3).insert(str_response.length(), ".");
                str_response = sb3.toString();
            }
            
            //Learn the best ending punctuation from example
            if (str_response.endsWith("$") || str_response.endsWith(".") || str_response.endsWith("!"))
            {
	            Data.getInputList();
	            if (Data.InputList.size() > 0)
	            {
	            	int q_count = 0;
		            int p_count = 0;
		            int e_count = 0;
		            
		            for (int i = 0; i < Data.InputList.size(); i++)
		            {
		            	String CurrentSentence = Data.InputList.get(i);
		            	String[] str_currentwords_check = CurrentSentence.split(" ");
		            	String[] str_response_check2 = str_response.split(" ");
		            	
		            	if (str_currentwords_check.length > 0 && str_response_check2.length > 0)
		            	{
			            	if (str_currentwords_check[0].toString().equals(str_response_check2[0].toString()))
			            	{
			            		if (str_currentwords_check[str_currentwords_check.length - 1].toString().equals("$"))
			            		{
			            			q_count++;
			            		}
			            		else if (str_currentwords_check[str_currentwords_check.length - 1].toString().equals("."))
			            		{
			            			p_count++;
			            		}
			            		else if (str_currentwords_check[str_currentwords_check.length - 1].toString().equals("!"))
			            		{
			            			e_count++;
			            		}
			            	}
		            	}
		            }
		            
		            if (q_count > p_count && q_count > e_count)
		            {
		            	String str3 = str_response;
		                StringBuilder sb3 = new StringBuilder(str3).replace(str_response.length() - 1, str_response.length(), "$");
		                str_response = sb3.toString();
		            }
		            else if (p_count > q_count && p_count > e_count)
		            {
		            	String str3 = str_response;
		                StringBuilder sb3 = new StringBuilder(str3).replace(str_response.length() - 1, str_response.length(), ".");
		                str_response = sb3.toString();
		            }
		            else if (e_count > q_count && e_count > p_count)
		            {
		            	String str3 = str_response;
		                StringBuilder sb3 = new StringBuilder(str3).replace(str_response.length() - 1, str_response.length(), "!");
		                str_response = sb3.toString();
		            }
	            }
            }
            
            //Replace any dollar signs with question marks
            while (str_response.contains("$"))
            {
            	String str3 = str_response;
                StringBuilder sb3 = new StringBuilder(str3).replace(str_response.indexOf("$"), str_response.indexOf("$") + 1, "?");
                str_response = sb3.toString();
            }
            
            //Remove any spaces before ending punctuation
            while (str_response.contains(" ."))
            {
            	String str3 = str_response;
                StringBuilder sb3 = new StringBuilder(str3).replace(str_response.indexOf(" ."), str_response.indexOf(" .") + 2, ".");
                str_response = sb3.toString();
            }
            while (str_response.contains(" ?"))
            {
            	String str3 = str_response;
                StringBuilder sb3 = new StringBuilder(str3).replace(str_response.indexOf(" ?"), str_response.indexOf(" ?") + 2, "?");
                str_response = sb3.toString();
            }
            while (str_response.contains(" !"))
            {
            	String str3 = str_response;
                StringBuilder sb3 = new StringBuilder(str3).replace(str_response.indexOf(" !"), str_response.indexOf(" !") + 2, "!");
                str_response = sb3.toString();
            }
    	}
    }
    
    public static void HistoryRules()
    {   
    	if (str_History.length() > 1 && !str_History.equals(""))
    	{           
            //Remove any spaces before commas
    		while (str_History.contains(" ,"))
            {
            	String str3 = str_History;
                StringBuilder sb3 = new StringBuilder(str3).replace(str_History.indexOf(" ,"), str_History.indexOf(" ,") + 2, ",");
                str_History = sb3.toString();
            }

            //Remove any spaces before colons
    		while (str_History.contains(" :"))
            {
            	String str3 = str_History;
                StringBuilder sb3 = new StringBuilder(str3).replace(str_History.indexOf(" :"), str_History.indexOf(" :") + 2, ":");
                str_History = sb3.toString();
            }

            //Remove any spaces before semicolons
    		while (str_History.contains(" ;"))
            {
            	String str3 = str_History;
                StringBuilder sb3 = new StringBuilder(str3).replace(str_History.indexOf(" ;"), str_History.indexOf(" ;") + 2, ";");
                str_History = sb3.toString();
            }
            
            //Make sure the first word is capitalized
            char first_letter = str_History.charAt(0);
            if (Character.isUpperCase(first_letter))
            {

            }
            else
            {            
                String str_capital_letter = Character.toString(first_letter);
                str_capital_letter = str_capital_letter.toUpperCase();
                String str2 = str_History;
                StringBuilder sb2 = new StringBuilder(str2).delete(0, 1);
                sb2.insert(0, str_capital_letter);
                str_History = sb2.toString();
            }
            
            //Remove any empty spaces at the end       
            String str2 = str_History;
            StringBuilder sb2 = new StringBuilder(str2).delete(0, str_History.length() - 1);
        	char last_letter = sb2.charAt(0);
            String str_last_letter = Character.toString(last_letter);
            
            while (str_last_letter.equals(" "))
            {            
            	String str3 = str_History;
            	StringBuilder sb3 = new StringBuilder(str3).delete(str_History.length() - 1, str_History.length());
            	str_History = sb3.toString();
                
                String str4 = str_History;
                StringBuilder sb4 = new StringBuilder(str4).delete(0, str_History.length() - 1);
                last_letter = sb4.charAt(0);
                
                str_last_letter = Character.toString(last_letter);
            }
            
            //Set an ending punctuation if one does not exist
            if (!str_last_letter.equals(".") && !str_last_letter.equals("$") && !str_last_letter.equals("!"))
            {
            	String str3 = str_History;
            	StringBuilder sb3 = new StringBuilder(str3).insert(str_History.length(), ".");
            	str_History = sb3.toString();
            }
                       
            //Replace any dollar signs with question marks
            while (str_History.contains("$"))
            {
            	String str3 = str_History;
                StringBuilder sb3 = new StringBuilder(str3).replace(str_History.indexOf("$"), str_History.indexOf("$") + 1, "?");
                str_History = sb3.toString();
            }
            
            //Remove any spaces before ending punctuation
            while (str_History.contains(" ."))
            {
            	String str3 = str_History;
                StringBuilder sb3 = new StringBuilder(str3).replace(str_History.indexOf(" ."), str_History.indexOf(" .") + 2, ".");
                str_History = sb3.toString();
            }
            while (str_History.contains(" ?"))
            {
            	String str3 = str_History;
                StringBuilder sb3 = new StringBuilder(str3).replace(str_History.indexOf(" ?"), str_History.indexOf(" ?") + 2, "?");
                str_History = sb3.toString();
            }
            while (str_History.contains(" !"))
            {
            	String str3 = str_History;
                StringBuilder sb3 = new StringBuilder(str3).replace(str_History.indexOf(" !"), str_History.indexOf(" !") + 2, "!");
                str_History = sb3.toString();
            }
    	}
    }
}
