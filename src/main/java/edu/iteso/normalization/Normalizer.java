package edu.iteso.normalization;

import java.util.Set;

public interface Normalizer {

    Database normalize(Table table);

    //Database normalize(Dataset dataset);

    NormalizerResult isNormalized(Table table);

}
