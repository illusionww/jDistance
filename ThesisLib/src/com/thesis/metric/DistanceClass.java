package com.thesis.metric;

public enum DistanceClass {
    WALK(Walk.class),
    LOG_FOREST(LogForest.class),
    FOREST(Forest.class),
    PLAIN_WALK(PlainWalk.class),
    COMMUNICABILITY(Communicability.class),
    LOG_COMMUNICABILITY(LogCommunicability.class),
    SP_CT(SP_CT.class),
    FREE_ENERGY(FreeEnergy.class);

    private Class clazz;

    DistanceClass(Class clazz) {
        this.clazz = clazz;
    }


    public static DistanceClass getByClassName(String className) {
        for(DistanceClass distanceClass : DistanceClass.values()) {
            Distance distance = distanceClass.getInstance();
            if(className.equals(distance.getClass().toString())) {
                return distanceClass;
            }
        }
        return null;
    }

    public Distance getInstance(String name) {
        Distance distance = getInstance();
        distance.setName(name);
        distance.setShortName(name);
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
}
