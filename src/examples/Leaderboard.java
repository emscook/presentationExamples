package examples;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.Comparator;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.IOException;
import java.util.Scanner;
import java.util.HashSet;
import java.util.HashMap;
public class Leaderboard {
	    	    static class Hotel implements Comparable<Hotel>{
			String myName;
			Double myLatitude;
			Double myLongitude;
			Double myRating;
			// 0 <= myX <= 90.0
			// 0 <= myY <= 180.0
			Hotel(String name, Double myX, Double myY, Double rating){
				this.myName = name;
				this.myLatitude = myX;
				this.myLongitude = myY;
				this.myRating = rating;
			}
			//Natrual ordering is alphabetical.
			public int compareTo(Hotel that) {
				return this.myName.compareTo(that.myName);
			}
			//A Hotel's rating can change and doesn't identify it!
			public boolean equals(Object c) {
		        if (c == this)
		            return true;
		        if (!(c instanceof Hotel))
		            return false;
		        Hotel that = (Hotel) c;
		        if (this.myName == that.myName && this.myLatitude == that.myLatitude)
		        	return true;
		        else 
		        	return false;
		    }
			
			public String toString() {
				return String.format("%30s  %.2f %.2f %.1f", myName, myLatitude, myLatitude, myRating);
			}
			// toString the Hotel showing distance rather than location (in miles)
			public String printDistance(Double inputLat, Double inputLong) {
				return String.format("%30s %7.2f %6.1f", myName,Math.hypot(myLatitude-inputLat, myLongitude-inputLong)*69.0  , myRating);
			}
			public static String printDistanceHeader() {
				return String.format("%30s   %s %s", "Hotel Name","Miles", "Rating");
			}
		}
	private static String firstNamesFile = "firstNames.txt";
	private static String lastNamesFile = "lastNames.txt";
	private static Random generator = new Random();
	private static String[] hotelTypes = new String[] {"Hostel","Hotel","Lodge","Hotel","Resort","Saloon","Tavern","Auberge","Hospice",
	"Hostelry","Roadhouse"
	};
	
	public static String[] getLines(String inputFilename){
	    List<String> lineList = new ArrayList<String>();
		try(BufferedReader br = new BufferedReader(new FileReader(inputFilename))){
	        for(String line; (line = br.readLine()) != null; ) 
	            lineList.add(line);
		}
	    catch(IOException err) {
	    	System.out.println("oh no");
	    }
		return lineList.toArray(new String[0]);
		}
	
	public static String[] getNames(int nameCount, String[] firsties, String[] lasties) {
		//Random generator = new Random();
		int firstTop = firsties.length;
		int lastTop = lasties.length;
	    List<String> nameList = new ArrayList<String>();
	    for(int i = 0; i < nameCount; i++)
	    	nameList.add(firsties[generator.nextInt(firstTop)] + " " + lasties[generator.nextInt(lastTop)]);
		return nameList.toArray(new String[0]);
	}
	
	public static String[] getNamesFromFiles(int nameCount, String firsts, String lasts) {
	    String[] firstNames = Leaderboard.getLines(firsts);
	    String[] lastNames = Leaderboard.getLines(lasts);
	    for(int i = 0; i < lastNames.length; i++) 
	    	lastNames[i] += " " + hotelTypes[generator.nextInt(hotelTypes.length)];
		return getNames(nameCount, firstNames, lastNames);
	}
	
	public static Integer[] getRandomScores(int randomCount, int maxScore) {
		List<Integer> scoreList = new ArrayList<Integer>();
	    for(int i = 0; i < randomCount; i++)
	    	scoreList.add(generator.nextInt(maxScore + 1));
	    return scoreList.toArray(new Integer[0]);
		
	}
	
	public static Double[][] getRandomLocations(int randomCount) {
		Double[][] hotelLocations = new Double[randomCount][2];
	    for(int i = 0; i < randomCount; i++)
	    	hotelLocations[i] = new Double[] {generator.nextDouble() * 90.0, generator.nextDouble() * 180.0};
	    return hotelLocations;
		
	}
	public static Double[] getRandomRatings(int ratingCount, int maxStars){
		Double[] hotelRatings = new Double[ratingCount];
		for(int i = 0; i < ratingCount; i++)
			hotelRatings[i] = generator.nextDouble() * maxStars;
		return hotelRatings;
    }
	
	public static void main(String args[]) throws IOException{
		//Treemap
		//
		//
	    int hotelCount = 15000;
	    Scanner input = new Scanner(System.in); 
	    //Memphis = 35.1495° N, 90.0490° W
		System.out.print ( "Enter your latitude: " );
	    Double ourLat = input.nextDouble();
		System.out.print ( "Enter your longitude: " );
	    Double ourLong = input.nextDouble();
	    //Define a TreeMap of Hotels sorted according to their distance from us (our coordinates).
	    TreeMap<Hotel, Double> leaderTreeMap = new TreeMap<Hotel, Double>(
	    new Comparator<Hotel>() {
            @Override
            public int compare(Hotel e1, Hotel e2) {
            		Double someRandom = Math.hypot(e1.myLatitude-ourLat, e1.myLongitude-ourLong) - Math.hypot(e2.myLatitude-ourLat, e2.myLongitude-ourLong);
            		if(someRandom < 0.0)
            			return -1;
            		else if(someRandom > 0.0)
            			return 1;
               		return 0;
            }
        });
	    String[] allNames = Leaderboard.getNamesFromFiles(hotelCount, firstNamesFile, lastNamesFile);
	    Double[] ratings = Leaderboard.getRandomRatings(hotelCount, 5);
	    Double[][] hotelLocations = Leaderboard.getRandomLocations(hotelCount);
	    for(int i = 0; i < hotelCount; i++) 
	    	leaderTreeMap.put(new Hotel(allNames[i],hotelLocations[i][0],hotelLocations[i][1], ratings[i]), null);	
	    //Real coordinates and google rating, plus mine
	    Hotel currentHotel = new Hotel("Extended Stay",35.1039,89.8525, 3.1);
	    leaderTreeMap.put(currentHotel, 3.5);	
	    int topCount = 0;
	    int topNumber = 10;
	    System.out.println(Hotel.printDistanceHeader());
	    for(Hotel topGuy : leaderTreeMap.keySet()) {
	    	if (topCount >= topNumber)
	    		break;
	    	currentHotel = topGuy;
	    	System.out.println(topGuy.printDistance(ourLat, ourLong));
	    	topCount += 1;
	    }
    	String navigateString = "";
    	while(!navigateString.equals("X")) {
    		do {
    			System.out.print("Use ^ to navigate up and V to navigate down.( X to exit):");
    			navigateString = input.next();
    		}while((!navigateString.equals( "^")) && (!navigateString.equals("V")) && (!navigateString.equals("X")));
    		if(navigateString.equals("X")) {
    			currentHotel = currentHotel; // stay here, just exit.
    		}
    		if(navigateString.equals("^") && leaderTreeMap.lowerKey(currentHotel) != null) {
    				currentHotel = leaderTreeMap.higherKey(currentHotel);
    		}
    		else if(navigateString.equals("V") && leaderTreeMap.higherKey(currentHotel) != null) {
    			currentHotel = leaderTreeMap.lowerKey(currentHotel);
    		}
	    	System.out.println(currentHotel.printDistance(ourLat, ourLong));
	    	
    	}
	    input.close();
	    //End TreeMap
	    //
	    //ArrayList
	    //Considering its performance benefits over linkedlist for get, add and iteration, lets show a case where we need those a lot
	    //Sets won't help us here, lets try a list.
	    String[] badDirections = new String[] {"NORTH","EAST","NORTH","SOUTH","NORTH","WEST","NORTH","EAST", "WEST", "EAST", "WEST"};
	    ArrayList<String> toDoList = new ArrayList<String>();
	    for(String currentDirection : badDirections)
	    	toDoList.add(currentDirection);
	    while(toDoList.contains("NORTH") && toDoList.contains("SOUTH") ) {
	    	toDoList.remove("NORTH");
	    	toDoList.remove("SOUTH");
	    }
	    while(toDoList.contains("EAST") && toDoList.contains("WEST") ) {
	    	toDoList.remove("EAST");
	    	toDoList.remove("WEST");
	    }
	    System.out.println(toDoList);
	    //End ArrayList
	    //
	    //HashSet
	    //Earlier we used a large amount of strings. Lets use the efficiency of HashSet to find out something interesting about them.
	    //upon inspection, lastNamesFile seemed to contain every name in firstNamesFile. Lets see if thats true.
	    HashSet<String> firstHash = new HashSet<String>(Arrays.asList(Leaderboard.getLines(firstNamesFile)));
	    System.out.println(firstHash.size());
	    HashSet<String> lastHash = new HashSet<String>(Arrays.asList(Leaderboard.getLines(lastNamesFile)));
	    System.out.println(lastHash.size());
	    firstHash.removeAll(Arrays.asList(Leaderboard.getLines(lastNamesFile)));
	    System.out.println(firstHash.size());
	    System.out.println(firstHash);
	    //End HashSet 
	    //
	    //HashMap
	    //Here we're utilizing the utility of key->value relations. These obviously aren't the same string,
	    //but do they contain the same letters in the same amount? That is to say, are they anagrams of one another?
	    //We can build a hashmap of each keeping track of the count of each letter and use those to determine if they're
	    //anagrams.
	    String[][] checkPairs = new String[][] {
	    	{"a gentleman", "elegant man"},{"debit card", "bad credit"},{"eleven plus two",  "twelve plus one"},
	    	{"hot water", "worth tea"},{"vacation time", "i am not active"},{"conversation", "voices rant on"},
	    	{"the eyes", "they see"},{"schoolmaster", "the classroom"},{"the country side", "no city dust here"},
	    	{"the detectives", "detect thieves"},{"mummy", "my mum"},{"dormitory", "dirty room"},
	    	{"a decimal point", "im a dot in place"},{"clint eastwood", "old west action"},{"astronomers", "no more stars"}
	    };
	    HashMap<Character,Integer> leftChars;
	    HashMap<Character,Integer> rightChars;
	    
	    for(String[] pair : checkPairs) {
	    	boolean areAnagrams = true;
	    	leftChars = new HashMap<Character,Integer>();
	    	rightChars= new HashMap<Character,Integer>();
	    	for(Character leftLetter : pair[0].toCharArray()) {
	    		if(!leftLetter.equals(' ')) {
	    			if (leftChars.get(leftLetter) != null) {
	    				leftChars.put(leftLetter, leftChars.get(leftLetter) + 1);
	    			}
	    			else {
	    				leftChars.put(leftLetter, 1);
	    			}
	    		}
	    	}
	    	for(Character rightLetter : pair[1].toCharArray()) {
	    		if(!rightLetter.equals(' ')) {
	    			if (rightChars.get(rightLetter) != null)
	    				rightChars.put(rightLetter, rightChars.get(rightLetter) + 1);
	    			else 
	    				rightChars.put(rightLetter, 1);
	    		}
	    	}
	    	if(leftChars.keySet().equals(rightChars.keySet())) {
	    		for(Character leftyKey : leftChars.keySet()) {
	    			if(leftChars.get(leftyKey) != rightChars.get(leftyKey)) {
	    				areAnagrams = false;
	    			}
	    			
	    		}
	    	}
	    	else {
	    		areAnagrams = false;
	    	}
	    	if(areAnagrams) 
	    		System.out.println("YES! \"" + pair[0] + "\" is an anagram of \"" + pair[1] + "\"");
	    	else 
	    		System.out.println("NO! \"" + pair[0] + "\" is not an anagram of \"" + pair[1] + "\"");
	    	
	    }
	    
	    //End HashMap
	    //
	    ///
	    
	}
}


