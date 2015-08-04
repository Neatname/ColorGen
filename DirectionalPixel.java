import java.util.*;
import java.awt.image.BufferedImage;
public class DirectionalPixel {
    final private double[][] DIRECTIONS = new double[][] {{3.0, 2.0, 1.0},
                                                        {4.0, 0.0, 0.0},
                                                        {5.0, 6.0, 7.0}};

    private int x;
    private int y;
    private double direction;
    private double shapeFactor;

    public DirectionalPixel (XORShiftRandom rand, int width, int height){
        x = rand.nextInt(width);
        y = rand.nextInt(height);
        shapeFactor = (double)rand.nextInt(100) + 1.0;
        direction = simplify((double)rand.nextInt(100));
    }
    
    public DirectionalPixel (DirectionalPixel parent, int[] location){
        shapeFactor = parent.getShapeFactor();
        direction = parent.newAverageDirection(location, shapeFactor);
        x = location[0];
        y = location[1];
    }
    
    public DirectionalPixel (DirectionalPixel parent, int x, int y){
        shapeFactor = parent.getShapeFactor();
        direction = parent.newAverageDirection(new int[] {x, y}, shapeFactor);
        this.x = x;
        this.y = y;
    }

    public DirectionalPixel (int x, int y, double direction, double shapeFactor){
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.shapeFactor = shapeFactor;
    }

    public int getX(){
        return x;
    }
    
    public int getY(){
        return y;
    }
    
    public double getDirection(){
        return direction;
    }
    
    public double getShapeFactor(){
        return shapeFactor;
    }
    
    private double simplify (double direction){
        while (direction > 4.0 || direction < -4.0){    
            if (direction > 4.0){
                direction -= 8.0;
            } else {
                direction += 8.0;
            }
        }
        return direction;
    }
    
    private double getRelativeDirection (int[] next){
        return DIRECTIONS[this.x - next[0] + 1][this.y - next[1] + 1];
    }
    
    private double newAverageDirection (int[] next, double shapeFactor){
        double newDirection = this.getRelativeDirection(next);
        while (Math.abs(this.direction - newDirection) > 4.0){
            if (this.direction - newDirection > 4.0){
                newDirection += 8.0;
            } else {
                newDirection -= 8.0;
            }
        }
        newDirection = (this.direction * shapeFactor + newDirection) / (shapeFactor + 1);
        return simplify(newDirection);
    }
    
    public void getNextPossibilities (BufferedImage image, ArrayList<DirectionalPixel> nextPossibilities){
        nextPossibilities.clear();
        ArrayList<Double> differences = new ArrayList<Double>(8);
        int xToCheck;
        int yToCheck;
        for (int xDiff = -1; xDiff <= 1; xDiff++){
            for (int yDiff = -1; yDiff <= 1; yDiff++){
                xToCheck = x + xDiff;
                yToCheck = y + yDiff;
                try{
                    if (image.getRGB(xToCheck, yToCheck) == -33554432){
                        nextPossibilities.add(new DirectionalPixel(this, xToCheck, yToCheck));
                        differences.add(getDifference(this.direction, this.getRelativeDirection(new int[] {nextPossibilities.get(nextPossibilities.size() - 1).getX(), nextPossibilities.get(nextPossibilities.size() - 1).getY()})));
                    }
                } catch (ArrayIndexOutOfBoundsException e){}
            }
        }
        boolean sorted = false;
        while (!sorted){
            sorted = true;
            for (int i = 1; i < differences.size(); i++){
                if (differences.get(i) < differences.get(i - 1)){
                    sorted = false;
                    differences.add(i - 1, differences.get(i));
                    differences.remove(i + 1);
                    nextPossibilities.add(i - 1, nextPossibilities.get(i));
                    nextPossibilities.remove(i + 1);
                }
            }
        }
    }
    
    private double getDifference(double first, double second){
        while (Math.abs(first - second) > 4){
            if (first - second > 4){
                second += 8;
            } else {
                second -= 8;
            }
        }
        return Math.abs(first - second);
    }
}