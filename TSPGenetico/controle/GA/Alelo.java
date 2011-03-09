package controle.GA;

public class Alelo {
	private int numero;
	private float pos_x;
	private float pos_y;
	
	public Alelo(int numero,float pos_x,float pos_y){
		this.numero=numero;
		this.pos_x=pos_x;
		this.pos_y=pos_y;
	}
	
	public int getNumero() {
		return numero;
	}
	public void setNumero(int numero) {
		this.numero = numero;
	}
	public float getPos_x() {
		return pos_x;
	}
	public void setPos_x(float pos_x) {
		this.pos_x = pos_x;
	}
	public float getPos_y() {
		return pos_y;
	}
	public void setPos_y(float pos_y) {
		this.pos_y = pos_y;
	}
}
