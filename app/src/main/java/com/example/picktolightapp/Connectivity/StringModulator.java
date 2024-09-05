package com.example.picktolightapp.Connectivity;

import java.util.ArrayList;
import java.util.List;

public class StringModulator {

    public static List<Integer> getIntegerListFromString(String input) {
        List<Integer> integerList = new ArrayList<>();

        if (input.startsWith("Status:")) {
            String[] parts = input.substring(7).split("%");

            for (String part : parts) {
                if (!part.isEmpty()) {
                    try {
                        integerList.add(Integer.parseInt(part.trim()));
                    } catch (NumberFormatException e) {
                        System.out.println("Errore nella conversione del numero: " + part);
                    }
                }
            }
        }

        return integerList;
    }

}
