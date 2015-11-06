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
public class Directional2 implements Runnable{
    
    public static final int COLORS = 16777216;
    public static final int WIDTH = 4096;
    public static final int HEIGHT = 4096;
    
    public static void main(String[] args){
        XORShiftRandom rand = new XORShiftRandom();
        Scanner in = new Scanner(System.in);
        System.out.print("Rear percentage (0-100): ");
        int rearPercent = in.nextInt();
        System.out.print("Individual percent (0-1000): ");
        int indPercent = in.nextInt();
        System.out.print("Direction choosing %: ");
        int dirPercent = in.nextInt();
        System.out.print("Curl: ");
        float curl = in.nextFloat();
        System.out.print("Shape Factor: ");
        int shapeFactor = in.nextInt();
        System.out.print("Flip chance 1 in ");
        int flipChance = in.nextInt();
        in.close();
        
        
        boolean[][][] colorTracker = null;
        BufferedImage image = null;
        BufferedImage resized = null;
        JFrame frame = null;
        setup(colorTracker, image, resized, frame));
        
        
        DirectionalPixel pixelToAdd = new DirectionalPixel(rand, WIDTH, HEIGHT, shapeFactor);
        
        ArrayList<DirectionalPixel> edgeList = new ArrayList<DirectionalPixel>(COLORS / 10);
        edgeList.add(pixelToAdd);
        
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
        
        
        Integer range = new Integer(0);
        Boolean colorFound = new Boolean(false);
        
        Integer currentR = new Integer(0);
        ArrayList<Integer> redList = new ArrayList<Integer>();
        Boolean redRunning = new Boolean(false);
        redThread = new Thread(new Runnable(){
            public void run(){red(redRunning, redList, range, currentR, colorFound);}
        });
        redThread.start();
        
        Integer currentG = new Integer(0);
        ArrayList<Integer> greenList = new ArrayList<Integer>();
        Boolean greenRunning = new Boolean(false);
        greenhread = new Thread(new Runnable(){
            public void run(){green(greenRunning, greenList, range, currentG, colorFound);}
        });
        greenThread.start();
        
        Integer currentB = new Integer(0);
        ArrayList<Integer> blueList = new ArrayList<Integer>();
        Boolean blueRunning = new Boolean(false);
        blueThread = new Thread(new Runnable(){
            public void run(){blue(blueRunning, blueList, range, currentB, colorFound);}
        });
        blueThread.start();
        
        
        
        ArrayList<Integer> colorPossibilities = new ArrayList<Integer>(50);
        ArrayList<DirectionalPixel> nextPossibilities = new ArrayList<DirectionalPixel>(8);
        
        int index; DirectionalPixel toAddTo = null; int counter = 1;
        while (edgeList.size() != 0){
            
            index = 0;
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
            toAddTo.getNextPossibilities(image, nextPossibilities, curl);
            if (nextPossibilities.size() == 0){
                edgeList.remove(index);
                continue;
            }
            edgeList.add(toAddTo);
            edgeList.remove(index);
            
            int rgb = image.getRGB(toAddTo.getX(), toAddTo.getY());
            
            getColorList(rgb, colorPossibilities, colorTracker);
            
            
            index = 0;
            for (int i = 0; i < nextPossibilities.size(); i++){
                if (rand.nextInt(100) < dirPercent){
                    index = i;
                    break;
                }
            }
            
            pixelToAdd = nextPossibilities.get(index);
            colorToAdd = colorPossibilities.get(rand.nextInt(colorPossibilities.size()));
            image.setRGB(pixelToAdd.getX(), pixelToAdd.getY(), colorToAdd);
            colorTracker[(colorToAdd >> 16) & 0xFF][(colorToAdd >> 8) & 0xFF][colorToAdd & 0xFF] = true;
            edgeList.add(pixelToAdd);
            if (pixelToAdd.getX() % 4 == 0 && pixelToAdd.getY() % 4 == 0){
                resized.setRGB(pixelToAdd.getX() / 4, pixelToAdd.getY() / 4, colorToAdd);
                frame.getContentPane().getComponent(1).repaint();
            }
            counter++;
            if (counter % 4096 == 0){
                System.out.printf("%4f %d\n", (double) counter / (double) COLORS * 100, edgeList.size());
            }
        }
        frame.getContentPane().getComponent(1).repaint();
        toFile(image, rand);
        System.out.println("Done.");
        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
        return;
    }
    
    public static void setup(boolean[][][] colorTracker, BufferedImage image, BufferedImage resized, JFrame frame){
        colorTracker = new boolean[256][256][256];
        for (int i = 0; i < colorTracker.length; i++){
            for (int j = 0; j < colorTracker.length; j++){
                Arrays.fill(colorTracker[i][j], false);
            }
        }
        
        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        resized = new BufferedImage(WIDTH / 4, HEIGHT / 4, BufferedImage.TYPE_INT_ARGB);
        
        Graphics2D graphics = image.createGraphics();
        graphics.setPaint(new Color (0, 0, 0, 254));
        graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
        
        graphics = resized.createGraphics();
        graphics.setPaint(new Color (0, 0, 0, 255));
        graphics.fillRect(0, 0, resized.getWidth(), resized.getHeight());
        
        
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
    
    public static void getColorList(int rgb, ArrayList<Integer> colorPossibilities, boolean[][][] colorTracker, Integer currentR, Integer currentG, Integer currentB){
        currentR = (rgb >> 16) & 0xFF;
        currentG = (rgb >> 8) & 0xFF;
        currentB = rgb & 0xFF;
        colorPossibilities.clear();
        do{
            range++;
            
            
            
        } while (colorPossibilities.size() == 0);
    }
    
    public static void red(Boolean redRunning, ArrayList<Integer> redList, Integer range, Integer currentR, Boolean colorFound){
        int rToCheck;
        int gToCheck;
        int bToCheck;
        
        for (;;){
            
            for (;;){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e){
                    if (redRunning){
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
                        redList.add(new Color(rToCheck, gToCheck, bToCheck, 255).getRGB());
                        colorFound = true;
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
                        redList.add(new Color(rToCheck, gToCheck, bToCheck, 255).getRGB());
                        colorFound = true;
                    }
                }
            }
        }
    }
    
    public static void green(Boolean greenRunning, ArrayList<Integer> greenList, Integer range, Integer currentG, Boolean colorFound){
        int rToCheck;
        int gToCheck;
        int bToCheck;
        
        for (;;){
            for (;;){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e){
                    if (greenRunning){
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
                        greenList.add(new Color(rToCheck, gToCheck, bToCheck, 255).getRGB());
                        colorFound = true;
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
                        greenList.add(new Color(rToCheck, gToCheck, bToCheck, 255).getRGB());
                        colorFound = true;
                    }
                }
            }
        }
    }
    
    public static void blue(Boolean blueRunning, ArrayList<Integer> blueList, Integer range, Integer currentB, Boolean colorFound){
        int rToCheck;
        int gToCheck;
        int bToCheck;
        
        for (;;){
            
            for (;;){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e){
                    if (blueRunning){
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
                        blueList.add(new Color(rToCheck, gToCheck, bToCheck, 255).getRGB());
                        colorFound = true;
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
                        blueList.add(new Color(rToCheck, gToCheck, bToCheck, 255).getRGB());
                        colorFound = true;
                    }
                }
            }
            
            blueRunning = false;
            
        }
    }
}