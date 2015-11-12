package random_image_generators;

import java.util.List;
import java.util.ListIterator;

import pixels.DirectionalPixel;

public class FasterIterator extends RandomImage {

    private int individualPercent;
    
    private ListIterator<DirectionalPixel> edgeIterator;
    
    public FasterIterator(int colors, int width, int height, int scaleFactor, int individualPercent, int shapeFactor){
        
        super(colors, width, height, scaleFactor);
        this.individualPercent = individualPercent;
        DirectionalPixel pixelToAdd = new DirectionalPixel(rand, width, height, shapeFactor);
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
        List<DirectionalPixel> nextPossibilities = toAddTo.getMorePossibilities(image);
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
        
        for (;;){
            if (rightInd){
                if (edgeIterator.hasNext()){
                    if (rand.nextInt(1000) < individualPercent){
                        return edgeIterator.next();
                    } else {
                        edgeIterator.next();
                    }
                } else {
                    rightInd = false;
                }
            } else {
                if (edgeIterator.hasPrevious()){
                    if (rand.nextInt(1000) < individualPercent){
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
