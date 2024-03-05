package com.affirm.livenessapi.model.response;

public class LivenessApiResponse {

    private Boolean approved;
    private LivenessData data;
    private LivenessRejection rejection;

    public static class LivenessData{
        private Double selfie_score;
        private Double video_duration;
        private Double total_frames;

        public Double getSelfie_score() {
            return selfie_score;
        }

        public void setSelfie_score(Double selfie_score) {
            this.selfie_score = selfie_score;
        }

        public Double getVideo_duration() {
            return video_duration;
        }

        public void setVideo_duration(Double video_duration) {
            this.video_duration = video_duration;
        }

        public Double getTotal_frames() {
            return total_frames;
        }

        public void setTotal_frames(Double total_frames) {
            this.total_frames = total_frames;
        }
    }

    public static class LivenessRejection {
        private String code;
        private String message;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public LivenessData getData() {
        return data;
    }

    public void setData(LivenessData data) {
        this.data = data;
    }

    public LivenessRejection getRejection() {
        return rejection;
    }

    public void setRejection(LivenessRejection rejection) {
        this.rejection = rejection;
    }
}
