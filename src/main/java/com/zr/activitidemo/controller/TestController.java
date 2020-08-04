package com.zr.activitidemo.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * Description:
 *
 * @author zhangr
 * 2020/8/3 16:19
 */
@Api("TestController")
@RestController
@Slf4j
public class TestController {
    @Resource
    private ProcessEngine processEngine;

    @GetMapping("test")
    @ApiOperation("测试")
    public void test() {
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //部署
        Deployment deploy = repositoryService.createDeployment()
                //流程名字
                .name("测试1")
                //流程资源文件
                .addClasspathResource("task/TestProcess.bpmn")
                //部署
                .deploy();
        log.info("Deploy----- id:" + deploy.getId());

        //启动流程
        RuntimeService runtimeService = processEngine.getRuntimeService();
        //画流程图的时候指定的id
        String processDefinitionKey = "testProcess";
        //业务逻辑里面的id
        String businessKey = "1";
        ProcessInstance instance = runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey);
        log.info("Run----- id:" + instance.getId());

        //查询任务
        TaskService taskService = processEngine.getTaskService();
        List<Task> tasks = taskService.createTaskQuery().taskAssignee("user1")
                //可以设置分页
//                .listPage(1, 10)
                //排序
//                .orderByTaskCreateTime().desc()
                //确定只有一个结果的时候，可以直接取单个返回
//                .singleResult()
                .list();

        //处理任务
        for (Task task : tasks) {
            //任务id
            String taskId = task.getId();
            //流程实例id
            String instanceId = task.getProcessInstanceId();
            //批注信息
            String comment = "同意";
            Authentication.setAuthenticatedUserId("user1");
            //添加批注
            taskService.addComment(taskId, instanceId, comment);

            log.info("Now complete Task id:" + taskId);
            //处理任务
            taskService.complete(taskId);
        }

        //查看批注与businessKey
        List<Task> user2Tasks = taskService.createTaskQuery().taskAssignee("user2").list();
        for (Task task : user2Tasks) {
            List<Comment> comments = taskService.getProcessInstanceComments(task.getProcessInstanceId());
            log.info("task id: {} ", task.getId());
            for (Comment comment : comments) {
                log.info("comment user:{}", comment.getUserId());
                log.info("comment message:{}", comment.getFullMessage());
                log.info("comment time:{}", comment.getTime());
            }
            //拿到businessKey
            ProcessInstance instance1 = runtimeService.createProcessInstanceQuery()
                    .processInstanceId(task.getProcessInstanceId())
                    .singleResult();
            log.info("business key:{}", instance1.getBusinessKey());
        }
    }

}
