package org.jfree.threads;

import java.util.ArrayList;
import java.util.Iterator;

public class ReaderWriterLock {
    private ArrayList waiters;

    private static class ReaderWriterNode {
        protected static final int READER = 0;
        protected static final int WRITER = 1;
        protected int nAcquires;
        protected int state;
        protected Thread t;

        private ReaderWriterNode(Thread t, int state) {
            this.t = t;
            this.state = state;
            this.nAcquires = READER;
        }
    }

    public ReaderWriterLock() {
        this.waiters = new ArrayList();
    }

    public synchronized void lockRead() {
        Thread me = Thread.currentThread();
        int index = getIndex(me);
        ReaderWriterNode node;
        if (index == -1) {
            node = new ReaderWriterNode(0, null);
            this.waiters.add(node);
        } else {
            node = (ReaderWriterNode) this.waiters.get(index);
        }
        while (getIndex(me) > firstWriter()) {
            try {
                wait();
            } catch (Exception e) {
                System.err.println("ReaderWriterLock.lockRead(): exception.");
                System.err.print(e.getMessage());
            }
        }
        node.nAcquires++;
    }

    public synchronized void lockWrite() {
        Thread me = Thread.currentThread();
        int index = getIndex(me);
        ReaderWriterNode node;
        if (index == -1) {
            node = new ReaderWriterNode(1, null);
            this.waiters.add(node);
        } else {
            node = (ReaderWriterNode) this.waiters.get(index);
            if (node.state == 0) {
                throw new IllegalArgumentException("Upgrade lock");
            }
            node.state = 1;
        }
        while (getIndex(me) != 0) {
            try {
                wait();
            } catch (Exception e) {
                System.err.println("ReaderWriterLock.lockWrite(): exception.");
                System.err.print(e.getMessage());
            }
        }
        node.nAcquires++;
    }

    public synchronized void unlock() {
        int index = getIndex(Thread.currentThread());
        if (index > firstWriter()) {
            throw new IllegalArgumentException("Lock not held");
        }
        ReaderWriterNode node = (ReaderWriterNode) this.waiters.get(index);
        node.nAcquires--;
        if (node.nAcquires == 0) {
            this.waiters.remove(index);
        }
        notifyAll();
    }

    private int firstWriter() {
        Iterator e = this.waiters.iterator();
        int index = 0;
        while (e.hasNext()) {
            if (((ReaderWriterNode) e.next()).state == 1) {
                return index;
            }
            index++;
        }
        return Integer.MAX_VALUE;
    }

    private int getIndex(Thread t) {
        Iterator e = this.waiters.iterator();
        int index = 0;
        while (e.hasNext()) {
            if (((ReaderWriterNode) e.next()).t == t) {
                return index;
            }
            index++;
        }
        return -1;
    }
}
