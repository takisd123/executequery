/*
 * FileUtils.java
 *
 * Copyright (C) 2002-2009 Takis Diakoumis
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.underworldlabs.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.UUID;

/**
 * File access utilities.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1460 $
 * @date     $Date: 2009-01-25 11:06:46 +1100 (Sun, 25 Jan 2009) $
 */
public class FileUtils {
    
    private FileUtils() {}

    public static String randomTempFilePath() {
        return System.getProperty("java.io.tmpdir") + 
            System.getProperty("file.separator") +  UUID.randomUUID().toString();
    }
    
    public static boolean fileExists(String path) {
        File file = new File(path);
        return file.exists();
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
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileWriter(file, append), true);
            writer.println(text);
            writer.flush();
        }
        finally {
            if (writer != null) {
                writer.close();
            }
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

            String value = null;
            StringBuilder sb = new StringBuilder();

            while ((value = reader.readLine()) != null) {
                sb.append(value);

                if (escapeLines) {
                    sb.append('\n');
                }

            }

            return sb.toString();
        }
        finally {
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
        BufferedReader reader = null;

        try {

            ClassLoader cl = FileUtils.class.getClassLoader();
            
            if (cl != null) {
                input = cl.getResourceAsStream(path);
            }
            else {
                input = ClassLoader.getSystemResourceAsStream(path);
            }

            reader = new BufferedReader(new InputStreamReader(input));

            String line = null;
            StringBuilder buf = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                buf.append(line);
                buf.append("\n");
            }

            return buf.toString();
        }
        finally {
            if (reader != null) {
                reader.close();
            }
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
            }
            else {
                properties = new Properties();
            }

            input = new FileInputStream(file);
            properties.load(input);
            return properties;
        }
        finally {
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
        }
        finally {
            if (output != null) {
                output.close();
            }
        }
    }
    
    public static Properties loadPropertiesResource(String path) throws IOException {
        InputStream input = null;
        
        try {
            ClassLoader cl = FileUtils.class.getClassLoader();            

            if (cl != null) {
                input = cl.getResourceAsStream(path);
            }
            else {
                input = ClassLoader.getSystemResourceAsStream(path);
            }

            Properties properties = new Properties();
            properties.load(input);
            return properties;
        }
        finally {
            if (input != null) {
                input.close();
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
        }
        catch (ClassNotFoundException cExc) {
            cExc.printStackTrace();
            return null;
        }
        finally {
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
            } catch (IOException e) {}
        }
    }
    
    public static void writeObject(Object object, String path) throws IOException {
        
        FileOutputStream fileOut = null;
        BufferedOutputStream bufferedOut = null;
        ObjectOutputStream objectOut = null;

        try {

            fileOut = new FileOutputStream(path);
            bufferedOut = new BufferedOutputStream(fileOut);
            objectOut = new ObjectOutputStream(bufferedOut);
            objectOut.writeObject(object);
            objectOut.flush();

        } finally {

            try {
                if (objectOut != null) {
                    objectOut.close();
                }
                if (bufferedOut != null) {
                    bufferedOut.close();
                }
                if (fileOut != null) {
                    fileOut.close();
                }
            } catch (IOException e) {}

        }

    }

    /** default buffer read size */
    private static final int DEFAULT_BUFFER_SIZE = 32768;
    
    /**
     * Copies the file at the specified from path to the 
     * specified to path.
     *
     * @param from  - the from path
     * @param to  - the to path
     * @throws IOException
     */
    public static void copyResource(String from, String to) throws IOException {
        InputStream in = null;
        OutputStream out = null;
        
        try {
            ClassLoader cl = FileUtils.class.getClassLoader();
            
            if (cl != null) {
                in = cl.getResourceAsStream(from);
            }
            else {
                in = ClassLoader.getSystemResourceAsStream(from);
            }

            out = new FileOutputStream(to);

            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            while (true) {
                synchronized(buffer) {
                    int amountRead = in.read(buffer);
                    if (amountRead == -1) {
                        break;
                    }
                    out.write(buffer, 0, amountRead);
                }
            }
            
            out.flush();

        }
        finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }            
        }
    }

    public static void copyFile(String from, String to) throws IOException {
        InputStream in = null;
        OutputStream out = null;
        
        try {
            in = new FileInputStream(from);
            out = new FileOutputStream(to);

            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            while (true) {
                synchronized(buffer) {
                    int amountRead = in.read(buffer);
                    if (amountRead == -1) {
                        break;
                    }
                    out.write(buffer, 0, amountRead);
                }
            }

            out.flush();
            
        }
        finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }            
        }
    }

}





