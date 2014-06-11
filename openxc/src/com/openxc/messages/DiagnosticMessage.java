package com.openxc.messages;

import java.util.Map;
import java.util.HashMap;

import android.os.Parcel;

import com.openxc.util.Range;

public abstract class DiagnosticMessage extends VehicleMessage
        implements KeyedMessage {

    public static final String ID_KEY = CanMessage.ID_KEY;
    public static final String BUS_KEY = CanMessage.BUS_KEY;
    public static final String MODE_KEY = "mode";
    public static final String PID_KEY = "pid";
    public static final String PAYLOAD_KEY = "payload";

    // TODO can there be more buses?
    public static final Range<Integer> BUS_RANGE = new Range<>(1, 2);
    public static final Range<Integer> MODE_RANGE = new Range<>(1, 15);
    public static final int MAX_PAYLOAD_LENGTH_IN_BYTES = 7;
    public static final int MAX_PAYLOAD_LENGTH_IN_CHARS = MAX_PAYLOAD_LENGTH_IN_BYTES * 2;

    private int mBusId;
    private int mId;
    private int mMode;
    private int mPid;
    private byte[] mPayload;

    // TODO if this class is abstract, what is not implemented? is it ihe fact
    // that there aren't any public constructors?
    // public abstract DiagnosticMessage();

    // TODO pid is optiona, it should not go in this basic constructor
    public DiagnosticMessage(int busId, int id, int mode, byte[] payload) {
        mBusId = busId;
        mId = id;
        mMode = mode;
        // TODO need a way to mark that we have *no* pid
        // TODO need to copy array
        mPayload = payload;
    }

    // TODO DRY
    public DiagnosticMessage(int busId, int id, int mode, int pid,
            byte[] payload) {
        mBusId = busId;
        mId = id;
        mMode = mode;
        mPid = pid;
        // TODO need to copy array
        mPayload = payload;
    }

    protected DiagnosticMessage(Map<String, Object> values)
            throws InvalidMessageFieldsException {
        super(values);
        if(!containsRequiredFields(values)) {
            throw new InvalidMessageFieldsException(
                    "Missing keys for construction in values = " +
                    values.toString());
        }

        mBusId = (Integer) getValuesMap().remove(CanMessage.BUS_KEY);
        mId = (Integer) getValuesMap().remove(CanMessage.ID_KEY);
        mMode = (Integer) getValuesMap().remove(MODE_KEY);

        if(contains(PID_KEY)) {
            mPid = (Integer) getValuesMap().remove(PID_KEY);
        }
        if(contains(PAYLOAD_KEY)) {
            // TODO what's the right way to convert this?
            // https://github.com/openxc/openxc-message-format
            // says "bytes [...] as a hexadecimal number in a string"
            mPayload = ((String) getValuesMap().remove(PAYLOAD_KEY)).getBytes();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !super.equals(obj)) {
            return false;
        }

        final DiagnosticMessage other = (DiagnosticMessage) obj;
        return mBusId == other.mBusId
                && mId == other.mId
                && mMode == other.mMode
                && mPid == other.mPid;
                // TODO
                // && mPayload.equals(other.mPayload);
    }

    public int getBusId() {
        return mBusId;
    }

    public int getId() {
        return mId;
    }

    public int getMode() {
        return mMode;
    }

    public int getPid() {
        return mPid;
    }

    public byte[] getPayload() {
        return mPayload;
    }

    public MessageKey getKey() {
        HashMap<String, Object> key = new HashMap<>();
        key.put(CanMessage.BUS_KEY, getBusId());
        key.put(CanMessage.ID_KEY, getId());
        key.put(MODE_KEY, getMode());
        key.put(PID_KEY, getPid());
        return new MessageKey(key);
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeInt(getBusId());
        out.writeInt(getId());
        out.writeInt(getMode());
        out.writeInt(getPid());
        // TODO
        // out.writeByteArray(getPayload());
    }

    @Override
    protected void readFromParcel(Parcel in) {
        super.readFromParcel(in);
        mBusId = in.readInt();
        mId = in.readInt();
        mMode = in.readInt();
        mPid = in.readInt();
        // TODO
        // in.readByteArray(mPayload);
    }

    protected static boolean containsRequiredFields(Map<String, Object> map) {
        return map.containsKey(BUS_KEY) && map.containsKey(ID_KEY)
                && map.containsKey(MODE_KEY);
    }

    protected DiagnosticMessage() { }
}
