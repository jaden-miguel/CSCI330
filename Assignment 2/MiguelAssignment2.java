/* 
This is a Java skeleton code to help you out with how to start this assignment.
Please remember that this is NOT a compilable/runnable java file.
Please feel free to use this skeleton code.
Please look closely at the "To Do" parts of this file. You may get an idea of how to finish this assignment. 
*/

import java.util.*;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.PrintWriter;

// Name: Jaden Ji Miguel
// Date: Summer 2022
// Purpose: Assignment2 (skeleton code + toDo algorithms)

class Assign2Skeleton {

   static class StockData {
      // To Do:
      // Create this class which should contain the information (date, open price,
      // high price, low price, close price) for a specific ticker
      double openPrice, highPrice, lowPrice, closePrice;
      String date;

      public StockData(String date, double openPrice, double highPrice, double lowPrice, double closePrice) {
         this.date = date;
         this.openPrice = openPrice;
         this.highPrice = highPrice;
         this.lowPrice = lowPrice;
         this.closePrice = closePrice;
      }

      public StockData getClone() {
         return new StockData(date, openPrice, highPrice, lowPrice, closePrice);
      }

   }

   static Connection conn;
   static final String prompt = "Enter ticker symbol [start/end dates]: ";

   public static void main(String[] args) throws Exception {
      // String paramsFile = "ConnectionParameters_LabComputer.txt";
      String paramsFile = "ConnectionParameters_RemoteComputer.txt";

      if (args.length >= 1) {
         paramsFile = args[0];
      }

      Properties connectprops = new Properties();
      connectprops.load(new FileInputStream(paramsFile));
      try {
         Class.forName("com.mysql.cj.jdbc.Driver");
         String dburl = connectprops.getProperty("dburl");
         String username = connectprops.getProperty("user");
         conn = DriverManager.getConnection(dburl, connectprops);
         System.out.println("Database connection is established");

         Scanner in = new Scanner(System.in);
         System.out.print(prompt);
         String input = in.nextLine().trim();

         while (input.length() > 0) {
            String[] params = input.split("\\s+");
            String ticker = params[0];
            String startdate = null, enddate = null;
            if (getName(ticker)) {
               if (params.length >= 3) {
                  startdate = params[1];
                  enddate = params[2];
               }
               Deque<StockData> data = getStockData(ticker, startdate, enddate);
               System.out.println();
               System.out.println("Executing investment strategy");
               doStrategy(ticker, data);
            } else {
               System.out.printf("%s not found in database\n", ticker);
            }

            System.out.println();
            System.out.print(prompt);
            input = in.nextLine().trim();
         }

         // Close the database connection
         conn.close();
         System.out.println("Database connection is closed");

      } catch (SQLException ex) {
         System.out.printf("SQLException: %s%nSQLState: %s%nVendorError: %s%n",
               ex.getMessage(), ex.getSQLState(), ex.getErrorCode());
      }
   }

   static boolean getName(String ticker) throws SQLException {
      // To Do:
      // Execute the first query and print the company name of the ticker user
      // provided (e.g., INTC to Intel Corp.)
      // Please don't forget to use a prepared statement
      PreparedStatement pstmt = conn.prepareStatement(
            "select Name from company where Ticker = ?");
      pstmt.setString(1, ticker);
      ResultSet results = pstmt.executeQuery();
      if (results.next()) {
         System.out.printf("%s%n", results.getString("Name"));
         return true;
      }
      return false;
   }

   //Start deque functionality as mentioned in outline
   static Deque<StockData> getStockData(String ticker, String start, String end) throws SQLException {
      // To Do:
      // Execute the second query, which will return stock information of the ticker
      // (descending on the transaction date)
      // Please don't forget to use a prepared statement

      Deque<StockData> result = new ArrayDeque<>();
      List<StockData> data = new ArrayList<>();

      // To Do:
      // Loop through all the dates of that company (descending order)
      // Find a split if there is any (2:1, 3:1, 3:2) and adjust the split accordingly
      // Include the adjusted data to the result (which is a Deque); You can use
      // addFirst method for that purpose
      PreparedStatement pstmt;
      if (start == null & end == null) {
         pstmt = conn
               .prepareStatement(
                     "select TransDate, OpenPrice, HighPrice, LowPrice, ClosePrice from pricevolume where Ticker = ? order by TransDate DESC");
         pstmt.setString(1, ticker);
      } else if (start == null) {
         pstmt = conn
               .prepareStatement(
                     "select TransDate, OpenPrice, HighPrice, LowPrice, ClosePrice from pricevolume where Ticker = ? and TransDate <= ? order by TransDate DESC");
         pstmt.setString(1, ticker);
         pstmt.setString(2, end);
      } else if (end == null) {
         pstmt = conn
               .prepareStatement(
                     "select TransDate, OpenPrice, HighPrice, LowPrice, ClosePrice from pricevolume where Ticker = ? and TransDate >= ? order by TransDate DESC");
         pstmt.setString(1, ticker);
         pstmt.setString(2, start);
      } else {
         pstmt = conn
               .prepareStatement(
                     "select TransDate, OpenPrice, HighPrice, LowPrice, ClosePrice from pricevolume where Ticker = ? and (TransDate >= ? and TransDate <= ?) order by TransDate DESC");
         pstmt.setString(1, ticker);
         pstmt.setString(2, start);
         pstmt.setString(3, end);
      }
      ResultSet results = pstmt.executeQuery();
      while (results.next()) {
         String date = results.getString("TransDate");
         double openPrice = Double.parseDouble(results.getString("OpenPrice").trim());
         double highPrice = Double.parseDouble(results.getString("HighPrice").trim());
         double lowPrice = Double.parseDouble(results.getString("LowPrice").trim());
         double closePrice = Double.parseDouble(results.getString("ClosePrice").trim());
         StockData stockData = new StockData(date, openPrice, highPrice, lowPrice,
               closePrice);
         data.add(stockData);
      }
      int loopSize = data.size();
      int loopCounter = splitadjustment(data, result);
      System.out.printf("%d splits on %d trading days%n", loopCounter, loopSize);
      return result;
   }

   static void doStrategy(String ticker, Deque<StockData> data) {
      // To Do:
      // Apply Steps 2.6 to 2.10 explained in the assignment description
      // data (which is a Deque) has all the information (after the split adjustment)
      // you need to apply these steps
      if (data.size() < 51) {
         System.out.println("Net Gain: 0");
         return;
      }

      List<StockData> stockData = new ArrayList<>();
      for (StockData stock : data) {
         stockData.add(stock);
      }
      int transactions = 0;
      double cash = 0;
      double avgPrice = 0;
      int stocks = 0;
      for (int i = 50; i < stockData.size() - 1; i++) {
         double temp = computeAVG(stockData, i - 50);
         if (temp == -1) {
            break;
         }
         avgPrice = temp;
         if (stockData.get(i).closePrice < avgPrice
               && (stockData.get(i).closePrice / stockData.get(i).openPrice) <= 0.97000001) {
            transactions++;
            cash -= stockData.get(i + 1).openPrice * 100;
            stocks = stocks + 100;
            cash -= 8.00;
         } else if (stocks >= 100 && stockData.get(i).openPrice > avgPrice
               && (stockData.get(i).openPrice / stockData.get(i - 1).closePrice) >= 1.00999999) {
            transactions++;
            cash += ((stockData.get(i).openPrice + stockData.get(i).closePrice) / 2) *
                  100;
            stocks = stocks - 100;
            cash -= 8.00;
         }
      }
      cash = cash + stocks * stockData.get(stockData.size() - 1).openPrice;
      System.out.printf("Transactions executed: %d%nNet Cash: %.2f%n", transactions,
            cash);
   }

   static int splitadjustment(List<StockData> data, Deque<StockData> result) {
      int loopSize = data.size();
      int loopCounter = 0;
      for (int i = 0; i < loopSize - 1; i++) {
         double opening = data.get(i).openPrice;
         double closing = data.get(i + 1).closePrice;
         if (Math.abs(closing / opening - 1.5) < 0.14999999) {
            System.out.printf("3:2 split on %s %.2f --> %.2f%n", data.get(i + 1).date, closing, opening);
            loopCounter++;
         } else if (Math.abs(closing / opening - 2.0) < 0.20000001) {
            System.out.printf("2:1 split on %s %.2f --> %.2f%n", data.get(i + 1).date, closing, opening);
            loopCounter++;
         } else if (Math.abs(closing / opening - 3.0) < 0.30000001) {
            System.out.printf("3:1 split on %s %.2f --> %.2f%n", data.get(i + 1).date, closing, opening);
            loopCounter++;
         }
      }
      for (int i = 0; i < loopSize - 1; i++) {
         double opening = data.get(i).openPrice;
         double closing = data.get(i + 1).closePrice;
         if (Math.abs(closing / opening - 1.5) < 0.14999999) {
            for (int j = i + 1; j < data.size() - 1; j++) {
               data.get(j).openPrice = data.get(j).openPrice / 1.5;
               data.get(j).closePrice = data.get(j).closePrice / 1.5;
               data.get(j).highPrice = data.get(j).highPrice / 1.5;
               data.get(j).lowPrice = data.get(j).lowPrice / 1.5;
            }
         } else if (Math.abs(closing / opening - 2.0) < 0.20000001) {
            for (int j = i + 1; j < data.size() - 1; j++) {
               data.get(j).openPrice = data.get(j).openPrice / 2.0;
               data.get(j).closePrice = data.get(j).closePrice / 2.0;
               data.get(j).highPrice = data.get(j).highPrice / 2.0;
               data.get(j).lowPrice = data.get(j).lowPrice / 2.0;
            }
         } else if (Math.abs(closing / opening - 3.0) < 0.30000001) {
            for (int j = i + 1; j < data.size() - 1; j++) {
               data.get(j).openPrice = data.get(j).openPrice / 3.0;
               data.get(j).closePrice = data.get(j).closePrice / 3.0;
               data.get(j).highPrice = data.get(j).highPrice / 3.0;
               data.get(j).lowPrice = data.get(j).lowPrice / 3.0;
            }
         }
         result.addFirst(data.get(i));
      }
      return loopCounter;
   }

   static double computeAVG(List<StockData> data, int range) {
      double sum = 0;
      if (range + 50 >= data.size()) {
         return -1;
      }
      for (int i = range; i < range + 50; i++) {
         sum = sum + data.get(i).closePrice;
      }
      return sum / 50;
   }
}