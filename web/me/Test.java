
class Test{
    public static void main(String[] args) {
        A.B a= (new A()).new B();
        a.b();
    }
}
class A {
    class B {
        B(){
            System.out.println("inner");
        }
        public void b(){
            System.out.println("printed");
        }
    }
}