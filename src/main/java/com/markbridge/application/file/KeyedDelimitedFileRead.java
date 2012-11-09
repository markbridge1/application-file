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

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * Header row has keys, default delimiter is comma
 * Expect data file continuous - to not have empty lines till reach end of data
 * Not thread safe
 * @author bridgem
 */
public class KeyedDelimitedFileRead {
    
    private ArrayList<String> keyRow = new ArrayList<>();
    private Scanner fileScanner;
    private String delimiter = ",";
    
    public KeyedDelimitedFileRead() {
    }
    
    public KeyedDelimitedFileRead(String fsFilePath, String delimiter) {
        this(Paths.get(fsFilePath), delimiter);
    }
    
    public KeyedDelimitedFileRead(Path path, String delimiter) {
        if(delimiter != null) {
            this.delimiter = delimiter;
        }
        
        try {
            fileScanner = new Scanner(path);
            Scanner lineScanner = getLineScanner();
            if(lineScanner != null) {
                Set<String>keySet = new HashSet<>();
                while(lineScanner.hasNext()) {
                    String key = lineScanner.next();
                    if(keySet.add(key)) {
                        keyRow.add(key);
                    } else {
                        throw new RuntimeException("duplicate key found in file header");
                    }
                }
            }
        } catch(IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    public HashMap<String, String> nextLine() {
        HashMap<String, String> retVal = null;
        
        Scanner lineScanner = getLineScanner();
        if(lineScanner != null) {
            retVal = new HashMap<>();
            int keyIndex = 0;
            while(lineScanner.hasNext()) {
                retVal.put(keyRow.get(keyIndex), lineScanner.next());
                keyIndex++;
            }
        }
        return retVal;
    }
    
    public String[] getKeyRow() {
        return keyRow.toArray(new String[0]);
    }
    
    private Scanner getLineScanner() {
        Scanner retVal = null;
        try { 
            if(fileScanner.hasNextLine()) {
                retVal = new Scanner(fileScanner.nextLine());
                retVal.useDelimiter(delimiter);
                if(! retVal.hasNext()) {
                    retVal = null;
                }
            } else {
                fileScanner.close();
            }
        } catch(Exception ex) {
            System.out.println(ex.getMessage());
        }
        return retVal;
    }
}
