package com.opt.imagemonitor;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ImageViewClassVisitor extends ClassVisitor {
    public ImageViewClassVisitor(ClassVisitor classVisitor) {
        super(Opcodes.ASM6, classVisitor);
    }


    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor methodVisitor = cv.visitMethod(access, name, desc, signature, exceptions);
        if ("setImageDrawable".equals(name) && "(Landroid/graphics/drawable/Drawable;)V".equals(desc)) {
            // 找到了 setImageDrawable 方法，创建 ImageViewMethodVisitor 处理该方法
            methodVisitor = new ImageViewMethodVisitor(methodVisitor);
        }
        return methodVisitor;
    }
}
