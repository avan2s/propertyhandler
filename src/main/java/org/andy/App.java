package org.andy;

/**
 * Created by Andy on 05.03.2017.
 */
public class App {

    public static void main(String[] args) {
        int appMin = PropertyHandler.getInstance().getAppMin();
        System.out.println(appMin);
    }
}
