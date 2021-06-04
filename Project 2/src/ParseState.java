package csi311;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import com.fasterxml.jackson.databind.ObjectMapper;
import csi311.MachineSpec.StateTransitions; 
import java.util.HashMap;
import java.util.Set;
import java.util.LinkedHashSet;

public class ParseState {

	public ParseState() {
	}
	//all the regexs
	String id_regex = "[0-9]{3}-[A-Za-z]{3}-[0-9]{4}";
	String time_regex = "[0-9]+";
	String customer_regex = "[0-9]{9}";
	String quant_regex = "[0-9]+";
	String cost_regex = "\\d+\\.*\\d{1,2}";
	//create array that stores malformed ID's
	ArrayList<String> malid = new ArrayList<String>();

	public void run(String jsonPath, String orderPath) throws Exception {
		System.out.println("Parse State");
		String json = processFile(jsonPath); 
		System.out.println("Raw json = " + json); 
		MachineSpec machineSpec = parseJson(json);
		dumpMachine(machineSpec); 
//order file------------------------------------------------------------------------------------------------------------------------------------
		//begin reading order file
		BufferedReader br = new BufferedReader(new FileReader(orderPath));
		String orderLine;
		//create a hashmap to store orderInfo later
		HashMap<String,String[]> orderInfo = new HashMap<String, String[]>();
		HashMap<String,Integer> map2 = new HashMap<String, Integer>();
		//This reads the order lines and checks each is properly formed and flags malformed lines 
		while ((orderLine = br.readLine()) != null) {
			//This separates the lines by comma and stores in array 
			String [] splitLine = orderLine.trim().split(","); 
			//all the stuff you need trimmed
			String timestamp = splitLine[0].trim(); 
			String orderId = splitLine[1].trim();
			String customerId = splitLine[2].trim();
			String state = splitLine[3].trim().toLowerCase();
			String quantity = splitLine[5].trim();
			String cost = splitLine[6].trim();
			//array orderArr holds the values of all the necessary order info
			String [] orderArr = new String[4];  
			//if this is 7 then its properly formed else its not
			if (splitLine.length == 7) {
				//this checks the ID's of properly formed lines
				if (orderId.matches(id_regex)) {
					//if the id matches continue into next condition
					if (timestamp.matches(time_regex)) {
						if (customerId.matches(customer_regex)) {
							if (quantity.matches(quant_regex)) {
								if (cost.matches(cost_regex)) {
									//from this point on only work on valid order lines
//Start on JSON file--------------------------------------------------------------------------------------------------------------------------------------------------------------                  
									//stores all valid formatted necessary info in each index
									orderArr[0] = orderId;
									orderArr[1] = state;
									orderArr[2] = quantity;
									orderArr[3] = cost;
									//this sets the prev_state and next_state
									if (orderInfo.containsKey(orderId)) {
										String[] arr = orderInfo.get(orderId);
										String last_state = arr[1];
										String oid = arr[0];
										String price = orderArr[3];
										//this checks that it makes a valid transition
										if (oid.contentEquals(orderId)) {
											if (machineSpec.verifyMachine(last_state, state)) {
												arr[1] = state;
												arr[2] = orderId;
												arr[3] = price;
												orderInfo.put(orderId, arr);
											} else {
												//if the array list doesn't contain Order id then flag 
												if (!malid.contains(orderId)) {
													malid.add(orderId);
												}
											}
										}
									} //ends if contains key
									//if it doesn't contain key  
									else {
										if (machineSpec.validateStartState(state)) {
											orderInfo.put(orderId, orderArr);
										}
										else {
											if (!malid.contains(orderId)) {
												malid.add(orderId);
											}
										} 
									}

//else statements for all the order file criteria------------------------------------------------------------------------------------------------------------------------------------
								} //ends if cost matches
								//if cost is malformed add id to the malid list
								else {
									malid.add(orderId);
								}
							} //ends if quantity matches
							//if quantity is invalid add id to the flagged id list
							else {
								malid.add(orderId);
							}
						}//ends if customerId matches
						//if customer id doesnt match take id and store in malid
						else {
							malid.add(orderId);
						}
					} //ends if timestamp matches
					//if the timestamp doesnt match the regex take id and store
					else {
						malid.add(orderId);
					}
				} //ends if the id matches

				//if the id doesnt match the regex then store in malid 
				else {
					malid.add(orderId);
				}
			}//ends if splitLine.length == 7 
			//If not properly formed but has a recoverable ID in array then store id in flagged id's
			else {
				if(orderId.matches(id_regex)) {
					// all malformed lines id's store in array list: malid 
					malid.add(orderId);
				}
			}//ends else splitLine == 7
			//get rid of repeats in the malid Arl
			Set<String> set = new LinkedHashSet<>(malid);
			set.addAll(malid);
			malid.clear(); 
			malid.addAll(set);
			//now array list holds all non-repeating flagged id's
//End reading Files-----------------------------------------------------------------------------------------------------------------------------------------------------------
		}//ends reading orderLine
		br.close();
		//for the last appearance of each id
		for (String id : orderInfo.keySet()) {
			//take final state specific for that id
			String final_state = orderInfo.get(id)[1];
			if (!map2.containsKey(final_state)) {
				int i = 0;
				//put the final states + 0 in the 2nd map   
				map2.put(final_state, i);
			}
		} //ends for loop 1
		for (String oID : orderInfo.keySet()) {
			//get and update this value
			String final2_state = orderInfo.get(oID)[1];
			//if its not invalid
			if (!malid.contains(oID)) {
				//add counter for the states
				int count = map2.get(final2_state);
				count = count + 1;
				//place final_states and count of those states into map2
				map2.put(final2_state, count);
			}
		}
		//get the prices totals
		//loads the states of map2 into key 
		for (String key : map2.keySet()) {
			//this hold the count of the states
			int statecounts = map2.get(key);
			float totalprice = 0; 
			//set the orderInfo hash map to key2
			for (String key2 : orderInfo.keySet()) {
				String [] mapvalues = orderInfo.get(key2);
				//states are in index 1
				String mapStates = mapvalues[1];
				//prices are in index 3, need to parse the float
				float mapPrice = Float.parseFloat(mapvalues[3]);
				//if it equals key and is not a flagged line
				if (mapStates.equals(key) && !malid.contains(key2)) { 
					totalprice = totalprice + mapPrice; 
				}
			}
//Print------------------------------------------------------------------------------------------------------------------------------------------------------------------------- 	

			System.out.println("State: " + key + " | count: " + statecounts + " | total price: $" + totalprice);      
		}
		System.out.println("Flagged ID's: " + malid + " Count: " + malid.size());
	} //ends run method



	private void dumpMachine(MachineSpec machineSpec) {
		if (machineSpec == null) {
			return;
		}
		for (StateTransitions st : machineSpec.getMachineSpec()) {
			System.out.println(st.getState() + " : " + st.getTransitions());
		}
	}


	private String processFile(String filename) throws Exception {
		System.out.println("Processing file: " + filename); 
		BufferedReader br = new BufferedReader(new FileReader(filename));  
		String json = "";
		String line; 
		while ((line = br.readLine()) != null) {
			json += " " + line; 
		} 
		br.close();
		// Get rid of special characters - newlines, tabs.  
		return json.replaceAll("\n", " ").replaceAll("\t", " ").replaceAll("\r", " "); 
	}


	private MachineSpec parseJson(String json) {
		ObjectMapper mapper = new ObjectMapper();
		try { 
			MachineSpec machineSpec = mapper.readValue(json, MachineSpec.class);
			return machineSpec; 
		}
		catch (Exception e) {
			e.printStackTrace(); 
		}
		return null;   
	}


	public static void main(String[] args) {
		ParseState theApp = new ParseState();
		String jsonPath = null; 
		String orderPath = null;
		if (args.length >= 2) {
			jsonPath= args[0];
			orderPath= args[1]; 
		}
		try { 
			theApp.run(jsonPath,orderPath);
		}
		catch (Exception e) {
			System.out.println("Something bad happened!");
			e.printStackTrace();
		}
	} 


  
}
