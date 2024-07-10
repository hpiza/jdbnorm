package edu.iteso.normalization;

import java.util.Set;

public interface DependencyCalculator {
    int isDependent(Table table, int key, int notKey);
    int isDependent(Table table, Set<Integer> compositeKey, int notKey);
}