package com.example.leave_management_system.models.users.model;

public class RecentLeaveModel {

    public final String leaveTitle;
    public final String leaveDuration;
    public final int leaveStatus;
    public final int leaveId;


    public RecentLeaveModel(int leaveId, String leaveTitle, String leaveDuration, int leaveStatus) {
        this.leaveTitle = leaveTitle;
        this.leaveDuration = leaveDuration;
        this.leaveStatus = leaveStatus;
        this.leaveId = leaveId;
    }
}
