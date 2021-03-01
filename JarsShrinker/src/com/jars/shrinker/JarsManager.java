package com.jars.shrinker;


import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;


public class JarsManager {
	
	
	private Map<String, ZipFile> classToZip = new HashMap<String, ZipFile>();
	
	private ZipOutputStream outputStream;
	
	
	public JarsManager(String dirPath, String outputPath) throws IOException {
		
		File dir = new File(dirPath);
		
		File[] jars = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".jar");
			}
		});
		
		for (int i = 0; i < jars.length; i++) {
			indexClasses(jars[i]);
		}
		
		outputStream = new ZipOutputStream(new FileOutputStream(outputPath));
	}
	
	
    public void indexClasses(File zip) throws IOException {
        ZipFile zipFile = new ZipFile(zip);
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while(entries.hasMoreElements()){
            ZipEntry entry = entries.nextElement();
            if (entry.getName().endsWith(".class")) {
            	classToZip.put(entry.getName(), zipFile);
            }
        }
    }
    
    
    public void extractClasses(Set<Class> classes) throws IOException {
    	for (Class clazz : classes) {
    		String name = clazz.getName().replace(".", "/") + ".class";
    		if (classToZip.containsKey(name)) {
    			ZipFile zip = classToZip.get(name);
    			ZipEntry entry = zip.getEntry(name);
    			BufferedInputStream bis = new BufferedInputStream(zip.getInputStream(entry));
    			byte[] bytes = readAllBytes(bis);
    			outputStream.putNextEntry(new ZipEntry(name));
    			outputStream.write(bytes, 0, bytes.length);
                outputStream.closeEntry();
    		} else {
    			System.out.println("Skiped class (not found in jars) : " + name);
    		}
    	}
    }
    
    
    public void close() throws IOException {
    	outputStream.close();
    	for (String key : classToZip.keySet()) {
    		ZipFile zip = classToZip.get(key);
    		zip.close();
    	}
    }
    
    
    public static byte[] readAllBytes(InputStream inputStream) throws IOException {
        final int bufLen = 1024;
        byte[] buf = new byte[bufLen];
        int readLen;
        IOException exception = null;

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            while ((readLen = inputStream.read(buf, 0, bufLen)) != -1)
                outputStream.write(buf, 0, readLen);

            return outputStream.toByteArray();
            
        } catch (IOException e) {
            exception = e;
            throw e;
        } finally {
            if (exception == null) inputStream.close();
            else try {
                inputStream.close();
            } catch (IOException e) {
                exception.addSuppressed(e);
            }
        }
    }
}
