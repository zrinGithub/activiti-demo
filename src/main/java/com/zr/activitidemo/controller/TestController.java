package com.zr.activitidemo.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

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
                .addClasspathResource("task/TestTask1.bpmn")
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
        taskService.createTaskQuery().taskAssignee("user1");
    }
}
