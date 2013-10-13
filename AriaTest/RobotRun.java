package org.vkedco.ariatest;


import com.mobilerobots.Aria.*;

public class RobotRun
{

    static
    {
        try
        {
            System.loadLibrary("AriaJava");
        } catch (UnsatisfiedLinkError e)
        {
            System.err.println("Native code library libAriaJava failed to load. Make sure that its directory is in your library path; See javaExamples/README.txt and the chapter on Dynamic Linking Problems in the SWIG Java documentation (http://www.swig.org) for help.\n" + e);
            System.exit(1);
        }
    }
    
    //Define the robot
    private ArRobot robot = new ArRobot();

    public RobotRun(String argv[])
    {
        //Initialize Aria
        Aria.init();

        ArSimpleConnector conn = new ArSimpleConnector(argv);

        if (!Aria.parseArgs())
        {
            Aria.logOptions();
            Aria.shutdown();
            System.exit(1);
        }

        //connect to the robot
        if (!conn.connectRobot(robot))
        {
            System.err.println("Could not connect to robot, exiting.\n");
            System.exit(1);
        }

        //run asynchronously
        robot.runAsync(true);

        //enable the motors
        robot.lock();
        robot.enableMotors();
        robot.unlock();

        //reset pose
        ArPose pose = new ArPose(0, 0, 0);
        robot.moveTo(pose, true);

        //replace the following three lines of code with your code to make the robot go in the pattern
        //mentioned in assignment 2
        move(1000); // go forward for 1 meter
        turn(-90); //turn clockwise for 90 degrees
        move(1000); //go forward for 1 meter

        //run continously
        while (robot.isRunning())
        {
            //sleep
            ArUtil.sleep(1);
        }

        //disconnect
        robot.lock();
        robot.disconnect();
        robot.unlock();


        Aria.shutdown();
    }

    /*
     * This method makes the robot move forward by the specified distance
     */
    private void move(double distance)
    {
        //reset the pose
        ArPose pose = new ArPose(0, 0, 0);
        robot.lock();
        robot.moveTo(pose);
        robot.unlock();

        //set Velocity
        robot.lock();
        robot.setVel(200);
        robot.unlock();

        //keep moving forward until distance is covered
        while (robot.getX() < distance)
        {
            ArUtil.sleep(10);
        }

        //stop the robot
        robot.lock();
        robot.setVel(0);
        robot.unlock();
    }

    /*
     * This method makes the robot turn by the specified angle
     */
    private void turn(double angle)
    {
        //reset the pose
        ArPose pose = new ArPose(0, 0, 0);
        robot.lock();
        robot.moveTo(pose);
        robot.unlock();

        //turn the robot
        robot.setDeltaHeading(angle);

        //keep turning until turn is complete (within +- 2 degrees)
        while (!robot.isHeadingDone(2))
        {
            ArUtil.sleep(10);
        }

        //stop the robot
        robot.lock();
        robot.setRotVel(0);
        robot.unlock();
    }

    public static void main(String argv[])
    {
        new RobotRun(argv);
    }
}
