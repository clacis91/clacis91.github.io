import framework.*;

public class Underliner implements Product {
    private char liner;
    public Underliner(char liner) {
        this.liner = liner;
    }

    @Override
    public void use(String str) {
        int strLen = str.getBytes().length;
        StringBuilder sb = new StringBuilder();
        sb.append(str + "\n");
        for(int i = 0; i < strLen; i++) 
            sb.append(liner);
        
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