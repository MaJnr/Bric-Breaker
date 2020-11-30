package sample;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;


import java.awt.*;
import java.util.*;
import java.util.List;


public class Main extends Application implements EventHandler<KeyEvent> {

    private static final int STAGE_NB = 0;
    final int HEIGHT = 900;
    final int WIDTH = 1600;
    final int RADIUS = 10;

    //ultra important : redraws the window every determined time
    Timeline timeline;

    Group root;
    Scene scene;

    Circle c;

    //initial values
    boolean isInitialDirection = true;
    int x = WIDTH / 2;
    int y = HEIGHT - 100;
    boolean rightDirection = true;
    boolean upDirection = true;

    //ball speed (in pixels per frame)
    double xSpeed;
    double ySpeed;
    final double FRAME_DURATION = 5;

    //bricks list
    List<Brick> bricksList;
    private List<Node> nodeList;

    //bar properties
    Bar bar;
    private int barWidth = 200;
    private final int BAR_HEIGHT = 20;
    private int barSpeed = 5;
    Rectangle tmpBar;
    Rectangle r = new Rectangle();

    //key pressed flag :
    //0 -> none
    //1 -> right
    //2 -> left
    int keyPressedState = 0;
    boolean isShooting = false;

    //game over screen
    private boolean isGameOver = false;
    Label l;

    Stage primaryStage;

    //check if level is cleared or not
    private boolean levelCleared = false;
    private boolean isGameWaiting = true;

    //arrow start hint
    Line line;
    private boolean arrowDrawn = false;

    @Override
    public void start(Stage primaryStage) {
        root = setupGame(STAGE_NB);

        // animation loop
        timeline = new Timeline(
                new KeyFrame(Duration.ZERO, event -> refreshScene()),
                new KeyFrame(Duration.millis(FRAME_DURATION))
        );
        timeline.setCycleCount(Timeline.INDEFINITE);

        primaryStage.setTitle("Bric-Breaker");

        primaryStage.setScene(setupScene());
        primaryStage.setResizable(false);

        this.primaryStage = primaryStage;
        primaryStage.show();
        timeline.play();
    }

    private Scene setupScene() {
        scene = new Scene(root, WIDTH, HEIGHT);
        scene.setFill(Color.BLACK);

        //keyboard listener
        scene.setOnKeyPressed(this);
        scene.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.LEFT) {
                keyPressedState = 0;
            }
            if (event.getCode() == KeyCode.SPACE) {
                isShooting = false;
            }
        });
        drawArrow(true);
        return scene;
    }


    private void refreshScene() {
        gotoxy(x, y);


        //bar position
        switch (keyPressedState) {
            case 1:
                if (bar.getTopRight().x <= WIDTH) {
                    startTranslate(barSpeed);
                }
                if (isGameWaiting) {
                    x = bar.getTopRight().x - barWidth /2;
                    rightDirection = true;
                    drawArrow(true);
                } else if (arrowDrawn) {
                    root.getChildren().remove(line);
                    arrowDrawn = false;
                }
                break;
            case 2:
                if (bar.getTopLeft().x >= 0) {
                    startTranslate(-barSpeed);
                }
                if (isGameWaiting) {
                    x = bar.getTopLeft().x + barWidth /2;
                    rightDirection = false;
                    drawArrow(false);
                } else if (arrowDrawn) {
                    root.getChildren().remove(line);
                    arrowDrawn = false;
                }
                break;
            default:
                break;
        }
        if (isShooting) {
            //todo: fix shoot rate lmao
            System.out.println("SHOOT");
            isGameWaiting = false;
            if (arrowDrawn) {
                root.getChildren().remove(line);
                arrowDrawn = false;
            }
        }
        if (isGameWaiting) {
            return;
        }

            levelCleared = true;
            //state of the next frame
            for (int p = 0; p < bricksList.size(); p++) {
                if (bricksList.get(p) == null) {
                    continue;
                }
                //if the level is cleared this statement is never reached
                levelCleared = false;
       /*         //todo: check diagonal hits (ball currently considered as a square)
                //oriented square (diamond) test
                boolean diamondDelete = false;
                if (bricksList.get(p).getTopLeft().y <= y+RADIUS*(-Math.sqrt(2)/2) && bricksList.get(p).getTopLeft().x <= x+RADIUS*(Math.sqrt(2)/2) && !upDirection && rightDirection) {
                    if (bricksList.get(p).getBottomRight().y >= y+RADIUS*(-Math.sqrt(2)/2) && bricksList.get(p).getBottomRight().x >= x+RADIUS*(Math.sqrt(2)/2)) {
                        x-=xSpeed;
                        y-=ySpeed;
                        upDirection = true;
                        rightDirection = false;
                        diamondDelete = true;
                    }
                }
                if (bricksList.get(p).getTopRight().y <= y+RADIUS*(-Math.sqrt(2)/2) && bricksList.get(p).getTopRight().x >= x+RADIUS*(-Math.sqrt(2)/2) && !upDirection && !rightDirection) {
                    if (bricksList.get(p).getBottomLeft().y >= y+RADIUS*(-Math.sqrt(2)/2) && bricksList.get(p).getBottomLeft().x <= x+RADIUS*(-Math.sqrt(2)/2)) {
                        x+=xSpeed;
                        y-=ySpeed;
                        upDirection = true;
                        rightDirection = true;
                        diamondDelete = true;
                    }
                }
                if (bricksList.get(p).getBottomRight().y >= y+RADIUS*(Math.sqrt(2)/2) && bricksList.get(p).getBottomRight().x >= x+RADIUS*(-Math.sqrt(2)/2) && upDirection && !rightDirection) {
                    if (bricksList.get(p).getTopLeft().y <= y+RADIUS*(Math.sqrt(2)/2) && bricksList.get(p).getTopLeft().x <= x+RADIUS*(-Math.sqrt(2)/2)) {
                        x+=xSpeed;
                        y+=ySpeed;
                        upDirection = false;
                        rightDirection = true;
                        diamondDelete = true;
                    }
                }
                if (bricksList.get(p).getBottomLeft().y >= y+RADIUS*(Math.sqrt(2)/2) && bricksList.get(p).getBottomLeft().x <= x+RADIUS*(Math.sqrt(2)/2) && upDirection && rightDirection) {
                    if (bricksList.get(p).getTopRight().y <= y+RADIUS*(Math.sqrt(2)/2) && bricksList.get(p).getTopRight().x >= x+RADIUS*(Math.sqrt(2)/2)) {
                        x-=xSpeed;
                        y+=ySpeed;
                        upDirection = false;
                        rightDirection = false;
                        diamondDelete = true;
                    }
                }*/
          /*          if (diamondDelete) {
                        System.out.println("diamond delete");
                        root.getChildren().remove(nodeList.get(p));
//                    bricksList.remove(bricksList.get(p));
//                    nodeList.remove(nodeList.get(p));
                        bricksList.set(p, null);
                        nodeList.set(p, null);
                    } else {*/
                boolean squareDelete = false;
                if (bricksList.get(p).getBottomLeft().y >= y - RADIUS && bricksList.get(p).getTopLeft().y <= y - RADIUS && upDirection) {
                    if (bricksList.get(p).getBottomLeft().x <= x && bricksList.get(p).getBottomRight().x >= x) {
                        y += ySpeed;
                        upDirection = false;
                        squareDelete = true;
                    }
                }
                if (bricksList.get(p).getTopRight().y <= y + RADIUS && bricksList.get(p).getBottomRight().y >= y + RADIUS && !upDirection) {
                    if (bricksList.get(p).getTopLeft().x <= x && bricksList.get(p).getTopRight().x >= x) {
                        y -= ySpeed;
                        upDirection = true;
                        squareDelete = true;
                    }
                }
                if (bricksList.get(p).getTopLeft().x <= x + RADIUS && bricksList.get(p).getTopRight().x >= x + RADIUS && rightDirection) {
                    if (bricksList.get(p).getTopLeft().y <= y && bricksList.get(p).getBottomLeft().y >= y) {
                        x -= xSpeed;
                        rightDirection = false;
                        squareDelete = true;
                    }
                }
                if (bricksList.get(p).getTopRight().x >= x - RADIUS && bricksList.get(p).getTopLeft().x <= x + RADIUS && !rightDirection) {
                    if (bricksList.get(p).getTopRight().y <= y && bricksList.get(p).getBottomRight().y >= y) {
                        x += xSpeed;
                        rightDirection = true;
                        squareDelete = true;
                    }
                }


                if (squareDelete) {
                    //triggers drop effect if exists

                    if (bricksList.get(p).getDrop() != null) {
                        String effect = bricksList.get(p).getDrop().getEffect();
                        switch (effect) {
                            case "increaseBarSize":
                                barWidth += 100;
                                setupBar(barWidth);
                                break;
                            case "decreaseBarSize":
                                if (barWidth >= 100)
                                barWidth -= 100;
                                setupBar(barWidth);
                                break;
                            case "increaseBarSpeed":
                                barSpeed += 3;
                                break;
                            case "decreaseBarSpeed":
                                barSpeed -= 3;
                                break;
                            case "increaseBallSpeed":
                                ySpeed++;
                                break;
                            case "decreaseBallSpeed":
                                if (ySpeed > 1)
                                ySpeed--;
                                break;
                            default:
                                System.out.println("erreur: pas d'effet");
                                break;
                        }
                        System.out.println(effect);
                    }

                    root.getChildren().remove(nodeList.get(p));
                    bricksList.set(p, null);
                    nodeList.set(p, null);
                }
                //  }
            }

            if (levelCleared) {
                System.out.println("level terminé");
                drawGameOver();
                return;
            }

            //-----------------------LEVEL BORDERS--------------------
            //x position
            if (rightDirection) {
                if (x >= WIDTH - RADIUS + 10) {
                    x -= xSpeed;
                    rightDirection = false;
                } else {
                    x += xSpeed;
                }
            } else if (x <= RADIUS) {
                x += xSpeed;
                rightDirection = true;
            } else {
                x -= xSpeed;
            }

            //y position
            if (upDirection) {
                if (y <= RADIUS) {
                    y += ySpeed;
                    upDirection = false;
                } else {
                    y -= ySpeed;
                }
            } else if (y >= HEIGHT - RADIUS + 10) {
                //hit the bottom : game over
                //timeline.stop();
                drawGameOver();
                System.out.println("gameover");
                return;
                //y -= ySpeed;
                //upDirection = true;
            } else {
                y += ySpeed;
            }
            //----------------------------------------------------------

            //--------------------BAR BORDERS---------------------------
            if (y + RADIUS >= bar.getTopLeft().y && y + RADIUS <= bar.getBottomRight().y && !upDirection) {
                if (x + RADIUS >= bar.getTopLeft().x && x + RADIUS <= bar.getBottomRight().x) {
                    y -= ySpeed;
                    upDirection = true;
                    if (x + RADIUS >= bar.getTopLeft().x && x + RADIUS < bar.getTopLeft().x + (barWidth / 4)) {
                        //if hit on the quarter left
                        x -= xSpeed;
                        if (rightDirection && xSpeed > 0) {
                            xSpeed--;
                        } else {
                            xSpeed++;
                        }
                        rightDirection = false;
                    } else if (x + RADIUS <= bar.getTopRight().x && x + RADIUS > bar.getTopRight().x - (barWidth / 4)) {
                        //if hit on the quarter right
                        x += xSpeed;
                        if (!rightDirection && xSpeed > 0) {
                            xSpeed--;
                        } else {
                            xSpeed++;
                        }
                        rightDirection = true;
                    }

                }
            }
            if (x + RADIUS >= bar.getTopLeft().x && x + RADIUS <= bar.getBottomRight().x && rightDirection) {
                if (y + RADIUS >= bar.getTopLeft().y && y + RADIUS <= bar.getBottomRight().y) {
                    x -= xSpeed;
                    y -= ySpeed;
                    upDirection = true;
                    rightDirection = false;
                }
            }
            if (x + RADIUS <= bar.getTopRight().x && x + RADIUS >= bar.getBottomLeft().x && !rightDirection) {
                if (y + RADIUS >= bar.getTopLeft().y && y + RADIUS <= bar.getBottomRight().y) {
                    x += xSpeed;
                    y -= ySpeed;
                    upDirection = true;
                    rightDirection = true;
                }
            }
            //---------------------------------------------------------

       // System.out.println(xSpeed + " " + ySpeed);
    }

    private void drawArrow(boolean toTheRight) {
        if (arrowDrawn) {
            root.getChildren().remove(line);
            arrowDrawn = false;
        }
        if (toTheRight) {
            line = new Line(x, y,x + 30, y - 30);
        } else {
            line = new Line(x, y, x-30, y-30);
        }
        line.setStroke(Color.YELLOW);
        root.getChildren().add(line);
        arrowDrawn = true;
    }

    private void drawGameOver() {
        timeline.pause();

        //draw different messages depending if level is cleared or not
        if (!levelCleared) {
            l = new Label("GAME OVER");
            l.setTextFill(Color.RED);
            l.setLayoutY(HEIGHT / 2. - 100);
            l.setLayoutX(WIDTH / 2. - 270);
        } else {
            l = new Label("STAGE CLEARED");
            l.setTextFill(Color.GREEN);
            l.setLayoutY(HEIGHT / 2. - 100);
            l.setLayoutX(WIDTH / 2. - 350);
        }

        l.setStyle("-fx-font-family: 'OCR A Extended'; -fx-font-size: 100; -fx-font-weight: bold");

        root.getChildren().add(l);
        isGameOver = true;
        isGameWaiting = true;
    }

    public Group setupGame(int stageNumber) {
        //todo: use class brick with all properties

        xSpeed = 1;
        ySpeed = 1;
        barSpeed = 5;
        barWidth = 200;
        nodeList = new ArrayList<>();
        bricksList = new ArrayList<>();
        // lines
        if (stageNumber == 2) {
            for (int i = 0; i < 20; i++) {
                // columns
                for (int j = 0; j < 20; j++) {
                    if (j != 10) {
                        continue;
                    }

                    Rectangle r = new Rectangle(i * 80 + 3, j * 20 + 3, 80, 20);
                    r.setStroke(Color.BLACK);
                    r.setStrokeWidth(0.5);
                    r.setFill(Color.rgb(52, 91, 168));
                    nodeList.add(r);
                    Point topRight = new Point(i * 80 + 3 + 80, j * 20 + 3);
                    Point bottomRight = new Point(i * 80 + 3 + 80, j * 20 + 3 + 20);
                    Point bottomLeft = new Point(i * 80 + 3, j * 20 + 3 + 20);
                    Point topLeft = new Point(i * 80 + 3, j * 20 + 3);
                    bricksList.add(new Brick(topRight, bottomRight, bottomLeft, topLeft));
                }
            }
        } else {
            for (int i = 0; i < 20; i++) {
                // columns
                for (int j = 0; j < 20; j++) {
                    if ((i+j)%2 == 0) {
                        continue;
                    }

                    Rectangle r = new Rectangle(i * 80 + 3, j * 20 + 3, 80, 20);
                    r.setStroke(Color.BLACK);
                    r.setStrokeWidth(0.5);
                    r.setFill(Color.rgb(52, 91, 168));
                    nodeList.add(r);
                    Point topRight = new Point(i * 80 + 3 + 80, j * 20 + 3);
                    Point bottomRight = new Point(i * 80 + 3 + 80, j * 20 + 3 + 20);
                    Point bottomLeft = new Point(i * 80 + 3, j * 20 + 3 + 20);
                    Point topLeft = new Point(i * 80 + 3, j * 20 + 3);
                    bricksList.add(new Brick(topRight, bottomRight, bottomLeft, topLeft));
                }
            }
        }
        //stage cleared test
        //bricksList.clear();

        //creation of a circle
        c = new Circle(600, 800, RADIUS, Color.WHITE);
        c.setCache(true);
        c.setCacheHint(CacheHint.SPEED);

        //first add bar then ball (might be multiples)
        setupBar(barWidth);
        nodeList.add(r);
        nodeList.add(c);

        return new Group(nodeList);
    }

    public void setupBar(int barWidth) {
        //r = new Rectangle((WIDTH / 2.) - (barWidth / 2.), HEIGHT - 60, barWidth, BAR_HEIGHT);

        if (isGameWaiting) {
            r.setWidth(barWidth);
            r.setHeight(BAR_HEIGHT);
            r.setFill(Color.GREEN);
            r.setId("bar");
            r.setX((WIDTH / 2.) - (barWidth / 2.));
            r.setY(HEIGHT - 60);
            Point topRight = new Point((WIDTH / 2) + (barWidth / 2), HEIGHT - 60);
            Point bottomRight = new Point((WIDTH / 2) + (barWidth / 2), HEIGHT - 60 + BAR_HEIGHT);
            Point bottomLeft = new Point((WIDTH / 2) - (barWidth / 2), HEIGHT - 60 + BAR_HEIGHT);
            Point topLeft = new Point((WIDTH / 2) - (barWidth / 2), HEIGHT - 60);
            bar = new Bar(topRight, bottomRight, bottomLeft, topLeft);
        } else {
            r.setWidth(barWidth);
            bar.setTopRight(new Point((WIDTH / 2) + (barWidth / 2), HEIGHT - 60));
            bar.setBottomRight(new Point((WIDTH / 2) + (barWidth / 2), HEIGHT - 60 + BAR_HEIGHT));
        }
    }

    private void gotoxy(double x, double y) {
        c.setCenterX(x);
        c.setCenterY(y);
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void handle(KeyEvent event) {
        //get the bar element

        //  Rectangle tmpBar = (Rectangle) nodeList.get(bricksList.size());

        if (event.getCode() == KeyCode.RIGHT) {
            keyPressedState = 1;
        }
        if (event.getCode() == KeyCode.LEFT) {
            keyPressedState = 2;
        }
        if (event.getCode() == KeyCode.SPACE) {
            isShooting = true;
        }
        if (isGameOver && event.getCode() == KeyCode.ENTER) {
            if (levelCleared) {
                root = setupGame(STAGE_NB+1);
                System.out.println("stage " + (STAGE_NB+1));
            } else {
                root = setupGame(STAGE_NB);
                System.out.println("stage " + STAGE_NB);
            }
            //reset of every element
            isGameOver = false;
            x = WIDTH / 2;
            y = HEIGHT - 100;
            upDirection = true;
            rightDirection = true;
            primaryStage.setScene(setupScene());
            timeline.play();
        }

        //debug only
        if (event.getCode() == KeyCode.UP) {
            ySpeed++;
        }
        if (event.getCode() == KeyCode.DOWN) {
            ySpeed--;
        }

    }

    public void startTranslate(int direction) {
        tmpBar = (Rectangle) nodeList.get(bricksList.size());
        tmpBar.setX(tmpBar.getX() + direction);
        bar.doTranslation(direction);
    }
}
