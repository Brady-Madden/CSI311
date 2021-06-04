package csi311;
import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import csi311.Order;
import org.apache.derby.jdbc.EmbeddedDriver;
import com.fasterxml.jackson.databind.ObjectMapper;
import csi311.MachineSpec;


public class SqlDemo {

	public SqlDemo() {
	}

	private static final String DB_URL = "jdbc:derby:csi311-testdb1;create=true";
	private Connection conn = null;
	private Statement stmt = null;
	HashMap<String, Integer> stateMap = new HashMap<>();
	HashMap<String, Order> orderInfoMap = new HashMap<>();
	MachineSpec machinespec;

	private MachineSpec parsejson (String json) {
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
	private String processStateFile(String filename) throws Exception {
		System.out.println("Processing file: " + filename); 
		BufferedReader br = new BufferedReader(new FileReader(filename));  
		String json = "";
		String line; 
		while ((line = br.readLine()) != null) {
			json += " " + line; 
		} 
		br.close();
		return json.replaceAll("\n", " ").replaceAll("\t", " ").replaceAll("\r", " "); 
	}

	private void createConnection() {
		try {
			Driver derbyEmbeddedDriver = new EmbeddedDriver();
			DriverManager.registerDriver(derbyEmbeddedDriver);
			conn = DriverManager.getConnection(DB_URL);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void shutdown() {
		try {
			if (stmt != null) {
				stmt.close();
			}
			if (conn != null) {
				DriverManager.getConnection(DB_URL + ";shutdown=true");
				conn.close();
			}           
		}
		catch (SQLException sqlExcept) {
		}
	}

	public void createStateMachineTable() {
		int MAX_LENGTH = 10000;
		try {
			stmt = conn.createStatement();
			stmt.execute("Create table state_machines (id INT NOT NULL, json_string varchar(" + MAX_LENGTH + ") not null)");
			stmt.close();	
		}
		catch(SQLException sqlExcept) 
		{
			if(!tableAlreadyExists(sqlExcept))
			{
				sqlExcept.printStackTrace();
			}
		}
	}
	private void insertStateMachine(int tenantId, String json_string) {
		try {
			stmt = conn.createStatement();
			stmt.execute("insert into state_machines (id, json_string) values (" + tenantId + ",' " + json_string + "')");
			stmt.close();
		}
		catch(SQLException sqlExcept){
			sqlExcept.printStackTrace();
		} 
	}
	private void createOrderTable() {
		int MAX_LENGTH = 10000;
		try {
			stmt = conn.createStatement();
			stmt.execute("create table order_table (id INT NOT NULL, orderLine varchar(" + MAX_LENGTH + ") NOT NULL)");
			stmt.close();
		}
		catch (SQLException sqlExcept) {
			if(!tableAlreadyExists(sqlExcept)) {
				sqlExcept.printStackTrace();
			}
		}
	}
	private void insertOrderTable(int tenantId, String orderLine) {
		try {
			stmt = conn.createStatement();
			stmt.execute("insert into order_table (id,orderLine) values (" + tenantId + ",'" + orderLine +"')");
			stmt.close();
		}
		catch (SQLException sqlExcept) {
			sqlExcept.printStackTrace();
		}
	}		
	private boolean tableAlreadyExists(SQLException e) {
		boolean exists;
		if(e.getSQLState().equals("X0Y32")) {
			exists = true;
		} else {
			exists = false;
		}
		return exists;
	}
	private void selectStateTable(int tenantId) throws Exception
	{
		createConnection();
		try {
			stmt = conn.createStatement();
			System.out.println("Extracting information from state machine table...");
			String json = null;
			ResultSet results = stmt.executeQuery("select * from state_machines where id = " + tenantId);
			while(results.next()) {
				json = results.getString("json_string");
				machinespec = parsejson(json);
			}
			results.close();
			stmt.close();
			System.out.println("\t\t" + "Processing Id: " + tenantId + "\t\t" + "State Machine: "+ json);
			System.out.println("-------------------------------------------------------------------|");
		}
		catch (SQLException sqlExcept) {
			sqlExcept.printStackTrace();
		}
	}
	private void selectOrderTable(int tenantId) throws Exception
	{
		createConnection();
		try {
			stmt = conn.createStatement();
			System.out.println("Extracting information from order file table...");
			ResultSet results = stmt.executeQuery("select * from order_table where id = " + tenantId);
			String line = null;
			while(results.next()) {
				line = results.getString("orderLine");
				System.out.println(line);
				String[] split = line.split(",");
				Order order = new Order();
				order.setTimeMs(Long.valueOf(split[0].trim()));
				order.setOrderId(split[1].trim());
				order.setCustomerId(split[2].trim());
				order.setState(split[3].trim().toLowerCase());
				order.setDescription(split[4].trim());
				order.setQuantity(Integer.valueOf(split[5].trim()));
				order.setCost(Float.valueOf(split[6].trim()));
				updateOrder(order);
			}
			results.close();
			stmt.close();
			System.out.println("|---------------------------------------------------------------|");
		}
		catch (SQLException sqlExcept) {
			sqlExcept.printStackTrace();
		}
	} 
	public void runStateMode(String filename) throws Exception {
		String json = processStateFile(filename);
		System.out.println("Raw JSON = " + json);
		MachineSpec machineSpec = parsejson(json);
		createConnection();
		createStateMachineTable();
		int tenantId = machineSpec.getTenantId();
		insertStateMachine(tenantId, json);
	}
	private void runOrderMode(String orderFilePath) throws Exception{	
		createConnection();
		createOrderTable();
		int tenantId;
		String line;
		System.out.println("Processing Order File: " + orderFilePath + "\n");
		BufferedReader br = new BufferedReader(new FileReader(orderFilePath));  
		String orderLine; 
		while ((orderLine = br.readLine()) != null) {
			String[] splitLine = orderLine.trim().split(",");
			tenantId = Integer.parseInt(splitLine[0]);
			String [] split2 = orderLine.split(",",2);
			line = split2[1];
			insertOrderTable(tenantId, line);	
			System.out.println(line);
		} 
		br.close();
	}
	public void runReportMode(int tenantId) throws Exception	{
		selectStateTable(tenantId);
		selectOrderTable(tenantId);
		runReport();
		shutdown();

	}
	public static void main(String[] args) {
		SqlDemo theApp = new SqlDemo();
		String mode_flag = null;
		String filePath = null;
		String tenantIdString = null;
		if (args.length > 1) {
			mode_flag = args[0]; 
			filePath = args[1];
		}
		//if mode is state mode runStateMode method
		if (mode_flag.equals("--state")) {
			try { 
				theApp.runStateMode(filePath);
			}
			catch (Exception e) {
				System.out.println("Something bad happened!");
				e.printStackTrace();
			}
		}	
		//if mode is order mode runOrderMode method
		else if (mode_flag.equals("--order")) {
			try { 
				theApp.runOrderMode(filePath);
			}
			catch (Exception e) {
				System.out.println("Something bad happened!");
				e.printStackTrace();
			}
		}
		//if mode is report mode runReportMode method
		else if (mode_flag.equals("--report")) {
			tenantIdString = args[1];
			int ID;
			//if tenant id is valid then pass it through
			if (tenantIdString.matches("[0-9]{5}")) {
				ID = Integer.parseInt(tenantIdString);
				try { 
					theApp.runReportMode(ID);
				}
				catch (Exception e) {
					System.out.println("Something bad happened!");
					e.printStackTrace();
				}
			}
			else {
				System.out.println("Malformed Tenant ID");
			}
		}
		else {
			System.out.println("Invalid Flag Mode");
		}
	}//end main	
	private void runReport() {
		Map<String,Integer> countMap = new HashMap<String,Integer>();
		Integer countFlagged = 0; 
		Map<String,Float> valueMap = new HashMap<String,Float>();
		for (String key : orderInfoMap.keySet()) {
			Order o = orderInfoMap.get(key);
			if (!countMap.containsKey(o.getState())) {
				countMap.put(o.getState(), 0);
				valueMap.put(o.getState(), 0.0f);
			}
			if (o.isFlagged()) {
				countFlagged++; 
			}
			else {
				countMap.put(o.getState(), countMap.get(o.getState()) + 1);
				valueMap.put(o.getState(), valueMap.get(o.getState()) + o.getCost());
			}
		}

		for(String state: countMap.keySet()){
			Float cost = valueMap.get(state);
			if(cost == null){
				cost = 0.0f;
			}
			String terminal = "";
			if(MachineSpec.isTerminalState(machinespec, state)){
				terminal = "(terminal)";
			}
			System.out.println(state + " " + countMap.get(state) + " $" + cost + " " + terminal);
		}
		System.out.println("Number of Flagged Ids " + countFlagged);
	}
	private void updateOrder(Order newOrder) {
		// Have we seen this order before?  If not, put it in the cache as the baseline.
		boolean isNew = false; 
		if (!orderInfoMap.containsKey(newOrder.getOrderId())) {
			orderInfoMap.put(newOrder.getOrderId(), newOrder);
			isNew = true; 
		}

		Order oldOrder = orderInfoMap.get(newOrder.getOrderId());

		if ( 	(!newOrder.validateOrderFields()) ||  
				(newOrder.getTimeMs() < oldOrder.getTimeMs()) ||
				(!newOrder.getCustomerId().equals(oldOrder.getCustomerId())) || 
				(!MachineSpec.isValidTransition(machinespec, oldOrder.getState(), newOrder.getState(), isNew)) 
				) {

			//System.out.println("Flagging order " + newOrder.getOrderId());
			oldOrder.setFlagged(true);
		}

		newOrder.setFlagged(oldOrder.isFlagged());
		orderInfoMap.put(oldOrder.getOrderId(), newOrder);
	}
}//end program
