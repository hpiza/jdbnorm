package edu.iteso;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class BigFiles {

    static char[] HEXA_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f',
    };

    public static int randInt(int max) {
        return (int) (max * Math.random());
    }

    public static char randomDigit() {
        return (char) ('0' + ('9' - '0' + 1) * Math.random());
    }


    public static char randomLowerLetter() {
        return (char) ('a' + ('z' - 'a' + 1) * Math.random());
    }

    public static char randomHexaDigit() {
        return HEXA_DIGITS[(int) (HEXA_DIGITS.length * Math.random())];
    }

    public static String randomLowerWord(int N) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < N; i++) {
            sb.append(randomLowerLetter());
        }
        return sb.toString();
    }

    public static String randomHexaWord(int N) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < N; i++) {
            sb.append(randomHexaDigit());
        }
        return sb.toString();
    }

    public static String randomInt(int N) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < N; i++) {
            sb.append(randomDigit());
        }
        return sb.toString();
    }


    public static void main(String[] args) throws Exception {
        final int ROWS = 640_000;
        final int N1 = ROWS / 10;
        final int N2 = ROWS / 10;
        final int N3 = N1 / 2;
        final int N4 = N2 / 3;
        String[][] catalog1 = new String[N1][3];
        String[][] catalog2 = new String[N2][2];
        String[] lowerWords = new String[N3];
        String[] intWords = new String[N3];
        String[] upperWords = new String[N4];
        for (int i = 0; i < N3; i++) {
            lowerWords[i] = randomLowerWord(6);
            intWords[i] = randomInt(4);
        }
        for (int i = 0; i < N4; i++) {
            upperWords[i] = randomLowerWord(5).toUpperCase();
        }
        for (int i = 0; i < N3; i++) lowerWords[i] = randomLowerWord(3);
        for (int i = 0; i < N1; i++) {
            catalog1[i][0] = randomHexaWord(12);
            catalog1[i][1] = lowerWords[randInt(N3)];
            catalog1[i][2] = intWords[randInt(N3)];
        }
        for (int i = 0; i < N2; i++) {
            catalog2[i][0] = randomHexaWord(10);
            catalog2[i][1] = upperWords[randInt(N4)];
        }

        BufferedWriter bw = new BufferedWriter(new FileWriter("datasets/dataset8.csv"));
        bw.append("Field1,Field2,Field3,Field4,Field5,Field6");
        bw.newLine();
        for (int r = 0; r < ROWS; r++) {
            int index1 = randInt(N1);
            int index2 = randInt(N2);
            String line = String.format("%s,%s,%s,%s,%s,%s", randomHexaWord(16),
                    catalog1[index1][1],
                    catalog2[index2][1],
                    catalog1[index1][2],
                    catalog2[index2][0],
                    catalog1[index1][0]);
            bw.append(line);
            bw.newLine();
        }
        bw.close();
    }

}
