package com.opt.imagemonitor;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;

import java.util.ArrayList;
import java.util.HashSet;

public class StartUpClassVisitor extends ClassVisitor {

    private String className;
    private String superName;
    private boolean isActivity;
    private boolean hasWindowFocusMethod = false;
    private boolean hasAttachBaseContext = false;
    private boolean isABSClass = false;
    private boolean isApplication = false;
    private ArrayList<String> superClasses = new ArrayList<>(7);
    private HashSet<String> methods = new HashSet<>();

    public StartUpClassVisitor(ClassVisitor classVisitor) {
        super(Opcodes.ASM6, classVisitor);
        superClasses.add(BuildConstants.TRACE_V4_ACTIVITY_CLASS);
        superClasses.add(BuildConstants.TRACE_ACTIVITY_CLASS);
        superClasses.add(BuildConstants.TRACE_V7_ACTIVITY_CLASS);
        superClasses.add(BuildConstants.TRACE_ANDROIDX_APPCOMPAT_ACTIVITY_CLASS);
        superClasses.add(BuildConstants.TRACE_ANDROIDX_CORE_COMPONENT_ACTIVITY_CLASS);
        superClasses.add(BuildConstants.TRACE_ANDROIDX_FRAGMENT_ACTIVITY_CLASS);
        superClasses.add(BuildConstants.TRACE_ANDROIDX_Component_ACTIVITY_CLASS);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        System.out.println("superName = " + superName.replace("/", "."));
        this.className = name;
        this.superName = superName;
        String superNameReplace = superName.replace("/", ".");
        this.isActivity = !name.startsWith("androidx/") && (
                name.endsWith("Activity") || (superName != null && superClasses.contains(superNameReplace))
        );
        this.isApplication = superName != null && BuildConstants.TRACE_APP_APPLICATION.equals(superNameReplace);
        if ((access & Opcodes.ACC_ABSTRACT) > 0 || (access & Opcodes.ACC_INTERFACE) > 0) {
            this.isABSClass = true;
        }
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        System.out.println("Method Name = " + name);
        MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);
        if (isABSClass) {
            return methodVisitor;
        }
        // 寻找Activity的onWindowFocusChange方法
        if (isActivity && !hasWindowFocusMethod) {
            hasWindowFocusMethod = isWindowFocusMethod(name, descriptor);
        }
        // 寻找Application的attachBaseContext方法
        if (isApplication && !hasAttachBaseContext) {
            hasAttachBaseContext = isAttachBaseContext(name, descriptor);
        }
        return new TraceMethodAdapter(api, methodVisitor, access, name, descriptor, className, hasWindowFocusMethod, hasAttachBaseContext, isActivity, isApplication);
    }

    @Override
    public void visitEnd() {
        System.out.println("--> visitEnd() ");
        if (!hasWindowFocusMethod && isActivity) {
            insertWindowFocusChangeMethod(cv, className, superName);
        }

        if (!hasAttachBaseContext && isApplication) {
            System.out.println("hasAttachBaseContext 重写代码");
            insertAttachBaseContextMethod(cv, className, superName);
        }
        super.visitEnd();

    }

    /**
     * 重写onWindowFocusChange()方法
     *
     * @param cv
     * @param className
     * @param superName
     */
    private void insertWindowFocusChangeMethod(ClassVisitor cv, String className, String superName) {
        MethodVisitor methodVisitor = cv.visitMethod(Opcodes.ACC_PUBLIC, BuildConstants.TRACE_ON_WINDOW_FOCUS_METHOD, BuildConstants.TRACE_ON_WINDOW_FOCUS_METHOD_ARGS,
                null, null);
        methodVisitor.visitCode();
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
        methodVisitor.visitVarInsn(Opcodes.ILOAD, 1);
        methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, superName, BuildConstants.TRACE_ON_WINDOW_FOCUS_METHOD,
                BuildConstants.TRACE_ON_WINDOW_FOCUS_METHOD_ARGS, false);
        traceWindowFocusChangeMethod(methodVisitor, className);
        methodVisitor.visitInsn(Opcodes.RETURN);
        methodVisitor.visitMaxs(2, 2);
        methodVisitor.visitEnd();

    }

    private boolean isWindowFocusMethod(String name, String descriptor) {
        return null != name && null != name && name.equals(BuildConstants.TRACE_ON_WINDOW_FOCUS_METHOD) &&
                descriptor.equals(BuildConstants.TRACE_ON_WINDOW_FOCUS_METHOD_ARGS);
    }

    /**
     * 插桩AppMethodBeat.at()方法
     *
     * @param mv
     * @param classname
     */
    private void traceWindowFocusChangeMethod(MethodVisitor mv, String classname) {
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitVarInsn(Opcodes.ILOAD, 1);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, BuildConstants.MATRIX_TRACE_CLASS, "at", "(Landroid/app/Activity;Z)V", false);
    }

    private boolean isAttachBaseContext(String name, String descriptor) {
        return null != name && null != name && name.equals(BuildConstants.TRACE_APPLICATION_ATTACH_BASE_CONTEXT) &&
                descriptor.equals(BuildConstants.TRACE_ATTACH_BASE_CONTEXT_METHOD_ARGS);
    }

    /**
     * 重写attachBaseContext()方法
     *
     * @param classVisitor
     * @param className
     * @param superName
     */
    private void insertAttachBaseContextMethod(ClassVisitor classVisitor, String className, String superName) {
        MethodVisitor methodVisitor = classVisitor.visitMethod(Opcodes.ACC_PROTECTED, BuildConstants.TRACE_APPLICATION_ATTACH_BASE_CONTEXT, BuildConstants.TRACE_ATTACH_BASE_CONTEXT_METHOD_ARGS, null, null);
        methodVisitor.visitCode();
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
        methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, superName, BuildConstants.TRACE_APPLICATION_ATTACH_BASE_CONTEXT, BuildConstants.TRACE_ATTACH_BASE_CONTEXT_METHOD_ARGS, false);
        traceAttachBaseContext(methodVisitor);
        methodVisitor.visitInsn(Opcodes.RETURN);
        methodVisitor.visitMaxs(2, 2);
        methodVisitor.visitEnd();

    }

    /**
     * 插桩AppMethodBeat.attachBaseContext()方法
     *
     * @param mv
     */
    private void traceAttachBaseContext(MethodVisitor mv) {
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, BuildConstants.MATRIX_TRACE_CLASS, "attachBaseContext", "()V", false);
    }

    public class TraceMethodAdapter extends AdviceAdapter {

        private String methodName;
        private String name;
        private String className;
        private boolean hasWindowFocusMethod;
        private boolean hasAttachBaseContext;
        private boolean isActivity;
        private boolean isApplication = false;

        protected TraceMethodAdapter(int api, MethodVisitor methodVisitor, int access, String name, String descriptor, String className,
                                     boolean hasWindowFocusMethod, boolean hasAttachBaseContext, boolean isActivity, boolean isApplication) {
            super(api, methodVisitor, access, name, descriptor);
            this.methodName = name;
            this.className = className;
            this.hasWindowFocusMethod = hasWindowFocusMethod;
            this.hasAttachBaseContext = hasAttachBaseContext;
            this.isActivity = isActivity;
            this.isApplication = isApplication;
        }

        @Override
        protected void onMethodEnter() {
            super.onMethodEnter();
            System.out.println("method = " + methodName);
            if (hasWindowFocusMethod && isActivity && methodName.equals(BuildConstants.TRACE_ON_WINDOW_FOCUS_METHOD)) {
                traceWindowFocusChangeMethod(mv, className);
            }
            if (hasAttachBaseContext && isApplication && methodName.equals(BuildConstants.TRACE_APPLICATION_ATTACH_BASE_CONTEXT)) {
                System.out.println("hasAttachBaseContext 插入代码");
                traceAttachBaseContext(mv);
            }
        }

        @Override
        protected void onMethodExit(int opcode) {
            super.onMethodExit(opcode);

        }

    }

}
