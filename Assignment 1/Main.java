import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

// Jaden Miguel - CSCI330 Summer 2022
// Part of Assignment 1

public class Main {
    // Main function.
    public static void main(String[] args) {
        Map<String, List<Stock>> companies = new LinkedHashMap<String, List<Stock>>();
        try {
            parse(companies, "StockMarketInput.txt"); // make sure source is correct (mine is in the zip folder)
            process(companies);
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    // Output function, processes our parsed data and calculates everything, as well as prints results out.
    public static void process(Map<String, List<Stock>> companies) throws IOException {
        BufferedWriter output = null;
        try {
            output = new BufferedWriter(new OutputStreamWriter(System.out));

            for(String key : companies.keySet()) {
                output.write("Processing " + key + "\n======================\n");
                processCrazyDays(companies.get(key), output);
                processSplits(companies.get(key), output);
            }

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            if(output != null) {
                output.close();
            }
        }
    }

    // Function to process the splits and then print them out as needed to the passed BufferedWriter.
    public static void processSplits(List<Stock> stocks, BufferedWriter output) throws IOException {
        DecimalFormat df = new DecimalFormat("##.00");
        int split_count = 0;
        int size_of_list = stocks.size();
        // Iterate over list and figure out our splits!
        for(int i = 0; i < stocks.size() - 1; i++) {
            // Split is basically if the next line's closing divided by current line's opening minus some number is less than a specific percentage.
            Double divided = stocks.get(i + 1).getClosing_price() / stocks.get(i).getOpening_price();
            // Check for which type of split we have.
            if(Math.abs(divided - 2.0) < 0.20) {
                output.write("2:1 split on " + stocks.get(i + 1).getDate() + "\t" + df.format(stocks.get(i + 1).getClosing_price()) + "\t--->\t" + df.format(stocks.get(i).getOpening_price()) + "\n");
                split_count++;
            } else if(Math.abs(divided - 3.0) < 0.30) {
                output.write("3:1 split on " + stocks.get(i + 1).getDate() + "\t" + df.format(stocks.get(i + 1).getClosing_price()) + "\t--->\t" + df.format(stocks.get(i).getOpening_price()) + "\n");
                split_count++;
            } else if(Math.abs(divided - 1.5) < 0.15) {
                output.write("3:2 split on " + stocks.get(i + 1).getDate() + "\t" + df.format(stocks.get(i + 1).getClosing_price()) + "\t--->\t" + df.format(stocks.get(i).getOpening_price()) + "\n");
                split_count++;
            }
        }
        output.write("Total number of splits = " + split_count + "\n");
        output.write("\n");
    }

    // Function to process the crazy days and print them out as needed to the passed BufferedWriter.
    public static void processCrazyDays(List<Stock> stocks, BufferedWriter output) throws IOException {
        DecimalFormat df = new DecimalFormat("##.00");
        int crazy_day_count = 0;
        String craziest_day = "";
        double craziest_percentage = 0.00;
        for(Stock stock : stocks) {
            double percentage = (stock.getHigh_price() - stock.getLow_price()) / stock.getHigh_price();
            if(percentage >= 0.15) {
                crazy_day_count++;
                if(percentage > craziest_percentage) {
                    craziest_percentage = percentage;
                    craziest_day = stock.getDate();
                }
                output.write("Crazy day: " + stock.getDate() + "\t" + df.format(percentage * 100) + "\n");
            }
        }
        output.write("Total crazy days = " + crazy_day_count + "\n");
        if(crazy_day_count > 0) {
            output.write("The craziest day: " + craziest_day + "\t" + df.format(craziest_percentage * 100) + "\n");
        }
        output.write("\n");
    }

    // Parsing and storage function.
    public static void parse(Map<String, List<Stock>> companies, String filename) {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(filename));
            String line = reader.readLine();
            while (line != null) {
                String[] tokens = line.split("\t");
                if(!companies.containsKey(tokens[0])) {
                    companies.put(tokens[0], new ArrayList<Stock>());
                }
                companies.get(tokens[0]).add(new Stock(
                        tokens[0],
                        tokens[1],
                        Double.parseDouble(tokens[2]),
                        Double.parseDouble(tokens[3]),
                        Double.parseDouble(tokens[4]),
                        Double.parseDouble(tokens[5]),
                        Integer.parseInt(tokens[6]),
                        Double.parseDouble(tokens[7])
                ));
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
