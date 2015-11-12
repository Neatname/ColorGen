package random_image_generators;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import pixels.DirectionalPixel;

public class DoubleIterator extends RandomImage {
    
    private int individualPercent;
    
    private float curl;
    
    private int flipChance;
    
    private ListIterator<DirectionalPixel> pixelIterator;
    
    private ListIterator<int[]> colorIterator;
    
    private int colorPercent;
    
    public DoubleIterator(int colors, int width, int height, int scaleFactor, int individualPercent,
                                   float curl, int flipChance, int directionPercent, int shapeFactor, int colorPercent){
        super(colors, width, height, scaleFactor);
        this.individualPercent = individualPercent;
        this.curl = curl;
        this.flipChance = flipChance;
        DirectionalPixel pixelToAdd = new DirectionalPixel(rand, width, height, shapeFactor);
        edgeList.add(pixelToAdd);
        this.colorPercent = colorPercent;
        
        int startR = rand.nextInt(256);
        int startG = rand.nextInt(256);
        int startB = rand.nextInt(256);
        int colorToAdd = colorTracker[startR][startG][startB];
        
        image.setRGB(pixelToAdd.getX(), pixelToAdd.getY(), colorToAdd);
        colorTracker[startR][startG][startB] = 0;
        
        pixelIterator = edgeList.listIterator();
        colorIterator = new LinkedList<int[]>().listIterator();
        int [] arrayToAdd = {startR, startG, startB};
        colorIterator.add(arrayToAdd);
    }
    
    @Override
    public void nextPixel() {
        
        if (rand.nextInt(flipChance) < 1){
            curl = -curl;
        }
        
        DirectionalPixel toAddTo = getNextPixel(); 
        List<DirectionalPixel> nextPixels = toAddTo.getPossibilities(image);
        if (nextPixels.size() == 0){
            pixelIterator.remove();
            return;
        }
        
        List<int[]> nextColors = getNextColors();
        if (nextColors.size() == 0){
            colorIterator.remove();
            return;
        }
        
        while (nextPixels.size() != 0 && nextColors.size() != 0){
            DirectionalPixel pixelToAdd = nextPixels.remove(rand.nextInt(nextPixels.size()));
            int[] colorToAdd = nextColors.remove(rand.nextInt(nextColors.size()));
            colorTracker[colorToAdd[0]][colorToAdd[1]][colorToAdd[2]] = 0;
            pixelIterator.add(pixelToAdd);
            colorIterator.add(colorToAdd);
            
            if (rand.nextInt(2) == 0){
                pixelIterator.previous();
                colorIterator.previous();
            }
            
            updateImages(pixelToAdd.getX(), pixelToAdd.getY(), new Color(colorToAdd[0], colorToAdd[1], colorToAdd[2]).getRGB());
            
        }
    }
    
    private int[] getColorToAddTo() {
        boolean rightInd;
        if (rand.nextInt(2) == 0){
            rightInd = true;
        } else {
            rightInd = false;
        }
        
        for (;;){
            if (rightInd){
                if (colorIterator.hasNext()){
                    if (rand.nextInt(1000) < colorPercent){
                        return colorIterator.next();
                    } else {
                        colorIterator.next();
                    }
                } else {
                    rightInd = false;
                }
            } else {
                if (colorIterator.hasPrevious()){
                    if (rand.nextInt(1000) < colorPercent){
                        return colorIterator.previous();
                    } else {
                        colorIterator.previous();
                    }
                } else {
                    rightInd = true;
                }
            }
        }
    }
    
    private List<int[]> getNextColors(){
        int[] toAddTo = getColorToAddTo();
        List<int[]> ret = new ArrayList<int[]>();
                
        for (int r = toAddTo[0] - 1; r <= toAddTo[0] + 1; r++){
            if (r > 255 || r < 0){
                continue;
            }
            for (int g = toAddTo[1] - 1; g <= toAddTo[1] + 1; g++){
                if (g > 255 || g < 0){
                    continue;
                }
                for (int b = toAddTo[2] - 1; b <= toAddTo[2] + 1; b++){
                    if (b > 255 || b < 0){
                        continue;
                    }
                    if (colorTracker[r][g][b] != 0){
                        int[] temp = {r, g, b};
                        ret.add(temp);
                    }
                }
            }
        }
        return ret;
    }

    private DirectionalPixel getNextPixel(){
        
        boolean rightInd;
        if (rand.nextInt(2) == 0){
            rightInd = true;
        } else {
            rightInd = false;
        }
        
        for (;;){
            if (rightInd){
                if (pixelIterator.hasNext()){
                    if (rand.nextInt(1000) < individualPercent){
                        return pixelIterator.next();
                    } else {
                        pixelIterator.next();
                    }
                } else {
                    rightInd = false;
                }
            } else {
                if (pixelIterator.hasPrevious()){
                    if (rand.nextInt(1000) < individualPercent){
                        return pixelIterator.previous();
                    } else {
                        pixelIterator.previous();
                    }
                } else {
                    rightInd = true;
                }
            }
        }
        
    }
}
