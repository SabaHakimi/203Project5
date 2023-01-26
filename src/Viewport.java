public final class Viewport {
    private int row;
    private int col;
    private int numRows;
    private int numCols;

    public Viewport(int numRows, int numCols) {
        this.numRows = numRows;
        this.numCols = numCols;
    }

    public int getRow() {
        return this.row;
    }

    public void setRow(int r) {
        this.row = r;
    }

    public int getCol() {
        return this.col;
    }

    public void setCol(int c) {
        this.col = c;
    }

    public int getNumRows() {
        return this.numRows;
    }

    public int getNumCols() {
        return this.numCols;
    }
}
