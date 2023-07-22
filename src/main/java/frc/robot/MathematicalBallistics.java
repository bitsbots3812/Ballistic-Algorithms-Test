package frc.robot;

public class MathematicalBallistics {
   
    static Ballistics.Trajectory getTrajectory(double x, double y, double offset) {
        Ballistics.Trajectory t = new Ballistics.Trajectory();
        try {
            t.x = x;
            double vy = Math.sqrt(2 * 9.81 * (y + offset));
            double time = ((-1 * vy) - (Math.sqrt(Math.pow(vy, 2) - (2 * -9.81 * -y)))) / -9.81;
            //System.out.println(time);
            double vx = x / time;
            t.v = Math.sqrt(Math.pow(vx, 2) + Math.pow(vy, 2));
            //System.out.println(vy);
            //System.out.println(vx);
            t.a = Math.toDegrees(Math.atan(vy / vx));
            return t;
        } 
        catch(ArithmeticException e) {
            //TODO: Add validity flag to trajectory class
            t.x = 0;
            t.v = 0;
            t.a = 0;
            return t;
        }
    }   
    static Ballistics.Trajectory getTrajectoryLinearOffset(double x, double y, double offset) {
        return getTrajectory(x, y, offset * x);
    }
}
