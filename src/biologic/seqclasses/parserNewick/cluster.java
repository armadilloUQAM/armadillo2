package biologic.seqclasses.parserNewick;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Group - Collective Intelligence
 * Contains some code adapted from the O'Reilly book:
 * Programming Collective Intelligence
 * Building Smart Web 2.0 Applications
 * By Toby Segaran
 * Publisher: O'Reilly Media
 * Released: August 2007
 * http://oreilly.com/catalog/9780596529321
 *
 * @author Etienne Lord
 */
public class cluster {

//    public static double Pearson(int[] i1, int[] i2) {
//        Vector<Double>v1=new Vector<Double>();
//        Vector<Double>v2=new Vector<Double>();
//        for (Integer i:i1) v1.add(i.doubleValue());
//        for (Integer i:i2) v2.add(i.doubleValue());
//        return Pearson(v1,v2);
//    }
//
//    public static double Pearson(Vector<Double> v1, Vector<Double> v2) {
//        //--sum
//        double sum1=sum(v1);
//        double sum2=sum(v2);
//
//        double sumSq1=sumSq(v1);
//        double sumSq2=sumSq(v2);
//
//        double pSum=sumPr(v1, v2);
//
//        double num=pSum-((sum1*sum2)/v1.size());
//        double den=Math.sqrt((sumSq1-(Math.pow(sum1, 2)/v1.size()))*(sumSq2-(Math.pow(sum2, 2)/v2.size())));
//
//        if (den==0) return 0;
//        return (num / den);
//    }
//
//    /**
//     * Return the Tanamoto distance between two set of int[]
//     * @param i1
//     * @param i2
//     * @return
//     */
//    public static double Tanamoto(int[] i1, int[] i2) {
//        Vector<Double>v1=new Vector<Double>();
//        Vector<Double>v2=new Vector<Double>();
//        for (Integer i:i1) v1.add(i.doubleValue());
//        for (Integer i:i2) v2.add(i.doubleValue());
//        return Tanamoto(v1,v2);
//    }
//
//    /**
//     * This return the tanimoto coefficient which is the raio of intersecting set
//     * @param v1
//     * @param v2
//     * @return
//     */
//    public static double Tanamoto(Vector v1, Vector v2) {
//        double c1=0.0;
//        double c2=0.0;
//        double shr=0.0;
//        //--Create a complete list
//       ArrayList list=new ArrayList();
//       list.addAll(v1);
//       list.addAll(v2);
//       for (Object o:list) {
//           if (v1.contains(o)) c1++;
//           if (v2.contains(o)) c2++;
//           if (v1.contains(o)&&v2.contains(o)) shr++;
//        }
//        return (shr/(c1+c2-shr));
//    }
//
//     /**
//     * This return the tanimoto coefficient which is the raio of intersecting set
//      * Note: in this case, presence or absence of the element is define by True or False
//     * @param v1
//     * @param v2
//     * @return
//     */
//    public static double TanamotoBoolean(Vector<Boolean> v1, Vector<Boolean> v2) {
//        double c1=0.0;
//        double c2=0.0;
//        double shr=0.0;
//        for (int i=0; i<v1.size();i++) {
//            if (v1.get(i)) c1++;
//            if (v2.get(i)) c2++;
//            if (v1.get(i)&&v2.get(i)) shr++;
//        }
//
//        return (shr/(c1+c2-shr));
//    }
//
//    ////////////////////////////////////////////////////////////////////////////
//    ///
//
//
//    public static int sum(Vector<Integer> v) {
//        int count=0;
//        for (int i:v) count+=i;
//        return count;
//    }
//
//    public static float sum(Vector<Float> v) {
//        float count=0;
//        for (float i:v) count+=i;
//        return count;
//    }
//
//    public static double sum(Vector<Double> v) {
//        double count=0;
//        for (double i:v) count+=i;
//        return count;
//    }
//
//    /**
//     * Sum of square
//     * @param v
//     * @return
//     */
//    public static int sumSq(Vector<Integer> v) {
//        int count=0;
//        for (int i:v) count+=Math.pow(i, 2);
//        return count;
//    }
//
//     /**
//     * Sum of square
//     * @param v
//     * @return
//     */
//    public static float sumSq(Vector<Float> v) {
//        float count=0;
//        for (float i:v) count+=Math.pow(i, 2);
//        return count;
//    }
//
//     /**
//     * Sum of square
//     * @param v
//     * @return
//     */
//    public static double sumSq(Vector<Double> v) {
//        double count=0;
//        for (double i:v) count+=Math.pow(i, 2);
//        return count;
//    }
//
//    /**
//     * Sum product
//     * @param v
//     * @return
//     */
//    public static int sumPr(Vector<Integer> v1, Vector<Integer> v2) {
//        int count=0;
//        int j=Math.min(v1.size(), v2.size());
//        for (int i=0;i<j;i++) count+=(v1.get(i)*v2.get(i));
//        return count;
//    }
//
//    /**
//     * Sum product
//     * @param v
//     * @return
//     */
//    public static float sumPr(Vector<Float> v1, Vector<Float> v2) {
//        float count=0;
//        float j=Math.min(v1.size(), v2.size());
//        for (int i=0;i<j;i++) count+=(v1.get(i)*v2.get(i));
//        return count;
//    }
//
//     /**
//     * Sum product
//     * @param v
//     * @return
//     */
//    public static double sumPr(Vector<Double> v1, Vector<Double> v2) {
//        double count=0;
//        double j=Math.min(v1.size(), v2.size());
//        for (int i=0;i<j;i++) count+=(v1.get(i)*v2.get(i));
//        return count;
//    }
//
//    public static void main(String[] argv) {
//        int[] i1={1,1,4,6};
//        int[] i2={1,1,5,6};
//        System.out.println(Pearson(i1,i2));
//        System.out.println(Tanamoto(i1,i2));
//    }
}
