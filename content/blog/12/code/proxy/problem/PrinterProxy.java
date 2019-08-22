public class PrinterProxy implements Printable {
    private String name;
    private Printable real;
    private String realClass;

    public PrinterProxy() {}
    public PrinterProxy(String name, String realClass) {
        this.name = name;
        this.realClass = realClass;
    }

    public synchronized void setPrinterName(String name) {
        if(real != null) real.setPrinterName(name);
        this.name = name;
    }
    public String getPrinterName() {
        return name;
    }
    public void print(String string) {
        realize();
        real.print(string);
    }
    private synchronized void realize() {
        if(real == null) {
            try {
                real = (Printable) Class.forName(realClass).newInstance();
                real.setPrinterName(name);
            } catch(ClassNotFoundException e) {
                e.printStackTrace();
            } catch(InstantiationException e) {
                e.printStackTrace();
            } catch(IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}