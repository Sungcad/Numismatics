package me.sungcad.numismatics.tools;

import java.util.Comparator;
import java.util.Map;

public class ValueComparator implements Comparator<String> {
    private Map<String, Double> map;

    public ValueComparator(Map<String, Double> map) {
        this.map = map;
    }

    public int compare(String a, String b) {
        if (map.get(a) > map.get(b))
            return -1;
        else if (map.get(a) < map.get(b))
            return 1;
        else
            return a.compareToIgnoreCase(b) < 0 ? -1 : 1;
    }
}
