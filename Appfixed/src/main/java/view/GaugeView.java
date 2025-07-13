package view;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class GaugeView extends Pane {
    private final Canvas canvas = new Canvas(360, 200);
    private double bmi = 0;

    public GaugeView() {
        getChildren().add(canvas);
        drawGauge();
    }

    public void setBMI(double bmi) {
        this.bmi = bmi;
        drawGauge();
    }

    private void drawGauge() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        double centerX = 180;
        double centerY = 170;
        double radius = 140;
        gc.setLineWidth(30);

        double angleUnderweight = 45;
        double angleNormal = 45;
        double angleOverweight = 45;
        double angleObese = 45;
        double startAngle = 180;

        gc.setStroke(Color.DODGERBLUE); // Underweight
        gc.strokeArc(centerX - radius, centerY - radius, radius * 2, radius * 2,
                startAngle, -angleUnderweight, ArcType.OPEN);

        startAngle -= angleUnderweight;
        gc.setStroke(Color.LIMEGREEN); // Normal
        gc.strokeArc(centerX - radius, centerY - radius, radius * 2, radius * 2,
                startAngle, -angleNormal, ArcType.OPEN);

        startAngle -= angleNormal;
        gc.setStroke(Color.ORANGE); // Overweight
        gc.strokeArc(centerX - radius, centerY - radius, radius * 2, radius * 2,
                startAngle, -angleOverweight, ArcType.OPEN);

        startAngle -= angleOverweight;
        gc.setStroke(Color.CRIMSON); // Obesity
        gc.strokeArc(centerX - radius, centerY - radius, radius * 2, radius * 2,
                startAngle, -angleObese, ArcType.OPEN);

        // Labels
        drawLabel(gc, centerX, centerY, radius - 35, 180, "16.0");
        drawLabel(gc, centerX, centerY, radius - 35, 135, "18.5");
        drawLabel(gc, centerX, centerY, radius - 35, 90, "25.0");
        drawLabel(gc, centerX, centerY, radius - 35, 45, "30.0");
        drawLabel(gc, centerX, centerY, radius - 35, 0, "40.0");

        drawLabel(gc, centerX, centerY, radius - 25, 157.5, "Underweight", 11, Color.WHITE);
        drawLabel(gc, centerX, centerY, radius - 25, 112.5, "Normal", 11, Color.WHITE);
        drawLabel(gc, centerX, centerY, radius - 25, 67.5, "Overweight", 11, Color.WHITE);
        drawLabel(gc, centerX, centerY, radius - 25, 22.5, "Obesity", 11, Color.WHITE);

        gc.setFill(Color.WHITE);
        gc.setFont(new Font(16));
        gc.fillText("BMI", centerX - 15, centerY - 25);

        gc.setFill(Color.DODGERBLUE);
        gc.setFont(new Font(24));
        gc.fillText(String.format("%.2f", bmi), centerX - 30, centerY + 5);

        drawNeedle(gc, centerX, centerY, radius - 45, bmi);
    }

    private void drawNeedle(GraphicsContext gc, double cx, double cy, double length, double bmiValue) {
        double angle;

        if (bmiValue < 16.0) {
            // Jika BMI belum valid (misalnya awal 0), arahkan jarum datar kiri
            angle = 180.0;
        } else if (bmiValue <= 18.5) {
            double ratio = (bmiValue - 16.0) / (18.5 - 16.0);
            angle = 180 - (ratio * 45);
        } else if (bmiValue <= 25.0) {
            double ratio = (bmiValue - 18.5) / (25.0 - 18.5);
            angle = 135 - (ratio * 45);
        } else if (bmiValue <= 30.0) {
            double ratio = (bmiValue - 25.0) / (30.0 - 25.0);
            angle = 90 - (ratio * 45);
        } else {
            double ratio = (bmiValue - 30.0) / (40.0 - 30.0);
            angle = 45 - (ratio * 45);
        }

        double rad = Math.toRadians(angle);
        double x = cx + length * Math.cos(rad);
        double y = cy - length * Math.sin(rad);

        gc.setStroke(Color.WHITE);
        gc.setLineWidth(3);
        gc.strokeLine(cx, cy, x, y);
        gc.setFill(Color.WHITE);
        gc.fillOval(cx - 5, cy - 5, 10, 10);
    }

    public void animateToBMI(double targetBMI) {
        double clampedTarget = Math.max(16.0, Math.min(targetBMI, 40.0));

        // Jika BMI awal masih 0 atau invalid, langsung set ke posisi awal (tidak muter)
        if (this.bmi < 16.0) {
            this.bmi = 16.0;
        }

        double startBMI = this.bmi;
        double delta = clampedTarget - startBMI;
        Timeline timeline = new Timeline();
        int steps = 60;

        for (int i = 0; i <= steps; i++) {
            final double progress = (double) i / steps;
            final double intermediateBMI = startBMI + delta * progress;

            KeyFrame kf = new KeyFrame(Duration.millis(i * 15), e -> {
                this.bmi = intermediateBMI;
                drawGauge();
            });

            timeline.getKeyFrames().add(kf);
        }

        timeline.setOnFinished(e -> {
            this.bmi = Math.round(clampedTarget * 100.0) / 100.0;
            drawGauge();
        });

        timeline.play();
    }

    private void drawLabel(GraphicsContext gc, double cx, double cy, double r, double angle, String text) {
        drawLabel(gc, cx, cy, r, angle, text, 9, Color.WHITE);
    }

    private void drawLabel(GraphicsContext gc, double cx, double cy, double r, double angle, String text, int fontSize, Color color) {
        double rad = Math.toRadians(angle);
        double x = cx + r * Math.cos(rad);
        double y = cy - r * Math.sin(rad);
        gc.setFill(color);
        gc.setFont(new Font(fontSize));
        gc.fillText(text, x - (text.length() * 2.5), y + 5);
    }
}
