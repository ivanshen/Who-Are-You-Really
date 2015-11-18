package org.jfree.chart.annotations;

import org.jfree.chart.event.AnnotationChangeListener;

public interface Annotation {
    void addChangeListener(AnnotationChangeListener annotationChangeListener);

    void removeChangeListener(AnnotationChangeListener annotationChangeListener);
}
