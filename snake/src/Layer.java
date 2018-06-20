import javax.swing.*;
import java.awt.*;
import java.util.List;

public class Layer extends JPanel {
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(java.awt.Color.GREEN); 
        g.fillRect(Room.game.getWidth() * 10, 0, 10, (Room.game.getWidth() * 10) + 10);  
        g.fillRect(0, Room.game.getHeight() * 10, (Room.game.getHeight() * 10) + 10, 10); 

        g.fillRect(Room.game.getMouse().getX()*10, Room.game.getMouse().getY()*10, 10, 10);

        List<SnakeSection> getsection = Room.game.getSnake().getSections(); 
        for (int i = 0; i < getsection.size(); i++) {
            g.fillRect(getsection.get(i).getX()*10, getsection.get(i).getY()*10, 10, 10); 
        }
    }
}
