package com.affirm.common.util;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by john on 02/11/16.
 */
public class RandomNameGenerator {

    private static final String[] randomNamesMale = {
            "Santiago",
            "Sebastián",
            "Diego",
            "Nicolás",
            "Samuel",
            "Alejandro",
            "Daniel",
            "Mateo",
            "Ángel",
            "Matías",
            "Gabriel",
            "Tomás",
            "David",
            "Emiliano",
            "Andrés",
            "Joaquín",
            "Carlos",
            "Alexander",
            "Adrián",
            "Lucas"
    };
    private static final String[] randomNamesFemale = {
            "Sofía",
            "María",
            "Camila",
            "Isabella",
            "Mía",
            "Luna",
            "Sol",
            "Daniela",
            "Laura",
            "Fernanda",
            "Carolina",
            "Verónica",
            "Lucía",
            "Elena",
            "Aliza",
            "Bianca",
            "Julia",
            "Amanda",
            "Ariada",
            "Clara"
    };

    public static String[] getRandomNamesArray(Character gender, int totalLength, String inValue) {
        String[] randoms = new String[totalLength];
        for (int i = 0; i < totalLength; i++) {
            if (i == 0 && inValue != null) {
                randoms[i] = inValue;
            } else {
                Character realGender = gender;
                if (realGender == null) {
                    if (i % 2 == 0) {
                        realGender = 'M';
                    } else {
                        realGender = 'F';
                    }
                }
                if (realGender == 'M') {
                    randoms[i] = randomNamesMale[ThreadLocalRandom.current().nextInt(randomNamesMale.length)];
                } else {
                    randoms[i] = randomNamesFemale[ThreadLocalRandom.current().nextInt(randomNamesFemale.length)];
                }
            }
        }
        shuffleArray(randoms);
        return randoms;
    }

    private static void shuffleArray(String[] ar) {
        Random rnd = ThreadLocalRandom.current();
        for (int i = ar.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            String a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
    }

}
