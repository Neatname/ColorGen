package random_image_generators;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

import pixels.DirectionalPixel;
import rng.XORShiftRandom;

public abstract class RandomImage {
    /** this is the number of colors representable in 32 bits. */
    public static final int MAX_COLORS = 16777216;
    
    protected int colors;
    
    protected int width;
    
    protected int height;
    
    protected int[][][] colorTracker;
    
    protected BufferedImage image;
    
    private BufferedImage resizedImage;
    
    private int scaleFactor;
    
    protected List<DirectionalPixel> edgeList;
    
    protected XORShiftRandom rand;
    
    
    public RandomImage(int colors, int width, int height, int scaleFactor){
        if (colors > MAX_COLORS || width * height > MAX_COLORS){
            throw new IllegalArgumentException();
        }
        this.colors = colors;
        this.width = width;
        this.height = height;
        this.scaleFactor = scaleFactor;
        rand = new XORShiftRandom();
        
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setPaint(new Color (0, 0, 0, 254));
        graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
        
        resizedImage = new BufferedImage(width / scaleFactor, height / scaleFactor, BufferedImage.TYPE_INT_ARGB);
        graphics = resizedImage.createGraphics();
        graphics.setPaint(new Color (0, 0, 0, 255));
        graphics.fillRect(0, 0, resizedImage.getWidth(), resizedImage.getHeight());
        
        setupColorTracker();
        edgeList = new LinkedList<DirectionalPixel>();
    }
    
    public BufferedImage getImage(){
        return image;
    }
    
    public BufferedImage getResizedImage(){
        return resizedImage;
    }
    
    private void setupColorTracker(){
        colorTracker = new int[256][256][256];
        for (int r = 0; r <= 255; r++){
            for (int g = 0; g<= 255; g++){
                for (int b = 0; b <= 255; b++){
                    colorTracker[r][g][b] = new Color(r, g, b, 255).getRGB();
                }
            }
        }
    }
    
    public void saveImage(String path){
        File out = new File(path);
        try {
            ImageIO.write(image, "PNG", out);
        } catch (IOException e){
            System.out.println("Image save failed, exiting...");
            System.exit(0);
        }
    }
    
    protected void updateImages(int x, int y, int color){
        image.setRGB(x, y, color);
        
        if (x % scaleFactor == 0 && y % scaleFactor == 0){
            resizedImage.setRGB(x / scaleFactor, y / scaleFactor, color);
        }
    }
    
    public List<Integer> getClosestColors(int colorToMatch){
        List<Integer> closestColors = new ArrayList<Integer>();
        int rToCheck;
        int bToCheck;
        int gToCheck;
        int currentR = (colorToMatch >> 16) & 0xFF;
        int currentG = (colorToMatch >> 8) & 0xFF;
        int currentB = colorToMatch & 0xFF;
        int range = 0;
        do{
            range++;
            rToCheck = currentR - range;
            if (rToCheck >= 0){
                for (int g = -range; g <= range; g++){
                    for (int b = -range; b <= range; b++){
                        gToCheck = g + currentG;
                        bToCheck = b + currentB;
                        if (gToCheck >= 0 && bToCheck >= 0 && gToCheck <= 255 && bToCheck <= 255 &&
                            colorTracker[rToCheck][gToCheck][bToCheck] != 0){
                            closestColors.add(colorTracker[rToCheck][gToCheck][bToCheck]);
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
                            colorTracker[rToCheck][gToCheck][bToCheck] != 0){
                            closestColors.add(colorTracker[rToCheck][gToCheck][bToCheck]);
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
                            colorTracker[rToCheck][gToCheck][bToCheck] != 0){
                            closestColors.add(colorTracker[rToCheck][gToCheck][bToCheck]);
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
                            colorTracker[rToCheck][gToCheck][bToCheck] != 0){
                            closestColors.add(colorTracker[rToCheck][gToCheck][bToCheck]);
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
                            colorTracker[rToCheck][gToCheck][bToCheck] != 0){
                            closestColors.add(colorTracker[rToCheck][gToCheck][bToCheck]);
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
                            colorTracker[rToCheck][gToCheck][bToCheck] != 0){
                            closestColors.add(colorTracker[rToCheck][gToCheck][bToCheck]);
                        }
                    }
                }
            }
        } while (closestColors.size() == 0);
        return closestColors;
    }
    
    public boolean isFinished(){
        return edgeList.size() == 0;
    }
    
    public abstract void nextPixel();
    
    public int getQueueSize(){
        return edgeList.size();
    }
}
