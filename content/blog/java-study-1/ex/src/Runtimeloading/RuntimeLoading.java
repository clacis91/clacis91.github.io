package Runtimeloading;

public class RuntimeLoading {
    public static void main(String[] args) {
        try {
            Class<?> cls = Class.forName(args[0]);
            Object obj = cls.newInstance();
            PrintInterface print = (PrintInterface) obj;
        }
        catch(ClassNotFoundException e) {
            e.printStackTrace();
        }
        catch(InstantiationException e) {
            e.printStackTrace();
        }
        catch(IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}