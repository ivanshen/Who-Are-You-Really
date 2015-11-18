package net.miginfocom.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import net.miginfocom.layout.AC;
import net.miginfocom.layout.BoundSize;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.ComponentWrapper;
import net.miginfocom.layout.ConstraintParser;
import net.miginfocom.layout.ContainerWrapper;
import net.miginfocom.layout.Grid;
import net.miginfocom.layout.LC;
import net.miginfocom.layout.LayoutCallback;
import net.miginfocom.layout.LayoutUtil;
import net.miginfocom.layout.PlatformDefaults;
import net.miginfocom.layout.UnitValue;
import org.jfree.chart.plot.Plot;

public final class MigLayout implements LayoutManager2, Externalizable {
    private transient ContainerWrapper cacheParentW;
    private transient ArrayList<LayoutCallback> callbackList;
    private final transient Map<ComponentWrapper, CC> ccMap;
    private Object colConstraints;
    private transient AC colSpecs;
    private transient Timer debugTimer;
    private transient boolean dirty;
    private transient Grid grid;
    private transient int lastHash;
    private transient Dimension lastInvalidSize;
    private transient int lastModCount;
    private transient Dimension lastParentSize;
    private long lastSize;
    private transient boolean lastWasInvalid;
    private Object layoutConstraints;
    private transient LC lc;
    private Object rowConstraints;
    private transient AC rowSpecs;
    private final Map<Component, Object> scrConstrMap;

    class 1 implements Runnable {
        final /* synthetic */ Component val$parent;

        1(Component component) {
            this.val$parent = component;
        }

        public void run() {
            Container parent = this.val$parent.getParent();
            if (parent == null) {
                return;
            }
            if (parent instanceof JComponent) {
                ((JComponent) parent).revalidate();
                return;
            }
            this.val$parent.invalidate();
            parent.validate();
        }
    }

    class 2 implements Runnable {
        final /* synthetic */ ContainerWrapper val$containerWrapper;

        2(ContainerWrapper containerWrapper) {
            this.val$containerWrapper = containerWrapper;
        }

        public void run() {
            MigLayout.this.adjustWindowSize(this.val$containerWrapper);
        }
    }

    private class MyDebugRepaintListener implements ActionListener {
        private MyDebugRepaintListener() {
        }

        public void actionPerformed(ActionEvent actionEvent) {
            if (MigLayout.this.grid == null || !((Component) MigLayout.this.grid.getContainer().getComponent()).isShowing()) {
                MigLayout.this.debugTimer.stop();
                MigLayout.this.debugTimer = null;
                return;
            }
            MigLayout.this.grid.paintDebug();
        }
    }

    public MigLayout() {
        this("", "", "");
    }

    public MigLayout(String str) {
        this(str, "", "");
    }

    public MigLayout(String str, String str2) {
        this(str, str2, "");
    }

    public MigLayout(String str, String str2, String str3) {
        this.scrConstrMap = new IdentityHashMap(8);
        this.layoutConstraints = "";
        this.colConstraints = "";
        this.rowConstraints = "";
        this.cacheParentW = null;
        this.ccMap = new HashMap(8);
        this.debugTimer = null;
        this.lc = null;
        this.colSpecs = null;
        this.rowSpecs = null;
        this.grid = null;
        this.lastModCount = PlatformDefaults.getModCount();
        this.lastHash = -1;
        this.lastInvalidSize = null;
        this.lastWasInvalid = false;
        this.lastParentSize = null;
        this.callbackList = null;
        this.dirty = true;
        this.lastSize = 0;
        setLayoutConstraints(str);
        setColumnConstraints(str2);
        setRowConstraints(str3);
    }

    public MigLayout(LC lc) {
        this(lc, null, null);
    }

    public MigLayout(LC lc, AC ac) {
        this(lc, ac, null);
    }

    public MigLayout(LC lc, AC ac, AC ac2) {
        this.scrConstrMap = new IdentityHashMap(8);
        this.layoutConstraints = "";
        this.colConstraints = "";
        this.rowConstraints = "";
        this.cacheParentW = null;
        this.ccMap = new HashMap(8);
        this.debugTimer = null;
        this.lc = null;
        this.colSpecs = null;
        this.rowSpecs = null;
        this.grid = null;
        this.lastModCount = PlatformDefaults.getModCount();
        this.lastHash = -1;
        this.lastInvalidSize = null;
        this.lastWasInvalid = false;
        this.lastParentSize = null;
        this.callbackList = null;
        this.dirty = true;
        this.lastSize = 0;
        setLayoutConstraints(lc);
        setColumnConstraints(ac);
        setRowConstraints(ac2);
    }

    private void adjustWindowSize(ContainerWrapper containerWrapper) {
        BoundSize packWidth = this.lc.getPackWidth();
        BoundSize packHeight = this.lc.getPackHeight();
        if (packWidth != null || packHeight != null) {
            Window window = (Window) SwingUtilities.getAncestorOfClass(Window.class, (Component) containerWrapper.getComponent());
            if (window != null) {
                Dimension preferredSize = window.getPreferredSize();
                int constrain = constrain(checkParent(window), window.getWidth(), preferredSize.width, packWidth);
                int constrain2 = constrain(checkParent(window), window.getHeight(), preferredSize.height, packHeight);
                window.setBounds(Math.round(((float) window.getX()) - (((float) (constrain - window.getWidth())) * (Plot.DEFAULT_FOREGROUND_ALPHA - this.lc.getPackWidthAlign()))), Math.round(((float) window.getY()) - (((float) (constrain2 - window.getHeight())) * (Plot.DEFAULT_FOREGROUND_ALPHA - this.lc.getPackHeightAlign()))), constrain, constrain2);
            }
        }
    }

    private void checkCache(Container container) {
        boolean z = true;
        if (container != null) {
            if (this.dirty) {
                this.grid = null;
            }
            int modCount = PlatformDefaults.getModCount();
            if (this.lastModCount != modCount) {
                this.grid = null;
                this.lastModCount = modCount;
            }
            if (container.isValid()) {
                this.lastWasInvalid = false;
            } else if (!this.lastWasInvalid) {
                this.lastWasInvalid = true;
                boolean z2 = false;
                int i = 0;
                for (ComponentWrapper componentWrapper : this.ccMap.keySet()) {
                    Object component = componentWrapper.getComponent();
                    if ((component instanceof JTextArea) || (component instanceof JEditorPane)) {
                        z2 = true;
                    }
                    i = componentWrapper.getLayoutHashCode() + i;
                }
                if (z2) {
                    resetLastInvalidOnParent(container);
                }
                if (i != this.lastHash) {
                    this.grid = null;
                    this.lastHash = i;
                }
                Dimension size = container.getSize();
                if (this.lastInvalidSize == null || !this.lastInvalidSize.equals(size)) {
                    if (this.grid != null) {
                        this.grid.invalidateContainerSize();
                    }
                    this.lastInvalidSize = size;
                }
            }
            Object checkParent = checkParent(container);
            if (getDebugMillis() <= 0) {
                z = false;
            }
            setDebug(checkParent, z);
            if (this.grid == null) {
                this.grid = new Grid(checkParent, this.lc, this.rowSpecs, this.colSpecs, this.ccMap, this.callbackList);
            }
            this.dirty = false;
        }
    }

    private ContainerWrapper checkParent(Container container) {
        if (container == null) {
            return null;
        }
        if (this.cacheParentW == null || this.cacheParentW.getComponent() != container) {
            this.cacheParentW = new SwingContainerWrapper(container);
        }
        return this.cacheParentW;
    }

    private int constrain(ContainerWrapper containerWrapper, int i, int i2, BoundSize boundSize) {
        if (boundSize == null) {
            return i;
        }
        UnitValue preferred = boundSize.getPreferred();
        int constrain = boundSize.constrain(preferred != null ? preferred.getPixels((float) i2, containerWrapper, containerWrapper) : i, (float) i2, containerWrapper);
        if (boundSize.getGapPush()) {
            constrain = Math.max(i, constrain);
        }
        return constrain;
    }

    private boolean getDebug() {
        return this.debugTimer != null;
    }

    private int getDebugMillis() {
        int globalDebugMillis = LayoutUtil.getGlobalDebugMillis();
        return globalDebugMillis > 0 ? globalDebugMillis : this.lc.getDebugMillis();
    }

    private Dimension getSizeImpl(Container container, int i) {
        int[] iArr = null;
        checkCache(container);
        Insets insets = container.getInsets();
        int sizeSafe = (LayoutUtil.getSizeSafe(this.grid != null ? this.grid.getWidth() : null, i) + insets.left) + insets.right;
        if (this.grid != null) {
            iArr = this.grid.getHeight();
        }
        return new Dimension(sizeSafe, (LayoutUtil.getSizeSafe(iArr, i) + insets.top) + insets.bottom);
    }

    private Object readResolve() throws ObjectStreamException {
        return LayoutUtil.getSerializedObject(this);
    }

    private void resetLastInvalidOnParent(Container container) {
        while (container != null) {
            LayoutManager layout = container.getLayout();
            if (layout instanceof MigLayout) {
                ((MigLayout) layout).lastWasInvalid = false;
            }
            container = container.getParent();
        }
    }

    private void setComponentConstraintsImpl(Component component, Object obj, boolean z) {
        Container parent = component.getParent();
        synchronized ((parent != null ? parent.getTreeLock() : new Object())) {
            if (!z) {
                if (!this.scrConstrMap.containsKey(component)) {
                    throw new IllegalArgumentException("Component must already be added to parent!");
                }
            }
            SwingComponentWrapper swingComponentWrapper = new SwingComponentWrapper(component);
            if (obj == null || (obj instanceof String)) {
                String prepare = ConstraintParser.prepare((String) obj);
                this.scrConstrMap.put(component, obj);
                this.ccMap.put(swingComponentWrapper, ConstraintParser.parseComponentConstraint(prepare));
            } else if (obj instanceof CC) {
                this.scrConstrMap.put(component, obj);
                this.ccMap.put(swingComponentWrapper, (CC) obj);
            } else {
                throw new IllegalArgumentException("Constraint must be String or ComponentConstraint: " + obj.getClass().toString());
            }
            this.dirty = true;
        }
    }

    private void setDebug(ComponentWrapper componentWrapper, boolean z) {
        if (z && (this.debugTimer == null || this.debugTimer.getDelay() != getDebugMillis())) {
            if (this.debugTimer != null) {
                this.debugTimer.stop();
            }
            ContainerWrapper parent = componentWrapper.getParent();
            Component component = parent != null ? (Component) parent.getComponent() : null;
            this.debugTimer = new Timer(getDebugMillis(), new MyDebugRepaintListener());
            if (component != null) {
                SwingUtilities.invokeLater(new 1(component));
            }
            this.debugTimer.setInitialDelay(100);
            this.debugTimer.start();
        } else if (!z && this.debugTimer != null) {
            this.debugTimer.stop();
            this.debugTimer = null;
        }
    }

    public void addLayoutCallback(LayoutCallback layoutCallback) {
        if (layoutCallback == null) {
            throw new NullPointerException();
        }
        if (this.callbackList == null) {
            this.callbackList = new ArrayList(1);
        }
        this.callbackList.add(layoutCallback);
    }

    public void addLayoutComponent(Component component, Object obj) {
        synchronized (component.getParent().getTreeLock()) {
            setComponentConstraintsImpl(component, obj, true);
        }
    }

    public void addLayoutComponent(String str, Component component) {
        addLayoutComponent(component, (Object) str);
    }

    public Object getColumnConstraints() {
        return this.colConstraints;
    }

    public Object getComponentConstraints(Component component) {
        Object obj;
        synchronized (component.getParent().getTreeLock()) {
            obj = this.scrConstrMap.get(component);
        }
        return obj;
    }

    public Map<Component, Object> getConstraintMap() {
        return new IdentityHashMap(this.scrConstrMap);
    }

    public float getLayoutAlignmentX(Container container) {
        return (this.lc == null || this.lc.getAlignX() == null) ? 0.0f : (float) this.lc.getAlignX().getPixels(Plot.DEFAULT_FOREGROUND_ALPHA, checkParent(container), null);
    }

    public float getLayoutAlignmentY(Container container) {
        return (this.lc == null || this.lc.getAlignY() == null) ? 0.0f : (float) this.lc.getAlignY().getPixels(Plot.DEFAULT_FOREGROUND_ALPHA, checkParent(container), null);
    }

    public Object getLayoutConstraints() {
        return this.layoutConstraints;
    }

    public Object getRowConstraints() {
        return this.rowConstraints;
    }

    public void invalidateLayout(Container container) {
        this.dirty = true;
    }

    public boolean isManagingComponent(Component component) {
        return this.scrConstrMap.containsKey(component);
    }

    public void layoutContainer(Container container) {
        synchronized (container.getTreeLock()) {
            checkCache(container);
            Insets insets = container.getInsets();
            int[] iArr = new int[]{insets.left, insets.top, (container.getWidth() - insets.left) - insets.right, (container.getHeight() - insets.top) - insets.bottom};
            if (this.grid.layout(iArr, this.lc.getAlignX(), this.lc.getAlignY(), getDebug(), true)) {
                this.grid = null;
                checkCache(container);
                this.grid.layout(iArr, this.lc.getAlignX(), this.lc.getAlignY(), getDebug(), false);
            }
            long j = ((long) this.grid.getHeight()[1]) + (((long) this.grid.getWidth()[1]) << 32);
            if (this.lastSize != j) {
                this.lastSize = j;
                ContainerWrapper checkParent = checkParent(container);
                Window window = (Window) SwingUtilities.getAncestorOfClass(Window.class, (Component) checkParent.getComponent());
                if (window != null) {
                    if (window.isVisible()) {
                        SwingUtilities.invokeLater(new 2(checkParent));
                    } else {
                        adjustWindowSize(checkParent);
                    }
                }
            }
            this.lastInvalidSize = null;
        }
    }

    public Dimension maximumLayoutSize(Container container) {
        return new Dimension(32767, 32767);
    }

    public Dimension minimumLayoutSize(Container container) {
        Dimension sizeImpl;
        synchronized (container.getTreeLock()) {
            sizeImpl = getSizeImpl(container, 0);
        }
        return sizeImpl;
    }

    public Dimension preferredLayoutSize(Container container) {
        Dimension sizeImpl;
        synchronized (container.getTreeLock()) {
            if (this.lastParentSize == null || !container.getSize().equals(this.lastParentSize)) {
                for (ComponentWrapper component : this.ccMap.keySet()) {
                    Component component2 = (Component) component.getComponent();
                    if ((component2 instanceof JTextArea) || (component2 instanceof JEditorPane) || ((component2 instanceof JComponent) && Boolean.TRUE.equals(((JComponent) component2).getClientProperty("migLayout.dynamicAspectRatio")))) {
                        layoutContainer(container);
                        break;
                    }
                }
            }
            this.lastParentSize = container.getSize();
            sizeImpl = getSizeImpl(container, 1);
        }
        return sizeImpl;
    }

    public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
        LayoutUtil.setSerializedObject(this, LayoutUtil.readAsXML(objectInput));
    }

    public void removeLayoutCallback(LayoutCallback layoutCallback) {
        if (this.callbackList != null) {
            this.callbackList.remove(layoutCallback);
        }
    }

    public void removeLayoutComponent(Component component) {
        synchronized (component.getParent().getTreeLock()) {
            this.scrConstrMap.remove(component);
            this.ccMap.remove(new SwingComponentWrapper(component));
        }
    }

    public void setColumnConstraints(Object obj) {
        if (obj == null || (obj instanceof String)) {
            String prepare = ConstraintParser.prepare((String) obj);
            this.colSpecs = ConstraintParser.parseColumnConstraints(prepare);
            obj = prepare;
        } else if (obj instanceof AC) {
            this.colSpecs = (AC) obj;
        } else {
            throw new IllegalArgumentException("Illegal constraint type: " + obj.getClass().toString());
        }
        this.colConstraints = obj;
        this.dirty = true;
    }

    public void setComponentConstraints(Component component, Object obj) {
        setComponentConstraintsImpl(component, obj, false);
    }

    public void setConstraintMap(Map<Component, Object> map) {
        this.scrConstrMap.clear();
        this.ccMap.clear();
        for (Entry entry : map.entrySet()) {
            setComponentConstraintsImpl((Component) entry.getKey(), entry.getValue(), true);
        }
    }

    public void setLayoutConstraints(Object obj) {
        if (obj == null || (obj instanceof String)) {
            String prepare = ConstraintParser.prepare((String) obj);
            this.lc = ConstraintParser.parseLayoutConstraint(prepare);
            obj = prepare;
        } else if (obj instanceof LC) {
            this.lc = (LC) obj;
        } else {
            throw new IllegalArgumentException("Illegal constraint type: " + obj.getClass().toString());
        }
        this.layoutConstraints = obj;
        this.dirty = true;
    }

    public void setRowConstraints(Object obj) {
        if (obj == null || (obj instanceof String)) {
            String prepare = ConstraintParser.prepare((String) obj);
            this.rowSpecs = ConstraintParser.parseRowConstraints(prepare);
            obj = prepare;
        } else if (obj instanceof AC) {
            this.rowSpecs = (AC) obj;
        } else {
            throw new IllegalArgumentException("Illegal constraint type: " + obj.getClass().toString());
        }
        this.rowConstraints = obj;
        this.dirty = true;
    }

    public void writeExternal(ObjectOutput objectOutput) throws IOException {
        if (getClass() == MigLayout.class) {
            LayoutUtil.writeAsXML(objectOutput, this);
        }
    }
}
