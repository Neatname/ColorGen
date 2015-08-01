import java.util.*;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.*;
import java.awt.Frame;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.imageio.ImageIO;
public class ColorGen {
    
    /** this is the number of colors representable in 32 bits. */
    public static final int MAX_COLORS = 16777216;
    
    public static void main (String[] args) throws IOException{
        System.out.print("Which method? ");
        Scanner in = new Scanner(System.in);
        String method = in.next();
        if (method.equals("random")){
            random();
        }
        if (method.equals("growing")){
            growing();
        }
        if (method.equals("snake")){
            snake();
        }
        // whenever you add more methods, just add another if
        
    }
    
    // this thing will destroy your CPU, so don't plan on doing anything important while running this lol
    public static void random(){
        
        // note that colors must be equal to height * width because otherwise you aren't filling the right number of pixels
        int colors = 262144;
        int width = 512;
        int height = 512;
        
        // declare array of colors to be used
        Color[] colorArray = new Color[colors];
        
        // fill array
        // This hinges on how colors are represented. The way I'm doing it here is with Color objects, which have a few different
        // ways to do it. I'm using RGB. It's all packed into 32 bits. It's split into four 8-bit chunks. Each chunk has a max
        // decimal value of 255 (11111111 in binary, FF in hex). The first represents alpha, which is always 255 because I'm
        // not dealing with alpha. Second is red, third is green, fourth is blue. The setRGB and getRGB methods in the Color class
        // set and return the values of the colors as integers. So the value of white (xFFFFFFFF) is -1. Black (xFF000000) is
        // -16777215.
        
        // starts at -1 and stuffs all the colors (skipping some if not using all of them, that's what the adjustmentFactor is for)
        int adjustmentFactor = MAX_COLORS / colors;
        for (int i = 0; i > -colors; i--){
            Color current = new Color((i - 1) * adjustmentFactor);
            colorArray[-i] = current;
        }
        
        // creates a new image to draw on
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        // I'll be honest, I don't fully understand what this block of code is, I got it from the internet. It makes the GUI.
        JFrame frame = new JFrame("ColorGen");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JLabel emptyLabel = new JLabel("");
        emptyLabel.setPreferredSize(new Dimension(width, height));
        frame.getContentPane().add(emptyLabel, BorderLayout.CENTER);
        //Display the window.
        frame.pack();
        frame.setVisible(true);
        
        Random rand = new Random();
        
        // steps through the image in order and for each pixel, fills a random, unused color.
        for (int i = 0; i < height; i++){
            for (int j = 0; j < width; j++){
                int index;
                do {
                    index = rand.nextInt(colors);
                } while (colorArray[index] == null);        // makes sure the color hasn't been used
                int current = colorArray[index].getRGB();   // gets color as an int
                colorArray[index] = null;                   // deletes the color from the list after using it
                image.setRGB(i, j, current);                // set the pixel
                if (j % 64 == 0){                           // the GUI kills the computer, so I only let it update every 64 iterations
                    frame.getContentPane().add(new JLabel(new ImageIcon(image)));
                    frame.setVisible(true);
                }
            }
        }
    }
    
    // I just need to commit this to transfer between computers, comment it out or this won't compile.
    public static void growing() throws IOException{
        int colors = 16777216;
        int width = 1024;
        int height = 1024;
        int initialColorRange = 5;
        boolean[] colorArray = new boolean[colors + 1];
        Arrays.fill(colorArray, false);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setPaint(new Color (0, 0, 0, 254));
        graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
        
        JFrame frame = new JFrame("ColorGen");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JLabel emptyLabel = new JLabel("");
        emptyLabel.setPreferredSize(new Dimension(width, height));
        frame.getContentPane().add(emptyLabel, BorderLayout.CENTER);
 
        //Display the window.
        frame.pack();
        frame.setVisible(true);
        
        Random rand = new Random();
        int startX = rand.nextInt(width);
        int startY = rand.nextInt(height);
        int startColorIndex = rand.nextInt(colors + 1) + 1;
        int startColor = -startColorIndex;
        
        image.setRGB(startX, startY, startColor);
        frame.getContentPane().add(new JLabel(new ImageIcon(image)));
        frame.setVisible(true);
        
        ArrayList<Integer[]> edgeList = new ArrayList<Integer[]>(colors / 10);
        
        Integer[] temp = new Integer[2];
        edgeList.add(new Integer[] {startX, startY});
        colorArray[startColorIndex] = true;
        int counter = 1;
        ArrayList<Integer[]> nextPossibilities = new ArrayList<Integer[]>();
        Integer[] coords = null;
        ArrayList<Color> colorPossibilities = new ArrayList<Color>();
        while (edgeList.size() != 0){
            int index = rand.nextInt(edgeList.size());
            coords = edgeList.get(index);
            int currentX = coords[0];
            int currentY = coords[1];
            nextPossibilities.clear();
            for (int x = -1; x <= 1; x++){
                for (int y = -1; y <= 1; y++){
                    int xToCheck = currentX + x;
                    int yToCheck = currentY + y;
                    if (xToCheck >= 0 && yToCheck >= 0 && xToCheck < width && yToCheck < height && image.getRGB(xToCheck, yToCheck) == -33554432) {
                        nextPossibilities.add(new Integer[] {xToCheck, yToCheck});
                    }
                }
            }
            if (nextPossibilities.size() == 0){
                edgeList.remove(index);
                continue;
            }
            colorPossibilities.clear();
            int range = initialColorRange - 3;
            int tempColorInt = 1;
            int rgb = image.getRGB(currentX, currentY);
            int currentR = (rgb >> 16) & 0xFF;
            int currentG = (rgb >> 8) & 0xFF;
            int currentB = rgb & 0xFF;
            int rToCheck;
            int gToCheck;
            int bToCheck;
            do{
                range += 3;
                for (int r = -range; r <= range; r++){
                    for (int g = -range; g <= range; g++){
                        for (int b = -range; b <= range; b++){
                            rToCheck = r + currentR;
                            gToCheck = g + currentG;
                            bToCheck = b + currentB;
                            if (rToCheck >= 0 && gToCheck >= 0 && bToCheck >= 0 && rToCheck <= 255 && gToCheck <= 255 && bToCheck <= 255){
                                Color tempColor = new Color(rToCheck, gToCheck, bToCheck, 255);
                                tempColorInt = tempColor.getRGB();
                                if (colorArray[-tempColorInt]){
                                    continue;
                                }
                                colorPossibilities.add(new Color(rToCheck, gToCheck, bToCheck, 255));
                            }
                        }
                    }
                }
            } while (colorPossibilities.size() == 0);
            Integer[] xyToAdd = nextPossibilities.get(rand.nextInt(nextPossibilities.size()));
            Color colorToAdd = colorPossibilities.get(rand.nextInt(colorPossibilities.size()));
            image.setRGB(xyToAdd[0], xyToAdd[1], colorToAdd.getRGB());
            colorArray[-colorToAdd.getRGB()] = true;
            edgeList.add(xyToAdd);
            frame.getContentPane().getComponent(1).repaint();
            counter++;
        }
        frame.getContentPane().add(new JLabel(new ImageIcon(image)));
        frame.setVisible(true);
        toFile(image);
        System.out.println("Done.");
    }
    
    
    public static void snake() throws IOException{
        int colors = 16777216;
        int width = 4096;
        int height = 4096;
        int initialColorRange = 2;
        boolean[] colorArray = new boolean[colors + 1];
        Arrays.fill(colorArray, false);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setPaint(new Color (0, 0, 0, 254));
        graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
        
        JFrame frame = new JFrame("ColorGen");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JLabel emptyLabel = new JLabel("");
        emptyLabel.setPreferredSize(new Dimension(width, height));
        frame.getContentPane().add(emptyLabel, BorderLayout.CENTER);
 
        //Display the window.
        frame.pack();
        frame.setVisible(true);
        
        Random rand = new Random();
        int startX = rand.nextInt(width);
        int startY = rand.nextInt(height);
        int startColorIndex = rand.nextInt(colors + 1) + 1;
        int startColor = -startColorIndex;
        
        image.setRGB(startX, startY, startColor);
        frame.getContentPane().add(new JLabel(new ImageIcon(image)));
        frame.setVisible(true);
        
        ArrayList<Integer[]> edgeList = new ArrayList<Integer[]>(colors / 3);
        
        Integer[] temp = new Integer[2];
        edgeList.add(new Integer[] {startX, startY});
        colorArray[startColorIndex] = true;
        int counter = 1;
        ArrayList<Integer[]> nextPossibilities = new ArrayList<Integer[]>();
        Integer[] coords = null;
        ArrayList<Color> colorPossibilities = new ArrayList<Color>();
        int range = 1;
        while (edgeList.size() != 0){
            int index = -1;
            for (int i = edgeList.size() - 1; i >= 0; i--){
                if (rand.nextInt(100) < 5){
                    index = i;
                    break;
                }
            }
            if (index == -1){
                index = edgeList.size() - 1;
            }
            coords = edgeList.get(index);
            int currentX = coords[0];
            int currentY = coords[1];
            nextPossibilities.clear();
            for (int x = -1; x <= 1; x++){
                for (int y = -1; y <= 1; y++){
                    int xToCheck = currentX + x;
                    int yToCheck = currentY + y;
                    if (xToCheck >= 0 && yToCheck >= 0 && xToCheck < width && yToCheck < height && image.getRGB(xToCheck, yToCheck) == -33554432) {
                        nextPossibilities.add(new Integer[] {xToCheck, yToCheck});
                    }
                }
            }
            if (nextPossibilities.size() == 0){
                edgeList.remove(index);
                continue;
            }
            colorPossibilities.clear();
            int tempColorInt = 1;
            int rgb = image.getRGB(currentX, currentY);
            int currentR = (rgb >> 16) & 0xFF;
            int currentG = (rgb >> 8) & 0xFF;
            int currentB = rgb & 0xFF;
            int rToCheck;
            int gToCheck;
            int bToCheck;
            Color tempColor = null;
            do{
                range++;
                for (int r = -range; r <= range; r++){
                    for (int g = -range; g <= range; g++){
                        for (int b = -range; b <= range; b++){
                            rToCheck = r + currentR;
                            gToCheck = g + currentG;
                            bToCheck = b + currentB;
                            if (rToCheck >= 0 && gToCheck >= 0 && bToCheck >= 0 && rToCheck <= 255 && gToCheck <= 255 && bToCheck <= 255){
                                tempColor = new Color(rToCheck, gToCheck, bToCheck, 255);
                                tempColorInt = tempColor.getRGB();
                                if (colorArray[-tempColorInt]){
                                    continue;
                                }
                                colorPossibilities.add(new Color(rToCheck, gToCheck, bToCheck, 255));
                            }
                        }
                    }
                }
            } while (colorPossibilities.size() == 0);
            Integer[] xyToAdd = nextPossibilities.get(rand.nextInt(nextPossibilities.size()));
            Color colorToAdd = colorPossibilities.get(rand.nextInt(colorPossibilities.size()));
            image.setRGB(xyToAdd[0], xyToAdd[1], colorToAdd.getRGB());
            colorArray[-colorToAdd.getRGB()] = true;
            edgeList.add(xyToAdd);
            frame.getContentPane().getComponent(1).repaint();
            counter++;
            if (counter % 2048 == 0){
                System.out.printf("%4f\n", (double) counter / (double) colors * 100);
            }
            range--;
        }
        frame.getContentPane().add(new JLabel(new ImageIcon(image)));
        frame.setVisible(true);
        toFile(image);
        System.out.println("Done.");
    }
    
    
    public static void toFile(BufferedImage i) throws IOException{
        File out = new File(System.currentTimeMillis() + ".png");
        ImageIO.write(i, "PNG", out);
    }
}















