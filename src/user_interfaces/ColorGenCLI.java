package user_interfaces;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import random_image_generators.Directional;
import random_image_generators.DirectionalWithIterator;
import random_image_generators.FastIterator;
import random_image_generators.RandomImage;
import rng.XORShiftRandom;

public class ColorGenCLI {
    
    private static JFrame frame;
    
    public static void main(String[] args){
        System.out.print("Which method? ");
        Scanner in = new Scanner(System.in);
        String method = in.next();
        
        if (method.equals("directional")){
            in.close();
            for(;;){
                makeDirectional();
            }
        }
        if (method.equals("iterator")){
            in.close();
            for(;;){
                makeDirectionalWithIterator();
            }
        }
        if (method.equals("fast")){
            in.close();
            for(;;){
                makeFastIterator();
            }
        }
        in.close();
    }
    
    private static void makeFastIterator() {
        //Scanner in = null;
        for(;;){
            int colors = 16777216;
            int width = 4096;
            int height = 4096;
            
            /*in = new Scanner(System.in);
            System.out.print("Rear percentage (0-100): ");
            int rearPercent = in.nextInt();
            System.out.print("Individual percent (0-1000): ");
            int individualPercent = in.nextInt();
            System.out.print("Direction choosing %: ");
            int directionPercent = in.nextInt();
            System.out.print("Curl: ");
            float curl = in.nextFloat();
            System.out.print("Shape Factor: ");
            int shapeFactor = in.nextInt();
            System.out.print("Flip chance 1 in ");
            int flipChance = in.nextInt();
            System.out.print("Scale factor: ");
            int scaleFactor = in.nextInt();
            in.close();*/
            
            XORShiftRandom rand = new XORShiftRandom();
            System.out.print("Individual percent (0-1000): ");
            int individualPercent = 10 + rand.nextInt(80);
            System.out.print("Shape Factor: ");
            int shapeFactor = rand.nextInt(2000);
            System.out.print("Scale factor: ");
            int scaleFactor = 4;
            
            RandomImage image = new FastIterator(colors, width, height, scaleFactor,
                                                individualPercent,  shapeFactor);
            BufferedImage display = image.getResizedImage();
            
            setupGraphics(display, width / scaleFactor, height / scaleFactor);
            
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
    }

    private static void makeDirectional(){
        //Scanner in = null;
        for(;;){
            int colors = 16777216;
            int width = 4096;
            int height = 4096;
            
            /*in = new Scanner(System.in);
            System.out.print("Rear percentage (0-100): ");
            int rearPercent = in.nextInt();
            System.out.print("Individual percent (0-1000): ");
            int individualPercent = in.nextInt();
            System.out.print("Direction choosing %: ");
            int directionPercent = in.nextInt();
            System.out.print("Curl: ");
            float curl = in.nextFloat();
            System.out.print("Shape Factor: ");
            int shapeFactor = in.nextInt();
            System.out.print("Flip chance 1 in ");
            int flipChance = in.nextInt();
            System.out.print("Scale factor: ");
            int scaleFactor = in.nextInt();
            in.close();*/
            
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
            
            RandomImage image = new Directional(colors, width, height, scaleFactor, rearPercent,
                                                individualPercent, curl, flipChance, directionPercent, shapeFactor);
            BufferedImage display = image.getResizedImage();
            
            setupGraphics(display, width / scaleFactor, height / scaleFactor);
            
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
        }
    }
    
    private static void makeDirectionalWithIterator(){
        //Scanner in = null;
        for(;;){
            int colors = 16777216;
            int width = 4096;
            int height = 4096;
            
            /*in = new Scanner(System.in);
            System.out.print("Rear percentage (0-100): ");
            int rearPercent = in.nextInt();
            System.out.print("Individual percent (0-1000): ");
            int individualPercent = in.nextInt();
            System.out.print("Direction choosing %: ");
            int directionPercent = in.nextInt();
            System.out.print("Curl: ");
            float curl = in.nextFloat();
            System.out.print("Shape Factor: ");
            int shapeFactor = in.nextInt();
            System.out.print("Flip chance 1 in ");
            int flipChance = in.nextInt();
            System.out.print("Scale factor: ");
            int scaleFactor = in.nextInt();
            in.close();*/
            
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
            
            RandomImage image = new DirectionalWithIterator(colors, width, height, scaleFactor,
                                                individualPercent, curl, flipChance, directionPercent, shapeFactor);
            BufferedImage display = image.getResizedImage();
            
            setupGraphics(display, width / scaleFactor, height / scaleFactor);
            
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
    }
    
    private static void setupGraphics(BufferedImage display, int width, int height){
        frame = new JFrame("ColorGen");
        JLabel emptyLabel = new JLabel("");
        emptyLabel.setPreferredSize(new Dimension(width, height));
        frame.getContentPane().add(emptyLabel, BorderLayout.CENTER);
 
        //Display the window.
        frame.pack();
        frame.setVisible(true);
        
        frame.getContentPane().add(new JLabel(new ImageIcon(display)));
        frame.setVisible(true);
    }
}
