package com.jdistance.learning.gridsearch;

import java.io.Serializable;
import java.util.*;

public class LinkedList<E> implements Serializable {
    public Node<E> first = null;
    public Node<E> last = null;
    public int size = 0;

    public LinkedList(List<? extends E> items) {
        for (E item : items) {
            linkLast(item);
        }
    }

    public void linkLast(E item) {
        final Node<E> newNode = new Node<>(last, item, null);
        if (last == null) {
            first = newNode;
        } else {
            last.next = newNode;
        }
        last = newNode;
        size++;
    }

    public E unlink(Node<E> item) {
        final E element = item.item;
        final Node<E> next = item.next;
        final Node<E> prev = item.prev;

        if (prev == null) {
            first = next;
        } else {
            prev.next = next;
            item.prev = null;
        }
        if (next == null) {
            last = prev;
        } else {
            next.prev = prev;
            item.next = null;
        }
        item.item = null;
        size--;
        return element;
    }

    public static class Node<E> {
        public E item;
        public LinkedList.Node<E> next;
        LinkedList.Node<E> prev;

        Node(LinkedList.Node<E> prev, E element, LinkedList.Node<E> next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }
    }
}