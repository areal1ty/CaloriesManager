package ru.javawebinar.topjava.util;

import java.net.NetworkInterface;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Enumeration;

public class uuidGenerator {
    private static final int NODE_ID_BITS = 10;
    private static final int SEQUENCE_BITS = 12;

    private static final int MAX_NODE_ID = (int) (Math.pow(2, NODE_ID_BITS) - 1);
    private static final int MAX_SEQUENCE = (int) (Math.pow(2, SEQUENCE_BITS) - 1);

    private static final long CUSTOM_EPOCH = 1420070400000L;

    private final Integer uuid;

    private volatile long lastTimestamp = -1L;
    private volatile long sequence = 0L;

    public uuidGenerator() {
        this.uuid = createId();
    }

    public synchronized Integer nextId() {
        long currentTimestamp = timestamp();

        if (currentTimestamp < lastTimestamp) {
            throw new IllegalStateException("Invalid System Clock!");
        }

        if (currentTimestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0) {
                currentTimestamp = waitNextMillis(currentTimestamp);
            }
        } else {
            sequence = 0;
        }

        lastTimestamp = currentTimestamp;

        long uuid= currentTimestamp << (NODE_ID_BITS + SEQUENCE_BITS);
       uuid|= (uuid << SEQUENCE_BITS);
       uuid|= sequence;
        if (uuid > Integer.MAX_VALUE) {
            int truncatedId = (int) (uuid % Integer.MAX_VALUE);
            return Math.abs(truncatedId);
        } else {
            return Math.abs((int) uuid);
        }
    }

    private static long timestamp() {
        return Instant.now().toEpochMilli() - CUSTOM_EPOCH;
    }

    private long waitNextMillis(long currentTimestamp) {
        while (currentTimestamp == lastTimestamp) {
            currentTimestamp = timestamp();
        }
        return currentTimestamp;
    }

    private Integer createId() {
        int uuid;
        try {
            StringBuilder sb = new StringBuilder();
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                byte[] mac = networkInterface.getHardwareAddress();
                if (mac != null) {
                    for (byte b : mac) {
                        sb.append(String.format("%02X", b));
                    }
                }
            }
           uuid= sb.toString().hashCode();
        } catch (Exception ex) {
           uuid= new SecureRandom().nextInt();
        }
       uuid= Math.abs(uuid) % MAX_NODE_ID;
        return uuid;
    }
}
