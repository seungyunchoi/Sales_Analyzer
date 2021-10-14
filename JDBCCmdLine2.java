import java.sql.*;
import java.util.*;

public class JDBCCmdLine2 {
   // JDBC driver name and database URL
   static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";  
   static final String DB_URL = "jdbc:mysql://localhost:3306/adventureworks?allowMultiQueries=true";

   //  Database credentials
   static final String USER = "root";
   static final String PASS = "password";
   
   public static String getResults(String[] args) {
	   Connection conn = null;
	   Statement stmt = null;
	   String resultstr = "";
	   try{
	      //STEP 2: Register JDBC driver
	      Class.forName("com.mysql.cj.jdbc.Driver");

	      //STEP 3: Open a connection
	      System.out.println("Connecting to database...");
	      conn = DriverManager.getConnection(DB_URL,USER,PASS);

	      //STEP 4: Execute a query
	      System.out.println("Creating statement...");
	      stmt = conn.createStatement();
	      String sql;
	      sql = "USE adventureworks;";
	      stmt.executeQuery(sql);
	      //while(true) {
		      //System.out.print("mysql> ");
		      //Scanner input = new Scanner(System.in);
		      String userIn = args[0];
		      ResultSet rs;
		      String[] parse = userIn.split(" ");
		      //System.out.println(parse[0]);
		      if(parse[0].equals("jdb-show-related-table")) {
		    	  try {
		    	  DatabaseMetaData DbMetaData = conn.getMetaData();
		    	  ResultSet targetKeys = DbMetaData.getPrimaryKeys(null, null, parse[1]);
		    	  List<String> tKeys = new ArrayList<String>();
		    	  while(targetKeys.next()) {
		    		  tKeys.add(targetKeys.getString("COLUMN_NAME"));
		    	  }
		    	  ResultSet tables = DbMetaData.getTables(null,null, "%",new String[] {"TABLE"});
		    	  while(tables.next()) {
		    		  String catalog = tables.getString("TABLE_CAT");
		    	      String schema = tables.getString("TABLE_SCHEM");
		    	      String tableName = tables.getString("TABLE_NAME");
		    	      if(!tableName.equals(parse[1])) {
			    		  ResultSet pKeys = DbMetaData.getPrimaryKeys(catalog,schema,tableName);
			    		  boolean added = false;
			    		  while(pKeys.next()) {
			    			  for(int i = 0; i < tKeys.size();i++) {
			    				  //System.out.print(tKeys.get(i) + ", ");
			    				  //System.out.println(pKeys.getString("COLUMN_NAME"));
			    				  if(tKeys.get(i).equals(pKeys.getString("COLUMN_NAME")) && !added) {
			    					  resultstr += tableName + "\n";
			    					  added = true;
			    					  break;
			    				  }
			    			  }
			    			  if(added)
			    				  break;
			    		  }
		    	      }
		    	  }
		    	  }catch(Exception e) {
		    		  System.out.println("Input Error");
		    	  }
		      }
		      else if(parse[0].equals("jdb-search-path")) {
		    	  try {
		    	  DatabaseMetaData DbMetaData = conn.getMetaData();
		    	  ResultSet t1KeySet = DbMetaData.getColumns(null,null,parse[1], null);
		    	  List<String> t1Keys = new ArrayList<String>();
		    	  while(t1KeySet.next()) {
		    		  if(!t1KeySet.getString("COLUMN_NAME").equals("rowguid") && !t1KeySet.getString("COLUMN_NAME").equals("ModifiedDate")) {
			    		  //System.out.println(t1KeySet.getString("COLUMN_NAME"));
			    		  t1Keys.add(t1KeySet.getString("COLUMN_NAME"));
		    		  }
		    	  }
		    	  ResultSet t2KeySet = DbMetaData.getColumns(null,null,parse[2], null);
		    	  List<String> t2Keys = new ArrayList<String>();
		    	  while(t2KeySet.next()) {
		    		  if(!t2KeySet.getString("COLUMN_NAME").equals("rowguid") && !t2KeySet.getString("COLUMN_NAME").equals("ModifiedDate")) {
			    		  //System.out.println(t2KeySet.getString("COLUMN_NAME"));
			    		  t2Keys.add(t2KeySet.getString("COLUMN_NAME"));
		    		  }
		    	  } 
		    	  boolean check = false;
		    	  for(int i = 0;i<t1Keys.size();i++) {
		    		  for(int j = 0;j < t2Keys.size();j++) {
		    			  if(t1Keys.get(i).equals(t2Keys.get(j))) {
		    				  resultstr += "No intermediate Tables";
		    				  check = true;
		    				  continue;
		    			  }
		    		  }
		    	  }
		    	  if(!check) {
		    		  resultstr += parse[1] + " -> ";
			    	  boolean matched = false;
			    	  ResultSet targetKeys = DbMetaData.getPrimaryKeys(null, null, parse[1]);
			    	  List<String> tKeys = new ArrayList<String>();
			    	  while(targetKeys.next()) {
			    		  tKeys.add(targetKeys.getString("COLUMN_NAME"));
			    	  }
			    	  ResultSet tables = DbMetaData.getTables(null,null, "%",new String[] {"TABLE"});
			    	  List<String> tTables = new ArrayList<String>();
			    	  while(tables.next()) {
			    		  String catalog = tables.getString("TABLE_CAT");
			    	      String schema = tables.getString("TABLE_SCHEM");
			    	      String tableName = tables.getString("TABLE_NAME");
			    	      if(!tableName.equals(parse[1])) {
				    		  ResultSet pKeys = DbMetaData.getPrimaryKeys(catalog,schema,tableName);
				    		  boolean added = false;
				    		  while(pKeys.next()) {
				    			  for(int i = 0; i < tKeys.size();i++) {
				    				  //System.out.print(tKeys.get(i) + ", ");
				    				  //System.out.println(pKeys.getString("COLUMN_NAME"));
				    				  if(tKeys.get(i).equals(pKeys.getString("COLUMN_NAME")) && !added) {
				    					  //System.out.println(tableName);
				    					  tTables.add(tableName);
				    					  added = true;
				    					  break;
				    				  }
				    			  }
				    			  if(added)
				    				  break;
				    		  }
			    	      }
			    	  }
			    	  label:
			    	  while(!matched) {
			    		  for(int i = 0; i < tTables.size();i++) {
				    		  ResultSet current = DbMetaData.getColumns(null,null,tTables.get(i), null);
					    	  List<String> curr = new ArrayList<String>();
					    	  while(current.next()) {
					    		  if(!current.getString("COLUMN_NAME").equals("rowguid") && !current.getString("COLUMN_NAME").equals("ModifiedDate")) {
						    		  //System.out.println(t1KeySet.getString("COLUMN_NAME"));
						    		  curr.add(current.getString("COLUMN_NAME"));
					    		  }
					    	  }
					    	  for(int j = 0; j < curr.size();j++) {
					    		  for(int q = 0; q < t2Keys.size();q++) {
					    			  if(curr.get(j).equals(t2Keys.get(q))) {
						    			  resultstr += tTables.get(i) + " -> ";
						    			  matched = true;
						    			  break label;
						    		  }
					    		  }
					    	  }
			    		  }
			    		  
			    	  }
			    	  resultstr += parse[2];
		    	  }
		    	  }catch(Exception e) {
		    		  System.out.println("Input Error");
		    	  }
		      }
		      else if(parse[0].equals("jdb-search-and-join")) {
		    	  try {
		    	  DatabaseMetaData DbMetaData = conn.getMetaData();
		    	  ResultSet t1KeySet = DbMetaData.getColumns(null,null,parse[1], null);
		    	  List<String> t1Keys = new ArrayList<String>();
		    	  while(t1KeySet.next()) {
		    		  if(!t1KeySet.getString("COLUMN_NAME").equals("rowguid") && !t1KeySet.getString("COLUMN_NAME").equals("ModifiedDate")) {
			    		  //System.out.println(t1KeySet.getString("COLUMN_NAME"));
			    		  t1Keys.add(t1KeySet.getString("COLUMN_NAME"));
		    		  }
		    	  }
		    	  ResultSet t2KeySet = DbMetaData.getColumns(null,null,parse[2], null);
		    	  List<String> t2Keys = new ArrayList<String>();
		    	  while(t2KeySet.next()) {
		    		  if(!t2KeySet.getString("COLUMN_NAME").equals("rowguid") && !t2KeySet.getString("COLUMN_NAME").equals("ModifiedDate")) {
			    		  //System.out.println(t2KeySet.getString("COLUMN_NAME"));
			    		  t2Keys.add(t2KeySet.getString("COLUMN_NAME"));
		    		  }
		    	  } 
		    	  boolean check = false;
		    	  for(int i = 0;i<t1Keys.size();i++) {
		    		  for(int j = 0;j < t2Keys.size();j++) {
		    			  if(t1Keys.get(i).equals(t2Keys.get(j))) {
		    				  resultstr += "No intermediate Tables";
		    				  check = true;
		    				  continue;
		    			  }
		    		  }
		    	  }
		    	  if(!check) {
			    	  boolean matched = false;
			    	  ResultSet targetKeys = DbMetaData.getPrimaryKeys(null, null, parse[1]);
			    	  List<String> tKeys = new ArrayList<String>();
			    	  while(targetKeys.next()) {
			    		  tKeys.add(targetKeys.getString("COLUMN_NAME"));
			    	  }
			    	  ResultSet tables = DbMetaData.getTables(null,null, "%",new String[] {"TABLE"});
			    	  List<String> tTables = new ArrayList<String>();
			    	  List<String> tableKeys = new ArrayList<String>();
			    	  while(tables.next()) {
			    		  String catalog = tables.getString("TABLE_CAT");
			    	      String schema = tables.getString("TABLE_SCHEM");
			    	      String tableName = tables.getString("TABLE_NAME");
			    	      if(!tableName.equals(parse[1])) {
				    		  ResultSet pKeys = DbMetaData.getPrimaryKeys(catalog,schema,tableName);
				    		  boolean added = false;
				    		  while(pKeys.next()) {
				    			  for(int i = 0; i < tKeys.size();i++) {
				    				  //System.out.print(tKeys.get(i) + ", ");
				    				  //System.out.println(pKeys.getString("COLUMN_NAME"));
				    				  if(tKeys.get(i).equals(pKeys.getString("COLUMN_NAME")) && !added) {
				    					  //System.out.println(tableName);
				    					  tTables.add(tableName);
				    					  tableKeys.add(tKeys.get(i));
				    					  added = true;
				    					  break;
				    				  }
				    			  }
				    			  if(added)
				    				  break;
				    		  }
			    	      }
			    	  }
			    	  label:
			    	  while(!matched) {
			    		  for(int i = 0; i < tTables.size();i++) {
				    		  ResultSet current = DbMetaData.getColumns(null,null,tTables.get(i), null);
					    	  List<String> curr = new ArrayList<String>();
					    	  while(current.next()) {
					    		  if(!current.getString("COLUMN_NAME").equals("rowguid") && !current.getString("COLUMN_NAME").equals("ModifiedDate")) {
						    		  //System.out.println(t1KeySet.getString("COLUMN_NAME"));
						    		  curr.add(current.getString("COLUMN_NAME"));
					    		  }
					    	  }
					    	  for(int j = 0; j < curr.size();j++) {
					    		  for(int q = 0; q < t2Keys.size();q++) {
					    			  if(curr.get(j).equals(t2Keys.get(q))) {
					    				  String items = "";
					    				  ResultSet item = DbMetaData.getColumns(null,null,parse[2],null);
					    				  for(int s = 0; s < curr.size();s++) {
					    					  if(curr.get(s).equals(curr.get(j)))
					    						  items+= tTables.get(i) + "." + curr.get(s) + ", ";
					    					  else
					    						  items+= curr.get(s) + ", ";
					    				  }
					    				  List<String> curr2 = new ArrayList<String>();
					    				  while(item.next()) {
					    					  if(!item.getString("COLUMN_NAME").equals("rowguid") && !item.getString("COLUMN_NAME").equals("ModifiedDate")) {
									    		  //System.out.println(t1KeySet.getString("COLUMN_NAME"));
									    		  curr2.add(item.getString("COLUMN_NAME"));
								    		  }
					    				  }
					    				  for(int s = 0; s < curr2.size();s++) {
					    					  if(curr2.get(s).equals(curr.get(j)))
					    						  continue;
					    					  else if(s <curr2.size()-1)
					    						  items+= curr2.get(s) + ", ";
					    					  else
					    						  items+= curr2.get(s);
					    				  }
					    				  sql = "select * from " + parse[1] + " join (select " + items + " from " + tTables.get(i) + " join " + parse[2] + " on " + parse[2] + "." + curr.get(j) + " = " + tTables.get(i) + "." + curr.get(j) 
						    			  	+ ") as A on " + parse[1] + "."  + tableKeys.get(i) + " = A." + tableKeys.get(i);
					    				  //System.out.println(sql);
						    			  stmt.executeQuery(sql);
						    			  matched = true;
						    			  break label;
						    		  }
					    		  }
					    	  }
			    		  }
			    		  
			    	  }
		    	  }
		    	  }catch(Exception e) {
		    		  System.out.println("Input Error");
		    	  }
		      }
		      else if(parse[0].equals("jdb-stat")) {
		    	  try {
		    	  userIn = "select max("+parse[2]+") as MxValue, min("+parse[2]+") as MinValue, avg("+parse[2]+") as AvgValue from "+parse[1]+";";
		    	  //System.out.println(userIn);
		    	  stmt = conn.prepareStatement(userIn,ResultSet.TYPE_SCROLL_SENSITIVE);
		    	  rs = stmt.executeQuery(userIn);
		    	  
		    	  double max = 0;
		    	  double min = 0;
		    	  ResultSetMetaData rsmd= rs.getMetaData();
			      int numCols = rsmd.getColumnCount();
			      for(int i = 1; i <= numCols;i++) {
			    	  if(i > 1)
			    		  resultstr += "|";
			    	  resultstr += String.format("%-20s",rsmd.getColumnName(i));
			      }
			      resultstr += "\n";
			      while(rs.next()) {
			    	  for(int i = 1; i <= numCols; i++) {
			    		  if(i > 1)
			    			  resultstr += "|";
			    		  resultstr += String.format("%-20s",rs.getString(i));
			    		  if(i==1)
			    			  max = Double.parseDouble(rs.getString(i));
			    		  else if(i==2)
			    			  min = Double.parseDouble(rs.getString(i));
			    	  }
			    	  resultstr += "\n";
			      }
		    	  
			      
			      
		    	  
		    	   
		    	   
		    	  userIn = "set @rowindex := -1;"; 
		    	  stmt.execute(userIn);
		    	  userIn = "select avg(x.y) as Median from (select @rowindex:=@rowindex + 1 as rowindex, "+parse[1]+"."+parse[2]+" as y from "+parse[1]+" order by "+parse[1]+"."+parse[2]+") as x where x.rowindex in (FLOOR(@rowindex / 2) , CEIL(@rowindex / 2));";
		    	  rs = stmt.executeQuery(userIn);
		    	  resultstr += printResultSet(rs);
		    	  int size = 6;
		    	  String bins[] = new String[size];
		    	  Arrays.fill(bins, "");
		    	  double interval = (max-min)/size;
		    	  userIn = "select "+parse[2]+" from "+parse[1]+";";
		    	  rs = stmt.executeQuery(userIn);
		    	  
		    	  ArrayList<Double> vals = new ArrayList<>();
		    	  int nrows = 0;
		    	  while(rs.next()) {
		    		  vals.add(Double.parseDouble(rs.getString(1)));
		    		  nrows++;
		    	  }
		    	  
		    	  for(double i: vals) {
		    		  int diff = (int) ((i - min)/interval); 
		    		  if(i==max) bins[size-1] += "*";
		    		  else bins[diff] += "*";
		    	  }
		    	  double range = min;
		    	  nrows *= 3;
		    	  nrows /= 5;
		    	  
		    	  for(int i = 0; i<nrows; i++) {
		    		  if(i == 0) resultstr += String.format("%20s","0");
		    		  else if(i%(nrows/4)==0) resultstr += i;
		    		  else resultstr += "_";
		    	  }
		    	  resultstr += "\n";
		    	  for(String s: bins) {
		    		  String a = String.format("%.2f-%.2f |",range,range+interval);
		    		  resultstr += String.format("%20s",a);
		    		  resultstr += s + "\n";
		    		  range += interval;
		    	  }
		    	  //continue;
		    	  }catch(Exception e) {
		    		  System.out.println("Input Error");
		    	  }
		      }
		      else if(parse[0].equals("jdb-customers-by-city")) {
		    	  try {
		    	  userIn = "select City, count(*) as N_Customers from address group by City;";
		    	  rs = stmt.executeQuery(userIn);
		    	  resultstr+=printResultSet(rs);
		    	  }catch(Exception e) {
		    		  System.out.println("Input Error");
		    	  }
		      }
		      else if (parse[0].equals("jdb-find-column")) {
		    	  try {
		    	  String columnName = userIn.substring(userIn.indexOf(' ')+1);
		    	  String newStmt = "SELECT COLUMN_NAME,TABLE_NAME\nFROM INFORMATION_SCHEMA.COLUMNS\n"
		    	  		+ "WHERE COLUMN_NAME LIKE '%" + columnName + "%';";
		    	  ResultSet rs2 = stmt.executeQuery(newStmt);
		    	  ResultSetMetaData rsmd2 = rs2.getMetaData();
		    	  int numCols2 = rsmd2.getColumnCount();
			      for(int i = 1; i <= numCols2;i++) {
			    	  if(i > 1)
			    		  resultstr += "|";
			    	  resultstr += String.format("%-20s",rsmd2.getColumnName(i));
			      }
			      resultstr += "\n";
			      while(rs2.next()) {
			    	  for(int i = 1; i <= numCols2; i++) {
			    		  if(i > 1)
			    			  resultstr += "|";
			    		  resultstr += String.format("%-20s",rs2.getString(i));
			    	  }
			    	  resultstr += "\n";
			      }
		    	  }catch(Exception e) {
		    		  System.out.println("Input Error");
		    	  }
		      }
		      else if (parse[0].equals("jdb-show-customer-sales")) {
		    	  try {
		    	  //ResultSet rs1 = stmt.executeQuery("select ProductID, sum(OrderQty) as AmountSold , sum(OrderQty)*unitPrice as TotalPrice from salesorderdetail group by ProductID order by ProductID;");
		    	  ResultSet rs1 = stmt.executeQuery("select ProductID, sum(OrderQty)*unitPrice as TotalSalesRevenue from salesorderdetail group by ProductID order by ProductID;");
		    	  ResultSetMetaData rsmd1 = rs1.getMetaData();
		    	  int numCols = rsmd1.getColumnCount();
			      for(int i = 1; i <= numCols;i++) {
			    	  if(i > 1)
			    		  resultstr += "|";
			    	  resultstr += String.format("%-20s",rsmd1.getColumnName(i));
			      }
			      resultstr += "\n";
			      while(rs1.next()) {
			    	  for(int i = 1; i <= numCols; i++) {
			    		  if(i > 1)
			    			  resultstr += "|";
			    		  resultstr += String.format("%-20s",rs1.getString(i));
			    	  }
			    	  resultstr += "\n";
			      }
		    	  }catch(Exception e) {
		    		  System.out.println("Input Error");
		    	  }
		      }
		      else if(parse[0].equals("jdb-show-top-spenders")) {
		    	  try {
		    	  if(parse.length > 1)
		    		  rs = stmt.executeQuery("select CustomerID,Truncate(sum(TotalDue),2) as AmountSpent from salesorderheader group by CustomerID order by AmountSpent desc limit " + Integer.parseInt(parse[1])+ ";");
		    	  else
		    		  rs = stmt.executeQuery("select CustomerID,Truncate(sum(TotalDue),2) as AmountSpent from salesorderheader group by CustomerID order by AmountSpent desc limit 10;"); 
		    	  ResultSetMetaData rsmd= rs.getMetaData();
			      int numCols = rsmd.getColumnCount();
			      for(int i = 1; i <= numCols;i++) {
			    	  if(i > 1)
			    		  resultstr += "|";
			    	  resultstr += String.format("%-20s",rsmd.getColumnName(i));
			      }
			      resultstr += "\n";
			      while(rs.next()) {
			    	  for(int i = 1; i <= numCols; i++) {
			    		  if(i > 1)
			    			  resultstr += "|";
			    		  resultstr += String.format("%-20s",rs.getString(i));
			    	  }
			    	  resultstr += "\n";
			      }
		    	  }catch(Exception e) {
		    		  System.out.println("Input Error");
		    	  }
		      }
		      else if (parse[0].equals("jdb-show-product-ratings")) {
		    	  try {
		    	  stmt.executeQuery("SET GLOBAL sql_mode=(SELECT REPLACE(@@sql_mode,'ONLY_FULL_GROUP_BY',''));");
		    	  ResultSet rs1 = stmt.executeQuery("select ProductID, Rating from productreview group by ProductID order by Rating;");
		    	  ResultSetMetaData rsmd1 = rs1.getMetaData();
		    	  int numCols = rsmd1.getColumnCount();
			      for(int i = 1; i <= numCols;i++) {
			    	  if(i > 1)
			    		  resultstr += "|";
			    	  resultstr += String.format("%-20s",rsmd1.getColumnName(i));
			      }
			      resultstr += "\n";
			      while(rs1.next()) {
			    	  for(int i = 1; i <= numCols; i++) {
			    		  if(i > 1)
			    			  resultstr += "|";
			    		  resultstr += String.format("%-20s",rs1.getString(i));
			    	  }
			    	  resultstr += "\n";
			      }
		    	  }catch(Exception e) {
		    		  System.out.println("Input Error");
		    	  }
		    	
		      }
		      else if(parse[0].equals("jdb-show-all-primary-keys")) {
			      try {
		    	  DatabaseMetaData metaData = conn.getMetaData();
			      try(ResultSet tables = metaData.getTables(null, null, "%", new String[] { "TABLE" })){
			    	  while (tables.next()) {
			    	        String tableName = tables.getString("TABLE_NAME");
			    	        String tableSchemaCatalog = tables.getString("TABLE_SCHEM");
			    	        String tableCatalog = tables.getString("TABLE_CAT");

			    	        
			    	        try (ResultSet primaryKey = metaData.getPrimaryKeys(tableCatalog, tableSchemaCatalog, tableName)) {
			    	            while (primaryKey.next()) {
			    	            	// prints (table_name, column_name)
			    	            	resultstr += ("(" + tableName + ", " + primaryKey.getString("COLUMN_NAME") + ")") + "\n";
			    	            }
			    	        }
			    	    }
			      }
			      }catch(Exception e) {
		    		  System.out.println("Input Error");
		    	  }
		      }
		      else if(parse[0].equals("jdb-get-view")) {
		    	  try {
		    	  String query = "";
		    	  for(int i = 2; i < parse.length; i++) {
		    		  resultstr += parse[i] + "\n";
		    		  query+= parse[i] + " ";
		    	  }
		    	  resultstr += query;
		    	  sql = "create view " + parse[1] + " as " + query;
		    	  stmt.executeUpdate(sql);
		    	  resultstr += ("The table " + parse[1] + " has been created.") + "\n";
		    	  }catch(Exception e) {
		    		  System.out.println("Input Error");
		    	  }
		      }
		      else if(parse[0].equals("jdb-get-products-per-city")){
		    	  try {
		    	  sql = "select CustomerID, City from customeraddress join address on customeraddress.AddressID = address.AddressID where city = \"" +
		    		      parse[2] +
		    		      "\" order by CustomerID";	  
		    	  resultstr += sql;
		    		      rs = stmt.executeQuery(sql);

		    		      ArrayList<Integer> city_list = new ArrayList<Integer>();
		    		      ArrayList<Integer> customer_list = new ArrayList<Integer>();

		    		      //STEP 5: Extract data from result set
		    		      while(rs.next()){
		    		         //Retrieve by column name
		    		         int customer = rs.getInt("CustomerID");
		    		         city_list.add(customer);
		    		      }

		    		      int size = city_list.size(); 

		    		      sql = "select CustomerID, ProductID from salesorderheader inner join salesorderdetail on salesorderheader.SalesOrderID = salesorderdetail.SalesOrderID where ProductID="+ 
		    		      parse[1]+
		    		      " group by CustomerID order by ProductID";
		    		      rs = stmt.executeQuery(sql);

		    		      while(rs.next()){
		    		         //Retrieve by column name
		    		         int customer = rs.getInt("CustomerID");
		    		         customer_list.add(customer);

		    		      }

		    		      //STEP 6: Clean-up environment
		    		      customer_list.retainAll(city_list);
		    		      int t = customer_list.size();
		    		      resultstr += String.format("There were %d units sold of product id %d in " ,t, Integer.parseInt(parse[1]));
		    		      resultstr += parse[2] + "\n";
		    	  }catch(Exception e) {
		    		  System.out.println("Input Error");
		    	  }
		      }	      
		      else {
			      try {
		    	  rs = stmt.executeQuery(userIn);
			      ResultSetMetaData rsmd= rs.getMetaData();
			      int numCols = rsmd.getColumnCount();
			      for(int i = 1; i <= numCols;i++) {
			    	  if(i > 1)
			    		  resultstr += "|";
			    	  resultstr += String.format("%-20s",rsmd.getColumnName(i));
			      }
			      resultstr += "\n";
			      while(rs.next()) {
			    	  for(int i = 1; i <= numCols; i++) {
			    		  if(i > 1)
			    			  resultstr += "|";
			    		  resultstr += String.format("%-20s",rs.getString(i));
			    	  }
			    	  resultstr += "\n";
			      }
			      }catch(Exception e) {
		    		  System.out.println("Input Error");
		    	  }
		      }
	      //}
	   }catch(SQLException se){
	      //Handle errors for JDBC
	      se.printStackTrace();
	   }catch(Exception e){
	      //Handle errors for Class.forName
	      e.printStackTrace();
	   }finally{
	      //finally block used to close resources
	      try{
	         if(stmt!=null)
	            stmt.close();
	      }catch(SQLException se2){
	      }// nothing we can do
	      try{
	         if(conn!=null)
	            conn.close();
	      }catch(SQLException se){
	         se.printStackTrace();
	      }//end finally try
	   }//end try
	   
	   return resultstr;
	   
   }
   
   
   public static void main(String[] args) {
	   
	   
   }
   /*
   public static void main(String[] args) {
   Connection conn = null;
   Statement stmt = null;
   try{
      //STEP 2: Register JDBC driver
      Class.forName("com.mysql.cj.jdbc.Driver");
      //STEP 3: Open a connection
      System.out.println("Connecting to database...");
      conn = DriverManager.getConnection(DB_URL,USER,PASS);
      //STEP 4: Execute a query
      System.out.println("Creating statement...");
      stmt = conn.createStatement();
      String sql;
      sql = "USE adventureworks;";
      stmt.executeQuery(sql);
      //while(true) {
	      System.out.print("mysql> ");
	      Scanner input = new Scanner(System.in);
	      String userIn = args[0];
	      ResultSet rs;
	      String[] parse = userIn.split(" ");
	      //System.out.println(parse[0]);
	      if(parse[0].equals("jdb-show-related-table")) {
	    	  DatabaseMetaData DbMetaData = conn.getMetaData();
	    	  ResultSet targetKeys = DbMetaData.getPrimaryKeys(null, null, parse[1]);
	    	  List<String> tKeys = new ArrayList<String>();
	    	  while(targetKeys.next()) {
	    		  tKeys.add(targetKeys.getString("COLUMN_NAME"));
	    	  }
	    	  ResultSet tables = DbMetaData.getTables(null,null, "%",new String[] {"TABLE"});
	    	  while(tables.next()) {
	    		  String catalog = tables.getString("TABLE_CAT");
	    	      String schema = tables.getString("TABLE_SCHEM");
	    	      String tableName = tables.getString("TABLE_NAME");
	    	      if(!tableName.equals(parse[1])) {
		    		  ResultSet pKeys = DbMetaData.getPrimaryKeys(catalog,schema,tableName);
		    		  boolean added = false;
		    		  while(pKeys.next()) {
		    			  for(int i = 0; i < tKeys.size();i++) {
		    				  //System.out.print(tKeys.get(i) + ", ");
		    				  //System.out.println(pKeys.getString("COLUMN_NAME"));
		    				  if(tKeys.get(i).equals(pKeys.getString("COLUMN_NAME")) && !added) {
		    					  System.out.println(tableName);
		    					  added = true;
		    					  break;
		    				  }
		    			  }
		    			  if(added)
		    				  break;
		    		  }
	    	      }
	    	  }
	      }
	      else if(parse[0].equals("jdb-search-path")) {
	    	  DatabaseMetaData DbMetaData = conn.getMetaData();
	    	  ResultSet t1KeySet = DbMetaData.getColumns(null,null,parse[1], null);
	    	  List<String> t1Keys = new ArrayList<String>();
	    	  while(t1KeySet.next()) {
	    		  if(!t1KeySet.getString("COLUMN_NAME").equals("rowguid") && !t1KeySet.getString("COLUMN_NAME").equals("ModifiedDate")) {
		    		  //System.out.println(t1KeySet.getString("COLUMN_NAME"));
		    		  t1Keys.add(t1KeySet.getString("COLUMN_NAME"));
	    		  }
	    	  }
	    	  ResultSet t2KeySet = DbMetaData.getColumns(null,null,parse[2], null);
	    	  List<String> t2Keys = new ArrayList<String>();
	    	  while(t2KeySet.next()) {
	    		  if(!t2KeySet.getString("COLUMN_NAME").equals("rowguid") && !t2KeySet.getString("COLUMN_NAME").equals("ModifiedDate")) {
		    		  //System.out.println(t2KeySet.getString("COLUMN_NAME"));
		    		  t2Keys.add(t2KeySet.getString("COLUMN_NAME"));
	    		  }
	    	  } 
	    	  boolean check = false;
	    	  for(int i = 0;i<t1Keys.size();i++) {
	    		  for(int j = 0;j < t2Keys.size();j++) {
	    			  if(t1Keys.get(i).equals(t2Keys.get(j))) {
	    				  System.out.println("No intermediate Tables");
	    				  check = true;
	    				  continue;
	    			  }
	    		  }
	    	  }
	    	  if(!check) {
	    		  System.out.print(parse[1] + " -> ");
		    	  boolean matched = false;
		    	  ResultSet targetKeys = DbMetaData.getPrimaryKeys(null, null, parse[1]);
		    	  List<String> tKeys = new ArrayList<String>();
		    	  while(targetKeys.next()) {
		    		  tKeys.add(targetKeys.getString("COLUMN_NAME"));
		    	  }
		    	  ResultSet tables = DbMetaData.getTables(null,null, "%",new String[] {"TABLE"});
		    	  List<String> tTables = new ArrayList<String>();
		    	  while(tables.next()) {
		    		  String catalog = tables.getString("TABLE_CAT");
		    	      String schema = tables.getString("TABLE_SCHEM");
		    	      String tableName = tables.getString("TABLE_NAME");
		    	      if(!tableName.equals(parse[1])) {
			    		  ResultSet pKeys = DbMetaData.getPrimaryKeys(catalog,schema,tableName);
			    		  boolean added = false;
			    		  while(pKeys.next()) {
			    			  for(int i = 0; i < tKeys.size();i++) {
			    				  //System.out.print(tKeys.get(i) + ", ");
			    				  //System.out.println(pKeys.getString("COLUMN_NAME"));
			    				  if(tKeys.get(i).equals(pKeys.getString("COLUMN_NAME")) && !added) {
			    					  //System.out.println(tableName);
			    					  tTables.add(tableName);
			    					  added = true;
			    					  break;
			    				  }
			    			  }
			    			  if(added)
			    				  break;
			    		  }
		    	      }
		    	  }
		    	  label:
		    	  while(!matched) {
		    		  for(int i = 0; i < tTables.size();i++) {
			    		  ResultSet current = DbMetaData.getColumns(null,null,tTables.get(i), null);
				    	  List<String> curr = new ArrayList<String>();
				    	  while(current.next()) {
				    		  if(!current.getString("COLUMN_NAME").equals("rowguid") && !current.getString("COLUMN_NAME").equals("ModifiedDate")) {
					    		  //System.out.println(t1KeySet.getString("COLUMN_NAME"));
					    		  curr.add(current.getString("COLUMN_NAME"));
				    		  }
				    	  }
				    	  for(int j = 0; j < curr.size();j++) {
				    		  for(int q = 0; q < t2Keys.size();q++) {
				    			  if(curr.get(j).equals(t2Keys.get(q))) {
					    			  System.out.print(tTables.get(i) + " -> ");
					    			  matched = true;
					    			  break label;
					    		  }
				    		  }
				    	  }
		    		  }
		    		  
		    	  }
		    	  System.out.println(parse[2]);
	    	  }
	      }
	      else if(parse[0].equals("jdb-search-and-join")) {
	    	  DatabaseMetaData DbMetaData = conn.getMetaData();
	    	  ResultSet t1KeySet = DbMetaData.getColumns(null,null,parse[1], null);
	    	  List<String> t1Keys = new ArrayList<String>();
	    	  while(t1KeySet.next()) {
	    		  if(!t1KeySet.getString("COLUMN_NAME").equals("rowguid") && !t1KeySet.getString("COLUMN_NAME").equals("ModifiedDate")) {
		    		  //System.out.println(t1KeySet.getString("COLUMN_NAME"));
		    		  t1Keys.add(t1KeySet.getString("COLUMN_NAME"));
	    		  }
	    	  }
	    	  ResultSet t2KeySet = DbMetaData.getColumns(null,null,parse[2], null);
	    	  List<String> t2Keys = new ArrayList<String>();
	    	  while(t2KeySet.next()) {
	    		  if(!t2KeySet.getString("COLUMN_NAME").equals("rowguid") && !t2KeySet.getString("COLUMN_NAME").equals("ModifiedDate")) {
		    		  //System.out.println(t2KeySet.getString("COLUMN_NAME"));
		    		  t2Keys.add(t2KeySet.getString("COLUMN_NAME"));
	    		  }
	    	  } 
	    	  boolean check = false;
	    	  for(int i = 0;i<t1Keys.size();i++) {
	    		  for(int j = 0;j < t2Keys.size();j++) {
	    			  if(t1Keys.get(i).equals(t2Keys.get(j))) {
	    				  System.out.println("No intermediate Tables");
	    				  check = true;
	    				  continue;
	    			  }
	    		  }
	    	  }
	    	  if(!check) {
		    	  boolean matched = false;
		    	  ResultSet targetKeys = DbMetaData.getPrimaryKeys(null, null, parse[1]);
		    	  List<String> tKeys = new ArrayList<String>();
		    	  while(targetKeys.next()) {
		    		  tKeys.add(targetKeys.getString("COLUMN_NAME"));
		    	  }
		    	  ResultSet tables = DbMetaData.getTables(null,null, "%",new String[] {"TABLE"});
		    	  List<String> tTables = new ArrayList<String>();
		    	  List<String> tableKeys = new ArrayList<String>();
		    	  while(tables.next()) {
		    		  String catalog = tables.getString("TABLE_CAT");
		    	      String schema = tables.getString("TABLE_SCHEM");
		    	      String tableName = tables.getString("TABLE_NAME");
		    	      if(!tableName.equals(parse[1])) {
			    		  ResultSet pKeys = DbMetaData.getPrimaryKeys(catalog,schema,tableName);
			    		  boolean added = false;
			    		  while(pKeys.next()) {
			    			  for(int i = 0; i < tKeys.size();i++) {
			    				  //System.out.print(tKeys.get(i) + ", ");
			    				  //System.out.println(pKeys.getString("COLUMN_NAME"));
			    				  if(tKeys.get(i).equals(pKeys.getString("COLUMN_NAME")) && !added) {
			    					  //System.out.println(tableName);
			    					  tTables.add(tableName);
			    					  tableKeys.add(tKeys.get(i));
			    					  added = true;
			    					  break;
			    				  }
			    			  }
			    			  if(added)
			    				  break;
			    		  }
		    	      }
		    	  }
		    	  label:
		    	  while(!matched) {
		    		  for(int i = 0; i < tTables.size();i++) {
			    		  ResultSet current = DbMetaData.getColumns(null,null,tTables.get(i), null);
				    	  List<String> curr = new ArrayList<String>();
				    	  while(current.next()) {
				    		  if(!current.getString("COLUMN_NAME").equals("rowguid") && !current.getString("COLUMN_NAME").equals("ModifiedDate")) {
					    		  //System.out.println(t1KeySet.getString("COLUMN_NAME"));
					    		  curr.add(current.getString("COLUMN_NAME"));
				    		  }
				    	  }
				    	  for(int j = 0; j < curr.size();j++) {
				    		  for(int q = 0; q < t2Keys.size();q++) {
				    			  if(curr.get(j).equals(t2Keys.get(q))) {
				    				  String items = "";
				    				  ResultSet item = DbMetaData.getColumns(null,null,parse[2],null);
				    				  for(int s = 0; s < curr.size();s++) {
				    					  if(curr.get(s).equals(curr.get(j)))
				    						  items+= tTables.get(i) + "." + curr.get(s) + ", ";
				    					  else
				    						  items+= curr.get(s) + ", ";
				    				  }
				    				  List<String> curr2 = new ArrayList<String>();
				    				  while(item.next()) {
				    					  if(!item.getString("COLUMN_NAME").equals("rowguid") && !item.getString("COLUMN_NAME").equals("ModifiedDate")) {
								    		  //System.out.println(t1KeySet.getString("COLUMN_NAME"));
								    		  curr2.add(item.getString("COLUMN_NAME"));
							    		  }
				    				  }
				    				  for(int s = 0; s < curr2.size();s++) {
				    					  if(curr2.get(s).equals(curr.get(j)))
				    						  continue;
				    					  else if(s <curr2.size()-1)
				    						  items+= curr2.get(s) + ", ";
				    					  else
				    						  items+= curr2.get(s);
				    				  }
				    				  sql = "select * from " + parse[1] + " join (select " + items + " from " + tTables.get(i) + " join " + parse[2] + " on " + parse[2] + "." + curr.get(j) + " = " + tTables.get(i) + "." + curr.get(j) 
					    			  	+ ") as A on " + parse[1] + "."  + tableKeys.get(i) + " = A." + tableKeys.get(i);
				    				  //System.out.println(sql);
					    			  stmt.executeQuery(sql);
					    			  matched = true;
					    			  break label;
					    		  }
				    		  }
				    	  }
		    		  }
		    		  
		    	  }
	    	  }
	      }
	      else if(parse[0].equals("jdb-stat")) {
	    	  userIn = "select max("+parse[2]+") as MxValue, min("+parse[2]+") as MinValue, avg("+parse[2]+") as AvgValue from "+parse[1]+";";
	    	  //System.out.println(userIn);
	    	  stmt = conn.prepareStatement(userIn,ResultSet.TYPE_SCROLL_SENSITIVE);
	    	  rs = stmt.executeQuery(userIn);
	    	  
	    	  double max = 0;
	    	  double min = 0;
	    	  ResultSetMetaData rsmd= rs.getMetaData();
		      int numCols = rsmd.getColumnCount();
		      for(int i = 1; i <= numCols;i++) {
		    	  if(i > 1)
		    		  System.out.print("|");
		    	  System.out.format("%-20s",rsmd.getColumnName(i));
		      }
		      System.out.println("");
		      while(rs.next()) {
		    	  for(int i = 1; i <= numCols; i++) {
		    		  if(i > 1)
		    			  System.out.print("|");
		    		  System.out.format("%-20s",rs.getString(i));
		    		  if(i==1)
		    			  max = Double.parseDouble(rs.getString(i));
		    		  else if(i==2)
		    			  min = Double.parseDouble(rs.getString(i));
		    	  }
		    	  System.out.println("");
		      }
	    	  
		      
		      
	    	  
	    	   
	    	   
	    	  userIn = "set @rowindex := -1;"; 
	    	  stmt.execute(userIn);
	    	  userIn = "select avg(x.y) as Median from (select @rowindex:=@rowindex + 1 as rowindex, "+parse[1]+"."+parse[2]+" as y from "+parse[1]+" order by "+parse[1]+"."+parse[2]+") as x where x.rowindex in (FLOOR(@rowindex / 2) , CEIL(@rowindex / 2));";
	    	  rs = stmt.executeQuery(userIn);
	    	  printResultSet(rs);
	    	  int size = 6;
	    	  String bins[] = new String[size];
	    	  Arrays.fill(bins, "");
	    	  double interval = (max-min)/size;
	    	  userIn = "select "+parse[2]+" from "+parse[1]+";";
	    	  rs = stmt.executeQuery(userIn);
	    	  
	    	  ArrayList<Double> vals = new ArrayList<>();
	    	  int nrows = 0;
	    	  while(rs.next()) {
	    		  vals.add(Double.parseDouble(rs.getString(1)));
	    		  nrows++;
	    	  }
	    	  
	    	  for(double i: vals) {
	    		  int diff = (int) ((i - min)/interval); 
	    		  if(i==max) bins[size-1] += "*";
	    		  else bins[diff] += "*";
	    	  }
	    	  double range = min;
	    	  nrows *= 3;
	    	  nrows /= 5;
	    	  
	    	  for(int i = 0; i<nrows; i++) {
	    		  if(i == 0) System.out.format("%20s","0");
	    		  else if(i%(nrows/4)==0) System.out.print(i);
	    		  else System.out.print("_");
	    	  }
	    	  System.out.println();
	    	  for(String s: bins) {
	    		  String a = String.format("%.2f-%.2f |",range,range+interval);
	    		  System.out.format("%20s",a);
	    		  System.out.println(s);
	    		  range += interval;
	    	  }
	    	  //continue;
	      }
	      else if(parse[0].equals("jdb-customers-by-city")) {
	    	  userIn = "select City, count(*) as N_Customers from address group by City;";
	    	  rs = stmt.executeQuery(userIn);
	    	  printResultSet(rs);
	      }
	      else if (parse[0].equals("jdb-find-column")) {
	    	  String columnName = userIn.substring(userIn.indexOf(' ')+1);
	    	  String newStmt = "SELECT COLUMN_NAME,TABLE_NAME\nFROM INFORMATION_SCHEMA.COLUMNS\n"
	    	  		+ "WHERE COLUMN_NAME LIKE '%" + columnName + "%';";
	    	  ResultSet rs2 = stmt.executeQuery(newStmt);
	    	  ResultSetMetaData rsmd2 = rs2.getMetaData();
	    	  int numCols2 = rsmd2.getColumnCount();
		      for(int i = 1; i <= numCols2;i++) {
		    	  if(i > 1)
		    		  System.out.print("|");
		    	  System.out.format("%-20s",rsmd2.getColumnName(i));
		      }
		      System.out.println("");
		      while(rs2.next()) {
		    	  for(int i = 1; i <= numCols2; i++) {
		    		  if(i > 1)
		    			  System.out.print("|");
		    		  System.out.format("%-20s",rs2.getString(i));
		    	  }
		    	  System.out.println("");
		      }
	      }
	      else if (parse[0].equals("jdb-show-customer-sales")) {
	    	  //ResultSet rs1 = stmt.executeQuery("select ProductID, sum(OrderQty) as AmountSold , sum(OrderQty)*unitPrice as TotalPrice from salesorderdetail group by ProductID order by ProductID;");
	    	  ResultSet rs1 = stmt.executeQuery("select ProductID, sum(OrderQty)*unitPrice as TotalSalesRevenue from salesorderdetail group by ProductID order by ProductID;");
	    	  ResultSetMetaData rsmd1 = rs1.getMetaData();
	    	  int numCols = rsmd1.getColumnCount();
		      for(int i = 1; i <= numCols;i++) {
		    	  if(i > 1)
		    		  System.out.print("|");
		    	  System.out.format("%-20s",rsmd1.getColumnName(i));
		      }
		      System.out.println("");
		      while(rs1.next()) {
		    	  for(int i = 1; i <= numCols; i++) {
		    		  if(i > 1)
		    			  System.out.print("|");
		    		  System.out.format("%-20s",rs1.getString(i));
		    	  }
		    	  System.out.println("");
		      }
	    	  
	      }
	      else if(parse[0].equals("jdb-show-top-spenders")) {
	    	  if(parse.length > 1)
	    		  rs = stmt.executeQuery("select CustomerID,Truncate(sum(TotalDue),2) as AmountSpent from salesorderheader group by CustomerID order by AmountSpent desc limit " + Integer.parseInt(parse[1])+ ";");
	    	  else
	    		  rs = stmt.executeQuery("select CustomerID,Truncate(sum(TotalDue),2) as AmountSpent from salesorderheader group by CustomerID order by AmountSpent desc limit 10;"); 
	    	  ResultSetMetaData rsmd= rs.getMetaData();
		      int numCols = rsmd.getColumnCount();
		      for(int i = 1; i <= numCols;i++) {
		    	  if(i > 1)
		    		  System.out.print("|");
		    	  System.out.format("%-20s",rsmd.getColumnName(i));
		      }
		      System.out.println("");
		      while(rs.next()) {
		    	  for(int i = 1; i <= numCols; i++) {
		    		  if(i > 1)
		    			  System.out.print("|");
		    		  System.out.format("%-20s",rs.getString(i));
		    	  }
		    	  System.out.println("");
		      }
	      }
	      else if (parse[0].equals("jdb-show-product-ratings")) {
	    	  stmt.executeQuery("SET GLOBAL sql_mode=(SELECT REPLACE(@@sql_mode,'ONLY_FULL_GROUP_BY',''));");
	    	  ResultSet rs1 = stmt.executeQuery("select ProductID, Rating from productreview group by ProductID order by Rating;");
	    	  ResultSetMetaData rsmd1 = rs1.getMetaData();
	    	  int numCols = rsmd1.getColumnCount();
		      for(int i = 1; i <= numCols;i++) {
		    	  if(i > 1)
		    		  System.out.print("|");
		    	  System.out.format("%-20s",rsmd1.getColumnName(i));
		      }
		      System.out.println("");
		      while(rs1.next()) {
		    	  for(int i = 1; i <= numCols; i++) {
		    		  if(i > 1)
		    			  System.out.print("|");
		    		  System.out.format("%-20s",rs1.getString(i));
		    	  }
		    	  System.out.println("");
		      }
	    	  
	    	
	      }
	      else if(parse[0].equals("jdb-show-all-primary-keys")) {
		      DatabaseMetaData metaData = conn.getMetaData();
		      try(ResultSet tables = metaData.getTables(null, null, "%", new String[] { "TABLE" })){
		    	  while (tables.next()) {
		    	        String tableName = tables.getString("TABLE_NAME");
		    	        String tableSchemaCatalog = tables.getString("TABLE_SCHEM");
		    	        String tableCatalog = tables.getString("TABLE_CAT");
		    	        
		    	        try (ResultSet primaryKey = metaData.getPrimaryKeys(tableCatalog, tableSchemaCatalog, tableName)) {
		    	            while (primaryKey.next()) {
		    	            	// prints (table_name, column_name)
		    	                System.out.println("(" + tableName + ", " + primaryKey.getString("COLUMN_NAME") + ")");
		    	            }
		    	        }
		    	    }
		      }
	    	  
	      }
	      else if(parse[0].equals("jdb-get-view")) {
	    	  String query = "";
	    	  for(int i = 2; i < parse.length; i++) {
	    		  System.out.println(parse[i]);
	    		  query+= parse[i] + " ";
	    	  }
	    	  System.out.println(query);
	    	  sql = "create view " + parse[1] + " as " + query;
	    	  stmt.executeUpdate(sql);
	    	  System.out.println("The table " + parse[1] + " has been created.");
	      }
	      else if(parse[0].equals("jdb-get-products-per-city")){
	    	  sql = "select CustomerID, City from customeraddress join address on customeraddress.AddressID = address.AddressID where city = \"" +
	    		      parse[2] +
	    		      "\" order by CustomerID";	  
	    	  System.out.println(sql);
	    		      rs = stmt.executeQuery(sql);
	    		      ArrayList<Integer> city_list = new ArrayList<Integer>();
	    		      ArrayList<Integer> customer_list = new ArrayList<Integer>();
	    		      //STEP 5: Extract data from result set
	    		      while(rs.next()){
	    		         //Retrieve by column name
	    		         int customer = rs.getInt("CustomerID");
	    		         city_list.add(customer);
	    		      }
	    		      int size = city_list.size(); 
	    		      sql = "select CustomerID, ProductID from salesorderheader inner join salesorderdetail on salesorderheader.SalesOrderID = salesorderdetail.SalesOrderID where ProductID="+ 
	    		      parse[1]+
	    		      " group by CustomerID order by ProductID";
	    		      rs = stmt.executeQuery(sql);
	    		      while(rs.next()){
	    		         //Retrieve by column name
	    		         int customer = rs.getInt("CustomerID");
	    		         customer_list.add(customer);
	    		      }
	    		      //STEP 6: Clean-up environment
	    		      customer_list.retainAll(city_list);
	    		      int t = customer_list.size();
	    		      System.out.format("There were %d units sold of product id %d in " ,t, Integer.parseInt(parse[1]));
	    		      System.out.println(parse[2]);
	      }	      
	      else {
		      rs = stmt.executeQuery(userIn);
		      ResultSetMetaData rsmd= rs.getMetaData();
		      int numCols = rsmd.getColumnCount();
		      for(int i = 1; i <= numCols;i++) {
		    	  if(i > 1)
		    		  System.out.print("|");
		    	  System.out.format("%-20s",rsmd.getColumnName(i));
		      }
		      System.out.println("");
		      while(rs.next()) {
		    	  for(int i = 1; i <= numCols; i++) {
		    		  if(i > 1)
		    			  System.out.print("|");
		    		  System.out.format("%-20s",rs.getString(i));
		    	  }
		    	  System.out.println("");
		      }
	      }
      //}
   }catch(SQLException se){
      //Handle errors for JDBC
      se.printStackTrace();
   }catch(Exception e){
      //Handle errors for Class.forName
      e.printStackTrace();
   }finally{
      //finally block used to close resources
      try{
         if(stmt!=null)
            stmt.close();
      }catch(SQLException se2){
      }// nothing we can do
      try{
         if(conn!=null)
            conn.close();
      }catch(SQLException se){
         se.printStackTrace();
      }//end finally try
   }//end try
   System.out.println("Goodbye!");
}//end main
   */
   static String printResultSet(ResultSet rs) throws SQLException {
	   String ret = "";
	   ResultSetMetaData rsmd= rs.getMetaData();
	      int numCols = rsmd.getColumnCount();
	      for(int i = 1; i <= numCols;i++) {
	    	  if(i > 1)
	    		  ret+="|";
	    	  ret+=String.format("%-20s",rsmd.getColumnName(i));
	      }
	      ret+="\n";
	      while(rs.next()) {
	    	  for(int i = 1; i <= numCols; i++) {
	    		  if(i > 1)
	    			  ret+="|";
	    		  ret+=String.format("%-20s",rs.getString(i));
	    	  }
	    	  ret+="\n";
	      }
	      return ret;
   }
   
}//end FirstExample