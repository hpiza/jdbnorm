package edu.iteso.normalization;

import java.util.ArrayList;
import java.util.List;

public class NormalizerResult {

    public final boolean isNormalized;
    public final List<String> anomalyList;

    public static final NormalizerResult NORMALIZED = new NormalizerResult(true, List.of());

    public NormalizerResult(boolean isNormalized, List<String> anomalyList) {
        this.isNormalized = isNormalized;
        this.anomalyList = new ArrayList<>(anomalyList);
    }

    public static NormalizerResult defaultInstance() {
        return NORMALIZED;
    }

    @Override
    public String toString() {
        if(isNormalized) return "Normalized";
        return "Not normalized. Anomalies found: " + anomalyList.toString();
    }
}
