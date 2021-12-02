package Lexer;

import global.Config;
import util.LogUtil;

import java.util.*;


class Match {
    int a;
    int b;
    int length;
    public Match(){}

    public Match(int a, int b, int length) {
        this.a = a;
        this.b = b;
        this.length = length;
    }

    public void setA(int a) {
        this.a = a;
    }

    public void setB(int b) {
        this.b = b;
    }

    public int getA() {
        return a;
    }

    public int getB() {
        return b;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getLength() {
        return length;
    }

    @Override
    public String toString() {
        return "Match{" +
                "a=" + a +
                ", b=" + b +
                ", length=" + length +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Match match = (Match) o;
        return a == match.a &&
                b == match.b &&
                length == match.length;
    }

    @Override
    public int hashCode() {
        return Objects.hash(new String(a+","+b));
    }
}

public class GST {
    static List<Match> tiles = new ArrayList<Match>();
    static List<Match> matches = new ArrayList<>();


    public static void gst(String[] tokens1, String[] tokens2,int MinimumMatchLength){
        tiles.clear();

        int size1 = tokens1.length;
        int size2 = tokens2.length;
        boolean mark1[] = new boolean[size1];//记录字符串A和B中被标记的位
        boolean mark2[] = new boolean[size2];
        int maxmatch = 0;
        do {
            maxmatch = MinimumMatchLength;
            matches.clear();
            for (int a = 0; a < size1; ++a) {
                for (int b = 0; b < size2; ++b) {
                    int j = 0;
                    while (tokens1[a + j].equals(tokens2[b + j]) && !mark1[a + j] && !mark2[b + j]) {
                        j++;
                    }
                    if (j == maxmatch) {
                        matches.add(new Match(a, b, j));
                    }
                    else if (j > maxmatch) {
                        matches.clear();
                        matches.add(new Match(a, b, j));
                        maxmatch = j;
                    }
                }
            }
            for (Match match : matches) {
                for (int j = 0; j < match.length - 1; ++j) {
                    mark1[match.a + j] = true;
                    mark2[match.b + j] = true;
                }
                tiles.add(match);
            }
        } while(maxmatch > MinimumMatchLength);
    }
    public static double sim(List<String> tokens1, List<String> tokens2,int MinimumMatchLength){
        double sim = 0.0;
        tiles.clear();

        int size1 = tokens1.size();
        int size2 = tokens2.size();
        boolean mark1[] = new boolean[size1];//记录字符串A和B中被标记的位
        boolean mark2[] = new boolean[size2];
        int maxmatch = 0;
        do {
            maxmatch = MinimumMatchLength;
            matches.clear();
            for (int a = 0; a < size1; ++a) {
                for (int b = 0; b < size2; ++b) {
                    int j = 0;
                    while (a + j < size1 && b + j < size2 &&
                            tokens1.get(a + j).equals(tokens2.get(b + j)) && !mark1[a + j] && !mark2[b + j]) {
                        j++;
                    }
                    if (j == maxmatch) {
                        matches.add(new Match(a, b, j));
                    }
                    else if (j > maxmatch) {
                        matches.clear();
                        matches.add(new Match(a, b, j));
                        maxmatch = j;
                    }
                }
            }
            for (Match match : matches) {
                boolean needAdd = true;
                for (int j = 0; j < match.length - 1; ++j) {
                    if (mark1[match.a + j] || mark2[match.b + j]) needAdd = false;
                    mark1[match.a + j] = true;
                    mark2[match.b + j] = true;
                }
                if (needAdd)
                    tiles.add(match);
            }
        } while(maxmatch > MinimumMatchLength);
        //System.out.println(tiles);

        int coverage = 0;
        for (Match match : tiles) {
            coverage += match.length;
        }
        sim = 2 * (double) coverage / (double)(size1 + size2);
        if (Config.verbose) {
            LogUtil.getLogger().info("tiles:\n\t" + tiles);
            LogUtil.getLogger().info("coverage = sigma(tiles.length):\n\t" + coverage);
            LogUtil.getLogger().info("sim = 2 * coverage / (|tokens1| + |tokens2|):\n\t" + sim);
        }
        return sim;
    }
}