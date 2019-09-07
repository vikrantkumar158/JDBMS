import java.io.*;
import java.util.Scanner;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.*;
class CreateAndInsert{
	public void create(String filename,String rows)throws IOException
	{
		int flag=0;
		String[] arr=rows.split(",");
		for(int i=0;i<arr.length&&flag==0;++i)
		{
			for(int j=0;j<arr[i].length()&&flag==0;++j)
			{
				if(!Character.isLetter(arr[i].charAt(j))&&j==0)
					flag=1;
				else if(!Character.isLetter(arr[i].charAt(j))&&!Character.isDigit(arr[i].charAt(j))&&arr[i].charAt(j)!='_')
					flag=2;
			}
		}
		if(flag==0)
		{
			FileWriter fw=new FileWriter(filename.toLowerCase()+".db");
			for(int i=0;i<arr.length;++i)
			{
				fw.write(arr[i]);
				fw.write(" ");
			}
			fw.write("\n");
			fw.close();
			System.out.println("Table created successfully");
		}
		else if(flag==1)
			System.out.println("Error: Row names must not start with Digits, Underscore and Symbols");
		else if(flag==2)
			System.out.println("Error: Row names must not contain Symbols");
	}
	public void insert(String filename,String rows,String data)throws IOException
	{
		String createdRows;
		try{
			File file=new File(filename.toLowerCase()+".db");
			Scanner sc=new Scanner(file);
			createdRows=sc.nextLine().toLowerCase();
			sc.close();
			String temp=" "+createdRows;
			String[] arr1=rows.split(",");
			String[] arr2=data.split(",");
			int i,flag=1;
			for(i=0;i<arr1.length&&flag==1;++i)
			{
				String t=" "+arr1[i]+" ";
				if(!temp.toLowerCase().contains(t.toLowerCase()))
					flag=0;
			}
			if(flag==1)
			{
				String[] arr3=createdRows.split(" ");
				JSONObject obj=new JSONObject();    
				for(i=0;i<arr3.length;++i)
				{
					obj.put(arr3[i].toLowerCase(),"");	
				}
				for(i=0;i<arr1.length;++i)
				{
					obj.put(arr1[i].toLowerCase(),arr2[i]);
				}
				FileWriter fw=new FileWriter(filename.toLowerCase()+".db",true);
				fw.write(obj.toJSONString());
				fw.write("\n");
				fw.close();
				System.out.println("Data entered successfully !");
			}
			else
			{
				System.out.println("Error: Table doesn't contains the row(s) specified");
			}
		}
		catch(FileNotFoundException e)
		{
			System.out.println("Error: No such table found");
		}
	}
}
class Select{
	public void select(String filename,String args)
	{
		String createdRows;
		try{
			File file=new File(filename.toLowerCase()+".db");
			Scanner sc=new Scanner(file);
			createdRows=sc.nextLine();
			String ch;
			if(args.equals("*"))
			{
				args=createdRows;
				ch=" ";
			}
			else
				ch=",";
			String temp=" "+createdRows;
			String[] arr1=args.split(ch);
			int i,flag=1;
			for(i=0;i<arr1.length&&flag==1;++i)
			{
				String t=" "+arr1[i]+" ";
				if(!temp.toLowerCase().contains(t.toLowerCase()))
					flag=0;
			}
			if(flag==1)
			{
				while(sc.hasNextLine())
				{
					try{
						JSONParser jp=new JSONParser();
						JSONObject jo=(JSONObject) jp.parse(sc.nextLine());
						for(i=0;i<arr1.length;++i)
							System.out.print(jo.get(arr1[i].toLowerCase())+" ");
						System.out.println();
					}
					catch(ParseException pe)
					{
						System.out.println("Error: String cannot be parsed");
						break;
					}
				}
			}
			else
			{
				System.out.println("Error: Table doesn't contains the row(s) specified");
			}
			sc.close();
		}
		catch(FileNotFoundException e)
		{
			System.out.println("Error: No such table found");
		}	
	}
	public void selectcreateinsert(String targetfile,String filename,String args)throws IOException
	{
		try{
			FileReader fr=new FileReader(targetfile.toLowerCase()+".db");
			fr.close();
			System.out.println("Table already exists");
		}
		catch(FileNotFoundException fe)
		{
			CreateAndInsert c=new CreateAndInsert();
			String createdRows;
			try{
				File file=new File(filename.toLowerCase()+".db");
				Scanner sc=new Scanner(file);
				createdRows=sc.nextLine();
				String ch;
				int i,flag=1;
				if(args.equals("*"))
				{
					args=createdRows;
					for(i=0;i<args.length();++i)
					{
						if(args.charAt(i)==' ')
						{
							args=args.substring(0,i)+","+args.substring(i+1);
						}
					}
					args=args.substring(0,args.length()-1);
				}
				String temp=" "+createdRows;
				String[] arr1=args.split(",");
				if(!args.equals("*"))
				{
					for(i=0;i<arr1.length&&flag==1;++i)
					{
						String t=" "+arr1[i]+" ";
						if(!temp.toLowerCase().contains(t.toLowerCase()))
							flag=0;
					}
				}
				if(flag==1)
				{
					c.create(targetfile,args);
					while(sc.hasNextLine())
					{
						try{
							JSONParser jp=new JSONParser();
							JSONObject jo=(JSONObject) jp.parse(sc.nextLine());
							String data="";
							for(i=0;i<arr1.length;++i)
							{
								data+=jo.get(arr1[i].toLowerCase())+",";
							}
							data=data.substring(0,data.length()-1);
							c.insert(targetfile,args,data);
						}
						catch(ParseException pe)
						{
							System.out.println("Error: String cannot be parsed");
							break;
						}
					}
				}
				else
				{
					System.out.println("Error: Table doesn't contains the row(s) specified");
				}
				sc.close();
			}
			catch(FileNotFoundException e)
			{
				System.out.println("Error: No such table found");
			}
		}	
	}
	public boolean compare(String s1,String s2,String symbol)
	{
		s1=s1.toLowerCase();
		s2=s2.toLowerCase();
		int i,j;
		if(symbol.equals("ge"))
		{
			for(i=0,j=0;i<s1.length()&&j<s2.length();++i,++j)
			{
				if(s1.charAt(i)==s2.charAt(j))
					continue;
				else if(s1.charAt(i)>s2.charAt(j))
					return true;
				return false;
			}
			if(s1.length()<s2.length())
				return false;
			return true;
		}
		else if(symbol.equals("le"))
		{
			for(i=0,j=0;i<s1.length()&&j<s2.length();++i,++j)
			{
				if(s1.charAt(i)==s2.charAt(j))
					continue;
				else if(s1.charAt(i)<s2.charAt(j))
					return true;
				return false;
			}
			if(s1.length()>s2.length())
				return false;
			return true;
		}
		else if(symbol.equals("e"))
		{
			if(s1.equals(s2))
				return true;
			return false;
		}
		else if(symbol.equals("g"))
		{
			for(i=0,j=0;i<s1.length()&&j<s2.length();++i,++j)
			{
				if(s1.charAt(i)==s2.charAt(j))
					continue;
				else if(s1.charAt(i)>s2.charAt(j))
					return true;
				return false;
			}
			if(s1.length()>s2.length())
				return true;
			return false;
		}
		else if(symbol.equals("l"))
		{
			for(i=0,j=0;i<s1.length()&&j<s2.length();++i,++j)
			{
				if(s1.charAt(i)==s2.charAt(j))
					continue;
				if(s1.charAt(i)<s2.charAt(j))
					return true;
				return false;
			}
			if(s1.length()<s2.length())
				return true;
			return false;
		}
		else if(symbol.equals("ne"))
		{
			if(s1.equals(s2))
				return false;
			return true;
		}
		return false;
	}
	public void where(String filename,String args,String condition)
	{
		String createdRows;
		try{
			File file=new File(filename.toLowerCase()+".db");
			Scanner sc=new Scanner(file);
			createdRows=sc.nextLine();
			String ch;
			if(args.equals("*"))
			{
				args=createdRows;
				ch=" ";
			}
			else
				ch=",";
			String temp=" "+createdRows;
			String[] arr1=args.split(ch);
			String[] arr2=condition.split(" ");
			int i,j,flag=1;
			for(i=0;i<arr1.length&&flag==1;++i)
			{
				String t=" "+arr1[i]+" ";
				if(!temp.toLowerCase().contains(t.toLowerCase()))
					flag=0;
			}
			for(i=0;i<arr2.length&&flag==1;i+=4)
			{
				String t=" "+arr2[i]+" ";
				if(!temp.toLowerCase().contains(t.toLowerCase()))
					flag=0;
			}
			if(flag==1)
			{
				JSONArray ja=new JSONArray();
				while(sc.hasNextLine())
				{
					try{
						JSONParser jp=new JSONParser();
						JSONObject jo=new JSONObject();
						jo=(JSONObject) jp.parse(sc.nextLine());
						if(compare(jo.get(arr2[0].toLowerCase()).toString(),arr2[2].substring(1,arr2[2].length()-1),arr2[1]))
						{
							ja.add(jo);
						}
					}
					catch(ParseException pe)
					{
						System.out.println("Error: String cannot be parsed");
						break;
					}
				}
				for(i=4;i<arr2.length;i+=4)
				{
					for(j=0;j<ja.size();)
					{
						JSONObject jo=(JSONObject)ja.get(j);
						if(compare(jo.get(arr2[i].toLowerCase()).toString(),arr2[i+2].substring(1,arr2[i+2].length()-1),arr2[i+1]))
							++j;
						else
							ja.remove(j);
					}
				}
				for(j=0;j<ja.size();++j)
				{
					JSONObject jo=(JSONObject)ja.get(j);
					for(i=0;i<arr1.length;++i)
					{
						System.out.print(jo.get(arr1[i].toLowerCase()).toString()+" ");
					}
					System.out.println();
				}
			}
			else
			{
				System.out.println("Error: Table doesn't contains the row(s) specified");
			}
			sc.close();
		}
		catch(FileNotFoundException e)
		{
			System.out.println("Error: No such table found");
		}
	}
}
public class Jdbms
{
	public static String trim(String s)
	{
		if(s.equalsIgnoreCase("quit")||s.equalsIgnoreCase("exit"))
			return s;
		if(s.lastIndexOf(";")!=-1)
			s=s.substring(0,s.lastIndexOf(";")+1);
		for(int i=0;(i+1)<s.length();)
		{
			if(i==0&&s.charAt(i)==' ')
			{
				s=s.substring(1);
			}
			else if(i!=0&&s.charAt(i)==' ')
			{
				if(s.charAt(i+1)==' ')
					s=s.substring(0,i+1)+s.substring(i+2);
				else if(s.charAt(i+1)==',')
					s=s.substring(0,i)+s.substring(i+1);
				else if(s.charAt(i+1)==')')
					s=s.substring(0,i)+s.substring(i+1);
				else
					++i;
			}
			else if(i!=0&&s.charAt(i)==','&&s.charAt(i+1)==' ')
			{
				s=s.substring(0,i+1)+s.substring(i+2);
			}
			else if(i!=0&&s.charAt(i)=='('&&s.charAt(i-1)!=' ')
			{
				if(s.charAt(i+1)==' ')
					s=s.substring(0,i)+" "+s.substring(i,i+1)+s.substring(i+2);
				else  
					s=s.substring(0,i)+" "+s.substring(i);
			}
			else
				++i;
		}
		s=s.substring(0,s.length()-1)+" "+s.substring(s.length()-1);
		return s;
	}
	public static String trimCondition(String s)
	{
		s=s.toLowerCase();
		s=s.replaceAll(" ","");
		s=s.replaceAll("\"and","\" & ");
		s=s.replaceAll("or"," | ");
		s=s.replace(">="," ge ");
		s=s.replace("<="," le ");
		s=s.replace("!="," ne ");
		s=s.replace("="," e ");
		s=s.replace(">"," g ");
		s=s.replace("<"," l ");
		s=s.replace("  "," ");
		return s;
	}
	public static boolean checkValidCondition(String s)
	{
		String[] arr=s.split(" ");
		for(int i=2;i<arr.length;i+=4)
		{
			if(arr[i].charAt(0)!='\"'||arr[i].charAt(arr[i].length()-1)!='\"')
			{
				System.out.println("Error: Comparison Argument Error");
				return false;
			}
		}
		for(int i=1;i<arr.length;i+=4)
		{
			if(!(arr[i].equals("ge")||arr[i].equals("le")||arr[i].equals("ne")||arr[i].equals("e")||arr[i].equals("g")||arr[i].equals("l")))
			{
				System.out.println("Error: Provide proper comparison operator found");
				return false;
			}
		}
		return true;
	}
	public static void main(String args[])throws IOException
	{
		Scanner sc=new Scanner(System.in);
		System.out.println("Connection successful");
		System.out.println("Welcome to DBMS");
		while(true)
		{
			String s;
			int i;
			System.out.print(">");
			s=sc.nextLine();
			s=trim(s);
			String[] arr=s.split(" ");
			if(s.equalsIgnoreCase("quit")||s.equalsIgnoreCase("exit"))
			{
				System.out.println("Exiting");
				System.out.println("DB disconnected");
				break;
			}
			else if(arr[arr.length-1].charAt(0)!=';')
			{
				System.out.println("Error: ; missing at the end of statement");
			}
			else if(arr[0].equalsIgnoreCase("create")&&arr[1].equalsIgnoreCase("table"))
			{
				try
				{
					if(!arr[3].equalsIgnoreCase("as")&&arr.length==5)
					{
						if(arr[3].charAt(0)!='(')
						{
							System.out.println("Error: ( missing in the statement");
						}
						else if(arr[3].charAt(arr[3].length()-1)!=')')
						{
							System.out.println("Error: ) missing in the statement");
						}
						else if(arr[3].charAt(0)=='('&&arr[3].charAt(1)==')')
						{
							System.out.println("Error: Column Names must not contain special characters");	
						}
						else
						{
							try{
								FileReader fr=new FileReader(arr[2].toLowerCase()+".db");
								fr.close();
								System.out.println("Table already exists !");
							}
							catch(FileNotFoundException e)
							{
								CreateAndInsert c=new CreateAndInsert();
								c.create(arr[2],arr[3].substring(1,arr[3].length()-1));
							}
						}
					}
					else if(arr.length==9)
					{
						if(arr[4].equalsIgnoreCase("select")&&arr[6].equalsIgnoreCase("from"))
						{	
							Select se=new Select();
							se.selectcreateinsert(arr[2],arr[7],arr[5]);
						}
					}
					else
					{
						System.out.println("Error: Too Many arguments");
					}
				}
				catch(ArrayIndexOutOfBoundsException e)
				{
					System.out.println("Error: Less arguments specified");
				}
			}
			else if(arr[0].equalsIgnoreCase("insert")&&arr[1].equalsIgnoreCase("into")&&arr.length==7)
			{
				try
				{
					if(arr[3].charAt(0)!='('||arr[3].charAt(arr[3].length()-1)!=')')
					{
						System.out.println("Error: Columns not specified");
					}
					else if(arr[5].charAt(0)!='('||arr[5].charAt(arr[5].length()-1)!=')')
					{
						System.out.println("Error: Values not specified");
					}
					else if(arr[3].split(",").length>arr[5].split(",").length)
					{
						System.out.println("Error: Not enough arguments for columns specified");	
					}
					else if(arr[3].split(",").length<arr[5].split(",").length)
					{
						System.out.println("Error: Not enough columns for arguments specified");	
					}
					else
					{
						CreateAndInsert c=new CreateAndInsert();
						c.insert(arr[2],arr[3].substring(1,arr[3].length()-1),arr[5].substring(1,arr[5].length()-1));
					}
				}
				catch(ArrayIndexOutOfBoundsException e)
				{
					System.out.println("Error: Less arguments specified");
				}
				
			}
			else if(arr[0].equalsIgnoreCase("select")&&arr[2].equalsIgnoreCase("from"))
			{
				if(arr.length==5)
				{
					try
					{
						Select se=new Select();
						se.select(arr[3],arr[1]);
					}
					catch(ArrayIndexOutOfBoundsException e)
					{
						System.out.println("Error: Less arguments specified");
					}
				}
				else if(arr[4].equalsIgnoreCase("where"))
				{
					for(i=6;i<arr.length-1;++i)
					{
						arr[5]=arr[5]+" "+arr[i];
					}
					arr[5]=trimCondition(arr[5]);
					if(checkValidCondition(arr[5]))
					{
						Select se=new Select();
						se.where(arr[3],arr[1],arr[5]);
					}
				}
				else
				{
					System.out.println("Error: Too Many Arguments");		
				}
			}
			else
			{
				System.out.println("Error: Too Many Arguments");
			}
		}
	}
}