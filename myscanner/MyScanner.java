package myscanner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;
class FilesUtil 
{
    public static String readTextFile(String fileName) throws IOException 
    {
        String content = new String(Files.readAllBytes(Paths.get(fileName).toAbsolutePath()));
        return content;
    }
}
public class MyScanner
{
    String[] keywords = new String[] {"function","return", "arg","main","array", "int", "float","string","display","if", "endif", "else", "endelse", "while" ,"endwhile", "null"};
    int comment = 0;
    int singlelinecomment = 0;
    int quote = 0;
    public static void main(String[] args) throws IOException
    {
        MyScanner scan = new MyScanner();
        scan.input();

    }

    public boolean findInArray(String name)
    {
        int i;

        for(i = 0; i< keywords.length; i++){
            if(keywords[i].equals(name)){
                return true;
            }
        }
        return false;
    }

  

    public int finalState(String token)
    {

    
    int state = 0;
    for(int i=0;i<token.length();i++)
    {
        if((state==0)&&((Character.isLetter(token.charAt(i)))||(token.charAt(i)=='_'))) state = 1;
        else if((state==0)&&((token.charAt(i)=='-')||(token.charAt(i)=='+'))) state = 2;
        else if((state==0)&&(Character.isDigit(token.charAt(i)))) state = 3;
        else if((state==0)&&(token.charAt(i)=='"')) state = 6;
        else if((state==0)&&((token.charAt(i)=='<')||(token.charAt(i)=='='))) state = 9;
        else if(state==0) state = 11;
        else if((state==1)&&((Character.isLetter(token.charAt(i)))||(token.charAt(i)=='_')||Character.isDigit(token.charAt(i)))) state = 1;
        else if(state==1) state = 11;
        else if((state==2)&&(Character.isDigit(token.charAt(i)))) state = 3;
        else if(state==2) state = 11;
        else if((state==3)&&(Character.isDigit(token.charAt(i)))) state = 3;
        else if((state==3)&&(token.charAt(i)=='.')) state = 4;
        else if(state==3) state = 11;
        else if((state==4)&&(Character.isDigit(token.charAt(i)))) state = 5;
        else if(state==4) state = 11;
        else if((state==5)&&(Character.isDigit(token.charAt(i)))) state = 5;
        else if(state==5) state = 11;
        else if((state==6)&&((token.charAt(i)!='"'))) state = 7;
        else if((state==6)&&((token.charAt(i)=='"'))) state = 8;
        else if((state==7)&&((token.charAt(i)!='"'))) state = 7;
        else if((state==7)&&((token.charAt(i)=='"'))) state = 8;
        else if(state==8) state = 11;
        else if((state==9)&&((token.charAt(i)=='='))) state = 10;
        else if(state==9) state = 11;
        else if(state==10) state = 11;


    }
       


    return state;


    }

    public int findDFA(String token, int ln, int singlelinecomment)
    {
        MyScanner scan = new MyScanner();
        int l = token.length();
        char c,d,e,f;
        if(l==1)
        {
            if(comment==0)
            {
                c=token.charAt(0);
                switch(c)
                {
                    case '*':
                    case '+':
                    case '-':
                    case '/':
                    case '=':
                    case '<':
                    System.out.println("Token operator , string "+token+" , line number "+ ln); break;
                    case ':':
                    case ',':
                    case '(':
                    case ')':
                    System.out.println("Token delimiter , string "+token+" , line number "+ ln); break;
                    case '0':
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7':
                    case '8':
                    case '9':
                    System.out.println("Token integerliteral , string "+token+" , line number "+ ln); break;
                    default:
                    System.out.println("Token identifier , string "+token+" , line number "+ ln); break;

                }
                

            }

        }
        if(l>1)
        {
            
             if(comment==1)
        {
            e = token.charAt(l-2);
            f = token.charAt(l-1);
            if((f=='/')&&(e=='*'))
            {
                comment = 0; return singlelinecomment;
            }

        }
        else if((comment==0)&&(singlelinecomment==0))
        {
            c = token.charAt(0);
            d = token.charAt(1);
            if((c=='/')&&(d=='*'))
            {
                System.out.println("comment found");
                comment = 1; return singlelinecomment;

            }
            if((c=='/')&&(d=='/'))
            {
                System.out.println("comment found");
                singlelinecomment = 1; return singlelinecomment;

            }
        
            int finalstate = scan.finalState(token);

            switch(finalstate)
            {
                case 10: System.out.println("Token operator , string "+token+" , line number "+ ln); break;
                case 1: System.out.println("Token identifier , string "+token+" , line number "+ ln); break;
                case 3: System.out.println("Token integerliteral , string "+token+" , line number "+ ln); break;
                case 5: System.out.println("Token floatliteral , string "+token+" , line number "+ ln); break;
                case 8: System.out.println("Token stringliteral , string "+token+" , line number "+ ln); break;
                default: System.out.println(token+" is invalid! Error"); break;
            }
            


        }
           
            
        

        }
        return singlelinecomment;

        
    }
    public void input() throws IOException
    {
        int i;
        File file = new File("Code.txt");
        Scanner sca = new Scanner(System.in);
        MyScanner sc = new MyScanner();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        char nextchar;
        String token = "";
        int l = 0;
        int fin = 0;
        
        String line;
        String quotetoken = "";
        while( (line = br.readLine())!= null )
        {
            
            l++;
            String [] tokens = line.split("\\s+");

            for(i=0;i<tokens.length;i++)
            {
                    fin = 0;
                    if(tokens[i].length()>0)
                    {   
                    if(tokens[i].charAt(0)=='"') {
                        quote = 1;
                        quotetoken = tokens[i];
                    }
                    while(quote==1)
                    {
                        i++;
                        int len = tokens[i].length();
                        if(tokens[i].charAt(len-1)=='"') {
                            
                            quote = 0;
                            fin = 1;
                            quotetoken +=" ";
                            quotetoken +=tokens[i];
                            singlelinecomment = sc.findDFA(quotetoken,l,singlelinecomment);
                            break;
                        }
                        quotetoken +=" ";
                        quotetoken +=tokens[i];



                    }
                    

                    if(sc.findInArray(tokens[i]))
                        {
                            System.out.println("Token keyword , string "+tokens[i]+" , line number "+ l);
                        }
                    else
                        {
                            int len = tokens[i].length();


                              if((len>2)&&(tokens[i].charAt(len-2)==')')&&(tokens[i].charAt(len-1)==':'))
                            {
                                
                                if(fin==0) singlelinecomment = sc.findDFA(tokens[i].substring(0,len-2),l,singlelinecomment);
                                if(fin==0) singlelinecomment = sc.findDFA(")",l,singlelinecomment);
                                if(fin==0) singlelinecomment = sc.findDFA(":",l,singlelinecomment);
                                


                            }
                            else if((len>2)&&(tokens[i].charAt(2)=='(')&&(tokens[i].charAt(0)=='i')&&(tokens[i].charAt(1)=='f'))
                            {
                                System.out.println("Token keyword , string "+"if"+" , line number "+ l);
                                if(fin==0) singlelinecomment = sc.findDFA("(",l,singlelinecomment);
                                if(fin==0) singlelinecomment = sc.findDFA(tokens[i].substring(3,len),l,singlelinecomment);
                                

                            }
                            else if((len>5)&&(tokens[i].substring(0,5).equals("while")))
                            {
                                System.out.println("Token keyword , string "+"while"+" , line number "+ l);
                                if(fin==0) singlelinecomment = sc.findDFA("(",l,singlelinecomment);
                                if(fin==0) singlelinecomment = sc.findDFA(tokens[i].substring(6,len),l,singlelinecomment);
                               


                            }
                            
                            else if(tokens[i].charAt(len-1)==':')
                            {
                                
                                if(fin==0) singlelinecomment = sc.findDFA(tokens[i].substring(0,len-1),l,singlelinecomment);
                                if(fin==0) singlelinecomment = sc.findDFA(":",l,singlelinecomment);
                               


                            }
                            else if(tokens[i].charAt(len-1)==',')
                            {
                                
                                if(fin==0) singlelinecomment = sc.findDFA(tokens[i].substring(0,len-1),l,singlelinecomment);
                                if(fin==0) singlelinecomment = sc.findDFA(",",l,singlelinecomment);
                               

                            }

                            
                              else if(tokens[i].charAt(0)=='(')
                            {
                                if(fin==0) singlelinecomment = sc.findDFA("(",l,singlelinecomment);
                                if(fin==0) singlelinecomment = sc.findDFA(tokens[i].substring(1,len),l,singlelinecomment);
                               


                            }
                           
                           
                            else if(fin==0) singlelinecomment = sc.findDFA(tokens[i],l,singlelinecomment);
                            
                        }
                    }
            
            }
            singlelinecomment = 0;
}

            }
        }

