import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;

import org.knowm.xchart.BoxChart;
import org.knowm.xchart.BoxChartBuilder;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.demo.charts.pie.PieChart03;
import org.knowm.xchart.style.BoxStyler.BoxplotCalCulationMethod;
import org.knowm.xchart.style.PieStyler;
import org.knowm.xchart.style.Styler.LegendPosition;


public class GUI extends JFrame
{
       JPanel panel = new JPanel();
       JTextArea textArea = new JTextArea(10,45);
       JScrollPane display = new JScrollPane(textArea);
       
       //raw sql ( runs sql query entered)
       JTextField textfield_sql = new JTextField(30); // add text field for user input
       JButton button_sql = new JButton("SQL"); // add button with name of command
       
       // jdb show related tables
       JTextField textfield_rel = new JTextField(30);
       JButton button_rel= new JButton("Show Related Tables");
       
       //jdb search path
       JTextField textfield_path = new JTextField(30);
       JButton button_path = new JButton("Search Path");
       
       //jdb search and join
       JTextField textfield_join = new JTextField(30);
       JButton button_join = new JButton("Search and Join");
       
       // jdb stat
       JTextField textfield_stat = new JTextField(30);
       JButton button_stat = new JButton("STAT");
       
       // jdb show primary keys
       JButton button_keys = new JButton("Show Primary Keys");
       
       //jdb customers by city
       JButton button_customer_city = new JButton("Customers by City");
       
       //jdb find column
       JTextField textfield_col = new JTextField(30);
       JButton button_col = new JButton("Find Column");
       
       //jdb show customer sales

       JButton button_sales = new JButton("Show Customer Sales");
       
       //jdb show top spenders (has optional parameter for how many you want to see)
       JTextField textfield_spend = new JTextField(30);
       JButton button_spend = new JButton("Show Top Spenders");
       
       //jdb show product ratings
       JButton button_rate = new JButton("Show Product Ratings");
       
       //jdb get view
       JTextField textfield_view = new JTextField(30);
       JButton button_view = new JButton("Get View");
       
       //jdb get products per city
       JTextField textfield_product_city = new JTextField(30);
       JButton button_product_city = new JButton("Products Per City");
       
       JButton button_schem = new JButton("Show Schema");
       String input = "";
       
       //jdb find year
       JTextField textfield_year = new JTextField(30);
       JButton button_year= new JButton("Find Monthly Sales For A Year");
       
       //jdb show product review percentages
       JButton button_review = new JButton("Show Product Review Percentages");

       JButton vendor_percentage = new JButton("Show Vendor Percentages");

       JButton product_inventory = new JButton("Show Inventory per Product");

       JButton show_dash = new JButton("Display Dashboard");
       public GUI()
       {
              setTitle("Entry");
              setVisible(true);
              setSize(600, 600); // size of window
              textArea.setEditable(false);
              display.setVerticalScrollBarPolicy ( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS ); // add scroll bar to display
              
              
              setDefaultCloseOperation(EXIT_ON_CLOSE);
              
              // COMMAND 1: raw sql
              panel.add(textfield_sql); // add text field
              
              // Can press enter on keyboard
              textfield_sql.addActionListener(new ActionListener()
              {
                     public void actionPerformed(ActionEvent e)
                     {
                    	 String args [] = new String[1];
                           input = textfield_sql.getText();
                           args[0] = input; // sql query input from user
                           textArea.setText(JDBCCmdLine.getResults(args)); // display returned string from JDBCCmdLine.getResults Function onto the Window
                           textfield_sql.setText(""); // empty the text field after user has entered parameters
                     }
              });

              // Can press button
              panel.add(button_sql); // add button
              button_sql.addActionListener(new ActionListener()
              {
                      public void actionPerformed(ActionEvent e)
                      {
                    	  	String args [] = new String[1];
                            input = textfield_sql.getText();
                            args[0] = input; // sql query input from user
                            //textfield_sql.setText(input);
                            textArea.setText(JDBCCmdLine.getResults(args)); // display returned string from JDBCCmdLine.getResults Function onto the Window
                            textfield_sql.setText(""); // empty the text field after user has entered parameters
                      }
              });
              
              
              
           // jdb-find-year
              panel.add(textfield_year);
           // Can press enter on keyboard
              textfield_year.addActionListener(new ActionListener()
              {
                     public void actionPerformed(ActionEvent e)
                     {
                    	 String args [] = new String[1];
                           input = textfield_year.getText(); // get parameter input from user
                           args[0] = "jdb-find-year " + input; // append parameters and command name
                           textArea.setText(JDBCCmdLine.getResults(args)); // display returned string from JDBCCmdLine.getResults Function onto the Window
                           textfield_year.setText(""); // empty the text field after user has entered parameters
                     }
              });

              // Can press button
              panel.add(button_year); // add button
              button_year.addActionListener(new ActionListener()
              {
                      public void actionPerformed(ActionEvent e)
                      {
                    	  	String args [] = new String[1];
                            input = textfield_year.getText();
                            args[0] = "jdb-find-year " + input;
                            textArea.setText(JDBCCmdLine.getResults(args));
                            textfield_year.setText(""); // empty the text field after user has entered parameters
                      }
              });
              
              
              // COMMAND 2: jdb show related tables
              panel.add(textfield_rel);
           // Can press enter on keyboard
              textfield_rel.addActionListener(new ActionListener()
              {
                     public void actionPerformed(ActionEvent e)
                     {
                    	 String args [] = new String[1];
                           input = textfield_rel.getText(); // get parameter input from user
                           args[0] = "jdb-show-related-table " + input; // append parameters and command name
                           textArea.setText(JDBCCmdLine.getResults(args)); // display returned string from JDBCCmdLine.getResults Function onto the Window
                           textfield_rel.setText(""); // empty the text field after user has entered parameters
                     }
              });

              // Can press button
              panel.add(button_rel); // add button
              button_rel.addActionListener(new ActionListener()
              {
                      public void actionPerformed(ActionEvent e)
                      {
                    	  	String args [] = new String[1];
                            input = textfield_rel.getText();
                            args[0] = "jdb-show-related-table " + input;
                            textArea.setText(JDBCCmdLine.getResults(args));
                            textfield_rel.setText(""); // empty the text field after user has entered parameters
                      }
              });
              
              
              
              //COMMAND 3: jdb search path
              panel.add(textfield_path);
           // Can press enter on keyboard
              textfield_path.addActionListener(new ActionListener()
              {
                     public void actionPerformed(ActionEvent e)
                     {
                    	 String args [] = new String[1];
                           input = textfield_path.getText();
                           args[0] = "jdb-search-path " + input;
                           textArea.setText(JDBCCmdLine.getResults(args));
                           textfield_path.setText("");
                     }
              });

              // Can press button
              panel.add(button_path); // add button
              button_path.addActionListener(new ActionListener()
              {
                      public void actionPerformed(ActionEvent e)
                      {
                    	  	String args [] = new String[1];
                            input = textfield_path.getText();
                            args[0] = "jdb-search-path " + input;
                            textArea.setText(JDBCCmdLine.getResults(args));
                            textfield_path.setText("");
                      }
              });
              
              
              
             //COMMAND 4: jdb search and join
              panel.add(textfield_join);
           // Can press enter on keyboard
              textfield_join.addActionListener(new ActionListener()
              {
                     public void actionPerformed(ActionEvent e)
                     {
                    	 String args [] = new String[1];
                           input = textfield_join.getText();
                           args[0] = "jdb-search-and-join " + input;
                           textArea.setText(JDBCCmdLine.getResults(args));
                           textfield_join.setText("");
                     }
              });

              // Can press button
              panel.add(button_join); // add button
              button_join.addActionListener(new ActionListener()
              {
                      public void actionPerformed(ActionEvent e)
                      {
                    	  	String args [] = new String[1];
                            input = textfield_join.getText();
                            args[0] = "jdb-search-and-join " + input;
                            textArea.setText(JDBCCmdLine.getResults(args));
                            textfield_join.setText("");
                      }
              });
              
              
              
             //COMMAND 5: jdb-stat
              panel.add(textfield_stat);
           // Can press enter on keyboard
              textfield_stat.addActionListener(new ActionListener()
              {
                     public void actionPerformed(ActionEvent e)
                     {
                    	 String args [] = new String[1];
                           input = textfield_stat.getText();
                           args[0] = "jdb-stat " + input;
                           textArea.setText(JDBCCmdLine.getResults(args));
                           textfield_stat.setText("");
                     }
              });

              // Can press button
              panel.add(button_stat); // add button
              button_stat.addActionListener(new ActionListener()
              {
                      public void actionPerformed(ActionEvent e)
                      {
                    	  	String args [] = new String[1];
                            input = textfield_stat.getText();
                            args[0] = "jdb-stat " + input;
                            textArea.setText(JDBCCmdLine.getResults(args));
                            textfield_stat.setText("");
                      }
              });
              
              
            //COMMAND 8: jdb find column
              panel.add(textfield_col);
           // Can press enter on keyboard
              textfield_col.addActionListener(new ActionListener()
              {
                     public void actionPerformed(ActionEvent e)
                     {
                    	 String args [] = new String[1];
                           input = textfield_col.getText();
                           args[0] = "jdb-find-column " + input;
                           textArea.setText(JDBCCmdLine.getResults(args));
                           textfield_col.setText("");
                     }
              });

              // Can press button
              panel.add(button_col); // add button
              button_col.addActionListener(new ActionListener()
              {
                      public void actionPerformed(ActionEvent e)
                      {
                    	  	String args [] = new String[1];
                            input = textfield_col.getText();
                            args[0] = "jdb-find-column " + input;
                            textArea.setText(JDBCCmdLine.getResults(args));
                            textfield_col.setText("");
                      }
              });
              
              
              
             
            //COMMAND 10: jdb-show-top-spenders
              panel.add(textfield_spend);
           // Can press enter on keyboard
              textfield_spend.addActionListener(new ActionListener()
              {
                     public void actionPerformed(ActionEvent e)
                     {
                    	 String args [] = new String[1];
                           input = textfield_spend.getText();
                           args[0] = "jdb-show-top-spenders " + input;
                           textArea.setText(JDBCCmdLine.getResults(args));
                           textfield_spend.setText("");
                     }
              });

              // Can press button
              panel.add(button_spend); // add button
              button_spend.addActionListener(new ActionListener()
              {
                      public void actionPerformed(ActionEvent e)
                      {
                    	  	String args [] = new String[1];
                            input = textfield_spend.getText();
                            args[0] = "jdb-show-top-spenders " + input;
                            textArea.setText(JDBCCmdLine.getResults(args));
                            textfield_spend.setText("");
                      }
              });
              
             

            //COMMAND 11: jdb get view
              panel.add(textfield_view);
           // Can press enter on keyboard
              textfield_view.addActionListener(new ActionListener()
              {
                     public void actionPerformed(ActionEvent e)
                     {
                    	 String args [] = new String[1];
                           input = textfield_view.getText();
                           args[0] = "jdb-get-view " + input;
                           textArea.setText(JDBCCmdLine.getResults(args));
                           textfield_view.setText("");
                     }
              });

              // Can press button
              panel.add(button_view); // add button
              button_view.addActionListener(new ActionListener()
              {
                      public void actionPerformed(ActionEvent e)
                      {
                    	  	String args [] = new String[1];
                            input = textfield_view.getText();
                            args[0] = "jdb-get-view " + input;
                            textArea.setText(JDBCCmdLine.getResults(args));
                            textfield_view.setText("");
                      }
              });
              
              
              
            //COMMAND 11: jdb get products per city
              panel.add(textfield_product_city);
           // Can press enter on keyboard
              textfield_product_city.addActionListener(new ActionListener()
              {
                     public void actionPerformed(ActionEvent e)
                     {
                    	 String args [] = new String[1];
                           input = textfield_product_city.getText();
                           args[0] = "jdb-get-products-per-city " + input;
                           textArea.setText(JDBCCmdLine.getResults(args));
                           textfield_product_city.setText("");
                     }
              });

              // Can press button
              panel.add(button_product_city); // add button
              button_product_city.addActionListener(new ActionListener()
              {
                      public void actionPerformed(ActionEvent e)
                      {
                    	  	String args [] = new String[1];
                            input = textfield_product_city.getText();
                            args[0] = "jdb-get-products-per-city " + input;
                            textArea.setText(JDBCCmdLine.getResults(args));
                            textfield_product_city.setText("");
                      }
              });

              /*
              panel.add(vendor_percentage); // add button
              vendor_percentage.addActionListener(new ActionListener()
              {
                      public void actionPerformed(ActionEvent e)
                      {
                          String args [] = new String[1];
                            args[0] = "jdb-vendor-percentage";
                            textArea.setText(JDBCCmdLine.getResults(args));
                      }
              });
               */
              /*
              panel.add(product_inventory); // add button
              product_inventory.addActionListener(new ActionListener()
              {
                      public void actionPerformed(ActionEvent e)
                      {
                          String args [] = new String[1];
                            args[0] = "jdb-product-inventory";
                            textArea.setText(JDBCCmdLine.getResults(args));
                      }
              });
              */
              
              
              //ALL COMMANDS WITH NO PARAMETERS
              
              //COMMAND 6: jdb show all primary keys

              // Can press button
              panel.add(button_keys); // add button
              button_keys.addActionListener(new ActionListener()
              {
                      public void actionPerformed(ActionEvent e)
                      {
                    	  	String args [] = new String[1];
                            args[0] = "jdb-show-all-primary-keys";
                            textArea.setText(JDBCCmdLine.getResults(args));
                      }
              });
              
              
              
              //COMMAND 9: jdb show customer sales

              // Can press button
              panel.add(button_sales); // add button
              button_sales.addActionListener(new ActionListener()
              {
                      public void actionPerformed(ActionEvent e)
                      {
                    	  	String args [] = new String[1];
                            args[0] = "jdb-show-customer-sales";
                            textArea.setText(JDBCCmdLine.getResults(args));
                      }
              });
              
              //COMMAND 7: jdb  customers by city
              // Can press button 
              panel.add(button_customer_city); // add button
              button_customer_city.addActionListener(new ActionListener()
              {
                      public void actionPerformed(ActionEvent e)
                      {
                    	  	String args [] = new String[1];
                            args[0] = "jdb-customers-by-city " + input;
                            textArea.setText(JDBCCmdLine.getResults(args));
                      }
              });
            
              
            
              
              //COMMAND 11: jdb show product ratings

              // Can press button
              panel.add(button_rate); // add button
              button_rate.addActionListener(new ActionListener()
              {
                      public void actionPerformed(ActionEvent e)
                      {
                    	  	String args [] = new String[1];
                            args[0] = "jdb-show-product-ratings";
                            textArea.setText(JDBCCmdLine.getResults(args));
                      }
              });
              
              
              panel.add(button_schem);
              button_schem.addActionListener(new ActionListener()
              {
            	  public void actionPerformed(ActionEvent e) 
            	  {
//            		  DBSchema.main(null);
//            		  textArea.setText("Check Schem.pdf");
            	  }
              });

              /*
              //jdb show product review percentages
              panel.add(button_review); // add button
              button_review.addActionListener(new ActionListener()
              {
                      public void actionPerformed(ActionEvent e)
                      {
                    	  	String args [] = new String[1];
                            args[0] = "jdb-show-product-review-percentages";
                            textArea.setText(JDBCCmdLine.getResults(args));
                      }
              });
              */
              
              panel.add(show_dash);
              show_dash.addActionListener(new ActionListener()
              {
            	  public void actionPerformed(ActionEvent e)
            	  {
            		  createFrame();
            	  }
              });
              
              panel.add(display); // add scroll bar to panel
              add(panel); // add panel to the window
	          setResizable(false);

       }
       
       public static XYChart findYearChart() {
    	   String args [] = new String[1];
    	   args[0] = "jdb-find-year 2001";
    	   String vals [] = JDBCCmdLine.getResults(args).split("\\s");
    	   
    	   double x1[] = {1,2,3,4,5,6,7,8,9,10,11,12};
    	   double x2[] = {1,2,3,4,5,6};
    	   double y1[] = new double[vals.length/2];
    	   double y2[] = new double[vals.length/2];
    	   double y3[] = new double[vals.length/2];
    	   double y4[] = new double[x2.length];
    	   
    	   for(int i = 0; i < vals.length/2;i++) {
    		   y1[i] = Double.parseDouble(vals[2*i+1]);
    	   }
    	   
    	   args[0] = "jdb-find-year 2002";
    	   vals = JDBCCmdLine.getResults(args).split("\\s");
    	   for(int i = 0; i < vals.length/2;i++) {
    		   y2[i] = Double.parseDouble(vals[2*i+1]);
    	   }
    	   
    	   args[0] = "jdb-find-year 2003";
    	   vals = JDBCCmdLine.getResults(args).split("\\s");
    	   for(int i = 0; i < vals.length/2;i++) {
    		   y3[i] = Double.parseDouble(vals[2*i+1]);
    	   }
    	   
    	   args[0] = "jdb-find-year 2004";
    	   vals = JDBCCmdLine.getResults(args).split("\\s");
    	   for(int i = 0; i < y4.length;i++) {
    		   y4[i] = Double.parseDouble(vals[2*i+1]);
    	   }
    	   
    	   XYChart chart = new XYChartBuilder().width(600).height(400).title("Total Sales").xAxisTitle("Month").yAxisTitle("Sales").build();
    	   chart.addSeries("2001", x1, y1);
    	   chart.addSeries("2002", x1, y2);
    	   chart.addSeries("2003", x1, y3);
    	   chart.addSeries("2004", x2, y4);
    	   //new SwingWrapper(chart).displayChart();
    	   return chart;
       }
       
       public static CategoryChart productInventoryChart() {
    	   String args [] = new String[1];
    	   args[0] = "jdb-product-inventory";
    	   String vals [] = JDBCCmdLine.getResults(args).split("\\s");
    	   
    	   //int size = vals.length/2;
    	   CategoryChart chart = new CategoryChart(800,600);
    	   double x[] = new double[vals.length/2];
    	   double y[] = new double[vals.length/2];
    	   for(int i = 0; i < vals.length; i++) {
    		   if(i%2==0){
    			   x[i/2] = Double.parseDouble(vals[i]);
    		   }
    		   else {
    			   y[i/2] = Double.parseDouble(vals[i]);
    		   }
    	   }
    	   chart.addSeries("Product Inventory", x, y);
    	   //new SwingWrapper(chart).displayChart();
    	   return chart;
       }
       
       
       public static PieChart vendorPercentagesChart() {  	   
    	   String args [] = new String[1];
    	   args[0] = "jdb-vendor-percentage";
    	   String vals [] = JDBCCmdLine.getResults(args).split("\\s");
    	   
    	   
    	   int size = vals.length/2; 
    	   Color[] sliceColors = new Color[size];
    	   for(int i = 0; i < size; i++) {
    		   sliceColors[i] = new Color(225-2*i,0+2*i,(int) Math.random() * (255 + 1));
    		   
    	   }
    	   
    	   //PieChart03 chart = new PieChart03();
    	   PieChart chart = new PieChartBuilder().width(800).height(600).title("Vendor Percentages").build();
    	   chart.getStyler().setSeriesColors(sliceColors);
    	   chart.getStyler().setAnnotationType(PieStyler.AnnotationType.LabelAndPercentage);
    	   chart.getStyler().setLegendVisible(false);
    	   //chart.getStyler().setDrawAllAnnotations(true);
    	   /*BasicStroke[] s = new BasicStroke[size];
    	   
    	   for(int i = 0; i < size; i++) {
    		   s[i] = new BasicStroke((float) .5);
    	   }
    	   chart.getStyler().setSeriesLines(s);*/
    	   chart.getStyler().setAnnotationDistance(1.05);
    	   //chart.getStyler().setDrawAllAnnotations(true);
    	   String[] sliceNames = new String[size];
    	   for(int i = 0; i<vals.length; i+=2) {
    		   //System.out.println(vals[i]);}
    		   chart.addSeries(vals[i], Double.parseDouble(vals[i+1]));
    		   sliceNames[i/2] = vals[i]; 
    	   }
    	   
    		   
    	   //new SwingWrapper(chart).displayChart();
    	   return chart;
       }
       
       public static CategoryChart customerByCityChart() {
    	   String args [] = new String[1];
    	   args[0] = "jdb-customers-by-city-chart";
    	   String vals [] = JDBCCmdLine.getResults(args).split(",");
    	   //System.out.println(vals.length);
    	   //System.out.println(JDBCCmdLine.getResults(args));
    	   CategoryChart chart = new CategoryChart(900,600);
    	   chart.setTitle("Customers By City");
    	   //String x [] = new String[vals.length/2];
    	   ArrayList x = new ArrayList();
    	   //System.out.println(vals.length);
    	   //System.out.println(x.length);
    	   ArrayList y = new ArrayList<Integer>();
    	   
    	   for(int i = 1; i < (vals.length)-1;i+=2) {
    		   if(Integer.parseInt(vals[i])> 195) {
    			   x.add(vals[i-1]);    			   
    		   	   y.add(Integer.parseInt(vals[i]));
    		   }
    	   }
    	   //System.out.println(x.size() + " " + y.size());
    	   chart.addSeries("Customers By City", x, y);
    	   chart.getStyler().setLegendVisible(false);
    	   chart.setXAxisTitle("City");
    	   chart.setYAxisTitle("Customers");
    	   //new SwingWrapper<CategoryChart>(chart).displayChart();
    	   return chart;
       }
       
       public static XYChart profitMarginChart() {
    	   String args [] = new String[1];
    	   args[0] = "jdb-profit-margin";
    	   String vals [] = JDBCCmdLine.getResults(args).split("\\s");
    	   
    	   //number of products per year 
    	   int size1 = 0;
    	   int size2 = 0;
    	   int size3 = 0;
    	   for(int i = 0; i<vals.length; i+=5) {
    		   if(vals[i+1].contains("2001")) {
    			   size1++;
    		   }else if(vals[i+1].contains("2002")) {
    			   size2++;
    		   }else if(vals[i+1].contains("2003")) {
    			   size3++;
    		   }
    	   }
    	   
    	   
    	   double[] x1 = new double[size1];
    	   double[] x2 = new double[size2];
    	   double[] x3 = new double[size3];
    	   double[] y1 = new double[size1];
    	   double[] y2 = new double[size2];
    	   double[] y3 = new double[size3];
    	   int count1 = 0;
    	   int count2 = 0;
    	   int count3 = 0;
    	   
    	   //stores product ids in x, listed price - standard cost in y; separated in years
    	   for(int i = 0; i<vals.length; i+=5) {
    		   try {
    		   if(vals[i+1].contains("2001")) {
    			   x1[count1] = Double.parseDouble(vals[i]);
    			   y1[count1++] = Double.parseDouble(vals[i+4]) - Double.parseDouble(vals[i+3]); 
    		   }else if(vals[i+1].contains("2002")) {
    			   x2[count2] = Double.parseDouble(vals[i]);
    			   y2[count2++] = Double.parseDouble(vals[i+4]) - Double.parseDouble(vals[i+3]); 
    		   }else if(vals[i+1].contains("2003")) {
    			   x3[count3] = Double.parseDouble(vals[i]);
    			   y3[count3++] = Double.parseDouble(vals[i+4]) - Double.parseDouble(vals[i+3]); 
    		   }
    		   
    		   }catch(NumberFormatException e) {
    			   System.out.println(count1);
    			   System.out.println(count2);
    			   System.out.println(count3);
    		   }
    	   }
    	   System.out.println(x2[x2.length-4]);
    	   final XYChart chart = new XYChartBuilder().width(600).height(400).title("Profit Margin of Products").xAxisTitle("ProductID").yAxisTitle("Profit").build();
    	   
    	   //creates a series for each year
    	   chart.addSeries("2001", x1, y1);
    	   chart.addSeries("2002", x2, y2);
    	   chart.addSeries("2003", x3, y3);
    	   //this just displays the single graph in a jframe
    	   //new SwingWrapper(chart).displayChart();
    	   return chart;
       }
       
       public static BoxChart costBoxChart() {
    	   String args [] = new String[1];
    	   args[0] = "jdb-box-plot";
    	   String vals[] = JDBCCmdLine.getResults(args).split("\\s");
    	   System.out.println(vals[3]);
    	   
    	   List<Double> y1 = new ArrayList<Double>();
    	   List<Double> y2 = new ArrayList<Double>();
    	   List<Double> y3 = new ArrayList<Double>();
    	   
    	   
    	   //stores product ids in x, listed price - standard cost in y; separated in years
    	   for(int i = 0; i<vals.length; i+=3) {
    		   try {
    		   if(vals[i].contains("2001")) {
    			   
    			   y1.add(Double.parseDouble(vals[i+2])); 
    		   }else if(vals[i].contains("2002")) {
    			   
    			   y2.add(Double.parseDouble(vals[i+2])); 
    		   }else if(vals[i].contains("2003")) {
    			   
    			   y3.add(Double.parseDouble(vals[i+2]));  
    		   }
    		   
    		   }catch(IllegalArgumentException e) {
    			   System.out.println("ASDFAD");
    			   System.out.println(y2);
    		   }
    	   }
    	   BoxChart chart =
    				new BoxChartBuilder().title("Range of Prices Per Year").build();

    			// Choose a calculation method
    			chart.getStyler().setBoxplotCalCulationMethod(BoxplotCalCulationMethod.N_LESS_1_PLUS_1);
    			chart.getStyler().setToolTipsEnabled(true);

    			// Series
    			chart.addSeries("2001", y1);
    			chart.addSeries("2002", y2);
    			chart.addSeries("2003", y3);
    			//new SwingWrapper<BoxChart>(chart).displayChart();
    			return chart;
       }
       
       public static void createFrame()
       {
           EventQueue.invokeLater(new Runnable()
           {
               @Override
               public void run()
               {                   
                   JPanel panel = new XChartPanel(customerByCityChart());
                   panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                   JPanel panel1 = new XChartPanel(findYearChart());
                   panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                   JPanel panel2 = new XChartPanel(costBoxChart());
                   panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                   JPanel panel3 = new XChartPanel(productInventoryChart());
                   panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                   JPanel panel4 = new XChartPanel(profitMarginChart());
                   panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                   JPanel panel5 = new XChartPanel(vendorPercentagesChart());
                   panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

                   JFrame frame = new MultiPanel(panel,panel1,panel2,panel4,panel5,panel3);
                   frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                   
                   Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                   frame.setSize(screenSize.width, screenSize.height);
                   frame.setVisible(true);
               }
           });
       }
       
       
       public static void main(String[] args)
       {
    	   //customerByCityChart();
    	   //findYearChart();
    	   //costBoxChart();
    	   //productInventoryChart();
    	   //profitMarginChart();
    	   //vendorPercentagesChart();
           GUI t = new GUI(); // create GUI object 
       }
}

