package chapter01.item07;

import java.util.Arrays;
import java.util.EmptyStackException;

public class Stack {
    public Object[] elements;
    public int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 10;

    public Stack() {
        elements = new Object[DEFAULT_INITIAL_CAPACITY];
    }

    public void push(Object element) {
        ensureCapacity();
        elements[size++] = element;
    }

    public Object pop() {
        if (size == 0) {
            throw new EmptyStackException();
        }
        Object element = elements[--size];
        elements[size] = null;
        return element;
    }

    public long length() {
        return elements.length;
    }

    private void ensureCapacity() {
        if (elements.length == size) {
            elements = Arrays.copyOf(elements, size * 2);
        }
    }
}

class Main {
    public static void main(String[] args) {
        Stack stack = new Stack();

        Object[] elements = stack.elements;
        stack = null;

        System.out.println(stack.elements);
        System.out.println(stack.size);
        System.out.println("--------------");
    }
}
