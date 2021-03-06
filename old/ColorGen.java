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
        if (method.equals("snake2")){
            snake2();
        }
        if (method.equals("snake3")){
            snake3();
        }
        if (method.equals("clouds")){
            clouds();
        }
        if (method.equals("directional")){
            for(;;){
                directional();
            }
        }
        if (method.equals("combo")){
            for(;;){
                combo();
            }
        }
        if (method.equals("test")){
            for(;;){
                linkTest();
            }  
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
    
    
    public static void growing() throws IOException{
        // initializations
        int colors = 16777216;
        int width = 4096;
        int height = 4096;
        int startingPoints = 50;
        
        // array that keeps track of whether colors have been used. true = used.
        boolean[] colorArray = new boolean[colors + 1];
        Arrays.fill(colorArray, false);
        
        // creates a blank BufferedImage and fills it with black at 254 opacity
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        BufferedImage resized = new BufferedImage(width / 4, height / 4, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setPaint(new Color (0, 0, 0, 254));
        graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
        graphics = resized.createGraphics();
        graphics.setPaint(new Color (0, 0, 0, 255));
        graphics.fillRect(0, 0, resized.getWidth(), resized.getHeight());
        
        // creates the display
        JFrame frame = new JFrame("ColorGen");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JLabel emptyLabel = new JLabel("");
        emptyLabel.setPreferredSize(new Dimension(width / 4, height / 4));
        frame.getContentPane().add(emptyLabel, BorderLayout.CENTER);
 
        //Display the window.
        frame.pack();
        frame.setVisible(true);
        
        // more initialization
        XORShiftRandom rand = new XORShiftRandom();
        int startX = rand.nextInt(width);
        int startY = rand.nextInt(height);
        
        int startColorIndex = rand.nextInt(colors + 1) + 1;
        int startColor = -startColorIndex;
        
        image.setRGB(startX, startY, startColor);
        frame.getContentPane().add(new JLabel(new ImageIcon(resized)));
        frame.setVisible(true);
        
        // this keeps track of every pixel that still needs neighbors
        ArrayList<Integer[]> edgeList = new ArrayList<Integer[]>();
        
        edgeList.add(new Integer[] {startX, startY});
        if (startX % 4 == 0 && startY % 4 == 0){
            resized.setRGB(startX / 4, startY / 4, startColor);
            frame.getContentPane().getComponent(1).repaint();
        }
        colorArray[startColorIndex] = true;
        
        // one starting point is already set, this sets the rest, if any
        for (int i = startingPoints - 1; i > 0; i--){
            startX = rand.nextInt(width);
            startY = rand.nextInt(height);
            startColorIndex = rand.nextInt(colors + 1) + 1;
            if (colorArray[startColorIndex]){
                i++;
                continue;
            }
            startColor = -startColorIndex;
            image.setRGB(startX, startY, startColor);
            if (startX % 4 == 0 && startY % 4 == 0){
                resized.setRGB(startX / 4, startY / 4, startColor);
                frame.getContentPane().getComponent(1).repaint();
            }
            edgeList.add(new Integer[] {startX, startY});
        }
        
        // this just keeps track of the number of iterations for calculating percentage done.
        int counter = 1;
        
        // a list of possibilities for the next pixel to be populated
        ArrayList<Integer[]> nextPossibilities = new ArrayList<Integer[]>();
        
        Integer[] coords = null;
        ArrayList<Color> colorPossibilities = new ArrayList<Color>();
        int range; int neighborRange = 1;
        while (edgeList.size() != 0){
            
            // gets a random edge pixel and resets the possibilities list
            int index = rand.nextInt(edgeList.size());
            coords = edgeList.get(index);
            int currentX = coords[0];
            int currentY = coords[1];
            nextPossibilities.clear();
            
            // steps through each neighbor and checks if it's populated. If not populated, adds to possibilities list
            for (int x = -neighborRange; x <= neighborRange; x++){
                for (int y = -neighborRange; y <= neighborRange; y++){
                    int xToCheck = currentX + x;
                    int yToCheck = currentY + y;
                    if (xToCheck >= 0 && yToCheck >= 0 && xToCheck < width && yToCheck < height && image.getRGB(xToCheck, yToCheck) == -33554432) {
                        nextPossibilities.add(new Integer[] {xToCheck, yToCheck});
                    }
                }
            }
            
            // if no neighbors are available, this pixel is removed from the edge list
            if (nextPossibilities.size() == 0){
                edgeList.remove(index);
                continue;
            }
            
            // prepares to get the color to set next
            colorPossibilities.clear();
            int tempColorInt;
            int rgb = image.getRGB(currentX, currentY);
            int currentR = (rgb >> 16) & 0xFF;
            int currentG = (rgb >> 8) & 0xFF;
            int currentB = rgb & 0xFF;
            int rToCheck;
            int gToCheck;
            int bToCheck;
            Color tempColor = null;
            range = 0;
            do{
                range++;
                
                // Super long but it's basically just the same thing 6 times with small tweaks. Think of RGB colors as being on a 3D graph
                // with each color being an axis. This searches the surface of an increasingly larger cube centered around the current color.
                rToCheck = currentR - range;
                for (int g = -range; g <= range; g++){
                    for (int b = -range; b <= range; b++){
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
                rToCheck = currentR + range;
                for (int g = -range; g <= range; g++){
                    for (int b = -range; b <= range; b++){
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
                gToCheck = currentG - range;
                for (int r = -range + 1; r <= range - 1; r++){
                    for (int b = -range; b <= range; b++){
                        rToCheck = r + currentR;
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
                gToCheck = currentG + range;
                for (int r = -range + 1; r <= range - 1; r++){
                    for (int b = -range; b <= range; b++){
                        rToCheck = r + currentR;
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
                bToCheck = currentB - range;
                for (int r = -range + 1; r <= range - 1; r++){
                    for (int g = -range + 1; g <= range - 1; g++){
                        rToCheck = r + currentR;
                        gToCheck = g + currentG;
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
                bToCheck = currentB + range;
                for (int r = -range + 1; r <= range - 1; r++){
                    for (int g = -range + 1; g <= range - 1; g++){
                        rToCheck = r + currentR;
                        gToCheck = g + currentG;
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
            } while (colorPossibilities.size() == 0);
            
            // gets a random possible pixel and a random possible color and fills it
            Integer[] xyToAdd = nextPossibilities.get(rand.nextInt(nextPossibilities.size()));
            Color colorToAdd = colorPossibilities.get(rand.nextInt(colorPossibilities.size()));
            image.setRGB(xyToAdd[0], xyToAdd[1], colorToAdd.getRGB());
            colorArray[-colorToAdd.getRGB()] = true;
            edgeList.add(xyToAdd);
            
            // updates visual stuff
            counter++;
            if (xyToAdd[0] % 4 == 0 && xyToAdd[1] % 4 == 0){
                resized.setRGB(xyToAdd[0] / 4, xyToAdd[1] / 4, colorToAdd.getRGB());
                frame.getContentPane().getComponent(1).repaint();
            }
            if (counter % 2048 == 0){
                System.out.printf("%4f %d\n", (double) counter / (double) colors * 100, edgeList.size());
            }
        }
        frame.getContentPane().getComponent(1).repaint();
        toFile(image);
        System.out.println("Done.");
    }
    
    

    
    
    public static void snake() throws IOException{
        int colors = 16777216;
        int width = 4096;
        int height = 4096;
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
        int range;
        while (edgeList.size() != 0){
            int index = -1;
            for (int i = edgeList.size() - 1; i >= 0; i--){
                if (rand.nextInt(50) < 1){
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
            range = 0;
            do{
                range++;
                rToCheck = currentR - range;
                for (int g = -range; g <= range; g++){
                    for (int b = -range; b <= range; b++){
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
                rToCheck = currentR + range;
                for (int g = -range; g <= range; g++){
                    for (int b = -range; b <= range; b++){
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
                gToCheck = currentG - range;
                for (int r = -range + 1; r <= range - 1; r++){
                    for (int b = -range; b <= range; b++){
                        rToCheck = r + currentR;
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
                gToCheck = currentG + range;
                for (int r = -range + 1; r <= range - 1; r++){
                    for (int b = -range; b <= range; b++){
                        rToCheck = r + currentR;
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
                bToCheck = currentB - range;
                for (int r = -range + 1; r <= range - 1; r++){
                    for (int g = -range + 1; g <= range - 1; g++){
                        rToCheck = r + currentR;
                        gToCheck = g + currentG;
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
                bToCheck = currentB + range;
                for (int r = -range + 1; r <= range - 1; r++){
                    for (int g = -range + 1; g <= range - 1; g++){
                        rToCheck = r + currentR;
                        gToCheck = g + currentG;
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
        }
        frame.getContentPane().add(new JLabel(new ImageIcon(image)));
        frame.setVisible(true);
        toFile(image);
        System.out.println("Done.");
    }
    
    
    public static void snake2() throws IOException{
        int colors = 16777216;
        int width = 4096;
        int height = 4096;
        
        // [r][g][b]
        boolean[][][] colorTracker = new boolean[256][256][256];
        for (int i = 0; i < colorTracker.length; i++){
            for (int j = 0; j < colorTracker.length; j++){
                Arrays.fill(colorTracker[i][j], false);
            }
        }
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
        
        XORShiftRandom rand = new XORShiftRandom();
        int startX = rand.nextInt(width);
        int startY = rand.nextInt(height);
        int startR = rand.nextInt(256);
        int startG = rand.nextInt(256);
        int startB = rand.nextInt(256);
        int colorToAdd = new Color(startR, startG, startB, 255).getRGB();
        image.setRGB(startX, startY, colorToAdd);
        colorTracker[startR][startG][startB] = true;
        ArrayList<int[]> edgeList = new ArrayList<int[]>(colors / 8);
        edgeList.add(new int[] {startX, startY});
        frame.getContentPane().add(new JLabel(new ImageIcon(image)));
        frame.setVisible(true);
        
        ArrayList<int[]> nextPossibilities = new ArrayList<int[]>(8);
        
        int index; int currentX; int currentY; int xToCheck; int yToCheck; int rToCheck; int gToCheck; int bToCheck; int[] xyToAdd = null; int[] coords = null; int range; int counter = 1;
        ArrayList<Integer> colorPossibilities = new ArrayList<Integer>();
        while (edgeList.size() != 0){
            index = -1;
            for (int i = edgeList.size() - 1; i >= 0; i--){
                if (rand.nextInt(50) < 1){
                    index = i;
                    break;
                }
            }
            if (index == -1){
                index = edgeList.size() - 1;
            }
            coords = edgeList.get(index);
            currentX = coords[0];
            currentY = coords[1];
            nextPossibilities.clear();
            for (int x = -1; x <= 1; x++){
                for (int y = -1; y <= 1; y++){
                    xToCheck = currentX + x;
                    yToCheck = currentY + y;
                    try{
                        if (image.getRGB(xToCheck, yToCheck) == -33554432) {
                            nextPossibilities.add(new int[] {xToCheck, yToCheck});
                        }
                    } catch (ArrayIndexOutOfBoundsException e){}
                }
            }
            if (nextPossibilities.size() == 0){
                edgeList.remove(index);
                continue;
            }
            edgeList.add(edgeList.get(index));
            edgeList.remove(index);
            
            int rgb = image.getRGB(currentX, currentY);
            int currentR = (rgb >> 16) & 0xFF;
            int currentG = (rgb >> 8) & 0xFF;
            int currentB = rgb & 0xFF;
            range = 0;
            colorPossibilities.clear();
            do{
                range++;
                rToCheck = currentR - range;
                for (int g = -range; g <= range; g++){
                    for (int b = -range; b <= range; b++){
                        gToCheck = g + currentG;
                        bToCheck = b + currentB;
                        if (rToCheck >= 0 && gToCheck >= 0 && bToCheck >= 0 && rToCheck <= 255 && gToCheck <= 255 && bToCheck <= 255 &&
                            !colorTracker[rToCheck][gToCheck][bToCheck]){
                            colorPossibilities.add(new Color(rToCheck, gToCheck, bToCheck, 255).getRGB());
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
                            colorPossibilities.add(new Color(rToCheck, gToCheck, bToCheck, 255).getRGB());
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
                            colorPossibilities.add(new Color(rToCheck, gToCheck, bToCheck, 255).getRGB());
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
                            colorPossibilities.add(new Color(rToCheck, gToCheck, bToCheck, 255).getRGB());
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
                            colorPossibilities.add(new Color(rToCheck, gToCheck, bToCheck, 255).getRGB());
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
                            colorPossibilities.add(new Color(rToCheck, gToCheck, bToCheck, 255).getRGB());
                        }
                    }
                }
            } while (colorPossibilities.size() == 0);
            
            xyToAdd = nextPossibilities.get(rand.nextInt(nextPossibilities.size()));
            colorToAdd = colorPossibilities.get(rand.nextInt(colorPossibilities.size()));
            image.setRGB(xyToAdd[0], xyToAdd[1], colorToAdd);
            colorTracker[(colorToAdd >> 16) & 0xFF][(colorToAdd >> 8) & 0xFF][colorToAdd & 0xFF] = true;
            edgeList.add(xyToAdd);
            frame.getContentPane().getComponent(1).repaint();
            counter++;
            if (counter % 2048 == 0){
                System.out.printf("%4f\n", (double) counter / (double) colors * 100);
            }
        }
        frame.getContentPane().getComponent(1).repaint();
        toFile(image);
        System.out.println("Done.");
    }
    
    
    public static void snake3() throws IOException{
        int colors = 16777216;
        int width = 4096;
        int height = 4096;
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
        
        XORShiftRandom rand = new XORShiftRandom();
        int startX = rand.nextInt(width);
        int startY = rand.nextInt(height);
        //startX = 2048;
        //startY = 2048;
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
        int range;
        while (edgeList.size() != 0){
            int index = -1;
            for (int i = 0; i < edgeList.size(); i++){
                if (rand.nextInt(500) < 1){
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
            range = 0;
            do{
                range++;
                rToCheck = currentR - range;
                for (int g = -range; g <= range; g++){
                    for (int b = -range; b <= range; b++){
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
                rToCheck = currentR + range;
                for (int g = -range; g <= range; g++){
                    for (int b = -range; b <= range; b++){
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
                gToCheck = currentG - range;
                for (int r = -range + 1; r <= range - 1; r++){
                    for (int b = -range; b <= range; b++){
                        rToCheck = r + currentR;
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
                gToCheck = currentG + range;
                for (int r = -range + 1; r <= range - 1; r++){
                    for (int b = -range; b <= range; b++){
                        rToCheck = r + currentR;
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
                bToCheck = currentB - range;
                for (int r = -range + 1; r <= range - 1; r++){
                    for (int g = -range + 1; g <= range - 1; g++){
                        rToCheck = r + currentR;
                        gToCheck = g + currentG;
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
                bToCheck = currentB + range;
                for (int r = -range + 1; r <= range - 1; r++){
                    for (int g = -range + 1; g <= range - 1; g++){
                        rToCheck = r + currentR;
                        gToCheck = g + currentG;
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
        }
        frame.getContentPane().add(new JLabel(new ImageIcon(image)));
        frame.setVisible(true);
        toFile(image);
        System.out.println("Done.");
    }
    
    
    
    public static void clouds() throws IOException{
        int colors = 16777216;
        Scanner in = new Scanner(System.in);
        System.out.print("Width: ");
        int width = in.nextInt();
        System.out.print("Height: ");
        int height = in.nextInt();
        System.out.print("Rear percentage (0-100): ");
        int rearPercent = in.nextInt();
        System.out.print("Individual percent (0-100): ");
        int indPercent = in.nextInt();
        
        // [r][g][b]
        boolean[][][] colorTracker = new boolean[256][256][256];
        for (int i = 0; i < colorTracker.length; i++){
            for (int j = 0; j < colorTracker.length; j++){
                Arrays.fill(colorTracker[i][j], false);
            }
        }
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
        
        XORShiftRandom rand = new XORShiftRandom();
        int startX = rand.nextInt(width);
        int startY = rand.nextInt(height);
        int startR = rand.nextInt(256);
        int startG = rand.nextInt(256);
        int startB = rand.nextInt(256);
        int colorToAdd = new Color(startR, startG, startB, 255).getRGB();
        image.setRGB(startX, startY, colorToAdd);
        colorTracker[startR][startG][startB] = true;
        ArrayList<int[]> edgeList = new ArrayList<int[]>(colors / 8);
        edgeList.add(new int[] {startX, startY});
        frame.getContentPane().add(new JLabel(new ImageIcon(image)));
        frame.setVisible(true);
        
        ArrayList<int[]> nextPossibilities = new ArrayList<int[]>(8);
        
        int index; int currentX; int currentY; int xToCheck; int yToCheck; int rToCheck; int gToCheck; int bToCheck; int[] xyToAdd = null; int[] coords = null; int range; int counter = 1;
        ArrayList<Integer> colorPossibilities = new ArrayList<Integer>();
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
            coords = edgeList.get(index);
            currentX = coords[0];
            currentY = coords[1];
            nextPossibilities.clear();
            for (int x = -1; x <= 1; x++){
                for (int y = -1; y <= 1; y++){
                    xToCheck = currentX + x;
                    yToCheck = currentY + y;
                    try{
                        if (image.getRGB(xToCheck, yToCheck) == -33554432) {
                            nextPossibilities.add(new int[] {xToCheck, yToCheck});
                        }
                    } catch (ArrayIndexOutOfBoundsException e){}
                }
            }
            if (nextPossibilities.size() == 0){
                edgeList.remove(index);
                continue;
            }
            edgeList.add(edgeList.get(index));
            edgeList.remove(index);
            
            int rgb = image.getRGB(currentX, currentY);
            int currentR = (rgb >> 16) & 0xFF;
            int currentG = (rgb >> 8) & 0xFF;
            int currentB = rgb & 0xFF;
            range = 0;
            colorPossibilities.clear();
            do{
                range++;
                rToCheck = currentR - range;
                for (int g = -range; g <= range; g++){
                    for (int b = -range; b <= range; b++){
                        gToCheck = g + currentG;
                        bToCheck = b + currentB;
                        if (rToCheck >= 0 && gToCheck >= 0 && bToCheck >= 0 && rToCheck <= 255 && gToCheck <= 255 && bToCheck <= 255 &&
                            !colorTracker[rToCheck][gToCheck][bToCheck]){
                            colorPossibilities.add(new Color(rToCheck, gToCheck, bToCheck, 255).getRGB());
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
                            colorPossibilities.add(new Color(rToCheck, gToCheck, bToCheck, 255).getRGB());
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
                            colorPossibilities.add(new Color(rToCheck, gToCheck, bToCheck, 255).getRGB());
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
                            colorPossibilities.add(new Color(rToCheck, gToCheck, bToCheck, 255).getRGB());
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
                            colorPossibilities.add(new Color(rToCheck, gToCheck, bToCheck, 255).getRGB());
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
                            colorPossibilities.add(new Color(rToCheck, gToCheck, bToCheck, 255).getRGB());
                        }
                    }
                }
            } while (colorPossibilities.size() == 0);
            
            xyToAdd = nextPossibilities.get(rand.nextInt(nextPossibilities.size()));
            colorToAdd = colorPossibilities.get(rand.nextInt(colorPossibilities.size()));
            image.setRGB(xyToAdd[0], xyToAdd[1], colorToAdd);
            colorTracker[(colorToAdd >> 16) & 0xFF][(colorToAdd >> 8) & 0xFF][colorToAdd & 0xFF] = true;
            edgeList.add(xyToAdd);
            frame.getContentPane().getComponent(1).repaint();
            counter++;
            if (counter % 4096 == 0){
                System.out.printf("%4f\n", (double) counter / (double) colors * 100);
            }
        }
        frame.getContentPane().getComponent(1).repaint();
        toFile(image);
        System.out.println("Done.");
    }
    
    
    public static void directional() throws IOException{
        XORShiftRandom rand = new XORShiftRandom();
        int colors = 16777216;
        int width = 1920 * 2;
        int height = 1080;
        Scanner in = new Scanner(System.in);
        System.out.print("Rear percentage (0-100): ");
        int rearPercent = rand.nextInt(40) + 1;
        System.out.print("Individual percent (0-1000): ");
        int indPercent = rand.nextInt(150) + 1;
        System.out.print("Direction choosing %: ");
        int dirPercent = rand.nextInt(50) + 50;
        System.out.print("Curl: ");
        float curl = (float)rand.nextInt(32) / (float)4;
        System.out.print("Shape Factor: ");
        int shapeFactor = rand.nextInt(400);
        System.out.print("Flip chance 1 in ");
        int flipChance = rand.nextInt(100000);
        
        /* System.out.print("Rear percentage (0-100): ");
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
        int flipChance = in.nextInt(); */
        
        // [r][g][b]
        //boolean[][][] colorTracker = getColorArray(height, width, rand);
        boolean[][][] colorTracker = fillColorArray();
        
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        BufferedImage resized = new BufferedImage(width / 4, height / 4, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setPaint(new Color (0, 0, 0, 254));
        graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
        graphics = resized.createGraphics();
        graphics.setPaint(new Color (0, 0, 0, 255));
        graphics.fillRect(0, 0, resized.getWidth(), resized.getHeight());
        
        JFrame frame = new JFrame("ColorGen");
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JLabel emptyLabel = new JLabel("");
        emptyLabel.setPreferredSize(new Dimension(width / 4, height / 4));
        frame.getContentPane().add(emptyLabel, BorderLayout.CENTER);
 
        //Display the window.
        frame.pack();
        frame.setVisible(true);
        
        DirectionalPixel pixelToAdd = new DirectionalPixel(rand, width, height, shapeFactor);
        
        ArrayList<DirectionalPixel> edgeList = new ArrayList<DirectionalPixel>(colors / 10);
        edgeList.add(pixelToAdd);
        
        int startR = rand.nextInt(256);
        int startG = rand.nextInt(256);
        int startB = rand.nextInt(256);
        int colorToAdd = new Color(startR, startG, startB, 255).getRGB();
        
        image.setRGB(pixelToAdd.getX(), pixelToAdd.getY(), colorToAdd);
        colorTracker[startR][startG][startB] = true;
        
        frame.getContentPane().add(new JLabel(new ImageIcon(resized)));
        frame.setVisible(true);
        
        ArrayList<Integer> colorPossibilities = new ArrayList<Integer>(50);
        ArrayList<DirectionalPixel> nextPossibilities = new ArrayList<DirectionalPixel>(8);
        
        int index; DirectionalPixel toAddTo = null; int counter = 1; boolean passed = true; boolean firstWay = true;
        while (edgeList.size() != 0){
            
            index = 0;
            if (passed){
                if (rand.nextInt(100) < rearPercent){
                    for (int i = 0; i < edgeList.size(); i++){
                        if (rand.nextInt(100) < indPercent){
                            index = i;
                            firstWay = true;
                            break;
                        }
                    }
                } else {
                    for (int i = edgeList.size() - 1; i >= 0; i--){
                        if (rand.nextInt(100) < indPercent){
                            index = i;
                            firstWay = false;
                            break;
                        }
                    }
                }
            } else {
                if (firstWay){
                    for (int i = 0; i < edgeList.size(); i++){
                        if (rand.nextInt(100) < indPercent){
                            index = i;
                            firstWay = true;
                            break;
                        }
                    }
                } else {
                    for (int i = edgeList.size() - 1; i >= 0; i--){
                        if (rand.nextInt(100) < indPercent){
                            index = i;
                            firstWay = false;
                            break;
                        }
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
                passed = false;
                continue;
            }
            passed = true;
            edgeList.add(toAddTo);
            edgeList.remove(index);
            
            int rgb = image.getRGB(toAddTo.getX(), toAddTo.getY());
            
            getColorList(rgb, colorPossibilities, colorTracker, rand);
            
            
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
                System.out.printf("%4f %d\n", (double) counter / (double) colors * 100, edgeList.size());
            }
        }
        frame.getContentPane().getComponent(1).repaint();
        toFile(image, rand, width, height);
        System.out.println("Done.");
        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
        return;
    }
    
    
    public static void linkTest() throws IOException{
        XORShiftRandom rand = new XORShiftRandom();
        int colors = 16777216;
        int width = 4096;
        int height = 4096;
        Scanner in = new Scanner(System.in);
        /* System.out.print("Rear percentage (0-100): ");
        int rearPercent = rand.nextInt(100) + 1; */
       /*  System.out.print("Individual percent (0-1000): ");
        int indPercent = rand.nextInt(100) + 1;
        System.out.print("Direction choosing %: ");
        int dirPercent = rand.nextInt(30) + 70;
        System.out.print("Curl: ");
        float curl = (float)rand.nextInt(32) / (float)4;
        System.out.print("Shape Factor: ");
        int shapeFactor = rand.nextInt(500);
        System.out.print("Flip chance 1 in ");
        int flipChance = rand.nextInt(100000); */
        
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
        
        // [r][g][b]
        boolean[][][] colorTracker = getColorArray(height, width, rand);
        
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        BufferedImage resized = new BufferedImage(width / 4, height / 4, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setPaint(new Color (0, 0, 0, 254));
        graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
        graphics = resized.createGraphics();
        graphics.setPaint(new Color (0, 0, 0, 255));
        graphics.fillRect(0, 0, resized.getWidth(), resized.getHeight());
        
        JFrame frame = new JFrame("ColorGen");
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JLabel emptyLabel = new JLabel("");
        emptyLabel.setPreferredSize(new Dimension(width / 4, height / 4));
        frame.getContentPane().add(emptyLabel, BorderLayout.CENTER);
 
        //Display the window.
        frame.pack();
        frame.setVisible(true);
        
        DirectionalPixel pixelToAdd = new DirectionalPixel(rand, width, height, shapeFactor);
        
        LinkedList<DirectionalPixel> edgeList = new LinkedList<DirectionalPixel>();
        edgeList.add(pixelToAdd);
        
        int startR = rand.nextInt(256);
        int startG = rand.nextInt(256);
        int startB = rand.nextInt(256);
        int colorToAdd = new Color(startR, startG, startB, 255).getRGB();
        
        image.setRGB(pixelToAdd.getX(), pixelToAdd.getY(), colorToAdd);
        colorTracker[startR][startG][startB] = true;
        
        frame.getContentPane().add(new JLabel(new ImageIcon(resized)));
        frame.setVisible(true);
        
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
            
            toAddTo = edgeList.remove(index); 
            toAddTo.getNextPossibilities(image, nextPossibilities, curl);
            if (nextPossibilities.size() == 0){
                continue;
            }
            
            int rgb = image.getRGB(toAddTo.getX(), toAddTo.getY());
            
            getColorList(rgb, colorPossibilities, colorTracker, rand);
            
            
            /* int tmp = 0;
            for (int i = 0; i < nextPossibilities.size(); i++){
                if (rand.nextInt(100) < dirPercent){
                    tmp = i;
                    break;
                }
            }
            pixelToAdd = nextPossibilities.get(tmp); */
            
            pixelToAdd = nextPossibilities.get(rand.nextInt(nextPossibilities.size()));
            
            colorToAdd = colorPossibilities.get(rand.nextInt(colorPossibilities.size()));
            image.setRGB(pixelToAdd.getX(), pixelToAdd.getY(), colorToAdd);
            colorTracker[(colorToAdd >> 16) & 0xFF][(colorToAdd >> 8) & 0xFF][colorToAdd & 0xFF] = true;
            
            edgeList.add(pixelToAdd);
            edgeList.add(toAddTo);
            
            
            if (pixelToAdd.getX() % 4 == 0 && pixelToAdd.getY() % 4 == 0){
                resized.setRGB(pixelToAdd.getX() / 4, pixelToAdd.getY() / 4, colorToAdd);
                frame.getContentPane().getComponent(1).repaint();
            }
            counter++;
            if (counter % 4096 == 0){
                System.out.printf("%4f %d\n", (double) counter / (double) colors * 100, edgeList.size());
            }
        }
        frame.getContentPane().getComponent(1).repaint();
        toFile(image, rand);
        System.out.println("Done.");
        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
        return;
    }
    
    
    
    
    
    
    
    public static void combo() throws IOException{
        XORShiftRandom rand = new XORShiftRandom();
        int colors = 16777216;
        int width = 1920 * 2;
        int height = 1080;
        Scanner in = new Scanner(System.in);
        
        //System.out.print("Rear percentage (0-100): ");
        //int rearPercent = in.nextInt();
       /*  System.out.print("Individual percent (0-1000): ");
        int indPercent = in.nextInt();
        System.out.print("Direction choosing %: ");
        int dirPercent = in.nextInt();
        System.out.print("Curl: ");
        float curl = in.nextFloat();
        System.out.print("Shape Factor: ");
        int shapeFactor = in.nextInt();
        System.out.print("Flip chance 1 in ");
        int flipChance = in.nextInt();
        System.out.print("Mode change chance 1 in ");
        int modeChance = in.nextInt(); */
        
        //System.out.print("Rear percentage (0-100): ");
        //int rearPercent = in.nextInt();
        System.out.print("Individual percent (0-1000): ");
        int indPercent = rand.nextInt(50) + 1;
        System.out.print("Direction choosing %: ");
        int dirPercent = 80;
        System.out.print("Curl: ");
        float curl = (float)rand.nextInt(16) / (float)4;
        System.out.print("Shape Factor: ");
        int shapeFactor = rand.nextInt(400);
        System.out.print("Flip chance 1 in ");
        int flipChance = rand.nextInt(2000) + 1;
        System.out.print("Mode change chance 1 in ");
        int modeChance = rand.nextInt(2000) + 1;
        
        // [r][g][b]
        boolean[][][] colorTracker = getColorArray(height, width, rand);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        BufferedImage resized = new BufferedImage(width / 4, height / 4, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setPaint(new Color (0, 0, 0, 254));
        graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
        graphics = resized.createGraphics();
        graphics.setPaint(new Color (0, 0, 0, 255));
        graphics.fillRect(0, 0, resized.getWidth(), resized.getHeight());
        
        JFrame frame = new JFrame("ColorGen");
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JLabel emptyLabel = new JLabel("");
        emptyLabel.setPreferredSize(new Dimension(width / 4, height / 4));
        frame.getContentPane().add(emptyLabel, BorderLayout.CENTER);
 
        //Display the window.
        frame.pack();
        frame.setVisible(true);
        
        DirectionalPixel pixelToAdd = new DirectionalPixel(rand, width, height, shapeFactor);
        
        ArrayList<DirectionalPixel> edgeList = new ArrayList<DirectionalPixel>(colors / 10);
        edgeList.add(pixelToAdd);
        
        int startR = rand.nextInt(256);
        int startG = rand.nextInt(256);
        int startB = rand.nextInt(256);
        int colorToAdd = new Color(startR, startG, startB, 255).getRGB();
        
        System.out.println(pixelToAdd.getX() + " " + pixelToAdd.getY());
        image.setRGB(pixelToAdd.getX(), pixelToAdd.getY(), colorToAdd);
        colorTracker[startR][startG][startB] = true;
        
        frame.getContentPane().add(new JLabel(new ImageIcon(resized)));
        frame.setVisible(true);
        
        if (pixelToAdd.getX() % 4 == 0 && pixelToAdd.getY() % 4 == 0){
            resized.setRGB(pixelToAdd.getX() / 4, pixelToAdd.getY() / 4, colorToAdd);
            frame.getContentPane().getComponent(1).repaint();
        }
        
        ArrayList<Integer> colorPossibilities = new ArrayList<Integer>(50);
        ArrayList<DirectionalPixel> nextPossibilities = new ArrayList<DirectionalPixel>(8);
        
        int index; DirectionalPixel toAddTo = null; int counter = 1; boolean mode = true;
        while (edgeList.size() != 0){
            
            index = 0;
            if (mode){
                for (int i = edgeList.size() - 1; i >= 0; i--){
                    if (rand.nextInt(100) < indPercent){
                        index = i;
                        break;
                    }
                }
            } else {
                index = rand.nextInt(edgeList.size());
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
            
            if (rand.nextInt(modeChance) < 1){
                if (mode){
                    mode = false;
                } else {
                    if (rand.nextInt(3) < 1){
                        mode = true;
                    }
                }
            }
            
            if (rand.nextInt(flipChance) < 1){
                curl = rand.nextInt(8);
            }
            
            edgeList.add(toAddTo);
            edgeList.remove(index);
            
            int rgb = image.getRGB(toAddTo.getX(), toAddTo.getY());
            
            getColorList(rgb, colorPossibilities, colorTracker, rand);
            
            
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
                System.out.printf("%4f %d\n", (double) counter / (double) colors * 100, edgeList.size());
            }
        }
        frame.getContentPane().getComponent(1).repaint();
        toFile(image, rand);
        System.out.println("Done.");
        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
        return;
    }
    
    
    
    
    
    
    public static void toFile(BufferedImage i) throws IOException{
        File out = new File("images\\" + System.currentTimeMillis() + ".png");
        ImageIO.write(i, "PNG", out);
    }
    
    
    public static void toFile(BufferedImage i, XORShiftRandom rand, int width, int height) throws IOException{
        File out = new File("images\\" + width + "x" + height + "\\" + System.currentTimeMillis() + rand.nextInt(100000) + ".png");
        ImageIO.write(i, "PNG", out);
    }
    
    
    public static void toFile(BufferedImage i, XORShiftRandom rand) throws IOException{
        File out = new File("images\\" + System.currentTimeMillis() + rand.nextInt(100000) + ".png");
        ImageIO.write(i, "PNG", out);
    }
    
    public static void getColorList(int rgb, ArrayList<Integer> colorPossibilities, boolean[][][] colorTracker, XORShiftRandom rand){
        int rToCheck;
        int bToCheck;
        int gToCheck;
        int currentR = (rgb >> 16) & 0xFF;
        int currentG = (rgb >> 8) & 0xFF;
        int currentB = rgb & 0xFF;
        int range = 0;
        colorPossibilities.clear();
        do{
            range++;
            rToCheck = currentR - range;
            if (rToCheck >= 0){
                for (int g = -range; g <= range; g++){
                    for (int b = -range; b <= range; b++){
                        gToCheck = g + currentG;
                        bToCheck = b + currentB;
                        if (gToCheck >= 0 && bToCheck >= 0 && gToCheck <= 255 && bToCheck <= 255 &&
                            !colorTracker[rToCheck][gToCheck][bToCheck]){
                            colorPossibilities.add(new Color(rToCheck, gToCheck, bToCheck, 255).getRGB());
                        }
                    }
                }
            }
            rToCheck = currentR + range;
            if (rToCheck <= 255){
                for (int g = -range; g <= range; g++){
                    for (int b = -range; b <= range; b++){
                        gToCheck = g + currentG;
                        bToCheck = b + currentB;
                        if (gToCheck >= 0 && bToCheck >= 0 && gToCheck <= 255 && bToCheck <= 255 &&
                            !colorTracker[rToCheck][gToCheck][bToCheck]){
                            colorPossibilities.add(new Color(rToCheck, gToCheck, bToCheck, 255).getRGB());
                        }
                    }
                }
            }
            gToCheck = currentG - range;
            if(gToCheck >= 0){
                for (int r = -range + 1; r <= range - 1; r++){
                    for (int b = -range; b <= range; b++){
                        rToCheck = r + currentR;
                        bToCheck = b + currentB;
                        if (rToCheck >= 0 && bToCheck >= 0 && rToCheck <= 255 && bToCheck <= 255 &&
                            !colorTracker[rToCheck][gToCheck][bToCheck]){
                            colorPossibilities.add(new Color(rToCheck, gToCheck, bToCheck, 255).getRGB());
                        }
                    }
                }
            }
            gToCheck = currentG + range;
            if(gToCheck <= 255){
                for (int r = -range + 1; r <= range - 1; r++){
                    for (int b = -range; b <= range; b++){
                        rToCheck = r + currentR;
                        bToCheck = b + currentB;
                        if (rToCheck >= 0 && bToCheck >= 0 && rToCheck <= 255 && bToCheck <= 255 &&
                            !colorTracker[rToCheck][gToCheck][bToCheck]){
                            colorPossibilities.add(new Color(rToCheck, gToCheck, bToCheck, 255).getRGB());
                        }
                    }
                }
            }
            bToCheck = currentB - range;
            if (bToCheck >= 0){
                for (int r = -range + 1; r <= range - 1; r++){
                    for (int g = -range + 1; g <= range - 1; g++){
                        rToCheck = r + currentR;
                        gToCheck = g + currentG;
                        if (rToCheck >= 0 && gToCheck >= 0 && rToCheck <= 255 && gToCheck <= 255 &&
                            !colorTracker[rToCheck][gToCheck][bToCheck]){
                            colorPossibilities.add(new Color(rToCheck, gToCheck, bToCheck, 255).getRGB());
                        }
                    }
                }
            }
            bToCheck = currentB + range;
            if (bToCheck <= 255){
                for (int r = -range + 1; r <= range - 1; r++){
                    for (int g = -range + 1; g <= range - 1; g++){
                        rToCheck = r + currentR;
                        gToCheck = g + currentG;
                        if (rToCheck >= 0 && gToCheck >= 0 && rToCheck <= 255 && gToCheck <= 255 &&
                            !colorTracker[rToCheck][gToCheck][bToCheck]){
                            colorPossibilities.add(new Color(rToCheck, gToCheck, bToCheck, 255).getRGB());
                        }
                    }
                }
            }
        } while (colorPossibilities.size() == 0);
    }
    
    public static boolean[][][] getColorArray(int height, int width, XORShiftRandom rand){
        boolean[][][] colorTracker = new boolean[256][256][256];
        int pixels = height * width;
        if (pixels == 4096 * 4096){
            colorTracker = fillColorArray();
            return colorTracker;
        }
        int count = 0;
        for (int i = 0; i < colorTracker.length; i++){
            for (int j = 0; j < colorTracker.length; j++){
                Arrays.fill(colorTracker[i][j], true);
            }
        }
        int r, g, b;
        while (count < pixels){
            r = rand.nextInt(256);
            g = rand.nextInt(256);
            b = rand.nextInt(256);
            if (colorTracker[r][g][b]){
                colorTracker[r][g][b] = false;
                count++;
            }
        }
        return colorTracker;
    }
    
    public static boolean[][][] fillColorArray(){
        boolean[][][] colorTracker = new boolean[256][256][256];
        for (int i = 0; i < colorTracker.length; i++){
            for (int j = 0; j < colorTracker.length; j++){
                Arrays.fill(colorTracker[i][j], false);
            }
        }
        return colorTracker;
    }
}















