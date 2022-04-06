public class test1withcomment {
    public static void main(String[] args){
        //initialize variables
        int x = 1;
        //while loop
        while(x < 10){
            //switch statement print out number 1 3 5
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
            //if x is less than 5, add 2 to x
            if(x < 5){
                x=x+2;}
            //else add 1 to x
            else{
                x++;
                //random number
                double rand = Math.random();
                System.out.println(rand);}
        }   
    } 
}
