/*Brady Madden
 * CSI311 - Project1 
*/ 
 //imports of libraries 
  import java.util.regex.*;
  import java.io.BufferedReader;
  import java.io.FileReader;
  import java.util.ArrayList.*;
  import java.util.*;
  import java.io.*;
/*
 * This class reads a file and writes a report by sorting orders
 * into different groups specified by the Project1 directions 
*/
  public class Project1 {
/* 
*/
    public static void main (String[]args) {
//Necessary Variables 
         String filename = null; 
 //If a command line argument was given, use it as the filename.
         if (args.length > 0)  {
         filename = args[0];  } 
    System.out.println("Regex Co.");
       //File reader using given filename, else: null   
//-----------------------------------------------------------------------------------------------------------------------------------------------------------------------
 try { 
/*Read File and Store each line in Array: linesArray */       
     //If valid file name 
   if (filename != null) {
           System.out.println("Processing file " + filename); 
     // Open the file and connect it to a buffered reader.
             BufferedReader br = new BufferedReader(new FileReader(filename));  
             String line = null;  
     //Array list for storing dynamic size of file         
            ArrayList<String> linestore = new ArrayList<String>(); 
     // Get lines from the file one at a time until there are no more.
           while ((line = br.readLine()) != null) {
     //Array doesnt store blank lines  
            if (line.length() > 0) {
              linestore.add(line); }
     // Close the buffer and the underlying file.
           br.close();  }
     //Array list -> Array
            String [] linesArray = linestore.toArray(new String[linestore.size()]);
//--------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 /*Validate each Order form and store in Array : formedOrder */  
            //this pattern forms characters seperated by commas aka whole line pattern
            Pattern p = Pattern.compile("\\s*.+\\s*,\\s*.+\\s*,\\s*.+\\s*"); 
            Matcher mp;
        //array list that stores validated lines 
          ArrayList<String> formArr = new ArrayList<String>();
          ArrayList<String> malArr = new ArrayList<String>(); 
          //Validate each element in array using regex
            for (int i = 0; i <= linesArray.length; i++) {
                  mp = p.matcher(linesArray[i]);
             //If properly formed Line: Store in array formArr       
                  if (mp.matches()){
                    formArr.add(linesArray[i]); }
                 //If not properly formed, store in malformedOrder
                  if (!mp.matches()) {
                    malArr.add(linesArray[i]); } }
            //Array List -> mal/formedOrder (still have to collect id's)
        String [] malformedOrder = malArr.toArray(new String[malArr.size()]); //1 malformed    
       //This arr formedOrder still has invalid addresses ie: foo bar
        String [] formedOrder = formArr.toArray(new String[formArr.size()]); 
        //FormedOrder should hold all properly formatted Lines
//-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
//This section validates addresses if malformed store in malformAddress 
        //This pattern because these are the only street options ie: broadway, avenues or streets (w/e) 
        Pattern add = Pattern.compile("[^-](Broadway|broadway|Bway|B'way|bway|b'way|Avenue|Ave|ave|Street|St|st|street)");
        Matcher madd;
       //this array will hold formed addresses
        ArrayList<String> formAddyarl = new ArrayList<String>();  
       //this array will hold malformed addresses ie: foo bar
        ArrayList<String> malformAddyarl = new ArrayList<String>();  
        for (int k = 0; k <= formedOrder.length; k++) {
          madd = add.matcher(formedOrder[k]);
          //if pattern matches store in formedAddyarl
          if (madd.matches()){   
                formAddyarl.add(formedOrder[k]);   }
          if (!madd.matches()){   
                malformAddyarl.add(formedOrder[k]);   }
        }
      //Array Lists -> Arrays
      String [] formedAddress = formAddyarl.toArray(new String[formAddyarl.size()]);
      String [] malformAddress = malformAddyarl.toArray(new String[malformAddyarl.size()]); //2 malformed
        //formedAddress should hold all formatted lines + proper addresses
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
/*Validate each ID store malformed ID's in Array: malformArr */
            //this pattern matches ID's
             Pattern id = Pattern.compile("\\s*[0-9]{3}-[a-zA-z]{3}-[0-9]{4}\\s*,\\s*");
             Matcher mid;
             Matcher m;
      //create array that stores malformed ID's
        ArrayList<String> malformarl = new ArrayList<String>();  
       //this array will store the full order line of properly formed ID's
        ArrayList<String> fullValidAddyarl = new ArrayList<String>();  
        //This compares the ID pattern to all properly formed lines 
          for (int x = 0; x <= formedAddress.length; x++) {
               mid = id.matcher(formedAddress[x]); 
               //if it doesnt match the id pattern it is stored in malformedId
           if (!mid.matches()) { 
                 malformarl.add(formedAddress[x]);  }  
               if (mid.matches()) {
                    fullValidAddyarl.add(formedAddress[x]); }  }
    //Array List -> Array to hold malformed Id values    
     String [] malformedId = malformarl.toArray(new String[malformarl.size()]); //3 malformed
     //holds full order line of those w/ validated id's 
     String [] fullValidAddy = fullValidAddyarl.toArray(new String[fullValidAddyarl.size()]);
     //fullValidAddy stores lines that have proper format + id + addresses
 //--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

 /*This section takes the addresses and counts all Streets and seperates East and West*/ 
       int westcount = 0;
       int eastcount = 0;
      //East Pattern
       Pattern east = Pattern.compile("[A-Za-z0-9 ]*(e.|e|E.|E|East|east|EAST)[A-Za-z0-9 ]*(Street|St.|St|st|st.|STREET)");
       Matcher meast;
       //West Pattern
       Pattern west = Pattern.compile("[A-Za-z0-9 ]*(w.|w|W.|W|West|west|WEST)[A-Za-z0-9 ]*(Street|St.|St|st|st.|STREET)");
      Matcher mwest;
      //Loop for all properly formed orders 
      for (int a =0; a<= fullValidAddy.length; a++) { 
       meast = east.matcher(fullValidAddy[a]);
       mwest = west.matcher(fullValidAddy[a]);
    //if it matches the east pattern counter goes up by 1
       if (meast.matches()) {
           eastcount = eastcount + 1;  }
       //if it matches west, westcount goes up by 1
       if (mwest.matches()) {      
           westcount =westcount + 1; }
      }
      //These lines print the street (west/east) tally
      System.out.println("West: " + westcount);
      System.out.println("East: " + eastcount);
 //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------   
    /*This section collects the tally for the avenues and prints*/
      int avecount = 0;
     //All acceptable ways of formatting ave
      Pattern ave = Pattern.compile("[^-][A-Za-z0-9 ]*[A-Za-z0-9 ]*(Avenue|Ave.|Ave|ave|ave.|AVE|AVENUE)");
     Matcher mave;
     for (int g = 0; g<= fullValidAddy.length; g++) { 
        mave = ave.matcher(fullValidAddy[g]);
        if (mave.matches()) { 
              avecount = avecount + 1;   }
     }
     //prints aves:
     System.out.println("Avenues: " + avecount); 
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 /*This Section collects the tallys for the Broadways and prints*/
    int bwaycount = 0;
    //all acceptable ways of formatting Broadway
    Pattern bway = Pattern.compile("[A-Za-z0-9 ]*[A-Za-z0-9 ]*(Broadway|BROADWAY|broadway|bway|BWAY|Bway|B'way|b'way|B'WAY)");
    Matcher mbway;
    for (int y = 0; y <= fullValidAddy.length; y++) { 
     mbway = bway.matcher(fullValidAddy[y]);
     if (mbway.matches()) { 
       bwaycount = bwaycount + 1;  }
    }
    //prints broadways:
    System.out.println("Broadways: " + bwaycount);
//-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
  /*This section gets the weights of the packages and lbscounter +1 if > 50 lbs */
     int lbscounter = 0;
     String [] splitvalues = new String[fullValidAddy.length];
     //lbs is a int array with everything in full valid addy 
     //full valid addys is the string array
     for (int s = 0; s <= fullValidAddy.length; s++){
       //this splits each line by the second comma
       splitvalues = fullValidAddy[s].split("\\s*.*\\s*,\\s*.*\\s*,\\s*");
     }
     //stores values of fullValidAddy as an integer rather than a string
     ArrayList<Integer> lbsarl = new ArrayList<Integer>();
     for (int r = 0; r <= splitvalues.length; r++) { 
       //this is the line that converts string to int
      lbsarl.add(Integer.parseInt(splitvalues[r]));  }
    //Array list -> Array
    int [] lbs = new int[lbsarl.size()];
    for (int t = 0; t <= lbs.length; t++) {
   //this stores everything from lbsarl -> lbs
      lbs[t] = lbsarl.get(t);  
    } 
   //Finally this is where the package weights are identified and seperated
    for (int z = 0; z <= lbs.length; z++) {
      if (lbs[z] > 50) {
           lbscounter = lbscounter + 1;          }
    }
   System.out.println("Packages > 50lbs: " + lbscounter);
 //--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 //This prints the malformed lines id numbers
   //this pattern captures Id #'s
     Pattern malpt = Pattern.compile("\\s*([0-9]{3}-[a-zA-z]{3}-[0-9]{4})\\s*");
     //Extract ID # from 1.malformed orders
     Matcher m1id;
   //This list is to store the ID# of the malformed orders
   ArrayList<String> malOid = new ArrayList<String>();  
     for (int w = 0; w <= malformedOrder.length; w++) {
        m1id = malpt.matcher(malformedOrder[w]);                      
     //If malformed Order has an ID store in Array: malformedOrderId
      if (m1id.matches()) { 
       malOid.add(malformedOrder[w]); } }
String [] malformedOrderId = malOid.toArray(new String[malOid.size()]);     
//Extract ID # from 2.malformed addresses
     Matcher m2id;
     ArrayList<String> maladdy = new ArrayList<String>();
     for (int d =0; d <= malformAddress.length; d++) { 
     m2id = malpt.matcher(malformAddress[d]);
     if (m2id.matches()) {
            maladdy.add(malformAddress[d]);         } }
    String [] malAdd= maladdy.toArray(new String[maladdy.size()]);
 //Extract ID # from 3.malformed id
  Matcher m3id;
  ArrayList<String> malid = new ArrayList<String>();
  for (int h = 0; h <= malformedId.length; h++) {
    m3id = malpt.matcher(malformedId[h]);
     if (m3id.matches()) {
            malid.add(malformedId[h]);         }  }
  String [] mal3 = malid.toArray(new String[malid.size()]);
 //Transform all malformed arrays to printable string 
    String arr1 = Arrays.toString(malformedOrderId);
    String arr2 = Arrays.toString(malAdd);
    String arr3 = Arrays.toString(mal3);
    //This line prints the malformed Id's
  System.out.println("Flagged ID's:" + arr1 + arr2 + arr3); 
     //Flagged ID's should be printed 
 
   } //End if valid filename Application

 } //End try
 
//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
catch (Exception e) {
  // If anything bad happens, report it.
      System.out.println("File Name Invalid!");
      e.printStackTrace();
     }
   
     } //End Main
  
  } //End Program
   
