package net.waret.demo.alice.domain;

public class Contract {

    private String name;

    public Contract(String name) {
        this.setName(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
