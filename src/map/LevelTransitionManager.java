package map;


import java.awt.*;
public class LevelTransitionManager{
    private float fadeAlpha = 0;
    public boolean isTransitioning = false;
    private long fadeStartTime;
    private static final long FADE_DURATION = 1000;

    public void startTransition(){
        isTransitioning = true;
        fadeStartTime = System.currentTimeMillis();
    }

    public void update(){
        if(!isTransitioning) return;

        long elapsed = System.currentTimeMillis() - fadeStartTime;
        if(elapsed >= FADE_DURATION){
            isTransitioning = false;
            fadeAlpha = 0;
            return;
        }

        if(elapsed <= FADE_DURATION/2){
            fadeAlpha = elapsed/(FADE_DURATION/2f);  // Fade in
        } else{
            fadeAlpha = 1 - (elapsed - FADE_DURATION/2f)/(FADE_DURATION/2f);  // Fade out
        }
    }

    public void draw(Graphics2D g2, int screenWidth, int screenHeight){
        if(!isTransitioning) return;
        g2.setColor(new Color(0, 0, 0, (int)(fadeAlpha * 255)));
        g2.fillRect(0, 0, screenWidth, screenHeight);
    }

    public boolean shouldLoadLevel(){
        return System.currentTimeMillis() - fadeStartTime >= FADE_DURATION/2;
    }
}
