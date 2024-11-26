class Test{
    final int b;
    final int a(int a){
        return a;
    }
    public static void main(String[] args) {
        final Test t=new Test();
        System.out.println(t.a(7));
    }
}
