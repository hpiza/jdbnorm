package edu.iteso.normalization;

public abstract class Normalizer {

    private DependencyCalculator dependencyCalculator = null;

    public void setDependencyCalculator(DependencyCalculator dependencyCalculator) {
        if(dependencyCalculator != null) this.dependencyCalculator = dependencyCalculator;
    }

    public DependencyCalculator getDependencyCalculator() {
        if(this.dependencyCalculator == null) this.dependencyCalculator = StandardDependencyCalculator.getInstance();
        return this.dependencyCalculator;
    }

    public abstract Database normalize(Table table);
    public abstract NormalizerResult isNormalized(Table table);
}

