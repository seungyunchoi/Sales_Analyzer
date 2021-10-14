import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.*;
public class JDBCCmdLine {
   // JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	   static final String DB_URL = "jdbc:mysql://localhost:3306?allowMultiQueries=true";

	   //  Database credentials
	   static final String USER = "root";
	   static final String PASS = "password";
   
	   public static String getResults(String[] args) {
		   Connection conn = null;
		   Statement stmt = null;
		   String resultstr = "";
		   try{
		      //STEP 2: Register JDBC driver
		      Class.forName("com.mysql.jdbc.Driver");

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
			      else if(parse[0].equals("jdb-search-path")||parse[0].equals("jdb-search-and-join")) {
			    	  System.out.println(parse[1]);
		    		  System.out.println(parse[2]);
			    	  try {
			    		  
			    		  rs = stmt.executeQuery("show tables");
				    	  ArrayList<String> table_names = new ArrayList<String>();
				    	  ArrayList<SqlRelation> queue = new ArrayList<SqlRelation>(); //used for filling graph until target is found
				    	  Graph<SqlRelation> graph = new Graph<SqlRelation>();
				    	  while(rs.next()) {
				    		  table_names.add(rs.getString(1));
				    	  }
				    	  
				    	  SqlRelation current_vertex =new SqlRelation(parse[1],"none");
				    	  SqlRelation source = current_vertex;
				    	  SqlRelation target = new SqlRelation("","");
				    	  table_names.remove(current_vertex.tableName);
				    	  boolean found_dest = false;
				    	  queue.add(current_vertex);
				    	  
				    	  
				    	  //fills graph (using BFS) only with the necessary data, up until a relation with the 2nd table is found
				    	  while(!found_dest && !queue.isEmpty()) {
				    		  current_vertex = queue.remove(0);
				    		  ArrayList<String> current_cols = new ArrayList<String>();
				    		  rs = stmt.executeQuery("show columns from "+ current_vertex.tableName);
				    		 
				    		  //only stores keys that contain ID
				    		  while(rs.next()) {
				    			  if(rs.getString(1).contains("ID"))
				    				  current_cols.add(rs.getString(1));
				    		  }
				    		  
				    		  //search tables until a relation is found
				    		  for(int i = 0; i<table_names.size(); i++) {
				    			  rs = stmt.executeQuery("show columns from "+table_names.get(i));
				    			  while(rs.next()) {
				    				  boolean related = false;
				    				  
				    				  //compares all columns in table to the ID keys stored until it finds a match
				    				  for(int j = 0; j<current_cols.size(); j++) {
				    					  if(current_cols.get(j).equals(rs.getString(1))) {
				    						  SqlRelation col = new SqlRelation(table_names.get(i),current_cols.get(j));
				    						  graph.addEdge(current_vertex, col, false);
				    						  related = true;
				    						  queue.add(col);
				    						  if(table_names.get(i).equals(parse[2])) {
				    							  target = col;
				    							  found_dest = true;
				    						  }
				    						  break;
				    					  }
				    				  }
				    				  //table is no longer needed because it has been connected
				    				  //loop broken to execute a new query
				    				  if(related) {
				    					  table_names.remove(i--);
				    					  break;
				    				  }
				    			  }
				    			  //target has been found, stop looking
				    			  if(found_dest) break;
				    		  }
				    		  
				    	  }
				    	  
				    	  
				    	  //prints path between tables
				    	  if(found_dest) {
				    		  //prints table/key path: source --> ... --> target
				    		  List<SqlRelation> l = graph.path(source, target);
				    		  if(parse[0].equals("jdb-search-path")) {
				    			  resultstr += "Table Path:\n";
					    		  for(int i = l.size()-1; i>0; i--) {
					    			  resultstr += l.get(i).tableName + " --> ";
					    		  }
					    		  resultstr+=l.get(0).tableName + "\n";
					    		  
					    		  System.out.println("Key Path:");
					    		  for(int i = l.size()-1; i>0; i--) {
					    			  resultstr+=l.get(i).sharedKey + " --> ";
					    		  }
					    		  resultstr+=l.get(0).sharedKey;
				    		  }
				    		  
				    		  
				    		  //executes search-and-join 
				    		  else{
				    			  //initial statements
				    			  String select_part = "select ";
					    		  String from_part = "from " + l.get(l.size()-1).tableName + " ";
					    		  String join_part = "";
					    		  String order_part = "order by " + l.get(l.size()-1).tableName +"."+l.get(l.size()-2).sharedKey+" ASC;";
					    		  
					    		  //fills query with correct keys and table names
					    		  for(int i = l.size()-1; i>0;i--) {
					    			  if(l.get(i).sharedKey == "none") {
					    				  select_part+= l.get(i).tableName+".*, ";
					    			  }else {
					    				  select_part+= l.get(i).tableName+"."+l.get(i).sharedKey +", ";
					    				  join_part += "inner join "+ l.get(i).tableName + " on " + l.get(i+1).tableName + "." + l.get(i).sharedKey +" = " + l.get(i).tableName + "." + l.get(i).sharedKey+" ";
					    			  }
					    		  }
					    		  select_part += l.get(0).tableName+".* ";
					    		  join_part += "inner join "+ l.get(0).tableName +" on " + l.get(1).tableName+ "." + l.get(0).sharedKey + " = " + l.get(0).tableName + "." + l.get(0).sharedKey + " ";
					    		  System.out.println(select_part + from_part + join_part + order_part);
					    		  rs = stmt.executeQuery(select_part + from_part + join_part + order_part);
					    		  resultstr = printResultSet(rs);
				    		  }
				    		  
				    	  }
				    		  
				    	  else System.out.println("No relation between tables found");
			    	  }catch(Exception e) {
			    		  System.out.println("Input Error222");
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
			      else if(parse[0].equals("jdb-customers-by-city-chart")) {
			    	  try {
			    	  userIn = "select City, count(*) as N_Customers from address group by City;";
			    	  rs = stmt.executeQuery(userIn);
			    	  ResultSetMetaData rsmd= rs.getMetaData();
				      int numCols = rsmd.getColumnCount();
				      /*for(int i = 1; i <= numCols;i++) {
				    	  resultstr+=String.format("%-20s",rsmd.getColumnName(i));
				      }*/
				      while(rs.next()) {
				    	  for(int i = 1; i <= numCols; i++) {
				    		  resultstr+=rs.getString(i) + ",";
				    	  }
				      }
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



			      else if (parse[0].equals("jdb-product-inventory")) {
			    	  try {
				      ResultSet rs4;
				      String sql4;
				      stmt = conn.createStatement();
				      sql4 = "select ProductID, Quantity from productinventory";
				      rs4 = stmt.executeQuery(sql4);

				      // instantiate list for proudct ids and quantities for each
				      ArrayList<Integer> product_ids = new ArrayList<Integer>();
				      ArrayList<Integer> quantities = new ArrayList<Integer>();

				      //STEP 5: Extract data from result set

				      int j = 0;
				      while(rs4.next()){

				      	// get product ids and quantities
				         int id = rs4.getInt("ProductID");
				         int quantity = rs4.getInt("Quantity");

				         if (j == 0){
				         	// add to list if first iteration
				            product_ids.add(id);
				            quantities.add(quantity);
				            j = 1;

				         }
				         if (id == product_ids.get(product_ids.size() - 1) && product_ids.size() != 1){
				         	// add size if we are on the same product id
				            quantity = quantities.get(quantities.size() - 1) + quantity;
				            int sz = (quantities.size() - 1);
				            quantities.set(sz, quantity);
				         }
				         else{
				         	// if new product id then add new to list
				            product_ids.add(id);
				            quantities.add(quantity);
				            
				         }
				      }

				      // remove first element which is repeated
				      product_ids.remove(0);
				      quantities.remove(0);
				      float total = 0;
				      float temp;

				      // print ids and vendor percentage
				      for (int i = 0; i < product_ids.size(); i++){      
				         //resultstr += String.format("Product ID: %d Total Amount in Inventory: %d " ,product_ids.get(i), quantities.get(i));
				         //resultstr += "\n";
				    	  resultstr+= product_ids.get(i) + " ";
				    	  resultstr+=quantities.get(i) + " ";
				    	  
				      }

			    	  }catch(Exception e) {
			    		  System.out.println("Input Error");
			    	  }
			      }
			      //for making whiskers and box plots for every year of the stats
			      else if(parse[0].equals("jdb-box-plot")) {
			    	  rs = stmt.executeQuery("select StartDate, StandardCost from productcosthistory;");
			    	  while(rs.next()) {
		    			  for(int i = 1; i <= 2; i++) {
		    	    		  
		    			      resultstr+=rs.getString(i)+" ";
		    			  }
		    		  }
			      }

			      else if (parse[0].equals("jdb-vendor-percentage")) {
			    	  try {
				      ResultSet rs3;
				      String sql3;
				      stmt = conn.createStatement();
				      sql3 = "select VendorID, StandardPrice from productvendor order by VendorID";
				      rs3 = stmt.executeQuery(sql3);

				      // instantiate list of vendor ids and prices
				      ArrayList<Integer> vendor_ids = new ArrayList<Integer>();
				      ArrayList<Float> prices = new ArrayList<Float>();

				      int j = 0;
				      while(rs3.next()){

				      	// get ids and prices
				         int id = rs3.getInt("VendorID");
				         float price = rs3.getFloat("StandardPrice");

				         // add ids and prices
				         if (j == 0){
				            vendor_ids.add(id);
				            prices.add(price);
				            j = 1;

				         }
				         // if get same id then add price to that id
				         if (id == vendor_ids.get(vendor_ids.size() - 1) && vendor_ids.size() != 1){
				            price = prices.get(prices.size() - 1) + price;
				            int sz = (prices.size() - 1);
				            prices.set(sz, price);
				         }
				         else{
				         	// if new id then add new id
				            vendor_ids.add(id);
				            prices.add(price);
				            
				         }
				      }

				      // remove first value as repeated
				      vendor_ids.remove(0);
				      prices.remove(0);
				      float total = 0;
				      float temp;

				      // get total
				      for (int i = 0; i < vendor_ids.size(); i++){
				         temp = prices.get(i);
				         total = temp + total;

				      }

				      // create percentages
				      for (int i = 0; i < vendor_ids.size(); i++){
				         temp = prices.get(i);
				         prices.set(i, temp/total);

				      }

				      // print everything
				      for (int i = 0; i < vendor_ids.size(); i++){      
				         //resultstr += String.format("Vendor ID: %d Percentage of total inventory cost spent with vendor: %f " ,vendor_ids.get(i), prices.get(i));
				         //resultstr += "\n";
				    	  resultstr += String.format("VendorID:%d",vendor_ids.get(i)) + " ";
				    	  resultstr += prices.get(i) + " ";
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
			      else if (parse[0].equals("jdb-find-year")) {
			    	  try {
				    	  String year = userIn.substring(userIn.indexOf(' ')+1);
				    	  String newStmt = "SELECT OrderDate FROM salesorderheader";
				    	  ResultSet rs2 = stmt.executeQuery(newStmt);
				    	  ResultSetMetaData rsmd1 = rs2.getMetaData();
				    	  int numCols2 = rsmd1.getColumnCount();
				    	  Vector<String> jan_vect = new Vector<String>();
				    	  Vector<String> feb_vect = new Vector<String>();
				    	  Vector<String> mar_vect = new Vector<String>();
				    	  Vector<String> apr_vect = new Vector<String>();
				    	  Vector<String> may_vect = new Vector<String>();
				    	  Vector<String> jun_vect = new Vector<String>();
				    	  Vector<String> july_vect = new Vector<String>();
				    	  Vector<String> aug_vect = new Vector<String>();
				    	  Vector<String> sept_vect = new Vector<String>();
				    	  Vector<String> oct_vect = new Vector<String>();
				    	  Vector<String> nov_vect = new Vector<String>();
				    	  Vector<String> dec_vect = new Vector<String>();
				    	  
					      while(rs2.next()) {
					    	  for(int i = 1; i <= numCols2; i++) {
					    		  if (rs2.getString(i).substring(0,4).equals(year)) {
					    			  switch(rs2.getString(i).substring(5,7)) {
						    			  case "01":
						    				  jan_vect.add(rs2.getString(i));
						    			      break;
						    			  case "02":
						    				  feb_vect.add(rs2.getString(i));
						    				  break;
						    			  case "03":
						    				  mar_vect.add(rs2.getString(i));
						    				  break;
						    			  case "04":
						    				  apr_vect.add(rs2.getString(i));
						    				  break;
						    			  case "05":
						    				  may_vect.add(rs2.getString(i));
						    				  break;
						    			  case "06":
						    				  jun_vect.add(rs2.getString(i));
						    				  break;
						    			  case "07":
						    				  july_vect.add(rs2.getString(i));
						    				  break;
						    			  case "08":
						    				  aug_vect.add(rs2.getString(i));
						    				  break;
						    			  case "09":
						    				  sept_vect.add(rs2.getString(i));
						    				  break;
						    			  case "10":
						    				  oct_vect.add(rs2.getString(i));
						    				  break;
						    			  case "11":
						    				  nov_vect.add(rs2.getString(i));
						    				  break;
						    			  case "12":
						    				  dec_vect.add(rs2.getString(i));
						    				  break;
					    			  }  
					    		  }
				    		  }
					      }
					      resultstr += "January ";
			    		  resultstr += jan_vect.size()+ " ";
			    		  resultstr += "February ";
			    		  resultstr += feb_vect.size()+ " ";
			    		  resultstr += "March ";
			    		  resultstr += mar_vect.size()+ " ";
			    		  resultstr += "April ";
			    		  resultstr += apr_vect.size()+ " ";
			    		  resultstr += "May ";
			    		  resultstr += may_vect.size()+ " ";
			    		  resultstr += "June ";
			    		  resultstr += jun_vect.size()+ " ";
			    		  resultstr += "July ";
			    		  resultstr += july_vect.size()+ " ";
			    		  resultstr += "August ";
			    		  resultstr += aug_vect.size()+ " ";
			    		  resultstr += "September ";
			    		  resultstr += sept_vect.size()+ " ";
			    		  resultstr += "October ";
			    		  resultstr += oct_vect.size()+ " ";
			    		  resultstr += "November ";
			    		  resultstr += nov_vect.size() + " ";
			    		  resultstr += "December ";
			    		  resultstr += dec_vect.size();
			    	  }
			    	  catch(Exception e) {
			    		  System.out.println("Input Error");
			    	  }
			      }
			      //for making whiskers and box plots for every year of the stats
			      else if(parse[0].equals("jdb-box-plot")) {
			    	  rs = stmt.executeQuery("select * from productcosthistory;");
			    	  while(rs.next()) {
		    			  for(int i = 1; i <= 4; i++) {
		    	    		  
		    			      resultstr+=rs.getString(i)+" ";
		    			  }
		    		  }
			      }
			      
			      //shows the standard price and the list price for every item
			      else if(parse[0].equals("jdb-profit-margin")){
			    	  try {
			    		  rs = stmt.executeQuery("select productcosthistory.ProductID,productcosthistory.StartDate,productcosthistory.StandardCost, productlistpricehistory.ListPrice from productcosthistory inner join productlistpricehistory on productcosthistory.ProductID=productlistpricehistory.ProductID and productcosthistory.StartDate=productlistpricehistory.StartDate;");
			    		  while(rs.next()) {
			    			  for(int i = 1; i <= 4; i++) {
			    	    		  
			    			      resultstr+=rs.getString(i)+" ";
			    			  }
			    		  }
			    	  }catch(Exception e) {
			    		  System.out.println("Input Error");
			    	  }
			      }
			      
			      else if (parse[0].equals("jdb-show-product-review-percentages")) {
			    	  try {
				    	  String newStmt = "SELECT rating FROM productreview";
				    	  ResultSet rs2 = stmt.executeQuery(newStmt);
				    	  ResultSetMetaData rsmd1 = rs2.getMetaData();
				    	  int numCols2 = rsmd1.getColumnCount();
				    	  double total_ratings = 0.0;
				    	  Vector<String> r1_vect = new Vector<String>();
				    	  Vector<String> r2_vect = new Vector<String>();
				    	  Vector<String> r3_vect = new Vector<String>();
				    	  Vector<String> r4_vect = new Vector<String>();
				    	  Vector<String> r5_vect = new Vector<String>();
				    	  
					      while(rs2.next()) {
					    	  for (int i = 1; i <= numCols2; i++) {
						    	  total_ratings += 1;
					    		  switch(rs2.getString(i)) {
					    			  case "1":
					    				  r1_vect.add(rs2.getString(i));
					    			      break;
					    			  case "2":
					    				  r2_vect.add(rs2.getString(i));
					    				  break;
					    			  case "3":
					    				  r3_vect.add(rs2.getString(i));
					    				  break;
					    			  case "4":
					    				  r4_vect.add(rs2.getString(i));
					    				  break;
					    			  case "5":
					    				  r5_vect.add(rs2.getString(i));
					    				  break;
					    		  }	 
					    	  }
					      }
					      resultstr += "Rating 1: ";
					      resultstr += r1_vect.size()/total_ratings;
					      resultstr += "\nRating 2: ";
					      resultstr += r2_vect.size()/total_ratings;
					      resultstr += "\nRating 3: ";
					      resultstr += r3_vect.size()/total_ratings;
					      resultstr += "\nRating 4: ";
					      resultstr += r4_vect.size()/total_ratings;
					      resultstr += "\nRating 5: ";
					      resultstr += r5_vect.size()/total_ratings;
					      resultstr += "\n";
			    	  }
				    	  catch(Exception e) {
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
		   } 
	   
	   catch(SQLException se){
	      //Handle errors for JDBC
	      //se.printStackTrace();
		   System.out.println("Bad input. Please try again.");
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
	   resultstr += "\n";
	   
	   return resultstr;
	   
   }
   
   
public static void main(String[] args) {
	String[] a = {"jdb-customers-by-city-chart"};
    System.out.println(getResults(a));
}

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
   static void creatDot(String[] parse, ResultSet rs, Statement stmt) throws SQLException {
	   try {
	    	  File myObj = new File("schm.txt");
	    	  if (myObj.createNewFile()) 
	    	        System.out.println("File created: " + myObj.getName());
	    	       else 
	    	        System.out.println("File already exists.");
	    	  Path path = Paths.get("schm.txt");
	    	  System.out.println(path.toAbsolutePath());
	    	  FileWriter myWriter = new FileWriter("schm.txt");
	    	  myWriter.write("digraph T {\n edge [dir=\"none\", fontsize=\"8\"];\n");
	    	  rs = stmt.executeQuery("show tables");
	    	  ArrayList<String> table_names = new ArrayList<String>();
	    	  Graph<SqlRelation> graph = new Graph<SqlRelation>();
	    	  while(rs.next()) {
	    		  table_names.add(rs.getString(1));
	    		  myWriter.write(" "+rs.getString(1)+ " [label="+rs.getString(1)+"];\n");
	    	  }
	    	  
	    	  //fill graph
	    	  for(int k = 0; k<table_names.size(); k++) {
	    		  SqlRelation current_vertex =new SqlRelation(table_names.get(k),"none");
	    		  table_names.remove(k--);
	    		  ArrayList<String> current_cols = new ArrayList<String>();
	    		  rs = stmt.executeQuery("show columns from "+ current_vertex.tableName);
	    		 
	    		  //only stores keys that contain ID
	    		  while(rs.next()) {
	    			  if(rs.getString(1).contains("ID"))
	    				  current_cols.add(rs.getString(1));
	    		  }
	    		  
	    		  //search tables until a relation is found
	    		  for(int i = 0; i<table_names.size(); i++) {
	    			  rs = stmt.executeQuery("show columns from "+table_names.get(i));
	    			  while(rs.next()) {
	    				  boolean related = false;
	    				  
	    				  //compares all columns in table to the ID keys stored until it finds a match
	    				  for(int j = 0; j<current_cols.size(); j++) {
	    					  if(current_cols.get(j).equals(rs.getString(1))) {
	    						  SqlRelation col = new SqlRelation(table_names.get(i),current_cols.get(j));
	    						  graph.addEdge(current_vertex, col, false);
	    						  related = true;
	    						
	    						  break;
	    					  }
	    				  }
	    				  
	    				  //loop broken to execute a new query
	    				  if(related) {
	    					  
	    					  break;
	    				  }
	    			  }
	    			  
	    		  }  
	    	  }
		      
	    	  for(SqlRelation v: graph.map.keySet()) {
	    		  if(v.sharedKey == "none") {
	    			  System.out.print(v.tableName + ": ");
	    			  for(SqlRelation s: graph.map.get(v)) {
	    				  myWriter.write("  "+ v.tableName + " -> "+s.tableName+" [label="+s.sharedKey+"];\n");
	    			  }
	    			  System.out.println();
	    		  }
	    	  }
	    	  myWriter.write("}");
	    	  myWriter.close();
	    	  
	      } catch(IOException e) {
	    	  e.printStackTrace();
	      }
   }
}//end FirstExample
