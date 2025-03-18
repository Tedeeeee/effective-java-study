package chapter02.item13;

public class HashTableExam implements Cloneable {
    private Entry[] buckets = new Entry[100];

    private static class Entry {
        final Object key;
        Object value;
        Entry next;

        public Entry(Object key, Object value, Entry next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }

//        Entry deepCopy() {
//            return new Entry(key, value,
//                    next == null ? null : next.deepCopy());
//        }

        Entry deepCopy() {
            Entry result = new Entry(key, value, next);
            for (Entry p = result; p.next != null ; p = p.next) {
                p.next = new Entry(p.next.key, p.next.value, p.next.next);
            }
            return result;
        }
    }

    @Override
    public HashTableExam clone() throws CloneNotSupportedException {
        try {
            HashTableExam result = (HashTableExam) super.clone();
            result.buckets = new Entry[buckets.length];
            for (int i = 0; i < buckets.length; i++) {
                if (buckets[i] != null) {
                    result.buckets[i] = buckets[i].deepCopy();
                }
            }
            return result;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
