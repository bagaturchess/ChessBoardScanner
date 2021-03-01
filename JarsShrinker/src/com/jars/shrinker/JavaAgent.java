package com.jars.shrinker;


import java.lang.instrument.*;


public class JavaAgent {
	
	
    private static Instrumentation inst;
    
    
    public static Instrumentation getInstrumentation() {
    	return inst;
    }
    
    
    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println(inst.getClass() + ": " + inst);
        JavaAgent.inst = inst;
    }
}
