package android.ktodo.com.javalib;

public class FirstClass {
    /*
    * 符号
描述
运算规则




&
与
两个位都为 1 时，结果才为 1


I
或
两个位都是 0 时，结果才为 0


^
异或
两个位相同时为 0，相异为 1


~
取反
0 变 1，1 变 0


<<
左移
各二进位全部左移若干位，高位丢弃，低位补 0


>>
右移
各二进位全部右移若干位，对无符号数，高位补 0，有符号数，各编译器处理方法不一样，有的补符号位(算术右移)，有的补 0 (逻辑右移)

    *
    * */


    public static void main(String[] args) {
        String str = "";
        int a = 5 >>> 2;
        int b = 5 & 3;
        int c = 5 | 3;
        int d = 5 ^ 3;
        int e = ~3;
        str = e + "";
        print(str);
    }

    private static void print(String str) {
        System.out.println(str);
    }
}
