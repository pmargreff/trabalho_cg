public class Point {

    public Point(int x, int y) {
        this.x_ = x;
        this.y_ = y;
    }

    public Point() {
        this.x_ = 0;
        this.y_ = 0;
    }

    public int get_x() {
      return this.x_;
    }

    public int get_y() {
      return this.y_;
    }

    public void set_x(int n) {
      this.x_ = n;
    }

    public void set_y(int n) {
      this.y_ = n;
    }

    private int x_;
    private int y_;

}