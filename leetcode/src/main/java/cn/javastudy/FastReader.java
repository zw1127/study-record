package cn.javastudy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

/**
 * Function Description:  <br>
 * Writer: zw_1127 . <br>
 * Creating Time: 2025-02-05 12:02 <br>
 * Version: 1.0.0 <br>
 */
public class FastReader {
    static BufferedReader br;
    static StringTokenizer st;

    FastReader() {
        br = new BufferedReader(new InputStreamReader(System.in));
    }

    String next() {
        String str = "";
        while (st == null || !st.hasMoreElements()) {
            try {
                str = br.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            st = new StringTokenizer(str);
        }
        return st.nextToken();
    }

    int nextInt() {
        return Integer.parseInt(next());
    }

    double nextDouble() {
        return Double.parseDouble(next());
    }

    long nextLong() {
        return Long.parseLong(next());
    }
}
