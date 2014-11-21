/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JTE.game;

import JTE.UI.JTEUI;
import java.util.ArrayList;
import big.data.DataSource;
import big.data.DataSourceIterator;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 *
 * @author Jazzy
 */
public class JTEGameData {

    private ArrayList<City> cities;
    private ArrayList<Player> players;
    private ArrayList<Card> cards;
    private HashMap<String, String[]> cityLandNeighbors;
    private HashMap<String, String[]> citySeaNeighbors;
    private String ImgPath = "file:images/";
    private JTEUI ui;
    /*
     * Construct this object when a game begins.
     */

    public JTEGameData() {
        cities = new ArrayList<>();
        cards = new ArrayList<>();
        cityLandNeighbors = new HashMap<String, String[]>();
        citySeaNeighbors = new HashMap<String, String[]>();
        initCities("cities.txt");
        initCityNeighbors("city_neighbor.txt", "sea_neighbor.txt");
        initCards();
    }

    public ArrayList<City> getCities() {
        return cities;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public HashMap<String, String[]> getCityLandNeighbors() {
        return cityLandNeighbors;
    }

    public HashMap<String, String[]> getCitySeaNeighbors() {
        return citySeaNeighbors;
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }

    public void setUI(JTEUI ui) {
        this.ui = ui;
    }

    private void initCities(String csvFile) {
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

    private void initCityNeighbors(String landFile, String seaFile) {
        initCityLandNeighbors(landFile);
        initCitySeaNeighbors(seaFile);
    }

    private void initCityLandNeighbors(String file) {
        try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            String str;
            str = in.readLine();
            while ((str = in.readLine()) != null) {
                String[] landN = str.split(",");
                String city = landN[0];
                String[] copyOfRange = Arrays.copyOfRange(landN, 1, landN.length);
                cityLandNeighbors.put(city, copyOfRange);
            }
            in.close();
        } catch (IOException e) {
        }
    }

    private void initCitySeaNeighbors(String file) {
        try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            String str;
            str = in.readLine();
            while ((str = in.readLine()) != null) {
                String[] seaN = str.split(",");
                String city = seaN[0];
                String[] copyOfRange = Arrays.copyOfRange(seaN, 1, seaN.length);
                citySeaNeighbors.put(city, copyOfRange);
            }
            in.close();
        } catch (IOException e) {
        }
    }

    private void initCards() {
        for (City city : cities) {
            String color = city.getColor();
            String name = city.getName();
            String frontString, backString;
            Image front, back;
            Card card;
            File f, p;
            switch (color) {
                case "red":
                    frontString = "red/" + city.getName() + ".jpg";
                    System.out.println(frontString);
                    f = new File("images/red/" + city.getName() + "_I.jpg");
                    p = new File("images/red/" + city.getName() + "_I.png");
                    if (f.exists()) {
                        backString = "red/" + city.getName() + "_I.jpg";
                        System.out.println(backString);
                        back = loadImage(backString);
                    }
                    if (p.exists()) {
                        backString = "red/" + city.getName() + "_I.png";
                        System.out.println(backString);
                        back = loadImage(backString);
                    } else {
                        backString = frontString;
                    }
                    front = loadImage(frontString);
                    back = loadImage(frontString);
                    card = new Card(color, name, front, back);
                    cards.add(card);
                    break;
                case "green":
                    frontString = "green/" + city.getName() + ".jpg";
                    System.out.println(frontString);
                    f = new File("images/green/" + city.getName() + "_I.jpg");
                    p = new File("images/green/" + city.getName() + "_I.png");
                    if (f.exists()) {
                        backString = "green/" + city.getName() + "_I.jpg";
                        System.out.println(backString);
                        back = loadImage(backString);
                    }
                    if (p.exists()) {
                        backString = "green/" + city.getName() + "_I.png";
                        System.out.println(backString);
                        back = loadImage(backString);
                    } else {
                        backString = frontString;
                    }
                    front = loadImage(frontString);
                    back = loadImage(frontString);
                    card = new Card(color, name, front, back);
                    cards.add(card);
                    break;
                case "yellow":
                    frontString = "yellow/" + city.getName() + ".jpg";
                    System.out.println(frontString);
                    f = new File("images/yellow/" + city.getName() + "_I.jpg");
                    p = new File("images/yellow/" + city.getName() + "_I.png");
                    if (f.exists()) {
                        backString = "yellow/" + city.getName() + "_I.jpg";
                        System.out.println(backString);
                        back = loadImage(backString);
                    }
                    if (p.exists()) {
                        backString = "yellow/" + city.getName() + "_I.png";
                        System.out.println(backString);
                        back = loadImage(backString);
                    } else {
                        backString = frontString;
                    }
                    front = loadImage(frontString);
                    back = loadImage(frontString);
                    card = new Card(color, name, front, back);
                    cards.add(card);
                    break;
            }
        }
    }

    public Image loadImage(String imageName) {
        Image img = new Image(ImgPath + imageName);
        return img;
    }

    public static class Player {

        private ArrayList<Card> cardsOnHand;
        private Coordinates currentLocation;
        private City currentCity;
        private String name;
        private boolean isHuman;
        private Image image;

        public Player() {
            isHuman = false;
            initHand();
        }

        public void initHand() {
            for (int i = 0; i < 3; i++) {

            }
        }

        public String getName() {
            return name;
        }

        public boolean isHuman() {
            return isHuman;
        }

        public ArrayList<Card> getCardsOnHand() {
            return cardsOnHand;
        }

        public Coordinates getCurrentLocation() {
            return currentLocation;
        }

        public City getCurrentCity() {
            return currentCity;
        }

        public Image getImage() {
            return image;
        }

        public void setImage(Image image) {
            this.image = image;
        }

        public void setCurrentLocation(Coordinates c) {
            currentLocation = c;
        }

        public void setCurrentCity(City c) {
            currentCity = c;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setHuman() {
            isHuman = true;
        }

        public void setComputer() {
            isHuman = false;
        }
    }

    public static class Card {

        private Image front, back;
        private String color, name;

        public Card() {
        }

        public Card(String color, String name, Image front, Image back) {
            this.color = color;
            this.name = name;
            this.front = front;
            this.back = back;
        }

        public Image getFront() {
            return front;
        }

        public Image getBack() {
            return back;
        }

        public String getColor() {
            return color;
        }

        public String getName() {
            return name;
        }

        public void setFront(Image front) {
            this.front = front;
        }

        public void setBack(Image back) {
            this.back = back;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setColor(String color) {
            this.color = color;
        }
    }

    public class City {

        String name, color;
        Coordinates cityCoordinates;
        int mapQuadrant;
        boolean hasAirport;

        public City() {
            cityCoordinates = new Coordinates();
        }

        public City(String name, String color, Coordinates coord, int mapQ) {
            this.name = name;
            this.color = color;
            cityCoordinates = coord;
            mapQuadrant = mapQ;
            hasAirport = false;
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

        public boolean isAirport() {
            return hasAirport;
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

        public void isAirport(boolean b) {
            hasAirport = b;
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
