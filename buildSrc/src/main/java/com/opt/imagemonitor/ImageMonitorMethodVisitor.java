package com.opt.imagemonitor;

import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ImageMonitorMethodVisitor  extends MethodVisitor {

    private String className;

    public ImageMonitorMethodVisitor(MethodVisitor mv, String className) {
        super(Opcodes.ASM5, mv);
        this.className = className;
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        // 判断是否为setImageBitmap或setImageDrawable方法调用
        if ((opcode == Opcodes.INVOKEVIRTUAL || opcode == Opcodes.INVOKEINTERFACE)
                && ("android/widget/ImageView".equals(owner))
                && ("setImageBitmap".equals(name) || "setImageDrawable".equals(name))) {

            // 获取ImageView对象
            mv.visitVarInsn(Opcodes.ALOAD, 0);

            // 获取ImageView的宽高
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "android/widget/ImageView", "getWidth", "()I", false);
            int ivWidth = newLocal(Type.INT_TYPE);
            mv.visitVarInsn(Opcodes.ISTORE, ivWidth);

            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "android/widget/ImageView", "getHeight", "()I", false);
            int ivHeight = newLocal(Type.INT_TYPE);
            mv.visitVarInsn(Opcodes.ISTORE, ivHeight);

            // 获取图片的宽高
            mv.visitVarInsn(Opcodes.ALOAD, 1);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "android/graphics/drawable/Drawable", "getIntrinsicWidth", "()I", false);
            int imageWidth = newLocal(Type.INT_TYPE);
            mv.visitVarInsn(Opcodes.ISTORE, imageWidth);

            mv.visitVarInsn(Opcodes.ALOAD, 1);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "android/graphics/drawable/Drawable", "getIntrinsicHeight", "()I", false);
            int imageHeight = newLocal(Type.INT_TYPE);
            mv.visitVarInsn(Opcodes.ISTORE, imageHeight);

            // 输出Log
            Label l1 = new Label();
            mv.visitIntInsn(Opcodes.ILOAD, imageWidth);
            mv.visitIntInsn(Opcodes.ILOAD, ivWidth);
            mv.visitJumpInsn(Opcodes.IF_ICMPLE, l1);

            mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitLdcInsn("ImageMonitor: Activity=" + className + ", ViewId=" + name + ", ImageView Size=[" + ivWidth + "," + ivHeight + "], Image Size=[" + imageWidth + "," + imageHeight + "]");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);

            mv.visitLabel(l1);
        }
    }

    @Override
    public int newLocal(Type type) {
        if (type == Type.getType("Landroid/widget/ImageView;")) {
            imageViewVarIndex = super.newLocal(type);
            return imageViewVarIndex;
        }
        return super.newLocal(type);
    }

    private int imageViewVarIndex = -1;

}