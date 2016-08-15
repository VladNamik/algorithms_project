package com.vladnamik.developer.datastructures;

import java.util.*;

@SuppressWarnings("unused")
public class ListPriorityQueue<T> implements Queue<T>, SimpleQueue<T> {
    private final Comparator<T> comparator;
    int size = 0;
    private T data = null;
    private ListPriorityQueue<T> next = null;

    public ListPriorityQueue(Comparator<T> comparator) {
        this.comparator = comparator;
    }

    @Override
    public boolean add(T e) {
        if (data == null && next == null) {
            data = e;
        } else {
            ListPriorityQueue<T> newElement = new ListPriorityQueue<>(comparator);
            newElement.data = e;
            ListPriorityQueue<T> element = this;
            if (comparator.compare(element.data, e) >= 0) {
                newElement.data = data;
                newElement.next = next;
                next = newElement;
                data = e;
                size++;
                return true;
            }
            while (element.next != null && comparator.compare(element.next.data, e) < 0)
                element = element.next;
            newElement.next = element.next;
            element.next = newElement;
        }
        size++;
        return true;
    }

    @Override
    public boolean offer(T e) {
        return add(e);
    }

    @Override
    public T remove() {
        if (data == null)
            throw new NoSuchElementException();
        return poll();
    }

    @Override
    public T poll() {
        T res = data;
        if (next != null) {
            data = next.data;
            next = next.next;
        } else {
            data = null;
        }
        if (size != 0)
            size--;
        return res;
    }


    @Override
    public T element() {
        if (data == null)
            throw new NoSuchElementException();
        else
            return data;
    }

    @Override
    public T peek() {
        return data;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(Object o) {
        for (T objects : this)
            if (objects.equals(o))
                return true;
        return false;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            ListPriorityQueue<T> list = ListPriorityQueue.this;

            public boolean hasNext() {
                return list != null && list.data != null;
            }

            @Override
            public T next() {
                if (list == null)
                    throw new NoSuchElementException();
                T res = list.data;
                list = list.next;
                return res;
            }
        };
    }

    private ListPriorityQueue<T> getElement(int index) {
        if (index >= size || index < 0)
            return null;
        ListPriorityQueue<T> element = this;
        for (int i = 0; i < index; i++)
            element = element.next;
        return element;
    }

    private ListPriorityQueue<T> getElement(T o) {
        ListPriorityQueue<T> element = this;
        for (int i = 0; i < size; i++)
            if (element.data.equals(o))
                return element;
        return null;
    }

    @Override
    public Object[] toArray() {
        Object[] array = new Object[size];
        int i = 0;
        for (T object : this)
            array[i++] = object;
        return array;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> E[] toArray(E[] a) {
        Object[] array = toArray();
        for (int i = 0; i < a.length && i < size; i++)
            a[i] = (E) array[i];
        return a;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean remove(Object o) {
        ListPriorityQueue<T> removedElement = getElement((T) o);
        if (removedElement == null)
            return false;
        else if (removedElement == this) {
            removedElement.remove();
            return true;
        } else
            removedElement.remove();
        size--;
        return true;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object object : c)
            if (!contains(object))
                return false;
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        c.forEach(this::add);
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        c.forEach(this::remove);
        return true;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        ListPriorityQueue<T> element = this;
        for (int i = 0; i < size; i++) {
            if (!c.contains(element.data))
                remove(i--);
            element = element.next;
        }
        return true;
    }

    public boolean remove(int index) {
        ListPriorityQueue<T> removedElement = getElement(index);
        if (removedElement == null)
            return false;
        else if (removedElement == this) {
            removedElement.remove();
            return true;
        } else {
            ListPriorityQueue<T> previousElement = getElement(index - 1);
            if (previousElement != null) {
                previousElement.next = previousElement.next.next;
            }
        }
        size--;
        return true;
    }

    @Override
    public void clear() {
        while (size > 0)
            remove();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ListPriorityQueue)) {
            return false;
        } else {
            ListPriorityQueue queueObject = (ListPriorityQueue) o;
            if (queueObject.size() != size())
                return false;
            for (int i = 0; i < size; i++)
                if (!queueObject.get(i).equals(get(i)))
                    return false;
            return true;
        }
    }

    public T get(int index) {
        if (getElement(index) == null) {
            return null;
        } else {
            ListPriorityQueue<T> element = getElement(index);
            if (element == null) {
                return null;
            }
            return element.data;
        }
    }

    @Override
    public int hashCode() {
        if (data == null)
            return 0;
        return data.hashCode();
    }

    @Override
    public void insert(T element) {
        add(element);
    }

    @Override
    public T extractMin() {
        return poll();
    }

    @Override
    public T extractMax() {
        T k = get(size - 1);
        remove(size - 1);
        return k;
    }
}