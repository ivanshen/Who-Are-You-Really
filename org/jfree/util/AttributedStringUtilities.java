package org.jfree.util;

import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

public class AttributedStringUtilities {
    private AttributedStringUtilities() {
    }

    public static boolean equal(AttributedString s1, AttributedString s2) {
        boolean z = true;
        if (s1 == null) {
            if (s2 != null) {
                z = false;
            }
            return z;
        } else if (s2 == null) {
            return false;
        } else {
            AttributedCharacterIterator it1 = s1.getIterator();
            AttributedCharacterIterator it2 = s2.getIterator();
            char c1 = it1.first();
            char c2 = it2.first();
            int start = 0;
            while (c1 != '\uffff') {
                int limit1 = it1.getRunLimit();
                if (limit1 != it2.getRunLimit() || !it1.getAttributes().equals(it2.getAttributes())) {
                    return false;
                }
                for (int i = start; i < limit1; i++) {
                    if (c1 != c2) {
                        return false;
                    }
                    c1 = it1.next();
                    c2 = it2.next();
                }
                start = limit1;
            }
            if (c2 != '\uffff') {
                z = false;
            }
            return z;
        }
    }
}
