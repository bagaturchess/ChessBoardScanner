package com.jars.shrinker;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;


public class ClassLoaderUtils {
	
	
	public static Set<Class> getLoadedClasses(ClassLoader myCL) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		
		Set<Class> childClasses = new HashSet<Class>();
	    while (myCL != null) {
	    	childClasses.addAll(list(myCL));
	        myCL = myCL.getParent();
	    }
	    
	    Set<Class> parentClasses = new HashSet<Class>();
	    for (Class clazz : childClasses) {
	    	Class parent = clazz.getSuperclass();
	    	while (parent != null) {
	    		parentClasses.add(parent);
	    		parent = parent.getSuperclass();
	    	}
	    }
	    
	    
	    Set<Class> allClasses = new HashSet<Class>();
	    allClasses.addAll(childClasses);
	    allClasses.addAll(parentClasses);
	    
	    return allClasses;
	}
    
	
	private static Vector list(ClassLoader CL)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Class CL_class = CL.getClass();
		while (CL_class != java.lang.ClassLoader.class) {
			CL_class = CL_class.getSuperclass();
		}
		java.lang.reflect.Field ClassLoader_classes_field = CL_class.getDeclaredField("classes");
		ClassLoader_classes_field.setAccessible(true);
		Vector classes = (Vector) ClassLoader_classes_field.get(CL);
		return classes;
	}


	public static Set<Class> enrichWithParents(Class[] allDirectClasses) {
	    
	    Set<Class> parentClasses = new HashSet<Class>();
	    for (Class clazz : allDirectClasses) {
	    	Class parent = clazz.getSuperclass();
	    	while (parent != null) {
	    		parentClasses.add(parent);
	    		parent = parent.getSuperclass();
	    	}
	    }
	    
	    
	    Set<Class> allClasses = new HashSet<Class>();
	    allClasses.addAll(Arrays.asList(allDirectClasses));
	    allClasses.addAll(parentClasses);
	    
	    return allClasses;
	}
}
