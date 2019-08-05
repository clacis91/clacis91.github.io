public class Main {
    public static void main(String[] args) {
        BookShelf bookShelf = new BookShelf(4);
        //BookShelf bookShelf = new BookShelf(); // ArrayList 사용시
        bookShelf.appendBook(new Book("ABCD"));
        bookShelf.appendBook(new Book("Hello"));
        bookShelf.appendBook(new Book("바이블"));
        bookShelf.appendBook(new Book("불쏘시개"));

        Iterator it = bookShelf.iterator();

        while(it.hasNext()) {
            Book book = it.next();
            System.out.println(book.getName());
        }
    }
}