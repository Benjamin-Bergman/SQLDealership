/*
 * Copyright (c) Benjamin Bergman 2024.
 */

package com.pluralsight;

/**
 * Represents a collection which can be added to, removed from, and iterated over.
 *
 * @param <T> The element type
 */
public interface SimpleList<T> extends Iterable<T> {
    /**
     * Adds an item to this collection.
     *
     * @param item The item to add
     */
    void add(T item);

    /**
     * Removes an item from this collection.
     *
     * @param item The item to remove
     * @return {@code true} iff the operation was successful
     */
    boolean remove(T item);
}
