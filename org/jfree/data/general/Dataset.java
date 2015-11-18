package org.jfree.data.general;

public interface Dataset {
    void addChangeListener(DatasetChangeListener datasetChangeListener);

    DatasetGroup getGroup();

    void removeChangeListener(DatasetChangeListener datasetChangeListener);

    void setGroup(DatasetGroup datasetGroup);
}
