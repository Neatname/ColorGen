package random_image_generators;

import java.util.List;
import java.util.ListIterator;

import pixels.DirectionalPixel;

public class DirectionalWithIterator extends RandomImage {
    
    private int individualPercent;
    
    private float curl;
    
    private int flipChance;
    
    private int directionPercent;
    
    private ListIterator<DirectionalPixel> edgeIterator;
    
    public DirectionalWithIterator(int colors, int width, int height, int scaleFactor, int individualPercent,
                                   float curl, int flipChance, int directionPercent, int shapeFactor){
        super(colors, width, height, scaleFactor);
        this.individualPercent = individualPercent;
        this.curl = curl;
        this.flipChance = flipChance;
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
        
        if (rand.nextInt(flipChance) < 1){
            curl = -curl;
        }
        
        DirectionalPixel toAddTo = getNextPixel(); 
        List<DirectionalPixel> nextPossibilities = toAddTo.getNextPossibilities(image, curl);
        if (nextPossibilities.size() == 0){
            edgeIterator.remove();
            return;
        }
        
        int rgb = image.getRGB(toAddTo.getX(), toAddTo.getY());
        
        List<Integer> colorPossibilities = getClosestColors(rgb);
        
        
        int index = 0;
        for (int i = 0; i < nextPossibilities.size(); i++){
            if (rand.nextInt(100) < directionPercent){
                index = i;
                break;
            }
        }
        
        DirectionalPixel pixelToAdd = nextPossibilities.get(index);
        int colorToAdd = colorPossibilities.get(rand.nextInt(colorPossibilities.size()));
        colorTracker[(colorToAdd >> 16) & 0xFF][(colorToAdd >> 8) & 0xFF][colorToAdd & 0xFF] = 0;
        edgeIterator.add(pixelToAdd);
        updateImages(pixelToAdd.getX(), pixelToAdd.getY(), colorToAdd);
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
