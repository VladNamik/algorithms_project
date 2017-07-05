package com.vladnamik.developer.datastructures;

import java.util.Comparator;

public class SplayTree<T> {
    public class Node {
        private T key;
        private Node left;
        private Node right;
        private Node parent;

        public Node(T key, Node left, Node right, Node parent) {
            this.key = key;
            this.left = left;
            this.right = right;
            this.parent = parent;
        }

        public T getKey() {
            return key;
        }

        public void setKey(T key) {
            this.key = key;
        }

        public Node getLeft() {
            return left;
        }

        public void setLeft(Node left) {
            this.left = left;
        }

        public Node getRight() {
            return right;
        }

        public void setRight(Node right) {
            this.right = right;
        }

        public Node getParent() {
            return parent;
        }

        public void setParent(Node parent) {
            this.parent = parent;
        }
    }

    protected Comparator<T> comparator;
    protected Node head;

    public SplayTree(Comparator<T> comparator) {
        this.comparator = comparator;
    }

    private void splay(Node node) {
        if (node == null) {
            return;
        }
        while (node.getParent() != null) {
            // если он левый сын
            if (node.equals(node.getParent().getLeft())) {
                // если у node нет "деда" (zig)
                if (node.getParent().getParent() == null) {
                    node.getParent().setParent(node);
                    node.getParent().setLeft(node.getRight());
                    node.setRight(node.getParent());
                    node.setParent(null);
                }
                // если у node есть "дед"
                else {
                    Node rightNode = node.getRight();
                    Node leftNode = node.getLeft();
                    Node parentNode = node.getParent();
                    Node grandParentNode = node.getParent().getParent();
                    Node syblingNode = node.getParent().getRight();
                    // если отец левый сын деда (zigzig)
                    if (grandParentNode.getLeft().equals(parentNode)) {
                        node.setRight(parentNode);
                        node.setParent(grandParentNode.getParent());
                        parentNode.setLeft(rightNode);
                        parentNode.setParent(node);
                        parentNode.setRight(grandParentNode);
                        grandParentNode.setLeft(syblingNode);
                        grandParentNode.setParent(parentNode);
                    }
                    // если отец правый сын деда (zigzag)
                    else {
                        node.setParent(grandParentNode.getParent());
                        node.setLeft(grandParentNode);
                        node.setRight(parentNode);
                        grandParentNode.setParent(node);
                        grandParentNode.setRight(leftNode);
                        parentNode.setParent(node);
                        parentNode.setLeft(rightNode);
                    }
                }
            }
            //Аналогично для правого сына
            else {
                // если у node нет "деда" (zig)
                if (node.getParent().getParent() == null) {
                    node.getParent().setParent(node);
                    node.getParent().setRight(node.getLeft());
                    node.setLeft(node.getParent());
                    node.setParent(null);
                }
                // если у node есть "дед"
                else {
                    Node rightNode = node.getRight();
                    Node leftNode = node.getLeft();
                    Node parentNode = node.getParent();
                    Node grandParentNode = node.getParent().getParent();
                    Node syblingNode = node.getParent().getRight();
                    // если отец правый сын деда (zigzig)
                    if (grandParentNode.getRight().equals(parentNode)) {
                        node.setLeft(parentNode);
                        node.setParent(grandParentNode.getParent());
                        parentNode.setRight(leftNode);
                        parentNode.setParent(node);
                        parentNode.setLeft(grandParentNode);
                        grandParentNode.setRight(syblingNode);
                        grandParentNode.setParent(parentNode);
                    }
                    // если отец левый сын деда (zigzag)
                    else {
                        node.setParent(grandParentNode.getParent());
                        node.setRight(grandParentNode);
                        node.setLeft(parentNode);
                        grandParentNode.setParent(node);
                        grandParentNode.setLeft(rightNode);
                        parentNode.setParent(node);
                        parentNode.setRight(leftNode);
                    }
                }
            }
        }
        head = node;
    }

    public T search(T key) {
        if (head == null) {
            return null;
        }
        Node bufNode = head;
        while (comparator.compare(key, bufNode.getKey()) != 0) {
            if (comparator.compare(key, bufNode.getKey()) > 0) {
                if (bufNode.getRight() == null) {
                    break;
                } else {
                    bufNode = bufNode.getRight();
                }
            } else {
                if (bufNode.getLeft() == null) {
                    break;
                } else {
                    bufNode = bufNode.getLeft();
                }
            }
        }

        splay(bufNode);
        return comparator.compare(key, bufNode.getKey()) != 0 ? null : bufNode.getKey();
    }

    public void insert(T key) {
        Node newNode = new Node(key, null, null, null);
        if (head == null) {
            head = newNode;
            return;
        }
        Node bufNode = head;
        while (comparator.compare(key, bufNode.getKey()) != 0) {
            if (comparator.compare(key, bufNode.getKey()) > 0) {
                if (bufNode.getRight() == null) {
                    bufNode.setRight(newNode);
                    newNode.setParent(bufNode);
                    break;
                } else {
                    bufNode = bufNode.getRight();
                }
            } else {
                if (bufNode.getLeft() == null) {
                    bufNode.setLeft(newNode);
                    newNode.setParent(bufNode);
                } else {
                    bufNode = bufNode.getLeft();
                }
            }
        }

        splay(newNode);
        return;
    }

    private Pair<Node, Node> split(T key) {
        search(key);
        if (head == null) {
            return new Pair<>(null, null);
        } else if (head.getLeft() == null) {
            return new Pair<>(null, head);
        } else {
            Node leftNode = head.getLeft();
            leftNode.setParent(null);
            head.setLeft(null);
            return new Pair<>(leftNode, head);
        }
    }

    private void merge(Node left, Node right) {
        if (left == null) {
            head = right;
            return;
        }
        Node bufNode = left;
        while (bufNode.getRight() != null) {
            bufNode = bufNode.getRight();
        }
        head = left;
        splay(bufNode);
        bufNode.setRight(right);
    }

    public void remove(T key) {
        if (head == null) {
            return;
        }
        search(key);
        merge(head.getLeft(), head.getRight());
    }
}
