package org.executequery.io;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ByteArrayFileWriter {

    public void write(File file, byte[] data) throws IOException {
        
        OutputStream outputStream = null;
        BufferedOutputStream bufferedOutputStream = null;

        try {
        
            outputStream = new FileOutputStream(file);
            bufferedOutputStream = new BufferedOutputStream(outputStream); 

            bufferedOutputStream.write(data);
            bufferedOutputStream.flush();
            
        } finally {

            try {
            
                if (bufferedOutputStream != null) {
                    
                    bufferedOutputStream.close();
                }

                if (outputStream != null) {
                    
                    outputStream.close();
                }
                
            } catch (IOException e) {}

        }

    }
    
}
