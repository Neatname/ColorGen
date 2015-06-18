import java.util.*;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.*;
import java.awt.Frame;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
public class ColorGen {
    
    /** this is the number of colors representable in 32 bits. */
    public static final int MAX_COLORS = 16777216;
    
    public static void main (String[] args){
        System.out.print("Which method? ");
        Scanner in = new Scanner(System.in);
        String method = in.next();
        if (method.equals("random")){
            random();
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
    
    // this is a draft method, I'll finish it later, just ignore it. If you uncomment it, it won't compile.
    /* public static void growing(){
        boolean[] colorArray = new boolean[colors + 1];
        Arrays.fill(colorArray, false);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        JFrame frame = new JFrame("Something");
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
        int startColor = rand.nextInt(colors) * (-64);
        image.setRGB(startX, startY, startColor);
        frame.getContentPane().add(new JLabel(new ImageIcon(image)));
        frame.setVisible(true);
        
        ArrayList<Array> queue = new ArrayList<Array>(colors / 10);
        // this adds appropriate pixels to the queue
        if (startX - 1 < 0)
        // shit's not done, finish it
        ArrayList<Array> lastX = new ArrayList<Array>(LASTX);
        
        for (int i = 1; i <= colors; i++){
            
        }
    } */
}