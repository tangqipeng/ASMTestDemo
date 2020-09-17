package com.example.asmtest;

import org.junit.Test;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.commons.Method;

import java.io.FileInputStream;
import java.io.FileOutputStream;


/**
 * @author tangqipeng
 * @date 2020/8/14 6:54 PM
 * @email tangqipeng@aograph.com
 */
public class ASMUnitTest {

    @Test
    public void test() {
        try {
            FileInputStream stream = new FileInputStream("/Users/tangqipeng/codeManager/androidProjects/ASMTest/app/src/test/java/com/example/asmtest/InjectTest.class");
            if (stream != null) {
                ClassReader classReader = new ClassReader(stream);//获取分析器，读取class文件
                ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);//读了就去写
                classReader.accept(new MyClassVisitor(Opcodes.ASM7, writer), ClassReader.EXPAND_FRAMES);//插桩
                byte[] bytes = writer.toByteArray();
                FileOutputStream outputStream = new FileOutputStream("/Users/tangqipeng/codeManager/androidProjects/ASMTest/app/src/test/java/com/example/asmtest/InjectTest1.class");
                outputStream.write(bytes);

                stream.close();
                outputStream.close();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 用来访问类信息
     */
    static class MyClassVisitor extends ClassVisitor {

        public MyClassVisitor(int api, ClassVisitor classVisitor) {
            super(api, classVisitor);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);
            System.out.println(name);
//            return super.visitMethod(access, name, descriptor, signature, exceptions);
            return new MyMethedVisitor(api, methodVisitor, access, name, descriptor);
        }
    }


    static class MyMethedVisitor extends AdviceAdapter {

        int s;
        int e;
        boolean inject = false;

        protected MyMethedVisitor(int api, MethodVisitor methodVisitor, int access, String name, String descriptor) {
            super(api, methodVisitor, access, name, descriptor);
        }

        @Override
        protected void onMethodEnter() {
            super.onMethodEnter();
            if (inject) {
                invokeStatic(Type.getType("Ljava/lang/System;"), new Method("currentTimeMillis", "()J"));
                s = newLocal(Type.LONG_TYPE);
                storeLocal(s);
            }
        }

        @Override
        protected void onMethodExit(int opcode) {
            super.onMethodExit(opcode);
            if (inject) {
                invokeStatic(Type.getType("Ljava/lang/System;"), new Method("currentTimeMillis", "()J"));
                e = newLocal(Type.LONG_TYPE);
                storeLocal(e);

                getStatic(Type.getType("Ljava/lang/System;"), "out", Type.getType("Ljava/io/PrintStream;"));
                newInstance(Type.getType("Ljava/lang/StringBuilder;"));
                dup();
                invokeConstructor(Type.getType("Ljava/lang/StringBuilder;"), new Method("<init>", "()V"));
                visitLdcInsn("execute time is ");
                invokeVirtual(Type.getType("Ljava/lang/StringBuilder;"), new Method("append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;"));
                loadLocal(e);
                loadLocal(s);
                math(SUB, Type.LONG_TYPE);
                invokeVirtual(Type.getType("Ljava/lang/StringBuilder;"), new Method("append", "(J)Ljava/lang/StringBuilder;"));
                visitLdcInsn("ms");
                invokeVirtual(Type.getType("Ljava/lang/StringBuilder;"), new Method("append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;"));
                invokeVirtual(Type.getType("Ljava/lang/StringBuilder;"), new Method("toString", "()Ljava/lang/String;"));
                invokeVirtual(Type.getType("Ljava/io/PrintStream;"), new Method("println", "(Ljava/lang/String;)V;"));

                getStatic(Type.getType("Ljava/lang/System;"), "out", Type.getType("Ljava/io/PrintStream;"));
                visitLdcInsn("Hello world");
                invokeVirtual(Type.getType("Ljava/io/PrintStream;"), new Method("println", "(Ljava/lang/String;)V"));
            }

        }

        @Override
        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
            System.out.println(getName()+"-->"+descriptor);
            if (descriptor.equals("Lcom/example/asmtest/ASMTest;")){
                inject = true;
            }else {
                inject = false;
            }
            return super.visitAnnotation(descriptor, visible);
        }
    }

}
