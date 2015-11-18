package org.jfree.ui.about.resources;

import java.util.ListResourceBundle;
import javax.swing.KeyStroke;

public class AboutResources extends ListResourceBundle {
    private static final Object[][] CONTENTS;

    public Object[][] getContents() {
        return CONTENTS;
    }

    static {
        r0 = new Object[26][];
        r0[0] = new Object[]{"about-frame.tab.about", "About"};
        r0[1] = new Object[]{"about-frame.tab.system", "System"};
        r0[2] = new Object[]{"about-frame.tab.contributors", "Developers"};
        r0[3] = new Object[]{"about-frame.tab.licence", "Licence"};
        r0[4] = new Object[]{"about-frame.tab.libraries", "Libraries"};
        r0[5] = new Object[]{"contributors-table.column.name", "Name:"};
        r0[6] = new Object[]{"contributors-table.column.contact", "Contact:"};
        r0[7] = new Object[]{"libraries-table.column.name", "Name:"};
        r0[8] = new Object[]{"libraries-table.column.version", "Version:"};
        r0[9] = new Object[]{"libraries-table.column.licence", "Licence:"};
        r0[10] = new Object[]{"libraries-table.column.info", "Other Information:"};
        r0[11] = new Object[]{"system-frame.title", "System Properties"};
        r0[12] = new Object[]{"system-frame.button.close", "Close"};
        r0[13] = new Object[]{"system-frame.button.close.mnemonic", new Character('C')};
        r0[14] = new Object[]{"system-frame.menu.file", "File"};
        r0[15] = new Object[]{"system-frame.menu.file.mnemonic", new Character('F')};
        r0[16] = new Object[]{"system-frame.menu.file.close", "Close"};
        r0[17] = new Object[]{"system-frame.menu.file.close.mnemonic", new Character('C')};
        r0[18] = new Object[]{"system-frame.menu.edit", "Edit"};
        r0[19] = new Object[]{"system-frame.menu.edit.mnemonic", new Character('E')};
        r0[20] = new Object[]{"system-frame.menu.edit.copy", "Copy"};
        r0[21] = new Object[]{"system-frame.menu.edit.copy.mnemonic", new Character('C')};
        r0[22] = new Object[]{"system-properties-table.column.name", "Property Name:"};
        r0[23] = new Object[]{"system-properties-table.column.value", "Value:"};
        r0[24] = new Object[]{"system-properties-panel.popup-menu.copy", "Copy"};
        r0[25] = new Object[]{"system-properties-panel.popup-menu.copy.accelerator", KeyStroke.getKeyStroke(67, 2)};
        CONTENTS = r0;
    }
}
