package org.firstinspires.ftc.teamcode.Mechanisms;

import java.util.Arrays;

import dev.nextftc.control.util.InterpolatingMap;

/**
 * Looks up shooter power from the Limelight's vertical angle (ty) using an InterpolatingMap.
 *
 * Fill the table by testing on the field: park at a few distances, read ty from telemetry,
 * and find the shooter power that scores. Values between those points are filled in
 * automatically with a straight line between the two nearest entries.
 */
public class ShooterPowerTable {

    // ty (degrees) -> shooter throttle. Placeholder numbers: replace with your own measurements.
    // Keys can be listed in any order; the map sorts them.
    private static final InterpolatingMap<Double> POWER_BY_TY = InterpolatingMap.linear(
            Arrays.asList(-15.0, -10.0, -5.0, 0.0, 5.0, 10.0),
            Arrays.asList(0.95, 0.85, 0.75, 0.68, 0.62, 0.58)
    );
    // For a smooth curve instead of straight lines, use InterpolatingMap.spline(keys, values).

    /** Returns the shooter throttle for the given Limelight ty. */
    public static double powerFor(double ty) {
        // InterpolatingMap throws if you ask for a key outside the table, so clamp to its range.
        double clamped = Math.max(POWER_BY_TY.firstKey(), Math.min(POWER_BY_TY.lastKey(), ty));
        return POWER_BY_TY.get(clamped);
    }
}