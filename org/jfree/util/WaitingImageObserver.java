package org.jfree.util;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.Serializable;

public class WaitingImageObserver implements ImageObserver, Serializable, Cloneable {
    static final long serialVersionUID = -807204410581383550L;
    private boolean error;
    private Image image;
    private boolean lock;

    public WaitingImageObserver(Image image) {
        if (image == null) {
            throw new NullPointerException();
        }
        this.image = image;
        this.lock = true;
    }

    public synchronized boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
        boolean z = false;
        synchronized (this) {
            if ((infoflags & 32) == 32) {
                this.lock = false;
                this.error = false;
                notifyAll();
            } else if ((infoflags & 128) == 128 || (infoflags & 64) == 64) {
                this.lock = false;
                this.error = true;
                notifyAll();
            } else {
                z = true;
            }
        }
        return z;
    }

    public synchronized void waitImageLoaded() {
        if (this.lock) {
            BufferedImage img = new BufferedImage(1, 1, 1);
            Graphics g = img.getGraphics();
            while (this.lock && !g.drawImage(this.image, 0, 0, img.getWidth(this), img.getHeight(this), this)) {
                try {
                    wait(500);
                } catch (InterruptedException e) {
                    Log.info("WaitingImageObserver.waitImageLoaded(): InterruptedException thrown", e);
                }
            }
        }
    }

    public Object clone() throws CloneNotSupportedException {
        return (WaitingImageObserver) super.clone();
    }

    public boolean isLoadingComplete() {
        return !this.lock;
    }

    public boolean isError() {
        return this.error;
    }
}
