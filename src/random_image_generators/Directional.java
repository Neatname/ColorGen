package random_image_generators;

import java.util.List;

import pixels.DirectionalPixel;

public class Directional extends RandomImage {

    private boolean passed;
    
    private int rearPercent;
    
    private boolean firstWay;
    
    private int individualPercent;
    
    private float curl;
    
    private int flipChance;
    
    private int directionPercent;
    
    public Directional(int colors, int width, int height, int scaleFactor, int rearPercent,
                       int individualPercent, float curl, int flipChance, int directionPercent, int shapeFactor){
        super(colors, width, height, scaleFactor);
        passed = true;
        this.rearPercent = rearPercent;
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
    }
    
    @Override
    public void nextPixel() {
        
        int index = getIndex();
        
        if (rand.nextInt(flipChance) < 1){
            curl = -curl;
        }
        
        DirectionalPixel toAddTo = edgeList.get(index); 
        List<DirectionalPixel> nextPossibilities = toAddTo.getSortedPossibilities(image, curl);
        if (nextPossibilities.size() == 0){
            edgeList.remove(index);
            passed = false;
            return;
        }
        passed = true;
        edgeList.add(toAddTo);
        edgeList.remove(index);
        
        int rgb = image.getRGB(toAddTo.getX(), toAddTo.getY());
        
        List<Integer> colorPossibilities = getClosestColors(rgb);
        
        
        index = 0;
        for (int i = 0; i < nextPossibilities.size(); i++){
            if (rand.nextInt(100) < directionPercent){
                index = i;
                break;
            }
        }
        
        DirectionalPixel pixelToAdd = nextPossibilities.get(index);
        int colorToAdd = colorPossibilities.get(rand.nextInt(colorPossibilities.size()));
        colorTracker[(colorToAdd >> 16) & 0xFF][(colorToAdd >> 8) & 0xFF][colorToAdd & 0xFF] = 0;
        edgeList.add(pixelToAdd);
        updateImages(pixelToAdd.getX(), pixelToAdd.getY(), colorToAdd);
    }
    
    private int getIndex(){
        int index = 0;
        if (passed){
            if (rand.nextInt(100) < rearPercent){
                for (int i = 0; i < edgeList.size(); i++){
                    if (rand.nextInt(100) < individualPercent){
                        index = i;
                        firstWay = true;
                        break;
                    }
                }
            } else {
                for (int i = edgeList.size() - 1; i >= 0; i--){
                    if (rand.nextInt(100) < individualPercent){
                        index = i;
                        firstWay = false;
                        break;
                    }
                }
            }
        } else {
            if (firstWay){
                for (int i = 0; i < edgeList.size(); i++){
                    if (rand.nextInt(100) < individualPercent){
                        index = i;
                        firstWay = true;
                        break;
                    }
                }
            } else {
                for (int i = edgeList.size() - 1; i >= 0; i--){
                    if (rand.nextInt(100) < individualPercent){
                        index = i;
                        firstWay = false;
                        break;
                    }
                }
            }
        }
        return index;
    }

}
