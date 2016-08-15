package com.vladnamik.developer.datastructures;

/**
 * Очередь на двух стэках с поддержкой максимума
 * для задачи нахождения максимума в плавающем окне.
 *
 */
public class MaxInWindowQueue {
    private MaxValueStack leftStack = new MaxValueStack();
    private MaxValueStack rightStack = new MaxValueStack();

    public void push(Integer val) {
        leftStack.push(val);
    }

    public Integer pop() {
        throwOverIfNecessary();
        return rightStack.pop();
    }

    public Integer getLeftStackMax() {
        return leftStack.getMax();
    }

    public int getLeftStackSize() {
        return leftStack.size();
    }

    public int getRightStackSize() {
        return rightStack.size();
    }

    public Integer getRightStackMax() {
        throwOverIfNecessary();
        return rightStack.getMax();
    }

    private void throwOverIfNecessary() {
        if (rightStack.size() == 0) {
            int size = leftStack.size();
            for (int i = 0; i < size; i++) {
                rightStack.push(leftStack.pop());
            }
        }
    }

    public int size() {
        return leftStack.size() + rightStack.size();
    }
}
