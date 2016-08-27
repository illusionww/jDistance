package com.jdistance.learning;

import java.util.OptionalDouble;
import java.util.stream.DoubleStream;

public enum Collapse {
    MIN() {
        @Override
        public OptionalDouble apply(DoubleStream stream) {
            return stream.min();
        }
    },
    MAX() {
        @Override
        public OptionalDouble apply(DoubleStream stream) {
            return stream.max();
        }
    },
    AVERAGE() {
        @Override
        public OptionalDouble apply(DoubleStream stream) {
            return stream.average();
        }
    },
    CHECK_ONLY_ONE() {
        @Override
        public OptionalDouble apply(DoubleStream stream) {
            double[] values = stream.toArray();
            if (values.length > 1) {
                throw new IllegalArgumentException();
            } else if (values.length == 1) {
                return OptionalDouble.of(values[0]);
            } else {
                return OptionalDouble.empty();
            }
        }
    };

    public abstract OptionalDouble apply(DoubleStream stream);
}
