package javachallenge.graphics;

public class test {
    boolean isRectangle(double x1, double y1,
                     double x2, double y2,
                     double x3, double y3,
                     double x4, double y4)
    {
      double cx,cy;
      double dd1,dd2,dd3,dd4;

      cx=(x1+x2+x3+x4)/4;
      cy=(y1+y2+y3+y4)/4;

      dd1=Math.pow(cx-x1,2)+Math.pow(cy-y1,2);
      dd2=Math.pow(cx-x2,2)+Math.pow(cy-y2,2);
      dd3=Math.pow(cx-x3,2)+Math.pow(cy-y3,2);
      dd4=Math.pow(cx-x4,2)+Math.pow(cy-y4,2);
      System.out.println(dd1 + "   " + dd2 + "  " + dd3 + " " + dd4);
      return dd1==dd2 && dd1==dd3 && dd1==dd4;
    }
    
    public static void main(String[] args) {
    	test fj = new test();
		System.out.println(fj.isRectangle(431, 358, 476, 322, 482, 334, 438, 359));
	}
}
