package chapter01.item01;

class Item {
    private int id;
    private String name;

    public Item(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static Item createItemWithoutId(String name) {
        return new Item(0, name);
    }
}

public class Exam {
    public static void main(String[] args) {
        Item testItem = Item.createItemWithoutId("테스트");
    }
}
