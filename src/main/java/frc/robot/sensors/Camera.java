package frc.robot.sensors;

//import edu.wpi.first.apriltag.AprilTag;

//import javax.swing.text.StyleContext.SmallAttributeSet;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants;

public class Camera {
    static NetworkTable limelightLeft;
    static NetworkTable limelightRight;
    static NetworkTableEntry leftBotPose;
    static NetworkTableEntry rightBotPose;

    static NetworkTableEntry leftDetected;
    static NetworkTableEntry rightDetected;

    static NetworkTableEntry leftX;
    static NetworkTableEntry leftY;
    static NetworkTableEntry rightX;
    static NetworkTableEntry rightY;

    static NetworkTableEntry captureLatencyLeft;
    static NetworkTableEntry targetingLatencyLeft;
    static NetworkTableEntry captureLatencyRight;
    static NetworkTableEntry targetingLatencyRight;

    static Pose2d leftPose = new Pose2d(0,0, new Rotation2d());
    static Pose2d rightPose = new Pose2d(0,0, new Rotation2d());

    public static String limelightMode = "AprilTagClose";

    public Camera(){
        limelightLeft = NetworkTableInstance.getDefault().getTable("limelight-left");
        limelightRight = NetworkTableInstance.getDefault().getTable("limelight-right");
        leftBotPose = limelightLeft.getEntry("botpose");
        rightBotPose = limelightRight.getEntry("botpose");

        leftDetected = limelightLeft.getEntry("tv");
        rightDetected = limelightRight.getEntry("tv");

        leftX = limelightLeft.getEntry("tx");
        leftY = limelightLeft.getEntry("ty");

        rightX = limelightRight.getEntry("tx");
        rightY = limelightRight.getEntry("ty");

        captureLatencyLeft = limelightLeft.getEntry("cl");
        targetingLatencyLeft = limelightLeft.getEntry("tl");
        captureLatencyRight = limelightRight.getEntry("cl");
        targetingLatencyRight = limelightRight.getEntry("tl");

        limelightLeft.getEntry("ledMode").setNumber(0);
        limelightRight.getEntry("ledMode").setNumber(0);
        Alliance color = Alliance.Blue;

        if (color == Alliance.Blue) {
            leftBotPose = limelightLeft.getEntry("botpose_wpiblue");
            rightBotPose = limelightRight.getEntry("botpose_wpiblue");
            //System.out.println("Here");
        } else {
            leftBotPose = limelightLeft.getEntry("botpose_wpired");
            rightBotPose = limelightRight.getEntry("botpose_wpired");
            //System.out.println("There");
        }
    }

    //Pose being robot location
    public static Pose2d getLeftBotPose(){
        double[] defaultArray = {0,0,0,0,0,0};
        double[] aprilPose = leftBotPose.getDoubleArray(defaultArray);
        leftPose = new Pose2d(aprilPose[0], aprilPose[1], new Rotation2d(Math.toRadians(aprilPose[5])));
        return leftPose;
    }

    public static double[] getLeftBotPoseRaw(){
        double [] defaultArray = {0,0,0,0,0,0};
        return  leftBotPose.getDoubleArray(defaultArray);
    }

    public static double[] getRightBotPoseRaw(){
        double [] defaultArray = {0,0,0,0,0,0}; 
        return  rightBotPose.getDoubleArray(defaultArray);
    }

    public static Pose2d getRightBotPose(){
        double[] defaultArray = {0,0,0,0,0,0};
        double[] aprilPose = rightBotPose.getDoubleArray(defaultArray);
        rightPose = new Pose2d(aprilPose[0], aprilPose[1], new Rotation2d(Math.toRadians(aprilPose[5])));
        return rightPose;
    }

    public static double getLeftY(){
        return leftY.getDouble(0);
    }

    public static double getLeftX(){
        return leftX.getDouble(0);
    }

    public static double getRightY(){
        return rightY.getDouble(0);
    }

    public static double getRightX(){
        return rightX.getDouble(0);
    }

    //Sets the pipelines of the limelights, the pipelines being if its a neural model or april tag detector or retroreflective tape detector
    public static void setPipelineLeft(int pipeline){ 
        limelightLeft.getEntry("pipeline").setNumber(pipeline);
    }

    public static void setPipelineRight(int pipeline){
        limelightRight.getEntry("pipeline").setNumber(pipeline);
    }

    //If the limelights are in retroreflective "Retroreflective" mode or april tag "AprilTag" mode
    public static String getMode(){ 
        return limelightMode;
    }

    //Localizing pipeline setting, so its easier in other files
    public static void setMode(String mode){
        if(mode == "AprilTagClose"){
            setPipelineLeft(Constants.PIPELINE_APRILTAG_CLOSE);
            setPipelineRight(Constants.PIPELINE_APRILTAG_CLOSE);
            limelightMode = "AprilTagClose";
        }
        if(mode == "AprilTagFar"){
            setPipelineLeft(Constants.PIPELINE_APRILTAG_FAR);
            setPipelineRight(Constants.PIPELINE_APRILTAG_FAR);
            limelightMode = "AprilTagFar"; 
        }
        if(mode == "Retroreflective"){
            setPipelineLeft(Constants.PIPELINE_RETROREFLECTIVE);
            setPipelineRight(Constants.PIPELINE_RETROREFLECTIVE);
            limelightMode = "Retroreflective";
        }
    }
    
    //We do a little boolean logic
    //If the left camera sees an april tag
    public static boolean leftAprilDetected() {
        return leftDetected.getDouble(0.0) == 1 && (limelightMode == "AprilTagClose" || limelightMode == "AprilTagFar");
    }

    //If the right camera sees an april tag
    public static boolean rightAprilDetected() {
        return rightDetected.getDouble(0.0) == 1 && (limelightMode == "AprilTagClose" || limelightMode == "AprilTagFar");
    }

    //If the left camera sees retroreflective tape
    public static boolean leftTapeDetected() {
        return leftDetected.getDouble(0) == 1 && !(limelightMode == "AprilTagClose" || limelightMode == "AprilTagFar");
    }

    //If the right camera sees retroreflective tape
    public static boolean rightTapeDetected() {
        return rightDetected.getDouble(0) == 1 && !(limelightMode == "AprilTagClose" || limelightMode == "AprilTagFar");
    }

    public static double getCaptureLatencyRight(){
        return captureLatencyRight.getDouble(0);
    }

    public static double getTargetingLatencyRight(){
        return targetingLatencyRight.getDouble(0);
    }

    public static double getCaptureLatencyLeft(){
        return captureLatencyLeft.getDouble(0);
    }

    public static double getTargetingLatencyLeft(){
        return targetingLatencyLeft.getDouble(0);
    }

    //Pose meaning where the robot is on the field, as opposed to where something is in the april tags FOV
    public void logData(){
        SmartDashboard.putString("Limelight Mode", limelightMode);
        //TODO log object detection data regardless of mode


        if(limelightMode == "AprilTagClose" || limelightMode == "AprilTagFar"){
            SmartDashboard.putNumber("Left Bot Pose X", getLeftBotPose().getX()); 
            SmartDashboard.putNumber("Left Bot Pose Y", getLeftBotPose().getY());
            SmartDashboard.putNumber("Left Bot Pose Theta", getLeftBotPose().getRotation().getDegrees());
            // SmartDashboard.putNumber("Left Target Seen", limelightLeft.getEntry('tv'));

            SmartDashboard.putNumber("Right Bot Pose X", getRightBotPose().getX());
            SmartDashboard.putNumber("Right Bot Pose Y", getRightBotPose().getY());
            SmartDashboard.putNumber("Right Bot Pose Theta", getRightBotPose().getRotation().getDegrees());
        }else{
            SmartDashboard.putNumber("Left Retroreflective X", leftX.getDouble(0));
            SmartDashboard.putNumber("Left Retroreflective Y", leftY.getDouble(0));
            SmartDashboard.putNumber("Right Retroreflective X", rightX.getDouble(0));
            SmartDashboard.putNumber("Right Retroreflective Y", rightY.getDouble(0));
        }
    }
}
