package edu.iteso.normalization;

import java.util.Map;

public class NormalizerFactory {

    private static final Map<String, Normalizer> instances =
            Map.of("First", new FirstNF(),
                   "Second", new SecondNF(),
                   "Third",  new ThirdNF());

    public static Normalizer getFirstNF() {
        return instances.get("First");
    }

    public static Normalizer getSecondNF() {
        return instances.get("Second");
    }

    public static Normalizer getThirdNF() {
        return instances.get("Third");
    }

}
