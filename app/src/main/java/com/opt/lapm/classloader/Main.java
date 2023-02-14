package com.opt.lapm.classloader;

import static java.sql.DriverManager.println;

import android.system.ErrnoException;
import android.system.StructStat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import dalvik.system.DexClassLoader;
import dalvik.system.DexFile;
import dalvik.system.PathClassLoader;

public class Main {


//    /**
//     * 双亲委托机制
//     */
//    protected Class<?> loadClass(String name, boolean resolve)
//            throws ClassNotFoundException {
//        // 1. 先检查class是否已经加载过
//        Class<?> c = findLoadedClass(name);
//        if (c == null) {
//            // 没有加载过
//            try {
//                if (parent != null) {
//                    // 先给父ClassLoader加载Class
//                    c = parent.loadClass(name, false);
//                } else {
//                    // 调用BootstrapClassLoader加载Class
//                    c = findBootstrapClassOrNull(name);
//                }
//            } catch (ClassNotFoundException e) {
//                // ClassNotFoundException thrown if class not found
//                // from the non-null parent class loader
//            }
//
//
//            if (c == null) {
//                // 父的ClassLoader都没有加载class，则调用findClass()给此ClassLoader加载
//                c = findClass(name);
//            }
//        }
//        return c;
//    }


}
//
//
//public abstract class ClassLoader {
//
//    static private class SystemClassLoader {
//        public static ClassLoader loader = ClassLoader.createSystemClassLoader();
//    }
//
//    public final Map<List<Class<?>>, Class<?>> proxyCache =
//            new HashMap<List<Class<?>>, Class<?>>();
//
//    // The parent class loader for delegation
//    // Note: VM hardcoded the offset of this field, thus all new fields
//    // must be added *after* it.
//    private final ClassLoader parent;
//
//
//    private static ClassLoader createSystemClassLoader() {
//        String classPath = System.getProperty("java.class.path", ".");
//        String librarySearchPath = System.getProperty("java.library.path", "");
//        // 父加载器是BootClassLoader
//        return new PathClassLoader(classPath, librarySearchPath, BootClassLoader.getInstance());
//    }
//
//
//    private final HashMap<String, Package> packages = new HashMap<>();
//
//    private transient long allocator;
//
//    private transient long classTable;
//
//    private static Void checkCreateClassLoader() {
//        return null;
//    }
//
//    private ClassLoader(Void unused, ClassLoader parent) {
//        this.parent = parent;
//    }
//
//
//    protected ClassLoader(ClassLoader parent) {
//        this(checkCreateClassLoader(), parent);
//    }
//
//    protected ClassLoader() {
//        this(checkCreateClassLoader(), getSystemClassLoader());
//    }
//
//
//    public Class<?> loadClass(String name) throws ClassNotFoundException {
//        return loadClass(name, false);
//    }
//
//
//    protected Class<?> loadClass(String name, boolean resolve)
//            throws ClassNotFoundException {
//        // First, check if the class has already been loaded
//        Class<?> c = findLoadedClass(name);
//        if (c == null) {
//            try {
//                if (parent != null) {
//                    c = parent.loadClass(name, false);
//                } else {
//                    c = findBootstrapClassOrNull(name);
//                }
//            } catch (ClassNotFoundException e) {
//                // ClassNotFoundException thrown if class not found
//                // from the non-null parent class loader
//            }
//
//            if (c == null) {
//                // If still not found, then invoke findClass in order
//                // to find the class.
//                c = findClass(name);
//            }
//        }
//        return c;
//    }
//
//    protected Class<?> findClass(String name) throws ClassNotFoundException {
//        throw new ClassNotFoundException(name);
//    }
//
//    @Deprecated
//    protected final Class<?> defineClass(byte[] b, int off, int len)
//            throws ClassFormatError {
//        throw new UnsupportedOperationException("can't load this type of class file");
//    }
//
//
//    protected final Class<?> defineClass(String name, byte[] b, int off, int len)
//            throws ClassFormatError {
//        throw new UnsupportedOperationException("can't load this type of class file");
//    }
//
//
//    protected final Class<?> defineClass(String name, byte[] b, int off, int len,
//                                         ProtectionDomain protectionDomain)
//            throws ClassFormatError {
//        throw new UnsupportedOperationException("can't load this type of class file");
//    }
//
//
//    protected final Class<?> defineClass(String name, java.nio.ByteBuffer b,
//                                         ProtectionDomain protectionDomain)
//            throws ClassFormatError {
//        throw new UnsupportedOperationException("can't load this type of class file");
//    }
//
//    protected final void resolveClass(Class<?> c) {
//    }
//
//
//    protected final Class<?> findSystemClass(String name)
//            throws ClassNotFoundException {
//        return Class.forName(name, false, getSystemClassLoader());
//    }
//
//
//    private Class<?> findBootstrapClassOrNull(String name) {
//        return null;
//    }
//
//    protected final Class<?> findLoadedClass(String name) {
//        ClassLoader loader;
//        if (this == BootClassLoader.getInstance())
//            loader = null;
//        else
//            loader = this;
//        return VMClassLoader.findLoadedClass(loader, name);
//    }
//
//
//    protected final void setSigners(Class<?> c, Object[] signers) {
//    }
//
//
//    public URL getResource(String name) {
//        URL url;
//        if (parent != null) {
//            url = parent.getResource(name);
//        } else {
//            url = getBootstrapResource(name);
//        }
//        if (url == null) {
//            url = findResource(name);
//        }
//        return url;
//    }
//
//
//    public Enumeration<URL> getResources(String name) throws IOException {
//        @SuppressWarnings("unchecked")
//        Enumeration<URL>[] tmp = (Enumeration<URL>[]) new Enumeration<?>[2];
//        if (parent != null) {
//            tmp[0] = parent.getResources(name);
//        } else {
//            tmp[0] = getBootstrapResources(name);
//        }
//        tmp[1] = findResources(name);
//
//        return new CompoundEnumeration<>(tmp);
//    }
//
//
//    protected URL findResource(String name) {
//        return null;
//    }
//
//    protected Enumeration<URL> findResources(String name) throws IOException {
//        return java.util.Collections.emptyEnumeration();
//    }
//
//    @CallerSensitive
//    protected static boolean registerAsParallelCapable() {
//        return true;
//    }
//
//    public static URL getSystemResource(String name) {
//        ClassLoader system = getSystemClassLoader();
//        if (system == null) {
//            return getBootstrapResource(name);
//        }
//        return system.getResource(name);
//    }
//
//
//    public static Enumeration<URL> getSystemResources(String name)
//            throws IOException {
//        ClassLoader system = getSystemClassLoader();
//        if (system == null) {
//            return getBootstrapResources(name);
//        }
//        return system.getResources(name);
//    }
//
//
//    private static URL getBootstrapResource(String name) {
//        return null;
//    }
//
//    private static Enumeration<URL> getBootstrapResources(String name)
//            throws IOException {
//        return null;
//    }
//
//
//    public InputStream getResourceAsStream(String name) {
//        URL url = getResource(name);
//        try {
//            return url != null ? url.openStream() : null;
//        } catch (IOException e) {
//            return null;
//        }
//    }
//
//
//    public static InputStream getSystemResourceAsStream(String name) {
//        URL url = getSystemResource(name);
//        try {
//            return url != null ? url.openStream() : null;
//        } catch (IOException e) {
//            return null;
//        }
//    }
//
//
//    @CallerSensitive
//    public final ClassLoader getParent() {
//        return parent;
//    }
//
//    @CallerSensitive
//    public static ClassLoader getSystemClassLoader() {
//        return SystemClassLoader.loader;
//    }
//
//    // Returns the class's class loader, or null if none.
//    static ClassLoader getClassLoader(Class<?> caller) {
//        // This can be null if the VM is requesting it
//        if (caller == null) {
//            return null;
//        }
//        // Android-changed: Use Class.getClassLoader(); there is no Class.getClassLoader0().
//        // // Circumvent security check since this is package-private
//        // return caller.getClassLoader0();
//        return caller.getClassLoader();
//    }
//
//    protected Package definePackage(String name, String specTitle,
//                                    String specVersion, String specVendor,
//                                    String implTitle, String implVersion,
//                                    String implVendor, URL sealBase)
//            throws IllegalArgumentException {
//        synchronized (packages) {
//            Package pkg = packages.get(name);
//            if (pkg != null) {
//                throw new IllegalArgumentException(name);
//            }
//            pkg = new Package(name, specTitle, specVersion, specVendor,
//                    implTitle, implVersion, implVendor,
//                    sealBase, this);
//            packages.put(name, pkg);
//            return pkg;
//        }
//    }
//
//
//    protected Package getPackage(String name) {
//        Package pkg;
//        synchronized (packages) {
//            pkg = packages.get(name);
//        }
//        return pkg;
//    }
//
//
//    protected Package[] getPackages() {
//        Map<String, Package> map;
//        synchronized (packages) {
//            map = new HashMap<>(packages);
//        }
//        Package[] pkgs;
//        return map.values().toArray(new Package[map.size()]);
//    }
//
//    protected String findLibrary(String libname) {
//        return null;
//    }
//
//    public void setDefaultAssertionStatus(boolean enabled) {
//    }
//
//    public void setPackageAssertionStatus(String packageName,
//                                          boolean enabled) {
//    }
//
//    public void setClassAssertionStatus(String className, boolean enabled) {
//    }
//
//    public void clearAssertionStatus() {
//
//    }
//}
//
//
//class BootClassLoader extends ClassLoader {
//
//    private static BootClassLoader instance;
//
//    @FindBugsSuppressWarnings("DP_CREATE_CLASSLOADER_INSIDE_DO_PRIVILEGED")
//    public static synchronized BootClassLoader getInstance() {
//        if (instance == null) {
//            instance = new BootClassLoader();
//        }
//
//        return instance;
//    }
//
//    public BootClassLoader() {
//        super(null);
//    }
//
//    @Override
//    protected Class<?> findClass(String name) throws ClassNotFoundException {
//        return Class.classForName(name, false, null);
//    }
//
//    @Override
//    protected URL findResource(String name) {
//        return VMClassLoader.getResource(name);
//    }
//
//    @SuppressWarnings("unused")
//    @Override
//    protected Enumeration<URL> findResources(String resName) throws IOException {
//        return Collections.enumeration(VMClassLoader.getResources(resName));
//    }
//
//
//    @Override
//    protected Package getPackage(String name) {
//        if (name != null && !name.isEmpty()) {
//            synchronized (this) {
//                Package pack = super.getPackage(name);
//
//                if (pack == null) {
//                    pack = definePackage(name, "Unknown", "0.0", "Unknown", "Unknown", "0.0",
//                            "Unknown", null);
//                }
//
//                return pack;
//            }
//        }
//
//        return null;
//    }
//
//    @Override
//    public URL getResource(String resName) {
//        return findResource(resName);
//    }
//
//    @Override
//    protected Class<?> loadClass(String className, boolean resolve)
//            throws ClassNotFoundException {
//        Class<?> clazz = findLoadedClass(className);
//
//        if (clazz == null) {
//            clazz = findClass(className);
//        }
//
//        return clazz;
//    }
//
//    @Override
//    public Enumeration<URL> getResources(String resName) throws IOException {
//        return findResources(resName);
//    }
//}
//
//
//public class BaseDexClassLoader extends ClassLoader {
//
//    /* @NonNull */ private static volatile Reporter reporter = null;
//
//    @UnsupportedAppUsage
//    private final DexPathList pathList;
//
//    protected final ClassLoader[] sharedLibraryLoaders;
//
//    public BaseDexClassLoader(String dexPath, File optimizedDirectory,
//                              String librarySearchPath, ClassLoader parent) {
//        this(dexPath, librarySearchPath, parent, null, false);
//    }
//
//    @UnsupportedAppUsage
//    public BaseDexClassLoader(String dexPath, File optimizedDirectory,
//                              String librarySearchPath, ClassLoader parent, boolean isTrusted) {
//        this(dexPath, librarySearchPath, parent, null, isTrusted);
//    }
//
//
//    public BaseDexClassLoader(String dexPath,
//                              String librarySearchPath, ClassLoader parent, ClassLoader[] libraries) {
//        this(dexPath, librarySearchPath, parent, libraries, false);
//    }
//
//
//    public BaseDexClassLoader(String dexPath,
//                              String librarySearchPath, ClassLoader parent, ClassLoader[] sharedLibraryLoaders,
//                              boolean isTrusted) {
//        super(parent);
//        this.sharedLibraryLoaders = sharedLibraryLoaders == null
//                ? null
//                : Arrays.copyOf(sharedLibraryLoaders, sharedLibraryLoaders.length);
//        this.pathList = new DexPathList(this, dexPath, librarySearchPath, null, isTrusted);
//
//        reportClassLoaderChain();
//    }
//
//
//    @libcore.api.CorePlatformApi
//    public void reportClassLoaderChain() {
//        if (reporter == null) {
//            return;
//        }
//
//        String[] classPathAndClassLoaderContexts = computeClassLoaderContextsNative();
//        if (classPathAndClassLoaderContexts.length == 0) {
//            return;
//        }
//        Map<String, String> dexFileMapping =
//                new HashMap<>(classPathAndClassLoaderContexts.length / 2);
//        for (int i = 0; i < classPathAndClassLoaderContexts.length; i += 2) {
//            dexFileMapping.put(classPathAndClassLoaderContexts[i],
//                    classPathAndClassLoaderContexts[i + 1]);
//        }
//        reporter.report(Collections.unmodifiableMap(dexFileMapping));
//    }
//
//    private native String[] computeClassLoaderContextsNative();
//
//
//    public BaseDexClassLoader(ByteBuffer[] dexFiles, String librarySearchPath, ClassLoader parent) {
//        super(parent);
//        this.sharedLibraryLoaders = null;
//        this.pathList = new DexPathList(this, librarySearchPath);
//        this.pathList.initByteBufferDexPath(dexFiles);
//    }
//
//    @Override
//    protected Class<?> findClass(String name) throws ClassNotFoundException {
//        // First, check whether the class is present in our shared libraries.
//        if (sharedLibraryLoaders != null) {
//            for (ClassLoader loader : sharedLibraryLoaders) {
//                try {
//                    return loader.loadClass(name);
//                } catch (ClassNotFoundException ignored) {
//                }
//            }
//        }
//        // Check whether the class in question is present in the dexPath that
//        // this classloader operates on.
//        List<Throwable> suppressedExceptions = new ArrayList<Throwable>();
//        //
//        Class c = pathList.findClass(name, suppressedExceptions);
//        if (c == null) {
//            ClassNotFoundException cnfe = new ClassNotFoundException(
//                    "Didn't find class \"" + name + "\" on path: " + pathList);
//            for (Throwable t : suppressedExceptions) {
//                cnfe.addSuppressed(t);
//            }
//            throw cnfe;
//        }
//        return c;
//    }
//
//    @UnsupportedAppUsage
//    @libcore.api.CorePlatformApi
//    public void addDexPath(String dexPath) {
//        addDexPath(dexPath, false /*isTrusted*/);
//    }
//
//    @UnsupportedAppUsage
//    public void addDexPath(String dexPath, boolean isTrusted) {
//        pathList.addDexPath(dexPath, null /*optimizedDirectory*/, isTrusted);
//    }
//
//
//    @libcore.api.CorePlatformApi
//    public void addNativePath(Collection<String> libPaths) {
//        pathList.addNativePath(libPaths);
//    }
//
//    @Override
//    protected URL findResource(String name) {
//        (sharedLibraryLoaders != null) {
//            for (ClassLoader loader : sharedLibraryLoaders) {
//                URL url = loader.getResource(name);
//                if (url != null) {
//                    return url;
//                }
//            }
//        }
//        return pathList.findResource(name);
//    }
//
//    @Override
//    protected Enumeration<URL> findResources(String name) {
//        Enumeration<URL> myResources = pathList.findResources(name);
//        if (sharedLibraryLoaders == null) {
//            return myResources;
//        }
//
//        Enumeration<URL>[] tmp =
//                (Enumeration<URL>[]) new Enumeration<?>[sharedLibraryLoaders.length + 1];
//        // This will add duplicate resources if a shared library is loaded twice, but that's ok
//        // as we don't guarantee uniqueness.
//        for (int i = 0; i < sharedLibraryLoaders.length; i++) {
//            try {
//                tmp[i] = sharedLibraryLoaders[i].getResources(name);
//            } catch (IOException e) {
//                // Ignore.
//            }
//        }
//        tmp[sharedLibraryLoaders.length] = myResources;
//        return new CompoundEnumeration<>(tmp);
//    }
//
//    @Override
//    public String findLibrary(String name) {
//        return pathList.findLibrary(name);
//    }
//
//
//    @Override
//    protected synchronized Package getPackage(String name) {
//        if (name != null && !name.isEmpty()) {
//            Package pack = super.getPackage(name);
//
//            if (pack == null) {
//                pack = definePackage(name, "Unknown", "0.0", "Unknown",
//                        317"Unknown", "0.0", "Unknown", null);
//            }
//
//            return pack;
//        }
//
//        return null;
//    }
//
//    @UnsupportedAppUsage
//    @libcore.api.CorePlatformApi
//    public String getLdLibraryPath() {
//        StringBuilder result = new StringBuilder();
//        for (File directory : pathList.getNativeLibraryDirectories()) {
//            if (result.length() > 0) {
//                result.append(':');
//            }
//            result.append(directory);
//        }
//
//        return result.toString();
//    }
//
//    @Override
//    public String toString() {
//        return getClass().getName() + "[" + pathList + "]";
//    }
//
//
//    @libcore.api.CorePlatformApi
//    public static void setReporter(Reporter newReporter) {
//        reporter = newReporter;
//    }
//
//
//    public static Reporter getReporter() {
//        return reporter;
//    }
//
//
//    @libcore.api.CorePlatformApi
//    public interface Reporter {
//
//        @libcore.api.CorePlatformApi
//        void report(Map<String, String> contextsMap);
//    }
//}
//
//
//public final class DexPathList {
//    private static final String DEX_SUFFIX = ".dex";
//    private static final String zipSeparator = "!/";
//
//    @UnsupportedAppUsage
//    private final ClassLoader definingContext;
//
//    @UnsupportedAppUsage
//    private Element[] dexElements;
//
//    @UnsupportedAppUsage
//    /* package visible for testing */ NativeLibraryElement[] nativeLibraryPathElements;
//
//    @UnsupportedAppUsage
//    private final List<File> nativeLibraryDirectories;
//
//    @UnsupportedAppUsage
//    private final List<File> systemNativeLibraryDirectories;
//
//    @UnsupportedAppUsage
//    private IOException[] dexElementsSuppressedExceptions;
//
//    private List<File> getAllNativeLibraryDirectories() {
//        List<File> allNativeLibraryDirectories = new ArrayList<>(nativeLibraryDirectories);
//        allNativeLibraryDirectories.addAll(systemNativeLibraryDirectories);
//        return allNativeLibraryDirectories;
//    }
//
//    public DexPathList(ClassLoader definingContext, String librarySearchPath) {
//        if (definingContext == null) {
//            throw new NullPointerException("definingContext == null");
//        }
//
//        this.definingContext = definingContext;
//        this.nativeLibraryDirectories = splitPaths(librarySearchPath, false);
//        this.systemNativeLibraryDirectories =
//                splitPaths(System.getProperty("java.library.path"), true);
//        this.nativeLibraryPathElements = makePathElements(getAllNativeLibraryDirectories());
//    }
//
//    @UnsupportedAppUsage
//    public DexPathList(ClassLoader definingContext, String dexPath,
//                       String librarySearchPath, File optimizedDirectory) {
//        this(definingContext, dexPath, librarySearchPath, optimizedDirectory, false);
//    }
//
//    DexPathList(ClassLoader definingContext, String dexPath,
//                String librarySearchPath, File optimizedDirectory, boolean isTrusted) {
//        if (definingContext == null) {
//            throw new NullPointerException("definingContext == null");
//        }
//
//        if (dexPath == null) {
//            throw new NullPointerException("dexPath == null");
//        }
//
//        if (optimizedDirectory != null) {
//            if (!optimizedDirectory.exists()) {
//                throw new IllegalArgumentException(
//                        "optimizedDirectory doesn't exist: "
//                                + optimizedDirectory);
//            }
//
//            if (!(optimizedDirectory.canRead()
//                    && optimizedDirectory.canWrite())) {
//                throw new IllegalArgumentException(
//                        "optimizedDirectory not readable/writable: "
//                                + optimizedDirectory);
//            }
//        }
//
//        this.definingContext = definingContext;
//
//        ArrayList<IOException> suppressedExceptions = new ArrayList<IOException>();
//        // save dexPath for BaseDexClassLoader
//        this.dexElements = makeDexElements(splitDexPath(dexPath), optimizedDirectory,
//                suppressedExceptions, definingContext, isTrusted);
//        this.nativeLibraryDirectories = splitPaths(librarySearchPath, false);
//        this.systemNativeLibraryDirectories =
//                splitPaths(System.getProperty("java.library.path"), true);
//        this.nativeLibraryPathElements = makePathElements(getAllNativeLibraryDirectories());
//
//        if (suppressedExceptions.size() > 0) {
//            this.dexElementsSuppressedExceptions =
//                    suppressedExceptions.toArray(new IOException[suppressedExceptions.size()]);
//        } else {
//            dexElementsSuppressedExceptions = null;
//        }
//    }
//
//    @Override
//    public String toString() {
//        return "DexPathList[" + Arrays.toString(dexElements) +
//                ",nativeLibraryDirectories=" +
//                Arrays.toString(getAllNativeLibraryDirectories().toArray()) + "]";
//    }
//
//
//    public List<File> getNativeLibraryDirectories() {
//        return nativeLibraryDirectories;
//    }
//
//    @UnsupportedAppUsage
//    public void addDexPath(String dexPath, File optimizedDirectory) {
//        addDexPath(dexPath, optimizedDirectory, false);
//    }
//
//    public void addDexPath(String dexPath, File optimizedDirectory, boolean isTrusted) {
//        final List<IOException> suppressedExceptionList = new ArrayList<IOException>();
//        final Element[] newElements = makeDexElements(splitDexPath(dexPath), optimizedDirectory,
//                suppressedExceptionList, definingContext, isTrusted);
//
//        if (newElements != null && newElements.length > 0) {
//            dexElements = concat(Element.class, dexElements, newElements);
//        }
//
//        if (suppressedExceptionList.size() > 0) {
//            final IOException[] newSuppExceptions = suppressedExceptionList.toArray(
//                    new IOException[suppressedExceptionList.size()]);
//            dexElementsSuppressedExceptions = dexElementsSuppressedExceptions != null
//                    ? concat(IOException.class, dexElementsSuppressedExceptions, newSuppExceptions)
//                    : newSuppExceptions;
//        }
//    }
//
//    private static <T> T[] concat(Class<T> componentType, T[] inputA, T[] inputB) {
//        T[] output = (T[]) Array.newInstance(componentType, inputA.length + inputB.length);
//        System.arraycopy(inputA, 0, output, 0, inputA.length);
//        System.arraycopy(inputB, 0, output, inputA.length, inputB.length);
//        return output;
//    }
//
//
//    /* package */ void initByteBufferDexPath(ByteBuffer[] dexFiles) {
//        if (dexFiles == null) {
//            throw new NullPointerException("dexFiles == null");
//        }
//        if (Arrays.stream(dexFiles).anyMatch(v -> v == null)) {
//            throw new NullPointerException("dexFiles contains a null Buffer!");
//        }
//        if (dexElements != null || dexElementsSuppressedExceptions != null) {
//            throw new IllegalStateException("Should only be called once");
//        }
//
//        final List<IOException> suppressedExceptions = new ArrayList<IOException>();
//
//        try {
//            Element[] null_elements = null;
//            DexFile dex = new DexFile(dexFiles, definingContext, null_elements);
//            // Capture class loader context from *before* `dexElements` is set (see comment below).
//            String classLoaderContext = dex.isBackedByOatFile()
//                    ? null : DexFile.getClassLoaderContext(definingContext, null_elements);
//            dexElements = new Element[]{new Element(dex)};
//            if (classLoaderContext != null) {
//                dex.verifyInBackground(definingContext, classLoaderContext);
//            }
//        } catch (IOException suppressed) {
//            System.logE("Unable to load dex files", suppressed);
//            suppressedExceptions.add(suppressed);
//            dexElements = new Element[0];
//        }
//
//        if (suppressedExceptions.size() > 0) {
//            dexElementsSuppressedExceptions = suppressedExceptions.toArray(
//                    new IOException[suppressedExceptions.size()]);
//        }
//    }
//
//
//    private static List<File> splitDexPath(String path) {
//        return splitPaths(path, false);
//    }
//
//    @UnsupportedAppUsage
//    private static List<File> splitPaths(String searchPath, boolean directoriesOnly) {
//        List<File> result = new ArrayList<>();
//
//        if (searchPath != null) {
//            for (String path : searchPath.split(File.pathSeparator)) {
//                if (directoriesOnly) {
//                    try {
//                        StructStat sb = Libcore.os.stat(path);
//                        if (!S_ISDIR(sb.st_mode)) {
//                            continue;
//                        }
//                    } catch (ErrnoException ignored) {
//                        continue;
//                    }
//                }
//                result.add(new File(path));
//            }
//        }
//
//        return result;
//    }
//
//    // This method is not used anymore. Kept around only because there are many legacy users of it.
//    @SuppressWarnings("unused")
//    @UnsupportedAppUsage
//    public static Element[] makeInMemoryDexElements(ByteBuffer[] dexFiles,
//                                                    List<IOException> suppressedExceptions) {
//        Element[] elements = new Element[dexFiles.length];
//        int elementPos = 0;
//        for (ByteBuffer buf : dexFiles) {
//            try {
//                DexFile dex = new DexFile(new ByteBuffer[]{buf}, /* classLoader */ null,
//                        /* dexElements */ null);
//                elements[elementPos++] = new Element(dex);
//            } catch (IOException suppressed) {
//                System.logE("Unable to load dex file: " + buf, suppressed);
//                suppressedExceptions.add(suppressed);
//            }
//        }
//        if (elementPos != elements.length) {
//            elements = Arrays.copyOf(elements, elementPos);
//        }
//        return elements;
//    }
//
//
//    @UnsupportedAppUsage
//    private static Element[] makeDexElements(List<File> files, File optimizedDirectory,
//                                             List<IOException> suppressedExceptions, ClassLoader loader) {
//        return makeDexElements(files, optimizedDirectory, suppressedExceptions, loader, false);
//    }
//
//
//    private static Element[] makeDexElements(List<File> files, File optimizedDirectory,
//                                             List<IOException> suppressedExceptions, ClassLoader loader, boolean isTrusted) {
//        Element[] elements = new Element[files.size()];
//        int elementsPos = 0;
//        /*
//         * Open all files and load the (direct or contained) dex files up front.
//         */
//        for (File file : files) {
//            if (file.isDirectory()) {
//                // We support directories for looking up resources. Looking up resources in
//                // directories is useful for running libcore tests.
//                elements[elementsPos++] = new Element(file);
//            } else if (file.isFile()) {
//                String name = file.getName();
//
//                DexFile dex = null;
//                if (name.endsWith(DEX_SUFFIX)) {
//                    // Raw dex file (not inside a zip/jar).
//                    try {
//                        dex = loadDexFile(file, optimizedDirectory, loader, elements);
//                        if (dex != null) {
//                            elements[elementsPos++] = new Element(dex, null);
//                        }
//                    } catch (IOException suppressed) {
//                        System.logE("Unable to load dex file: " + file, suppressed);
//                        suppressedExceptions.add(suppressed);
//                    }
//                } else {
//                    try {
//                        dex = loadDexFile(file, optimizedDirectory, loader, elements);
//                    } catch (IOException suppressed) {
//                        /*
//                         * IOException might get thrown "legitimately" by the DexFile constructor if
//                         * the zip file turns out to be resource-only (that is, no classes.dex file
//                         * in it).
//                         * Let dex == null and hang on to the exception to add to the tea-leaves for
//                         * when findClass returns null.
//                         */
//                        suppressedExceptions.add(suppressed);
//                    }
//
//                    if (dex == null) {
//                        elements[elementsPos++] = new Element(file);
//                    } else {
//                        elements[elementsPos++] = new Element(dex, file);
//                    }
//                }
//                if (dex != null && isTrusted) {
//                    dex.setTrusted();
//                }
//            } else {
//                System.logW("ClassLoader referenced unknown path: " + file);
//            }
//        }
//        if (elementsPos != elements.length) {
//            elements = Arrays.copyOf(elements, elementsPos);
//        }
//        return elements;
//    }
//
//    @UnsupportedAppUsage
//    private static DexFile loadDexFile(File file, File optimizedDirectory, ClassLoader loader,
//                                       Element[] elements)
//            throws IOException {
//        if (optimizedDirectory == null) {
//            return new DexFile(file, loader, elements);
//        } else {
//            String optimizedPath = optimizedPathFor(file, optimizedDirectory);
//            return DexFile.loadDex(file.getPath(), optimizedPath, 0, loader, elements);
//        }
//    }
//
//    private static String optimizedPathFor(File path,
//                                           File optimizedDirectory) {
//        String fileName = path.getName();
//        if (!fileName.endsWith(DEX_SUFFIX)) {
//            int lastDot = fileName.lastIndexOf(".");
//            if (lastDot < 0) {
//                fileName += DEX_SUFFIX;
//            } else {
//                StringBuilder sb = new StringBuilder(lastDot + 4);
//                sb.append(fileName, 0, lastDot);
//                sb.append(DEX_SUFFIX);
//                fileName = sb.toString();
//            }
//        }
//
//        File result = new File(optimizedDirectory, fileName);
//        return result.getPath();
//    }
//
//    @UnsupportedAppUsage
//    @SuppressWarnings("unused")
//    private static Element[] makePathElements(List<File> files, File optimizedDirectory,
//                                              List<IOException> suppressedExceptions) {
//        return makeDexElements(files, optimizedDirectory, suppressedExceptions, null);
//    }
//
//    @UnsupportedAppUsage
//    private static NativeLibraryElement[] makePathElements(List<File> files) {
//        NativeLibraryElement[] elements = new NativeLibraryElement[files.size()];
//        int elementsPos = 0;
//        for (File file : files) {
//            String path = file.getPath();
//
//            if (path.contains(zipSeparator)) {
//                String split[] = path.split(zipSeparator, 2);
//                File zip = new File(split[0]);
//                String dir = split[1];
//                elements[elementsPos++] = new NativeLibraryElement(zip, dir);
//            } else if (file.isDirectory()) {
//                // We support directories for looking up native libraries.
//                elements[elementsPos++] = new NativeLibraryElement(file);
//            }
//        }
//        if (elementsPos != elements.length) {
//            elements = Arrays.copyOf(elements, elementsPos);
//        }
//        return elements;
//    }
//
//    public Class<?> findClass(String name, List<Throwable> suppressed) {
//        for (Element element : dexElements) {
//            Class<?> clazz = element.findClass(name, definingContext, suppressed);
//            if (clazz != null) {
//                return clazz;
//            }
//        }
//
//        if (dexElementsSuppressedExceptions != null) {
//            suppressed.addAll(Arrays.asList(dexElementsSuppressedExceptions));
//        }
//        return null;
//    }
//
//    public URL findResource(String name) {
//        for (Element element : dexElements) {
//            URL url = element.findResource(name);
//            if (url != null) {
//                return url;
//            }
//        }
//
//        return null;
//    }
//
//
//    public Enumeration<URL> findResources(String name) {
//        ArrayList<URL> result = new ArrayList<URL>();
//
//        for (Element element : dexElements) {
//            URL url = element.findResource(name);
//            if (url != null) {
//                result.add(url);
//            }
//        }
//
//        return Collections.enumeration(result);
//    }
//
//    public String findLibrary(String libraryName) {
//        String fileName = System.mapLibraryName(libraryName);
//
//        for (NativeLibraryElement element : nativeLibraryPathElements) {
//            String path = element.findNativeLibrary(fileName);
//
//            if (path != null) {
//                return path;
//            }
//        }
//
//        return null;
//    }
//
//    /*package*/ List<String> getDexPaths() {
//        List<String> dexPaths = new ArrayList<String>();
//        for (Element e : dexElements) {
//            String dexPath = e.getDexPath();
//            if (dexPath != null) {
//                // Add the element to the list only if it is a file. A null dex path signals the
//                // element is a resource directory or an in-memory dex file.
//                dexPaths.add(dexPath);
//            }
//        }
//        return dexPaths;
//    }
//
//    @UnsupportedAppUsage
//    public void addNativePath(Collection<String> libPaths) {
//        if (libPaths.isEmpty()) {
//            return;
//        }
//        List<File> libFiles = new ArrayList<>(libPaths.size());
//        for (String path : libPaths) {
//            libFiles.add(new File(path));
//        }
//        ArrayList<NativeLibraryElement> newPaths =
//                new ArrayList<>(nativeLibraryPathElements.length + libPaths.size());
//        newPaths.addAll(Arrays.asList(nativeLibraryPathElements));
//        for (NativeLibraryElement element : makePathElements(libFiles)) {
//            if (!newPaths.contains(element)) {
//                newPaths.add(element);
//            }
//        }
//        nativeLibraryPathElements = newPaths.toArray(new NativeLibraryElement[newPaths.size()]);
//    }
//
//    /*package*/ static class Element {
//        @UnsupportedAppUsage
//        private final File path;
//        /**
//         * Whether {@code path.isDirectory()}, or {@code null} if {@code path == null}.
//         */
//        private final Boolean pathIsDirectory;
//
//        @UnsupportedAppUsage
//        private final DexFile dexFile;
//
//        private ClassPathURLStreamHandler urlHandler;
//        private boolean initialized;
//
//        @UnsupportedAppUsage
//        public Element(DexFile dexFile, File dexZipPath) {
//            if (dexFile == null && dexZipPath == null) {
//                throw new NullPointerException("Either dexFile or path must be non-null");
//            }
//            this.dexFile = dexFile;
//            this.path = dexZipPath;
//            // Do any I/O in the constructor so we don't have to do it elsewhere, eg. toString().
//            this.pathIsDirectory = (path == null) ? null : path.isDirectory();
//        }
//
//        public Element(DexFile dexFile) {
//            this(dexFile, null);
//        }
//
//        public Element(File path) {
//            this(null, path);
//        }
//
//        @UnsupportedAppUsage
//        @Deprecated
//        public Element(File dir, boolean isDirectory, File zip, DexFile dexFile) {
//            this(dir != null ? null : dexFile, dir != null ? dir : zip);
//            System.err.println("Warning: Using deprecated Element constructor. Do not use internal"
//                    + " APIs, this constructor will be removed in the future.");
//            if (dir != null && (zip != null || dexFile != null)) {
//                throw new IllegalArgumentException("Using dir and zip|dexFile no longer"
//                        + " supported.");
//            }
//            if (isDirectory && (zip != null || dexFile != null)) {
//                throw new IllegalArgumentException("Unsupported argument combination.");
//            }
//        }
//
//        private String getDexPath() {
//            if (path != null) {
//                return path.isDirectory() ? null : path.getAbsolutePath();
//            } else if (dexFile != null) {
//                // DexFile.getName() returns the path of the dex file.
//                return dexFile.getName();
//            }
//            return null;
//        }
//
//        @Override
//        public String toString() {
//            if (dexFile == null) {
//                return (pathIsDirectory ? "directory \"" : "zip file \"") + path + "\"";
//            } else if (path == null) {
//                return "dex file \"" + dexFile + "\"";
//            } else {
//                return "zip file \"" + path + "\"";
//            }
//        }
//
//        public synchronized void maybeInit() {
//            if (initialized) {
//                return;
//            }
//
//            if (path == null || pathIsDirectory) {
//                initialized = true;
//                return;
//            }
//
//            try {
//                urlHandler = new ClassPathURLStreamHandler(path.getPath());
//            } catch (IOException ioe) {
//
//                System.logE("Unable to open zip file: " + path, ioe);
//                urlHandler = null;
//            }
//
//            // Mark this element as initialized only after we've successfully created
//            // the associated ClassPathURLStreamHandler. That way, we won't leave this
//            // element in an inconsistent state if an exception is thrown during initialization.
//            //
//            // See b/35633614.
//            initialized = true;
//        }
//
//        public Class<?> findClass(String name, ClassLoader definingContext,
//                                  List<Throwable> suppressed) {
//            return dexFile != null ? dexFile.loadClassBinaryName(name, definingContext, suppressed)
//                    : null;
//        }
//
//        public URL findResource(String name) {
//            maybeInit();
//
//            if (urlHandler != null) {
//                return urlHandler.getEntryUrlOrNull(name);
//            }
//
//            // We support directories so we can run tests and/or legacy code
//            // that uses Class.getResource.
//            if (path != null && path.isDirectory()) {
//                File resourceFile = new File(path, name);
//                if (resourceFile.exists()) {
//                    try {
//                        return resourceFile.toURI().toURL();
//                    } catch (MalformedURLException ex) {
//                        throw new RuntimeException(ex);
//                    }
//                }
//            }
//
//            return null;
//        }
//    }
//
//    /*package*/ static class NativeLibraryElement {
//
//        @UnsupportedAppUsage
//        private final File path;
//
//
//        private final String zipDir;
//
//        private ClassPathURLStreamHandler urlHandler;
//        private boolean initialized;
//
//        @UnsupportedAppUsage
//        public NativeLibraryElement(File dir) {
//            this.path = dir;
//            this.zipDir = null;
//
//            // We should check whether path is a directory, but that is non-eliminatable overhead.
//        }
//
//        public NativeLibraryElement(File zip, String zipDir) {
//            this.path = zip;
//            this.zipDir = zipDir;
//
//            // Simple check that should be able to be eliminated by inlining. We should also
//            // check whether path is a file, but that is non-eliminatable overhead.
//            if (zipDir == null) {
//                throw new IllegalArgumentException();
//            }
//        }
//
//        @Override
//        public String toString() {
//            if (zipDir == null) {
//                return "directory \"" + path + "\"";
//            } else {
//                return "zip file \"" + path + "\"" +
//                        (!zipDir.isEmpty() ? ", dir \"" + zipDir + "\"" : "");
//            }
//        }
//
//        public synchronized void maybeInit() {
//            if (initialized) {
//                return;
//            }
//
//            if (zipDir == null) {
//                initialized = true;
//                return;
//            }
//
//            try {
//                urlHandler = new ClassPathURLStreamHandler(path.getPath());
//            } catch (IOException ioe) {
//
//                System.logE("Unable to open zip file: " + path, ioe);
//                urlHandler = null;
//            }
//
//            // Mark this element as initialized only after we've successfully created
//            // the associated ClassPathURLStreamHandler. That way, we won't leave this
//            // element in an inconsistent state if an exception is thrown during initialization.
//            //
//            // See b/35633614.
//            initialized = true;
//        }
//
//        public String findNativeLibrary(String name) {
//            maybeInit();
//
//            if (zipDir == null) {
//                String entryPath = new File(path, name).getPath();
//                if (IoUtils.canOpenReadOnly(entryPath)) {
//                    return entryPath;
//                }
//            } else if (urlHandler != null) {
//                // Having a urlHandler means the element has a zip file.
//                // In this case Android supports loading the library iff
//                // it is stored in the zip uncompressed.
//                String entryName = zipDir + '/' + name;
//                if (urlHandler.isEntryStored(entryName)) {
//                    return path.getPath() + zipSeparator + entryName;
//                }
//            }
//
//            return null;
//        }
//
//        @Override
//        public boolean equals(Object o) {
//            if (this == o) return true;
//            if (!(o instanceof NativeLibraryElement)) return false;
//            NativeLibraryElement that = (NativeLibraryElement) o;
//            return Objects.equals(path, that.path) &&
//                    Objects.equals(zipDir, that.zipDir);
//        }
//
//        @Override
//        public int hashCode() {
//            return Objects.hash(path, zipDir);
//        }
//    }
//
//}
//
//
//public class PathClassLoader extends BaseDexClassLoader {
//
//    /**
//     * @param dexPath : Dex相关文件的路径
//     * @param parent  ： 父加载器
//     */
//    public PathClassLoader(String dexPath, ClassLoader parent) {
//        super(dexPath, null, null, parent);
//    }
//
//    /**
//     * @param dexPath                         : Dex相关文件的路径
//     * @param librarySearchPath：包含C/C++库的路径集合
//     * @param parent                          ： 父加载器
//     */
//    public PathClassLoader(String dexPath, String librarySearchPath, ClassLoader parent) {
//        super(dexPath, null, librarySearchPath, parent);
//    }
//    ...
//}
//
//
//public class ZygoteInit {
//
//    // 创建完system_server进程后，会执行此方法
//    private static Runnable handleSystemServerProcess(ZygoteArguments parsedArgs) {
//        if (systemServerClasspath != null) {
//            //...
//        } else {
//            ClassLoader cl = null;
//            // 创建PathClassLoader加载器
//            if (systemServerClasspath != null) {
//                cl = createPathClassLoader(systemServerClasspath, parsedArgs.mTargetSdkVersion);
//            }
//        }
//    }
//
//    static ClassLoader createPathClassLoader(String classPath, int targetSdkVersion) {
//        String libraryPath = System.getProperty("java.library.path");
//
//        // We use the boot class loader, that's what the runtime expects at AOT.
//        ClassLoader parent = ClassLoader.getSystemClassLoader().getParent();
//
//        // 创建工厂模式创建PathClassLoader
//        return ClassLoaderFactory.createClassLoader(classPath, libraryPath, libraryPath,
//                parent, targetSdkVersion, true /* isNamespaceShared */, null /* classLoaderName */);
//    }
//
//}
//
//public class DexClassLoader extends BaseDexClassLoader {
//
//    /**
//     * @param dexPath                         : Dex相关文件的路径
//     * @param optimizedDirectory:             解压的dex的存储路径
//     * @param librarySearchPath：包含C/C++库的路径集合
//     * @param parent                          ： 父加载器
//     */
//    public DexClassLoader(String dexPath, String optimizedDirectory,
//                          String librarySearchPath, ClassLoader parent) {
//        super(dexPath, null, librarySearchPath, parent);
//    }
//}


