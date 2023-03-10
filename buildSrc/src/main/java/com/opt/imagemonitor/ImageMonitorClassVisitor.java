package com.opt.imagemonitor;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;


public class ImageMonitorClassVisitor extends ClassVisitor {

    private String className;

    public ImageMonitorClassVisitor(ClassVisitor classVisitor) {
        super(Opcodes.ASM5, classVisitor);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        className = name;
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions);
        // 只对setImageBitmap、setImageDrawable和setImageResource三个方法进行插桩
        if (("setImageBitmap".equals(name) && "(Landroid/graphics/Bitmap;)V".equals(desc))
                || ("setImageDrawable".equals(name) && "(Landroid/graphics/drawable/Drawable;)V".equals(desc))
                || ("setImageResource".equals(name) && "(I)V".equals(desc))) {
            return new ImageMonitorMethodVisitor(methodVisitor,className);
        }
        return methodVisitor;
    }

}
