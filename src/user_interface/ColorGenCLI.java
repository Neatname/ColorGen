package user_interface;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import random_image_generators.*;
import rng.XORShiftRandom;

public class ColorGenCLI {
    
    private JFrame frame;
    private int colors = 16777216;
    private int width = 4096;
    private int height = 4096;
    private RandomImage image;
    private BufferedImage display;
    
    public ColorGenCLI(){
        System.out.print("Which method? ");
        Scanner in = new Scanner(System.in);
        String method = in.next();
        in.close();
        
        for(;;){
            if (method.equals("directional")){
                makeDirectional();
            } else if (method.equals("iterator")){
                makeDirectionalWithIterator();
            } else if (method.equals("fast")){
                makeFastIterator();
            } else if (method.equals("faster")){
                makeFasterIterator();
            } else if (method.equals("double")){
                makeDoubleIterator();
            } else if (method.equals("switch")){
                makeSwitchIterator();
            }
            runAlgorithm();
        }
    }
    
    private void runAlgorithm(){
        display = image.getResizedImage();
        setupGraphics();
        
        int counter = 0;
        while(!image.isFinished()){
            image.nextPixel();
            frame.getContentPane().getComponent(1).repaint();
            counter++;
            if (counter % 4096 == 0){
                System.out.printf("%4f %d\n", (double) counter / (double) colors * 100, image.getQueueSize());
            }
        }
        
        image.saveImage("images\\" + System.currentTimeMillis() + ".png");
        System.out.println("Done.");
        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
    }
    
    public static void main(String[] args){
        new ColorGenCLI();
    }
    
    private void makeFastIterator() {
        XORShiftRandom rand = new XORShiftRandom();
        System.out.print("Individual percent (0-1000): ");
        int individualPercent = 10 + rand.nextInt(60);
        System.out.print("Shape Factor: ");
        int shapeFactor = rand.nextInt(2000);
        System.out.print("Scale factor: ");
        int scaleFactor = 4;
        
        image = new FastIterator(colors, width, height, scaleFactor,
                                            individualPercent,  shapeFactor);
    }
    
    private void makeSwitchIterator() {
        int minPercent = 10;
        int maxPercent = 70;
        System.out.print("Scale factor: ");
        int scaleFactor = 4;
        
        image = new SwitchIterator(colors, width, height, scaleFactor, maxPercent, minPercent);
    }
    
    private void makeFasterIterator() {
        XORShiftRandom rand = new XORShiftRandom();
        System.out.print("Individual percent (0-100X0): ");
        int individualPercent = 10 + rand.nextInt(80);
        System.out.print("Shape Factor: ");
        int shapeFactor = rand.nextInt(2000);
        System.out.print("Scale factor: ");
        int scaleFactor = 4;
        
        image = new FasterIterator(colors, width, height, scaleFactor,
                                            individualPercent,  shapeFactor);
    }

    private void makeDirectional(){
        System.out.print("Rear percentage (0-100): ");
        int rearPercent = 40;
        System.out.print("Individual percent (0-1000): ");
        int individualPercent = 10;
        System.out.print("Direction choosing %: ");
        int directionPercent = 80;
        System.out.print("Curl: ");
        float curl = 1;
        System.out.print("Shape Factor: ");
        int shapeFactor = 800;
        System.out.print("Flip chance 1 in ");
        int flipChance = 1000000;
        System.out.print("Scale factor: ");
        int scaleFactor = 4;
        
        image = new Directional(colors, width, height, scaleFactor, rearPercent,
                                individualPercent, curl, flipChance, directionPercent, shapeFactor);
    }
    
    private void makeDoubleIterator(){
        XORShiftRandom rand = new XORShiftRandom();
        System.out.print("Individual percent (0-1000): ");
        int individualPercent = 20 + rand.nextInt(80);
        System.out.print("Color percent (0-1000): ");
        int colorPercent = 20 + rand.nextInt(500);
        System.out.print("Direction choosing %: ");
        int directionPercent = 80;
        System.out.print("Curl: ");
        float curl = 1;
        System.out.print("Shape Factor: ");
        int shapeFactor = 300 + rand.nextInt(1000);
        System.out.print("Flip chance 1 in ");
        int flipChance = 1000000;
        System.out.print("Scale factor: ");
        int scaleFactor = 4;
        
        image = new DoubleIterator(colors, width, height, scaleFactor,
                                   individualPercent, curl, flipChance, directionPercent, shapeFactor, colorPercent);
    }
    
    private void makeDirectionalWithIterator(){            
        XORShiftRandom rand = new XORShiftRandom();
        System.out.print("Individual percent (0-1000): ");
        int individualPercent = 10 + rand.nextInt(100);
        System.out.print("Direction choosing %: ");
        int directionPercent = 80;
        System.out.print("Curl: ");
        float curl = rand.nextInt(3);
        System.out.print("Shape Factor: ");
        int shapeFactor = rand.nextInt(2000);
        System.out.print("Flip chance 1 in ");
        int flipChance = rand.nextInt(2000000);
        System.out.print("Scale factor: ");
        int scaleFactor = 4;
        
        image = new DirectionalWithIterator(colors, width, height, scaleFactor,
                                            individualPercent, curl, flipChance, directionPercent, shapeFactor);
    }
    
    private void setupGraphics(){
        frame = new JFrame("ColorGen");
        JLabel emptyLabel = new JLabel("");
        emptyLabel.setPreferredSize(new Dimension(display.getWidth(), display.getHeight()));
        frame.getContentPane().add(emptyLabel, BorderLayout.CENTER);
 
        //Display the window.
        frame.pack();
        frame.setVisible(true);
        
        frame.getContentPane().add(new JLabel(new ImageIcon(display)));
        frame.setVisible(true);
    }
}
