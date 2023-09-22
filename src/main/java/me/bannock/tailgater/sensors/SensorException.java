package me.bannock.tailgater.sensors;

public class SensorException extends Exception {

    private String reason, description;
    private boolean fatal;

    /**
     * Creates a new SensorException
     * @param reason The reason for the exception, should be concise
     * @param description The description of the exception, should be detailed and verbose
     */
    public SensorException(String reason, String description) {
        this(reason, description, false);
    }

    /**
     * Creates a new SensorException
     * @param reason The reason for the exception, should be concise
     * @param description The description of the exception, should be detailed and verbose
     * @param fatal Whether the exception is fatal, if true then it's
     *              likely a hardware issue and cannot be fixed in software
     */
    public SensorException(String reason, String description, boolean fatal) {
        super(reason + ": " + description);
        this.reason = reason;
        this.description = description;
        this.fatal = fatal;
    }

    /**
     * @return The reason for the exception
     */
    public String getReason() {
        return reason;
    }

    /**
     * @return A detailed description explaining what the problem is
     */
    public String getDescription() {
        return description;
    }

    /**
     * Whether or not the exception is fatal, if true then it's likely a hardware issue and cannot be fixed in software
     * @return true if the exception is fatal, otherwise false
     */
    public boolean isFatal() {
        return fatal;
    }

}
