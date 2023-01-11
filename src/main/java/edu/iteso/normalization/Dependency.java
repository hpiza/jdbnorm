package edu.iteso.normalization;
public class Dependency extends Pair<Key, Integer> {

    public Dependency(Key first, Integer second) {
        super(first, second);
    }

    public boolean equals(Object o) {
        if(!(o instanceof Dependency)) return false;
        Dependency d = (Dependency) o;
        return getFirst().equals(d.getFirst()) && getSecond() == d.getSecond();
    }

    public int hashCode() {
        return 0;
    }

}
