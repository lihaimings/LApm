package com.opt.imageplugin

import org.objectweb.asm.ClassVisitor

class ClassInjectTimeVisitor extends ClassVisitor{

    ClassInjectTimeVisitor(int api) {
        super(api)
    }

    ClassInjectTimeVisitor(int api, ClassVisitor classVisitor) {
        super(api, classVisitor)
    }


}