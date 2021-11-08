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

public class Data 
{
	//Variables
	private static List<WordData> WordDataSet = new ArrayList<WordData>();
	public static List<String> Words = new ArrayList<String>();
	public static List<Integer> Frequencies = new ArrayList<Integer>();
	public static List<String> InputList = new ArrayList<String>();
	public static List<String> OutputList = new ArrayList<String>();
	public static List<String> HistoryList = new ArrayList<String>();
	public static List<String> ThoughtList = new ArrayList<String>();
	public static List<String> InformationBank = new ArrayList<String>();
	
	//Properties
	public static List<WordData> getWordDataSet() 
	{
		return WordDataSet;
	}

	public static void setWordDataSet(List<WordData> wordDataSet) 
	{
		WordDataSet = wordDataSet;
	}

	//Words Data
	public static void saveWords() throws IOException
    {
    	BufferedWriter writer = null;
        try 
        {
        	File file = new File(MainActivity.Brain_dir, "Words.txt");
        	if (!file.exists()) 
            {    
        		file.createNewFile();
            }
        	writer = new BufferedWriter(new FileWriter(file));
        	String WordsLine = "";
        	
        	for (int i = 0; i < getWordDataSet().size(); i++)
            {
                WordsLine = getWordDataSet().get(i).getWord() + "~" + getWordDataSet().get(i).getFrequency().toString() + "\n";
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
	
	public static void getWords()
    {
    	getWordDataSet().clear();
    	Words.clear();
    	Frequencies.clear();
    	String WordSet[];
    	
    	File file = new File(MainActivity.Brain_dir, "Words.txt");
    	
    	try 
    	{
    		BufferedReader br = new BufferedReader(new FileReader(file));

    		String line = "";
    		while ((line = br.readLine()) != null) 
    		{
    			if (line.contains("~"))
    	        {
    	            WordSet = line.split("~");
    	            Words.add(WordSet[0].toString());
    	            Frequencies.add(Integer.parseInt(WordSet[1]));
    	        }
    		}
    		for (int i = 0; i < Words.size(); i++)
            {
        		WordData newset = new WordData();
    			newset.setWord(Words.get(i));
    			newset.setFrequency(Frequencies.get(i));
                getWordDataSet().add(newset);
            }
    		br.close();
    	}
    	catch (IOException e)
    	{
    		e.printStackTrace();
    	}
    }
	
	//PreWords Data
    public static void savePreWords(String word) throws IOException
    {
    	BufferedWriter writer = null;
        try 
        {
        	File file = new File(MainActivity.Brain_dir, "Pre-" + word + ".txt");
        	if (!file.exists()) 
            {    
        		file.createNewFile();
            }
        	writer = new BufferedWriter(new FileWriter(file));
        	String WordsLine = "";
        	String[] WordSet = new String[getWordDataSet().size()];
        	
        	for (int i = 0; i < getWordDataSet().size(); i++)
            {
        		WordsLine = getWordDataSet().get(i).getWord() + "~" + getWordDataSet().get(i).getFrequency().toString();
                WordSet[i] = WordsLine;
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
    
    public static void getPreWords(String word)
    {
    	getWordDataSet().clear();
    	Words.clear();
    	Frequencies.clear();
    	String WordSet[];
    	File file = new File(MainActivity.Brain_dir, "Pre-" + word + ".txt");
    	
    	if (file.isFile())
    	{
	    	try 
	    	{
	    		BufferedReader br = new BufferedReader(new FileReader(file));
	
	    		String line = "";
	    		while ((line = br.readLine()) != null) 
	    		{
	    			if (line.contains("~"))
	    	        {
	    	            WordSet = line.split("~");
	    	            Words.add(WordSet[0].toString());
	    	            Frequencies.add(Integer.parseInt(WordSet[1]));
	    	        }
	    		}
	    		for (int i = 0; i < Words.size(); i++)
	            {
	        		WordData newset = new WordData();
	    			newset.setWord(Words.get(i));
	    			newset.setFrequency(Frequencies.get(i));
	                getWordDataSet().add(newset);
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
    		BufferedWriter writer = null;
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
    }
    
  //ProWords Data
    public static void saveProWords(String word) throws IOException
    {
    	BufferedWriter writer = null;
        try 
        {
        	File file = new File(MainActivity.Brain_dir, "Pro-" + word + ".txt");
        	if (!file.exists()) 
            {    
        		file.createNewFile();
            }
        	writer = new BufferedWriter(new FileWriter(file));
        	String WordsLine = "";
        	String[] WordSet = new String[getWordDataSet().size()];
        	
        	for (int i = 0; i < getWordDataSet().size(); i++)
            {
        		WordsLine = getWordDataSet().get(i).getWord() + "~" + getWordDataSet().get(i).getFrequency().toString();
                WordSet[i] = WordsLine;
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

    public static void getProWords(String word)
    {
    	getWordDataSet().clear();
    	Words.clear();
    	Frequencies.clear();
    	String WordSet[];
    	File file = new File(MainActivity.Brain_dir, "Pro-" + word + ".txt");
    	
    	if (file.isFile())
    	{
	    	try 
	    	{
	    		BufferedReader br = new BufferedReader(new FileReader(file));
	
	    		String line = "";
	    		while ((line = br.readLine()) != null) 
	    		{
	    			if (line.contains("~"))
	    	        {
	    	            WordSet = line.split("~");
	    	            Words.add(WordSet[0].toString());
	    	            Frequencies.add(Integer.parseInt(WordSet[1]));
	    	        }
	    		}
	    		for (int i = 0; i < Words.size(); i++)
	            {
	        		WordData newset = new WordData();
	    			newset.setWord(Words.get(i));
	    			newset.setFrequency(Frequencies.get(i));
	                getWordDataSet().add(newset);
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
    		BufferedWriter writer = null;
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
    }

    //Input Data
    public static void saveInputList() throws IOException
    {
    	BufferedWriter writer = null;
        try 
        {
        	File file = new File(MainActivity.Brain_dir, "InputList.txt");
        	if (!file.exists()) 
            {    
        		file.createNewFile();
            }
        	writer = new BufferedWriter(new FileWriter(file));

        	for (int i = 0; i < InputList.size(); i++)
            {
                writer.write(InputList.get(i));
                writer.newLine();
            }
        	writer.close();
        } 
        catch(IOException ex) 
        {
        	ex.printStackTrace();
        }
    }
    
    public static void getInputList()
    {
    	InputList.clear();
    	
    	try 
    	{
    		File file = new File(MainActivity.Brain_dir, "InputList.txt");
    		BufferedReader br = new BufferedReader(new FileReader(file));

    		String line = "";
    		while ((line = br.readLine()) != null) 
    		{
    			if (!line.equals(""))
    			{
    				InputList.add(line);
    			}
    		}
    		br.close();
    	}
    	catch (IOException e)
    	{
    		e.printStackTrace();
    	}
    }
    
    //Output Data
    public static void saveOutput(String input)
    {
    	BufferedWriter writer = null;
        try 
        {
        	File file = new File(MainActivity.Brain_dir, input + ".txt");
        	if (!file.exists()) 
            {    
        		file.createNewFile();
            }
        	writer = new BufferedWriter(new FileWriter(file));

        	for (int i = 0; i < OutputList.size(); i++)
            {
                writer.write(OutputList.get(i));
                writer.newLine();
            }
        	writer.close();
        } 
        catch(IOException ex) 
        {
        	ex.printStackTrace();
        }
    }
    
    public static void getOutputList(String input)
    {
    	OutputList.clear();
    	
    	File file = new File(MainActivity.Brain_dir, input + ".txt");
    	
    	if (file.isFile())
    	{
    		try 
        	{
        		BufferedReader br = new BufferedReader(new FileReader(file));

        		String line = "";
        		while ((line = br.readLine()) != null) 
        		{
        			if (!line.equals(""))
        			{
        				OutputList.add(line);
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
    		BufferedWriter writer = null;
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
    }
    
    //History Data
    public static void saveHistory() throws IOException
    {
    	BufferedWriter writer = null;
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
        	
        	for (int i = 0; i < HistoryList.size(); i++)
            {
                writer.write(HistoryList.get(i));
                writer.newLine();
            }
        	writer.close();
        } 
        catch(IOException ex) 
        {
        	ex.printStackTrace();
        }
    }    
    
    public static void getHistory()
    {
    	HistoryList.clear();
    	
    	DateFormat f = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
    	String currentDate = f.format(new Date());
    	File file = new File(MainActivity.History_dir, currentDate + ".txt");
    	
    	if (!file.exists())
    	{
    		try
    		{
				saveHistory();
			}
    		catch (IOException e)
    		{
				e.printStackTrace();
			}
    	}
    	
    	try 
    	{
    		BufferedReader br = new BufferedReader(new FileReader(file));

    		String line = "";
    		while ((line = br.readLine()) != null) 
    		{
    			if (!line.equals(""))
    			{
    				HistoryList.add(line);
    			}
    		}
    		br.close();
    	}
    	catch (IOException e)
    	{
    		e.printStackTrace();
    	}
    }
    
  //Thought Data
    public static void saveThoughts() throws IOException
    {
    	BufferedWriter writer = null;
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
        	
        	for (int i = 0; i < ThoughtList.size(); i++)
            {
                writer.write(ThoughtList.get(i));
                writer.newLine();
            }
        	writer.close();
        } 
        catch(IOException ex) 
        {
        	ex.printStackTrace();
        }
    }    
    
    public static void getThoughts()
    {
    	ThoughtList.clear();
    	
    	DateFormat f = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
    	String currentDate = f.format(new Date());
    	File file = new File(MainActivity.Thought_dir, currentDate + ".txt");
    	
    	if (!file.exists())
    	{
    		try
    		{
				saveHistory();
			}
    		catch (IOException e)
    		{
				e.printStackTrace();
			}
    	}
    	
    	try 
    	{
    		BufferedReader br = new BufferedReader(new FileReader(file));

    		String line = "";
    		while ((line = br.readLine()) != null) 
    		{
    			if (!line.equals(""))
    			{
    				ThoughtList.add(line);
    			}
    		}
    		br.close();
    	}
    	catch (IOException e)
    	{
    		e.printStackTrace();
    	}
    }
    
    public static void pullInfo(String topic)
    {
    	getInputList();
    	InformationBank.clear();
    	
    	if (InputList.size() > 0)
    	{
	    	for (int a = 0; a < InputList.size(); a++)
	    	{
	    		if (InputList.get(a).contains(topic))
	    		{
	    			getOutputList(InputList.get(a));
	    			if (OutputList.size() > 0)
	    			{
		    			for (int b = 0; b < OutputList.size(); b++)
		    			{
		    				InformationBank.add(OutputList.get(b));
		    			}
	    			}
	    		}
	    	}
    	}
    }
    
}
