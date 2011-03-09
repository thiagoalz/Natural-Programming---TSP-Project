package buscaLocal;

public class CruzamentoDeLinhas {

	public static final int INTERSECTION    =  1;
	public static final int NO_INTERSECTION =  0;
	public static final int COINCIDENT      = -1;
	public static final int PARALLEL        = -2;


	public static int cruzamento(double a1x, double a1y, double a2x,
			double a2y, double b1x, double b1y, double b2x, double b2y)
	{
		double ua_t = (b2x-b1x)*(a1y-b1y)-(b2y-b1y)*(a1x-b1x);
		double ub_t = (a2x-a1x)*(a1y-b1y)-(a2y-a1y)*(a1x-b1x);
		double u_b  = (b2y-b1y)*(a2x-a1x)-(b2x-b1x)*(a2y-a1y);

		if ( u_b != 0 ) {
			double ua = ua_t / u_b;
			double ub = ub_t / u_b;

			if ( 0 <= ua && ua <= 1 && 0 <= ub && ub <= 1 ) {
				return INTERSECTION;
			} else {
				return NO_INTERSECTION;
			}
		} else {
			return ( ua_t == 0 || ub_t == 0 ? COINCIDENT : PARALLEL );
		}
	}

	public static void main(String[] args){
		long time1 = System.currentTimeMillis();
		float f[];

		for(int  j=0; j< 50000;j++) {
			for(long i=0;i<99*50;i++) {
				CruzamentoDeLinhas.cruzamento(0, 0, i, 8, 4, i, 7, i);
			}
		}

		System.out.println(System.currentTimeMillis()-time1);
	}
}