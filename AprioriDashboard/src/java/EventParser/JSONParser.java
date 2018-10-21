/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EventParser;

import constants.Constants;
import java.io.BufferedReader;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import util.Apriori;

/**
 *
 */
public class JSONParser implements Constants {

    private static final String LINE = System.getProperty("line.separator");

    public static HashMap<String, Integer> main(String[] args) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE));
            String line;
            StringBuilder builder = new StringBuilder();
            while((line = reader.readLine()) != null) {
                builder.append(line);
            }
            JSONArray mainArray = new JSONArray(builder.toString().trim());
            SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss aa");
//            ArrayList<String> names = new ArrayList<>();
            ArrayList<String> types = new ArrayList<>();
            ArrayList<String> times = new ArrayList<>();
            StringBuilder inputBuilder = new StringBuilder();
            for (int i = 0; i < mainArray.length(); i++) {
                try {
                    JSONObject eventObject = mainArray.getJSONObject(i);
                    if(eventObject.has("event")) {
                        String type = eventObject.getString("event");
                        JSONObject propertiesObject = eventObject.getJSONObject("properties");
                        if(propertiesObject.has("time")) {
                            long time = propertiesObject.getLong("time");
                            String date = format.format(time);
                            if(!times.contains(date)) {
                                times.add(date);
                            }
//                            String region = propertiesObject.getString("$region");
//                            String city = propertiesObject.getString("$city");
//                            country = country.replaceAll(" ", "_");
//                            if (!names.contains(country)) {
//                                names.add(country);
//                            }
//                            region = region.replaceAll(" ", "_");
//                            if (!names.contains(region)) {
//                                names.add(region);
//                            }
//                            city = city.replaceAll(" ", "_");
//                            if (!names.contains(city)) {
//                                names.add(city);
//                            }
                            if(!types.contains(type)) {
                                types.add(type);
                            }
//                            inputBuilder.append(names.indexOf(country)).append(" ")
//                                    .append(names.indexOf(region)).append(" ")
//                                    .append(names.indexOf(city)).append(LINE);
                              inputBuilder.append(types.indexOf(type)).append(" ")
                                      .append(times.indexOf(date)).append(LINE);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                }
            }
//            System.out.println("Unique Types : " + types);
            HashMap<String, Integer> support = new Apriori().runAlgorithm(.005, inputBuilder);
            HashMap<String, Integer> areaSupport = new HashMap();
            for(Map.Entry<String, Integer> entry : support.entrySet()) {
                String key = entry.getKey();
                int value = entry.getValue();
                try {
                    String[] candidates = key.split(" ");
                    if(candidates.length != 2) {
                        continue;
                    }
                    if(candidates == null) {
                        continue;
                    }
                    StringBuilder area = new StringBuilder();
                    int i=1;
//                    boolean skip = false;
                    for(String candidate : candidates) {
                        int index = Integer.parseInt(candidate.trim());
                        if(index != -1) {
                            if(i != candidates.length) {
                                area.append(types.get(index)).append(" ");
                            } else {
//                                if(types.get(index).contains("IMPRESSION")) {
//                                    skip = true;
//                                } else {
                                    area.append(times.get(index)).append(" ");
//                                }
                            }
                        }
                        i++;
                    }
//                    if(skip) {
//                        continue;
//                    }
                    areaSupport.put(area.toString().trim(), value);
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                }
            }
            return areaSupport;
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return null;
    }

    private static class Sorter implements Comparator<String> {

        private HashMap<String, Integer> map;

        public Sorter(HashMap<String, Integer> map) {
            this.map = map;
        }

        @Override
        public int compare(String o1, String o2) {
            int count1 = o1.split(" ").length;
            int count2 = o2.split(" ").length;
            return count2-count1;
        }
        
    }

}