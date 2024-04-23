package cn.javastudy.algorithm;

import java.util.Scanner;

public class GouGuAlgorithm {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            int n = scanner.nextInt();
            int m = scanner.nextInt();
            boolean found = false;

            for (int i = n; i <= m; i++) {
                for (int j = i + 1; j < m; j++) {
                    int k = (int) Math.sqrt(i * i + j * j);
                    if (k > m) {
                        break;
                    }

                    if (k * k == i * i + j * j) {
                        if (gcd(k, i) == 1 && gcd(i, j) == 1) {
                            String msg = String.format("%d %d %d", i, j, k);
                            System.out.println(msg);
                            found = true;
                        }
                    }
                }
            }

            if (!found) {
                System.out.println("Na");
            }
        }
    }

    private static int gcd(int a, int b) {
        return b == 0 ? a : gcd(b, a % b);
    }
}
