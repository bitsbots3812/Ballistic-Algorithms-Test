package frc.robot;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class Ballistics {
    static DecimalFormat df = new DecimalFormat("0.00");
    ArrayList<Trajectory> Trajectories;

    public enum TargetSurface {
        TOP,
        SIDE
    }
    static public class Trajectory {
        public double x;
        public double a;
        public double v;
    }

    public static Trajectory InterpolateTrajectory(double x, ArrayList<Trajectory> trajs ){
        Trajectory t = new Trajectory();
        t.x = x;

        int max = trajs.size() - 1;
        for (int counter = 0; counter < max; counter++) { 	
            Trajectory t1 = trajs.get(counter);
            Trajectory t2 = trajs.get(counter+1);
            if (x >= t1.x && x <= t2.x){
            t.a = map(x, t1.x, t2.x, t1.a, t2.a);
            t.v = map(x, t1.x, t2.x, t1.v, t2.v);
            return t;
            }
            
        }   	
        return t;
    }

    public static ArrayList<Trajectory> FindMinimumEnergyShots(){ 
        System.out.println("FindMinimumEnergyShots");

        TargetSurface ts = TargetSurface.TOP; // SIDE to hit a target facing us, TOP to hit a target from above
        double pDiameter = 0.2413; //meters, projectile diameter

        double y = 2.72; // FIXED number of meters above projectile exit point, NOT THE FLOOR

        double xMin = 0.25; // meters, minimum horizontal distance to target
        double xMax = 9; // meters, maximum horizontal distance to target
        double xInc = 0.25; // x increment

        double vMin = 0.25; // meters per second, minimum speed device can expel projectile
        double vMax = 8.5; // meters per second, maximum speed device can expel projectile
        double vInc = 0.25; // v incremen

        ArrayList<Trajectory> solutions = new ArrayList<Trajectory>();

        for (double x = xMin; x < xMax; x+= xInc) {
            
            boolean foundSolution = false;
            for (double v = vMin; v < vMax; v+= vInc){
                if (foundSolution) continue;

                double[] angles = GetShootingAngles(x, y, v);
                
                if (angles.length > 0) {

                    double a = ts == TargetSurface.TOP ? angles[0] : angles[1];
                    double hMax = GetMaxHeight(v, a);
                    if ((y + (pDiameter/2)) < hMax) {
                        //shot is too low
                        continue;
                    }

                    Trajectory t = new Trajectory();
                    t.x = x;
                    t.a = a;
                    t.v = v;
                    solutions.add(t);
                    String result = "From " + df.format(x) + " meters away, set firing angle to " + df.format(a) + " degrees and exit velocity to " + v + " meters per second";

                    System.out.println(result);
                    foundSolution = true;

                }
            
            }
            if (!foundSolution) {
            System.out.println("From " + df.format(x) + " meters away, NO SOLUTION FOUND");
            }
        }

        return solutions;
    }

  // https://physics.stackexchange.com/questions/56265/how-to-get-the-angle-needed-for-a-projectile-to-pass-through-a-given-point-for-t
    public static double[] GetShootingAngles(double x, double y, double v) {
        double[] result = {};
        final double g = 9.81; // ish

        double eff = 2 * v * v / g;
        double rootterm = eff*(eff - 2*y) - 2*x*x;

        // test for imaginary roots
        if(rootterm < 0) {
            //... cannot hit target with this velocity ...
        } else {
            double gamma_first = (eff + Math.sqrt(rootterm))/2;
            double gamma_second = (eff - Math.sqrt(rootterm))/2;
            double theta_first = Math.toDegrees(Math.atan(gamma_first / x));
            double theta_second = Math.toDegrees(Math.atan(gamma_second / x));

            theta_first = Math.round(theta_first*100)/100D; //round to 2 decimal places
            theta_second = Math.round(theta_second*100)/100D; //round to 2 decimal places
            result = new double[]{theta_first, theta_second};
        }
        return result;
    }

    public static double GetMaxHeight(double v, double a){
        return Math.pow(v * Math.sin(a),2) / (2 * 9.81);
    }

    public static double map(double x, double in_min, double in_max, double out_min, double out_max) {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }
}
