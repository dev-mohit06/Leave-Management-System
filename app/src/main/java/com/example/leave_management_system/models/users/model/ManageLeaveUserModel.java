package com.example.leave_management_system.models.users.model;

public class ManageLeaveUserModel {
    public final String leaveType,leaveDuration;
    public final int leave_id;

    public ManageLeaveUserModel(String leaveTitle, String leaveDuration, int leaveId) {
        this.leaveType = leaveTitle;
        this.leaveDuration = leaveDuration;
        this.leave_id = leaveId;
    }
}
