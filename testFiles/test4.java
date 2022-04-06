import java.lang.Math;
public class test4 {
    public static void main(String[] args){
        int x = 1;
        for(int i = 0; i < 7; i++){
            switch(x){
                case 1:
                    System.out.println("one");
                    break;
                case 3:
                    System.out.println("three");
                    break;
                case 5:
                    System.out.println("five");
                    break;}               
            System.out.println(x);
            if(x < 5){
                x=x+2;}
            else{
                x++;
                double rand = Math.random();
                System.out.println(rand);}
        }
    }
    
    
}
