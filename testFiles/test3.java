import java.lang.Math;
public class test3 {
    public static void main(String[] args){
        int x = 1;
        while(x < 10){
            switch(x){
                case 1:
                    System.out.println("one");
                    break;
                case 3:
                    System.out.println("three");
                    break;}               
            System.out.println(x);
            if(x < 5){
                x=x+2;}
            else if(x == 5){
                System.out.println("five");
                x++;
                double rand1 = Math.random();
                System.out.println(rand1);}
            else{
                x++;
                double rand2 = Math.random();
                System.out.println(rand2);}
        }   
    }
}
