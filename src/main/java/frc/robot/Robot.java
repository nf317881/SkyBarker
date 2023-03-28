package frc.robot;

import java.util.List;

import org.hotutilites.hotlogger.HotLogger;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.math.trajectory.TrajectoryConfig;
import edu.wpi.first.math.trajectory.TrajectoryGenerator;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Autons.BlueAutoLeft;
import frc.robot.Autons.BlueAutoLeft1Bal;
import frc.robot.Autons.BlueAutoRight;
import frc.robot.Autons.OhCrap;
import frc.robot.Autons.RedAutoLeft;
import frc.robot.Autons.RedAutoLeft1Bal;
import frc.robot.Autons.RedAutoRight;
import frc.robot.sensors.Camera;
import frc.robot.sensors.Pigeon;
import frc.robot.subsystems.Arm;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.Gripper;
import frc.robot.subsystems.Hopper;
import frc.robot.subsystems.Intake;

public class Robot extends TimedRobot {
    private TeleopCommander teleopCommander;
    private Drivetrain drivetrain;
    private Hopper hopper;
    private Arm arm;
    private Intake intake;
    private Pigeon pigeon;
    private Camera camera;
    private AutonCommader autonCommader;
    private BlueAutoRight blueAutoRight;
    private Gripper gripper;
    private BlueAutoLeft blueAutoLeft;
    private OhCrap ohCrap;
    private RedAutoRight redAutoRight;
    private RedAutoLeft redAutoLeft;
    private RedAutoLeft1Bal redAutoLeft1Bal;
    private BlueAutoLeft1Bal blueAutoLeft1Bal;

    private String autonSelection = "Blue Left 1";

    private Alliance alliance;

    @Override
    public void robotInit() {
        HotLogger.Setup("Theta", "Left Front Absolute", "Left Front Assumed",
        "Right Front Absolute", "Right Front Assumed",
        "Left Rear Absolute", "Left Rear Assumed",
        "Right Rear Absolute", "Right Rear Assumed",
        "TargetX", "TargetY", "TargetTheta", "Robot State Theta", "poseX", "poseY",
        "Shoulder Absolute Pos", "Shoulder Motor Pos", "Extension Pos", "Elbow Absolute Pos", "Elbow Motor Pos",
        "Shoulder Desired Pos", "Extension Desired Pos", "Elbow Desired Pos",
        "HopSensor Bottom", "HopSensor Left", "HopSensor Right", "HopSensor Top", "Hopper Override");


        intake = new Intake();
        teleopCommander = new TeleopCommander();
        pigeon = new Pigeon();
        camera = new Camera();
        Camera.setMode("Retroreflective");
        drivetrain = new Drivetrain();
        autonCommader = new AutonCommader();
        arm = new Arm();
        gripper = new Gripper(Constants.GRIPPER);
        hopper = new Hopper();
        blueAutoRight = new BlueAutoRight();
        blueAutoLeft = new BlueAutoLeft();
        ohCrap = new OhCrap();
        redAutoRight = new RedAutoRight();
        redAutoLeft = new RedAutoLeft();
        redAutoLeft1Bal = new RedAutoLeft1Bal();
        blueAutoLeft1Bal = new BlueAutoLeft1Bal();
    }

    @Override
    public void robotPeriodic() {
        arm.updatePose();
        camera.logData();
        pigeon.logData();
        intake.logData();
        hopper.logData();
        SmartDashboard.putNumber("Match Time", DriverStation.getMatchTime());
        SmartDashboard.putNumber("FPGA Time", Timer.getFPGATimestamp());
        drivetrain.updatePose();
    }

    @Override
    public void disabledInit() {
        intake.setCoastMode();
        SmartDashboard.putString("Robot Mode", "Disabled");
    }

    @Override
    public void disabledPeriodic() {
        arm.armPercentOutZero();
        arm.coastMode();
    }


    @Override
    public void autonomousInit() {
        SmartDashboard.putString("Robot Mode", "Autonomous");

        SmartDashboard.getString("Auton Selection", autonSelection);
        
        if(autonSelection == "Blue Left"){
            alliance = Alliance.Blue;
            autonCommader.allaince = alliance;
            autonCommader.initAuton(blueAutoLeft);
        } else if(autonSelection == "Blue Right"){
            alliance = Alliance.Blue;
            autonCommader.allaince = alliance;
            autonCommader.initAuton(blueAutoRight);
        } else if(autonSelection == "Red Left"){
            alliance = Alliance.Red;
            autonCommader.allaince = alliance;
            autonCommader.initAuton(redAutoLeft);
        } else if(autonSelection == "Red Right"){
            alliance = Alliance.Red;
            autonCommader.allaince = alliance;
            autonCommader.initAuton(redAutoRight);
        }else if(autonSelection == "Red Left 1"){
            alliance = Alliance.Red;
            autonCommader.allaince = alliance;
            autonCommader.initAuton(redAutoLeft1Bal);
        }else if(autonSelection == "Red Right 1"){
            alliance = Alliance.Red;
            autonCommader.allaince = alliance;
            autonCommader.initAuton(redAutoLeft1Bal);
        }else if(autonSelection == "Blue Left 1"){
            alliance = Alliance.Blue;
            autonCommader.allaince = alliance;
            autonCommader.initAuton(blueAutoLeft1Bal);
        }else if(autonSelection == "Blue Right 1"){
            alliance = Alliance.Blue;
            autonCommader.allaince = alliance;
            autonCommader.initAuton(redAutoLeft1Bal);
        } else {
            autonCommader.initAuton(ohCrap);
        }

        if(alliance == Alliance.Blue){
            drivetrain.zero(-90);
            autonCommader.auton.reset();
            Pigeon.zeroSensor(-90);
        } else {
            drivetrain.zero(90);
            autonCommader.auton.reset();
            Pigeon.zeroSensor(90);
        }


        arm.initilizeOffsets();
    }

    @Override
    public void autonomousPeriodic() {
        autonCommader.runAuto();
        pigeon.enabledAction(teleopCommander);
        drivetrain.autonAction(autonCommader);
        arm.action(autonCommader);
        intake.IntakePeriodic(autonCommader);
        hopper.HopperPeriodic(autonCommader);
        // gripper.action(autonCommader);
    }
        
    @Override
    public void teleopInit() {
        SmartDashboard.putString("Robot Mode", "Teleop");

        alliance = DriverStation.getAlliance();
        // alliance = Alliance.Blue;

        teleopCommander.allaince = alliance;

        if(Camera.rightAprilDetected()){
            drivetrain.zero(Camera.getRightBotPose().getRotation().getDegrees());
            Drivetrain.setPose(Camera.getRightBotPose());
            Pigeon.zeroSensor(Camera.getRightBotPose().getRotation().getDegrees());
        } else {
            if(alliance == Alliance.Blue){
                drivetrain.zero(-90);
                Pigeon.zeroSensor(-90);
            } else {
                drivetrain.zero(90);
                Pigeon.zeroSensor(90);
            }
        }

        // X - 1.785, Y - 1.621

        intake.setBrakeMode();
        arm.initilizeOffsets();
    }

    @Override
    public void teleopPeriodic() {
        pigeon.enabledAction(teleopCommander);
        drivetrain.teleAction(teleopCommander);
        intake.IntakePeriodic(teleopCommander);
        arm.action(teleopCommander);
        arm.brakeMode();
        gripper.action(teleopCommander);
        // hopper.HopperPeriodic(teleopCommander);
    }
}
