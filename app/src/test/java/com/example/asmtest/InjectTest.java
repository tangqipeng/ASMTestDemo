package com.example.asmtest;

/**
 * @author tangqipeng
 * @date 2020/8/14 7:01 PM
 * @email tangqipeng@aograph.com
 */
public class InjectTest {

    @ASMTest
    public static void main() {
        try {
            long startTime = System.currentTimeMillis();
            Thread.sleep(2000);
            long endTime = System.currentTimeMillis();
            System.out.println("execute time is " + (endTime - startTime)+"ms");
            System.out.println("Hello world");
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
