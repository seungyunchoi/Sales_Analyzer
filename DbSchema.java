// From https://www.tutorialspoint.com/jdbc/jdbc-sample-code.htm
//STEP 1. Import required packages

// Choe: This should work on your MySQL instance running on docker. Just
// change the default username and password below.

import java.sql.*;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Collection;

public class DbSchema {
   
   public static void main(String[] args) {
      try{

         String command = "dot -Tpdf -o schem.pdf schm.dot";

         Process proc = Runtime.getRuntime().exec(command);

         BufferedReader reader =  new BufferedReader(new InputStreamReader(proc.getInputStream()));

         String line = "";
         while((line = reader.readLine()) != null) {
               System.out.print(line + "\n");
         }

         proc.waitFor();   

      }catch(Exception e){
         e.printStackTrace();
      }
   }
}