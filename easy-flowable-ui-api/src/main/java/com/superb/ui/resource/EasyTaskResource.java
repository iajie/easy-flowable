package com.superb.ui.resource;

import com.superb.core.constans.Constants;
import com.superb.core.domain.dto.FlowUserTask;
import com.superb.core.domain.model.Result;
import com.superb.core.domain.params.FlowCancellationParam;
import com.superb.core.domain.params.FlowExecuteParam;
import com.superb.core.service.EasyTaskService;
import lombok.SneakyThrows;
import org.flowable.engine.TaskService;
import org.flowable.engine.task.Attachment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * @package: {@link com.superb.ui.resource}
 * @Date: 2024-10-09-17:03
 * @Description:
 * @Author: MoJie
 */
@RestController
public class EasyTaskResource {

    @Autowired(required = false)
    private EasyTaskService easyTaskService;
    @Autowired(required = false)
    private TaskService taskService;

    /**
     * 执行任务
     * @param param {@link FlowExecuteParam} 执行参数
     * @return {@link List<FlowUserTask>>}
     * @Author: MoJie
     * @Date: 2024-10-09 17:05:06
     */
    @PostMapping(value = Constants.EASY_FLOWABLE + "/task/execute")
    public Result<?> execute(@RequestBody FlowExecuteParam param) {
        easyTaskService.executeNextStep(param);
        return Result.success();
    }

    /**
     * @param file 文件
     * @param taskId 任务ID
     * @param processInstanceId 流程实例ID
     * @Return: {@link Result}
     * @Author: MoJie
     * @Date: 2024/10/29 19:28
     * @Description: 上传附件,返回附件ID
     */
    @SneakyThrows
    @PostMapping(value = Constants.EASY_FLOWABLE + "/task/addAttachment")
    public Result<String> addAttachment(@RequestBody MultipartFile file, String taskId, String processInstanceId) {
        Attachment attachment = taskService.createAttachment(Constants.FILE, taskId, processInstanceId,
                file.getOriginalFilename(), null, file.getInputStream());
        return Result.success("附件上传成功！", attachment.getId());
    }

    /**
     * @param attachmentId 文件ID
     * @Return: {@link Result}
     * @Author: MoJie
     * @Date: 2024/10/29 19:28
     * @Description: 删除附件
     */
    @GetMapping(value = Constants.EASY_FLOWABLE + "/task/delAttachment/{attachmentId}")
    public Result<String> delAttachment(@PathVariable String attachmentId) {
        this.taskService.deleteAttachment(attachmentId);
        return Result.success();
    }

    /**
     * @param attachmentId 附件ID
     * @param response 响应
     * @Author: MoJie
     * @Date: 2024/10/29 22:11
     * @Description: 获取附件
     */
    @SneakyThrows
    @GetMapping(value = Constants.EASY_FLOWABLE + "/task/getAttachment/{attachmentId}")
    public void getAttachment(@PathVariable String attachmentId, HttpServletResponse response) {
        try (ServletOutputStream out = response.getOutputStream();
             InputStream in = taskService.getAttachmentContent(attachmentId)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }

    /**
     * 流程作废
     * @param cancellation 作废流程
     * @return {@link Result}
     * @Author: MoJie
     * @Date: 2024-10-09 17:06:15
     */
    @PostMapping(value = Constants.EASY_FLOWABLE + "/task/cancellation")
    public Result<?> cancellation(@RequestBody FlowCancellationParam cancellation) {
        easyTaskService.cancellationProcessInstance(cancellation);
        return Result.success();
    }

    /**
     * 获取当前任务节点执行人(候选人)
     * @param taskId 任务ID
     * @return {@link List<String>>}
     * @Author: MoJie
     * @Date: 2024-10-09 17:06:59
     */
    @GetMapping(value = Constants.EASY_FLOWABLE + "/task/executors/{taskId}")
    public Result<List<String>> getUserTaskExecutors(@PathVariable String taskId) {
        return Result.success(easyTaskService.getUserTaskExecutors(taskId, false));
    }

    /**
     * 获取当前任务节点执行部门(候选组)
     * @param taskId 任务ID
     * @return {@link List<String>>}
     * @Author: MoJie
     * @Date: 2024-10-09 17:08:34
     */
    @GetMapping(value = Constants.EASY_FLOWABLE + "/task/executeOrgan/{taskId}")
    public Result<List<String>> executeOrgan(@PathVariable String taskId) {
        return Result.success(easyTaskService.getUserTaskOrganIds(taskId));
    }

    @GetMapping(value = Constants.EASY_FLOWABLE + "/task/nextNodeVariables/{taskId}")
    public Result<Map<String, Object>> nextNodeVariables(@PathVariable String taskId) {
        return Result.success(easyTaskService.nextNodeVariables(taskId));
    }
}