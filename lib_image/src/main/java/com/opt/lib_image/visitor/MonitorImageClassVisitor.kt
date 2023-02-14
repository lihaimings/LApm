package com.opt.lib_image.visitor

import com.android.ddmlib.Log
import org.jetbrains.org.objectweb.asm.*

class MonitorImageClassVisitor constructor(api: Int = Opcodes.ASM5, classVisitor: ClassVisitor?) :
    ClassVisitor(api, classVisitor) {

    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        println("\"version = ${version}, access = ${access}, name = ${name}, signature = ${signature}, superName = ${superName} \\n\"")
//        if (superName?.equals("android/widget/ImageView") == true &&
//                )

        super.visit(version, access, name, signature, superName, interfaces)
    }

    // 方法
    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions)
        val method = SampleMethodVisitor(
            methodVisitor = methodVisitor,
            access = access,
            methodName = name,
            descriptor = descriptor
        )
        return method
    }

    // 动画
    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        return super.visitAnnotation(descriptor, visible)
    }

    // 属性
    override fun visitField(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        value: Any?
    ): FieldVisitor {
        return super.visitField(access, name, descriptor, signature, value)
    }

    // 最后
    override fun visitEnd() {
        super.visitEnd()
    }

}