package main;

public enum TaskType {
    COMPUTATIONAL(1) {
        @Override
        public String toString() {
            return "Computational Task";
        }
    },
    IO(2) {
        @Override
        public String toString() {
            return "IO - Bound Task";
        }
    },
    OTHER(3) {
        @Override
        public String toString() {
            return "Unknown Task";
        }
    };

    // Members
    private final int priority;

    private TaskType(int priority) {
        if (validatePriority(priority)) {
            this.priority = priority;
        } else {
            throw new IllegalArgumentException("Priority is not an integer");
        }
    }

    public int getPriority() {
        return priority;
    }

    public TaskType getType() {
        return this;
    }

    static public final int MIN_PRIORITY = 1;
    static public final int MAX_PRIORITY = 10;

    /**
     * priority is represented by an integer value, ranging from 1 to 10
     * @param priority the priority an integer value, ranging from 1 to 10
     * @return whether the priority is valid or not
     */
    private static boolean validatePriority(int priority) {
        if (priority < MIN_PRIORITY || priority > MAX_PRIORITY) {
            return false;
        }
        return true;
    }
}
