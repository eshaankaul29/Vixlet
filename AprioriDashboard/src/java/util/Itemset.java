package util;

import java.text.DecimalFormat;

public class Itemset {

    public int[] items;
    public int count = 0;

    public Itemset(int[] items) {
        this.items = items;
    }

    public String getSupportRelatifFormatted(int nbObject) {
        double frequence = ((double) count) / ((double) nbObject);
        DecimalFormat format = new DecimalFormat();
        format.setMinimumFractionDigits(0);
        format.setMaximumFractionDigits(2);
        return format.format(frequence);
    }

    public int getAbsoluteSupport() {
        return count;
    }

    public void print() {
        System.out.print(toString());
    }

    @Override
    public String toString() {
        StringBuilder r = new StringBuilder();
        for (Integer attribute : items) {
            r.append(attribute.toString());
            r.append(' ');
        }
        return r.toString();
    }
}