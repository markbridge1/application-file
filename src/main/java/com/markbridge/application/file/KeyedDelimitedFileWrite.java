/*
 * Mark Bridge (markbridge.com), San Francisco CA 94102, j2eewebtier@gmail.com 
 * Copyright (c) 2012 Mark Bridge All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * 
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.markbridge.application.file;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Header row has keys, default delimiter is comma
 * Expect data file continuous - to not have empty lines till reach end of data
 * Not thread safe
 * @author bridgem
 */
public class KeyedDelimitedFileWrite {
    
    private Path filePath;
    private String delimiter = ",";
    Writer out;
    
    Set<String>keyCheck = new HashSet<String>();
    
    public static final String outLock = "keyedDelimitedFile";
    
    public KeyedDelimitedFileWrite(Path path, String delimiter) throws FileNotFoundException {
        this.filePath = path;
        if(delimiter != null) {
            this.delimiter = delimiter;
        }
        out = new OutputStreamWriter(new FileOutputStream(filePath.toFile(), true), Charset.forName("utf-8"));
    }
    
    public KeyedDelimitedFileWrite(String fsFilePath, String delimiter) throws FileNotFoundException {
        this(Paths.get(fsFilePath), delimiter);
    }
    
    public boolean writeLine(String key, ArrayList<String> elements) {
        boolean retVal = false;
        
        try { 
            if(keyCheck.add(key)) {
                out.append(key).append(delimiter);
                for(String s : elements) {
                    out.append(s).append(delimiter);
                    retVal = true;
                }
                out.append(System.lineSeparator());
                retVal = true;
            }
        } catch (IOException ex) {
        }
        
        return retVal;
    }
    
    public boolean rawWriteElement(String element) {
        boolean retVal = false;
        try {
            out.append(element).append(delimiter);;
            retVal = true;
        } catch (IOException ex) {
        }
        return retVal;
    }
    
    public boolean rawEndLine() {
        boolean retVal = false;
        try {
            out.append(System.lineSeparator());
            retVal = true;
        } catch (IOException ex) {
        }
        return retVal;
    }
    
    public void close() {
        try {
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(KeyedDelimitedFileWrite.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        out.close();
    }
    
    public static void main(String[] args) throws FileNotFoundException {
        
        KeyedDelimitedFileWrite writeout = new KeyedDelimitedFileWrite(Paths.get(".", "log"), "\t");
        
        ArrayList<String> list = new ArrayList<String>();
        
        for(int i = 0; i < 10; i++) {
            list.add("" + 10);
            writeout.rawWriteElement("" + i);
        }
        
        writeout.rawEndLine();
        
        writeout.writeLine("1", list);
        
        writeout.close();
    }
}
