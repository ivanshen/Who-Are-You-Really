package org.jfree.ui.about.resources;

import java.util.ListResourceBundle;
import javax.swing.KeyStroke;

public class AboutResources_pl extends ListResourceBundle {
    private static final Object[][] CONTENTS;

    public Object[][] getContents() {
        return CONTENTS;
    }

    static {
        r0 = new Object[26][];
        r0[0] = new Object[]{"about-frame.tab.about", "Informacja o"};
        r0[1] = new Object[]{"about-frame.tab.system", "System"};
        r0[2] = new Object[]{"about-frame.tab.contributors", "Tw\u00f3rcy"};
        r0[3] = new Object[]{"about-frame.tab.licence", "Licencja"};
        r0[4] = new Object[]{"about-frame.tab.libraries", "Biblioteki"};
        r0[5] = new Object[]{"contributors-table.column.name", "Nazwa:"};
        r0[6] = new Object[]{"contributors-table.column.contact", "Kontakt:"};
        r0[7] = new Object[]{"libraries-table.column.name", "Nazwa:"};
        r0[8] = new Object[]{"libraries-table.column.version", "Wersja:"};
        r0[9] = new Object[]{"libraries-table.column.licence", "Licencja:"};
        r0[10] = new Object[]{"libraries-table.column.info", "Inne informacje:"};
        r0[11] = new Object[]{"system-frame.title", "W?a\u015bciwo\u015bci systemowe"};
        r0[12] = new Object[]{"system-frame.button.close", "Zamknij"};
        r0[13] = new Object[]{"system-frame.button.close.mnemonic", new Character('Z')};
        r0[14] = new Object[]{"system-frame.menu.file", "Plik"};
        r0[15] = new Object[]{"system-frame.menu.file.mnemonic", new Character('P')};
        r0[16] = new Object[]{"system-frame.menu.file.close", "Zamknij"};
        r0[17] = new Object[]{"system-frame.menu.file.close.mnemonic", new Character('K')};
        r0[18] = new Object[]{"system-frame.menu.edit", "Edycja"};
        r0[19] = new Object[]{"system-frame.menu.edit.mnemonic", new Character('E')};
        r0[20] = new Object[]{"system-frame.menu.edit.copy", "Kopiuj"};
        r0[21] = new Object[]{"system-frame.menu.edit.copy.mnemonic", new Character('C')};
        r0[22] = new Object[]{"system-properties-table.column.name", "Nazwa w?a\u015bciwo\u015bci:"};
        r0[23] = new Object[]{"system-properties-table.column.value", "Warto\u015b\u0107:"};
        r0[24] = new Object[]{"system-properties-panel.popup-menu.copy", "Kopiuj"};
        r0[25] = new Object[]{"system-properties-panel.popup-menu.copy.accelerator", KeyStroke.getKeyStroke(67, 2)};
        CONTENTS = r0;
    }
}
