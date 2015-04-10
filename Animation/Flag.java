package Animation;

public class Flag {

	public Flag(boolean b) {
		this.value = b;
	}

	public void set_value(boolean b) {
		this.value = b;
	}

	public boolean get_value() {
		return this.value;
	}

	private boolean value;
}