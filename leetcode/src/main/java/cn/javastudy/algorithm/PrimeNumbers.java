package cn.javastudy.algorithm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StreamTokenizer;

public class PrimeNumbers {

    /**
     * public static void main(String[] args) throws IOException {
     * 		// 把文件里的内容，load进来，保存在内存里，很高效，很经济，托管的很好
     * 		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
     * 		// 一个一个读数字
     * 		StreamTokenizer in = new StreamTokenizer(br);
     * 		// 提交答案的时候用的，也是一个内存托管区
     * 		PrintWriter out = new PrintWriter(new OutputStreamWriter(System.out));
     * 		while (in.nextToken() != StreamTokenizer.TT_EOF) { // 文件没有结束就继续
     * 			// n，二维数组的行
     * 			int n = (int) in.nval;
     * 			in.nextToken();
     * 			// m，二维数组的列
     * 			int m = (int) in.nval;
     * 			// 装数字的矩阵，临时动态生成
     * 			int[][] mat = new int[n][m];
     * 			for (int i = 0; i < n; i++) {
     * 				for (int j = 0; j < m; j++) {
     * 					in.nextToken();
     * 					mat[i][j] = (int) in.nval;
     * 				                }            * 			}
     * 			out.println(maxSumSubmatrix(mat, n, m));
     *        }
     * 		out.flush();
     * 		br.close();
     * 		out.close();    * 	}
     */
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StreamTokenizer in = new StreamTokenizer(br);
        PrintWriter out = new PrintWriter(new OutputStreamWriter(System.out));

        while (in.nextToken() != StreamTokenizer.TT_EOF) {
            int start = (int) in.nval;
            in.nextToken();
            int end = (int) in.nval;
            printPrime(start, end, out);
            out.flush();
        }

        out.flush();
        out.close();
        br.close();
    }

    private static void printPrime(int start, int end, PrintWriter out) {
        int count = 0;
        for (int i = start; i <= end; i++) {
            if (isPrime(i)) {
                out.print(i + " ");
                count++;
            }
        }

        out.println();
        out.println("prime counts: " + count);
    }

    private static boolean isPrime(int n) {
        if (n < 2) {
            return false;
        }

        for (int i = 2; i <= Math.sqrt(n); i++) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }
}
