package com.tny.game.oplog;

public class Alter<N> {

    private N value;

    private N lately;

    public static <N> Alter<N> of(N value) {
        return new Alter<N>(value);
    }

    public static <N> Alter<N> of(N value, N lately) {
        return new Alter<N>(value, lately);
    }

    private Alter(N value) {
        this.value = value;
    }

    private Alter(N value, N lately) {
        this.value = value;
        this.lately = lately;
    }

    public N getValue() {
        return this.value;
    }

    public N getLately() {
        return this.lately;
    }

    public void update(N alter) {
        if (alter == null)
            return;
        if (this.lately != null)
            this.lately = alter;
        else if (this.value == null || !this.value.equals(alter))
            this.lately = alter;
    }

    public boolean isChange() {
        return this.lately != null;
    }

    public String toString(String defaultValue) {
        if (this.lately == null) {
            return this.value == null ? defaultValue : this.value.toString();
        } else {
            return (this.value == null ? defaultValue : this.value.toString()) + "->" + this.lately;
        }
    }

    @Override
    public String toString() {
        if (this.lately == null) {
            return this.value.toString();
        } else {
            return this.value + "->" + this.lately;
        }
    }

    public boolean isHasValue() {
        return this.value != null;
    }

    public boolean isHasWorth() {
        return this.value != null || this.lately != null;
    }

}
