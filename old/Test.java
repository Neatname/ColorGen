public class Test {
    boolean t1;
    boolean t2;
    public Test(){
        t1 = true;
        t2 = false;
        System.out.println(bool(t1 && t2));
    }
    
    public boolean bool(boolean param){
        t2 = true;
        return param;
    }
    
    public static void main(String[] args){
        new Test();
    }
}