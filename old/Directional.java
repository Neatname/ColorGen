import java.util.*;
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.*;
import java.awt.Frame;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.lang.Thread;
import java.lang.*;
import java.util.concurrent.*;
public class Directional implements Runnable {
    
    private static final int COLORS = 16777216;
    private static final int WIDTH = 4096;
    private static final int HEIGHT = 4096;
    
    private boolean runningEdge;
    private boolean runningColors;
    private boolean[][][] colorTracker;
    private ArrayList<DirectionalPixel> edgeList;
    private ArrayList<Integer> colorPossibilities;
    private BufferedImage image;
    private BufferedImage resized;
    private JFrame frame;
    
    private Thread colorThread;
    private Thread edgeThread;
    private Thread mainThread;
    private Thread redPlusThread;
    private Thread redMinusThread;
    private Thread greenPlusThread;
    private Thread greenMinusThread;
    private Thread bluePlusThread;
    private Thread blueMinusThread;
    
    private int range;
    private boolean colorFound;
    private int counter;
    private XORShiftRandom rand;
    private DirectionalPixel pixelToAdd;
    private ArrayList<DirectionalPixel> nextPossibilities;
    private int currentR;
    private int currentG;
    private int currentB;
    private int rgb;
    private int colorToAdd;
    private DirectionalPixel toAddTo;
    private ArrayList<Integer> rPList;
    private ArrayList<Integer> rMList;
    private ArrayList<Integer> gPList;
    private ArrayList<Integer> gMList;
    private ArrayList<Integer> bPList;
    private ArrayList<Integer> bMList;
    int rearPercent;
    int indPercent;
    int dirPercent;
    float curl;
    int shapeFactor;
    int flipChance;
    int index;
    boolean rP;
    boolean rM;
    boolean bP;
    boolean bM;
    boolean gP;
    boolean gM;
    
    public static void main (String[] args){
        new Directional();
    }
    
    public Directional (){
        setup();
        
        mainThread = new Thread(new Runnable(){
            public void run(){
                createImage();
            }
        });
        mainThread.start();
        
        try {
            ColorGen.toFile(image);
        } catch (IOException e){}
    }
    
    private void createImage(){
        while (edgeList.size() != 0){
            
            clearLists();
            getPixel();
            
            //System.out.println("getting nextPossibilities, size = " + nextPossibilities.size());
            toAddTo.getNextPossibilities(image, nextPossibilities, curl);
            //System.out.println("doing removal check, nextPossibilities  = " + nextPossibilities.size());
            if (nextPossibilities.size() == 0){
                edgeList.remove(index);
                //System.out.println("edge removed");
                continue;
            }
            
            runningColors = true;
            colorThread.interrupt();
            runningEdge = true;
            edgeThread.interrupt();
            
            for (;;){
                try {
                    //System.out.println("Waiting for colors and edges to finish calcing...");
                    Thread.sleep(1000);
                } catch (InterruptedException e){
                    if (!runningColors && !runningEdge){
                        break;
                    }
                }
            }
            
            updateGUI();
        }
    }
    
    private void colorPicker (){
        
        while (edgeList.size() != 0){
            for (;;){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e){
                    if (runningColors){
                        break;
                    }
                }
            }
            
            rgb = image.getRGB(toAddTo.getX(), toAddTo.getY());
            currentR = (rgb >> 16) & 0xFF;
            currentG = (rgb >> 8) & 0xFF;
            currentB = rgb & 0xFF;
            
            colorFound = false;
            range = 0;
            
            while (!colorFound){
                range++;
                wakeColorThreads();
                
                colorLoop:
                for (;;){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e){
                        if (!rP && !rM && !gP && !gM && !bP && !bM){
                            break colorLoop;
                        }
                    }
                }
            }
            
            colorPossibilities.addAll(rPList);
            colorPossibilities.addAll(rMList);
            colorPossibilities.addAll(bPList);
            colorPossibilities.addAll(bMList);
            colorPossibilities.addAll(gPList);
            colorPossibilities.addAll(gMList);
            
            runningColors = false;
            mainThread.interrupt();
        }
    }
    
    private void wakeColorThreads(){
        rP = true; rM = true; gP = true; gM = true; bP = true; bM = true;
        redPlusThread.interrupt();
        redMinusThread.interrupt();
        greenPlusThread.interrupt();
        greenMinusThread.interrupt();
        bluePlusThread.interrupt();
        blueMinusThread.interrupt();
    }
    
    private void updateGUI(){
        pixelToAdd = nextPossibilities.get(index);
        colorToAdd = colorPossibilities.get(rand.nextInt(colorPossibilities.size()));
        image.setRGB(pixelToAdd.getX(), pixelToAdd.getY(), colorToAdd);
        if (pixelToAdd.getX() % 4 == 0 && pixelToAdd.getY() % 4 == 0){
            resized.setRGB(pixelToAdd.getX() / 4, pixelToAdd.getY() / 4, colorToAdd);
            frame.getContentPane().getComponent(1).repaint();
        }
        counter++;
        if (counter % 4096 == 0){
            System.out.printf("%4f %d\n", (double) counter / (double) (WIDTH * HEIGHT) * 100, edgeList.size());
        }
    }
    
    private void edgeManager(){
        while (edgeList.size() != 0){
            
            for (;;){
                try {
                    //System.out.println("Waiting for edge instructions...");
                    Thread.sleep(1000);
                } catch (InterruptedException e){
                    if (runningEdge){
                        break;
                    }
                }
            }
            //System.out.println("Got edge instructions");
            
            edgeList.add(toAddTo);
            edgeList.remove(index);
            edgeList.add(pixelToAdd);
            
            runningEdge = false;
            mainThread.interrupt();
            //System.out.println("finished edges");
        }
    }
    
    
    
    
    
    
    
    
    
    
    private void setup(){
        rand = new XORShiftRandom();
        setUpTrackers();
        startImages();
        randomStart();
        startThreads();
        makeGUI();
    }
    
    private void setUpTrackers(){
        
        // stuff false in the whole colorTracker
        colorTracker = new boolean[256][256][256];
        for (int i = 0; i < colorTracker.length; i++){
            for (int j = 0; j < colorTracker.length; j++){
                Arrays.fill(colorTracker[i][j], false);
            }
        }
        
        nextPossibilities = new ArrayList<DirectionalPixel>(8);
        
        edgeList = new ArrayList<DirectionalPixel>(COLORS / 10);
        
        counter = 1;
        
        colorPossibilities = new ArrayList<Integer>(60);
    }
    
    private void startImages(){
        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        resized = new BufferedImage(WIDTH / 4, HEIGHT / 4, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setPaint(new Color (0, 0, 0, 254));
        graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
        graphics = resized.createGraphics();
        graphics.setPaint(new Color (0, 0, 0, 255));
        graphics.fillRect(0, 0, resized.getWidth(), resized.getHeight());
    }
    
    private void makeGUI(){
        frame = new JFrame("ColorGen");
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JLabel emptyLabel = new JLabel("");
        emptyLabel.setPreferredSize(new Dimension(WIDTH / 4, HEIGHT / 4));
        frame.getContentPane().add(emptyLabel, BorderLayout.CENTER);
 
        //Display the window.
        frame.pack();
        frame.setVisible(true);
        
        frame.getContentPane().add(new JLabel(new ImageIcon(resized)));
        frame.setVisible(true);
    }
    
    private void randomStart(){
        
        pixelToAdd = new DirectionalPixel(rand, WIDTH, HEIGHT, shapeFactor);
        
        //System.out.println("adding first pixel...");
        edgeList.add(pixelToAdd);
        //System.out.println("added 1st px, edgeList.size() = " + edgeList.size());
        
        int startR = rand.nextInt(256);
        int startG = rand.nextInt(256);
        int startB = rand.nextInt(256);
        int colorToAdd = new Color(startR, startG, startB, 255).getRGB();
        
        image.setRGB(pixelToAdd.getX(), pixelToAdd.getY(), colorToAdd);
        colorTracker[startR][startG][startB] = true;
        if (pixelToAdd.getX() % 4 == 0 && pixelToAdd.getY() % 4 == 0){
            resized.setRGB(pixelToAdd.getX() / 4, pixelToAdd.getY() / 4, colorToAdd);
            frame.getContentPane().getComponent(1).repaint();
        }        
        
        Scanner in = new Scanner(System.in);
        System.out.print("Rear percentage (0-100): ");
        rearPercent = rand.nextInt(40) + 20;
        System.out.print("Individual percent (0-1000): ");
        indPercent = rand.nextInt(100) + 1;
        System.out.print("Direction choosing %: ");
        dirPercent = rand.nextInt(40) + 60;
        System.out.print("Curl: ");
        curl = (float)rand.nextInt(8);
        System.out.print("Shape Factor: ");
        shapeFactor = rand.nextInt(2000) + 1;
        flipChance = rand.nextInt(16777000);
        in.close();
    }
    
    private void clearLists(){
        colorPossibilities.clear();
        nextPossibilities.clear();
        rPList.clear();
        rMList.clear();
        gPList.clear();
        gMList.clear();
        bPList.clear();
        bMList.clear();
    }
    
    private void startThreads(){
        
        rP = true; rM = true; gP = true; gM = true; bP = true; bM = true;
        
        rPList = new ArrayList<Integer>();
        rMList = new ArrayList<Integer>();
        gPList = new ArrayList<Integer>();
        gMList = new ArrayList<Integer>();
        bPList = new ArrayList<Integer>();
        bMList = new ArrayList<Integer>();
        
        colorThread = new Thread(new Runnable(){
            public void run(){colorPicker();}
        });
        colorThread.start();
        
        edgeThread = new Thread(new Runnable(){
            public void run(){edgeManager();}
        });
        edgeThread.start();
        
        redPlusThread = new Thread(new Runnable(){
            public void run(){redPlus();}
        });
        redPlusThread.start();
        
        redMinusThread = new Thread(new Runnable(){
            public void run(){redMinus();}
        });
        redMinusThread.start();
        
        greenPlusThread = new Thread(new Runnable(){
            public void run(){greenPlus();}
        });
        greenPlusThread.start();
        
        greenMinusThread = new Thread(new Runnable(){
            public void run(){greenMinus();}
        });
        greenMinusThread.start();
        
        bluePlusThread = new Thread(new Runnable(){
            public void run(){bluePlus();}
        });
        bluePlusThread.start();
        
        blueMinusThread = new Thread(new Runnable(){
            public void run(){blueMinus();}
        });
        blueMinusThread.start();
    }
    
    private void getPixel(){
        int index = 0;
        if (rand.nextInt(100) < rearPercent){
            for (int i = 0; i < edgeList.size(); i++){
                if (rand.nextInt(100) < indPercent){
                    index = i;
                    break;
                }
            }
        } else {
            for (int i = edgeList.size() - 1; i >= 0; i--){
                if (rand.nextInt(100) < indPercent){
                    index = i;
                    break;
                }
            }
        }
        
        if (rand.nextInt(flipChance) < 1){
            curl = -curl;
        }
        
        //index = rand.nextInt(edgeList.size());
        
        /* index = edgeList.size() - 1;
        if (rand.nextInt(100) < rearPercent){
            index = rand.nextInt(edgeList.size());
        } else {
            for (int i = edgeList.size() - 1; i >= 0; i--){
                if (rand.nextInt(100) < indPercent){
                    index = i;
                    break;
                }
            }
        } */
        
        /* index = edgeList.size() - 1;
        for (int i = edgeList.size() - 1; i >= 0; i--){
            if (rand.nextInt(1000) < indPercent){
                index = i;
                break;
            }
        } */
        
        toAddTo = edgeList.get(index);
        //System.out.println("got toAddTo, index = " + index);
    }
    
    public void run(){
        
    }
    
    
    
    
    
    
    
    
    
    
    private void redPlus(){
        
        int rToCheck;
        int bToCheck;
        int gToCheck;
        
        while (counter <= COLORS){
            for (;;){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e){
                    if (rP){
                        break;
                    }
                }
            }
            
            rToCheck = currentR + range;
            for (int g = -range; g <= range; g++){
                for (int b = -range; b <= range; b++){
                    gToCheck = g + currentG;
                    bToCheck = b + currentB;
                    if (rToCheck >= 0 && gToCheck >= 0 && bToCheck >= 0 && rToCheck <= 255 && gToCheck <= 255 && bToCheck <= 255 &&
                        !colorTracker[rToCheck][gToCheck][bToCheck]){
                        rPList.add(new Color(rToCheck, gToCheck, bToCheck, 255).getRGB());
                        colorFound = true;
                    }
                }
            }
            rP = false;
            colorThread.interrupt();
        }
    }
    private void redMinus(){
        
        int rToCheck;
        int bToCheck;
        int gToCheck;
        
        while (counter <= COLORS){
            for (;;){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e){
                    if (rM){
                        break;
                    }
                }
            }
            
            rToCheck = currentR - range;
            for (int g = -range; g <= range; g++){
                for (int b = -range; b <= range; b++){
                    gToCheck = g + currentG;
                    bToCheck = b + currentB;
                    if (rToCheck >= 0 && gToCheck >= 0 && bToCheck >= 0 && rToCheck <= 255 && gToCheck <= 255 && bToCheck <= 255 &&
                        !colorTracker[rToCheck][gToCheck][bToCheck]){
                        rMList.add(new Color(rToCheck, gToCheck, bToCheck, 255).getRGB());
                        colorFound = true;
                    }
                }
            }
            
            rM = false;
            colorThread.interrupt();
        }
    }
    
    private void greenPlus(){
        
        int rToCheck;
        int bToCheck;
        int gToCheck;
        
        while (counter <= COLORS){
            for (;;){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e){
                    if (gP){
                        break;
                    }
                }
            }
            
            gToCheck = currentG + range;
            for (int r = -range + 1; r <= range - 1; r++){
                for (int b = -range; b <= range; b++){
                    rToCheck = r + currentR;
                    bToCheck = b + currentB;
                    if (rToCheck >= 0 && gToCheck >= 0 && bToCheck >= 0 && rToCheck <= 255 && gToCheck <= 255 && bToCheck <= 255 &&
                        !colorTracker[rToCheck][gToCheck][bToCheck]){
                        gPList.add(new Color(rToCheck, gToCheck, bToCheck, 255).getRGB());
                        colorFound = true;
                    }
                }
            }
            
            gP = false;
            colorThread.interrupt();
        }
    }
    
    private void greenMinus(){
        
        int rToCheck;
        int bToCheck;
        int gToCheck;
        
        while (counter <= COLORS){
            for (;;){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e){
                    if (gM){
                        break;
                    }
                }
            }
            
            gToCheck = currentG - range;
            for (int r = -range + 1; r <= range - 1; r++){
                for (int b = -range; b <= range; b++){
                    rToCheck = r + currentR;
                    bToCheck = b + currentB;
                    if (rToCheck >= 0 && gToCheck >= 0 && bToCheck >= 0 && rToCheck <= 255 && gToCheck <= 255 && bToCheck <= 255 &&
                        !colorTracker[rToCheck][gToCheck][bToCheck]){
                        gMList.add(new Color(rToCheck, gToCheck, bToCheck, 255).getRGB());
                        colorFound = true;
                    }
                }
            }
            
            gM = false;
            colorThread.interrupt();
        }
    }
    
    private void bluePlus(){
        
        int rToCheck;
        int bToCheck;
        int gToCheck;
        
        while (counter <= COLORS){
            for (;;){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e){
                    if (bP){
                        break;
                    }
                }
            }
            
            bToCheck = currentB + range;
            for (int r = -range + 1; r <= range - 1; r++){
                for (int g = -range + 1; g <= range - 1; g++){
                    rToCheck = r + currentR;
                    gToCheck = g + currentG;
                    if (rToCheck >= 0 && gToCheck >= 0 && bToCheck >= 0 && rToCheck <= 255 && gToCheck <= 255 && bToCheck <= 255 &&
                        !colorTracker[rToCheck][gToCheck][bToCheck]){
                        bPList.add(new Color(rToCheck, gToCheck, bToCheck, 255).getRGB());
                        colorFound = true;
                    }
                }
            }
            
            bP = false;
            colorThread.interrupt();
        }
    }
    
    private void blueMinus(){
        
        int rToCheck;
        int bToCheck;
        int gToCheck;
        
        while (counter <= COLORS){
            for (;;){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e){
                    if (bM){
                        break;
                    }
                }
            }
            
            bToCheck = currentB - range;
            for (int r = -range + 1; r <= range - 1; r++){
                for (int g = -range + 1; g <= range - 1; g++){
                    rToCheck = r + currentR;
                    gToCheck = g + currentG;
                    if (rToCheck >= 0 && gToCheck >= 0 && bToCheck >= 0 && rToCheck <= 255 && gToCheck <= 255 && bToCheck <= 255 &&
                        !colorTracker[rToCheck][gToCheck][bToCheck]){
                        bMList.add(new Color(rToCheck, gToCheck, bToCheck, 255).getRGB());
                        colorFound = true;
                    }
                }
            }
            
            bM = false;
            colorThread.interrupt();
        }
    }
}