package org.jfree.ui.about;

public class Contributor {
    private String email;
    private String name;

    public Contributor(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return this.name;
    }

    public String getEmail() {
        return this.email;
    }
}
