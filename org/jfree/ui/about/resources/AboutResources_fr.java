package org.jfree.ui.about.resources;

import java.util.ListResourceBundle;
import javax.swing.KeyStroke;

public class AboutResources_fr extends ListResourceBundle {
    private static final Object[][] CONTENTS;

    public Object[][] getContents() {
        return CONTENTS;
    }

    static {
        Object[][] objArr = new Object[25][];
        objArr[0] = new Object[]{"about-frame.tab.about", "A propos de"};
        objArr[1] = new Object[]{"about-frame.tab.system", "Syst\u00e8me"};
        objArr[2] = new Object[]{"about-frame.tab.contributors", "D\u00e9veloppeurs"};
        objArr[3] = new Object[]{"about-frame.tab.licence", "Licence"};
        objArr[4] = new Object[]{"about-frame.tab.libraries", "Biblioth\u00e8que"};
        objArr[5] = new Object[]{"contributors-table.column.name", "Nom:"};
        objArr[6] = new Object[]{"contributors-table.column.contact", "Contact:"};
        objArr[7] = new Object[]{"libraries-table.column.name", "Nom:"};
        objArr[8] = new Object[]{"libraries-table.column.version", "Version:"};
        objArr[9] = new Object[]{"libraries-table.column.licence", "Licence:"};
        objArr[10] = new Object[]{"libraries-table.column.info", "Autre Renseignement:"};
        objArr[11] = new Object[]{"system-frame.title", "Propri\u00e9t\u00e9s du Syst\u00e8me"};
        objArr[12] = new Object[]{"system-frame.button.close", "Fermer"};
        objArr[13] = new Object[]{"system-frame.menu.file", "Fichier"};
        objArr[14] = new Object[]{"system-frame.menu.file.mnemonic", new Character('F')};
        objArr[15] = new Object[]{"system-frame.menu.file.close", "Fermer"};
        objArr[16] = new Object[]{"system-frame.menu.file.close.mnemonic", new Character('C')};
        objArr[17] = new Object[]{"system-frame.menu.edit", "Edition"};
        objArr[18] = new Object[]{"system-frame.menu.edit.mnemonic", new Character('E')};
        objArr[19] = new Object[]{"system-frame.menu.edit.copy", "Copier"};
        objArr[20] = new Object[]{"system-frame.menu.edit.copy.mnemonic", new Character('C')};
        objArr[21] = new Object[]{"system-properties-table.column.name", "Nom de la Propri\u00e9t\u00e9:"};
        objArr[22] = new Object[]{"system-properties-table.column.value", "Valeur:"};
        objArr[23] = new Object[]{"system-properties-panel.popup-menu.copy", "Copier"};
        objArr[24] = new Object[]{"system-properties-panel.popup-menu.copy.accelerator", KeyStroke.getKeyStroke(67, 2)};
        CONTENTS = objArr;
    }
}
