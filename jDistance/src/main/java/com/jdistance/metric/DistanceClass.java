package com.jdistance.metric;

import com.jdistance.metric.impl.*;

import java.util.Arrays;
import java.util.List;

public enum DistanceClass {
    WALK(Walk.class),
    LOG_FOREST(LogForest.class),
    FOREST(Forest.class),
    PLAIN_WALK(PlainWalk.class),
    COMM(Comm.class),
    COMM_D(Comm_D.class),
    LOG_COMM(LogComm.class),
    LOG_COMM_D(LogComm_D.class),
    SP_CT(com.jdistance.metric.impl.SP_CT.class),
    FREE_ENERGY(FreeEnergy.class);

    private Class clazz;

    DistanceClass(Class clazz) {
        this.clazz = clazz;
    }

    public static List<DistanceClass> getAll() {
        return Arrays.asList(DistanceClass.values());
    }

    public static DistanceClass getDistanceName(Distance distance) {
        String className = distance.getClass().toString();
        for (DistanceClass distanceClass : DistanceClass.values()) {
            Distance distanceInstance = distanceClass.getInstance();
            if (className.equals(distanceInstance.getClass().toString())) {
                return distanceClass;
            }
        }
        return null;
    }

    public Distance getInstance(String name) {
        Distance distance = getInstance();
        distance.setName(name);
        return distance;
    }

    public Distance getInstance() {
        try {
            return (Distance) clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        throw new RuntimeException();
    }

    public static List<DistanceClass> getDefaultDistances() {
        return Arrays.asList(
                DistanceClass.COMM_D,
                DistanceClass.LOG_COMM_D,
                DistanceClass.SP_CT,
                DistanceClass.FREE_ENERGY,
                DistanceClass.WALK,
                DistanceClass.LOG_FOREST,
                DistanceClass.FOREST,
                DistanceClass.PLAIN_WALK
        );
    }
}
