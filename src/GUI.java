import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

public class GUI extends JPanel {
    Logic logic;
    Timer timer;
    public final int cellPx_X = 20;
    public final int cellPx_Y = 20;
    public final int cellCount_X = 20;
    public final int cellCount_Y = 40;
    public final int horizontalInset = 15; // in pixel up down
    public final int verticalInset = 15; // in pixel right left

    public final Color backgroundColor = Color.GRAY;
    public final Color gridColor = Color.BLACK;

    private static final HashMap<Character, Color> tetrominoesColor = new HashMap<>();
    // { tetrominoName, tetrominoShape }
  
    static {
        tetrominoesColor.put('I', Color.RED);
        tetrominoesColor.put('J', Color.ORANGE);
        tetrominoesColor.put('L', Color.GREEN);
        tetrominoesColor.put('O', Color.BLUE);
        tetrominoesColor.put('S', Color.CYAN);
        tetrominoesColor.put('Z', Color.YELLOW);
        tetrominoesColor.put('T', Color.WHITE);
    }



    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int a = 0; a < logic.block.tetrominoShape.length; a++) {
            for (int b = 0; b < logic.block.tetrominoShape[a].length; b++) {
                if (logic.block.tetrominoShape[a][b]){
                    g.setColor(tetrominoesColor.get(logic.block.tetrominoName));
                    g.fillRect(verticalInset + (logic.block.getX() + b) * cellPx_X, horizontalInset + (logic.map.map.length - 1 - logic.block.getY() + a) * cellPx_Y, cellPx_X, cellPx_Y);
                }
            }
        }

        for (int a = 0; a < logic.map.map.length; a++) {
            for (int b = 0; b < logic.map.map[a].length; b++) {
                if (logic.map.map[a][b] != null) {
                    g.setColor(tetrominoesColor.get(logic.map.map[a][b]));
                    g.fillRect(verticalInset + b * cellPx_X, horizontalInset + (logic.map.map.length - 1 - a) * cellPx_Y, cellPx_X, cellPx_Y);
                }
            }
        }

        g.setColor(gridColor);
        for (int a = 0; a < cellCount_Y; a++) {
            for (int b = 0; b < cellCount_X; b++) {
                g.drawRect(verticalInset + b * cellPx_X, horizontalInset + a * cellPx_Y, cellPx_X, cellPx_Y);
            }
        }





    }

    public GUI() {
        JFrame myFrame = new JFrame();
        this.logic = new Logic(cellCount_X, cellCount_Y);
        this.timer = new Timer();




        myFrame.setSize(cellCount_X * cellPx_X + 2 * verticalInset + 15, cellCount_Y * cellPx_Y + 2 * horizontalInset + 40);
        myFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        myFrame.addKeyListener(new keyMovement());

        myFrame.setBackground(backgroundColor);
        myFrame.add(this);
        setBackground(backgroundColor);


        myFrame.setVisible(true);

    }

    class Timer extends Thread {
        public final int START_POINT = 400;
        int timer = START_POINT;
        boolean canPaint = false;
        ReentrantLock lock = new ReentrantLock();
        public Timer() {
            start();
            new Thread(() -> {
                while (true) {
                    try {
                        sleep(0);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    if (timer<=0) {
                        lock.lock();

                        if (logic.map.isBelowEmpty(logic.block)) {
                            logic.block.goDown();
                        } else {
                            logic.map.createNewBlock();
                            logic.map.clearFullRows();
                        }

                        repaint();
                        timer = START_POINT;

                        lock.unlock();
                    }
                }


            }).start();
        }
        @Override
        public void run() {
            while (true) {
                System.out.println(timer);
                try {
                    sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                lock.lock();
                if (timer > 0) timer--;
                lock.unlock();
            }
        }
        public void reset() {
            lock.lock();
            timer = 0;
            lock.unlock();
        }
    }

    class keyMovement implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == 37) { // left
                logic.block.slideLeft();
                repaint();
            }
            if (e.getKeyCode() == 39) { // right
                logic.block.slideRight();
                repaint();
            }
            if (e.getKeyCode() == 38) { // up
                logic.block.turnRight(1);
                repaint();
            }
            if (e.getKeyCode() == 40) { // down
                logic.block.goDown();
                timer.reset();
                repaint();
            }
            if (e.getKeyCode() == 32) { // space
                logic.block.goDownFull();
                timer.reset();
                repaint();
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }
    }


    public static void main(String[] args) {
        new GUI();
    }
}
