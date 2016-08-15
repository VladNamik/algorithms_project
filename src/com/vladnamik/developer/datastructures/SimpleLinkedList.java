package com.vladnamik.developer.datastructures;

import java.util.*;

@SuppressWarnings("unused")
public class SimpleLinkedList<T> implements List<T>, Deque<T> {
    int size = 0;
    private T data = null;
    private SimpleLinkedList<T> next = null;
    private SimpleLinkedList<T> last = this;

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return (size == 0);
    }

    @Override
    public boolean contains(Object o) {
        SimpleLinkedList<T> element = this;
        for (int i = 0; i < size; i++) {
            if (o.equals(element.data))
                return true;
            element = element.next;
        }
        return false;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iter();
    }

    @Override
    public Object[] toArray() {
        Object[] array = new Object[size];
        int i = 0;
        for (T element : this)
            array[i++] = element;
        return array;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> E[] toArray(E[] a) {
        int i = 0;
        for (T element : this)
            if (i < a.length)
                a[i++] = (E) element;
            else
                break;
        return a;
    }

    @Override
    public boolean add(T e) {
        if (isEmpty())
            data = e;
        else {
            last.next = new SimpleLinkedList<>();
            last = last.next;
            last.data = e;
        }
        size++;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        if (!contains(o))
            return false;
        SimpleLinkedList<T> element = this;
        List<Integer> indexList = new SimpleLinkedList<>();
        for (int i = 0; i < size; i++)
            if (element.data.equals(o))
                indexList.add(i);
        int i = 0;
        for (Integer indexOfListElement : indexList)
            remove(indexOfListElement + i--);
        return true;
    }

    @Override
    public T remove(int index) {
        if (index >= size || index < 0)
            throw new NoSuchElementException();
        SimpleLinkedList<T> element;
        SimpleLinkedList<T> prevElement = this;
        T k = data;
        for (int i = 0; i < index - 1; i++)
            prevElement = prevElement.next;
        if (index != 0) {
            element = prevElement.next;
            k = element.data;
            element.data = null;
            prevElement.next = element.next;
        } else if (size == 1) {
            data = null;
            next = null;
        } else {
            data = next.data;
            next = next.next;
        }
        size--;
        updateLast();
        return k;
    }

    private void updateLast() {
        last = this;
        for (int i = 0; i < size; i++)
            last = last.next;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c)
            if (!contains(o))
                return false;
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        c.forEach(this::add);
        return true;
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        for (T element : c)
            add(index++, element);
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        c.forEach(this::remove);
        return true;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        SimpleLinkedList<T> thisList = this;
        for (int i = 0; i < size; i++) {
            if (!c.contains(thisList.data))
                remove(i--);
            thisList = thisList.next;
        }
        return true;
    }

    @Override
    public void clear() {
        for (int i = size - 1; i >= 0; i--)
            remove(i);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SimpleLinkedList)) {
            return false;
        } else {
            SimpleLinkedList listObject = (SimpleLinkedList) o;
            if (listObject.size() != size())
                return false;
            for (int i = 0; i < size; i++)
                if (!listObject.get(i).equals(get(i)))
                    return false;
            return true;
        }
    }

    @Override
    public int hashCode() {
        if (data == null)
            return 0;
        else
            return data.hashCode();
    }

    @Override
    public T get(int index) {
        if (index >= size || index < 0)
            throw new NoSuchElementException();
        SimpleLinkedList<T> element = this;
        for (int i = 0; i < index; i++)
            element = element.next;
        return element.data;
    }

    @Override
    public T set(int index, T element) {
        remove(index);
        add(index, element);
        return element;
    }

    @Override
    public void add(int index, T element) {
        if (index > size || index < 0)
            throw new ArrayIndexOutOfBoundsException();
        if (index == size) {
            add(element);
            return;
        }
        SimpleLinkedList<T> indexElement;
        SimpleLinkedList<T> prevElement = this;
        for (int i = 0; i < index - 1; i++)
            prevElement = prevElement.next;
        if (index != 0) {
            indexElement = prevElement.next;
            SimpleLinkedList<T> newElement = new SimpleLinkedList<>();
            newElement.data = element;
            newElement.next = indexElement;
            prevElement.next = newElement;
        } else {
            SimpleLinkedList<T> newElement = new SimpleLinkedList<>();
            newElement.data = data;
            newElement.next = next;
            data = element;
            next = newElement;
            if (size == 0)
                next = null;
        }
        size++;
        updateLast();
    }

    @Override
    public int indexOf(Object o) {
        SimpleLinkedList<T> element = this;
        int index = -1;
        for (int i = 0; i < size; i++) {
            if (element.data.equals(o)) {
                index = i;
                return index;
            }
            element = element.next;
        }
        return index;
    }

    @Override
    public int lastIndexOf(Object o) {
        SimpleLinkedList<T> element = this;
        int index = -1;
        for (int i = 0; i < size; i++) {
            if (element.data.equals(o))
                index = i;
            element = element.next;
        }
        return index;
    }

    @Override
    public ListIterator<T> listIterator() {
        throw new UnsupportedOperationException("listIterator");
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        throw new UnsupportedOperationException("listIterator");
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        if (fromIndex < 0)
            fromIndex = 0;
        if (toIndex > size)
            toIndex = size;
        if (fromIndex >= toIndex)
            return new SimpleLinkedList<>();
        if (fromIndex >= size && toIndex >= size)
            throw new ArrayIndexOutOfBoundsException();
        SimpleLinkedList<T> subList = new SimpleLinkedList<>();
        for (int i = fromIndex; i < toIndex; i++)
            subList.add(get(i));
        return subList;
    }

    @Override
    public void addFirst(T e) {
        add(0, e);
    }

    @Override
    public void addLast(T e) {
        add(e);
    }

    @Override
    public boolean offerFirst(T e) {
        add(0, e);
        return true;
    }

    @Override
    public boolean offerLast(T e) {
        return add(e);
    }

    @Override
    public T removeFirst() {
        return remove(0);
    }

    @Override
    public T removeLast() {
        return remove(size - 1);
    }

    @Override
    public T pollFirst() {
        return removeFirst();
    }

    @Override
    public T pollLast() {
        return removeLast();
    }

    @Override
    public T getFirst() {
        return get(0);
    }

    @Override
    public T getLast() {
        return get(size - 1);
    }

    @Override
    public T peekFirst() {
        return getFirst();
    }

    @Override
    public T peekLast() {
        return getLast();
    }

    @Override
    public boolean removeFirstOccurrence(Object o) {
        return remove(o);
    }

    @Override
    public boolean removeLastOccurrence(Object o) {
        return remove(o);
    }

    @Override
    public boolean offer(T e) {
        return add(e);
    }

    @Override
    public T poll() {
        return removeFirst();
    }

    @Override
    public T element() {
        return getFirst();
    }

    @Override
    public T peek() {
        return getFirst();
    }

    @Override
    public void push(T e) {
        addLast(e);
    }

    @Override
    public T pop() {
        return removeFirst();
    }

    @Override
    public Iterator<T> descendingIterator() {
        return iterator();
    }

    @Override
    public T remove() {
        return removeFirst();
    }

    private class Iter implements Iterator<T> {
        private SimpleLinkedList<T> element = SimpleLinkedList.this;

        @Override
        public boolean hasNext() {
            return element.next != null;
        }

        @Override
        public T next() {
            try {
                T thisData = element.data;
                element = element.next;
                return thisData;
            } catch (NullPointerException e) {
                throw new NoSuchElementException();
            }
        }
    }
}