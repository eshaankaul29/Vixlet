/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;

/**
 *
 */
public class Test {

    public static void main(String[] args) {
        try {
            FileReader fReader = new FileReader("data");
            BufferedReader reader = new BufferedReader(fReader);
            String line;
            String LINE = System.getProperty("line.separator");
            FileWriter writer = new FileWriter("output.txt");
            writer.write("[");
            int i=0;
            while((line = reader.readLine()) != null) {
                try {
                    if(i != 0) {
                        writer.write(", " + LINE);
                    }
                    writer.write(line);
                    i++;
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                }
            }
            writer.close();
            writer.write("]");
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

}