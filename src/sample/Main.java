package sample;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.image.*;


import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.List;

import static java.lang.Math.*;


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
    //3 -> both, then stop
    int keyPressedState = 0;
    boolean isShooting = false;
    boolean rightPressed = false;
    boolean leftPressed = false;

    //game over screen
    private boolean isGameOver = false;
    Label l;
    private boolean hasGameStarted = false;

    Stage primaryStage;

    //check if level is cleared or not
    private boolean levelCleared = false;
    private boolean isGameWaiting = true;

    //arrow start hint
    Line line;
    private boolean arrowDrawn = false;

    //drops list
    Drop drop;
    List<Integer> dropIndexes = new ArrayList<>();
    List<Drop> cettefoiscestlabonne;
    private double DROP_FALLING_SPEED = 0.5;
    private boolean needToBeDeleted = false;

    //effects icons when theirs effects are triggered
    Image increaseBarSpeedImg;
    Image decreaseBarSpeedImg;
    Image increaseBarSizeImg;
    Image decreaseBarSizeImg;
    Image increaseBallSpeedImg;
    Image decreaseBallSpeedImg;
    private int nbOfIcons = 0;

    //commands icons
    Image commandsIcons;
    Image resumeIcon;
    private ImageView iv;
    private ImageView view;
    private Label pauseLabel;
    private Label gameOverLabel;
    private Image enterIcon;

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

        //loads the effects icons
        try {
            loadIcons();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        this.primaryStage = primaryStage;
        primaryStage.show();
        try {
            commandsIcons = new Image(new FileInputStream("src/resources/commandsIcons2.png"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //image view
        iv = new ImageView(commandsIcons);
        iv.setLayoutX(WIDTH - 200);
        iv.setLayoutY(HEIGHT - 50);
        root.getChildren().add(iv);

        //hasGameStarted = true;
        timeline.play();
    }

    private Scene setupScene() {
        scene = new Scene(root, WIDTH, HEIGHT);
        scene.setFill(Color.BLACK);

        //keyboard listener
        scene.setOnKeyPressed(this);
        scene.setOnKeyReleased(event -> {

            if (event.getCode() == KeyCode.RIGHT) {
                if (leftPressed) {
                    keyPressedState = 2;
                }
                rightPressed = false;
                //   System.out.println("right released");
            }
            if (event.getCode() == KeyCode.LEFT) {
                if (rightPressed) {
                    keyPressedState = 1;
                }
                leftPressed = false;
                //  System.out.println("left released");

            }
            if (!rightPressed && !leftPressed) {
                keyPressedState = 0;
            }
            if (event.getCode() == KeyCode.SPACE) {
                isShooting = false;
            }
        });
        drawArrow(true);

        //image view
        iv = new ImageView(commandsIcons);
        iv.setLayoutX(WIDTH - 200);
        iv.setLayoutY(HEIGHT - 50);
        root.getChildren().add(iv);

        return scene;
    }


    private void refreshScene() {
        moveBallTo(x, y);


        //bar position
        switch (keyPressedState) {
            case 1:
                if (bar.getTopRight().x <= WIDTH) {
                    startTranslate(barSpeed);
                }
                if (isGameWaiting) {
                    x = bar.getTopRight().x - barWidth / 2;
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
                    x = bar.getTopLeft().x + barWidth / 2;
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
            hasGameStarted = true;

            //remove commands icons
            if (iv != null) {
                FadeTransition ft = new FadeTransition(Duration.millis(3000), iv);
                ft.setFromValue(1.0);
                ft.setToValue(0);
                ft.setOnFinished(event -> {
                    root.getChildren().remove(iv);
                    iv = null;
                });
                ft.play();
            }

            if (arrowDrawn) {
                root.getChildren().remove(line);
                arrowDrawn = false;
            }
        }
        if (isGameWaiting) {
            return;
        }
        //hasGameStarted = true;
        levelCleared = true;
        //state of the next frame
        for (int p = 0; p < bricksList.size(); p++) {
            if (bricksList.get(p) == null) {
                continue;
            }
            //if the level is cleared this statement is never reached
            levelCleared = false;
            //todo: check diagonal hits (ball currently considered as a square)

            boolean squareDelete = false;

            ///////WIP///////
          /*  if (isInside(bricksList.get(p))) {
                System.out.println("bounce");
                squareDelete = true;
                if (y != 0) {
                    upDirection = !upDirection;
                    y -= ySpeed;
                } else
                if (x != 0) {
                    rightDirection = !rightDirection;
                    x -= xSpeed;
                }
            }*/
            ////////////////

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
                    drop = bricksList.get(p).getDrop();
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
               // System.out.println("xspeed : " + xSpeed);
                if (xSpeed == 0) {
                    xSpeed++;
                    //x -= xSpeed;

                }
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

        //------------------------DROPS----------------------------
        //getting the elements that might be deleted
        int temp = cettefoiscestlabonne.size();
        if (!cettefoiscestlabonne.isEmpty()) {
            for (Drop d : cettefoiscestlabonne) {
                dropFall(d);
            }
            if (needToBeDeleted) {
                //todo: bug : when too much (how much ?) drops are grouped and one is deleted (bar or bottom hit), all drops from the group are deleted
                //removes the last element that was added
                needToBeDeleted = false;
                root.getChildren().remove(cettefoiscestlabonne.get(0).getCircle());
                cettefoiscestlabonne.remove(0);
            }
        }

        if (drop != null) {
            //store the drop
            cettefoiscestlabonne.add(drop);
            drop = null;
        }

        //drop falling
//        if (!dropIndexes.isEmpty()) {
//            Integer i = null;
//            List<Circle> tmpList = new ArrayList<>();
//            for (Integer d : dropIndexes) {
//                i = d;
//                Circle dropCircle = (Circle) root.getChildren().get(d);
//                tmpList.add(dropCircle);
//                dropFall(dropCircle);
//                System.out.println(dropIndexes.size());
//            }
//            dropIndexes.remove(i);
//        }

        if (cettefoiscestlabonne.size() != temp) {
            if (cettefoiscestlabonne.size() > temp) {
                // a drop just appeared
                root.getChildren().add(cettefoiscestlabonne.get(temp).getCircle());
            } else {
                // a drop just disappeared
                //root.getChildren().remove(tmpDrop.getCircle());
            }
        }

        //---------------------------------------------------------

        // System.out.println(xSpeed + " " + ySpeed);
    }

    //return true if ball is inside a brick
    private boolean isInside(Brick b) {
        List<Point> pointsList = listAllPoints(b);

        // equation to determine if a point of the brick is on the ball perimeter
       /* for (Point p : pointsList) {
            if (pow(p.getX() - x,  2) + pow(p.getY() - y, 2) == pow(RADIUS, 2)) {
                System.out.println("collision");
                return true;
            }
        }*/
        if (pow(b.getTopRight().getX() - x, 2) + pow(b.getTopRight().getY() - y, 2) == pow(RADIUS, 2)
                || pow(b.getBottomRight().getX() - x, 2) + pow(b.getBottomRight().getY() - y, 2) == pow(RADIUS, 2)
                || pow(b.getBottomLeft().getX() - x, 2) + pow(b.getBottomLeft().getY() - y, 2) == pow(RADIUS, 2)
                || pow(b.getTopLeft().getX() - x, 2) + pow(b.getTopLeft().getY() - y, 2) == pow(RADIUS, 2)) {
            System.out.println("collision");
            return true;
        }
        return false;
    }

    private List<Point> listAllPoints(Brick b) {
        // lists all points (integers) of the brick
        // angles are duplicate
        List<Point> resultTab = new ArrayList<>();
        for (int right = 0; right < 20; right++) {
            resultTab.add(new Point(b.getTopRight().x, b.getTopRight().y + right));
        }
        for (int bottom = 0; bottom < 80; bottom++) {
            resultTab.add(new Point(b.getBottomRight().x - bottom, b.getBottomRight().y));
        }
        for (int left = 0; left < 20; left++) {
            resultTab.add(new Point(b.getBottomLeft().x, b.getBottomLeft().y - left));
        }
        for (int top = 0; top < 80; top++) {
            resultTab.add(new Point(b.getTopLeft().x + top, b.getTopLeft().y));
        }
        return resultTab;
    }

    private void drawArrow(boolean toTheRight) {
        if (arrowDrawn) {
            root.getChildren().remove(line);
            arrowDrawn = false;
        }
        if (toTheRight) {
            line = new Line(x, y, x + 30, y - 30);
        } else {
            line = new Line(x, y, x - 30, y - 30);
        }
        line.setStroke(Color.YELLOW);
        root.getChildren().add(line);
        arrowDrawn = true;
    }

    private void drawGameOver() {
        timeline.pause();
        hasGameStarted = false;

        //draw different messages depending if level is cleared or not
        if (!levelCleared) {
            l = new Label("GAME OVER");
            l.setTextFill(Color.RED);
            l.setLayoutY(HEIGHT / 2. - 100);
            l.setLayoutX(WIDTH / 2. - 270);

            //retry label
            gameOverLabel = new Label("Réessayer");
            gameOverLabel.setLayoutY(HEIGHT / 2. + 25);
            gameOverLabel.setLayoutX(WIDTH / 2. - 200);
        } else {
            l = new Label("STAGE CLEARED");
            l.setTextFill(Color.GREEN);
            l.setLayoutY(HEIGHT / 2. - 100);
            l.setLayoutX(WIDTH / 2. - 350);

            //next level label
            gameOverLabel = new Label("Continuer");
            gameOverLabel.setLayoutY(HEIGHT / 2. + 25);
            gameOverLabel.setLayoutX(WIDTH / 2. - 150);
        }
        l.setStyle("-fx-font-family: 'OCR A Extended'; -fx-font-size: 100; -fx-font-weight: bold");
        gameOverLabel.setStyle("-fx-font-family: 'OCR A Extended'; -fx-font-size: 50;");

        //enter icon
        try {
            enterIcon = new Image(new FileInputStream("src/resources/enterIcon.png"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ImageView enterImage = new ImageView(enterIcon);
        enterImage.setLayoutY(HEIGHT / 2. + 25);
        enterImage.setLayoutX(WIDTH / 2. + 150);

        root.getChildren().add(l);
        root.getChildren().add(gameOverLabel);
        root.getChildren().add(enterImage);

        isGameOver = true;
        isGameWaiting = true;
    }

    public Group setupGame(int stageNumber) {
        //todo: use class brick with all properties

        xSpeed = 1;
        ySpeed = 2;
        barSpeed = 5;
        barWidth = 200;
        nodeList = new ArrayList<>();
        bricksList = new ArrayList<>();
        cettefoiscestlabonne = new ArrayList<>();
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
                for (int j = 0; j < 10; j++) {
                    if (i != 10 || j != 9) {
                        //(i + j) % 2 == 0
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
            // get the current points before modifying them
            Point tmpTopRight = new Point(bar.getTopRight().x, bar.getTopRight().y);
            Point tmpBottomRight = new Point(bar.getBottomRight().x, bar.getBottomRight().y);
            Point tmpBottomLeft = new Point(bar.getBottomLeft().x, bar.getBottomLeft().y);
            Point tmpTopLeft = new Point(bar.getTopLeft().x, bar.getTopLeft().y);

            //modification of the hitbox
            bar.setTopRight(new Point((int) ((tmpTopLeft.x + r.getWidth() / 2) + barWidth / 2), HEIGHT - 60));
            bar.setBottomRight(new Point((int) ((tmpBottomLeft.x + r.getWidth() / 2) + barWidth / 2), HEIGHT - 60 + BAR_HEIGHT));
            bar.setTopLeft(new Point((int) ((tmpTopRight.x - r.getWidth() / 2) - barWidth / 2), HEIGHT - 60));
            bar.setBottomLeft(new Point((int) (tmpBottomRight.x - r.getWidth() / 2 - barWidth / 2), HEIGHT - 60 + BAR_HEIGHT));

            //modifications of the sprite
            r.setWidth(barWidth);
            r.setX(bar.getTopLeft().x);
        }
        System.out.println(barWidth);
    }

    private void moveBallTo(double x, double y) {
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
            rightPressed = true;
            if (!leftPressed) {
                keyPressedState = 1;
            } else {
                keyPressedState = 0;
            }
        }
        if (event.getCode() == KeyCode.LEFT) {
            leftPressed = true;
            if (!rightPressed) {
                keyPressedState = 2;
            } else {
                keyPressedState = 0;
            }
        }
        if (event.getCode() == KeyCode.SPACE) {
            isShooting = true;
        }
        if (isGameOver && event.getCode() == KeyCode.ENTER) {
            if (levelCleared) {
                root = setupGame(STAGE_NB + 1);
                System.out.println("stage " + (STAGE_NB + 1));
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
          //  hasGameStarted = true;
            timeline.play();
        }
        if (event.getCode() == KeyCode.ESCAPE) {
            if (!isGameWaiting && hasGameStarted) {
                isGameWaiting = true;
                timeline.pause();

                //draw resume button hint
                try {
                    resumeIcon = new Image(new FileInputStream("src/resources/resume.png"));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                //pause text
                pauseLabel = new Label("PAUSE");
                pauseLabel.setLayoutY(HEIGHT / 2. - 100);
                pauseLabel.setLayoutX(WIDTH / 2. - 150);
                pauseLabel.setStyle("-fx-font-family: 'OCR A Extended'; -fx-font-size: 100; -fx-font-weight: bold");

                view = new ImageView(resumeIcon);
                view.setLayoutY(HEIGHT / 2.);
                view.setLayoutX(WIDTH / 2. - 75);
                root.getChildren().add(view);
                root.getChildren().add(pauseLabel);

            } else if (hasGameStarted) {
                if (view != null && pauseLabel != null) {
                    root.getChildren().remove(view);
                    root.getChildren().remove(pauseLabel);
                    view = null;
                    pauseLabel = null;
                }
                timeline.play();
                isGameWaiting = false;
            }
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

    public void dropFall(Drop dropCircle) {
        if (dropCircle.getCircle().getCenterY() <= HEIGHT) {
            dropCircle.getCircle().setCenterY(dropCircle.getCircle().getCenterY() + DROP_FALLING_SPEED);
        } else {
            // cettefoiscestlabonne.remove(dropCircle);
            root.getChildren().remove(dropCircle.getCircle());
            needToBeDeleted = true;
        }
        if (dropCircle.getCircle().getCenterX() - 5 <= bar.getTopRight().getX()
                && dropCircle.getCircle().getCenterX() + 5 >= bar.getTopLeft().getX()
                && dropCircle.getCircle().getCenterY() + 5 >= HEIGHT - 60
                && dropCircle.getCircle().getCenterY() - 5 <= HEIGHT - 60 + BAR_HEIGHT) {
            popEffect(dropCircle);
            dropCircle.setEffectPop(true);
            needToBeDeleted = true;
        }
    }

    private void popEffect(Drop dropCircle) {
        if (dropCircle.isEffectPop())
            return;

        String effect = dropCircle.getEffect();
        switch (effect) {
            case "increaseBarSize":
                barWidth += 50;
                setupBar(barWidth);
                playIconAnimation(increaseBarSizeImg);
                break;
            case "decreaseBarSize":
                if (barWidth > 100) {
                    barWidth -= 50;
                    setupBar(barWidth);
                }
                playIconAnimation(decreaseBarSizeImg);
                break;
            case "increaseBarSpeed":
                barSpeed += 2;
                playIconAnimation(increaseBarSpeedImg);
                break;
            case "decreaseBarSpeed":
                if (barSpeed < 3)
                    barSpeed -= 2;
                playIconAnimation(decreaseBarSpeedImg);
                break;
            case "increaseBallSpeed":
                ySpeed++;
                playIconAnimation(increaseBallSpeedImg);
                break;
            case "decreaseBallSpeed":
                if (ySpeed > 1)
                    ySpeed--;
                playIconAnimation(decreaseBallSpeedImg);
                break;
            default:
                System.out.println("erreur: pas d'effet");
                break;
        }
        System.out.println(effect);
        // needToDrop = true;
    }

    // loads the effects icons
    public void loadIcons() throws FileNotFoundException {
       // inputIncreaseBarSpeed = new FileInputStream("src/resources/test.png");
        increaseBarSpeedImg = new Image(new FileInputStream("src/resources/barSpeedUp.png"));
        decreaseBarSpeedImg = new Image(new FileInputStream("src/resources/barSpeedDown.png"));
        increaseBarSizeImg = new Image(new FileInputStream("src/resources/barSizeUp3.png"));
        decreaseBarSizeImg = new Image(new FileInputStream("src/resources/barSizeDown3.png"));
        increaseBallSpeedImg = new Image(new FileInputStream("src/resources/ballSpeedUp.png"));
        decreaseBallSpeedImg = new Image(new FileInputStream("src/resources/ballSpeedDown.png"));




          //  root.getChildren().remove(imageView);
    }

    public void playIconAnimation(Image img) {
        // GIVE THE IMAGE TO THE IMAGE VIEW (IMPLEMENT IN DROP TRIGGERED EFFECT)
        //todo: bug : icons dont display sometimes
        nbOfIcons++;
        for (int i = 0; i < nbOfIcons; i++) {
            System.out.println("nb of icons : " + nbOfIcons);
            ImageView imageView = new ImageView(img);
            // the icons stacks one on top of the others
            imageView.setLayoutY(HEIGHT - 50*nbOfIcons);
            imageView.setLayoutX(30);

            FadeTransition ft = new FadeTransition(Duration.millis(3000), imageView);
            ft.setFromValue(1.0);
            ft.setToValue(0);
            //ft.setCycleCount(1);
            // ft.setAutoReverse(true);
            ft.setOnFinished(event -> {
                root.getChildren().remove(imageView);
                //imageView.setImage(null);
                nbOfIcons--;
                System.out.println("icon deleted");
            });
            root.getChildren().add(imageView);
            ft.play();

        }
        // System.out.println("icon already used");
//            ImageView imageView2 = imageView;
//            imageView2.setImage(img);
//            imageView2.setLayoutY(imageView.getLayoutY() - 50);
//            root.getChildren().add(imageView2);


    }
}
