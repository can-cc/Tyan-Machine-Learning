import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.*;

public class Viterbi2
{
	public static void main(String[] args)
	{
		
		//----------------------------------------------------------------------------------
		//统计出训练样本中词性种类及其频率
	  String content="";
	  BufferedReader reader=null;
	  try{  //读取199801train.txt文本中的内容，并保存在content的字符流中
		    reader=new BufferedReader(new FileReader("199801train.txt"));
		    String line;
		    while((line=reader.readLine())!=null) content+=line;
		   }
	  catch(IOException e)
	  {
		   e.printStackTrace();
	  }
	  finally
	  {
		   if(reader!=null)
       {
    	   try{reader.close();}   	   
    	   catch(IOException e){}
       }
	  }
	  
	  String[] text;  //text[]用于存储训练样本中的词语
	  text=content.split("(/[a-z]*\\s{0,})|(][a-z]*\\s{1,})"); //去除词性标注
	  //for(String wd:text)
	    //System.out.println(wd);
	   
	    
	  String[] temp;  //temp[]数组用于存储单个词的词性标注符号
    temp=content.split("[0-9|-]*/|\\s{1,}[^a-z]*|][a-z]"); //仅保留词性标注符号。
    String[] temp1;
    temp1=new String[temp.length-1];//去除temp[0]为空的情况
    for(int i=0;i<temp.length-1;i++)
      temp1[i]=temp[i+1];	
    //for(String wd:temp1)
      //System.out.print(wd+"  ");
      
    String[] temp2;  //temp2[]数组用于存储每两个词的词性标注符号
    temp2=new String[temp1.length-1];
    for(int i=0;i<temp1.length-1;i++)
      temp2[i]=temp1[i]+','+temp1[i+1];    
    //for(String wd:temp2)
      //System.out.println(wd);
      
    String[] word_pos;
    word_pos=new String[text.length];
    for(int i=0;i<text.length;i++)
      word_pos[i]=text[i]+','+temp1[i];
    //for(String wd:word_pos)
      //System.out.println(wd);
         
      
    Hashtable hash1=new Hashtable();  //创建hash1，存储单个词的词性及其频率
    for(String wd:temp1)
    {
    	if(hash1.containsKey(wd))
    	  hash1.put(wd,hash1.get(wd).hashCode()+1);
    	else
    	  hash1.put(wd,1);    	      	  
    }
    int sp=hash1.size();  //统计词性个数
      //System.out.println(hash1);
    
    Hashtable hash2=new Hashtable();  //创建hash2，存储每两个词的词性及其频率
    for(String wd:temp2)
    {
    	if(hash2.containsKey(wd))
    	  hash2.put(wd,hash2.get(wd).hashCode()+1);
    	else
    	  hash2.put(wd,1);
    }
    //System.out.println(hash2);    
   
   Hashtable hash3=new Hashtable();  //创建hash3,存储词语、词性和词频
   for(String wd:word_pos)
   {
   	if(hash3.containsKey(wd))
   	  hash3.put(wd,hash3.get(wd).hashCode()+1);
   	else
   	  hash3.put(wd,1);
   }
   //System.out.println(hash3);
   
    String[] table_pos;  //table_pos[]用于存储所有不同的词性符号
    table_pos=new String[sp];
    Enumeration key=hash1.keys();    
    for(int i=0;i<sp;i++)
    {       
      String str=(String)key.nextElement();
      table_pos[i]=str;
    }
    //for(String wd:table_pos)
       //System.out.println(wd);
          
   
   //--------------------------------------------------------------------------------------
   //计算状态转移概率
    double[][] status; //  status[i][j]用于存储转移概率,表示由状态j转移到状态i的概率。
    status=new double[sp][sp];
    for(int i=0;i<sp;i++)  //初始化
    {
      for(int j=0;j<sp;j++)
        status[i][j]=0;
    }
    
    for(int i=0;i<sp;i++)
    {
      for(int j=0;j<sp;j++)
      {
      	String wd=table_pos[j];
        String str=wd+','+table_pos[i];
        if(hash2.containsKey(str))
          status[i][j]=Math.log(((double)hash2.get(str).hashCode()/(double)hash1.get(wd).hashCode())*100000000);
        else
          status[i][j]=Math.log((1/((double)hash1.get(wd).hashCode()*1000))*100000000);
      }
    }    
    /*for(int i=0;i<sp;i++)  
    {
    	System.out.println('\n');
      for(int j=0;j<sp;j++)
        System.out.print(status[j][0]+"  ");
    }*/
    
    //-----------------------------------------------------------------------------------------
    //计算发射概率
    String sentence="";  
    try{  //读取test.txt文本中的内容，并保存在sentence的字符流中。
  	    BufferedReader str=new BufferedReader(new FileReader("199801test.txt"));
        String line;
        while((line=str.readLine())!=null) sentence+=line;
       }
    catch(IOException e)
    {
       e.printStackTrace();
    }
    
    String[] test;
    test=sentence.split("(/[a-z]*\\s{0,})|(][a-z]*\\s{1,})");
    int sw=0;  //记录test.txt中词语的总数
    sw=test.length;
    //for(String wd:test)
      //System.out.println(wd);
      //System.out.print(sw);
    
   double[][] observe;  //observe[i][j]表示在词性状态Sj下，输出词语Oi的概率。
    observe=new double[sw][sp];
    for(int i=0;i<sw;i++)  //初始化
    {
      for(int j=0;j<sp;j++)
        observe[i][j]=0;
    }
    
    for(int i=0;i<sw;i++)
    {
      for(int j=0;j<sp;j++)
      {
      	String wd=test[i];
      	String ws=table_pos[j];
      	String str=wd+','+ws;
      	if(hash3.containsKey(str))
      	  observe[i][j]=Math.log(((double)hash3.get(str).hashCode()/(double)hash1.get(ws).hashCode())*100000000);
      	else
      	  observe[i][j]=Math.log((1/((double)hash1.get(ws).hashCode()*1000))*100000000);
      }
    }
    /*for(int i=0;i<sw;i++)  
    {
      for(int j=0;j<sp;j++)
        System.out.println(observe[j][0]);
    }*/

    //-----------------------------------------------------------------------------------------------
    //Viterbi算法，进行词性标注。找出the best path
    double[][] path;  //path[][]存储单个词语的最大概率
    path=new double[sw][sp];
    for(int i=0;i<sw;i++)
    {
    	for(int j=0;j<sp;j++)
    	  path[i][j]=0.0;
    }
    
    int[][] backpointer;  //backpointer[][]记录单个词中每个词性取得最大概率时所对应的前一个词性的位置
    backpointer=new int[sw][sp];
    for(int i=0;i<sw;i++)
    {
    	for(int j=0;j<sp;j++)
    	  backpointer[i][j]=0;
    }
    
    for(int s=0;s<sp;s++) //对test[]中的第一个词，初始化在每个词性下产生该词的概率。 
    {   
      path[0][s]=Math.log(((double)hash1.get(table_pos[s]).hashCode()/(double)temp1.length)*100000000)+observe[0][s];
    }
    //for(int s=0;s<sp;s++)
      //System.out.println(path[0][s]);    
    for(int i=1;i<sw;i++)  //对test[]中剩下的词，依次计算单个词性对应的最大概率并记录其位置
    {
    	for(int j=0;j<sp;j++)
    	{
    		double maxp=path[i-1][0]+status[j][0]+observe[i][j];  
    		int index=0;
    		for(int k=1;k<sp;k++)
    		{
    		  path[i][j]=path[i-1][k]+status[j][k]+observe[i][j];
    		  if(path[i][j]>maxp)
    		  {
    		    index=k;
    		    maxp=path[i][j];
    		  }
    		}
    		backpointer[i][j]=index;
    		path[i][j]=maxp;
    	} 
    }
    /*for(int i=0;i<sw;i++)
      for(int j=0;j<sp;j++)
        System.out.println(backpointer[sw-2][j]); */
    
    //回溯遍历，找出概率最大的路径,输出结果 
    int maxindex=0;   //记录测试文本中最后一个词取得最大概率的位置。
    double max=path[sw-1][0];
    for(int i=1;i<sp;i++)
    {      
      if(path[sw-1][i]>max)
      {
        maxindex=i;
        max=path[sw-1][maxindex]; 
      }   
    }
    //System.out.println(max);
    
    String[] result;  //存储词性标注结果
    String[] object;  //存储结果集中的所有词性，用于计算精确度
    result=new String[sw];
    object=new String[sw];
    result[sw-1]=test[sw-1]+'/'+table_pos[maxindex];
    object[sw-1]=table_pos[maxindex];
    int t=0;
    int front=maxindex;
    for(int i=sw-2;i>=0;i--)
    {
    	t=backpointer[i+1][front];
    	result[i]=test[i]+'/'+table_pos[t]+"  ";
    	object[i]=table_pos[t];
    	front=t;
    }
      
   try{
   	   FileWriter f=new FileWriter("result.txt");
       for (int i = 0; i < result.length; i++)
          f.write(result[i] + "");
       f.flush();
       f.close();
      }
   catch(IOException e)
     {System.out.println("错误");}

    //--------------------------------------------------------------------------------------------------------
    // 测试算法效果
   int correct=0;
    double correct_rate=0.0;
    String[] source;  //source[]数组用于存储测试文本中单个词的词性标注符号
    source=sentence.split("[0-9|-]*/|\\s{1,}[^a-z]*|][a-z]"); //仅保留词性标注符号。
    String[] source1;
    source1=new String[source.length-1];//去除source[0]为空的情况
    for(int i=0;i<source.length-1;i++)
      source1[i]=source[i+1];	          
      
    for(int i=0;i<sw;i++)
    {
      if(source1[i].equals(object[i]))
        correct++;
    }
    correct_rate=(double)correct/(double)sw*100;   
    System.out.println("正确标注词性数:"+correct+"  "+"标注的总词数："+sw);
    System.out.println("正确率："+correct_rate+"%"); 
  }
}