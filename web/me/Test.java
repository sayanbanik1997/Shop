import java.text.DecimalFormat;

class Test{
    static Integer a ;
    
    public static void main(String[] args) {
        a=5;
        A aa = new A(a);
        // B b=new B(aa);

        System.err.println(a);     
    }
    public static int a(int a){
        return ++a;
    }
}
class A {
    int a;
    public A(Integer a){
        this.a=a++;
    }   
}
class B {
    B(A a){
        a.a++;
    }
}