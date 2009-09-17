/*
 * SunJpegImageWriter.java
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

package org.executequery.imageio;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.executequery.ApplicationException;
import org.executequery.log.Log;

import com.sun.image.codec.jpeg.ImageFormatException;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

public class SunJpegImageWriter implements ImageWriter {

    public void write(ImageWriterInfo imageWriterInfo) {

        if (!(imageWriterInfo instanceof JpegImageWriterInfo)) {

            throw new IllegalArgumentException(
                    "Image writer info must be an instance of JpegImageWriterInfo");
        }

        JpegImageWriterInfo jpegImageWriterInfo = (JpegImageWriterInfo) imageWriterInfo;
        
        FileOutputStream fos = null;
        
        try {

            fos = new FileOutputStream(jpegImageWriterInfo.getWriteToFile());
            
            BufferedOutputStream bfos = new BufferedOutputStream(fos);
            JPEGImageEncoder jpegEncoder = JPEGCodec.createJPEGEncoder(bfos);
            JPEGEncodeParam jpegParam = jpegEncoder.getDefaultJPEGEncodeParam(
                    jpegImageWriterInfo.getBufferedImage());

            float quality = (jpegImageWriterInfo.getImageQuality() *10) / 100.0f;
            jpegParam.setQuality(quality, false);
            jpegEncoder.setJPEGEncodeParam(jpegParam);
            jpegEncoder.encode(jpegImageWriterInfo.getBufferedImage());

            bfos.flush();
            bfos.close();
            
        } catch (FileNotFoundException e) {

            handleException(e);
            
        } catch (ImageFormatException e) {

            handleException(e);
            
        } catch (IOException e) {

            handleException(e);
            
        } finally {
            
            if (fos != null) {
                
                try {
                    fos.close();
                } catch (IOException e) {}

            }
            
        }
        
    }

    private void handleException(Throwable e) {

        if (Log.isDebugEnabled()) {

            Log.error("Error saving in SVG image format", e);
        }

        throw new ApplicationException(e);
    }

}

