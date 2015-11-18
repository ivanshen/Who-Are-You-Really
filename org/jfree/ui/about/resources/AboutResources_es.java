package org.jfree.ui.about.resources;

import java.util.ListResourceBundle;
import javax.swing.KeyStroke;

public class AboutResources_es extends ListResourceBundle {
    private static final Object[][] CONTENTS;

    public Object[][] getContents() {
        return CONTENTS;
    }

    static {
        Object[][] objArr = new Object[25][];
        objArr[0] = new Object[]{"about-frame.tab.about", "Acerca"};
        objArr[1] = new Object[]{"about-frame.tab.system", "Sistema"};
        objArr[2] = new Object[]{"about-frame.tab.contributors", "Desarrolladores"};
        objArr[3] = new Object[]{"about-frame.tab.licence", "Licencia"};
        objArr[4] = new Object[]{"about-frame.tab.libraries", "Bibliotecas"};
        objArr[5] = new Object[]{"contributors-table.column.name", "Nombre:"};
        objArr[6] = new Object[]{"contributors-table.column.contact", "Contacto:"};
        objArr[7] = new Object[]{"libraries-table.column.name", "Nombre::"};
        objArr[8] = new Object[]{"libraries-table.column.version", "Versi\u00f5n:"};
        objArr[9] = new Object[]{"libraries-table.column.licence", "Licencia:"};
        objArr[10] = new Object[]{"libraries-table.column.info", "Otra Informaci?n:"};
        objArr[11] = new Object[]{"system-frame.title", "propiedades del Sistema"};
        objArr[12] = new Object[]{"system-frame.button.close", "Cerrar"};
        objArr[13] = new Object[]{"system-frame.menu.file", "Archivo"};
        objArr[14] = new Object[]{"system-frame.menu.file.mnemonic", new Character('F')};
        objArr[15] = new Object[]{"system-frame.menu.file.close", "Cerrar"};
        objArr[16] = new Object[]{"system-frame.menu.file.close.mnemonic", new Character('C')};
        objArr[17] = new Object[]{"system-frame.menu.edit", "Edici?n"};
        objArr[18] = new Object[]{"system-frame.menu.edit.mnemonic", new Character('E')};
        objArr[19] = new Object[]{"system-frame.menu.edit.copy", "Copiar"};
        objArr[20] = new Object[]{"system-frame.menu.edit.copy.mnemonic", new Character('C')};
        objArr[21] = new Object[]{"system-properties-table.column.name", "Nombre de Propiedad:"};
        objArr[22] = new Object[]{"system-properties-table.column.value", "Valor:"};
        objArr[23] = new Object[]{"system-properties-panel.popup-menu.copy", "Copiar"};
        objArr[24] = new Object[]{"system-properties-panel.popup-menu.copy.accelerator", KeyStroke.getKeyStroke(67, 2)};
        CONTENTS = objArr;
    }
}
