import framework.*;

public class Boxer implements Product {
    private char decochar;
    public Boxer(char decochar) {
        this.decochar = decochar;
    }

    @Override
    public void use(String str) {
        int strLen = str.getBytes().length;
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < strLen + 2; i++) 
            sb.append(decochar);

        sb.append("\n" + decochar + str + decochar + "\n");

        for(int i = 0; i < strLen + 2; i++) 
            sb.append(decochar);
        
        System.out.println(sb.toString());
    }

    @Override
    public Product createClone() {
        Product p = null;
        try {
            p = (Product) clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return p;
    }
}