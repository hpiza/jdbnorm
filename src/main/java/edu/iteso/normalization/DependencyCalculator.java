package edu.iteso.normalization;

import java.util.Set;

public interface DependencyCalculator {
    boolean isDependent(Table table, int key, int notKey);
    boolean isDependent(Table table, Set<Integer> compositeKey, int notKey);
}