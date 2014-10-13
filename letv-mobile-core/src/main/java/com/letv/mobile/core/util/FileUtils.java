/**
 * 
 */
package com.letv.mobile.core.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;


/**
 * @author  
 *
 */
public abstract class FileUtils {
    public static void createFile(File fout, String content) throws IOException{
        File parent = fout.getParentFile();
        parent.mkdirs();
        if (!parent.isDirectory())
            throw new IOException("Unable to create directory " + parent.getPath());
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter(fout));
            out.write(content);
        } finally {
            if (out != null)
                try {
                    out.close();
                } catch (IOException ex1) {
                }
        }
    }

    public static void copyFile(File inF, File outF) throws IOException{
        checkFileIsValid(inF, "Source");
        if (outF == null)
            throw new IllegalArgumentException("Destination file should not be null");
        if (outF.isFile())
            delete(outF);

        outF.getParentFile().mkdirs();
        FileInputStream in = null;
        FileOutputStream out = null;
        try {
            in = new FileInputStream(inF);
            out = new FileOutputStream(outF);
            copyStream(in, out);
        } finally {
            closeStreams(in, out);
        }
    }

    public static void copyStream(InputStream in, OutputStream out) throws IOException {
        if (in != null && out != null) {
            byte buffer[] = new byte[1024];
            int length = 0;
            while ((length = in.read(buffer)) > 0)
                out.write(buffer, 0, length);
        }
    }

    public static void copyReaders(Reader in, Writer out) throws IOException {
        if (in != null && out != null) {
            char buffer[] = new char[1024];
            int length = 0;
            while ((length = in.read(buffer)) > 0)
                out.write(buffer, 0, length);
        }
    }

    public static void closeStreams(InputStream in, OutputStream out) {
        if (in != null)
            try {
                in.close();
            } catch (IOException ex) {
            }
        if (out != null)
            try {
                out.close();
            } catch (IOException ex) {
            }
    }

    public static void delete(File current) throws IOException{
        if (current.isDirectory())
            for (File sub : current.listFiles())
                delete(sub);
        if (current.exists() && (!current.delete()))
            throw new IOException("Unable to remove " + current.getPath());
    }

    public static void copyInSync(File source, File target) throws IOException {
        if (target.exists() && source.isDirectory() != target.isDirectory())
            FileUtils.delete(target);

        if (source.isDirectory()) {
            if ((!target.exists()) && (!target.mkdirs()))
                throw new IOException("Unable to create directory " + target.getPath());
            for (File item : source.listFiles())
                copyInSync(item, new File(target, item.getName()));
        } else if (source.length() != target.length())
            copyFile(source, target);
    }

    public static void checkFileIsValid(File test, String type) throws IOException {
        if (test == null)
            throw new IllegalArgumentException(type + " file should not be null");
        if (!test.isFile())
            throw new IOException(type + " '" + test.getPath() + "' is not a file");
        if (!test.canRead())
            throw new IOException(type + " file '" + test.getPath() + "' is not readable");
    }

    public static String readFile(File input) throws IOException {
        if (input == null)
            throw new IOException("Input file not defined");
        StringWriter out = new StringWriter();
        Reader in = null;
        try {
            in = new InputStreamReader(new FileInputStream(input), "UTF-8");
            copyReaders(in, out);
            return out.toString();
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (IOException ex) {
                }
        }
    }

    public static void writeFile(File output, String data) throws IOException {
        if (output == null)
            throw new IllegalArgumentException("Input file not defined");
        StringReader in = new StringReader(data == null ? "" : data);
        Writer out = null;
        try {
            out = new OutputStreamWriter(new FileOutputStream(output), "UTF-8");
            copyReaders(in, out);
        } finally {
            if (out != null)
                try {
                    out.close();
                } catch (IOException ex) {
                }
        }
    }

}
