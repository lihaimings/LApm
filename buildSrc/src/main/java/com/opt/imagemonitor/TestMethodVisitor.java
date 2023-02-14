package com.opt.imagemonitor;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;

public class TestMethodVisitor extends AdviceAdapter {

    private String className;
    private String superName;

    protected TestMethodVisitor(int api, MethodVisitor mv, int access, String name, String desc, String className, String superName) {
        super(api, mv, access, name, desc);
        this.className = className;
        this.superName = superName;
    }


    @Override
    protected void onMethodEnter() {
        super.onMethodEnter();
        System.out.println("开始方法");
        mv.visitLdcInsn("TAG");
        mv.visitLdcInsn(className + "---->" + superName);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "android/util/Log", "i", "(Ljava/lang/String;Ljava/lang/String;)I", false);
        mv.visitInsn(Opcodes.POP);
    }

    @Override
    protected void onMethodExit(int opcode) {
        mv.visitLdcInsn("TAG");
        mv.visitLdcInsn("this is end");
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "android/util/Log", "i", "(Ljava/lang/String;Ljava/lang/String;)I", false);
        mv.visitInsn(Opcodes.POP);
        super.onMethodExit(opcode);
    }

}
