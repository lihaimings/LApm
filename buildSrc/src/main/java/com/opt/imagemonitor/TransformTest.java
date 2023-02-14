package com.opt.imagemonitor;

import com.android.build.api.transform.Format;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.api.transform.TransformOutputProvider;
import com.android.build.gradle.internal.pipeline.TransformManager;
import com.android.utils.FileUtils;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

public class TransformTest extends Transform {
    @Override
    public String getName() {
        return "TransformSam";
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT;
    }

    @Override
    public boolean isIncremental() {
        return false;
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation);
        try {
            doTransform(transformInvocation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doTransform(TransformInvocation transformInvocation) throws IOException {
        System.out.println("doTransform   =======================================================");
        // 输入
        Collection<TransformInput> inputs = transformInvocation.getInputs();
        // 输出
        TransformOutputProvider outputProvider = transformInvocation.getOutputProvider();
        if (outputProvider != null) {
            outputProvider.deleteAll();
        }

        // 有目录格式和jar格式
        inputs.forEach(transformInput -> {
            transformInput.getDirectoryInputs().forEach(directoryInput -> {
                ArrayList<File> list = new ArrayList<>();
                getFileList(directoryInput.getFile(), list);
                list.forEach(file -> {
//                    System.out.println("getDirectoryInputs   =======================================================" + file.getName());
                    if (file.isFile() && file.getName().endsWith(".class")) {
                        try {
                            ClassReader classReader = new ClassReader(new FileInputStream(file));
                            ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);
//                            ClassVisitor visitor = new TestClassVisitor(classWriter);
                            ClassVisitor visitor = new StartUpClassVisitor(classWriter);
                            classReader.accept(visitor, ClassReader.EXPAND_FRAMES);
                            //通过toByteArray方法，将变更后信息转成byte数组
                            byte[] bytes = classWriter.toByteArray();
                            //放入输出流中往原文件中写入
                            FileOutputStream fileOutputStream = new FileOutputStream(file.getAbsolutePath());
                            fileOutputStream.write(bytes);
                            fileOutputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                if (outputProvider != null) {
                    File dest = outputProvider.getContentLocation(directoryInput.getName(), directoryInput.getContentTypes(), directoryInput.getScopes(), Format.DIRECTORY);
                    try {
                        FileUtils.copyDirectory(directoryInput.getFile(), dest);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            });

            transformInput.getJarInputs().forEach(jarInput -> {
                ArrayList<File> list = new ArrayList<>();
                getFileList(jarInput.getFile(), list);
                list.forEach(file -> {
//                    System.out.println("getJarInputs   =======================================================" + file.getName());
                });
                if (outputProvider != null) {
                    File dest = outputProvider.getContentLocation(
                            jarInput.getName(),
                            jarInput.getContentTypes(),
                            jarInput.getScopes(),
                            Format.JAR
                    );
                    try {
                        FileUtils.copyFile(jarInput.getFile(), dest);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            });
        });

    }

    private void getFileList(File file, ArrayList<File> fileList) {
        if (file.isFile()) {
            fileList.add(file);
        } else {
            File[] list = file.listFiles();
            for (File value : list) {
                getFileList(value, fileList);
            }
        }
    }

}
