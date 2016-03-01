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
    
    public static final int RGB_VALUES = 256;
    
    public static final int MAX_COLOR_VALUE = 255;
    
    protected int width;
    
    protected int height;
    
    protected int[][][] colorTracker;
    
    protected BufferedImage image;
    
    private BufferedImage resizedImage;
    
    private int scaleFactor;
    
    protected double colorScalar;
    
    protected List<DirectionalPixel> edgeList;
    
    protected XORShiftRandom rand;
    
    
    public RandomImage(int width, int height, int scaleFactor){
        if (width * height > MAX_COLORS){
            throw new IllegalArgumentException();
        }
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
        if (width * height <= MAX_COLORS){
            fillColorTracker();
        } else {
            throw new IllegalArgumentException();
        }
    }
    
    private void fillColorTracker(){
    	int pixels = width * height;
    	int arraySize = 1;
    	while (arraySize * arraySize * arraySize < pixels){
    		arraySize++;
    	}
    	colorScalar = (double) RGB_VALUES / (double) arraySize;
        colorTracker = new int[arraySize][arraySize][arraySize];
        for (int r = 0; r < arraySize; r++){
            for (int g = 0; g < arraySize; g++){
                for (int b = 0; b < arraySize; b++){
                    colorTracker[r][g][b] = new Color((int)(r * colorScalar), (int)(g * colorScalar), (int)(b * colorScalar), MAX_COLOR_VALUE).getRGB();
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
        currentR /= colorScalar;
        currentG /= colorScalar;
        currentB /= colorScalar;
        int range = 0;
        do{
            range++;
            rToCheck = currentR - range;
            if (rToCheck >= 0){
                for (int g = -range; g <= range; g++){
                    for (int b = -range; b <= range; b++){
                        gToCheck = g + currentG;
                        bToCheck = b + currentB;
                        if (gToCheck >= 0 && bToCheck >= 0 && gToCheck < colorTracker.length && bToCheck < colorTracker.length &&
                            colorTracker[rToCheck][gToCheck][bToCheck] != 0){
                            closestColors.add(colorTracker[rToCheck][gToCheck][bToCheck]);
                        }
                    }
                }
            }
            rToCheck = currentR + range;
            if (rToCheck < colorTracker.length){
                for (int g = -range; g <= range; g++){
                    for (int b = -range; b <= range; b++){
                        gToCheck = g + currentG;
                        bToCheck = b + currentB;
                        if (gToCheck >= 0 && bToCheck >= 0 && gToCheck < colorTracker.length && bToCheck < colorTracker.length &&
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
                        if (rToCheck >= 0 && bToCheck >= 0 && rToCheck < colorTracker.length && bToCheck < colorTracker.length &&
                            colorTracker[rToCheck][gToCheck][bToCheck] != 0){
                            closestColors.add(colorTracker[rToCheck][gToCheck][bToCheck]);
                        }
                    }
                }
            }
            gToCheck = currentG + range;
            if(gToCheck < colorTracker.length){
                for (int r = -range + 1; r <= range - 1; r++){
                    for (int b = -range; b <= range; b++){
                        rToCheck = r + currentR;
                        bToCheck = b + currentB;
                        if (rToCheck >= 0 && bToCheck >= 0 && rToCheck < colorTracker.length && bToCheck < colorTracker.length &&
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
                        if (rToCheck >= 0 && gToCheck >= 0 && rToCheck < colorTracker.length && gToCheck < colorTracker.length &&
                            colorTracker[rToCheck][gToCheck][bToCheck] != 0){
                            closestColors.add(colorTracker[rToCheck][gToCheck][bToCheck]);
                        }
                    }
                }
            }
            bToCheck = currentB + range;
            if (bToCheck < colorTracker.length){
                for (int r = -range + 1; r <= range - 1; r++){
                    for (int g = -range + 1; g <= range - 1; g++){
                        rToCheck = r + currentR;
                        gToCheck = g + currentG;
                        if (rToCheck >= 0 && gToCheck >= 0 && rToCheck < colorTracker.length && gToCheck < colorTracker.length &&
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
