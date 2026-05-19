package com.eventticketbooking.file;

import java.util.List;

/**
 * Interface for file read/write operations (demonstrates polymorphism).
 */
public interface FileHandler<T> {

    List<T> readAll();

    void writeAll(List<T> items);

    void append(T item);
}
