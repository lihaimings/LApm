package com.opt.imagemonitor;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ImageViewMethodVisitor extends MethodVisitor {

    private int imageViewId;

    public ImageViewMethodVisitor(MethodVisitor methodVisitor) {
        super(Opcodes.ASM5, methodVisitor);
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
        if (opcode == Opcodes.GETFIELD && "id".equals(name) && "Landroid/R$id;".equals(desc)) {
            // 找到了 ImageView 的 id 字段，记录其值
            imageViewId = 0;
        }
        super.visitFieldInsn(opcode, owner, name, desc);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        if (opcode == Opcodes.INVOKEVIRTUAL && "android/graphics/drawable/Drawable".equals(owner) && "getIntrinsicWidth".equals(name) && "()I".equals(desc)) {
            // 找到了 getIntrinsicWidth 方法，将 ImageView 的宽度压入操作数栈
            super.visitVarInsn(Opcodes.ALOAD, 0);
            super.visitFieldInsn(Opcodes.GETFIELD, "android/widget/ImageView", "mDrawable", "Landroid/graphics/drawable/Drawable;");
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "android/graphics/drawable/Drawable", "getIntrinsicWidth", "()I", false);
        } else if (opcode == Opcodes.INVOKEVIRTUAL && "android/graphics/drawable/Drawable".equals(owner) && "getIntrinsicHeight".equals(name) && "()I".equals(desc)) {
            // 找到了 getIntrinsicHeight 方法，将 ImageView 的高度压入操作数栈
            super.visitVarInsn(Opcodes.ALOAD, 0);
            super.visitFieldInsn(Opcodes.GETFIELD, "android/widget/ImageView", "mDrawable", "Landroid/graphics/drawable/Drawable;");
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "android/graphics/drawable/Drawable", "getIntrinsicHeight", "()I", false);

            // 将 ImageView 的宽度和高度分别压入操作数栈
            super.visitInsn(Opcodes.DUP2);
            super.visitVarInsn(Opcodes.ALOAD, 0);
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "android/widget/ImageView", "getWidth", "()I", false);
            super.visitVarInsn(Opcodes.ALOAD, 0);
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "android/widget/ImageView", "getHeight", "()I", false);

            // 将 ImageView 的 id 压入操作数栈
            super.visitLdcInsn(imageViewId);

            // 创建 Label，用于标记是否需要输出日志
            Label label = new Label();

            // 将 ImageView 的宽度和高度分别与 ImageView 的宽度和高度比较，如果有一个大于 ImageView 的宽度和高度，则跳转到 label 标记的位置
            super.visitJumpInsn(Opcodes.IF_ICMPLE, label);

            // 输出日志，包括 ImageView 的 id 和图片的链接
            super.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            super.visitTypeInsn(Opcodes.NEW, "java/lang/StringBuilder");
            super.visitInsn(Opcodes.DUP);
            super.visitLdcInsn("Image size exceeds ImageView size, id = ");
            super.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V", false);
            super.visitLdcInsn(imageViewId);
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;", false);
            super.visitLdcInsn(", url = ");
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            super.visitVarInsn(Opcodes.ALOAD, 1);
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "android/widget/ImageView", "getTag", "()Ljava/lang/Object;", false);
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/Object;)Ljava/lang/StringBuilder;", false);
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);

            // 将 Label 标记的位置
            super.visitLabel(label);
        }
        super.visitMethodInsn(opcode, owner, name, desc, itf);
    }

    @Override
    public void visitVarInsn(int opcode, int var) {
        if (opcode == Opcodes.ASTORE && var == 2) {
            // 找到了 ImageView 的 id 的赋值操作，将其记录的值设置为对应的值
            imageViewId = 0;
        }
        super.visitVarInsn(opcode, var);
    }

}
