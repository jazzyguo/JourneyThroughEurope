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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.commons.lang3.ArrayUtils;

/**
 *
 * @author Jazzy
 */
public class JTEGameData {

    private ArrayList<City> cities;
    private ArrayList<City> flightCities;
    private ArrayList<Player> players;
    private static ArrayList<Card> cards;
    private HashMap<String, String[]> cityLandNeighbors;
    private HashMap<String, String[]> citySeaNeighbors;
    private String ImgPath = "file:images/";
    private JTEUI ui;
    private Die die;
    /*
     * Construct this object when a game begins.
     */

    public JTEGameData() {
        die = new Die();
        cities = new ArrayList<>();
        flightCities = new ArrayList<>();
        cards = new ArrayList<>();
        players = new ArrayList<>();
        cityLandNeighbors = new HashMap<String, String[]>();
        citySeaNeighbors = new HashMap<String, String[]>();
        initCities("cities.txt");
        initFlightCities("flightcities.txt");
        initCityNeighbors("city_neighbor.txt", "sea_neighbor.txt");
        initCityInfo("cityinfo.txt");
    }

    public JTEGameData(JTEUI initUI) {
        ui = initUI;
        die = new Die();
        cities = new ArrayList<>();
        flightCities = new ArrayList<>();
        cards = new ArrayList<>();
        players = new ArrayList<>();
        cityLandNeighbors = new HashMap<String, String[]>();
        citySeaNeighbors = new HashMap<String, String[]>();
        initCities("cities.txt");
        initFlightCities("flightcities.txt");
        initCityNeighbors("city_neighbor.txt", "sea_neighbor.txt");
        initCards();
        initPlayerPlaceCard();
        initCityInfo("cityinfo.txt");
    }

    public Die getDie() {
        return die;
    }

    public ArrayList<City> getCities() {
        return cities;
    }

    public ArrayList<City> getFlightCities() {
        return flightCities;
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

    public ArrayList<String> shortestPath(String from, String to) {
        Queue<Node> q = new LinkedList<>();
        Set<String> v = new TreeSet<>();
        Node head = new Node(from);
        q.add(head);
        v.add(from);
        while (!q.isEmpty()) {
            Node n = q.poll();
            if (n.name.equals(to)) {
                break;
            }
            String[] neighbors = getNeighbors(n.getName());
            if (neighbors != null) {
                for (int i = 0; i < neighbors.length; i++) {
                    if (!v.contains(neighbors[i])) {
                        v.add(neighbors[i]);
                        Node node = new Node(neighbors[i]);
                        q.add(node);
                        n.getNodes().add(node);
                    }
                }
            }
        }
        ArrayList<String> path = new ArrayList();
        path(head, path, to);
        if (!path.isEmpty()) {
            path.remove(0);
        }
        Collections.reverse(path);
        return path;
    }

    public boolean path(Node node, ArrayList<String> path, String dest) {
        if (node.getName().equals(dest)) {
            path.add(node.getName());
            return true;
        }
        for (Node n : node.getNodes()) {
            if (path(n, path, dest)) {
                path.add(n.getName());
                return true;
            }
        }
        return false;
    }

    public String[] getNeighbors(String city) {
        String[] land = cityLandNeighbors.get(city);
        String[] sea = citySeaNeighbors.get(city);
        String[] all = ArrayUtils.addAll(land, sea);
        return all;
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }

    private void initCityInfo(String file) {
        try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            String str;
            str = in.readLine();
            for (City city : cities) {
                if (str.equals(city.name)) {
                    String info = "";
                    str = in.readLine();
                    info += str;
                    str = in.readLine();
                    info += str;
                    str = in.readLine();
                    info += str;
                    str = in.readLine();
                    info += str;
                    str = in.readLine();
                    city.setInfo(info);
                }
            }
            while ((str = in.readLine()) != null) {
                for (City city : cities) {
                    if (str !=null && str.equals(city.name)) {
                        String info = "";
                        str = in.readLine();
                        info += str;
                        str = in.readLine();
                        info += str;
                        str = in.readLine();
                        info += str;
                        str = in.readLine();
                        info += str;
                        str = in.readLine();
                        city.setInfo(info);
                    }
                }
            }
            in.close();
        } catch (IOException e) {
        }
    }

    private void initFlightCities(String csvFile) {
        DataSource ds = DataSource.connectCSV(csvFile);
        ds.load();
        DataSourceIterator iter = ds.iterator();
        while (iter.hasData()) { //retrieves and stores the raw data
            City city = new City();
            city.setName(iter.fetchString("City"));
            city.setQuadrant(iter.fetchInt("quarter"));
            city.setCoordinates((double) (iter.fetchInt("x")), (double) (iter.fetchInt("y")));
            this.flightCities.add(city);
            iter.loadNext();
        }
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
                    backString = "";
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
                    }
                    if (!p.exists() && !f.exists()) {
                        backString = frontString;
                    }
                    front = loadImage(frontString);
                    back = loadImage(backString);
                    card = new Card(color, name, front, back);
                    cards.add(card);
                    break;
                case "green":
                    frontString = "green/" + city.getName() + ".jpg";
                    backString = "";
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
                    }
                    if (!p.exists() && !f.exists()) {
                        backString = frontString;
                    }
                    front = loadImage(frontString);
                    back = loadImage(backString);
                    card = new Card(color, name, front, back);
                    cards.add(card);
                    break;
                case "yellow":
                    frontString = "yellow/" + city.getName() + ".jpg";
                    backString = "";
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
                    }
                    if (!p.exists() && !f.exists()) {
                        backString = frontString;
                    }
                    front = loadImage(frontString);
                    back = loadImage(backString);
                    card = new Card(color, name, front, back);
                    cards.add(card);
                    break;
            }
        }
    }

    public void saveGameDataToFile(File file) {
        try {
            PrintWriter writer = null;
            writer = new PrintWriter(file, "UTF-8");
            writer.println(players.size());
            for (int i = 0; i < players.size(); i++) {
                writer.println(players.get(i).getName());
                writer.println(players.get(i).getCurrentCity());
                writer.println((int) players.get(i).getCurrentLocation().getX());
                writer.println((int) players.get(i).getCurrentLocation().getY());
                writer.println((int) players.get(i).getQuadrant());
                writer.println(players.get(i).getHome());
                writer.println(players.get(i).isHuman());
                writer.println(players.get(i).getNum());
                writer.println(players.get(i).getHomeQ());
                writer.println((int) players.get(i).getHomeLocation().getX());
                writer.println((int) players.get(i).getHomeLocation().getY());
                writer.println(players.get(i).getCardsOnHand().size());
                for (int c = 0; c < players.get(i).getCardsOnHand().size(); c++) {
                    writer.println(players.get(i).getCardsOnHand().get(c).name + "," + players.get(i).getCardsOnHand().get(c).color);
                }
            }
            writer.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(JTEGameData.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(JTEGameData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initPlayerPlaceCard() {
        players = ui.getPlayers();
        for (int i = 0; i < players.size(); i++) {
            switch (i) {
                case 0:
                    players.get(i).getRedCard();
                    players.get(i).setImage(loadImage("p1.png"));
                    players.get(i).setFlag(loadImage("1.png"));
                    players.get(i).setNum(1);
                    players.get(i).setCurrentCity(players.get(i).getCardsOnHand().get(0).getName());
                    players.get(i).initHand();
                    if (!players.get(i).isHuman) {
                        ArrayList<String> route = new ArrayList();
                        route.addAll(shortestPath(players.get(i).currentCity, players.get(i).getCardsOnHand().get(1).name));
                        route.addAll(shortestPath(players.get(i).getCardsOnHand().get(1).name, players.get(i).getCardsOnHand().get(2).name));
                        route.addAll(shortestPath(players.get(i).getCardsOnHand().get(2).name, players.get(i).currentCity));
                        for (String s : route) {
                            players.get(i).getRoute().add(s);
                        }
                    }
                    for (City city : cities) {
                        if (city.getName().equals(players.get(i).getCardsOnHand().get(0).getName())) {
                            players.get(i).setCurrentLocation(city.getCoordinates());
                            players.get(i).setHomeLocation(city.getCoordinates());
                            players.get(i).setQuadrant(city.getQuadrant());
                            players.get(i).setHomeQ(city.getQuadrant());
                            players.get(i).setHome(city.getName());
                        }
                    }
                    break;
                case 1:
                    players.get(i).getGreenCard();
                    players.get(i).setImage(loadImage("p2.png"));
                    players.get(i).setFlag(loadImage("2.png"));
                    players.get(i).setNum(2);
                    players.get(i).setCurrentCity(players.get(i).getCardsOnHand().get(0).getName());
                    players.get(i).initHand();
                    if (!players.get(i).isHuman) {
                        ArrayList<String> route1 = new ArrayList();
                        route1.addAll(shortestPath(players.get(i).currentCity, players.get(i).getCardsOnHand().get(1).name));
                        route1.addAll(shortestPath(players.get(i).getCardsOnHand().get(1).name, players.get(i).getCardsOnHand().get(2).name));
                        route1.addAll(shortestPath(players.get(i).getCardsOnHand().get(2).name, players.get(i).currentCity));
                        for (String s : route1) {
                            players.get(i).getRoute().add(s);
                        }
                    }
                    for (City city : cities) {
                        if (city.getName().equals(players.get(i).getCardsOnHand().get(0).getName())) {
                            players.get(i).setCurrentLocation(city.getCoordinates());
                            players.get(i).setHomeLocation(city.getCoordinates());
                            players.get(i).setQuadrant(city.getQuadrant());
                            players.get(i).setHome(city.getName());
                            players.get(i).setHomeQ(city.getQuadrant());
                        }
                    }
                    break;
                case 2:
                    players.get(i).getYellowCard();
                    players.get(i).setImage(loadImage("p3.png"));
                    players.get(i).setFlag(loadImage("3.png"));
                    players.get(i).setNum(3);
                    players.get(i).setCurrentCity(players.get(i).getCardsOnHand().get(0).getName());
                    players.get(i).initHand();
                    if (!players.get(i).isHuman) {
                        ArrayList<String> route2 = new ArrayList();
                        route2.addAll(shortestPath(players.get(i).currentCity, players.get(i).getCardsOnHand().get(1).name));
                        route2.addAll(shortestPath(players.get(i).getCardsOnHand().get(1).name, players.get(i).getCardsOnHand().get(2).name));
                        route2.addAll(shortestPath(players.get(i).getCardsOnHand().get(2).name, players.get(i).currentCity));
                        for (String s : route2) {
                            players.get(i).getRoute().add(s);
                        }
                    }
                    for (City city : cities) {
                        if (city.getName().equals(players.get(i).getCardsOnHand().get(0).getName())) {
                            players.get(i).setCurrentLocation(city.getCoordinates());
                            players.get(i).setHomeLocation(city.getCoordinates());
                            players.get(i).setQuadrant(city.getQuadrant());
                            players.get(i).setHome(city.getName());
                            players.get(i).setHomeQ(city.getQuadrant());
                        }
                    }
                    break;
                case 3:
                    players.get(i).getRedCard();
                    players.get(i).setImage(loadImage("p4.png"));
                    players.get(i).setFlag(loadImage("4.png"));
                    players.get(i).setNum(4);
                    players.get(i).setCurrentCity(players.get(i).getCardsOnHand().get(0).getName());
                    players.get(i).initHand();
                    if (!players.get(i).isHuman) {
                        ArrayList<String> route3 = new ArrayList();
                        route3.addAll(shortestPath(players.get(i).currentCity, players.get(i).getCardsOnHand().get(1).name));
                        route3.addAll(shortestPath(players.get(i).getCardsOnHand().get(1).name, players.get(i).getCardsOnHand().get(2).name));
                        route3.addAll(shortestPath(players.get(i).getCardsOnHand().get(2).name, players.get(i).currentCity));
                        for (String s : route3) {
                            players.get(i).getRoute().add(s);
                        }
                    }
                    for (City city : cities) {
                        if (city.getName().equals(players.get(i).getCardsOnHand().get(0).getName())) {
                            players.get(i).setCurrentLocation(city.getCoordinates());
                            players.get(i).setHomeLocation(city.getCoordinates());
                            players.get(i).setHome(city.getName());
                            players.get(i).setQuadrant(city.getQuadrant());
                            players.get(i).setHomeQ(city.getQuadrant());
                        }
                    }
                    break;
                case 4:
                    players.get(i).getGreenCard();
                    players.get(i).setImage(loadImage("p5.png"));
                    players.get(i).setFlag(loadImage("5.png"));
                    players.get(i).setNum(5);
                    players.get(i).setCurrentCity(players.get(i).getCardsOnHand().get(0).getName());
                    players.get(i).initHand();
                    if (!players.get(i).isHuman) {
                        ArrayList<String> route4 = new ArrayList();
                        route4.addAll(shortestPath(players.get(i).currentCity, players.get(i).getCardsOnHand().get(1).name));
                        route4.addAll(shortestPath(players.get(i).getCardsOnHand().get(1).name, players.get(i).getCardsOnHand().get(2).name));
                        route4.addAll(shortestPath(players.get(i).getCardsOnHand().get(2).name, players.get(i).currentCity));
                        for (String s : route4) {
                            players.get(i).getRoute().add(s);
                        }
                    }
                    for (City city : cities) {
                        if (city.getName().equals(players.get(i).getCardsOnHand().get(0).getName())) {
                            players.get(i).setCurrentLocation(city.getCoordinates());
                            players.get(i).setHomeLocation(city.getCoordinates());
                            players.get(i).setQuadrant(city.getQuadrant());
                            players.get(i).setHome(city.getName());
                            players.get(i).setHomeQ(city.getQuadrant());
                        }
                    }
                    break;
                case 5:
                    players.get(i).getYellowCard();
                    players.get(i).setImage(loadImage("p6.png"));
                    players.get(i).setFlag(loadImage("6.png"));
                    players.get(i).setNum(6);
                    players.get(i).setCurrentCity(players.get(i).getCardsOnHand().get(0).getName());
                    players.get(i).initHand();
                    if (!players.get(i).isHuman) {
                        ArrayList<String> route5 = new ArrayList();
                        route5.addAll(shortestPath(players.get(i).currentCity, players.get(i).getCardsOnHand().get(1).name));
                        route5.addAll(shortestPath(players.get(i).getCardsOnHand().get(1).name, players.get(i).getCardsOnHand().get(2).name));
                        route5.addAll(shortestPath(players.get(i).getCardsOnHand().get(2).name, players.get(i).currentCity));
                        for (String s : route5) {
                            players.get(i).getRoute().add(s);
                        }
                    }
                    for (City city : cities) {
                        if (city.getName().equals(players.get(i).getCardsOnHand().get(0).getName())) {
                            players.get(i).setCurrentLocation(city.getCoordinates());
                            players.get(i).setHomeLocation(city.getCoordinates());
                            players.get(i).setQuadrant(city.getQuadrant());
                            players.get(i).setHome(city.getName());
                            players.get(i).setHomeQ(city.getQuadrant());
                        }
                    }
                    break;
            }
        }
    }

    public Image loadImage(String imageName) {
        Image img = new Image(ImgPath + imageName);
        return img;
    }

    public final class Die {

        private int roll;
        private Image img;

        public Die() {
        }

        public void roll() {
            Random rand = new Random();
            roll = rand.nextInt((6 - 1) + 1) + 1;
        }

        public int getRoll() {
            return roll;
        }

        public void setRoll(int roll) {
            this.roll = roll;
        }

        public Image getImg() {
            return img;
        }

        public void setImage() {
            switch (roll) {
                case 1:
                    img = loadImage("die_1.jpg");
                    break;
                case 2:
                    img = loadImage("die_2.jpg");
                    break;
                case 3:
                    img = loadImage("die_3.jpg");
                    break;
                case 4:
                    img = loadImage("die_4.jpg");
                    break;
                case 5:
                    img = loadImage("die_5.jpg");
                    break;
                case 6:
                    img = loadImage("die_6.jpg");
                    break;
            }
        }
    }

    public static class Player {

        private ArrayList<Card> cardsOnHand;
        Queue<String> route;
        private Coordinates currentLocation, tempLocation;
        private Coordinates homeLocation;
        private String currentCity, home;
        private String name;
        private boolean isHuman;
        private Image image, flag;
        private int quadrant, num, homeQuadrant;
        private Die die;
        private boolean turn;

        public Player() {
            route = new LinkedList<>();
            isHuman = false;
            cardsOnHand = new ArrayList<>();
            currentLocation = new Coordinates();
            tempLocation = new Coordinates();
            turn = false;
        }

        public Queue<String> getRoute() {
            return route;
        }

        public int getHomeQ() {
            return homeQuadrant;
        }

        public void setHomeQ(int q) {
            homeQuadrant = q;
        }

        public String getHome() {
            return home;
        }

        public boolean isTurn() {
            return turn;
        }

        public int getQuadrant() {
            return quadrant;
        }

        public int getNum() {
            return num;
        }

        public void initHand() {
            if (cardsOnHand.get(0).getColor().equals("green")) {
                getRedCard();
                getYellowCard();
            }
            if (cardsOnHand.get(0).getColor().equals("red")) {
                getGreenCard();
                getYellowCard();
            }
            if (cardsOnHand.get(0).getColor().equals("yellow")) {
                getRedCard();
                getGreenCard();
            }
        }

        public void setQuadrant(int q) {
            quadrant = q;
        }

        public void setNum(int n) {
            num = n;
        }

        public void getRedCard() {
            int i = (int) (Math.random() * 180);
            while (!cards.get(i).getColor().equals("red")) {
                i = (int) (Math.random() * 180);
            }
            cardsOnHand.add(cards.get(i));
        }

        public void getGreenCard() {
            int i = (int) (Math.random() * 180);
            while (!cards.get(i).getColor().equals("green")) {
                i = (int) (Math.random() * 180);
            }
            cardsOnHand.add(cards.get(i));
        }

        public void getYellowCard() {
            int i = (int) (Math.random() * 180);
            while (!cards.get(i).getColor().equals("yellow")) {
                i = (int) (Math.random() * 180);
            }
            cardsOnHand.add(cards.get(i));
        }

        public String getName() {
            return name;
        }

        public boolean isHuman() {
            return isHuman;
        }

        public void setHome(String home) {
            this.home = home;
        }

        public ArrayList<Card> getCardsOnHand() {
            return cardsOnHand;
        }

        public Coordinates getCurrentLocation() {
            return currentLocation;
        }

        public Coordinates getTempLocation() {
            return tempLocation;
        }

        public void setTempLocation(Coordinates temp) {
            this.tempLocation = temp;
        }

        public Coordinates getHomeLocation() {
            return homeLocation;
        }

        public String getCurrentCity() {
            return currentCity;
        }

        public Image getImage() {
            return image;
        }

        public void setImage(Image image) {
            this.image = image;
        }

        public Image getFlag() {
            return flag;
        }

        public void setFlag(Image flag) {
            this.flag = flag;
        }

        public void setCurrentLocation(Coordinates c) {
            currentLocation = c;
        }

        public void setHomeLocation(Coordinates c) {
            homeLocation = c;
        }

        public void setCurrentCity(String c) {
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

        public void setTurnEnd() {
            turn = false;
        }

        public void setTurn() {
            turn = true;
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

    public static class City {

        String name, color, info;
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

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
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

    public static class Coordinates {

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

    public static class Node {

        String name;
        ArrayList<Node> nodes;

        public Node(String name) {
            this.name = name;
            nodes = new ArrayList();
        }

        public String getName() {
            return name;
        }

        public ArrayList<Node> getNodes() {
            return nodes;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
