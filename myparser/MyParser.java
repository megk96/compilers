package myparser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;
class Rules
{
	String lhs;
	String[] rhs;
}
class Tokens
{
	String type;
	String value;
}
class FilesUtil 
{
    public static String readTextFile(String fileName) throws IOException 
    {
        String content = new String(Files.readAllBytes(Paths.get(fileName).toAbsolutePath()));
        return content;
    }
}

public class MyParser
{
	public static String[] terminals = {"function","identifier",":","return","arg","float","int","char","string","integerliteral","floatliteral","stringliteral","(",")",",","null","main","=","else","endelse","if","endif","while","endwhile","==","<",">","+","-","*","/","$"};
	public static String[] nonTerminals = {"PROGRAM", "FUNCTIONBLOCK", "FUNCTION", "RETURNARG","BLOCK" ,"TYPE", "ID", "VALUE", "FUNCCALL", "PARAMLIST", "P'", "TYPELIST", "T'","MAINBLOCK",  "STATEMENT", "DECLARATION", "D'","A'","ASSIGNMENT", "B'","IFSTATEMENT", "IFBLOCK", "ELSEBLOCK", "WHILESTATEMENT", "CONDITION", "OP", "ASSIGNMENTSTATEMENT", "EXPRESSION", "E'", "TERM", "F'","FACTOR" };
	
	public static Stack<String> stack;
	public static Rules[] rules;
	
	public static int l;
	public static Tokens[] t;


	public static int[][] parseTable = {
	{1,60,60,60,60,60,60,60,60,60,60,60,60,60,60,60,1,60,60,60,60,60,60,60,60,60,60,60,60,60,60,59},
	{2,60,60,60,60,60,60,60,60,60,60,60,60,60,60,60,3,60,60,60,60,60,60,60,60,60,60,60,60,60,60,60},
	{4,60,60,60,60,60,60,60,60,60,60,60,60,60,60,60,59,60,60,60,60,60,60,60,60,60,60,60,60,60,60,60},
	{59,59,60,5,60,59,59,59,59,60,60,60,60,60,60,59,59,60,60,60,59,60,59,60,60,60,60,60,60,60,60,59},
	{7,6,60,60,60,6,6,6,6,60,60,60,60,60,60,6,7,60,60,7,6,7,6,7,60,60,60,60,60,60,60,7},
	{60,59,60,60,60,8,9,10,11,59,59,59,60,60,60,60,60,60,60,60,60,60,60,60,60,60,60,60,60,60,60,60},
	{59,12,60,60,59,59,59,59,59,13,13,13,60,59,59,59,59,60,60,59,59,59,59,59,59,59,59,59,59,59,59,59},
	{59,17,60,60,59,59,59,59,59,14,15,16,60,59,59,59,59,60,60,59,59,59,59,59,59,59,59,59,59,59,59,59},
	{59,18,60,60,59,59,59,59,59,60,60,60,60,59,59,59,59,60,60,59,59,59,59,59,59,59,59,59,59,59,59,59},
	{60,19,60,60,60,60,60,60,60,19,19,19,60,59,60,60,60,60,60,60,60,60,60,60,60,60,60,60,60,60,60,60},
	{60,60,60,60,60,60,60,60,60,60,60,60,60,21,20,60,60,60,60,60,60,60,60,60,60,60,60,60,60,60,60,60},
	{59,59,60,60,60,22,22,22,22,60,60,60,60,60,59,23,59,60,60,59,59,59,59,59,60,60,60,60,60,60,60,59},
	{25,25,60,60,60,25,25,25,25,60,60,60,60,60,24,25,25,60,60,25,25,25,25,25,60,60,60,60,60,60,60,25},
	{60,60,60,60,60,60,60,60,60,60,60,60,60,60,60,60,26,60,60,60,60,60,60,60,60,60,60,60,60,60,60,59},
	{59,30,60,60,60,27,27,27,27,60,60,60,60,60,60,27,59,60,60,59,28,59,29,59,60,60,60,60,60,60,60,59},
	{59,59,60,60,60,32,32,32,32,60,60,60,60,60,60,31,59,60,60,59,59,59,59,59,60,60,60,60,60,60,60,59},
	{34,34,60,60,60,34,34,34,34,60,60,60,60,60,33,34,34,60,60,34,34,34,34,34,60,60,60,60,60,60,60,34},
	{36,36,60,60,60,36,36,36,36,60,60,60,60,60,35,36,36,60,60,36,36,36,36,36,60,60,60,60,60,60,60,36},
	{59,59,60,60,60,37,37,37,37,60,60,60,60,60,59,59,59,60,60,59,59,59,59,59,60,60,60,60,60,60,60,59},
	{39,39,60,60,60,39,39,39,39,60,60,60,60,60,39,39,39,60,60,39,39,39,39,39,60,60,60,60,60,60,60,39},
	{59,59,60,60,60,59,59,59,59,60,60,60,60,60,60,59,59,60,60,59,40,59,59,59,60,60,60,60,60,60,60,59},
	{41,41,60,60,60,41,41,41,41,60,60,60,60,60,60,41,41,60,42,41,41,41,41,41,60,60,60,60,60,60,60,41},
	{59,59,60,60,60,59,59,59,59,60,60,60,60,60,60,59,59,60,42,59,43,59,59,59,60,60,60,60,60,60,60,59},
	{59,59,60,60,60,59,59,59,59,60,60,60,60,60,60,59,59,60,60,59,59,59,44,59,60,60,60,60,60,60,60,59},
	{60,45,60,60,60,60,60,60,60,45,45,45,60,59,60,60,60,60,60,60,60,60,60,60,60,60,60,60,60,60,60,60},
	{60,60,60,60,60,60,60,60,60,60,60,60,60,59,60,60,60,60,60,60,60,60,60,60,46,47,48,60,60,60,60,60},
	{59,49,60,60,60,59,59,59,59,60,60,60,60,60,60,59,59,60,60,59,59,59,59,59,60,60,60,60,60,60,60,59},
	{59,50,60,60,60,59,59,59,59,50,50,50,60,59,60,59,59,60,60,59,59,59,59,59,60,60,60,60,60,60,60,59},
	{53,53,60,60,60,53,53,53,53,60,60,60,60,53,60,53,53,60,60,53,53,53,53,53,60,60,60,51,52,60,60,53},
	{59,54,60,60,60,59,59,59,59,54,54,54,60,59,60,59,59,60,60,59,59,59,59,59,60,60,60,59,59,60,60,59},
	{57,57,60,60,60,57,57,57,57,60,60,60,60,57,60,57,57,60,60,57,57,57,57,57,60,60,60,57,57,55,56,57},
	{59,58,60,60,60,59,59,59,59,58,58,58,60,59,60,59,59,60,60,59,59,59,59,59,60,60,60,59,59,59,59,59}};
	public static void main(String[] args) throws IOException
    {
    	
        MyParser parse = new MyParser();
         
        parse.rules();
        parse.tokens();
        parse.parser();

    }
    public static int findInNonTerminals(String name)
    {
        int i;

        for(i = 0; i< nonTerminals.length; i++){
            if(nonTerminals[i].equals(name)){
                return i;
            }
        }
        return -1;
    }
    public static int findInTerminals(String name)
    {
        int i;

        for(i = 0; i< terminals.length; i++){
            if(terminals[i].equals(name)){
                return i;
            }
        }
        return -1;
    }
    public static void rules() throws IOException
    {
    	rules =  new Rules[58];
		for( int i=0; i<58; i++ )
    		rules[i] = new Rules();

    	File file = new File("rules.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        String line;
        int i = 0;
        while( (line = br.readLine())!= null )
        {
            String [] tokens = line.split("\\s+");
            int len = tokens.length;
            rules[i].lhs = tokens[0];
            rules[i].rhs = new String[len-1];
            for(int j = 2;j<tokens.length;j++)
            {
            	rules[i].rhs[j-2] = tokens[j];

            }  
            i++;

 	   }
	}
	 public static void tokens() throws IOException
    {
    	File file = new File("badparse.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        String line;
        l = 0;
        while( (line = br.readLine())!= null )
        {
        	l++;
        }
        t =  new Tokens[l];
        
		for( int i=0; i<l; i++ )
    		t[i] = new Tokens();
        BufferedReader bu = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        int i = 0;
        while( (line = bu.readLine())!= null )
        {
        	String [] okens = line.split("\\s+");
        	t[i].type = okens[1];
        	t[i].value = okens[4];
        	i++;
        }

        	
        }
        public static void parser()
        {
        	stack = new Stack<String>();
        	MyParser mp = new MyParser();
        	stack.push("$");
        	stack.push("PROGRAM");
        	String top,tok;
        	int row,col,rule;
        	int i = 0;
        	while(i<t.length)
        	{

        		top = stack.peek();
        		if((t[i].type.equals("stringliteral"))||(t[i].type.equals("integerliteral")) || (t[i].type.equals("floatliteral"))||(t[i].type.equals("identifier"))) tok = t[i].type;
        		else tok = t[i].value;
        		System.out.println("The top of the stack is "+top);
        		System.out.println("The next token is "+tok);
        		if(top.equals(tok)==false)
        		{
        			row = mp.findInNonTerminals(top);
	        		col = mp.findInTerminals(tok);
	        		if(row!=-1 && col !=-1)
	                rule=parseTable[row][col];
	            	else if(row==-1)
	                rule=60;
	            	else
	                rule=59;
	            	if(rule==59)
	            	{
	            		System.out.println("               -------->     POP ERROR HAS OCCURED                --------------> CONTINUE PARSING");
	            		stack.pop();
	            	}
	            	else if(rule==60)
	            	{
	            		System.out.println("               -------->     SCAN ERROR HAS OCCURED               --------------> CONTINUE PARSING");
	            		i++;
	            	}
	            	else
	            	{
	            		stack.pop();
	            		rule--;
	            		System.out.print("The rule chosen is   ");
	            		System.out.print(rules[rule].lhs);
	            		System.out.print(" -> ");
	            		for(int h=0;h<rules[rule].rhs.length-1;h++)
	            			System.out.print(rules[rule].rhs[h]+" ");
	            		for(int h=rules[rule].rhs.length-2;h>=0;h--)
	            			if(rules[rule].rhs[h].equals("EPSILON")==false) stack.push(rules[rule].rhs[h]);
	            		System.out.println();

            		}

        		}
        		else
        		{
        			System.out.println("Both are same");
        			stack.pop();
        			i++;


        		}
        }
      }

  }
   
