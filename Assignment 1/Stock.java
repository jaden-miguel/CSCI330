// Holds the information for a particular companies stock.
// Jaden Miguel - CSCI330 Summer 2022
// Part of Assignment 1

public class Stock {
    private String ticker;
    private String date;
    private double opening_price, high_price, low_price, closing_price;
    private int volume;
    private double adjusted_closing_price;

    @Override
    public String toString() {
        return "Stock{" +
                "ticker='" + ticker + '\'' +
                ", date='" + date + '\'' +
                ", opening_price=" + opening_price +
                ", high_price=" + high_price +
                ", low_price=" + low_price +
                ", closing_price=" + closing_price +
                ", volume=" + volume +
                ", adjusted_closing_price=" + adjusted_closing_price +
                '}';
    }

    public Stock(String ticker, String date, double opening_price, double high_price, double low_price, double closing_price, int volume, double adjusted_closing_price) {
        this.ticker = ticker;
        this.date = date;
        this.opening_price = opening_price;
        this.high_price = high_price;
        this.low_price = low_price;
        this.closing_price = closing_price;
        this.volume = volume;
        this.adjusted_closing_price = adjusted_closing_price;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getOpening_price() {
        return opening_price;
    }

    public void setOpening_price(double opening_price) {
        this.opening_price = opening_price;
    }

    public double getHigh_price() {
        return high_price;
    }

    public void setHigh_price(double high_price) {
        this.high_price = high_price;
    }

    public double getLow_price() {
        return low_price;
    }

    public void setLow_price(double low_price) {
        this.low_price = low_price;
    }

    public double getClosing_price() {
        return closing_price;
    }

    public void setClosing_price(double closing_price) {
        this.closing_price = closing_price;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public double getAdjusted_closing_price() {
        return adjusted_closing_price;
    }

    public void setAdjusted_closing_price(double adjusted_closing_price) {
        this.adjusted_closing_price = adjusted_closing_price;
    }
}
