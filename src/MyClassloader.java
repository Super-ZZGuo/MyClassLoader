import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MyClassloader extends ClassLoader{

    private byte[] getBytes(String fileName) throws IOException {
        File file = new File(fileName);
        long len = file.length();
        byte[] raw = new byte[(int)len];

        try(FileInputStream fin = new FileInputStream(file)) {

            //读取Class文件的全部二进制数据
            int read = fin.read(raw);
            if(read != len) {
                throw new IOException("读取二进制文件失败");
            }
            return raw;
        }
    }

    // 重写ClassLoader的findClass方法
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Class clazz = null;
        // 替换包路径 . 为 /
        String fileStub = name.replace(".","/");
        String classFileName = fileStub + ".class";
        File classFile = new File(classFileName);

        // 如果该Class文件存在，则系统负责将该文件转换为Class对象
        if(classFile.exists()) {
            try {
                // 将Class文件的二进制数据读入数组
                byte[] raw = getBytes(classFileName);
                // 调用defineClass方法将二进制数据转换为Class对象
                clazz = defineClass(name, raw, 0, raw.length);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(clazz == null) {
            throw new ClassNotFoundException(name);
        }
        return clazz;
    }
    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        String classPath = "Hello";
        MyClassloader myClassloader = new MyClassloader();
        Class<?> aClass = myClassloader.loadClass(classPath);
        Method main = aClass.getMethod("test", String.class);
        System.out.println(main);
        main.invoke(aClass.newInstance(),"Hello World!!!!");
    }
}
