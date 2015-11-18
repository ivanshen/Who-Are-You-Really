package org.jfree.data.resources;

import java.util.ListResourceBundle;
import org.jfree.data.xml.DatasetTags;

public class DataPackageResources_es extends ListResourceBundle {
    private static final Object[][] CONTENTS;

    public Object[][] getContents() {
        return CONTENTS;
    }

    static {
        r0 = new Object[2][];
        r0[0] = new Object[]{"series.default-prefix", DatasetTags.SERIES_TAG};
        r0[1] = new Object[]{"categories.default-prefix", "Categor?a"};
        CONTENTS = r0;
    }
}
