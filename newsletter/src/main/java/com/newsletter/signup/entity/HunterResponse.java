package com.newsletter.signup.entity;

public class HunterResponse {
    private Data data;

    public Data getData() {
        return data;
    }

    public static class Data {
        private String status;
        private boolean disposable;

        public String getStatus() {
            return status;
        }

        public boolean isDisposable() {
            return disposable;
        }

        public boolean isRisky() {
            return status.equalsIgnoreCase("invalid") ||
                    status.equalsIgnoreCase("disposable") ||
                    status.equalsIgnoreCase("unknown") ||
                    disposable;
        }
    }
}

