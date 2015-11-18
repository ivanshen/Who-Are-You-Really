package org.jfree.ui.action;

import java.awt.Component;
import java.io.File;
import javax.swing.JFileChooser;
import org.jfree.ui.ExtensionFileFilter;
import org.jfree.util.StringUtils;

public abstract class AbstractFileSelectionAction extends AbstractActionDowngrade {
    private JFileChooser fileChooser;
    private Component parent;

    protected abstract String getFileDescription();

    protected abstract String getFileExtension();

    public AbstractFileSelectionAction(Component parent) {
        this.parent = parent;
    }

    protected File getCurrentDirectory() {
        return new File(".");
    }

    protected File performSelectFile(File selectedFile, int dialogType, boolean appendExtension) {
        if (this.fileChooser == null) {
            this.fileChooser = createFileChooser();
        }
        this.fileChooser.setSelectedFile(selectedFile);
        this.fileChooser.setDialogType(dialogType);
        if (this.fileChooser.showDialog(this.parent, null) != 0) {
            return null;
        }
        String selFileName = this.fileChooser.getSelectedFile().getAbsolutePath();
        if (!StringUtils.endsWithIgnoreCase(selFileName, getFileExtension())) {
            selFileName = selFileName + getFileExtension();
        }
        return new File(selFileName);
    }

    protected JFileChooser createFileChooser() {
        JFileChooser fc = new JFileChooser();
        fc.addChoosableFileFilter(new ExtensionFileFilter(getFileDescription(), getFileExtension()));
        fc.setMultiSelectionEnabled(false);
        fc.setCurrentDirectory(getCurrentDirectory());
        return fc;
    }
}
