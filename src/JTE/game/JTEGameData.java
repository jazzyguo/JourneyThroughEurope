/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JTE.game;

import java.util.ArrayList;
import big.data.DataSource;
import big.data.DataSourceIterator;

/**
 *
 * @author Jazzy
 */
public class JTEGameData {

    private ArrayList<City> cities;
    /*
     * Construct this object when a game begins.
     */

    public JTEGameData() {
        cities = new ArrayList<City>();
        initCities("cities.txt");
    }

    public ArrayList<City> getCities() {
        return cities;
    }

    public void initCities(String csvFile) {
        DataSource ds = DataSource.connectCSV(csvFile);
        ds.load();
        DataSourceIterator iter = ds.iterator();
        while (iter.hasData()) { //retrieves and stores the raw data
            City city = new City();
            city.setName(iter.fetchString("City"));
            city.setQuadrant(iter.fetchInt("quarter"));
            if (city.getQuadrant() == 1) {
                city.setCoordinates(((double) (iter.fetchInt("x")) / 2010) * 500, ((double) (iter.fetchInt("y")) / 2569) * 640);
            }
            if (city.getQuadrant() == 2) {
                city.setCoordinates(((double) (iter.fetchInt("x")) / 1903) * 471, ((double) (iter.fetchInt("y")) / 2585) * 640);
            }
            if (city.getQuadrant() == 3) {
                city.setCoordinates(((double) (iter.fetchInt("x")) / 1985) * 491, ((double) (iter.fetchInt("y")) / 2583) * 640);
            }
            if (city.getQuadrant() == 4) {
                city.setCoordinates(((double) (iter.fetchInt("x")) / 1927) * 481, ((double) (iter.fetchInt("y")) / 2561) * 640);
            }
            city.setColor(iter.fetchString("Color"));
            this.cities.add(city);
            iter.loadNext();
        }
    }

    public class City {

        String name, color;
        Coordinates cityCoordinates;
        int mapQuadrant;

        public City() {
            cityCoordinates = new Coordinates();
        }

        public City(String name, String color, Coordinates coord, int mapQ) {
            this.name = name;
            this.color = color;
            cityCoordinates = coord;
            mapQuadrant = mapQ;
        }

        public String getName() {
            return name;
        }

        public String getColor() {
            return color;
        }

        public Coordinates getCoordinates() {
            return cityCoordinates;
        }

        public int getQuadrant() {
            return mapQuadrant;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public void setCoordinates(double x, double y) {
            cityCoordinates.setX(x);
            cityCoordinates.setY(y);
        }

        public void setQuadrant(int q) {
            mapQuadrant = q;
        }
    }

    public class Coordinates {

        private double X;
        private double Y;

        public Coordinates() {
            this(0, 0);
        }

        public Coordinates(double X, double Y) {
            this.X = X;
            this.Y = Y;
        }

        public double getX() {
            return X;
        }

        public double getY() {
            return Y;
        }

        public void setX(double X) {
            this.X = X;
        }

        public void setY(double Y) {
            this.Y = Y;
        }
    }
}
