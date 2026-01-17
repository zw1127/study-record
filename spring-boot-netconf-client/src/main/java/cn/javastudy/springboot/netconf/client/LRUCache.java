package cn.javastudy.springboot.netconf.client;

import java.util.HashMap;
import java.util.Map;

/**
 * 实现一个LRU缓存，要求O(1)时间复杂度的get和put操作，并解释你的实现思路
 */
public class LRUCache {
    class Node {
        int key;
        int value;
        Node next;
        Node prev;

        public Node(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }

    private final int capacity;
    private final Node head;
    private final Node tail;
    private final Map<Integer, Node> cache;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        cache = new HashMap<Integer, Node>();

        //虚拟的头尾节点
        head = new Node(0, 0);
        tail = new Node(0, 0);
        head.next = tail;
        tail.prev = head;
    }

    public int get(int key) {
        if (!cache.containsKey(key)) {
            return -1;
        }

        Node node = cache.get(key);
        moveToHead(node);
        return node.value;
    }

    public void put(int key, int value) {
        if (cache.containsKey(key)) {
            Node node = cache.get(key);
            node.value = value;
            moveToHead(node);
        } else {
            Node node = new Node(key, value);
            cache.put(key, node);
            addToHead(node);

            if (cache.size() > capacity) {
                Node removedTail = removeTail();
                cache.remove(removedTail.key);
            }
        }
    }

    // 将节点移动到头部（最近使用）
    private void moveToHead(Node node) {
        removeNode(node);
        addToHead(node);
    }

    // 在头部插入新节点
    private void addToHead(Node node) {
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
    }

    // 删除节点
    private void removeNode(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    // 删除链表尾部节点（最久未使用）
    private Node removeTail() {
        Node lastNode = tail.prev;
        removeNode(lastNode);
        return lastNode;
    }
}
