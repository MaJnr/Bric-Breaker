package sample;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class BricksController {
    private final Model model;

    public BricksController(Model model) {
        this.model = model;
    }

    public Group fillWithBricks() {
        //todo: use class brick with all properties

        List nodeList = new ArrayList<Node>();
        // lines
        for (int i = 0; i < 20; i++) {
            // columns
            for (int j = 0; j < 5; j++) {
                Rectangle r = new Rectangle(i*80, j*20,80, 20);
                r.setStroke(Color.BLACK);
                r.setFill(Color.rgb(52,91,168));
                nodeList.add(r);
            }
        }
//        Circle c = new Circle(400, 800, 10, Color.GRAY);
//        c.setStroke(Color.BLACK);
//        nodeList.add(c);
        return new Group(nodeList);
    }

    
}
