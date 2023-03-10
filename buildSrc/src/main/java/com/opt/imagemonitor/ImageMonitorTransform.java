package com.opt.imagemonitor;

import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInvocation;

import org.gradle.internal.service.scopes.Scope;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import proguard.classfile.ClassPool;

public class ImageMonitorTransform  {
//
//    @Override
//    public String getName() {
//        return "ImageMonitorTransform";
//    }
//
//    @Override
//    public Set<QualifiedContent.ContentType> getInputTypes() {
//        return Collections.singleton(QualifiedContent.DefaultContentType.CLASSES);
//    }
//
//    @Override
//    public Set<? super QualifiedContent.Scope> getScopes() {
//        return Collections.singleton(QualifiedContent.Scope.PROJECT);
//    }
//
//    @Override
//    public boolean isIncremental() {
//        return false;
//    }
//
//    @Override
//    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
//        if (!extension.enable) {
//            return;
//        }
//
//        ASMHelper asmHelper = new ASMHelper();
//        ClassPool classPool = asmHelper.createClassPool(transformInvocation);
//        List<ClassfileBuffer> classesToProcess = asmHelper.getClassesToProcess(transformInvocation);
//
//        for (ClassfileBuffer classfileBuffer : classesToProcess) {
//            String className = classfileBuffer.getName().replace('/', '.');
//            if (className.startsWith("android.widget.ImageView")) {
//                ClassNode classNode = asmHelper.readClassNode(classfileBuffer);
//
//                // 修改setImageDrawable方法
//                MethodNode setImageDrawableMethod = ASMUtils.findMethod(classNode, "setImageDrawable", "(Landroid/graphics/drawable/Drawable;)V");
//                if (setImageDrawableMethod != null) {
//                    setImageDrawableMethod.accept(new ImageMonitorMethodVisitor());
//                }
//
//                // 重新生成字节码
//                ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
//                classNode.accept(classWriter);
//                byte[] bytes = classWriter.toByteArray();
//
//                // 保存修改后的字节码
//                File outputFile = transformInvocation.getOutputProvider().getContentLocation(
//                        classfileBuffer.getName(),
//                        classfileBuffer.getContentTypes(),
//                        classfileBuffer.getScopes(),
//                        Format.CLASS
//                );
//                FileUtils.writeByteArrayToFile(outputFile, bytes);
//            }
//        }
//    }


}
