import java.lang.Math;

public class test4 {
	public static void main(String[] args) {
		int x = 1;
		for (int i = 0; i < 7; i++) {
			if (x == 1) {
				System.out.println("one");
			}
			if (x == 3) {
				System.out.println("three");
			}
			if (x == 5) {
				System.out.println("five");
			}
			System.out.println(x);
			if (x < 5) {
				x = x + 2;
			} else {
				x++;
				double rand = Math.random();
				System.out.println(rand);
				
			}
		}
	}

}
