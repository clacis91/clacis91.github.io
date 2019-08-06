package idcard;

//import java.util.ArrayList;
//import java.util.List;
import java.util.HashMap;
import java.util.Map;

import framework.*;

public class IDCardFactory extends Factory {
    //private List owners = new ArrayList<>();
    private Map owners = new HashMap<String, Integer>();

    public Product createProduct(String owner) {
        return new IDCard(owner);
    }

    public void registerProduct(Product product) {
        //owners.add( ((IDCard) product).getOwner() );
        owners.put( ((IDCard) product).getOwner(), ((IDCard) product).getCardId() );
    }

    //public List getOwners() {
    public Map getOwners() {
        return owners;
    }
}