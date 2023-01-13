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

    static public final int MIN_PRIORITY = 1;
    static public final int MAX_PRIORITY = 10;

    /**
     * A default priority for threads that not a Task
     */
    static public final TaskType DEFAULT = TaskType.OTHER;
    // Members
    private final int priority;

    /**
     *
     * @param priority the priority an integer value, ranging from 1 to 10
     */
    TaskType(int priority) {
        if (MIN_PRIORITY <= priority && priority <= MAX_PRIORITY) {
            this.priority = priority;
        } else {
            throw new IllegalArgumentException("Priority is not an integer");
        }
    }

    public int getPriority() {
        return priority;
    }
}
