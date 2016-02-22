package com.vladnaminas.developer;

import java.util.*;

public class DataStructures {

    public static class SimpleLinkedList<T> implements List<T>, Deque<T>//простой список
    {
        private T data=null;
        private SimpleLinkedList<T> next=null;
        private SimpleLinkedList<T> last=this;
        int size=0;

        @Override
        public int size()
        {
            return size;
        }

        @Override
        public boolean isEmpty()
        {
            return (size==0);
        }

        @Override
        public boolean contains(Object o)
        {
            SimpleLinkedList<T> element = this;
            for (int i=0; i<size; i++) {
                if (o.equals(element.data))
                    return true;
                element=element.next;
            }
            return false;
        }

        @Override
        public Iterator<T> iterator()
        {
            return new Iter();
        }
        private class Iter implements Iterator<T> {
            private SimpleLinkedList<T> element=SimpleLinkedList.this;
            @Override
            public boolean hasNext() {
                return element.next!=null;
            }

            @Override
            public T next() {
                try {
                    T thisData = element.data;
                    element = element.next;
                    return thisData;
                }
                catch(NullPointerException e)
                {
                    throw new NoSuchElementException();
                }
            }
        }

        @Override
        public Object[] toArray()
        {
            Object[] array = new Object[size];
            int i=0;
            for (T element : this)
                array[i++]=element;
            return array;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <E> E[] toArray(E[] a)
        {
            int i = 0;
            for (T element : this)
                if (i < a.length)
                    a[i++] = (E) element;
                else
                    break;
            return a;
        }

        @Override
        public boolean add(T e)//NullPointerException ���������
        {
            if (isEmpty())
                data = e;
            else
            {
                last.next=new SimpleLinkedList<>();
                last=last.next;
                last.data=e;
            }
            size++;
            return true;
        }

        @Override
        public boolean remove(Object o)
        {
            if (!contains(o))
                return false;
            SimpleLinkedList<T> element = this;
            List<Integer> indexList = new SimpleLinkedList<>();
            for (int i =0; i < size; i++)
                if (element.data.equals(o))
                    indexList.add(i);
            int i=0;
            for (Integer indexOfListElement : indexList)
                remove(indexOfListElement + i--);
            return true;
        }

        @Override
        public T remove(int index)
        {
            if (index>=size || index<0)
                throw new NoSuchElementException();
            SimpleLinkedList<T> element ;
            SimpleLinkedList<T> prevElement = this;
            T k = data;
            for (int i=0; i< index-1; i++)
                prevElement=prevElement.next;
            if (index!=0) {
                element = prevElement.next;
                k = element.data;
                element.data = null;
                prevElement.next = element.next;
            }
            else if (size==1)
            {
                data=null;
                next=null;
            }
            else
            {
                data=next.data;
                next=next.next;
            }
            size--;
            updateLast();
            return k;
        }

        private void updateLast()
        {
            last = this;
            for (int i=0; i< size; i++)
                last=last.next;
        }
        @Override
        public boolean containsAll(Collection<?> c)
        {
            for (Object o : c)
                if (!contains(o))
                    return false;
            return true;
        }

        @Override
        public boolean addAll(Collection<? extends T> c)
        {
            c.forEach(this::add);
            return true;
        }

        @Override
        public boolean addAll(int index, Collection<? extends T> c)
        {
            for (T element : c)
                add(index++, element);
            return true;
        }

        @Override
        public boolean removeAll(Collection<?> c)
        {
            c.forEach(this::remove);
            return true;
        }

        @Override
        public boolean retainAll(Collection<?> c)
        {
            SimpleLinkedList<T> thisList = this;
            for (int i=0; i< size; i++)
            {
                if (!c.contains(thisList.data))
                    remove(i--);
                thisList=thisList.next;
            }
            return true;
        }

        @Override
        public void clear()
        {
            for (int i=size-1; i>=0; i--)
                remove(i);
        }

        @Override
        public boolean equals(Object o)
        {
            if (!(o instanceof SimpleLinkedList))
            {
                return false;
            }
            else
            {
                SimpleLinkedList listObject = (SimpleLinkedList)o;
                if (listObject.size()!=size())
                    return false;
                for (int i = 0; i < size; i++)
                    if (!listObject.get(i).equals(get(i)))
                        return false;
                return true;
            }
        }

        @Override
        public int hashCode()
        {
            if (data==null)
                return 0;
            else
                return data.hashCode();
        }

        @Override
        public T get(int index)
        {
            if (index>=size || index<0)
                throw new NoSuchElementException();
            SimpleLinkedList<T> element = this;
            for (int i = 0; i < index; i++)
                element=element.next;
            return element.data;
        }

        @Override
        public  T set(int index, T element)
        {
            remove(index);
            add(index,element);
            return element;
        }

        @Override
        public void add(int index, T element)
        {
            if (index>size || index<0)
                throw new ArrayIndexOutOfBoundsException();
            if (index==size) {
                add(element);
                return;
            }
            SimpleLinkedList<T> indexElement ;
            SimpleLinkedList<T> prevElement = this;
            for (int i=0; i< index-1; i++)
                prevElement=prevElement.next;
            if (index!=0) {
                indexElement = prevElement.next;
                SimpleLinkedList<T> newElement = new SimpleLinkedList<>();
                newElement.data = element;
                newElement.next = indexElement;
                prevElement.next = newElement;
            }
            else
            {
                SimpleLinkedList<T> newElement = new SimpleLinkedList<>();
                newElement.data = data;
                newElement.next = next;
                data=element;
                next=newElement;
                if (size==0)
                    next=null;
            }
            size++;
            updateLast();
        }

        @Override
        public int indexOf(Object o)
        {
            SimpleLinkedList<T> element = this;
            int index=-1;
            for (int i=0; i < size; i++)
            {
                if (element.data.equals(o))
                {
                    index=i;
                    return index;
                }
                element=element.next;
            }
            return index;
        }

        @Override
        public int lastIndexOf(Object o)
        {
            SimpleLinkedList<T> element = this;
            int index=-1;
            for (int i=0; i < size; i++)
            {
                if (element.data.equals(o))
                    index=i;
                element=element.next;
            }
            return index;
        }

        @Override
        public ListIterator<T> listIterator()
        {
            throw  new UnsupportedOperationException("listIterator");
        }

        @Override
        public ListIterator<T> listIterator(int index)
        {
            throw  new UnsupportedOperationException("listIterator");
        }

        @Override
        public List<T> subList(int fromIndex, int toIndex)
        {
            if (fromIndex<0)
                fromIndex=0;
            if (toIndex>size)
                toIndex=size;
            if (fromIndex >= toIndex)
                return new SimpleLinkedList<>();
            if (fromIndex>=size && toIndex>=size)
                throw new ArrayIndexOutOfBoundsException();
            SimpleLinkedList<T> subList = new SimpleLinkedList<>();
            for (int i=fromIndex; i< toIndex; i++)
                subList.add(get(i));
            return subList;
        }

        @Override
        public void addFirst(T e)
        {
            add(0,e);
        }

        @Override
        public void addLast(T e)
        {
            add(e);
        }

        @Override
        public boolean offerFirst(T e)
        {
            add(0,e);
            return true;
        }

        @Override
        public boolean offerLast(T e)
        {
            return add(e);
        }

        @Override
        public T removeFirst()
        {
            return remove(0);
        }

        @Override
        public T removeLast()
        {
            return remove(size-1);
        }

        @Override
        public T pollFirst()
        {
            return removeFirst();
        }

        @Override
        public T pollLast()
        {
            return removeLast();
        }

        @Override
        public T getFirst()
        {
            return get(0);
        }

        @Override
        public T getLast() { return get(size-1);}

        @Override
        public T peekFirst()
        {
            return getFirst();
        }

        @Override
        public T peekLast() { return getLast();}

        @Override
        public boolean removeFirstOccurrence(Object o)
        {
            return remove(o);
        }

        @Override
        public boolean removeLastOccurrence(Object o)
        {
            return remove(o);
        }

        @Override
        public boolean offer(T e)
        {
            return add(e);
        }

        @Override
        public T poll()
        {
            return removeFirst();
        }

        @Override
        public T element()
        {
            return getFirst();
        }

        @Override
        public T peek()
        {
            return getFirst();
        }

        @Override
        public void push(T e)
        {
            addLast(e);
        }

        @Override
        public T pop()
        {
            return removeFirst();
        }

        @Override
        public Iterator<T> descendingIterator()
        {
            return iterator();
        }

        @Override
        public T remove()
        {
            return removeFirst();
        }
    }

    interface SimpleQueue<T>
    {
        void insert(T element);
        T extractMin();
        T extractMax();
        int size();
    }

    public static class ListPriorityQueue<T> implements Queue<T> , SimpleQueue<T>//очередь с приоритетом на основе очереди
    {
        private T data=null;
        private ListPriorityQueue<T> next=null;
        private final Comparator<T> comparator;
        int size=0;

        ListPriorityQueue(Comparator<T> comparator)
        {
            this.comparator=comparator;
        }

        @Override
        public boolean add(T e)
        {
            if (data==null && next==null)
            {
                data=e;
            }
            else
            {
                ListPriorityQueue<T> newElement = new ListPriorityQueue<>(comparator);
                newElement.data=e;
                ListPriorityQueue<T> element = this;
                if (comparator.compare(element.data, e)>=0)
                {
                    newElement.data=data;
                    newElement.next=next;
                    next=newElement;
                    data=e;
                    size++;
                    return true;
                }
                while (element.next!=null && comparator.compare(element.next.data, e)<0)
                    element=element.next;
                newElement.next=element.next;
                element.next=newElement;
            }
            size++;
            return true;
        }

        @Override
        public boolean offer(T e)
        {
            return add(e);
        }

        @Override
        public T remove()
        {
            if (data==null)
                throw new NoSuchElementException();
            return poll();
        }

        @Override
        public T poll()
        {
            T res=data;
            if (next!=null)
            {
                data=next.data;
                next=next.next;
            }
            else
            {
                data=null;
            }
            if (size!=0)
                size--;
            return res;
        }


        @Override
        public T element()
        {
            if (data==null)
                throw new NoSuchElementException();
            else
                return data;
        }

        @Override
        public T peek()
        {
            return data;
        }

        @Override
        public int size()
        {
            return size;
        }

        @Override
        public boolean isEmpty()
        {
            return size==0;
        }

        @Override
        public boolean contains(Object o)
        {
            for (T objects : this)
                if (objects.equals(o))
                    return true;
            return false;
        }

        @Override
        public Iterator<T> iterator()
        {
            return new Iterator<T>() {
                ListPriorityQueue<T> list=ListPriorityQueue.this;
                public boolean hasNext() {
                    return list!=null && list.data!=null;
                }

                @Override
                public T next() {
                    if (list==null)
                        throw new NoSuchElementException();
                    T res = list.data;
                    list=list.next;
                    return res;
                }
            };
        }

        private ListPriorityQueue<T> getElement(int index)
        {
            if (index>=size || index <0)
                return null;
            ListPriorityQueue<T> element = this;
            for (int i = 0; i < index; i++)
                element=element.next;
            return element;
        }

        private ListPriorityQueue<T> getElement(T o)
        {
            ListPriorityQueue<T> element = this;
            for (int i = 0; i < size; i++)
                if (element.data.equals(o))
                    return element;
            return null;
        }

        @Override
        public Object[] toArray()
        {
            Object[] array = new Object[size];
            int i=0;
            for (T object : this)
                array[i++]=object;
            return array;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <E> E[] toArray(E[] a)
        {
            Object[] array= toArray();
            for (int i = 0; i < a.length && i < size; i++)
                a[i] = (E) array[i];
            return a;
        }

        @Override
        @SuppressWarnings("unchecked")
        public boolean remove(Object o)
        {
            ListPriorityQueue<T> removedElement = getElement((T)o);
            if (removedElement==null)
                return false;
            else if (removedElement==this)
            {
                removedElement.remove();
                return true;
            }
            else
                removedElement.remove();
            size--;
            return true;
        }

        @Override
        public boolean containsAll(Collection<?> c)
        {
            for (Object object : c)
                if (!contains(object))
                    return false;
            return true;
        }

        @Override
        public boolean addAll(Collection<? extends T> c)
        {
            c.forEach(this::add);
            return true;
        }

        @Override
        public boolean removeAll(Collection<?> c)
        {
            c.forEach(this::remove);
            return true;
        }

        @Override
        public boolean retainAll(Collection<?> c)
        {
            ListPriorityQueue<T> element = this;
            for (int i=0; i< size; i++)
            {
                if (!c.contains(element.data))
                    remove(i--);
                element=element.next;
            }
            return true;
        }

        public boolean remove(int index)
        {
            ListPriorityQueue<T> removedElement = getElement(index);
            if (removedElement==null)
                return false;
            else if (removedElement==this)
            {
                removedElement.remove();
                return true;
            }
            else
            {
                ListPriorityQueue<T> kkk =getElement(index-1);
                kkk.next=kkk.next.next;
            }
            size--;
            return true;
        }

        @Override
        public void clear()
        {
            while(size>0)
                remove();
        }

        @Override
        public boolean equals(Object o)
        {
            if (!(o instanceof ListPriorityQueue))
            {
                return false;
            }
            else
            {
                ListPriorityQueue queueObject = (ListPriorityQueue)o;
                if (queueObject.size()!=size())
                    return false;
                for (int i = 0; i < size; i++)
                    if (!queueObject.get(i).equals(get(i)))
                        return false;
                return true;
            }
        }

        public T get(int index)
        {
            if (getElement(index)==null)
                return null;
            else
                try {
                    return getElement(index).data;
                }
                catch(NullPointerException e)
                {
                    return null;
                }
        }

        @Override
        public int hashCode()
        {
            if (data==null)
                return 0;
            return data.hashCode();
        }

        @Override
        public void insert(T element)
        {
            add(element);
        }

        @Override
        public T extractMin()
        {
            return poll();
        }

        @Override
        public T extractMax()
        {
            T k = get(size-1);
            remove(size-1);
            return k;
        }
    }

    public static class MinHeapTree<T> implements SimpleQueue<T>//куча с минимальным элементом в голове на основе очереди
    {
        private T data;
        private final Comparator<T> comparator;
        private int size=0;
        private MinHeapTree<T> left;
        private MinHeapTree<T> right;
        private MinHeapTree<T> parent;

        public MinHeapTree(final Comparator<T> comparator)
        {
            this.comparator= new Comparator<T>() {
                @Override
                public int compare(T o1, T o2) {//����� �������, ��� ���� ���� �� ��������� �� ����������, �� �������� �������
                    if (o1==null && o2==null)
                        return 0;
                    else if (o1==null)
                        return 1;
                    else if (o2 == null)
                        return -1;
                    else
                        return comparator.compare(o1,o2);
                }
            };
        }

        @Override
        public int size()
        {
            return size;
        }

        @Override
        public void insert(T element)
        {
            size++;
            if (size==1)
            {
                data=element;
                return;
            }
            MinHeapTree<T> newElement = new MinHeapTree<>(comparator);
            newElement.data=element;
            newElement.left=null;
            newElement.right=null;

            // ��� �������� ����������������� ������, ����������� ����������� �������������� ��� ����������� ��������
            MinHeapTree<T> bufferElement = this;//������� ��� �����������
            int height= (int)(Math.log(size)/Math.log(2));//������ ���������,������� �� ������ ��������� ������
            int placeNumber=size % (int)Math.pow(2,height);//��������� ����������� ��������
            for (int i=1; i< (int)(Math.log(size)/Math.log(2)); i++)//������� �� ������������� ��������
            {
                if (placeNumber>=(int)Math.pow(2,height-1))
                    bufferElement=bufferElement.right;
                else
                    bufferElement=bufferElement.left;
                height--;
                placeNumber=placeNumber % (int)Math.pow(2, height);
            }
            if (placeNumber==1) {
                bufferElement.right=newElement;
                newElement.parent=bufferElement;
            }
            else
            {
                bufferElement.left=newElement;
                newElement.parent=bufferElement;
            }
            sift(newElement);//����������� ������������ ��������
        }

        private MinHeapTree<T> getLastElement()
        {
            MinHeapTree<T> bufferElement = this;//������� ��� �����������
            int height= (int)(Math.log(size)/Math.log(2));//������ ���������,������� �� ������ ��������� ������
            int placeNumber=size % (int)Math.pow(2,height);//��������� ����������� ��������
            for (int i=0; i< (int)(Math.log(size)/Math.log(2)); i++)//������� �� ������������� ��������
            {
                if (placeNumber>=(int)Math.pow(2,height-1))
                    bufferElement=bufferElement.right;
                else
                    bufferElement=bufferElement.left;
                height--;
                placeNumber=placeNumber % (int)Math.pow(2, height);
            }
            return bufferElement;
        }

        @Override
        public T extractMin()
        {
            T res=data;
            data = null;
            if (left==null) {
                size=0;
                return res;
            }
            MinHeapTree<T> removedElement =sift(this);
            swapData(removedElement,getLastElement());
            sift(removedElement);//���������� �������, ������� �� �������� ������� � ���������
            MinHeapTree<T> removeElementParent = getLastElement().parent;//������ ��������� ������� �� ������ �� ��������� ������
            if (removeElementParent.left.data==null)
                removeElementParent.left=null;
            else
                removeElementParent.right=null;
            size--;
            return res;
        }

        @Override
        public T extractMax()
        {
            return extractMin();
        }
        private void swapData(MinHeapTree<T> first, MinHeapTree<T> second) //����� ������ ����� ����� ���������
        {
            T bufData=first.data;
            first.data=second.data;
            second.data=bufData;
        }

        private MinHeapTree<T> sift(MinHeapTree<T> element) //"�����������" ��������
        {
            return siftDown(siftUp(element));
        }

        private MinHeapTree<T> siftUp(MinHeapTree<T> element) // "�����������" �����
        {
            MinHeapTree<T> bufElement = element;
            while(bufElement.parent!=null && comparator.compare(bufElement.parent.data, bufElement.data)>0) {
                swapData(bufElement, bufElement.parent);
                bufElement=bufElement.parent;
            }
            return bufElement;
        }

        private MinHeapTree<T> siftDown(MinHeapTree<T> element) // "�����������" ����
        {
            MinHeapTree<T> bufElement = element;
            while(bufElement.left!=null && bufElement.right!=null && (comparator.compare(bufElement.left.data,bufElement.data)<0 || comparator.compare(bufElement.right.data,bufElement.data)<0))
            {
                if (comparator.compare(bufElement.left.data, bufElement.right.data)>=0)//������ � ������ ������
                {
                    swapData(bufElement, bufElement.right);
                    bufElement=bufElement.right;
                }
                else //����� ������ � �����
                {
                    swapData(bufElement, bufElement.left);
                    bufElement=bufElement.left;
                }
            }
            if (bufElement.left!=null && bufElement.right==null && comparator.compare(bufElement.left.data, bufElement.data)<0)
            {
                swapData(bufElement, bufElement.left);
                bufElement=bufElement.left;
            }
            return bufElement;
        }
    }

    public static class MaxHeapTree<T> implements SimpleQueue<T>//куча с максимальным элементом в голове на основе очереди
    {
        MinHeapTree<T> heap;

        MaxHeapTree(Comparator<T> comparator)
        {
            heap=new MinHeapTree<>((x, y)-> -comparator.compare(x,y));
        }

        @Override
        public void insert(T element)
        {
            heap.insert(element);
        }

        @Override
        public int size()
        {
            return heap.size();
        }

        @Override
        public T extractMin()
        {
            return heap.extractMax();
        }

        @Override
        public T extractMax()
        {
            return heap.extractMin();
        }

    }


}
