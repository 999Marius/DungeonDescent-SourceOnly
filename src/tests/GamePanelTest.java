package tests;

import main.GamePanel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;

class GamePanelTest {
    private GamePanel gp;

    @BeforeEach
    public void setUp() {
        gp = new GamePanel();
    }

    //start of all game state transition tests
    @Test
    void testPlayToPauseTransition() {
        gp.currentState = GamePanel.GameState.PLAY;
        gp.keyH.escPressed = true;
        gp.update();

        assertEquals(GamePanel.GameState.PAUSE, gp.currentState);
    }

    @Test
    void testPauseToPlayTransition() {
        gp.currentState = GamePanel.GameState.PAUSE;
        gp.keyH.escPressed = true;
        gp.update();

        assertEquals(GamePanel.GameState.PLAY, gp.currentState);
    }

    @Test
    void testPlayToGameOverTransition() {
        gp.currentState = GamePanel.GameState.PLAY;
        gp.getPlayer().setHealth(0);
        gp.update();

        assertEquals(GamePanel.GameState.GAME_OVER, gp.currentState);
    }

    //this should fail
    @Test
    void testGameOverToPlayTransition() {
        gp.currentState = GamePanel.GameState.GAME_OVER;
        gp.keyH.escPressed = true;
        gp.update();

        assertEquals(GamePanel.GameState.GAME_OVER, gp.currentState);
    }

    @Test
    public void testStatePreservation() {
        gp.currentState = GamePanel.GameState.PLAY;
        for (int i = 0; i < 100; i++) {
            gp.update();
            assertEquals(GamePanel.GameState.PLAY, gp.currentState);
        }
    }

    @Test
    public void testStatePreservation2() {
        gp.currentState = GamePanel.GameState.PAUSE;
        for (int i = 0; i < 100; i++) {
            gp.update();
            assertEquals(GamePanel.GameState.PAUSE, gp.currentState);
        }
    }

    @Test
    public void testStatePreservation3() {
        gp.currentState = GamePanel.GameState.SAVES_MENU;
        for (int i = 0; i < 100; i++) {
            gp.update();
            assertEquals(GamePanel.GameState.SAVES_MENU, gp.currentState);
        }
    }

    @Test
    public void testEscKeyPressReleaseSequence(){
        //start in PLAY
        gp.currentState = GamePanel.GameState.PLAY;

        //press ESC
        gp.keyH.escPressed = true;
        gp.update();
        assertEquals(GamePanel.GameState.PAUSE, gp.currentState);

        //keep ESC pressed
        gp.update();
        assertEquals(GamePanel.GameState.PAUSE, gp.currentState);

        //release ESC
        gp.keyH.escPressed = false;
        gp.update();
        assertEquals(GamePanel.GameState.PAUSE, gp.currentState);

        //pess ESC again
        gp.keyH.escPressed = true;
        gp.update();
        assertEquals(GamePanel.GameState.PLAY, gp.currentState);
    }


    @Test
    public void testPlayerMovementInPlayState(){
        //start in PLAY
        gp.currentState = GamePanel.GameState.PLAY;


        int startY = gp.getPlayer().worldY;
        gp.keyH.upPressed = true;
        gp.update();
        gp.keyH.upPressed = false;
        assertTrue(startY > gp.getPlayer().worldY);

        //test DOWN
        gp.getPlayer().worldY = startY;
        gp.keyH.downPressed = true;
        gp.update();
        gp.keyH.downPressed = false;
        assertTrue(startY < gp.getPlayer().worldY);

        //test LEFT movement
        int startX = gp.getPlayer().worldX;
        gp.keyH.leftPressed = true;
        gp.update();
        gp.keyH.leftPressed = false;
        assertTrue(startX > gp.getPlayer().worldX);

        //test RIGHT
        gp.getPlayer().worldX = startX;
        gp.keyH.rightPressed = true;
        gp.update();
        gp.keyH.rightPressed = false;
        assertTrue(startX < gp.getPlayer().worldX);
    }

    @Test
    public void testPlayerMovementInPauseState() {
        //start in PLAY
        gp.currentState = GamePanel.GameState.PAUSE;

        int startY = gp.getPlayer().worldY;
        gp.keyH.upPressed = true;
        gp.update();
        gp.keyH.upPressed = false;
        assertEquals(startY, gp.getPlayer().worldY);

        //test DOWN
        gp.getPlayer().worldY = startY;
        gp.keyH.downPressed = true;
        gp.update();
        gp.keyH.downPressed = false;
        assertEquals(startY, gp.getPlayer().worldY);

        //test LEFT movement
        int startX = gp.getPlayer().worldX;
        gp.keyH.leftPressed = true;
        gp.update();
        gp.keyH.leftPressed = false;
        assertEquals(startX, gp.getPlayer().worldX);

        //test RIGHT
        gp.getPlayer().worldX = startX;
        gp.keyH.rightPressed = true;
        gp.update();
        gp.keyH.rightPressed = false;
        assertEquals(startX, gp.getPlayer().worldX);
    }

    @Test
    public void testStateTransitionDuringLevelLoad(){
        gp.currentState = GamePanel.GameState.PLAY;
        gp.loadNextLevel();
        assertEquals(GamePanel.GameState.PLAY, gp.currentState);
    }
    //end of all game state transition tests




    //startGame() tests
    @Test
    public void testStartGame() {
        assertNull(gp.gameThread);

        gp.startGame();

        assertNotNull(gp.gameThread);
        assertEquals(GamePanel.GameState.PLAY, gp.currentState);
    }

    //update() tests
    @Test
    public void testUpdate() {
        gp.transitionManager.startTransition();
        gp.update();
        assertTrue(gp.transitionManager.isTransitioning);
    }

    //loadNextLevel() tests
    @Test
    public void testLoadNextLevel() throws InterruptedException{
        int initialLevel = gp.mapManager.getCurrentMap().getLevelNumber();


        gp.transitionManager.startTransition();
        Thread.sleep(501);

        gp.loadNextLevel();

        assertEquals(initialLevel + 1, gp.mapManager.getCurrentMap().getLevelNumber());
    }

    //loadGame() tests
    @Test
    public void testLoadGame(){
        gp.loadGame(1);

        assertNotNull(gp.mapManager.getCurrentMap());
        assertNotNull(gp.getPlayer());
        assertNotNull(gp.enemyManager);

        assertNotNull(gp.collisionChecker);
    }


    //draw() tests
    @Test
    public void testDraw(){
        BufferedImage image = new BufferedImage(gp.screenWidth, gp.screenHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();

        try{
            gp.paintComponent(g2);
            assertTrue(true);
        } catch(Exception e){
            fail("Drawing should not throw exception");
        }
        g2.dispose();
    }


    //drawButtons() tests
    @Test
    public void testDrawButtons(){
        BufferedImage image = new BufferedImage(gp.screenWidth, gp.screenHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();

        gp.currentState = GamePanel.GameState.PAUSE;

        try{
            gp.paintComponent(g2);
            assertNotNull(gp.resumeButton);
            assertNotNull(gp.exitButton);
            assertNotNull(gp.savesButton);
            assertNotNull(gp.exitButton);
        } catch(Exception e){
            fail("Drawing should not throw exception");
        }
        g2.dispose();
    }

    //drawPauseScreen() tests
    @Test
    public void testDrawGameOver(){
        BufferedImage image = new BufferedImage(gp.screenWidth, gp.screenHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();

        try{
            gp.drawGameOverScreen(g2);
            assertTrue(true); // Drawing didn't crash
        } finally{
            g2.dispose();
        }
    }

    //drawLevelCompleteMessage() tests
    @Test
    public void testDrawLevelComplete(){
        BufferedImage image = new BufferedImage(gp.screenWidth, gp.screenHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();

        try{
            gp.drawLevelCompleteMessage(g2);
            assertTrue(true); // Drawing didn't crash
        } finally{
            g2.dispose();
        }
    }

    //drawSavesMenu() tests
    @Test
    public void testDrawSavesMenu(){
        BufferedImage image = new BufferedImage(gp.screenWidth, gp.screenHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();

        try{
            gp.drawSavesMenu(g2);
            assertTrue(true); // Drawing didn't crash
        } finally{
            g2.dispose();
        }
    }

}