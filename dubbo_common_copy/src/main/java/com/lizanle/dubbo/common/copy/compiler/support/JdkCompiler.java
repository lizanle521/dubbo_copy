package com.lizanle.dubbo.common.copy.compiler.support;

import com.lizanle.dubbo.common.copy.compiler.Compiler;
import com.lizanle.dubbo.common.copy.utils.ClassHelper;

import javax.tools.*;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.*;

public class JdkCompiler extends AbstractCompiler {
    private final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    private final DiagnosticCollector<JavaFileObject> diagnosticCollector = new DiagnosticCollector<>();

    private final ClassLoaderImpl classLoader;

    private final JavaFileManagerImpl javaFileManager;

    private volatile List<String> options;

    public JdkCompiler() {
        this.options = new ArrayList<>();
        options.add("-target");
        options.add("1.6");
        StandardJavaFileManager manager = compiler.getStandardFileManager(diagnosticCollector, null, null);
        final ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if(loader instanceof URLClassLoader && (!loader.getClass().getName().equals("sum.misc.Launcher$AppClassLoader"))){
            try {
                URLClassLoader urlClassLoader = (URLClassLoader)loader;
                List<File> files = new ArrayList<>();
                for (URL url : urlClassLoader.getURLs()) {
                    files.add(new File(url.getFile()));
                }
                manager.setLocation(StandardLocation.CLASS_PATH,files);
            } catch (IOException e) {
                throw new IllegalStateException(e.getMessage(),e);
            }
        }
        classLoader = AccessController.doPrivileged(new PrivilegedAction<ClassLoaderImpl>() {
            @Override
            public ClassLoaderImpl run() {
                return new ClassLoaderImpl(loader);
            }
        });
        javaFileManager = new JavaFileManagerImpl(manager,classLoader);
    }

    public Class<?> doCompile(String name, String sourceCode) throws Throwable {
        int i = name.lastIndexOf('.');
        String packageName = i < 0 ? "" : name.substring(0,i);
        String className = i < 0 ? name : name.substring(i+1);
        JavaFileObjectImpl javaFileObject = new JavaFileObjectImpl(className, sourceCode);
        javaFileManager.putFileForInput(StandardLocation.SOURCE_PATH,packageName,className+ClassUtils.JAVA_EXTENSIONS,javaFileObject);
        Boolean result = compiler.getTask(null, javaFileManager, diagnosticCollector, options, null, Arrays.asList(new JavaFileObject[]{javaFileObject})).call();
        if(result == null || !result.booleanValue()){
            throw new IllegalStateException("Compilation faild class "+name+",diagnostic " +diagnosticCollector );
        }
        return classLoader.loadClass(name);
    }

    private static final class JavaFileManagerImpl extends ForwardingJavaFileManager<JavaFileManager> {
        private ClassLoaderImpl classLoader;
        private final Map<URI,JavaFileObject> fileObjectMap = new HashMap<>();

        protected JavaFileManagerImpl(JavaFileManager fileManager,ClassLoaderImpl classLoader) {
            super(fileManager);
            this.classLoader = classLoader;
        }

        @Override
        public FileObject getFileForInput(Location location, String pkgName, String relativeName) throws IOException {
            JavaFileObject javaFileObject = fileObjectMap.get(uri(location, pkgName, relativeName));
            if(javaFileObject != null){
                return javaFileObject;
            }
            return super.getFileForInput(location, pkgName, relativeName);
        }

        public void putFileForInput(Location location, String pkgName, String relativeName,JavaFileObject fileObject) throws IOException {
            fileObjectMap.put(uri(location,pkgName,relativeName),fileObject);
        }

        private URI uri(Location location,String packageName,String relativeName){
            return ClassUtils.toURI(location.getName()+"/"+packageName+"/"+relativeName);
        }

        @Override
        public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
            JavaFileObjectImpl fileObject = new JavaFileObjectImpl(className, kind);
            classLoader.add(className,fileObject);
            return fileObject;
        }

        @Override
        public ClassLoader getClassLoader(Location location) {
            return classLoader;
        }

        @Override
        public String inferBinaryName(Location location, JavaFileObject file) {
            if(file instanceof JavaFileObjectImpl){
                return file.getName();
            }
            return super.inferBinaryName(location, file);
        }

        @Override
        public Iterable<JavaFileObject> list(Location location, String packageName, Set<JavaFileObject.Kind> kinds, boolean recurse) throws IOException {
            Iterable<JavaFileObject> result = super.list(location, packageName, kinds, recurse);
            // TODO 中间这里漏掉一段，暂时不知道那代码有啥用。就先不写
            ArrayList<JavaFileObject> files = new ArrayList<>();
            if(location == StandardLocation.CLASS_PATH && kinds.contains(JavaFileObject.Kind.CLASS)){
                for (JavaFileObject fileObject : fileObjectMap.values()) {
                    if(fileObject.getKind() == JavaFileObject.Kind.CLASS && fileObject.getName().startsWith(packageName)){
                        files.add(fileObject);
                    }
                }
                files.addAll(classLoader.files());
            } else if(location == StandardLocation.SOURCE_PATH && kinds.contains(JavaFileObject.Kind.SOURCE)){
                for (JavaFileObject fileObject : fileObjectMap.values()) {
                    if(fileObject.getKind() == JavaFileObject.Kind.SOURCE && fileObject.getName().startsWith(packageName)){
                        files.add(fileObject);
                    }
                }
            }

            for (JavaFileObject fileObject : result) {
                files.add(fileObject);
            }
            return files;
        }
    }

    private final class ClassLoaderImpl extends ClassLoader {
        private final Map<String,JavaFileObject> classes = new HashMap<>();

        ClassLoaderImpl(final ClassLoader parent) {
            super(parent);
        }
        Collection<JavaFileObject> files() {
            return Collections.unmodifiableCollection(classes.values());
        }

        @Override
        protected Class<?> findClass(final String name) throws ClassNotFoundException {
            JavaFileObject file = classes.get(name);
            if(file != null){
                byte[] byteCode = ((JavaFileObjectImpl) file).getByteCode();
                return defineClass(name,byteCode,0,byteCode.length);
            }
            try {
                return ClassHelper.forNameWithCallerClassLoader(name,getClass());
            } catch (ClassNotFoundException e) {
                return super.findClass(name);
            }
        }

        void add(final String name,final JavaFileObject javaFile){
            classes.put(name,javaFile);
        }

        @Override
        protected synchronized Class<?> loadClass(final String name,final boolean resolve) throws ClassNotFoundException {
            return super.loadClass(name, resolve);
        }

        @Override
        public InputStream getResourceAsStream(final String name) {
            if(name.endsWith(ClassUtils.CLASS_EXTENSIONS)){
                String qualifiedClassName = name.substring(0,name.length()-ClassUtils.CLASS_EXTENSIONS.length()).replaceAll("/",".");
                JavaFileObjectImpl javaFileObject = (JavaFileObjectImpl)classes.get(qualifiedClassName);
                if(javaFileObject != null){
                    return new ByteArrayInputStream(javaFileObject.getByteCode());
                }
            }
            return super.getResourceAsStream(name);
        }
    }

    private static final class JavaFileObjectImpl extends SimpleJavaFileObject {
        private final CharSequence source;
        private ByteArrayOutputStream bytecode;
        public JavaFileObjectImpl(final String name,final CharSequence s){
            super(ClassUtils.toURI(name+ClassUtils.JAVA_EXTENSIONS),Kind.SOURCE);
            this.source = s;
        }

        public JavaFileObjectImpl(final String name ,final Kind kind){
            super(ClassUtils.toURI(name),kind);
            this.source = null;
        }
        /**
         * Construct a SimpleJavaFileObject of the given kind and with the
         * given URI.
         *
         * @param uri  the URI for this file object
         * @param kind the kind of this file object
         */
        protected JavaFileObjectImpl(URI uri, Kind kind) {
            super(uri, kind);
            this.source = null;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
            if(source == null){
                throw new UnsupportedOperationException("source == null");
            }
            return source;
        }

        @Override
        public InputStream openInputStream() throws IOException {
            return new ByteArrayInputStream(getByteCode());
        }

        @Override
        public OutputStream openOutputStream() throws IOException {
            return bytecode = new ByteArrayOutputStream();
        }

        public byte[] getByteCode(){
            return bytecode.toByteArray();
        }
    }
}
