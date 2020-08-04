package com.zr.activitidemo.listener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

/**
 * Description:
 * 任务3的监听器
 * 本监听器针对 TestProcess2.bpmn
 *
 * @author zhangr
 * 2020/8/4 11:15
 */
public class Task3Listener implements TaskListener {
    @Override
    public void notify(DelegateTask delegateTask) {
        delegateTask.setAssignee("user3");
    }
}
