package net.lilydev.configurator.util;

public class SemifinalValue<V> {
    private V value;
    private boolean set = false;

    public SemifinalValue() {}

    public SemifinalValue(V initial) {
        this.value = initial;
    }

    public V get() {
        return this.value;
    }

    public void set(V value) {
        if (this.set) {
            throw new IllegalStateException("Can't modify a SemifinalValue at runtime!");
        }

        this.set = true;
        this.value = value;
    }
}
