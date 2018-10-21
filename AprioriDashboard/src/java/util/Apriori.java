package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.JOptionPane;

public class Apriori {

    private static final String LINE = System.getProperty("line.separator");
    protected int k;
    protected int totalCandidateCount = 0;
    protected long startTimestamp;
    protected long endTimestamp;
    private int itemsetCount;
    private int minsupRelative;
    private List<int[]> database = null;
    private double maxMemory;
    private HashMap<String, Integer> support = new HashMap<>();

    public Apriori() {
    }

    public HashMap<String, Integer> runAlgorithm(double minsup, StringBuilder inputBuilder) throws IOException {
        startTimestamp = System.currentTimeMillis();
        itemsetCount = 0;
        totalCandidateCount = 0;
        maxMemory = 0;
        int transactionCount = 0;
        if(inputBuilder.length() <= 0) {
            System.err.println("No input set.");
            return support;
        }
        Map<Integer, Integer> mapItemCount = new HashMap<Integer, Integer>();
        database = new ArrayList<int[]>();
        String[] lines = inputBuilder.toString().split(LINE);
        for (String line : lines) {
            if(line.trim().length() <= 0) {
                continue;
            }
            String[] lineSplited = line.split(" ");
            int transaction[] = new int[lineSplited.length];
            for (int i = 0; i < lineSplited.length; i++) {
                Integer item = Integer.parseInt(lineSplited[i]);
                transaction[i] = item;
                Integer count = mapItemCount.get(item);
                if (count == null) {
                    mapItemCount.put(item, 1);
                } else {
                    mapItemCount.put(item, ++count);
                }
            }
            database.add(transaction);
            transactionCount++;
        }
        this.minsupRelative = (int) Math.ceil(minsup * transactionCount);
        k = 1;
        List<Integer> frequent1 = new ArrayList<Integer>();
        for (Entry<Integer, Integer> entry : mapItemCount.entrySet()) {
            if (entry.getValue() >= minsupRelative) {
                frequent1.add(entry.getKey());
                support.put(entry.getKey() + "", entry.getValue());
                itemsetCount++;
            }
        }
        mapItemCount = null;
        Collections.sort(frequent1, new Comparator<Integer>() {

            public int compare(Integer o1, Integer o2) {
                return o1 - o2;
            }
        });
        if (frequent1.size() == 0) {
            return support;
        }
        totalCandidateCount += frequent1.size();
        List<Itemset> level = null;
        k = 2;
        do {
            checkMemory();
            List<Itemset> candidatesK;
            if (k == 2) {
                candidatesK = generateCandidate2(frequent1);
            } else {
                candidatesK = generateCandidateSizeK(level);
            }
            totalCandidateCount += candidatesK.size();
            for (int[] transaction : database) {
                loopCand:
                for (Itemset candidate : candidatesK) {
                    int pos = 0;
                    for (int item : transaction) {
                        if (item == candidate.items[pos]) {
                            pos++;
                            if (pos == candidate.items.length) {
                                candidate.count++;
                                continue loopCand;
                            }
                        } else if (item > candidate.items[pos]) {
                            continue loopCand;
                        }
                    }
                }
            }
            level = new ArrayList<Itemset>();
            for (Itemset candidate : candidatesK) {
                if (candidate.getAbsoluteSupport() >= minsupRelative) {
                    level.add(candidate);
                    support.put(candidate.toString(), candidate.getAbsoluteSupport());
                    itemsetCount++;
                }
            }
            k++;
        } while (level.isEmpty() == false);
        endTimestamp = System.currentTimeMillis();
        checkMemory();
        return support;
    }

    private List<Itemset> generateCandidate2(List<Integer> frequent1) {
        List<Itemset> candidates = new ArrayList<Itemset>();
        for (int i = 0; i < frequent1.size(); i++) {
            Integer item1 = frequent1.get(i);
            for (int j = i + 1; j < frequent1.size(); j++) {
                Integer item2 = frequent1.get(j);
                candidates.add(new Itemset(new int[]{item1, item2}));
            }
        }
        return candidates;
    }

    protected List<Itemset> generateCandidateSizeK(List<Itemset> levelK_1) {
        List<Itemset> candidates = new ArrayList<Itemset>();
        loop1:
        for (int i = 0; i < levelK_1.size(); i++) {
            int[] itemset1 = levelK_1.get(i).items;
            loop2:
            for (int j = i + 1; j < levelK_1.size(); j++) {
                int[] itemset2 = levelK_1.get(j).items;
                for (int k = 0; k < itemset1.length; k++) {
                    if (k == itemset1.length - 1) {
                        if (itemset1[k] >= itemset2[k]) {
                            continue loop1;
                        }
                    } else if (itemset1[k] < itemset2[k]) {
                        continue loop2;
                    } else if (itemset1[k] > itemset2[k]) {
                        continue loop1;
                    }
                }
                int newItemset[] = new int[itemset1.length + 1];
                System.arraycopy(itemset1, 0, newItemset, 0, itemset1.length);
                newItemset[itemset1.length] = itemset2[itemset2.length - 1];
                if (allSubsetsOfSizeK_1AreFrequent(newItemset, levelK_1)) {
                    candidates.add(new Itemset(newItemset));
                }
            }
        }
        return candidates;
    }

    protected boolean allSubsetsOfSizeK_1AreFrequent(int[] candidate, List<Itemset> levelK_1) {
        for (int posRemoved = 0; posRemoved < candidate.length; posRemoved++) {
            int first = 0;
            int last = levelK_1.size() - 1;
            boolean found = false;
            while (first <= last) {
                int middle = (first + last) / 2;
                if (sameAs(levelK_1.get(middle), candidate, posRemoved) < 0) {
                    first = middle + 1;
                } else if (sameAs(levelK_1.get(middle), candidate, posRemoved) > 0) {
                    last = middle - 1;
                } else {
                    found = true;
                    break;
                }
            }
            if (found == false) {
                return false;
            }
        }
        return true;
    }

    private int sameAs(Itemset itemset, int[] candidate, int posRemoved) {
        int j = 0;
        for (int i = 0; i < itemset.items.length; i++) {
            if (j == posRemoved) {
                j++;
            }
            if (itemset.items[i] == candidate[j]) {
                j++;
            } else if (itemset.items[i] > candidate[j]) {
                return 1;
            } else {
                return -1;
            }
        }
        return 0;
    }

    private void checkMemory() {
        double currentMemory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())
                / 1024d / 1024d;
        if (currentMemory > maxMemory) {
            maxMemory = currentMemory;
        }
    }
}