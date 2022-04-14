import java.lang.Math;
public class test2 {
    public static void main(String[] args){
        int x = 1;
        while(x < 10){
                if(x==1){
                    System.out.println("one");}   
                if(x==3){
                    System.out.println("three");}  
                if(x==5){
                    System.out.println("five");}           
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
