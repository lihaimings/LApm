package com.opt.lapm.classloader

class CusterClassLoader : ClassLoader() {

    override fun findClass(name: String?): Class<*> {
        return super.findClass(name)
    }


}