package org.jfree.date;

public class EasterSundayRule extends AnnualDateRule {
    public SerialDate getDate(int year) {
        int g = year % 19;
        int c = year / 100;
        int h = ((((c - (c / 4)) - (((c * 8) + 13) / 25)) + (g * 19)) + 15) % 30;
        int i = h - ((h / 28) * (1 - (((((h / 28) * 29) / (h + 1)) * (21 - g)) / 11)));
        int l = i - (((((((year / 4) + year) + i) + 2) - c) + (c / 4)) % 7);
        int month = ((l + 40) / 44) + 3;
        return SerialDate.createInstance((l + 28) - ((month / 4) * 31), month, year);
    }
}
