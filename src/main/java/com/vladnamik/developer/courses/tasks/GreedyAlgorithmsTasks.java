package com.vladnamik.developer.courses.tasks;


import com.vladnamik.developer.datastructures.ListPriorityQueue;
import com.vladnamik.developer.datastructures.SimpleQueue;
import com.vladnamik.developer.sorting.MergeSort;

import java.util.*;

@SuppressWarnings("unused")
public class GreedyAlgorithmsTasks {

    public static List<Integer> coveredLinesDots(SimpleLine[] lines)//Возвращает список концов отрезков на прямой, которые присутствуют в оптимальном (макс. кол-во) размещении линий на прямой
    {
        SimpleLine[] newlines = Arrays.copyOf(lines, lines.length);
        new MergeSort().sort(newlines);
        List<Integer> list = new LinkedList<>();
        for (int i = 0; i < newlines.length; ) {
            list.add(newlines[i].end);
            if (++i >= newlines.length)
                break;
            while (newlines[i].begin <= list.get(list.size() - 1))
                if (++i >= newlines.length)
                    break;
        }
        return list;
    }

    public static double continuousBackpackTask(SimpleObject[] objects, int w)//Задача про бесконечный рюкзак
    {
        SimpleObject[] newObjects = Arrays.copyOf(objects, objects.length);
        new MergeSort().sort(newObjects);
        double maxPrice = 0.0;
        for (int i = newObjects.length - 1; i >= 0; i--) {
            if (w >= newObjects[i].w) {
                maxPrice += (double) newObjects[i].c;
                w -= newObjects[i].w;
            } else {
                if (w != 0)
                    maxPrice += ((double) newObjects[i].c / (double) newObjects[i].w) * (double) w;
                break;
            }
        }
        return maxPrice;
    }

    public static CharFreq getHuffmanTree(String stringToCode)//Создаёт дерево по алгоритму Хаффмана, на вход - строка для кодирования, на выходе - голова дерева
    {
        SimpleQueue<CharFreq> queue = new ListPriorityQueue<>(CharFreq::comparing);

        for (Character k = 'a'; k <= 'z'; k++) {
            int f;
            if ((f = CharFreq.getCharFreqFromString(k, stringToCode)) > 0)
                queue.insert(new CharFreq(k, f));
        }

        while (queue.size() > 1)
            queue.insert(new CharFreq(queue.extractMin(), queue.extractMin()));

        return queue.extractMin();
    }

    public static Map<Character, String> fromHuffmanTreeToMap(CharFreq tree) //из дерева в таблицу (алгоритм Хаффмана)
    {
        Map<Character, String> map = new HashMap<>();
        LinkedList<CharFreq> list = new LinkedList<>();
        LinkedList<String> stringList = new LinkedList<>();
        if (tree == null)
            return map;
        if (tree.left == null) {
            map.put(tree.symbol, "0");
            return map;
        }
        list.add(tree);
        stringList.add("");
        while (list.size() > 0) {
            CharFreq element = list.removeLast();
            String code = stringList.removeLast();
            if (element.left == null) {
                map.put(element.getSymbol(), code);
            } else {
                list.add(element.left);
                stringList.add(code.concat("1"));
                list.add(element.right);
                stringList.add(code.concat("0"));
            }
        }
        return map;
    }

    public static CharFreq fromMapToHuffmanTree(Map<Character, String> map) // из таблицы в дерево (алгоритм Хаффмана)
    {
        if (map == null || map.isEmpty())
            return null;
        CharFreq tree = null;
        if (map.size() == 1) {
            for (Character c : map.keySet())
                tree = new CharFreq(c, 1);
            return tree;
        }
        tree = new CharFreq(null, null);
        for (Character c : map.keySet()) {
            CharFreq el = tree;
            for (char cc : map.get(c).toCharArray())
                if (cc == '0') {
                    if (el.right == null)
                        el.right = new CharFreq(null, null);
                    el = el.right;
                    el.symbol = c;
                } else if (cc == '1') {
                    if (el.left == null)
                        el.left = new CharFreq(null, null);
                    el = el.left;
                    el.symbol = c;
                }
        }
        return tree;
    }

    public static String stringCoding(Map<Character, String> table, String data) // Кодирование строки по таблице
    {
        StringBuilder outputString = new StringBuilder("");
        for (Character c : data.toCharArray())
            if (table.containsKey(c))
                outputString.append(table.get(c));
        return outputString.toString();
    }

    public static String stringDecoding(CharFreq tree, String encodedData) // декодирование строки с помощью дерева
    {
        if (tree == null)
            return "";
        char[] inString = encodedData.toCharArray();
        StringBuilder outString = new StringBuilder("");
        if (tree.left == null) {
            for (char c : inString)
                outString.append(tree.symbol);
            return outString.toString();
        }
        CharFreq treeElement = tree;
        for (char c : inString) {
            if (c == '0')
                treeElement = treeElement.right;
            else
                treeElement = treeElement.left;
            if (treeElement.left == null) {
                outString.append(treeElement.symbol);
                treeElement = tree;
            }
        }
        return outString.toString();
    }

    public static class SimpleLine implements Comparable<SimpleLine>//Линия на прямой
    {
        public Integer begin;
        public Integer end;

        public SimpleLine() {
        }

        public SimpleLine(Integer begin, Integer end) {
            this.begin = begin;
            this.end = end;
        }

        @Override
        public int compareTo(SimpleLine o)// сравнение по концу отрезка
        {
            return end.compareTo(o.end);
        }
    }

    public static class SimpleObject implements Comparable<SimpleObject>//простой предмет (для задачи про бесконечный рюкзак)
    {
        int w;//����� ��������
        int c;//��������� ��������

        @Override
        public int compareTo(SimpleObject o) {
            return new Double((double) c / (double) w).compareTo((double) o.c / (double) o.w);
        }
    }

    public static class CharFreq // Символ-частота(алгоритм Хаффмана)
    {
        private Character symbol = null;
        private int frequency;
        private CharFreq left = null;
        private CharFreq right = null;

        public CharFreq(Character symbol, int frequency) {
            this.symbol = symbol;
            this.frequency = frequency;
        }

        public CharFreq(CharFreq left, CharFreq right) {
            this.left = left;
            this.right = right;
            if (left != null && right != null)
                frequency = left.frequency + right.frequency;
        }

        public static int comparing(CharFreq x, CharFreq y) {
            return x.frequency == y.frequency ? 0 : (x.frequency > y.frequency ? 1 : -1);
        }

        public static int getCharFreqFromString(char c, String s) {
            char[] str = s.toCharArray();
            int freq = 0;
            for (char symbol : str)
                if (symbol == c)
                    freq++;
            return freq;
        }

        public Character getSymbol() {
            return symbol;
        }

        public int getFrequency() {
            return frequency;
        }

        public CharFreq getLeft() {
            return left;
        }

        public CharFreq getRight() {
            return right;
        }
    }
}
