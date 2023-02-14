package com.opt.lib_image.visitor

import org.jetbrains.org.objectweb.asm.MethodVisitor
import org.jetbrains.org.objectweb.asm.Opcodes
import org.jetbrains.org.objectweb.asm.commons.AdviceAdapter

class SampleMethodVisitor constructor(
    api: Int = Opcodes.ASM5, var methodName: String?, methodVisitor: MethodVisitor?, access: Int,
    descriptor: String?
) : AdviceAdapter(
    api,
    methodVisitor, access, methodName, descriptor
) {

    override fun visitMethodInsn(
        opcode: Int,
        owner: String?,
        name: String?,
        descriptor: String?,
        isInterface: Boolean
    ) {
        if (methodName == "onCreate") {
            println("owner -> $owner, name -> $name, descriptor -> $descriptor")
        }
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)
    }

    override fun visitInsn(opcode: Int) {
        super.visitInsn(opcode)

    }

    override fun visitEnd() {
        super.visitEnd()

        if (methodName == "onCreate") {
            println("visitEnd methodName -> $methodName")
            // 参数怎么写
            mv.visitLdcInsn("TAG")
            mv.visitLdcInsn("enterMethod")
            // 这里一定是要字节码的方法
            mv.visitMethodInsn(
                INVOKESTATIC, "android/util/Log", "e",
                "(Ljava/lang/String;Ljava/lang/String;)I", false
            )
        }
    }

}