package org.isobit.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.channels.FileChannel;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.isobit.app.X;
//import org.isobit.data.proxy.HTTPProxy;

public class XFile extends File {

    public static void write(InputStream uploadedInputStream, File file) {

        try {
            int read = 0;
            byte[] bytes = new byte[1024];
            OutputStream out = new FileOutputStream(file);
            while ((read = uploadedInputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
            out.close();
        } catch (IOException e) {

            e.printStackTrace();
        }

    }

    /**
     * Get current dir (without resolving symlinks), and normalize path by
     * calling FastPathResolver.resolve().
     */
    public static String getCurrDirPathStr() {
        String currDirPathStr = "";
        try {
            // The result is moved to currDirPathStr after each step, so we can provide fine-grained debug info
            // and a best guess at the path, if the current dir doesn't exist (#109), or something goes wrong
            // while trying to get the current dir path.
            Path currDirPath = Paths.get("").toAbsolutePath();
            currDirPathStr = currDirPath.toString();
            currDirPath = currDirPath.normalize();
            currDirPathStr = currDirPath.toString();
            currDirPath = currDirPath.toRealPath(LinkOption.NOFOLLOW_LINKS);
            currDirPathStr = currDirPath.toString();
            //currDirPathStr = FastPathResolver.resolve(currDirPathStr);
        } catch (final IOException e) {
            throw new RuntimeException("Could not resolve current directory: " + currDirPathStr, e);
        }
        return currDirPathStr;
    }

    /**
     * Read all the bytes in an InputStream.
     */
    public static byte[] readAllBytes(final InputStream inputStream, final long fileSize/* , final LogNode log*/)
            throws IOException {
        // Java arrays can only currently have 32-bit indices
        if (fileSize > Integer.MAX_VALUE
                // ZipEntry#getSize() can wrap around to negative for files larger than 2GB
                // http://bugs.java.com/bugdatabase/view_bug.do?bug_id=6916399
                || (fileSize < 0
                // ZipEntry#getSize() can return -1 for unknown size 
                && fileSize != -1L)) {
            throw new IOException("File larger that 2GB, cannot read contents into a Java array");
        }

        // We can't always trust the fileSize, unfortunately, so we just use it as a hint
        final ByteArrayOutputStream baos = new ByteArrayOutputStream(fileSize <= 0 ? 16384 : (int) fileSize);

        // N.B. there is a better solution for this in Java 9, byte[] bytes = inputStream.readAllBytes()
        final byte[] buf = new byte[4096];
        int totBytesRead = 0;
        for (int bytesRead; (bytesRead = inputStream.read(buf)) != -1;) {
            baos.write(buf, 0, bytesRead);
            totBytesRead += bytesRead;
        }
        if (totBytesRead != fileSize) {
            /*if (log != null) {
                log.log("File length expected to be " + fileSize + " bytes, but read " + totBytesRead + " bytes");
            }*/
        }
        return baos.toByteArray();
    }

    /**
     * Returns true if path has a .class extension, ignoring case.
     */
    public static boolean isClassfile(final String path) {
        final int len = path.length();
        return len > 6 && path.regionMatches(true, len - 6, ".class", 0, 6);
    }

    public static File getFile(String name) {
        return getFile(name, true);
    }

    public static File getFile(String name, boolean create) {
        String fileSeparator = System.getProperty("file.separator");
        return getFile(
                new File(
                        //                        System.getProperty("user.home") + fileSeparator + System.getProperty(X.USER_HOME_DIR) + fileSeparator + 

                        name),
                create, name.lastIndexOf(".") > name.lastIndexOf(fileSeparator));
    }

    public static File getFile(File file) {
        return getFile(file, true);
    }

    public static File getFile(File file, boolean create) {
        String fileSeparator = System.getProperty("file.separator");
        String name = file.getName();
        int dot = name.lastIndexOf(".");
//        try{
//        X.log("file.getCanonicalPath=" + (file = file.getCanonicalFile()));
//        }catch(Exception e){throw new RuntimeException(e);}
        return getFile(file, create, /*dot>0&&*/ dot > name.lastIndexOf(fileSeparator));
    }

    public static File getFile(File file, boolean create, boolean isFile) {
        if (!create && !file.exists()) {
            return null;
        }
        if (!isFile) {
            file.mkdirs();
        } else {
            try {
                File parent = new File(file.getParent());
                if (!parent.exists()) {
                    parent.mkdirs();
                }
                if (!file.isFile()) {
                    //file.delete();
                }
                if (!file.exists()) {
                    file.createNewFile();
                }
            } catch (IOException exception) {
                throw new RuntimeException(exception);
////                X.log(exception);
            }
        }
        if(create&&!file.exists()) throw new RuntimeException("No se pudo crear '"+file+"'");
        return file;
    }

    private XFile() {
        super("");
    }

    public static boolean fileExists(String path) {
        File file = new File(path);
        return file.exists();
    }

    public static short saveResource(String FROMFile, File f) {
        InputStream stream = X.class.getResourceAsStream(FROMFile);
        if (stream == null) {
            throw new RuntimeException("Error obteniendo recurso [" + FROMFile + "]");
        }
        OutputStream resStreamOut;
        int readBytes;
        byte[] buffer = new byte[4096];
        try {
            if (!f.getParentFile().exists()) {
                if (!f.getParentFile().mkdirs());
                return 1;
            }
            resStreamOut = new FileOutputStream(f);
            while ((readBytes = stream.read(buffer)) != -1) { //LINE 80
                resStreamOut.write(buffer, 0, readBytes);
            }
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return 0;
    }

    public static void writeFile(String path, String text) throws IOException {
        writeFile(new File(path), text, false);
    }

    public static void writeFile(String path, String text, boolean append) throws IOException {
        writeFile(new File(path), text, append);
    }

    public static void writeFile(File file, String text) throws IOException {
        writeFile(file, text, false);
    }

    public static void writeFile(File file, String text, boolean append) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file, append), true)) {
            writer.print(text);
        }
    }

    public static String loadFile(File file) throws IOException {
        return loadFile(file, true);
    }

    public static String loadFile(String path) throws IOException {
        return loadFile(new File(path), true);
    }

    public static String loadFile(String path, boolean escapeLines) throws IOException {
        return loadFile(new File(path), escapeLines);
    }

    public static String loadFile(File file, boolean escapeLines) throws IOException {
        FileReader fileReader = null;
        BufferedReader reader = null;
        try {
            fileReader = new FileReader(file);
            reader = new BufferedReader(fileReader);
            String value = reader.readLine();
            StringBuilder sb = new StringBuilder();
            if (value != null) {
                sb.append(value);
                while ((value = reader.readLine()) != null) {
                    if (escapeLines) {
                        sb.append('\n');
                    }
                    sb.append(value);
                }
                return sb.toString();
            }
            return null;
        } finally {
            if (reader != null) {
                reader.close();
            }
            if (fileReader != null) {
                fileReader.close();
            }
        }
    }

    public static String loadResource(String path) throws IOException {
        InputStream input = null;

        try {
            ClassLoader cl = XFile.class.getClassLoader();

            if (cl != null) {
                input = cl.getResourceAsStream(path);
            } else {
                input = ClassLoader.getSystemResourceAsStream(path);
            }

            int i = 0;
            StringBuffer buf = new StringBuffer();

            while ((i = input.read()) != -1) {
                buf.append((char) i);
            }

            return buf.toString();
        } finally {
            if (input != null) {
                input.close();
            }
        }
    }

    public static Properties loadProperties(String path, Properties defaults) throws IOException {
        return loadProperties(new File(path), defaults);
    }

    public static Properties loadProperties(File file, Properties defaults) throws IOException {
        InputStream input = null;

        try {
            Properties properties = null;

            if (defaults != null) {
                properties = new Properties(defaults);
            } else {
                properties = new Properties();
            }

            input = new FileInputStream(file);
            properties.load(input);
            return properties;
        } finally {
            if (input != null) {
                input.close();
            }
        }

    }

    public static Properties loadProperties(String path) throws IOException {
        return loadProperties(new File(path), null);
    }

    public static Properties loadProperties(File file) throws IOException {
        return loadProperties(file, null);
    }

    public static void storeProperties(String path,
            Properties properties, String header) throws IOException {
        storeProperties(new File(path), properties, header);
    }

    public static void storeProperties(File file,
            Properties properties, String header) throws IOException {
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(file);
            properties.store(output, header);
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }

    public static Object readObject(String path) throws IOException {
        return readObject(new File(path));
    }

    public static Object readObject(File file) throws IOException {
        FileInputStream fileIn = null;
        BufferedInputStream buffIn = null;
        ObjectInputStream obIn = null;

        try {
            fileIn = new FileInputStream(file);
            buffIn = new BufferedInputStream(fileIn);
            obIn = new ObjectInputStream(buffIn);
            return obIn.readObject();
        } catch (ClassNotFoundException cExc) {
            cExc.printStackTrace();
            return null;
        } finally {
            try {
                if (obIn != null) {
                    obIn.close();
                }
                if (buffIn != null) {
                    buffIn.close();
                }
                if (fileIn != null) {
                    fileIn.close();
                }
            } catch (IOException e) {
            }
        }
    }

    public static void saveObject(String filename, Object object, Object... extra) throws IOException {
        writeObject(XFile.getFile(filename), object, extra);
    }

    public static File writeObject(File filename, Object object, Object... extra) throws IOException {
//        X.log("filenamefilenamefilename=="+filename);
        FileOutputStream fileOut = null;
        BufferedOutputStream bufferedOut = null;
        ObjectOutputStream obOut = null;
        try {
            fileOut = new FileOutputStream(filename);
            bufferedOut = new BufferedOutputStream(fileOut);
            obOut = new ObjectOutputStream(bufferedOut);
            obOut.writeObject(object);
        } finally {
            try {
                if (bufferedOut != null) {
                    bufferedOut.close();
                }
                if (obOut != null) {
                    obOut.close();
                }
                if (fileOut != null) {
                    fileOut.close();
                }
            } catch (IOException e) {
            }
        }
        return filename;
    }

    private static int defaultBufferSize = 32768;

    public static void copyResource(String from, String to) throws IOException {
        copyResource(from, new File(to));
    }

    public static void copyResource(String from, File to) throws IOException {
        InputStream in = null;
        OutputStream out = null;
        try {
            ClassLoader cl = XFile.class.getClassLoader();
            if (cl != null) {
                in = cl.getResourceAsStream(from);
            } else {
                in = ClassLoader.getSystemResourceAsStream(from);
            }
            out = new FileOutputStream(to);
            byte[] buffer = new byte[defaultBufferSize];
            while (true) {
                synchronized (buffer) {
                    int amountRead = in.read(buffer);
                    if (amountRead == -1) {
                        break;
                    }
                    out.write(buffer, 0, amountRead);
                }
            }
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }

    public static String simplifyFileName(String input) {
        return Normalizer.normalize(input.trim(), Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "").replaceAll(" +", " ").replaceAll("[*]", "");
    }

    private Object[] info;

    @Override
    public long length() {
        return Long.parseLong(info[2].toString());
    }

    public String getKey() {
        return info[1].toString();
    }

    @Override
    public String getName() {
        return info[0].toString();
    }

    private XFile(Object[] info) {
        super(info[0].toString());
        this.info = info;
    }

    public static void main(String[] args) {
        System.out.println(XFile.simplifyFileName("complejo-arqueológico-chavin-de-huántar"));
        //simplifyFileName
        //System.out.println("SSDX2000.XS.DDDD.XLS".matches(".*2000.*[.]XLS"));
    }

    public static File copy(File from, String to) throws IOException {
        if (to.startsWith("http:")) {
            /*HTTPProxy h = (HTTPProxy) HTTPProxy.getInstance();
            try {
                String s = h.getServerHost();
                if (!s.endsWith("/")) {
                    s += "/";
                }
                Object o = h.upload(s + "upload?mode=1", from, null);
                X.log(X.gson.toJson(((Map) o).get("files")));
                return new XFile(((List<Object[]>) ((Map) o).get("files")).get(0));
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new RuntimeException("Fail uploading file!", ex);
            }*/
            throw new RuntimeException("HTTPProxy must be implemented!");
        } else {
            return XFile.copy(from, new File(to));
        }
    }

    public static File move(File origen, File dstFile) {
        try {
            System.out.println("move from '" + origen.getCanonicalPath() + "' to '" + dstFile.getCanonicalPath() + "'");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        File parent = dstFile.getParentFile();
        if (!parent.exists() && !parent.mkdirs()) {
            throw new RuntimeException(parent + " es un destino no valido o no puede ser creado.");
        }
        if (dstFile.exists()) {
            String name = dstFile.getName();
            int p = name.lastIndexOf(".");
            String ext = "";
            if (p > -1) {
                ext = name.substring(p);
                name = name.substring(0, p);
            }
            int c = 1;
            File f;
            while ((f = new File(dstFile.getParent(), name + "(" + (c++) + ")" + ext)).exists());
            dstFile = f;
        }
        if (origen.renameTo(dstFile)) {
            return dstFile;
        } else {
            throw new RuntimeException("No es posible mover " + origen + " a la carpeta " + dstFile);
        }
    }

    public interface CopyListener {

        boolean copy(File from);

        void close(File from);

    }

    private static CopyListener defaultCopyListener = new CopyListener() {
        @Override
        public boolean copy(File f) {
            return true;
        }

        @Override
        public void close(File from) {
        }

    };

    public static File copy(File from, File to) throws IOException {
        return copy(from, to, defaultCopyListener);
    }

    public static File copy(File from, File to, CopyListener cl) throws IOException {
        if (from.isDirectory()) {
            for (File f : from.listFiles()) {
                if (cl.copy(f)) {
                    copy(f, XFile.getFile(new File(to, f.getName()), true, f.isFile()), cl);
                }
            }
            cl.close(from);
            return to;
        }
        if (to.isDirectory()) {
            to = new File(to, from.getName());
        }
        if (!to.exists()) {
            
            to = getFile(to);
        }
        Exception e = null;
        FileInputStream in = null;
        FileOutputStream out = null;
        try {
            FileChannel fin = (in = new FileInputStream(from)).getChannel();
            FileChannel fout = (out = new FileOutputStream(to)).getChannel();
            fin.transferTo(0L, from.length(), fout);
        } catch (Exception ex) {
            X.log("No se pudo usar FileChannel");
            e = ex;
        } finally {
            close(in, out);
        }
        if (e != null) {
            try {
                in = new FileInputStream(from);
                out = new FileOutputStream(to);
                byte[] buffer = new byte[defaultBufferSize];
                while (true) {
                    synchronized (buffer) {
                        int amountRead = in.read(buffer);
                        if (amountRead == -1) {
                            break;
                        }
                        out.write(buffer, 0, amountRead);
                    }
                }
//            while((bytesRead=in.read(buffer))!=-1)out.write(buffer,0,bytesRead);
            } catch (Exception ex) {
                e = ex;
            } finally {
                close(in, out);
            }
        }
        if (e != null) {
            throw new RuntimeException(e);
        }
        return to;
    }

    private static void close(FileInputStream in, FileOutputStream out) {
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
            }
        }
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
            }
        }
    }

    public static void copy(String from, String to) throws IOException {
        XFile.copy(new File(from), new File(to));
    }

    public static String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        return name.substring(lastIndexOf + 1).toLowerCase();
    }

    public static boolean isFilenameValid(String file) {
        File f = new File(file);
        try {
            f.getCanonicalPath();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}
