package org.jfree.chart.urls;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.jfree.data.general.PieDataset;
import org.jfree.util.PublicCloneable;

public class CustomPieURLGenerator implements PieURLGenerator, Cloneable, PublicCloneable, Serializable {
    private static final long serialVersionUID = 7100607670144900503L;
    private ArrayList urls;

    public CustomPieURLGenerator() {
        this.urls = new ArrayList();
    }

    public String generateURL(PieDataset dataset, Comparable key, int pieIndex) {
        return getURL(key, pieIndex);
    }

    public int getListCount() {
        return this.urls.size();
    }

    public int getURLCount(int list) {
        Map urlMap = (Map) this.urls.get(list);
        if (urlMap != null) {
            return urlMap.size();
        }
        return 0;
    }

    public String getURL(Comparable key, int mapIndex) {
        if (mapIndex >= getListCount()) {
            return null;
        }
        Map urlMap = (Map) this.urls.get(mapIndex);
        if (urlMap != null) {
            return (String) urlMap.get(key);
        }
        return null;
    }

    public void addURLs(Map urlMap) {
        this.urls.add(urlMap);
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof CustomPieURLGenerator)) {
            return false;
        }
        CustomPieURLGenerator generator = (CustomPieURLGenerator) o;
        if (getListCount() != generator.getListCount()) {
            return false;
        }
        for (int pieItem = 0; pieItem < getListCount(); pieItem++) {
            if (getURLCount(pieItem) != generator.getURLCount(pieItem)) {
                return false;
            }
            for (String key : ((HashMap) this.urls.get(pieItem)).keySet()) {
                if (!getURL(key, pieItem).equals(generator.getURL(key, pieItem))) {
                    return false;
                }
            }
        }
        return true;
    }

    public Object clone() throws CloneNotSupportedException {
        CustomPieURLGenerator urlGen = new CustomPieURLGenerator();
        Iterator i = this.urls.iterator();
        while (i.hasNext()) {
            Map map = (Map) i.next();
            Map newMap = new HashMap();
            for (String key : map.keySet()) {
                newMap.put(key, map.get(key));
            }
            urlGen.addURLs(newMap);
        }
        return urlGen;
    }
}
