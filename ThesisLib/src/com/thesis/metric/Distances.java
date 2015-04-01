package com.thesis.metric;

public enum Distances {
    WALK(Walk.class),
    LOG_FOREST(LogForest.class),
    FOREST(Forest.class),
    PLAIN_WALK(PlainWalk.class),
    COMMUNICABILITY(Communicability.class),
    LOG_COMMUNICABILITY(LogCommunicability.class),
    SP_CT(SP_CT.class),
    FREE_ENERGY(FreeEnergy.class);

    private Class clazz;

    Distances(Class clazz) {
        this.clazz = clazz;
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
