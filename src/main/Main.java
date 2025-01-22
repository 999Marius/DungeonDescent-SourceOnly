package main;//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {

        //SaveManager.getInstance().deleteAllSaves();

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setTitle("Dungeon's Decent");

        GamePanel gamePanel = new GamePanel();
        frame.add(gamePanel);
        frame.pack();

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        gamePanel.startGame();

    }
}