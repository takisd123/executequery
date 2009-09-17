/*
 * SourceHeader.java
 *
 * Created on 3 January 2007, 17:36
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

//package testing;


import java.io.*;


public class SourceHeader {

    private int counter = 0;

    private static String REMOVE = "/** [ REMOVE FOR SRC ] */";
    private static String baseDir;
    private static String headerFile;
    private String headerText;
    
    public SourceHeader() {
        headerText = readFile(new File(headerFile)).append("\n").toString();
        startFind(new File(baseDir));
        
        System.out.println("\nApplied header to " + counter + " files.\n\n");
        
        System.exit(0);
    }

    private void startFind(File file) {
        
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                
                if (files[i].isDirectory()) {
                    startFind(files[i]);
                }                
                else {
                    doReplace(files[i]);
                }
            }
        }
        else {
            doReplace(file);
        }

    }

    private StringBuilder fileText;

    private void doReplace(File file) {
        
        if (fileText == null) {
            fileText = new StringBuilder();
        } else {
            fileText.setLength(0);
        }

        String fileName = file.getName();
        if (fileName.endsWith(".java")) {
            fileText = readFile(file);
            
            int index1 = fileText.indexOf("package");
            if (index1 != -1) {
                fileText.delete(0, index1);
            }

            fileText.insert(0, headerText.replaceAll("<filename>",fileName));
            writeFile(file, fileText.toString());
            System.out.println("write file: " + fileName);
            
            /*
            int index1 = fileText.indexOf("/*");

            if (index1 != -1) {
                int index2 = fileText.indexOf("* /");
                if (index2 != -1) {
                    String cpHeader = headerText.replaceAll("<filename>",fileName);
                    fileText.replace(index1, index2+2, cpHeader);
                    writeFile(file, fileText.toString());
                }
                else {
                    System.out.println("end not found in " + fileName);
                }
            }
            else {
                System.out.println("not found in " + fileName);
            } */
        }
        
    }
    
    private void writeFile(File file, String text) {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(file, false), true);
            writer.println(text);
            writer.close();
            counter++;
        }        
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private StringBuilder readFile(File file) {
        StringBuilder sb = null;
        String text = null;

        try {
            FileInputStream input = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            sb = new StringBuilder();

            char newLine = '\n';
            boolean addLine = true;
            while((text = reader.readLine()) != null) {
                sb.append(text).append(newLine);
                /*
                if (text.indexOf(REMOVE) == -1 && addLine) {
                    sb.append(text).append(newLine);
                } else if (!addLine) { // last was not added so reset
                    addLine = true;
                } else {
                    addLine = false;
                }
                */
            }

            reader.close();
            input.close();
            return sb;
        }        
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            sb = null;
            text = null;
            return null;
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }        
    }

    public static void main(String[] args) {
        baseDir = args[0];
        headerFile = args[1];
        new SourceHeader();
    }

}
