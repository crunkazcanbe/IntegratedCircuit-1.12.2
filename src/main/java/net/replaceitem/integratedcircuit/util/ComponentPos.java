package net.replaceitem.integratedcircuit.util;

import net.minecraft.util.math.Vec3i;

public class ComponentPos extends Vec3i { // using vec3, but only 2d required

    public ComponentPos(int x, int y) {
        super(x, y, 0);
    }

    public ComponentPos add(int x, int y) {
        return new ComponentPos(getX() + x, getY() + y);
    }

    public ComponentPos offset(Vec3i vec3i) {
        return this.add(vec3i.getX(), vec3i.getY());
    }

    public ComponentPos multiply(int amount) {
        return new ComponentPos(getX() * amount, getY() * amount);
    }

    public ComponentPos offset(FlatDirection direction, int amount) {
        Vec3i o = direction.getOffset();
        return this.add(o.getX() * amount, o.getY() * amount);
    }

    public ComponentPos offset(FlatDirection direction) {
        return this.offset(direction.getOffset());
    }

    public ComponentPos north() {
        return this.offset(FlatDirection.NORTH);
    }

    public ComponentPos east() {
        return this.offset(FlatDirection.EAST);
    }

    public ComponentPos south() {
        return this.offset(FlatDirection.SOUTH);
    }

    public ComponentPos west() {
        return this.offset(FlatDirection.WEST);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ComponentPos)) return false;
        ComponentPos componentPos = (ComponentPos) o;
        return this.getX() == componentPos.getX() && this.getY() == componentPos.getY();
    }

    @Override
    public int hashCode() {
        return getX() * 31 + getY();
    }
}
