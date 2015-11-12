package random_image_generators;

import java.util.List;
import java.util.ListIterator;

import pixels.DirectionalPixel;

public class SwitchIterator extends RandomImage {
    
    private ListIterator<DirectionalPixel> edgeIterator;
    
    private int maxPercent;
    
    private int minPercent;
    
    private int currentPercent;
    
    public SwitchIterator(int colors, int width, int height, int scaleFactor, int maxPercent, int minPercent){
        
        super(colors, width, height, scaleFactor);
        this.maxPercent = maxPercent;
        this.minPercent = minPercent;
        this.currentPercent = minPercent + rand.nextInt(maxPercent - minPercent + 1);
        DirectionalPixel pixelToAdd = new DirectionalPixel(rand, width, height, 1);
        edgeList.add(pixelToAdd);
        
        int startR = rand.nextInt(256);
        int startG = rand.nextInt(256);
        int startB = rand.nextInt(256);
        int colorToAdd = colorTracker[startR][startG][startB];
        
        image.setRGB(pixelToAdd.getX(), pixelToAdd.getY(), colorToAdd);
        colorTracker[startR][startG][startB] = 0;
        
        edgeIterator = edgeList.listIterator();
    }
    @Override
    public void nextPixel() {
        
        DirectionalPixel toAddTo = getNextPixel(); 
        List<DirectionalPixel> nextPossibilities = toAddTo.getPossibilities(image);
        if (nextPossibilities.size() == 0){
            edgeIterator.remove();
            return;
        }
        
        int colorToMatch = image.getRGB(toAddTo.getX(), toAddTo.getY());
        
        List<Integer> colorPossibilities = getClosestColors(colorToMatch);
        
        while (nextPossibilities.size() != 0 && colorPossibilities.size() != 0){
            DirectionalPixel pixelToAdd = nextPossibilities.remove(nextPossibilities.size() - 1);
            int colorToAdd = colorPossibilities.get(rand.nextInt(colorPossibilities.size()));
            colorTracker[(colorToAdd >> 16) & 0xFF][(colorToAdd >> 8) & 0xFF][colorToAdd & 0xFF] = 0;
            edgeIterator.add(pixelToAdd);
            if (rand.nextInt(2) == 0){
                edgeIterator.previous();
            }
            updateImages(pixelToAdd.getX(), pixelToAdd.getY(), colorToAdd);
        }
    }
    
    
    private DirectionalPixel getNextPixel(){
        
        boolean rightInd;
        if (rand.nextInt(2) == 0){
            rightInd = true;
        } else {
            rightInd = false;
        }
        
        if (rand.nextInt(1000000) == 0){
            this.currentPercent = minPercent + rand.nextInt(maxPercent - minPercent + 1);
        }
        
        for (;;){
            if (rightInd){
                if (edgeIterator.hasNext()){
                    if (rand.nextInt(1000) < currentPercent){
                        return edgeIterator.next();
                    } else {
                        edgeIterator.next();
                    }
                } else {
                    rightInd = false;
                }
            } else {
                if (edgeIterator.hasPrevious()){
                    if (rand.nextInt(1000) < currentPercent){
                        return edgeIterator.previous();
                    } else {
                        edgeIterator.previous();
                    }
                } else {
                    rightInd = true;
                }
            }
        }
    }
}
