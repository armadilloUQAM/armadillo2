

package configuration;

/**
 * Code from : http://www.roseindia.net/javatutorials/determining_memory_usage_in_java.shtml
 * 
 * Determining Memory Usage in Java
 * 2001-08-28 The Java Specialists' Newsletter [Issue 029] - Determining Memory Usage in Java
 * Author: Dr. Heinz M. Kabutz
 * @author Etienne Lord
 */
public class MemoryTest {
    
          public long calculateMemoryUsage() {
            
            long mem0 = Runtime.getRuntime().totalMemory() -
              Runtime.getRuntime().freeMemory();
            long mem1 = Runtime.getRuntime().totalMemory() -
              Runtime.getRuntime().freeMemory();
           
            System.gc(); System.gc(); System.gc(); System.gc();
            System.gc(); System.gc(); System.gc(); System.gc();
            System.gc(); System.gc(); System.gc(); System.gc();
            System.gc(); System.gc(); System.gc(); System.gc();
            mem0 = Runtime.getRuntime().totalMemory() -
              Runtime.getRuntime().freeMemory();
            
            System.gc(); System.gc(); System.gc(); System.gc();
            System.gc(); System.gc(); System.gc(); System.gc();
            System.gc(); System.gc(); System.gc(); System.gc();
            System.gc(); System.gc(); System.gc(); System.gc();
            mem1 = Runtime.getRuntime().totalMemory() -
              Runtime.getRuntime().freeMemory();
            return mem1 - mem0;
          }
          
          public MemoryTest() {
              System.out.println(calculateMemoryUsage());
          }
    
}
