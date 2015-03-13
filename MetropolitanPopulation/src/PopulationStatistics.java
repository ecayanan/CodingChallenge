import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class PopulationStatistics {
	private ArrayList<CityStatistics> growthStatsList;
	private ArrayList<Map.Entry<String, Integer>> stateStatsList;
	private ArrayList<Map.Entry<String,Integer>> stateStatsPopList;
	private ArrayList<Map.Entry<String, Float>> statePercentList;
	private HashMap<String,Integer> statesTable;
	private HashMap<String,Integer> statesPopTable;
	public static final int MINIMUM_POPULATION = 50000;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		PopulationStatistics obj = new PopulationStatistics();
		obj.run();

	}
	
	public void run(){
		String csvFile = "C:/Development/CodingChallenge/75f647c2ac77-Metropolitan_Populations_2010-2012_.csv";
		BufferedReader br = null;
		String line = "";
		String csvSplitBy = ",";
		growthStatsList = new ArrayList<CityStatistics>();
		//stateStatsList = new ArrayList<StateStatistics>();
		statesTable = new HashMap<String,Integer>();
		statesPopTable = new HashMap<String,Integer>();
		try{
			
			br = new BufferedReader(new FileReader(csvFile));
			line = br.readLine(); //read first line with labels
			while((line = br.readLine()) != null)
			{
				//remove double quotes from the string
				line = line.replace("\"", "");
				//use comma as seperator
				
				String[] row = line.split(csvSplitBy);
				
				String city = row[0];
				String state = row[1];
				int pop1 = Integer.parseInt(row[2]);
				int pop2 = Integer.parseInt(row[3]);
				int pop3 = Integer.parseInt(row[4]);
				//System.out.println("pop1:" + pop1);
				if(pop1 >= MINIMUM_POPULATION && pop2 >= MINIMUM_POPULATION && pop3 >= MINIMUM_POPULATION)
				{
					CityStatistics  temp = new CityStatistics(city,state,pop1,pop2,pop3);
					growthStatsList.add(temp);
				}
				
				//Adding the population of 2010-2012 to states object

				if(statesTable.containsKey(state))
				{
					statesTable.put(state, statesTable.get(state) + (pop3-pop1));
					statesPopTable.put(state,statesPopTable.get(state) + pop1);
				}else
				{
					statesTable.put(state, pop3-pop1);
					statesPopTable.put(state,pop1);
				}
				
			} //end of while loop
			
			Collections.sort(growthStatsList,new GrowthCityComparator());
			stateStatsList = new ArrayList(statesTable.entrySet());
			stateStatsPopList = new ArrayList(statesPopTable.entrySet());
			statePercentList = new ArrayList();
			
			for(int i = 0; i < stateStatsList.size(); i++)
			{
				Map.Entry<String, Integer> temp1 = stateStatsList.get(i);
				Map.Entry<String, Integer> temp2 = stateStatsPopList.get(i);
				if( temp1.getKey() == temp2.getKey())
				{
					float percentage = (float)(temp1.getValue())/(float)(temp2.getValue());
					percentage = percentage * 100;
					//System.out.println("temp1 = " + temp1.getValue() + " temp 2 = " + temp2.getValue() + percentage);
					String tempState = temp1.getKey();
					statePercentList.add(new AbstractMap.SimpleEntry<String,Float>(tempState, percentage));
				}
			}
		    Collections.sort(statePercentList, new Comparator<Map.Entry<String, Float>>(){

		         public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
		        	if(o1.getValue() > o2.getValue()) return -1;
		        	if(o1.getValue() < o2.getValue()) return 1;
		            return 0;
		        }});
			
			//Printing out the 5 Cities with biggest Growth
			for(int i = 0; i < 5; i++){
				System.out.println("Cities with the biggest growth between 2010-2012");
				CityStatistics temp = growthStatsList.get(i);
				System.out.println(temp.getCityName()+ ": Population 2010 = " + temp.getPopulation1()
						+ " Population 2012 = " + temp.getPopulation3() + "\n Percent Change: "+ temp.getPercentChange() + "\n");
			}
			
			//Printing out the 5 Cities with the biggest decrease in population
			for(int i = growthStatsList.size()-5; i < growthStatsList.size()-1; i++)
			{
				System.out.println("Cities with the biggest shrink in population between 2010-2012");
				CityStatistics temp = growthStatsList.get(i);
				System.out.println(temp.getCityName()+ ": Population 2010 = " + temp.getPopulation1()
						+ " Population 2012 = " + temp.getPopulation3() + "\n Percent Change: "+ temp.getPercentChange() + "\n");				
			}
			
			
			//Printing out the 5 states with the biggest cumulative growth
			for(int i = 0; i < 5; i++)
			{
				System.out.println("States with the biggest cumulative growth between 2010-2012");
				Map.Entry<String,Float>temp = statePercentList.get(i);
				System.out.println(temp.getKey() + ": Percent Change: "+ temp.getValue() + "\n");
				
			}
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		} finally
		{
			//close buffered reader
			if(br != null)
			{
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

} //end of populationstatistics



	

class CityStatistics
{
	private String cityName;
	private String stateName;
	private int pop1;
	private int pop2;
	private int pop3;
	
	public CityStatistics(String cName, String sName, int one, int two, int three)
	{
		cityName = cName;
		stateName = sName;
		pop1 = one;
		pop2 = two;
		pop3 = three;		
	}
	

	public float getPercentChange()
	{
		float percentage;
		
		percentage = (float)(pop3 - pop1)/pop1;
		//System.out.println("pop3: " + pop3 + "pop1: " + pop1);
		return percentage*100;
	}
	
	public String getCityName()
	{
		return cityName;
	}
	
	public int getPopulation1()
	{
		return pop1;
	}
	
	public int getPopulation3()
	{
		return pop3;
	}

}//end of citystatistics



class GrowthCityComparator implements Comparator<CityStatistics>
{

	@Override
	public int compare(CityStatistics i, CityStatistics j)
	{
		
		if(i.getPercentChange() > j.getPercentChange()) return -1;
		if(i.getPercentChange() < j.getPercentChange()) return 1;
		return 0;
	}
	
}
	
	

