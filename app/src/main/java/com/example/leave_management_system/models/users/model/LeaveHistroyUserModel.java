package com.example.leave_management_system.models.users.model;

public class LeaveHistroyUserModel{
    public final String leave_type,leave_duration,leave_id;
    public final int leave_status;

    public LeaveHistroyUserModel(String leave_id,String leaveType, String leaveDuration, int leaveStatus) {
        this.leave_type = leaveType;
        this.leave_duration = leaveDuration;
        this.leave_status = leaveStatus;
        this.leave_id = leave_id;
    }
}
