package ru.ifmo.rain.vanyan.arrayset;

import java.util.*;

public class ArraySet<E> extends AbstractSet<E> implements NavigableSet<E>  {

    private final List<E> elements;
    private final Comparator<? super E> comparator;

    public ArraySet() {
        elements = Collections.emptyList();
        comparator = null;
    }

    public ArraySet(Collection<? extends E> collection) {
        this(collection, null);
    }



    public ArraySet(Collection<? extends E> collection, Comparator<? super E> comparator) {
        Set<E> treeSet = new TreeSet<>(comparator);
        treeSet.addAll(collection);
        elements = new ArrayList<>(treeSet);
        this.comparator = comparator;
    }

    private ArraySet(ListView listView, Comparator<? super E> comparator) {
        this.comparator = comparator;
        elements = listView;
    }



    @Override
    @SuppressWarnings("unchecked")
    public boolean contains(Object o) {
        return Collections.binarySearch(elements, o, (Comparator<Object>) comparator) >= 0;
    }

    @Override
    public Iterator<E> iterator() {
        return Collections.unmodifiableList(elements).iterator();
    }

    @Override
    public Iterator<E> descendingIterator() {
        return descendingSet().iterator();
    }

    @Override
    public int size() {
        return elements.size();
    }

    @Override
    public E first() {
        checkNotEmpty();
        return elements.get(0);
    }

    @Override
    public E last() {
        checkNotEmpty();
        return elements.get(size() - 1);
    }

    private void checkNotEmpty() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
    }

    @Override
    public E lower(E e) {
        return validateValue(lowerIndex(e));
    }

    @Override
    public E floor(E e) {
        return validateValue(floorIndex(e));
    }

    @Override
    public E ceiling(E e) {
        return validateValue(ceilingIndex(e));
    }

    @Override
    public E higher(E e) {
        return validateValue(higherIndex(e));
    }

    private E validateValue(int index) {
        return (index >= 0 && index < size()) ? elements.get(index) : null;
    }

    private int ceilingIndex(E e) {
        int index = indexSearch(e);
        return index >= 0 ? index : (-index - 1);
    }

    private int floorIndex(E e) {
        int index = indexSearch(e);
        return index >= 0 ? index : (-index - 1) - 1;
    }

    private int higherIndex(E e) {
        int index = indexSearch(e);
        return index >= 0 ? index + 1 : (-index - 1);
    }

    private int lowerIndex(E e) {
        int index = indexSearch(e);
        return index >= 0 ? index - 1 : (-index - 1) - 1;
    }



    private int indexSearch(E e) {
        return Collections.binarySearch(elements, e, comparator);
    }


    @Override
    public E pollFirst() {
        throw new UnsupportedOperationException();
    }

    @Override
    public E pollLast() {
        throw new UnsupportedOperationException();
    }

    @Override
    public NavigableSet<E> descendingSet() {
        return new ArraySet<E>(new ListView(0, size(), false), Collections.reverseOrder(comparator));
    }

    private int compare(E a, E b) {
        if (comparator == null) {
            return ((Comparable)a).compareTo(b);
        } else {
            return comparator.compare(a, b);
        }
    }
    @Override
    public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
        if (compare(fromElement, toElement) > 0) {
            throw new IllegalArgumentException();
        }
        int fromIndex = fromInclusive ? ceilingIndex(fromElement) : higherIndex(fromElement);
        int toIndex = toInclusive ? floorIndex(toElement) : lowerIndex(toElement);
        if (fromIndex == -1 || fromIndex > toIndex ) {
            return new ArraySet<>(Collections.emptyList(), comparator);
        }
        return new ArraySet<E>(new ListView(fromIndex, toIndex + 1, true), comparator);
    }

    @Override
    public NavigableSet<E> headSet(E toElement, boolean inclusive) {
        if (isEmpty()) {
            return new ArraySet<>(Collections.emptyList(), comparator);
        }
        try {
            return subSet(first(), true, toElement, inclusive);
        } catch (IllegalArgumentException  e) {
            return new ArraySet<>(Collections.emptyList(), comparator);
        }
    }

    @Override
    public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
        if (isEmpty()) {
            return new ArraySet<>(Collections.emptyList(), comparator);
        }
        try {
            return subSet(fromElement, inclusive, last(), true);
        } catch (IllegalArgumentException  e) {
            return new ArraySet<>(Collections.emptyList(), comparator);
        }
    }

    @Override
    public SortedSet<E> subSet(E fromElement, E toElement) {
        return subSet(fromElement, true, toElement, false);
    }

    @Override
    public SortedSet<E> headSet(E toElement) {
        return headSet(toElement, false);
    }

    @Override
    public SortedSet<E> tailSet(E fromElement) {
        return tailSet(fromElement, true);
    }

    @Override
    public Comparator<? super E> comparator() {
        return comparator;
    }

    private class ListView extends AbstractList<E> implements List<E>, RandomAccess {
        private final int fromIndex, toIndex; // [fr,to)
        private final boolean ascending;

        private ListView(int fromIndex, int toIndex, boolean ascending) {
            this.fromIndex = fromIndex;
            this.toIndex = toIndex;
            this.ascending = ascending;
        }

        @Override
        public E get(int index) {
            if (ascending) {
                if (fromIndex + index < toIndex) {
                    return elements.get(fromIndex + index);
                }
                else {
                    throw new IndexOutOfBoundsException();
                }
            } else {
                if (toIndex - index - 1 >= fromIndex) {
                    return elements.get(toIndex - index - 1);
                } else {
                    throw new IndexOutOfBoundsException();
                }
            }
        }

        @Override
        public int size() {
            return toIndex - fromIndex;
        }
    }
}