public class Main {
    public static void main(String[] args) {
        Printable p = new PrinterProxy("Alice", "Printer");
        System.out.println("프린터 현재 이름 : " + p.getPrinterName());
        p.setPrinterName("Bob");
        System.out.println("프린터 현재 이름 : " + p.getPrinterName());
        p.print("Hello world!");
    }
}