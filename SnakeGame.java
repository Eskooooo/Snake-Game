import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

public class SnakeGame extends JFrame {
    private GamePanel gamePanel;
    private GameLogic gameLogic;

    public SnakeGame() {
        gameLogic = new GameLogic();
        gamePanel = new GamePanel();
        gamePanel.setGameLogic(gameLogic);

        setTitle("Snake Game");
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.BLACK);
        getContentPane().add(gamePanel);
        pack();
        setLocationRelativeTo(null);
        addKeyListener(new GameKeyListener());

        setVisible(true);
        gameLogic.startGame();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SnakeGame::new);
    }

    private class GameKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            if (gameLogic.running) {
                if (key == KeyEvent.VK_UP && gameLogic.direction != 'D') {
                    gameLogic.direction = 'U';
                } else if (key == KeyEvent.VK_DOWN && gameLogic.direction != 'U') {
                    gameLogic.direction = 'D';
                } else if (key == KeyEvent.VK_LEFT && gameLogic.direction != 'R') {
                    gameLogic.direction = 'L';
                } else if (key == KeyEvent.VK_RIGHT && gameLogic.direction != 'L') {
                    gameLogic.direction = 'R';
                }
            } else {
                if (key == KeyEvent.VK_ENTER) {
                    gameLogic.startGame();
                }
            }
        }
    }

    private class GameActionListener implements ActionListener {
        private GamePanel gamePanel;

        public void setGamePanel(GamePanel gamePanel) {
            this.gamePanel = gamePanel;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (gameLogic.running) {
                gameLogic.move();
                gameLogic.checkApple();
                gameLogic.checkCollisions();
            }
            gamePanel.repaint();
        }
    }

    private class GamePanel extends JPanel {
        private GameLogic gameLogic;

        public GamePanel() {
            setPreferredSize(new Dimension(GameLogic.WIDTH, GameLogic.HEIGHT));
            setBackground(Color.BLACK); // ตั้งค่าพื้นหลังเป็นสีดำ
        }

        public void setGameLogic(GameLogic gameLogic) {
            this.gameLogic = gameLogic;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            draw(g);
        }

        public void draw(Graphics g) {
            if (gameLogic.running) {
                // Draw the apple
                g.setColor(Color.RED);
                g.fillOval(gameLogic.appleX, gameLogic.appleY, gameLogic.UNIT_SIZE, gameLogic.UNIT_SIZE);

                // Draw the snake
                for (int i = 0; i < gameLogic.bodyParts; i++) {
                    if (i == 0) {
                        g.setColor(Color.GREEN);
                        g.fillRect(gameLogic.x[i], gameLogic.y[i], gameLogic.UNIT_SIZE, gameLogic.UNIT_SIZE);
                    } else {
                        g.setColor(new Color(45, 180, 0));
                        g.fillRect(gameLogic.x[i], gameLogic.y[i], gameLogic.UNIT_SIZE, gameLogic.UNIT_SIZE);
                    }
                }

                // Draw the score
                g.setColor(Color.WHITE);
                g.setFont(new Font("Ink Free", Font.BOLD, 40));
                FontMetrics metrics = getFontMetrics(g.getFont());
                g.drawString("Score: " + gameLogic.applesEaten,
                        (GameLogic.WIDTH - metrics.stringWidth("Score: " + gameLogic.applesEaten)) / 2,
                        g.getFont().getSize());
            } else {
                gameOver(g);
            }
        }

        public void gameOver(Graphics g) {
            // Game Over text
            g.setColor(Color.RED);
            g.setFont(new Font("Ink Free", Font.BOLD, 75));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Game Over", (GameLogic.WIDTH - metrics.stringWidth("Game Over")) / 2, GameLogic.HEIGHT / 2);

            // Final score
            g.setColor(Color.WHITE);
            g.setFont(new Font("Ink Free", Font.BOLD, 40));
            FontMetrics scoreMetrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + gameLogic.applesEaten,
                    (GameLogic.WIDTH - scoreMetrics.stringWidth("Score: " + gameLogic.applesEaten)) / 2,
                    GameLogic.HEIGHT / 2 + 50);

            // Restart instructions
            g.setColor(Color.WHITE);
            g.setFont(new Font("Ink Free", Font.BOLD, 30));
            FontMetrics restartMetrics = getFontMetrics(g.getFont());
            g.drawString("Press Enter to Restart",
                    (GameLogic.WIDTH - restartMetrics.stringWidth("Press Enter to Restart")) / 2,
                    GameLogic.HEIGHT / 2 + 100);
        }
    }

    class GameLogic {
        public static final int WIDTH = 600;
        public static final int HEIGHT = 600;
        public static final int UNIT_SIZE = 25;
        public static final int GAME_UNITS = (WIDTH * HEIGHT) / (UNIT_SIZE * UNIT_SIZE);

        public int applesEaten;
        public int[] x;
        public int[] y;
        public int bodyParts;
        public int appleX;
        public int appleY;
        public char direction;
        public boolean running;
        public Timer timer;

        public GameLogic() {
            x = new int[GAME_UNITS];
            y = new int[GAME_UNITS];
            direction = 'R';
            running = false;
            timer = new Timer(100, new GameActionListener());
        }

        public void startGame() {
            restartGame();
            running = true;
            timer.start();
        }

        public void restartGame() {
            applesEaten = 0;
            bodyParts = 6;
            direction = 'R';
            x = new int[GAME_UNITS];
            y = new int[GAME_UNITS];
            running = false;
        }

        public void newApple() {
            appleX = (int) (Math.random() * (WIDTH / UNIT_SIZE)) * UNIT_SIZE;
            appleY = (int) (Math.random() * (HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
        }

        public void move() {
            for (int i = bodyParts; i > 0; i--) {
                x[i] = x[i - 1];
                y[i] = y[i - 1];
            }

            switch (direction) {
                case 'U':
                    y[0] -= UNIT_SIZE;
                    break;
                case 'D':
                    y[0] += UNIT_SIZE;
                    break;
                case 'L':
                    x[0] -= UNIT_SIZE;
                    break;
                case 'R':
                    x[0] += UNIT_SIZE;
                    break;
            }
        }

        public void checkApple() {
            if (x[0] == appleX && y[0] == appleY) {
                applesEaten++;
                bodyParts++;
                newApple();
            }
        }

        public void checkCollisions() {
            // Check if head collides with body
            for (int i = bodyParts; i > 0; i--) {
                if (x[0] == x[i] && y[0] == y[i]) {
                    running = false;
                    break;
                }
            }

            // Check if head touches left border
            if (x[0] < 0) {
                running = false;
            }

            // Check if head touches right border
            if (x[0] >= WIDTH) {
                running = false;
            }

            // Check if head touches top border
            if (y[0] < 0) {
                running = false;
            }

            // Check if head touches bottom border
            if (y[0] >= HEIGHT) {
                running = false;
            }

            if (!running) {
                timer.stop();
            }
        }

        private class GameActionListener implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (running) {
                    move();
                    checkApple();
                    checkCollisions();
                }
                gamePanel.repaint();
            }
        }
    }
}
