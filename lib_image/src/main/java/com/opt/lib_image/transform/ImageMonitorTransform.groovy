package com.opt.lib_image.transform

import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.Format
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformInvocation
import com.android.build.api.transform.TransformOutputProvider
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import com.opt.lib_image.visitor.MonitorImageClassVisitor
import groovyjarjarasm.asm.ClassReader
import groovyjarjarasm.asm.ClassWriter
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.IOUtils
import org.jetbrains.org.objectweb.asm.ClassVisitor

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

class ImageMonitorTransform extends Transform {

    @Override
    String getName() {
        return ImageMonitorTransform.name
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)
        // 需要获取上一个 transform 的输出
        Collection<TransformInput> inputs = transformInvocation.inputs
        TransformOutputProvider outputProvider = transformInvocation.outputProvider
        if (outputProvider != null)
            outputProvider.deleteAll()
        //  class ，自己写的代码，第三方库 aar
        inputs.each { TransformInput transformInput ->
            // 处理自己写的代码
            transformInput.directoryInputs.each { DirectoryInput directoryInput ->
                handleDirectoryInput(directoryInput, outputProvider)
            }
            // 处理第三方引入的 class
            transformInput.jarInputs.each { JarInput jarInput ->
                handleJarInput(jarInput, outputProvider)
            }
        }
    }

    /**
     * 处理文件目录下的class文件
     */
    void handleDirectoryInput(DirectoryInput directoryInput, TransformOutputProvider outputProvider) {
        if (directoryInput.file.isDirectory()) {
            directoryInput.file.eachFileRecurse { file ->
                String name = file.name
                //插桩class
                if (filterClass(name)) {
                    // 用来读 class 信息
                    ClassReader classReader = new ClassReader(file.bytes)
                    // 用来写
                    ClassWriter classWriter = new ClassWriter(0)
                    // 有需求，或者修改，基本只要改这里就可以了，asm 的常用操作
                    // 有需求，或者修改，基本只要改这里就可以了，asm 的常用操作
                    ClassVisitor classVisitor = new MonitorImageClassVisitor(classWriter)
                    // 下面还可以包多层
                    classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES)
                    // 重新覆盖写入文件
                    byte[] code = classWriter.toByteArray()
                    FileOutputStream fos = new FileOutputStream(file.parentFile.absolutePath + File.separator + name)
                    fos.write(code)
                    fos.close()
                }
            }
        }
        def dest = outputProvider.getContentLocation(directoryInput.name,
                directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)
        FileUtils.copyDirectory(directoryInput.file, dest)
    }

    /**
     * 处理第三方引入的 class jar
     */
    void handleJarInput(JarInput jarInput, TransformOutputProvider outputProvider) {
        if (jarInput.file.absolutePath.endsWith(".jar")) {
            def jarName = jarInput.name
            def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
            if (jarName.endsWith(".jar")){
                jarName = jarName.substring(0, jarName.length() - 4)
            }
            JarFile jarFile = new JarFile(jarInput.file)
            Enumeration enumeration = jarFile.entries()
            File tmpFile =  new File(jarInput.file.getParent() + File.separator + "classes_temp.jar")
            //避免上次的缓存被重复插入
            if (tmpFile.exists()){
                tmpFile.delete()
            }
            JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(tmpFile))
            //用于保存
            while (enumeration.hasMoreElements()) {
                JarEntry jarEntry = (JarEntry) enumeration.nextElement()
                String entryName = jarEntry.getName()
                ZipEntry zipEntry = new ZipEntry(entryName)
                InputStream inputStream = jarFile.getInputStream(jarEntry)
                //插桩class
                if (filterClass(entryName)) {
                    //class文件处理
                    jarOutputStream.putNextEntry(zipEntry)
                    ClassReader classReader = new ClassReader(IOUtils.toByteArray(inputStream))
                    ClassWriter classWriter = new ClassWriter(0)
                    // 有需求，或者修改，基本只要改这里就可以了，asm 的常用操作
                    ClassVisitor classVisitor = new MonitorImageClassVisitor(classWriter)
                    // 下面还可以包多层
                    classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES)
                    byte[] code = classWriter.toByteArray()
                    jarOutputStream.write(code)
                } else {
                    jarOutputStream.putNextEntry(zipEntry)
                    jarOutputStream.write(IOUtils.toByteArray(inputStream))
                }
                jarOutputStream.closeEntry()
            }
            //结束
            jarOutputStream.close()
            jarFile.close()
            def dest = outputProvider.getContentLocation(jarName + md5Name,
                    jarInput.contentTypes, jarInput.scopes, Format.JAR)
            org.apache.commons.io.FileUtils.copyFile(tmpFile, dest)
            tmpFile.delete()

        }
    }

    boolean filterClass(String className) {
        return (className.endsWith(".class") && !className.startsWith("R\$")
                && "R.class" != className && "BuildConfig.class" != className)
    }

}