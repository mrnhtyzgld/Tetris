import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class Logic extends Thread {
    /*
    Tetris
    up is length-1 down is 0
    left is 0 right is length-1
    map holds tetrominoes' name inside if not empty, otherwise null


   */

    public Map map;
    public Block block;
    private int cellCountX, cellCountY;

    private static HashMap<Character, boolean[][]> tetrominoes = new HashMap<>();
    // { tetrominoName, tetrominoShape }

    static {
        tetrominoes.put('I', new boolean[][]{
                {true, true, true, true}
        });
        tetrominoes.put('J', new boolean[][]{
                {true, false, false},
                {true, true, true}
        });
        tetrominoes.put('L', new boolean[][]{
                {false, false, true},
                {true, true, true}
        });
        tetrominoes.put('O', new boolean[][]{
                {true, true},
                {true, true}
        });
        tetrominoes.put('S', new boolean[][]{
                {false, true, true},
                {true, true, false}
        });
        tetrominoes.put('Z', new boolean[][]{
                {true, true, false},
                {false, true, true}
        });
        tetrominoes.put('T', new boolean[][]{
                {true, true, true},
                {false, true, false}
        });
    }


    class Map {
        public Character[][] map;
        public Map(int x, int y) {
            map = new Character[y][x];
        }
        public void placeNewBlock(Block block) {
            int startX = block.x;
            int startY = block.y;
            boolean[][] tetrominoShape = block.tetrominoShape;
            Character tetrominoName = block.tetrominoName;
            for (int a = 0; a < tetrominoShape.length; a++) {
                for (int b = 0; b < tetrominoShape[a].length; b++) {
                    if (map.length > startY - a
                            && map[a].length > b + startX
                            && tetrominoShape[a][b]) {
                        map[startY - a][startX + b] = tetrominoName;
                    }
                }
            }
        }
        public void print() {
            for (int a = map.length-1; a >= 0; a--) {
                for (int b = 0; b < map[a].length; b++) {
                    System.out.print(map[a][b] + "\t");
                }
                System.out.println();
            }
        }


        /*
        this method controls if current block object can go any further
            -cases-
            cant go down if reached bottom
            cant go down if on top of a block
            will start outside of array
         */
        public boolean isBelowEmpty(Block block) {
            int startX = block.x;
            int startY = block.y;
            boolean[][] tetrominoShape = block.tetrominoShape;

            for (int a = 0; a < tetrominoShape.length; a++) {
                int currY = startY - a;

                for (int b = 0; b < tetrominoShape[a].length; b++) {
                    int currX = startX + b;
                    if (map.length >= currY) {
                        if (currY == 0) return false; // at the bottom

                        if (map[currY-1][currX] != null) return false; // on top of a piece

                    }
                }
            }
            return true;
        }


        public void clearFullRows() {
            for (int a = 0; a < map.length; a++) {
                clearFullRow(a);
            }
        }
        private void clearFullRow(int row) {
                boolean isFull = true;
                for (int b = 0; b < map[row].length; b++) {
                    if (map[row][b] == null) isFull = false;
                }
                if (isFull) scrollRows(row);
        }

        private void scrollRows(int deletedRow) { // deleting a row and then sliding all the above one down
            for (int a = deletedRow + 1; a < map.length; a++) {
                for (int b = 0; b < map[a].length; b++) {
                    map[a-1][b] = map[a][b];
                }
            }
            for (int b = 0; b < map[map.length-1].length; b++) {
                map[map.length-1][b] = null;
            }
        }
        public void createNewBlock() {
            this.placeNewBlock(block);
            Character randTet = tetrominoes.keySet().toArray(new Character[tetrominoes.size()])[new Random().nextInt(tetrominoes.size())];
            block = new Block(tetrominoes.get(randTet), randTet);

        }

    }
    class Block {
        boolean[][] tetrominoShape;
        Character tetrominoName;
        private int x, y;

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public Block(boolean[][] tetrominoShape, Character tetrominoName) {
            this.tetrominoShape = tetrominoShape;
            this.tetrominoName = tetrominoName;
            x = (cellCountX + tetrominoShape[0].length)/2;  // TODO
            y = cellCountY + tetrominoShape.length;  // TODO
        }
        public void turnRight(int times) {
            for (int a = 0; a < times; a++)  {
                turnMatriceRight();
                updateCoordinates();
            }
        }
        private void turnMatriceRight() {
            boolean[][] newMatrice = new boolean[tetrominoShape[0].length][tetrominoShape.length];
            for (int a = 0; a < newMatrice.length; a++) {
                for (int b = 0; b < newMatrice[a].length; b++) {
                    newMatrice[a][b] = tetrominoShape[b][newMatrice.length-1-a];
                }
            }
            tetrominoShape = newMatrice;
        }
        public void slideRight() {
            if (block.x + block.tetrominoShape[0].length  < cellCountX) {
                Block a = new Block(block.tetrominoShape, block.tetrominoName);
                a.x = block.getX() + 1;
                a.y = block.getY() + 1;
                if (map.isBelowEmpty(a)) {
                    a.y--;
                    block = a;
                }
            }

        }
        public void slideLeft() {
            if (block.x > 0) {
                Block a = new Block(block.tetrominoShape, block.tetrominoName);
                a.x = block.getX() - 1;
                a.y = block.getY() + 1;
                if (map.isBelowEmpty(a)) {
                    a.y--;
                    block = a;
                }
            }
        }

        public void goDown() {
            if (map.isBelowEmpty(block)) y--;
        }

        public void goDownFull() {
            while (true) {
                goDown();
                if (!map.isBelowEmpty(this)) break;
            }
        }
        /*
        shape turns around the first point, we need to make it turn around somewhere in center
         */
        private void updateCoordinates() {
            // TODO
        }

        public void goUp() {
            y++;
        }
    }
    public Logic(int cellCountX, int cellCountY) {
        this.cellCountX = cellCountX;
        this.cellCountY = cellCountY;
        Character randTet = tetrominoes.keySet().toArray(new Character[tetrominoes.size()])[new Random().nextInt(tetrominoes.size())];
        block = new Block(tetrominoes.get(randTet), randTet);
        map = new Map(cellCountX, cellCountY);

    }


}
