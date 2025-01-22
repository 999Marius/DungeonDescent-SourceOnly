package main;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MouseHandler extends MouseAdapter {
    private GamePanel gp;
    public MouseHandler(GamePanel gp) {
        this.gp = gp;
    }

    @Override
    public void mouseClicked(MouseEvent e){
        if(gp.currentState == GamePanel.GameState.PAUSE){
            checkButonClicks(e.getX(), e.getY());
        } else if(gp.currentState == GamePanel.GameState.SAVES_MENU){
            checkSaveMenuClicks(e.getX(), e.getY());
        } else if(gp.currentState == GamePanel.GameState.GAME_OVER){
            checkGameOverClicks(e.getX(), e.getY());
        }
    }

    private void checkSaveMenuClicks(int x, int y){
        for(int i = 0; i < gp.saveSlotButtons.size(); i++){
            if(gp.saveSlotButtons.get(i).contains(x, y)){
                List<SaveManager.SaveSlot> saves = SaveManager.getInstance().getAllSaveSlots();
                if (i < saves.size()) {
                    SaveManager.SaveSlot save = saves.get(i);
                    gp.loadGame(save.id);
                }
                gp.currentState = GamePanel.GameState.PLAY;
                return;
            }
        }

        if(y > gp.screenHeight - 50){
            gp.currentState = GamePanel.GameState.PAUSE;
        }
    }

    private void checkButonClicks(int mouseX, int mouseY){
        if(gp.resumeButton.contains(mouseX, mouseY)){
            gp.currentState = GamePanel.GameState.PLAY;
        }else if(gp.savesButton.contains(mouseX, mouseY)){
            gp.currentState = GamePanel.GameState.SAVES_MENU;
        }else if(gp.saveGameButton.contains(mouseX, mouseY)){
            createNewSave();
        }else if(gp.exitButton.contains(mouseX, mouseY)){
            System.exit(0);
        }
    }
    private void checkGameOverClicks(int mouseX, int mouseY){
        if(gp.restartButton.contains(mouseX, mouseY)){
            Main.main(null);  // Start new game
            ((JFrame)gp.getTopLevelAncestor()).dispose();
        } else if(gp.quitButton.contains(mouseX, mouseY)){
            System.exit(0);
        }
    }
    private void createNewSave(){
        String saveName = "Save " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        int slotId = SaveManager.getInstance().createSaveSlot(saveName);
        SaveManager.getInstance().saveGameState(slotId, gp);
    }
}
