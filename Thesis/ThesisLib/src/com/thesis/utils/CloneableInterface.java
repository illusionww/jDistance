package com.thesis.utils;

public interface CloneableInterface<T> extends Cloneable {
    public T clone() throws CloneNotSupportedException;
}
