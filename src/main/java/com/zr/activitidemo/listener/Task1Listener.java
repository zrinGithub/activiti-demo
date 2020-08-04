package com.zr.activitidemo.listener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

/**
 * Description:
 * 任务1的监听器
 * 本监听器针对 TestProcess2.bpmn
 *
 * @author zhangr
 * 2020/8/4 11:15
 */
public class Task1Listener implements TaskListener {
    @Override
    public void notify(DelegateTask delegateTask) {
        delegateTask.setAssignee("user1");
    }
}
